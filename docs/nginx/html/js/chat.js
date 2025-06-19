// 添加登录状态检查
window.addEventListener('load', () => {
    const userId = localStorage.getItem('userId');
    const token = localStorage.getItem('token');
    // 获取文件上传相关DOM
    const fileButton = document.getElementById('fileButton');
    const fileInput = document.getElementById('fileInput');
    const filePreview = document.getElementById('filePreview');
    const imagePreviewModal = document.getElementById('imagePreviewModal');
    const previewImage = document.getElementById('previewImage');
    const modalClose = document.getElementById('modalClose');
    const searchButton = document.getElementById('searchButton'); // 新添加的搜索按钮
    // 新增图片创作相关DOM
    const functionButton = document.getElementById('functionButton');
    const functionDropdown = document.getElementById('functionDropdown');
    const ratioButton = document.getElementById('ratioButton');
    const ratioDropdown = document.getElementById('ratioDropdown');
    const referenceImageButton = document.getElementById('referenceImageButton');
    const referenceImageInput = document.getElementById('referenceImageInput');

    // 获取两组按钮容器
    const defaultActionButtons = document.getElementById('defaultActionButtons');
    const imageActionButtons = document.getElementById('imageActionButtons');

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

    // 添加文件上传相关事件监听
    fileButton.addEventListener('click', () => {
        fileInput.click();
    });

    fileInput.addEventListener('change', handleFileUpload);

    modalClose.addEventListener('click', () => {
        imagePreviewModal.style.display = 'none';
    });

    window.addEventListener('click', (e) => {
        if (e.target === imagePreviewModal) {
            imagePreviewModal.style.display = 'none';
        }
    });

    // 初始化控件状态
    updateControlsState();

    // 搜索按钮点击事件
    searchButton.addEventListener('click', toggleSearch);

    // 获取历史记录
    fetchHistoryCodes();

    // 图片创作模式相关事件
    functionButton.addEventListener('click', () => {
        functionDropdown.style.display = functionDropdown.style.display === 'block' ? 'none' : 'block';
        ratioDropdown.style.display = 'none';
    });

    // 在功能选择下拉菜单的点击事件中修改
    functionDropdown.querySelectorAll('.dropdown-item').forEach(item => {
        item.addEventListener('click', () => {
            const previousFunction = functionButton.querySelector('span').textContent;
            const selectedFunction = item.getAttribute('data-value');

            functionButton.querySelector('span').textContent = item.textContent;
            functionButton.classList.add('active');
            functionDropdown.style.display = 'none';

            // 如果之前选择了"生成图片"，现在切换到了其他功能，清除比例选择
            if (previousFunction === '生成图片' && selectedFunction !== 'generate') {
                ratioButton.querySelector('span').textContent = '比例大小';
                ratioButton.classList.remove('active');
            }

            // 如果之前选择了非"生成图片"功能，现在切换到"生成图片"，清除参考图
            if (previousFunction !== '生成图片' && selectedFunction === 'generate') {
                referenceImageUrl = null;
                referenceImageButton.querySelector('span').textContent = '上传参考图';
                referenceImageButton.classList.remove('active');
            }

            // 只有选择生成图片时，比例大小才可用
            if (selectedFunction === 'generate') {
                ratioButton.disabled = false;
                ratioButton.classList.remove('disabled');
                referenceImageButton.disabled = true;
                referenceImageButton.classList.add('disabled');
            } else {
                ratioButton.disabled = true;
                ratioButton.classList.add('disabled');

                // 如果选择的是指令编辑、去文字水印、扩图、图像超分、图像上色，则启用上传参考图
                const referenceFunctions = ['edit', 'removeWatermark', 'expand', 'superResolution', 'colorize'];
                if (referenceFunctions.includes(selectedFunction)) {
                    referenceImageButton.disabled = false;
                    referenceImageButton.classList.remove('disabled');
                } else {
                    referenceImageButton.disabled = true;
                    referenceImageButton.classList.add('disabled');
                }
            }
        });
    });

    // 修改比例按钮点击事件
    ratioButton.addEventListener('click', () => {
        if (ratioButton.disabled) {
            showNotification('请先选择功能', 'error');
            return;
        }
        ratioDropdown.style.display = ratioDropdown.style.display === 'block' ? 'none' : 'block';
        functionDropdown.style.display = 'none';
    });

    // 修改参考图按钮点击事件
    referenceImageButton.addEventListener('click', () => {
        if (referenceImageButton.disabled) {
            showNotification('请先选择功能', 'error');
            return;
        }
        referenceImageInput.click();
    });

    ratioDropdown.querySelectorAll('.dropdown-item').forEach(item => {
        item.addEventListener('click', () => {
            ratioButton.querySelector('span').textContent = item.textContent;
            ratioButton.classList.add('active');
            ratioDropdown.style.display = 'none';
        });
    });

    referenceImageButton.addEventListener('click', () => {
        if (referenceImageButton.disabled) {
            showNotification('请先选择功能', 'error');
            return;
        }
        referenceImageInput.click();
    });

    referenceImageInput.addEventListener('change', handleReferenceImageUpload);

    // 点击其他地方关闭下拉菜单
    document.addEventListener('click', (e) => {
        if (!functionButton.contains(e.target) && !functionDropdown.contains(e.target)) {
            functionDropdown.style.display = 'none';
        }
        if (!ratioButton.contains(e.target) && !ratioDropdown.contains(e.target)) {
            ratioDropdown.style.display = 'none';
        }
    });

});

