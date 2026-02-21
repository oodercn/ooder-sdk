package net.ooder.common.org.service;

import net.ooder.common.JDSException;
import net.ooder.engine.ConnectInfo;
import net.ooder.jds.core.User;

public interface UserInnerService {
    public void logout() throws JDSException;

    /**
     * 注册新用户
     * 
     * @param user
     * @return
     * @throws JDSException
     */
    public User register(User user) throws JDSException;

    /**
     * 登录验证
     * 
     * @param userName
     * @param password
     * @return
     * @throws JDSException
     */
    public ConnectInfo login(String userName, String password, String imei) throws JDSException;

    /**
     * 修改密码
     * 
     * @param user
     * @throws JDSException
     */
    public void modifyPwd(User user, String code) throws JDSException;

    /**
     * 发送验证码
     * 
     * @param userData
     * @return
     * @throws JDSException
     */
    public String sendCode(User userData) throws JDSException;

    /**
     * 发送指定号码验证码
     * 
     * @param phonenum
     * @return
     * @throws JDSException
     */
    public String sendCode(String phonenum) throws JDSException;

    /**
     * 保存用户信息
     * 
     * @param user
     * @return
     * @throws JDSException
     */
    public User saveUser(User user) throws JDSException;

    /**
     * 根据账户取得用户对象
     * 
     * @param account
     * @return
     * @throws JDSException
     */
    public User getUserByAccount(String account) throws JDSException;

    /**
     * 根据ID取得用户对象
     * 
     * @param Id
     * @return
     * @throws JDSException
     */
    public User getUserById(String Id) throws JDSException;

}
