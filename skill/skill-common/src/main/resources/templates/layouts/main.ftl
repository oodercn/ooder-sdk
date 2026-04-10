<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${pageTitle!'Ooder MVP'}</title>
    <link rel="icon" type="image/svg+xml" href="/favicon.svg">
    <link rel="stylesheet" href="/css/remixicon/remixicon.css">
    <link rel="stylesheet" href="/css/base/variables.css">
    <link rel="stylesheet" href="/css/base/reset.css">
    <link rel="stylesheet" href="/css/base/typography.css">
    <link rel="stylesheet" href="/css/components/nx-button.css">
    <link rel="stylesheet" href="/css/components/nx-card.css">
    <link rel="stylesheet" href="/css/components/nx-form.css">
    <link rel="stylesheet" href="/css/layouts/nx-page.css">
    <#if cssFiles??>
    <#list cssFiles as css>
    <link rel="stylesheet" href="${css}">
    </#list>
    </#if>
    <style>
        body {
            font-family: var(--nx-font-family);
            background: var(--nx-bg-secondary);
            color: var(--nx-text-primary);
        }
    </style>
</head>
<body class="nx-theme--${theme!'light'}" data-page="${pageId!'default'}">
    <div class="nx-page">
        <#include "components/sidebar.ftl">
        
        <main class="nx-page__main">
            <#include "components/header.ftl">
            
            <div class="nx-page__content">
                <#nested>
            </div>
        </main>
    </div>
    
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
    </script>
    
    <script type="module" src="/js/app.js"></script>
    <#if jsFiles??>
    <#list jsFiles as js>
    <script type="module" src="${js}"></script>
    </#list>
    </#if>
</body>
</html>