// 获取DOM元素
const sidebar = document.getElementById('sidebar');
const toggleSidebarBtn = document.getElementById('toggleSidebar');
const chatContainer = document.getElementById('chatContainer');
const messageInput = document.getElementById('messageInput');
const sendButton = document.getElementById('sendButton');
const searchButton = document.getElementById('searchButton'); // 新添加的搜索按钮
const fileButton = document.getElementById('fileButton'); // 文件上传按钮
const fileInput = document.getElementById('fileInput');
const typingIndicator = document.getElementById('typingIndicator');
const menuItems = document.querySelectorAll('.menu-item');

// 从localStorage获取用户ID
const userId = localStorage.getItem('userId');
let currentHistoryCode = null; // 当前使用的历史记录码
let messageCount = 0;
let isStreaming = false;
let currentAIResponse = null;
let isSearchEnabled = true; // 默认开启搜索
let historyCodesList = []; // 存储历史记录码列表
// 声明文件URL数组
let fileUrls = [];
// 全局变量
let referenceImageUrl = null; // 用于存储参考图的URL
let currentMode = 'default'; // 'default' 或 'image'

// 更新控件状态函数
function updateControlsState() {
    const hasFiles = fileUrls.length > 0;

    // 更新搜索按钮状态
    if (isSearchEnabled) {
        searchButton.classList.add('active');
    } else {
        searchButton.classList.remove('active');
    }

    // 更新文件按钮状态
    if (hasFiles) {
        fileButton.classList.add('active');
    } else {
        fileButton.classList.remove('active');
    }

    // 文件存在时禁用联网搜索
    if (hasFiles) {
        searchButton.disabled = true;
        searchButton.title = "文件上传不支持联网搜索"; // 新增提示
        fileButton.title = "文件已上传";
    } else {
        searchButton.disabled = false;
        // 根据搜索状态设置提示
        if (isSearchEnabled) {
            searchButton.title = "联网搜索已开启";
        } else {
            searchButton.title = "联网搜索已关闭";
        }
        fileButton.title = "上传文件";
    }

    // 联网搜索开启时禁用文件上传
    fileButton.disabled = isSearchEnabled;
    fileInput.disabled = isSearchEnabled;

    // 当联网搜索开启时，显示文件上传禁用提示
    if (isSearchEnabled) {
        fileButton.title = "联网搜索开启时无法上传文件";
        fileButton.classList.add('disabled');
    } else {
        fileButton.title = "上传文件";
        fileButton.classList.remove('disabled');
    }
}

