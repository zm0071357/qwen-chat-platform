// 获取DOM元素
const registerLink = document.getElementById('registerLink');
const registerModal = document.getElementById('registerModal');
const closeModal = document.getElementById('closeModal');
const registerForm = document.getElementById('registerForm');

// 显示注册模态框
registerLink.addEventListener('click', () => {
    registerModal.style.display = 'flex';
});

// 关闭注册模态框
closeModal.addEventListener('click', () => {
    registerModal.style.display = 'none';
});

// 点击模态框外部关闭
window.addEventListener('click', (e) => {
    if (e.target === registerModal) {
        registerModal.style.display = 'none';
    }
});

// 注册表单提交
registerForm.addEventListener('submit', async function(e) {
    e.preventDefault();

    const regUserId = document.getElementById('regUserId').value;
    const regPassword = document.getElementById('regPassword').value;
    const regConfirmPassword = document.getElementById('regConfirmPassword').value;

    // 显示加载状态
    const regBtn = registerForm.querySelector('.btn');
    regBtn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> 注册中...';
    regBtn.disabled = true;

    try {
        // 调用注册接口
        const response = await fetch('http://localhost:8097/login/register', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                userId: regUserId,
                password: regPassword,
                confirm_password: regConfirmPassword
            })
        });

        const result = await response.json();

        // 显示通知
        showNotification(result.info, result.code === String(RegisterStatusEnum.SUCCESS.getCode()) ? 'success' : 'error');

        // 注册成功后的操作
        if (result.code === String(RegisterStatusEnum.SUCCESS.getCode())) {
            // 保存token和userId
            localStorage.setItem('token', result.data.token);
            localStorage.setItem('tokenName', result.data.tokenName);
            localStorage.setItem('userId', regUserId);
            // 显示跳转提示
            showNotification('注册成功，正在跳转...', 'success');
            // 跳转到chat.html
            setTimeout(() => {
                window.location.href = 'chat.html';
            }, 1500);
        }

    } catch (error) {
        showNotification('网络请求失败: ' + error.message, 'error');
        console.error('注册请求错误:', error);
    } finally {
        // 重置按钮状态
        regBtn.innerHTML = '<i class="fas fa-user-plus"></i> 注册';
        regBtn.disabled = false;
    }
});

// 登录表单提交
document.getElementById('loginForm').addEventListener('submit', async function(e) {
    e.preventDefault();

    const userId = document.getElementById('userId').value;
    const password = document.getElementById('password').value;

    // 显示加载状态
    const btn = document.querySelector('#loginForm .btn');
    btn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> 登录中...';
    btn.disabled = true;

    try {
        // 调用登录接口
        const response = await fetch('http://localhost:8097/login/by_acc', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                userId: userId,
                password: password
            })
        });

        const result = await response.json();

        // 显示通知
        showNotification(result.info, result.code === String(LoginStatusEnum.SUCCESS.getCode()) ? 'success' : 'error');

        // 登录成功后的操作
        if (result.code === String(LoginStatusEnum.SUCCESS.getCode())) {
            // 保存token和userId
            localStorage.setItem('token', result.data.token);
            localStorage.setItem('tokenName', result.data.tokenName);
            localStorage.setItem('userId', userId); // 存储userId

            // 显示跳转提示
            showNotification('登录成功', 'success');
            // 跳转到chat.html
            setTimeout(() => {
                window.location.href = 'chat.html';
            }, 1500);
        }

    } catch (error) {
        showNotification('网络请求失败: ' + error.message, 'error');
        console.error('登录请求错误:', error);
    } finally {
        // 重置按钮状态
        btn.innerHTML = '<i class="fas fa-sign-in-alt"></i> 登录';
        btn.disabled = false;
    }
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

// 枚举值（根据Java代码定义）
const RegisterStatusEnum = {
    SUCCESS: { getCode: () => "0" },
    FAILED: { getCode: () => "1" }
};

const LoginStatusEnum = {
    SUCCESS: { getCode: () => "0" }
};