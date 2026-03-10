package net.ooder.scene.skill.install;

import net.ooder.scene.skill.capability.Capability;

/**
 * 能力安装生命周期回调接口
 *
 * <p>提供能力安装过程中的生命周期回调，支持：</p>
 * <ul>
 *   <li>安装前处理</li>
 *   <li>安装后处理</li>
 *   <li>卸载处理</li>
 *   <li>安装失败处理</li>
 * </ul>
 *
 * @author ooder
 * @since 2.3.2
 */
public interface CapabilityInstallLifecycle {

    /**
     * 安装前回调
     *
     * @param capability 能力
     * @param context 安装上下文
     */
    void onPreInstall(Capability capability, InstallContext context);

    /**
     * 安装后回调
     *
     * @param capability 能力
     * @param result 安装结果
     */
    void onPostInstall(Capability capability, InstallResult result);

    /**
     * 卸载回调
     *
     * @param capability 能力
     */
    void onUninstall(Capability capability);

    /**
     * 安装失败回调
     *
     * @param capability 能力
     * @param error 错误信息
     */
    void onInstallFailed(Capability capability, Exception error);
}
