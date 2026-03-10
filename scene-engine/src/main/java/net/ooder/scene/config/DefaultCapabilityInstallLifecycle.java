package net.ooder.scene.config;

import net.ooder.scene.skill.capability.Capability;
import net.ooder.scene.skill.install.CapabilityInstallLifecycle;
import net.ooder.scene.skill.install.InstallContext;
import net.ooder.scene.skill.install.InstallResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 默认能力安装生命周期实现
 *
 * @author ooder
 * @since 2.3.2
 */
public class DefaultCapabilityInstallLifecycle implements CapabilityInstallLifecycle {

    private static final Logger log = LoggerFactory.getLogger(DefaultCapabilityInstallLifecycle.class);

    @Override
    public void onPreInstall(Capability capability, InstallContext context) {
        log.info("[Lifecycle] Pre-install: {}", capability.getId());
    }

    @Override
    public void onPostInstall(Capability capability, InstallResult result) {
        log.info("[Lifecycle] Post-install: {}, success: {}", capability.getId(), result.isSuccess());
    }

    @Override
    public void onUninstall(Capability capability) {
        log.info("[Lifecycle] Uninstall: {}", capability.getId());
    }

    @Override
    public void onInstallFailed(Capability capability, Exception error) {
        log.error("[Lifecycle] Install failed: {}, error: {}", capability.getId(), error.getMessage());
    }
}