// 切换搜索状态
function toggleSearch() {
    if (fileUrls.length > 0) return; // 有文件时不允许切换

    isSearchEnabled = !isSearchEnabled;
    updateControlsState();

    if (isSearchEnabled) {
        showNotification('联网搜索已开启', 'success');
    } else {
        showNotification('联网搜索已关闭', 'info');
    }
}

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
            // 隐藏默认按钮组，显示图片创作按钮组
            defaultActionButtons.style.display = 'none';
            imageActionButtons.style.display = 'flex';
            addMessage("您已切换到图片创作模式。请描述您想要生成的图片内容，例如：'生成一份薯条'", 'ai');
        } else if(menuText === '代码助手') {
            // 显示默认按钮组，隐藏图片创作按钮组
            defaultActionButtons.style.display = 'flex';
            imageActionButtons.style.display = 'none';
            addMessage("您已切换到代码助手模式。请描述您的编程需求，例如：'用Java写一个冒泡排序'", 'ai');
        } else if(menuText === '视频创作') {
            defaultActionButtons.style.display = 'flex';
            imageActionButtons.style.display = 'none';
            addMessage("您已切换到视频创作模式。请描述您想要生成的视频内容，例如：'一只小猫在草地上奔跑'", 'ai');
        } else if(menuText === '新对话') {
            defaultActionButtons.style.display = 'flex';
            imageActionButtons.style.display = 'none';
            startNewConversation();
        }
    });
});

// 开始新对话
function startNewConversation() {
    // 生成新的历史记录码
    const newHistoryCode = `历史记录${historyCodesList.length + 1}`;

    currentHistoryCode = newHistoryCode;

    // 清空聊天区域
    chatContainer.innerHTML = `
        <div class="welcome-section">
            <div class="logo">
                <i class="fas fa-comment-dots"></i>
            </div>
            <h2>我是 ShallowBrowse，很高兴见到你！</h2>
            <p>我可以帮你写代码、生成图片/视频，请把你的任务交给我吧~</p>
        </div>
    `;

    referenceImageUrl = null;
    // 重置图片创作按钮
    functionButton.querySelector('span').textContent = '功能选择';
    functionButton.classList.remove('active');
    ratioButton.querySelector('span').textContent = '比例大小';
    ratioButton.classList.remove('active');
    referenceImageButton.classList.remove('active');
    // 重置按钮状态
    ratioButton.disabled = true;
    referenceImageButton.disabled = true;

    // 添加欢迎消息
    setTimeout(() => {
        const welcomeMsg = "欢迎使用浅度浏览！您现在可以：\n\n1. 在左侧边栏选择功能\n2. 在下方输入您的问题\n3. 勾选'联网搜索'获取最新信息\n\n随时告诉我您的需求！";
        addMessage(welcomeMsg, 'ai');
    }, 1000);

    // 清空文件上传
    fileUrls = [];
    filePreview.innerHTML = '';
    updateControlsState();

    showNotification(`已创建新对话: ${newHistoryCode}`, 'success');
}

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

