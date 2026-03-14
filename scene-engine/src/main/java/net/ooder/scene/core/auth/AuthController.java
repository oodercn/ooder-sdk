package net.ooder.scene.core.auth;

import net.ooder.scene.core.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 认证控制器
 *
 * <p>提供角色查询等认证相关API</p>
 *
 * @author Ooder Team
 * @since 2.3.1
 */
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final RoleService roleService;

    @Autowired
    public AuthController(RoleService roleService) {
        this.roleService = roleService;
    }

    /**
     * 获取所有角色列表
     *
     * @return 角色列表
     */
    @GetMapping("/roles")
    public Result<List<Role>> getRoles() {
        List<Role> roles = roleService.getAllRoles();
        return Result.success(roles);
    }
}
