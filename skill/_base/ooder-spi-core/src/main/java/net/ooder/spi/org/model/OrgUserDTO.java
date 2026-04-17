package net.ooder.spi.org.model;

import lombok.Data;
import java.util.List;

@Data
public class OrgUserDTO {

    private String userId;

    private String name;

    private String email;

    private String phone;

    private String departmentId;

    private String departmentName;

    private String role;

    private String title;

    private String avatar;

    private String password;

    private List<String> permissions;

    private long createTime;

    private long updateTime;

    private boolean active;
}
