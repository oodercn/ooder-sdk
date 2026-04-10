<header class="nx-header">
    <div class="nx-header__left">
        <button class="nx-header__toggle" id="menuToggle">
            <i class="ri-menu-line"></i>
        </button>
        <h1 class="nx-header__title">${pageTitle!'Ooder MVP'}</h1>
    </div>
    
    <div class="nx-header__right">
        <div class="nx-header__actions">
            <button class="nx-header__action" id="themeToggle" title="切换主题">
                <i class="ri-moon-line"></i>
            </button>
            
            <#if user??>
            <div class="nx-header__user">
                <div class="nx-header__avatar">
                    <i class="ri-user-line"></i>
                </div>
                <div class="nx-header__userinfo">
                    <span class="nx-header__name">${name!'用户'}</span>
                    <span class="nx-header__role">${roleType!'user'}</span>
                </div>
                <button class="nx-header__logout" id="logoutBtn" title="退出登录">
                    <i class="ri-logout-box-line"></i>
                </button>
            </div>
            <#else>
            <a href="/login.html" class="nx-btn nx-btn--primary nx-btn--sm">
                <i class="ri-login-box-line"></i>
                登录
            </a>
            </#if>
        </div>
    </div>
</header>

<style>
.nx-header {
    height: var(--nx-header-height, 60px);
    background: var(--nx-bg-primary);
    border-bottom: 1px solid var(--nx-border-color-light);
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 0 var(--nx-space-6);
    position: sticky;
    top: 0;
    z-index: var(--nx-z-index-sticky);
}

.nx-header__left {
    display: flex;
    align-items: center;
    gap: var(--nx-space-4);
}

.nx-header__toggle {
    width: 36px;
    height: 36px;
    background: transparent;
    border: none;
    border-radius: var(--nx-radius-md);
    cursor: pointer;
    display: flex;
    align-items: center;
    justify-content: center;
    color: var(--nx-text-secondary);
    transition: var(--nx-transition-normal);
}

.nx-header__toggle:hover {
    background: var(--nx-bg-hover);
    color: var(--nx-text-primary);
}

.nx-header__title {
    font-size: var(--nx-font-size-xl);
    font-weight: var(--nx-font-weight-semibold);
    color: var(--nx-text-primary);
    margin: 0;
}

.nx-header__right {
    display: flex;
    align-items: center;
}

.nx-header__actions {
    display: flex;
    align-items: center;
    gap: var(--nx-space-4);
}

.nx-header__action {
    width: 36px;
    height: 36px;
    background: transparent;
    border: none;
    border-radius: var(--nx-radius-md);
    cursor: pointer;
    display: flex;
    align-items: center;
    justify-content: center;
    color: var(--nx-text-secondary);
    font-size: 20px;
    transition: var(--nx-transition-normal);
}

.nx-header__action:hover {
    background: var(--nx-bg-hover);
    color: var(--nx-text-primary);
}

.nx-header__user {
    display: flex;
    align-items: center;
    gap: var(--nx-space-3);
    padding: var(--nx-space-2);
    background: var(--nx-bg-secondary);
    border-radius: var(--nx-radius-lg);
}

.nx-header__avatar {
    width: 32px;
    height: 32px;
    background: linear-gradient(135deg, var(--nx-color-primary) 0%, #764ba2 100%);
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    color: white;
    font-size: 16px;
}

.nx-header__userinfo {
    display: flex;
    flex-direction: column;
}

.nx-header__name {
    font-size: var(--nx-font-size-sm);
    font-weight: var(--nx-font-weight-medium);
    color: var(--nx-text-primary);
}

.nx-header__role {
    font-size: var(--nx-font-size-xs);
    color: var(--nx-text-tertiary);
}

.nx-header__logout {
    width: 32px;
    height: 32px;
    background: transparent;
    border: none;
    border-radius: var(--nx-radius-md);
    cursor: pointer;
    display: flex;
    align-items: center;
    justify-content: center;
    color: var(--nx-text-tertiary);
    transition: var(--nx-transition-normal);
}

.nx-header__logout:hover {
    background: var(--nx-color-danger-light);
    color: var(--nx-color-danger);
}
</style>

<script>
document.getElementById('logoutBtn')?.addEventListener('click', async () => {
    try {
        await fetch('/api/v1/auth/logout', { method: 'POST' });
        window.location.href = '/login.html';
    } catch (e) {
        console.error('Logout failed:', e);
    }
});

document.getElementById('themeToggle')?.addEventListener('click', () => {
    const body = document.body;
    const currentTheme = body.getAttribute('data-theme') || 'light';
    const newTheme = currentTheme === 'light' ? 'dark' : 'light';
    body.setAttribute('data-theme', newTheme);
    localStorage.setItem('nx-theme', newTheme);
    
    const icon = document.querySelector('#themeToggle i');
    icon.className = newTheme === 'light' ? 'ri-moon-line' : 'ri-sun-line';
});
</script>
