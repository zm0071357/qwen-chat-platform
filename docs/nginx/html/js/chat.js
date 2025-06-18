// 添加登录状态检查
window.addEventListener('load', () => {
    const userId = localStorage.getItem('userId');
    const token = localStorage.getItem('token');

    if (!userId || !token) {
        showNotification('请先登录', 'error');
        setTimeout(() => {
            window.location.href = 'login.html';
        }, 2000);
        return;
    }

    // 原有的欢迎消息代码
    setTimeout(() => {
        const welcomeMsg = "欢迎使用浅度浏览！您现在可以：\n\n1. 在左侧边栏选择功能\n2. 在下方输入您的问题\n3. 勾选'联网搜索'获取最新信息\n\n随时告诉我您的需求！";
        addMessage(welcomeMsg, 'ai');
    }, 1000);
});

// 获取DOM元素
const sidebar = document.getElementById('sidebar');
const toggleSidebarBtn = document.getElementById('toggleSidebar');
const chatContainer = document.getElementById('chatContainer');
const messageInput = document.getElementById('messageInput');
const sendButton = document.getElementById('sendButton');
const searchToggle = document.getElementById('searchToggle');
const typingIndicator = document.getElementById('typingIndicator');
const menuItems = document.querySelectorAll('.menu-item');

// 从localStorage获取用户ID
const userId = localStorage.getItem('userId');
const historyCode = userId + "_history_1"; // 动态生成历史记录代码
let messageCount = 0;
let isStreaming = false;
let currentAIResponse = null;

// 配置marked解析器
marked.setOptions({
    highlight: function(code, lang) {
        if (lang && hljs.getLanguage(lang)) {
            try {
                return hljs.highlight(code, { language: lang }).value;
            } catch (__) {}
        }
        return hljs.highlightAuto(code).value;
    },
    langPrefix: 'hljs language-',
    pedantic: false,
    gfm: true,
    breaks: false,
    sanitize: false,
    smartypants: false,
    xhtml: false
});

// 切换侧边栏
function toggleSidebar() {
    sidebar.classList.toggle('collapsed');
}

toggleSidebarBtn.addEventListener('click', toggleSidebar);

// 菜单项点击事件
menuItems.forEach(item => {
    item.addEventListener('click', () => {
        // 移除所有active类
        menuItems.forEach(i => i.classList.remove('active'));
        // 添加active类到当前项
        item.classList.add('active');

        // 根据点击的菜单项显示不同内容
        const menuText = item.querySelector('span').textContent;
        if(menuText === '图片创作') {
            addMessage("您已切换到图片创作模式。请描述您想要生成的图片内容，例如：'生成一份薯条'", 'ai');
        } else if(menuText === '代码助手') {
            addMessage("您已切换到代码助手模式。请描述您的编程需求，例如：'用Java写一个冒泡排序'", 'ai');
        }
    });
});

// 显示通知函数
function showNotification(message, type) {
    const notification = document.getElementById('notification');
    notification.textContent = message;
    notification.className = 'notification ' + type;
    notification.classList.add('show');

    // 3秒后隐藏通知
    setTimeout(() => {
        notification.classList.remove('show');
    }, 3000);
}

// 发送消息函数
async function sendMessage() {
    if (isStreaming) return;

    const message = messageInput.value.trim();
    if (!message) return;

    // 添加用户消息
    addMessage(message, 'user');

    // 显示正在输入指示器
    typingIndicator.style.display = 'block';
    sendButton.disabled = true;

    // 清空输入框
    messageInput.value = '';

    // 获取搜索选项状态
    const search = searchToggle.checked;

    try {
        isStreaming = true;
        // 调用后端聊天接口 - 流式处理
        await streamChatResponse(message, search);
    } catch (error) {
        // 错误处理
        addMessage(`<span style="color: red;">请求失败: ${error.message}</span>`, 'ai');
    } finally {
        // 隐藏正在输入指示器
        typingIndicator.style.display = 'none';
        sendButton.disabled = false;
        isStreaming = false;
        currentAIResponse = null;
    }
}

