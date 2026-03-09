package net.ooder.scene.skill.share;

import java.util.List;

/**
 * 知识分享服务接口
 *
 * <p>提供知识库分享的完整能力，包括：</p>
 * <ul>
 *   <li>分享链接生成</li>
 *   <li>分享权限控制</li>
 *   <li>分享记录管理</li>
 *   <li>团队协作</li>
 * </ul>
 *
 * <p>架构层次：应用层 - 知识分享</p>
 *
 * @author ooder
 * @since 2.3
 */
public interface ShareService {
    
    /**
     * 创建分享链接
     *
     * @param request 分享请求
     * @return 分享信息
     */
    ShareInfo createShare(ShareCreateRequest request);
    
    /**
     * 获取分享信息
     *
     * @param shareId 分享ID
     * @return 分享信息
     */
    ShareInfo getShare(String shareId);
    
    /**
     * 通过分享码获取分享信息
     *
     * @param shareCode 分享码
     * @return 分享信息
     */
    ShareInfo getShareByCode(String shareCode);
    
    /**
     * 验证分享码
     *
     * @param shareCode 分享码
     * @param password 密码（可选）
     * @return 验证结果
     */
    ShareValidationResult validateShare(String shareCode, String password);
    
    /**
     * 取消分享
     *
     * @param shareId 分享ID
     * @param userId 操作用户ID
     */
    void cancelShare(String shareId, String userId);
    
    /**
     * 列出知识库的所有分享
     *
     * @param kbId 知识库ID
     * @param userId 操作用户ID
     * @return 分享列表
     */
    List<ShareInfo> listShares(String kbId, String userId);
    
    /**
     * 列出用户创建的所有分享
     *
     * @param userId 用户ID
     * @return 分享列表
     */
    List<ShareInfo> listUserShares(String userId);
    
    /**
     * 更新分享设置
     *
     * @param shareId 分享ID
     * @param request 更新请求
     * @param userId 操作用户ID
     * @return 更新后的分享信息
     */
    ShareInfo updateShare(String shareId, ShareUpdateRequest request, String userId);
    
    /**
     * 记录分享访问
     *
     * @param shareId 分享ID
     * @param visitorId 访问者ID
     */
    void recordAccess(String shareId, String visitorId);
    
    /**
     * 获取分享统计
     *
     * @param shareId 分享ID
     * @return 统计信息
     */
    ShareStats getStats(String shareId);
}
