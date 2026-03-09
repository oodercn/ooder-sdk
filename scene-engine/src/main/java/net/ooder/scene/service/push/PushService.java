package net.ooder.scene.service.push;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * 推送服务接口
 *
 * <p>提供场景推送能力，支持领导向下属推送场景。</p>
 *
 * <h3>使用场景：</h3>
 * <ul>
 *   <li>领导激活场景后，选择下属推送</li>
 *   <li>获取推送反馈状态</li>
 *   <li>下属确认或拒绝推送</li>
 * </ul>
 *
 * @author Ooder Team
 * @version 2.3.1
 * @since 2.3.1
 */
public interface PushService {

    /**
     * 推送场景给下属
     *
     * @param request 推送请求
     * @return 推送结果
     */
    CompletableFuture<PushResult> pushToSubordinates(PushRequest request);

    /**
     * 获取推送详情
     *
     * @param pushId 推送ID
     * @return 推送详情
     */
    PushDetail getPushDetail(String pushId);

    /**
     * 获取场景的推送反馈列表
     *
     * @param sceneId 场景ID
     * @param leaderId 领导ID
     * @return 推送反馈列表
     */
    List<PushFeedback> getPushFeedbacks(String sceneId, String leaderId);

    /**
     * 确认推送
     *
     * @param pushId 推送ID
     * @param userId 用户ID
     */
    void confirmPush(String pushId, String userId);

    /**
     * 拒绝推送
     *
     * @param pushId 推送ID
     * @param userId 用户ID
     * @param reason 拒绝原因
     */
    void rejectPush(String pushId, String userId, String reason);

    /**
     * 取消推送
     *
     * @param pushId 推送ID
     * @return 是否成功
     */
    boolean cancelPush(String pushId);

    /**
     * 重推送给失败的用户
     *
     * @param pushId 原推送ID
     * @return 新推送ID
     */
    String retryPush(String pushId);

    /**
     * 获取用户收到的推送列表
     *
     * @param userId 用户ID
     * @return 推送列表
     */
    List<PushDetail> getReceivedPushes(String userId);
}
