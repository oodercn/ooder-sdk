package net.ooder.skill.common.api;

import net.ooder.skill.common.model.LoginRequest;
import net.ooder.skill.common.model.ResultModel;
import net.ooder.skill.common.model.UserSession;
import net.ooder.skill.common.service.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@CrossOrigin(originPatterns = "*", allowCredentials = "true", allowedHeaders = "*")
public class AuthApi {

    private static final Logger log = LoggerFactory.getLogger(AuthApi.class);

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResultModel<UserSession> login(@RequestBody LoginRequest request, HttpServletRequest httpRequest) {
        log.info("[login] Login request: username={}, role={}", request.getUsername(), request.getRole());
        
        UserSession session = authService.login(request, httpRequest);
        if (session != null) {
            return ResultModel.success(session);
        }
        return ResultModel.error(401, "用户名或密码错误");
    }

    @PostMapping("/logout")
    public ResultModel<Void> logout(HttpServletRequest request) {
        log.info("[logout] Logout request");
        authService.logout(request);
        return ResultModel.success(null);
    }

    @GetMapping("/session")
    public ResultModel<UserSession> getSession(HttpServletRequest request) {
        UserSession user = authService.getCurrentUser(request);
        if (user != null) {
            return ResultModel.success(user);
        }
        return ResultModel.error(401, "未登录或会话已过期");
    }

    @GetMapping("/current-user")
    public ResultModel<UserSession> getCurrentUser(HttpServletRequest request) {
        return getSession(request);
    }

    @GetMapping("/roles")
    public ResultModel<List<Map<String, Object>>> getAvailableRoles() {
        List<Map<String, Object>> roles = authService.getAvailableRoles();
        return ResultModel.success(roles);
    }

    @GetMapping("/check-permission")
    public ResultModel<Map<String, Object>> checkPermission(
            @RequestParam String permission,
            HttpServletRequest request) {
        
        UserSession user = authService.getCurrentUser(request);
        Map<String, Object> result = new HashMap<>();
        result.put("hasPermission", authService.hasPermission(request, permission));
        result.put("user", user);
        
        return ResultModel.success(result);
    }

    @GetMapping("/check-role")
    public ResultModel<Map<String, Object>> checkRole(
            @RequestParam String role,
            HttpServletRequest request) {
        
        UserSession user = authService.getCurrentUser(request);
        Map<String, Object> result = new HashMap<>();
        result.put("hasRole", authService.hasRole(request, role));
        result.put("user", user);
        
        return ResultModel.success(result);
    }
}
