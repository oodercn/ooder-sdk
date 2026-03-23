package net.ooder.scene.core.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.ooder.scene.core.Result;
import net.ooder.scene.core.security.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * 权限拦截器
 *
 * <p>统一权限检查拦截器，支持 @RequirePermission 注解</p>
 *
 * @author Ooder Team
 * @since 2.3.1
 */
@Component
public class PermissionInterceptor implements HandlerInterceptor {

    private final PermissionService permissionService;
    private final ObjectMapper objectMapper;

    @Autowired
    public PermissionInterceptor(PermissionService permissionService, ObjectMapper objectMapper) {
        this.permissionService = permissionService;
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 只处理方法级别的拦截
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        HandlerMethod handlerMethod = (HandlerMethod) handler;

        // 检查类级别的注解
        RequirePermission classAnnotation = handlerMethod.getBeanType().getAnnotation(RequirePermission.class);
        // 检查方法级别的注解
        RequirePermission methodAnnotation = handlerMethod.getMethodAnnotation(RequirePermission.class);

        // 合并权限要求
        String[] requiredPermissions = mergePermissions(classAnnotation, methodAnnotation);
        RequirePermission.Logic logic = methodAnnotation != null ? methodAnnotation.logic() :
                                       (classAnnotation != null ? classAnnotation.logic() : RequirePermission.Logic.AND);

        // 如果没有权限要求，放行
        if (requiredPermissions.length == 0) {
            return true;
        }

        // 获取当前用户权限
        String userId = getCurrentUserId(request);
        List<String> userPermissions = permissionService.getUserPermissionStrings(userId).join();

        // 检查权限
        boolean hasPermission = checkPermissions(userPermissions, requiredPermissions, logic);

        if (!hasPermission) {
            sendErrorResponse(response, requiredPermissions);
            return false;
        }

        return true;
    }

    /**
     * 合并类级别和方法级别的权限要求
     */
    private String[] mergePermissions(RequirePermission classAnnotation, RequirePermission methodAnnotation) {
        if (classAnnotation == null && methodAnnotation == null) {
            return new String[0];
        }

        if (classAnnotation == null) {
            return methodAnnotation.value();
        }

        if (methodAnnotation == null) {
            return classAnnotation.value();
        }

        // 合并两个注解的权限
        String[] classPerms = classAnnotation.value();
        String[] methodPerms = methodAnnotation.value();
        String[] merged = new String[classPerms.length + methodPerms.length];
        System.arraycopy(classPerms, 0, merged, 0, classPerms.length);
        System.arraycopy(methodPerms, 0, merged, classPerms.length, methodPerms.length);
        return merged;
    }

    /**
     * 检查权限
     */
    private boolean checkPermissions(List<String> userPermissions, String[] requiredPermissions, RequirePermission.Logic logic) {
        if (logic == RequirePermission.Logic.AND) {
            // 需要所有权限
            return Arrays.stream(requiredPermissions)
                .allMatch(reqPerm -> userPermissions.contains(reqPerm));
        } else {
            // 任一权限即可
            return Arrays.stream(requiredPermissions)
                .anyMatch(reqPerm -> userPermissions.contains(reqPerm));
        }
    }

    /**
     * 获取当前用户ID
     */
    private String getCurrentUserId(HttpServletRequest request) {
        // 从请求头或会话中获取用户ID
        String userId = request.getHeader("X-User-Id");
        if (userId == null) {
            userId = (String) request.getSession().getAttribute("userId");
        }
        return userId != null ? userId : "anonymous";
    }

    /**
     * 发送错误响应
     */
    private void sendErrorResponse(HttpServletResponse response, String[] requiredPermissions) throws IOException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json;charset=UTF-8");

        Result<Void> result = Result.error("权限不足: 缺少 " + String.join(", ", requiredPermissions) + " 权限", 403);
        response.getWriter().write(objectMapper.writeValueAsString(result));
    }
}
