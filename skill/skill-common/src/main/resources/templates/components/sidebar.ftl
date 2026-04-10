<aside class="nx-sidebar" id="nxSidebar">
    <div class="nx-sidebar__header">
        <div class="nx-sidebar__logo">
            <i class="ri-rocket-2-line"></i>
        </div>
        <span class="nx-sidebar__title">Ooder MVP</span>
    </div>
    
    <nav class="nx-sidebar__nav">
        <ul class="nx-sidebar__menu">
            <li class="nx-sidebar__item">
                <a href="/" class="nx-sidebar__link <#if pageId?? && pageId == 'home'>nx-sidebar__link--active</#if>">
                    <i class="ri-home-line"></i>
                    <span>首页</span>
                </a>
            </li>
            
            <#if user??>
            <li class="nx-sidebar__item">
                <a href="/pages/dashboard" class="nx-sidebar__link <#if pageId?? && pageId == 'dashboard'>nx-sidebar__link--active</#if>">
                    <i class="ri-dashboard-line"></i>
                    <span>仪表盘</span>
                </a>
            </li>
            
            <#if roleType?? && (roleType == 'admin' || roleType == 'installer')>
            <li class="nx-sidebar__item">
                <a href="/pages/capabilities" class="nx-sidebar__link">
                    <i class="ri-puzzle-line"></i>
                    <span>能力管理</span>
                </a>
            </li>
            <li class="nx-sidebar__item">
                <a href="/pages/scenes" class="nx-sidebar__link">
                    <i class="ri-git-branch-line"></i>
                    <span>场景管理</span>
                </a>
            </li>
            </#if>
            
            <#if roleType?? && roleType == 'admin'>
            <li class="nx-sidebar__item">
                <a href="/pages/users" class="nx-sidebar__link">
                    <i class="ri-user-settings-line"></i>
                    <span>用户管理</span>
                </a>
            </li>
            <li class="nx-sidebar__item">
                <a href="/pages/config" class="nx-sidebar__link">
                    <i class="ri-settings-3-line"></i>
                    <span>系统配置</span>
                </a>
            </li>
            </#if>
            
            <#if roleType?? && roleType == 'installer'>
            <li class="nx-sidebar__item">
                <a href="/pages/install" class="nx-sidebar__link">
                    <i class="ri-install-line"></i>
                    <span>安装向导</span>
                </a>
            </li>
            </#if>
            </#if>
        </ul>
    </nav>
    
    <div class="nx-sidebar__footer">
        <button class="nx-sidebar__toggle" id="sidebarToggle" title="切换侧边栏">
            <i class="ri-menu-fold-line"></i>
        </button>
    </div>
</aside>

<style>
.nx-sidebar {
    width: var(--nx-sidebar-width, 260px);
    height: 100vh;
    background: var(--nx-bg-primary);
    border-right: 1px solid var(--nx-border-color-light);
    display: flex;
    flex-direction: column;
    position: fixed;
    left: 0;
    top: 0;
    z-index: var(--nx-z-index-sticky);
    transition: var(--nx-transition-normal);
}

.nx-sidebar--collapsed {
    width: var(--nx-sidebar-collapsed-width, 64px);
}

.nx-sidebar__header {
    padding: var(--nx-space-4);
    display: flex;
    align-items: center;
    gap: var(--nx-space-3);
    border-bottom: 1px solid var(--nx-border-color-light);
}

.nx-sidebar__logo {
    width: 40px;
    height: 40px;
    background: linear-gradient(135deg, var(--nx-color-primary) 0%, #764ba2 100%);
    border-radius: var(--nx-radius-lg);
    display: flex;
    align-items: center;
    justify-content: center;
    color: white;
    font-size: 20px;
    flex-shrink: 0;
}

.nx-sidebar__title {
    font-size: var(--nx-font-size-lg);
    font-weight: var(--nx-font-weight-semibold);
    color: var(--nx-text-primary);
}

.nx-sidebar__nav {
    flex: 1;
    overflow-y: auto;
    padding: var(--nx-space-4) 0;
}

.nx-sidebar__menu {
    list-style: none;
    padding: 0;
    margin: 0;
}

.nx-sidebar__item {
    margin: var(--nx-space-1) var(--nx-space-2);
}

.nx-sidebar__link {
    display: flex;
    align-items: center;
    gap: var(--nx-space-3);
    padding: var(--nx-space-3) var(--nx-space-4);
    color: var(--nx-text-secondary);
    text-decoration: none;
    border-radius: var(--nx-radius-md);
    transition: var(--nx-transition-normal);
}

.nx-sidebar__link:hover {
    background: var(--nx-bg-hover);
    color: var(--nx-text-primary);
}

.nx-sidebar__link--active {
    background: var(--nx-color-primary-light);
    color: var(--nx-color-primary);
}

.nx-sidebar__link i {
    font-size: 20px;
    flex-shrink: 0;
}

.nx-sidebar__footer {
    padding: var(--nx-space-4);
    border-top: 1px solid var(--nx-border-color-light);
}

.nx-sidebar__toggle {
    width: 100%;
    padding: var(--nx-space-2);
    background: var(--nx-bg-secondary);
    border: 1px solid var(--nx-border-color);
    border-radius: var(--nx-radius-md);
    cursor: pointer;
    display: flex;
    align-items: center;
    justify-content: center;
    color: var(--nx-text-secondary);
    transition: var(--nx-transition-normal);
}

.nx-sidebar__toggle:hover {
    background: var(--nx-bg-tertiary);
    color: var(--nx-text-primary);
}
</style>
