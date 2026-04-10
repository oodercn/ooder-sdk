package net.ooder.skill.common.spi;

import net.ooder.skill.common.spi.bind.QrCodeInfo;
import net.ooder.skill.common.spi.bind.BindStatus;
import net.ooder.skill.common.spi.bind.BindInfo;

import java.util.List;

public interface PlatformBindService {
    
    QrCodeInfo generateBindQrCode(String platform);
    
    BindStatus checkBindStatus(String platform, String sessionId);
    
    BindInfo getBinding(String platform, String userId);
    
    List<BindInfo> getBindings(String userId);
    
    void unbind(String platform, String userId);
    
    List<String> getAvailablePlatforms();
}