// 流式处理聊天响应
async function streamChatResponse(content, search) {
    // 从localStorage获取token
    const token = localStorage.getItem('token') || "";
    const tokenName = localStorage.getItem('tokenName') || "";
    // 构建请求体
    const requestBody = {
        userId: userId,
        content: content,
        history_code: historyCode,
        search: search
    };

    // 创建AI消息元素
    currentAIResponse = createAIMessageElement();
    chatContainer.appendChild(currentAIResponse.messageElement);

    // 添加光标动画
    const cursor = document.createElement('span');
    cursor.className = 'cursor';
    currentAIResponse.contentElement.appendChild(cursor);

    // 平滑滚动到底部
    smoothScrollToBottom();

    // 添加响应状态指示器
    const responseIndicator = document.createElement('span');
    responseIndicator.className = 'response-indicator';
    responseIndicator.innerHTML = '<span class="dot"></span><span class="dot"></span><span class="dot"></span>';
    currentAIResponse.headerElement.appendChild(responseIndicator);

    try {
        // 发送请求到后端
        const response = await fetch('http://localhost:8097/chat', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                [tokenName]: token
            },
            body: JSON.stringify(requestBody)
        });

        // 处理响应
        if (!response.ok) {
            throw new Error(`HTTP错误! 状态: ${response.status}`);
        }

        // 获取响应文本
        const responseText = await response.text();

        // 检查是否是 token 无效的错误
        if (responseText.includes('"code":403') && responseText.includes('token 无效')) {
            // 清除本地存储
            localStorage.removeItem('token');
            localStorage.removeItem('userId');
            localStorage.removeItem('tokenName');

            // 移除响应指示器
            responseIndicator.remove();
            cursor.remove();

            // 移除已创建的AI消息元素
            if (currentAIResponse && currentAIResponse.messageElement) {
                currentAIResponse.messageElement.remove();
            }

            // 显示提示并跳转
            showNotification('登录超时，请重新登录', 'error');
            setTimeout(() => {
                window.location.href = 'login.html';
            }, 2000);
            return;
        }

        // 移除响应指示器
        responseIndicator.remove();

        // 移除光标
        cursor.remove();

        // 处理流式响应 - 这里需要重新创建 reader，因为我们已经读取了响应文本
        const reader = new ReadableStream({
            start(controller) {
                controller.enqueue(new TextEncoder().encode(responseText));
                controller.close();
            }
        }).getReader();

        const decoder = new TextDecoder("utf-8");
        let accumulatedText = '';

        // 添加内容到消息元素
        const processStream = async () => {
            try {
                while (true) {
                    const { done, value } = await reader.read();
                    if (done) break;

                    // 解码并处理数据块
                    const chunk = decoder.decode(value, { stream: true });
                    accumulatedText += chunk;

                    // 使用Markdown渲染文本
                    const sanitizedHtml = DOMPurify.sanitize(marked.parse(accumulatedText));

                    // 更新消息内容
                    currentAIResponse.contentElement.innerHTML = sanitizedHtml;

                    // 高亮代码块
                    document.querySelectorAll('pre code').forEach((el) => {
                        hljs.highlightElement(el);
                    });

                    // 平滑滚动到底部
                    smoothScrollToBottom();
                }
            } catch (error) {
                console.error('流处理错误:', error);
                currentAIResponse.contentElement.innerHTML += `<p style="color: red;">流处理错误: ${error.message}</p>`;
            }
        };

        await processStream();
    } catch (error) {
        console.error('请求错误:', error);
        // 移除指示器
        responseIndicator.remove();
        cursor.remove();

        // 检查是否是403错误
        if (error.message.includes('403')) {
            // 清除本地存储
            localStorage.removeItem('token');
            localStorage.removeItem('userId');
            localStorage.removeItem('tokenName');

            // 移除已创建的AI消息元素
            if (currentAIResponse && currentAIResponse.messageElement) {
                currentAIResponse.messageElement.remove();
            }

            // 显示提示并跳转
            showNotification('登录超时，请重新登录', 'error');
            setTimeout(() => {
                window.location.href = 'login.html';
            }, 2000);
            return;
        }

        // 显示其他错误消息
        currentAIResponse.contentElement.innerHTML = `<span style="color: red;">请求失败: ${error.message}</span>`;
    }
}

