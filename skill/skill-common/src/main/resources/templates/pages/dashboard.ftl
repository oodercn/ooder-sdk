<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>仪表盘 - Ooder MVP</title>
    <link rel="icon" type="image/svg+xml" href="/favicon.svg">
    <link rel="stylesheet" href="/css/remixicon/remixicon.css">
    <link rel="stylesheet" href="/css/base/variables.css">
    <link rel="stylesheet" href="/css/base/reset.css">
    <link rel="stylesheet" href="/css/base/typography.css">
    <link rel="stylesheet" href="/css/components/nx-button.css">
    <link rel="stylesheet" href="/css/components/nx-card.css">
    <link rel="stylesheet" href="/css/components/nx-form.css">
    <link rel="stylesheet" href="/css/components/nx-badge.css">
    <link rel="stylesheet" href="/css/layouts/nx-page.css">
    <style>
        .dashboard-page {
            min-height: 100vh;
            background: var(--nx-bg-secondary);
        }
        
        .dashboard-header {
            background: linear-gradient(135deg, var(--nx-color-primary) 0%, #764ba2 100%);
            color: white;
            padding: var(--nx-space-6);
        }
        
        .dashboard-header__content {
            max-width: var(--nx-page-max-width);
            margin: 0 auto;
            display: flex;
            align-items: center;
            justify-content: space-between;
        }
        
        .dashboard-header__title {
            display: flex;
            align-items: center;
            gap: var(--nx-space-3);
        }
        
        .dashboard-header__logo {
            width: 44px;
            height: 44px;
            background: rgba(255,255,255,0.2);
            border-radius: var(--nx-radius-lg);
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 22px;
        }
        
        .dashboard-header__text h1 {
            font-size: var(--nx-font-size-xl);
            margin: 0;
        }
        
        .dashboard-header__text p {
            font-size: var(--nx-font-size-sm);
            opacity: 0.8;
            margin: var(--nx-space-1) 0 0 0;
        }
        
        .dashboard-header__actions {
            display: flex;
            align-items: center;
            gap: var(--nx-space-4);
        }
        
        .user-menu {
            display: flex;
            align-items: center;
            gap: var(--nx-space-3);
            background: rgba(255,255,255,0.15);
            padding: var(--nx-space-2) var(--nx-space-4);
            border-radius: var(--nx-radius-full);
        }
        
        .user-menu__avatar {
            width: 36px;
            height: 36px;
            background: rgba(255,255,255,0.2);
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
        }
        
        .user-menu__name {
            font-weight: var(--nx-font-weight-medium);
        }
        
        .logout-btn {
            background: rgba(255,255,255,0.2);
            border: none;
            color: white;
            padding: var(--nx-space-2) var(--nx-space-4);
            border-radius: var(--nx-radius-md);
            cursor: pointer;
            display: flex;
            align-items: center;
            gap: var(--nx-space-2);
            transition: var(--nx-transition-normal);
        }
        
        .logout-btn:hover {
            background: rgba(255,255,255,0.3);
        }
        
        .dashboard-content {
            max-width: var(--nx-page-max-width);
            margin: 0 auto;
            padding: var(--nx-space-6);
        }
        
        .stats-grid {
            display: grid;
            grid-template-columns: repeat(4, 1fr);
            gap: var(--nx-space-5);
            margin-bottom: var(--nx-space-6);
        }
        
        @media (max-width: 900px) {
            .stats-grid {
                grid-template-columns: repeat(2, 1fr);
            }
        }
        
        @media (max-width: 600px) {
            .stats-grid {
                grid-template-columns: 1fr;
            }
        }
        
        .stat-card {
            background: var(--nx-bg-primary);
            border-radius: var(--nx-radius-lg);
            padding: var(--nx-space-5);
            box-shadow: var(--nx-shadow-sm);
            border: 1px solid var(--nx-border-color-light);
            display: flex;
            align-items: center;
            gap: var(--nx-space-4);
        }
        
        .stat-card__icon {
            width: 56px;
            height: 56px;
            border-radius: var(--nx-radius-lg);
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 28px;
        }
        
        .stat-card__icon--users { background: #e0f2fe; color: #0ea5e9; }
        .stat-card__icon--depts { background: #fef3c7; color: #f59e0b; }
        .stat-card__icon--roles { background: #dbeafe; color: #3b82f6; }
        .stat-card__icon--health { background: #d1fae5; color: #10b981; }
        
        .stat-card__info h3 {
            font-size: var(--nx-font-size-3xl);
            font-weight: var(--nx-font-weight-bold);
            color: var(--nx-text-primary);
            margin: 0;
            line-height: 1.2;
        }
        
        .stat-card__info p {
            font-size: var(--nx-font-size-sm);
            color: var(--nx-text-secondary);
            margin: var(--nx-space-1) 0 0 0;
        }
        
        .content-grid {
            display: grid;
            grid-template-columns: 2fr 1fr;
            gap: var(--nx-space-5);
        }
        
        @media (max-width: 900px) {
            .content-grid {
                grid-template-columns: 1fr;
            }
        }
        
        .quick-actions {
            display: grid;
            grid-template-columns: repeat(2, 1fr);
            gap: var(--nx-space-3);
        }
        
        .action-btn {
            display: flex;
            flex-direction: column;
            align-items: center;
            gap: var(--nx-space-2);
            padding: var(--nx-space-5);
            background: var(--nx-bg-secondary);
            border-radius: var(--nx-radius-lg);
            border: none;
            cursor: pointer;
            transition: var(--nx-transition-normal);
        }
        
        .action-btn:hover {
            background: var(--nx-color-primary);
            color: white;
        }
        
        .action-btn i {
            font-size: 24px;
        }
        
        .action-btn span {
            font-size: var(--nx-font-size-sm);
            font-weight: var(--nx-font-weight-medium);
        }
        
        .api-list {
            display: flex;
            flex-direction: column;
            gap: var(--nx-space-2);
        }
        
        .api-item {
            display: flex;
            align-items: center;
            justify-content: space-between;
            padding: var(--nx-space-3) var(--nx-space-4);
            background: var(--nx-bg-secondary);
            border-radius: var(--nx-radius-md);
            font-size: var(--nx-font-size-sm);
        }
        
        .api-item__method {
            font-weight: var(--nx-font-weight-semibold);
            padding: var(--nx-space-1) var(--nx-space-2);
            border-radius: var(--nx-radius-sm);
            font-size: var(--nx-font-size-xs);
        }
        
        .api-item__method--get { background: #dbeafe; color: #3b82f6; }
        .api-item__method--post { background: #d1fae5; color: #10b981; }
        
        .api-item__path {
            color: var(--nx-text-secondary);
            font-family: var(--nx-font-family-mono);
            flex: 1;
            margin: 0 var(--nx-space-3);
        }
        
        .api-item__status {
            font-size: var(--nx-font-size-xs);
            padding: var(--nx-space-1) var(--nx-space-2);
            border-radius: var(--nx-radius-sm);
            background: #d1fae5;
            color: #10b981;
        }
    </style>
</head>
<body class="nx-theme--${theme!'light'}">
    <div class="dashboard-page">
        <header class="dashboard-header">
            <div class="dashboard-header__content">
                <div class="dashboard-header__title">
                    <div class="dashboard-header__logo">
                        <i class="ri-dashboard-3-line"></i>
                    </div>
                    <div class="dashboard-header__text">
                        <h1>MVP 控制台</h1>
                        <p id="welcome-text">欢迎回来<#if name?? && name?length gt 0>，${name}</#if></p>
                    </div>
                </div>
                <div class="dashboard-header__actions">
                    <div class="user-menu">
                        <div class="user-menu__avatar">
                            <i class="ri-user-line"></i>
                        </div>
                        <span class="user-menu__name" id="user-name">${name!'用户'}</span>
                    </div>
                    <button class="logout-btn" onclick="handleLogout()">
                        <i class="ri-logout-box-line"></i> 退出
                    </button>
                </div>
            </div>
        </header>
        
        <main class="dashboard-content">
            <div class="stats-grid">
                <div class="stat-card">
                    <div class="stat-card__icon stat-card__icon--users">
                        <i class="ri-user-line"></i>
                    </div>
                    <div class="stat-card__info">
                        <h3 id="user-count">-</h3>
                        <p>用户总数</p>
                    </div>
                </div>
                
                <div class="stat-card">
                    <div class="stat-card__icon stat-card__icon--depts">
                        <i class="ri-building-line"></i>
                    </div>
                    <div class="stat-card__info">
                        <h3 id="dept-count">-</h3>
                        <p>部门数量</p>
                    </div>
                </div>
                
                <div class="stat-card">
                    <div class="stat-card__icon stat-card__icon--roles">
                        <i class="ri-shield-user-line"></i>
                    </div>
                    <div class="stat-card__info">
                        <h3 id="role-count">4</h3>
                        <p>角色类型</p>
                    </div>
                </div>
                
                <div class="stat-card">
                    <div class="stat-card__icon stat-card__icon--health">
                        <i class="ri-heart-pulse-line"></i>
                    </div>
                    <div class="stat-card__info">
                        <h3 id="health-status">UP</h3>
                        <p>系统状态</p>
                    </div>
                </div>
            </div>
            
            <div class="content-grid">
                <div class="nx-card">
                    <div class="nx-card__header">
                        <h3 class="nx-card__title">快速操作</h3>
                    </div>
                    <div class="nx-card__body">
                        <div class="quick-actions">
                            <button class="action-btn" onclick="window.location.href='/login.html'">
                                <i class="ri-login-box-line"></i>
                                <span>登录管理</span>
                            </button>
                            <button class="action-btn" onclick="loadData()">
                                <i class="ri-refresh-line"></i>
                                <span>刷新数据</span>
                            </button>
                            <button class="action-btn" onclick="window.location.href='/actuator/health'">
                                <i class="ri-heart-pulse-line"></i>
                                <span>健康检查</span>
                            </button>
                            <button class="action-btn" onclick="window.location.href='/'">
                                <i class="ri-home-line"></i>
                                <span>返回首页</span>
                            </button>
                        </div>
                    </div>
                </div>
                
                <div class="nx-card">
                    <div class="nx-card__header">
                        <h3 class="nx-card__title">API 状态</h3>
                    </div>
                    <div class="nx-card__body">
                        <div class="api-list">
                            <div class="api-item">
                                <span class="api-item__method api-item__method--get">GET</span>
                                <span class="api-item__path">/api/v1/auth/roles</span>
                                <span class="api-item__status">OK</span>
                            </div>
                            <div class="api-item">
                                <span class="api-item__method api-item__method--get">GET</span>
                                <span class="api-item__path">/api/v1/menu</span>
                                <span class="api-item__status">OK</span>
                            </div>
                            <div class="api-item">
                                <span class="api-item__method api-item__method--get">GET</span>
                                <span class="api-item__path">/api/v1/llm/providers</span>
                                <span class="api-item__status">OK</span>
                            </div>
                            <div class="api-item">
                                <span class="api-item__method api-item__method--get">GET</span>
                                <span class="api-item__path">/api/v1/org/users</span>
                                <span class="api-item__status">OK</span>
                            </div>
                            <div class="api-item">
                                <span class="api-item__method api-item__method--post">POST</span>
                                <span class="api-item__path">/api/v1/auth/login</span>
                                <span class="api-item__status">OK</span>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </main>
    </div>
    
    <script src="/js/utils/api.js"></script>
    <script src="/js/utils/auth.js"></script>
    <script>
        window.__NX_CONFIG__ = {
            version: '${version!"2.3.1"}',
            theme: '${theme!"light"}',
            timestamp: ${timestamp!0}
        };
        
        <#if user??>
        window.__USER__ = {
            userId: '${userId!""}',
            username: '${username!""}',
            name: '${name!""}',
            role: '${role!""}',
            roleType: '${roleType!""}',
            permissions: <#if permissions??><#list permissions as p>"${p}"<#if p_has_next>,</#if></#list><#else>[]</#if>
        };
        <#else>
        window.__USER__ = null;
        </#if>
        
        function handleLogout() {
            if (typeof AuthUtils !== 'undefined') {
                AuthUtils.logout();
            } else {
                document.cookie = 'token=; expires=Thu, 01 Jan 1970 00:00:00 UTC; path=/;';
                window.location.href = '/login';
            }
        }
        
        async function loadData() {
            try {
                const user = window.__USER__;
                if (user && user.name) {
                    document.getElementById('user-name').textContent = user.name;
                }
                
                if (typeof OrgApi !== 'undefined') {
                    const usersResult = await OrgApi.getUsers();
                    if (usersResult.data) {
                        document.getElementById('user-count').textContent = usersResult.data.length;
                    }
                    
                    const deptsResult = await OrgApi.getDepartments();
                    if (deptsResult.data) {
                        document.getElementById('dept-count').textContent = deptsResult.data.length;
                    }
                }
                
                if (typeof SystemApi !== 'undefined') {
                    const healthResult = await SystemApi.getHealth();
                    if (healthResult.data) {
                        document.getElementById('health-status').textContent = healthResult.data.status || 'UP';
                    }
                }
            } catch (e) {
                console.error('Load data error:', e);
            }
        }
        
        document.addEventListener('DOMContentLoaded', function() {
            loadData();
        });
    </script>
</body>
</html>
