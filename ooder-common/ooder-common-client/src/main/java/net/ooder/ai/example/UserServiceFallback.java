/**
 * $RCSfile: UserServiceFallback.java,v $
 * $Revision: 1.0 $
 * $Date: 2025/08/25 $
 * <p>
 * Copyright (c) 2025 ooder.net
 * </p>
 * <p>
 * Company: ooder.net
 * </p>
 * <p>
 * License: MIT License
 * </p>
 */
package net.ooder.ai.example;


/**
 * 服务降级处理类，当UserService调用失败时提供默认实现
 */
public class UserServiceFallback  {

    public String getUserInfo(String userId) {
        return "默认用户信息（服务降级）";
    }
}