// 文件上传处理函数
async function handleFileUpload(e) {
    const files = e.target.files;
    if (!files || files.length === 0) return;

    // 检查联网搜索是否开启
    if (isSearchEnabled) {
        showNotification('开启联网搜索时不能上传文件', 'error');
        fileInput.value = '';
        return;
    }

    const token = localStorage.getItem('token') || "";
    const tokenName = localStorage.getItem('tokenName') || "";
    const userId = localStorage.getItem('userId');

    for (let i = 0; i < files.length; i++) {
        const file = files[i];

        // 显示上传中指示器
        const previewItem = createPreviewItem(file, true);
        filePreview.appendChild(previewItem);

        const formData = new FormData();
        formData.append('file', file);
        formData.append('userId', userId);

        try {
            const response = await fetch('http://localhost:8097/chat/upload_file', {
                method: 'POST',
                headers: {
                    [tokenName]: token
                },
                body: formData
            });

            const result = await response.json();

            // 修复点：使用字符串 '0' 进行比较
            if (result.code === '0' && result.data && result.data.url) {
                // 上传成功，更新预览项
                const url = result.data.url;
                fileUrls.push(url);

                // 更新预览项显示实际图片
                previewItem.innerHTML = `
                    <img src="${url}" alt="${file.name}">
                    <div class="remove-btn"><i class="fas fa-times"></i></div>
                `;

                // 添加点击事件
                previewItem.querySelector('.remove-btn').addEventListener('click', (e) => {
                    e.stopPropagation();
                    removeFile(url, previewItem);
                });

                previewItem.addEventListener('click', () => {
                    previewImage.src = url;
                    imagePreviewModal.style.display = 'block';
                });
            } else {
                // 上传失败
                previewItem.innerHTML = `
                    <div class="upload-error">上传失败</div>
                    <div class="remove-btn"><i class="fas fa-times"></i></div>
                `;
                previewItem.querySelector('.remove-btn').addEventListener('click', (e) => {
                    e.stopPropagation();
                    previewItem.remove();
                });

                showNotification('文件上传失败: ' + (result.info || '未知错误'), 'error');
            }
        } catch (error) {
            previewItem.innerHTML = `
                <div class="upload-error">上传失败</div>
                <div class="remove-btn"><i class="fas fa-times"></i></div>
            `;
            previewItem.querySelector('.remove-btn').addEventListener('click', (e) => {
                e.stopPropagation();
                previewItem.remove();
            });

            showNotification('文件上传失败: ' + error.message, 'error');
            console.error('文件上传错误:', error);
        }
    }

    // 重置文件输入
    fileInput.value = '';

    // 更新控件状态
    updateControlsState();
}

// 创建预览项
function createPreviewItem(file, isLoading = false) {
    const previewItem = document.createElement('div');
    previewItem.className = 'file-preview-item';

    if (isLoading) {
        previewItem.innerHTML = `
            <div class="upload-loading">
                <i class="fas fa-spinner fa-spin"></i>
            </div>
            <div class="remove-btn"><i class="fas fa-times"></i></div>
        `;
    } else {
        if (file.type.startsWith('image/')) {
            previewItem.innerHTML = `
                <img src="${URL.createObjectURL(file)}" alt="${file.name}">
                <div class="remove-btn"><i class="fas fa-times"></i></div>
            `;
        } else {
            previewItem.innerHTML = `
                <div class="file-icon">
                    <i class="fas fa-file"></i>
                    <span>${file.name.substring(0, 5)}...</span>
                </div>
                <div class="remove-btn"><i class="fas fa-times"></i></div>
            `;
        }
    }

    return previewItem;
}

// 移除文件
function removeFile(url, element) {
    fileUrls = fileUrls.filter(u => u !== url);
    element.remove();

    // 更新控件状态
    updateControlsState();
}

// 发送消息函数（支持文件上传）
async function sendMessage() {
    if (isStreaming) return;

    const message = messageInput.value.trim();
    if (!message && fileUrls.length === 0) return;

    // 如果没有选择历史记录码，创建一个新的
    if (!currentHistoryCode) {
        currentHistoryCode = `历史记录${historyCodesList.length + 1}`;
    }

    // 添加用户消息（包含文件预览）
    addMessageWithFiles(message, fileUrls);

    // 清空文件预览区域和文件列表
    filePreview.innerHTML = '';
    const filesToSend = [...fileUrls];
    fileUrls = [];

    // 显示正在输入指示器
    typingIndicator.style.display = 'block';
    sendButton.disabled = true;

    // 清空输入框
    messageInput.value = '';

    try {
        isStreaming = true;
        // 调用后端聊天接口 - 流式处理（包含文件列表）
        await streamChatResponse(message, isSearchEnabled, filesToSend);
    } catch (error) {
        // 错误处理
        addMessage(`<span style="color: red;">请求失败: ${error.message}</span>`, 'ai');
    } finally {
        // 隐藏正在输入指示器
        typingIndicator.style.display = 'none';
        sendButton.disabled = false;
        isStreaming = false;
        currentAIResponse = null;

        // 更新控件状态
        updateControlsState();
    }
}

