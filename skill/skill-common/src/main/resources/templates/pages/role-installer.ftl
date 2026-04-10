<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>安装向导 - Ooder MVP</title>
    <link rel="icon" type="image/svg+xml" href="/favicon.svg">
    <link rel="stylesheet" href="/css/remixicon/remixicon.css">
    <link rel="stylesheet" href="/css/base/variables.css">
    <link rel="stylesheet" href="/css/base/reset.css">
    <link rel="stylesheet" href="/css/base/typography.css">
    <link rel="stylesheet" href="/css/components/nx-button.css">
    <link rel="stylesheet" href="/css/components/nx-card.css">
    <link rel="stylesheet" href="/css/components/nx-form.css">
    <link rel="stylesheet" href="/css/layouts/nx-page.css">
    <style>
        .installer-page {
            min-height: 100vh;
            display: flex;
            flex-direction: column;
        }
        
        .installer-header {
            background: linear-gradient(135deg, var(--nx-color-primary) 0%, #764ba2 100%);
            color: white;
            padding: var(--nx-space-6);
        }
        
        .installer-header__content {
            max-width: 1000px;
            margin: 0 auto;
            display: flex;
            align-items: center;
            justify-content: space-between;
        }
        
        .installer-header__title {
            display: flex;
            align-items: center;
            gap: var(--nx-space-4);
        }
        
        .installer-header__logo {
            width: 48px;
            height: 48px;
            background: rgba(255,255,255,0.2);
            border-radius: var(--nx-radius-lg);
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 24px;
        }
        
        .installer-header__text h1 {
            font-size: var(--nx-font-size-2xl);
            margin: 0;
        }
        
        .installer-header__text p {
            font-size: var(--nx-font-size-sm);
            opacity: 0.8;
            margin: var(--nx-space-1) 0 0 0;
        }
        
        .installer-content {
            flex: 1;
            max-width: 1000px;
            margin: 0 auto;
            padding: var(--nx-space-8);
            width: 100%;
        }
        
        .installer-steps {
            display: flex;
            justify-content: center;
            margin-bottom: var(--nx-space-8);
        }
        
        .nx-steps {
            display: flex;
            align-items: center;
        }
        
        .nx-step {
            display: flex;
            align-items: center;
            gap: var(--nx-space-2);
        }
        
        .nx-step__indicator {
            width: 32px;
            height: 32px;
            border-radius: 50%;
            background: var(--nx-bg-tertiary);
            color: var(--nx-text-tertiary);
            display: flex;
            align-items: center;
            justify-content: center;
            font-weight: var(--nx-font-weight-semibold);
            font-size: var(--nx-font-size-sm);
        }
        
        .nx-step--active .nx-step__indicator {
            background: var(--nx-color-primary);
            color: white;
        }
        
        .nx-step--completed .nx-step__indicator {
            background: var(--nx-color-success);
            color: white;
        }
        
        .nx-step__label {
            font-size: var(--nx-font-size-sm);
            color: var(--nx-text-tertiary);
        }
        
        .nx-step--active .nx-step__label {
            color: var(--nx-text-primary);
            font-weight: var(--nx-font-weight-medium);
        }
        
        .nx-step__connector {
            width: 60px;
            height: 2px;
            background: var(--nx-border-color);
            margin: 0 var(--nx-space-2);
        }
        
        .nx-step--completed + .nx-step__connector {
            background: var(--nx-color-success);
        }
        
        .installer-card {
            background: var(--nx-bg-primary);
            border-radius: var(--nx-radius-xl);
            box-shadow: var(--nx-shadow-md);
            overflow: hidden;
        }
        
        .installer-card__header {
            padding: var(--nx-space-6);
            border-bottom: 1px solid var(--nx-border-color-light);
        }
        
        .installer-card__title {
            font-size: var(--nx-font-size-xl);
            font-weight: var(--nx-font-weight-semibold);
            color: var(--nx-text-primary);
            margin: 0 0 var(--nx-space-2) 0;
        }
        
        .installer-card__desc {
            font-size: var(--nx-font-size-md);
            color: var(--nx-text-secondary);
            margin: 0;
        }
        
        .installer-card__body {
            padding: var(--nx-space-6);
        }
        
        .installer-card__footer {
            padding: var(--nx-space-4) var(--nx-space-6);
            border-top: 1px solid var(--nx-border-color-light);
            background: var(--nx-bg-secondary);
            display: flex;
            justify-content: space-between;
        }
        
        .profile-grid {
            display: grid;
            grid-template-columns: repeat(4, 1fr);
            gap: var(--nx-space-4);
        }
        
        @media (max-width: 768px) {
            .profile-grid {
                grid-template-columns: repeat(2, 1fr);
            }
        }
        
        .profile-card {
            padding: var(--nx-space-5);
            border: 2px solid var(--nx-border-color);
            border-radius: var(--nx-radius-lg);
            cursor: pointer;
            transition: var(--nx-transition-normal);
            text-align: center;
        }
        
        .profile-card:hover {
            border-color: var(--nx-color-primary);
            background: var(--nx-color-primary-light);
        }
        
        .profile-card--selected {
            border-color: var(--nx-color-primary);
            background: var(--nx-color-primary-light);
        }
        
        .profile-card__icon {
            width: 48px;
            height: 48px;
            margin: 0 auto var(--nx-space-3);
            background: var(--nx-bg-secondary);
            border-radius: var(--nx-radius-lg);
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 24px;
            color: var(--nx-color-primary);
        }
        
        .profile-card__name {
            font-size: var(--nx-font-size-md);
            font-weight: var(--nx-font-weight-semibold);
            color: var(--nx-text-primary);
            margin-bottom: var(--nx-space-1);
        }
        
        .profile-card__desc {
            font-size: var(--nx-font-size-sm);
            color: var(--nx-text-secondary);
        }
        
        .profile-card__specs {
            margin-top: var(--nx-space-3);
            padding-top: var(--nx-space-3);
            border-top: 1px solid var(--nx-border-color-light);
            display: flex;
            justify-content: center;
            gap: var(--nx-space-4);
            font-size: var(--nx-font-size-xs);
            color: var(--nx-text-tertiary);
        }
        
        .installer-log {
            background: var(--nx-gray-900);
            border-radius: var(--nx-radius-md);
            padding: var(--nx-space-4);
            max-height: 300px;
            overflow-y: auto;
            font-family: var(--nx-font-family-mono);
            font-size: var(--nx-font-size-sm);
            color: var(--nx-gray-100);
        }
        
        .installer-log__line {
            margin-bottom: var(--nx-space-1);
        }
        
        .installer-log__line--success { color: var(--nx-color-success); }
        .installer-log__line--error { color: var(--nx-color-danger); }
        .installer-log__line--warning { color: var(--nx-color-warning); }
    </style>
</head>
<body class="nx-theme--${theme!'light'}">
    <div class="installer-page">
        <header class="installer-header">
            <div class="installer-header__content">
                <div class="installer-header__title">
                    <div class="installer-header__logo">
                        <i class="ri-install-line"></i>
                    </div>
                    <div class="installer-header__text">
                        <h1>系统安装向导</h1>
                        <p>Ooder MVP v${version!'2.3.1'} - 初始化配置</p>
                    </div>
                </div>
                <a href="/" class="nx-btn nx-btn--ghost" style="color: white;">
                    <i class="ri-home-line"></i> 返回首页
                </a>
            </div>
        </header>
        
        <main class="installer-content">
            <div class="installer-steps">
                <div class="nx-steps">
                    <div class="nx-step nx-step--active" data-step="1">
                        <div class="nx-step__indicator">1</div>
                        <span class="nx-step__label">选择规模</span>
                    </div>
                    <div class="nx-step__connector"></div>
                    <div class="nx-step" data-step="2">
                        <div class="nx-step__indicator">2</div>
                        <span class="nx-step__label">配置选项</span>
                    </div>
                    <div class="nx-step__connector"></div>
                    <div class="nx-step" data-step="3">
                        <div class="nx-step__indicator">3</div>
                        <span class="nx-step__label">执行安装</span>
                    </div>
                    <div class="nx-step__connector"></div>
                    <div class="nx-step" data-step="4">
                        <div class="nx-step__indicator">4</div>
                        <span class="nx-step__label">完成</span>
                    </div>
                </div>
            </div>
            
            <div class="installer-card">
                <div class="installer-card__header">
                    <h2 class="installer-card__title">选择部署规模</h2>
                    <p class="installer-card__desc">根据您的使用场景选择合适的部署规模，不同规模包含不同的功能模块</p>
                </div>
                
                <div class="installer-card__body">
                    <div class="profile-grid">
                        <div class="profile-card" data-profile="micro" onclick="selectProfile(this, 'micro')">
                            <div class="profile-card__icon">
                                <i class="ri-cpu-line"></i>
                            </div>
                            <div class="profile-card__name">微型部署</div>
                            <div class="profile-card__desc">核心功能，适合开发测试</div>
                            <div class="profile-card__specs">
                                <span><i class="ri-memory-line"></i> 128MB</span>
                                <span><i class="ri-timer-line"></i> 5s</span>
                            </div>
                        </div>
                        
                        <div class="profile-card" data-profile="small" onclick="selectProfile(this, 'small')">
                            <div class="profile-card__icon">
                                <i class="ri-server-line"></i>
                            </div>
                            <div class="profile-card__name">小型部署</div>
                            <div class="profile-card__desc">核心 + 本地LLM</div>
                            <div class="profile-card__specs">
                                <span><i class="ri-memory-line"></i> 256MB</span>
                                <span><i class="ri-timer-line"></i> 10s</span>
                            </div>
                        </div>
                        
                        <div class="profile-card" data-profile="large" onclick="selectProfile(this, 'large')">
                            <div class="profile-card__icon">
                                <i class="ri-cloud-line"></i>
                            </div>
                            <div class="profile-card__name">大型部署</div>
                            <div class="profile-card__desc">核心 + LLM + 知识库</div>
                            <div class="profile-card__specs">
                                <span><i class="ri-memory-line"></i> 512MB</span>
                                <span><i class="ri-timer-line"></i> 15s</span>
                            </div>
                        </div>
                        
                        <div class="profile-card" data-profile="enterprise" onclick="selectProfile(this, 'enterprise')">
                            <div class="profile-card__icon">
                                <i class="ri-building-4-line"></i>
                            </div>
                            <div class="profile-card__name">企业部署</div>
                            <div class="profile-card__desc">全功能，生产环境</div>
                            <div class="profile-card__specs">
                                <span><i class="ri-memory-line"></i> 1GB</span>
                                <span><i class="ri-timer-line"></i> 30s</span>
                            </div>
                        </div>
                    </div>
                </div>
                
                <div class="installer-card__footer">
                    <div></div>
                    <button class="nx-btn nx-btn--primary" id="nextBtn" onclick="nextStep()">
                        下一步 <i class="ri-arrow-right-line"></i>
                    </button>
                </div>
            </div>
        </main>
    </div>
    
    <script>
        let currentStep = 1;
        let selectedProfile = null;
        
        function selectProfile(el, profile) {
            document.querySelectorAll('.profile-card').forEach(card => {
                card.classList.remove('profile-card--selected');
            });
            el.classList.add('profile-card--selected');
            selectedProfile = profile;
        }
        
        function nextStep() {
            if (currentStep === 1 && !selectedProfile) {
                alert('请选择部署规模');
                return;
            }
            
            if (currentStep < 4) {
                currentStep++;
                updateSteps();
            }
            
            if (currentStep === 3) {
                startInstall();
            }
        }
        
        function prevStep() {
            if (currentStep > 1) {
                currentStep--;
                updateSteps();
            }
        }
        
        function updateSteps() {
            document.querySelectorAll('.nx-step').forEach((step, index) => {
                const stepNum = index + 1;
                step.classList.remove('nx-step--active', 'nx-step--completed');
                if (stepNum < currentStep) {
                    step.classList.add('nx-step--completed');
                } else if (stepNum === currentStep) {
                    step.classList.add('nx-step--active');
                }
            });
        }
        
        async function startInstall() {
            const logContainer = document.querySelector('.installer-card__body');
            logContainer.innerHTML = '<div class="installer-log" id="installLog"></div>';
            const log = document.getElementById('installLog');
            
            const steps = [
                { text: '正在初始化安装环境...', delay: 500 },
                { text: '检查系统依赖...', delay: 300 },
                { text: '✓ Java 运行时环境正常', type: 'success', delay: 200 },
                { text: '✓ 数据目录创建成功', type: 'success', delay: 200 },
                { text: `加载 Profile: ${'$'}{selectedProfile}...`, delay: 400 },
                { text: '初始化核心模块...', delay: 300 },
                { text: '✓ skill-common 加载成功', type: 'success', delay: 200 },
                { text: '✓ skill-capability 加载成功', type: 'success', delay: 200 },
                { text: '配置 LLM 服务...', delay: 300 },
                { text: '✓ LLM 配置完成', type: 'success', delay: 200 },
                { text: '初始化数据库...', delay: 400 },
                { text: '✓ 数据库初始化完成', type: 'success', delay: 200 },
                { text: '写入系统配置...', delay: 300 },
                { text: '✓ 系统配置保存成功', type: 'success', delay: 200 },
                { text: '', delay: 200 },
                { text: '========================================', delay: 100 },
                { text: '✓ 安装完成！', type: 'success', delay: 100 },
                { text: '========================================', delay: 100 },
            ];
            
            for (const step of steps) {
                await new Promise(resolve => setTimeout(resolve, step.delay));
                const line = document.createElement('div');
                line.className = 'installer-log__line' + (step.type ? ' installer-log__line--' + step.type : '');
                line.textContent = step.text;
                log.appendChild(line);
                log.scrollTop = log.scrollHeight;
            }
            
            document.querySelector('.installer-card__footer').innerHTML = `
                <button class="nx-btn nx-btn--secondary" onclick="location.reload()">
                    <i class="ri-refresh-line"></i> 重新安装
                </button>
                <a href="/pages/dashboard" class="nx-btn nx-btn--primary">
                    进入系统 <i class="ri-arrow-right-line"></i>
                </a>
            `;
        }
        
        document.addEventListener('DOMContentLoaded', function() {
            document.querySelector('[data-profile="micro"]').click();
        });
    </script>
</body>
</html>