// 平滑滚动到底部
function smoothScrollToBottom() {
    chatContainer.scrollTo({
        top: chatContainer.scrollHeight,
        behavior: 'smooth'
    });
}

// 创建AI消息元素
function createAIMessageElement() {
    const messageElement = document.createElement('div');
    messageElement.classList.add('message', 'ai');
    messageElement.style.opacity = '0';
    messageElement.style.transform = 'translateY(20px)';

    const messageHeader = document.createElement('div');
    messageHeader.classList.add('message-header');

    const avatar = document.createElement('div');
    avatar.classList.add('message-avatar');
    avatar.textContent = 'AI';

    const senderElement = document.createElement('div');
    senderElement.classList.add('message-sender');
    senderElement.textContent = 'ShallowBrowse';

    messageHeader.appendChild(avatar);
    messageHeader.appendChild(senderElement);

    const contentElement = document.createElement('div');
    contentElement.classList.add('message-content', 'markdown-content');

    messageElement.appendChild(messageHeader);
    messageElement.appendChild(contentElement);

    // 应用动画
    setTimeout(() => {
        messageElement.style.transition = 'opacity 0.3s, transform 0.3s';
        messageElement.style.opacity = '1';
        messageElement.style.transform = 'translateY(0)';
    }, 10);

    return {
        messageElement,
        headerElement: messageHeader,
        contentElement
    };
}

// 添加消息到聊天区域
function addMessage(content, sender) {
    const messageElement = document.createElement('div');
    messageElement.classList.add('message', sender);
    messageElement.style.opacity = '0';
    messageElement.style.transform = sender === 'user' ? 'translateY(20px) translateX(20px)' : 'translateY(20px) translateX(-20px)';

    const messageHeader = document.createElement('div');
    messageHeader.classList.add('message-header');

    const avatar = document.createElement('div');
    avatar.classList.add('message-avatar');
    avatar.textContent = sender === 'user' ? 'U' : 'AI';

    const senderElement = document.createElement('div');
    senderElement.classList.add('message-sender');
    senderElement.textContent = sender === 'user' ? '您' : 'ShallowBrowse';

    messageHeader.appendChild(avatar);
    messageHeader.appendChild(senderElement);

    const contentElement = document.createElement('div');
    contentElement.classList.add('message-content');

    if (sender === 'ai') {
        contentElement.classList.add('markdown-content');
        contentElement.innerHTML = DOMPurify.sanitize(marked.parse(content));
    } else {
        contentElement.textContent = content;
    }

    messageElement.appendChild(messageHeader);
    messageElement.appendChild(contentElement);

    chatContainer.appendChild(messageElement);

    // 应用动画
    setTimeout(() => {
        messageElement.style.transition = 'opacity 0.3s, transform 0.3s';
        messageElement.style.opacity = '1';
        messageElement.style.transform = 'translateY(0) translateX(0)';
    }, 10);

    // 高亮代码块
    if (sender === 'ai') {
        setTimeout(() => {
            document.querySelectorAll('pre code').forEach((el) => {
                hljs.highlightElement(el);
            });
        }, 100);
    }

    // 平滑滚动到底部
    smoothScrollToBottom();
}

// 事件监听
sendButton.addEventListener('click', sendMessage);

messageInput.addEventListener('keydown', (e) => {
    if (e.key === 'Enter' && !e.shiftKey) {
        e.preventDefault();
        sendMessage();
    }
});

// 更新搜索选择框样式
searchToggle.addEventListener('change', () => {
    if (searchToggle.checked) {
        searchToggle.parentElement.style.color = '#4b6cb7';
        searchToggle.parentElement.style.fontWeight = '500';
    } else {
        searchToggle.parentElement.style.color = '#666';
        searchToggle.parentElement.style.fontWeight = 'normal';
    }
});