// 处理token过期函数
function handleTokenExpired() {
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
}

// 流式处理聊天响应
async function streamChatResponse(content, search, files = []) {
    // 从localStorage获取token
    const token = localStorage.getItem('token') || "";
    const tokenName = localStorage.getItem('tokenName') || "";
    // 构建请求体
    const requestBody = {
        userId: userId,
        content: content,
        history_code: currentHistoryCode,  // 使用当前选择的历史记录码
        search: search,
        file: files.length > 0 ? files : null
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

        // 直接使用流读取器，保持流式输出
        const reader = response.body.getReader();
        const decoder = new TextDecoder("utf-8");
        let accumulatedText = '';
        let isTokenExpired = false;

        // 移除响应指示器
        responseIndicator.remove();

        // 添加内容到消息元素
        const processStream = async () => {
            try {
                while (true) {
                    const { done, value } = await reader.read();
                    if (done) break;

                    // 解码并处理数据块
                    const chunk = decoder.decode(value, { stream: true });
                    accumulatedText += chunk;

                    // 检查是否是 token 无效的错误
                    if (accumulatedText.includes('"code":403') && accumulatedText.includes('token 无效')) {
                        isTokenExpired = true;
                        break; // 立即中断流处理
                    }

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
            } finally {
                // 移除光标
                cursor.remove();
            }
        };

        await processStream();

        // 流结束后检查token是否过期
        if (isTokenExpired) {
            handleTokenExpired();
            return;
        }
    } catch (error) {
        console.error('请求错误:', error);
        // 移除指示器
        responseIndicator.remove();
        cursor.remove();

        // 检查是否是403错误
        if (error.message.includes('403')) {
            handleTokenExpired();
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

// 添加带文件预览的消息函数
function addMessageWithFiles(content, files) {
    const messageElement = document.createElement('div');
    messageElement.classList.add('message', 'user');
    messageElement.style.opacity = '0';
    messageElement.style.transform = 'translateY(20px) translateX(20px)';

    const messageHeader = document.createElement('div');
    messageHeader.classList.add('message-header');

    const avatar = document.createElement('div');
    avatar.classList.add('message-avatar');
    avatar.textContent = 'U';

    const senderElement = document.createElement('div');
    senderElement.classList.add('message-sender');
    senderElement.textContent = '您';

    messageHeader.appendChild(avatar);
    messageHeader.appendChild(senderElement);

    const contentElement = document.createElement('div');
    contentElement.classList.add('message-content');

    // 添加文本内容
    if (content) {
        const textElement = document.createElement('div');
        textElement.textContent = content;
        contentElement.appendChild(textElement);
    }

    // 添加文件预览
    if (files && files.length > 0) {
        const filePreviewContainer = document.createElement('div');
        filePreviewContainer.className = 'message-file-preview';

        files.forEach(url => {
            const fileItem = document.createElement('div');
            fileItem.className = 'message-file-item';

            // 检查是否是图片
            if (url.match(/\.(jpeg|jpg|gif|png|webp)$/i)) {
                fileItem.innerHTML = `<img src="${url}" alt="Uploaded file">`;
                fileItem.addEventListener('click', () => {
                    previewImage.src = url;
                    imagePreviewModal.style.display = 'block';
                });
            } else {
                fileItem.innerHTML = `
                    <div style="display: flex; flex-direction: column; align-items: center; justify-content: center; height: 100%;">
                        <i class="fas fa-file" style="font-size: 24px; margin-bottom: 5px;"></i>
                        <span style="font-size: 10px;">文件</span>
                    </div>
                `;
            }

            filePreviewContainer.appendChild(fileItem);
        });

        contentElement.appendChild(filePreviewContainer);
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

    // 平滑滚动到底部
    smoothScrollToBottom();
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

// 添加历史记录码获取功能
async function fetchHistoryCodes() {
    const token = localStorage.getItem('token') || "";
    const tokenName = localStorage.getItem('tokenName') || "";
    const userId = localStorage.getItem('userId');

    try {
        const response = await fetch(`http://localhost:8097/chat/get_history_code_list/${userId}`, {
            method: 'GET',
            headers: {
                [tokenName]: token
            }
        });

        const result = await response.json();

        if (result.code === '0' && result.data) {
            historyCodesList = result.data; // 存储历史记录码列表
            renderHistoryDropdown(result.data);
        } else {
            showNotification('获取历史记录失败: ' + (result.info || '未知错误'), 'error');
        }
    } catch (error) {
        console.error('获取历史记录错误:', error);
        showNotification('获取历史记录失败: ' + error.message, 'error');
    }
}

// 渲染历史记录码下拉菜单
function renderHistoryDropdown(historyCodes) {
    const dropdown = document.getElementById('historyDropdown');

    if (!historyCodes || historyCodes.length === 0) {
        dropdown.innerHTML = '<div class="history-empty">暂无历史记录</div>';
        return;
    }

    dropdown.innerHTML = '';

    historyCodes.forEach(code => {
        const item = document.createElement('div');
        item.className = 'history-item';
        item.textContent = code;
        item.addEventListener('click', () => {
            loadHistory(code);
        });
        dropdown.appendChild(item);
    });
}

// 添加历史记录加载功能
async function loadHistory(selectedHistoryCode) {
    const token = localStorage.getItem('token') || "";
    const tokenName = localStorage.getItem('tokenName') || "";
    const userId = localStorage.getItem('userId');

    try {
        // 显示加载提示
        document.getElementById('historyLoading').style.display = 'flex';

        const response = await fetch('http://localhost:8097/chat/get_history', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                [tokenName]: token
            },
            body: JSON.stringify({
                userId: userId,
                history_code: selectedHistoryCode
            })
        });

        const result = await response.json();

        if (result.code === '0' && result.data) {
            // 更新当前历史记录码
            currentHistoryCode = selectedHistoryCode;

            // 清空当前聊天区域
            chatContainer.innerHTML = '';

            // 添加历史记录标题
            const historyHeader = document.createElement('div');
            historyHeader.className = 'history-header';
            historyHeader.textContent = `历史记录: ${selectedHistoryCode}`;
            chatContainer.appendChild(historyHeader);

            // 跳过第一条系统消息
            const messagesToShow = result.data.slice(1);

            // 添加历史消息
            messagesToShow.forEach(message => {
                if (message.role === 'user') {
                    // 处理用户消息（可能包含文本和图片）
                    let textContent = '';
                    let imageUrls = [];

                    message.content.forEach(item => {
                        if (item.text) {
                            textContent = item.text;
                        } else if (item.image) {
                            imageUrls.push(item.image);
                        }
                    });

                    addMessageWithFiles(textContent, imageUrls);
                } else if (message.role === 'system') {
                    // 处理AI回复
                    message.content.forEach(item => {
                        if (item.text) {
                            addMessage(item.text, 'ai');
                        }
                    });
                }
            });

            // 滚动到底部
            smoothScrollToBottom();
            showNotification(`已加载历史记录: ${selectedHistoryCode}`, 'success');
        } else {
            showNotification('加载历史记录失败: ' + (result.info || '未知错误'), 'error');
        }
    } catch (error) {
        console.error('加载历史记录错误:', error);
        showNotification('加载历史记录失败: ' + error.message, 'error');
    } finally {
        // 隐藏加载提示
        document.getElementById('historyLoading').style.display = 'none';
    }
}

async function handleReferenceImageUpload(e) {
    const files = e.target.files;
    if (!files || files.length === 0) return;

    const file = files[0]; // 参考图只支持单张

    // 检查文件类型
    if (!file.type.startsWith('image/')) {
        showNotification('请上传图片文件', 'error');
        return;
    }

    const token = localStorage.getItem('token') || "";
    const tokenName = localStorage.getItem('tokenName') || "";
    const userId = localStorage.getItem('userId');

    // 显示上传中
    referenceImageButton.innerHTML = '<i class="fas fa-spinner fa-spin"></i> 上传中...';
    referenceImageButton.disabled = true;

    const formData = new FormData();
    formData.append('file', file);
    formData.append('userId', userId);

    try {
        const response = await fetch('http://localhost:8097/create/upload_image', {
            method: 'POST',
            headers: {
                [tokenName]: token
            },
            body: formData
        });

        const result = await response.json();

        if (result.code === '0' && result.data && result.data.url) {
            referenceImageUrl = result.data.url;
            referenceImageButton.innerHTML = '<i class="fas fa-image"></i> 参考图已上传';
            referenceImageButton.classList.add('active');
            showNotification('参考图上传成功', 'success');
        } else {
            showNotification('参考图上传失败: ' + (result.info || '未知错误'), 'error');
            referenceImageButton.innerHTML = '<i class="fas fa-image"></i> 上传参考图';
        }
    } catch (error) {
        showNotification('参考图上传失败: ' + error.message, 'error');
        referenceImageButton.innerHTML = '<i class="fas fa-image"></i> 上传参考图';
        console.error('参考图上传错误:', error);
    } finally {
        referenceImageButton.disabled = false;
        referenceImageInput.value = '';
    }
}





// 发送消息函数（支持文件上传）
async function sendMessage() {
    if (isStreaming) return;

    const message = messageInput.value.trim();

    if (currentMode === 'image') {
        // 图片创作模式发送逻辑
        if (!message && !referenceImageUrl) return;

        // 如果没有选择功能
        if (!functionButton.classList.contains('active')) {
            showNotification('请先选择功能', 'error');
            return;
        }

        // 构建图片创作请求
        const requestData = {
            userId: userId,
            historyCode: currentHistoryCode || `历史记录${historyCodesList.length + 1}`,
            commandType: getCommandTypeByFunction(), // 根据功能选择返回对应的命令类型
            content: message,
            sizeType: getSizeTypeByRatio(), // 根据比例大小返回对应的尺寸类型
            refer: referenceImageUrl // 参考图URL
        };

        // 发送请求...
        // 这里需要根据实际接口实现

        showNotification('图片创作请求已发送', 'success');
    } else {
        // 默认模式
        // ... 原有代码保持不变 ...
    }
}

// 根据功能选择获取命令类型
function getCommandTypeByFunction() {
    const functionText = functionButton.querySelector('span').textContent;
    // 这里需要根据按钮文本返回对应的命令类型枚举值
    // 示例映射关系：
    const functionMap = {
        '生成图片': 1,
        '指令编辑': 2,
        '去文字水印': 3,
        '扩图': 4,
        '图像超分': 5,
        '图像上色': 6
    };
    return functionMap[functionText] || 1; // 默认生成图片
}

// 根据比例大小获取尺寸类型
function getSizeTypeByRatio() {
    const ratioText = ratioButton.querySelector('span').textContent;
    const ratioMap = {
        '1:1': 1,
        '4:3': 2,
        '9:16': 3,
        '16:9': 4
    };
    return ratioMap[ratioText] || 1; // 默认1:1
}
