package net.ooder.scene.audit;

import net.ooder.scene.core.*;
import net.ooder.scene.core.PageResult;
import net.ooder.scene.core.AuditLog;
import net.ooder.scene.core.AuditLogFilter;
import net.ooder.scene.core.PageRequest;

/**
 * AuditService 审计服务接口
 * 
 * <p>提供统一的审计日志记录和查询功能。</p>
 * 
 * @author Ooder Team
 * @version 0.7.3
 */
public interface AuditService {

    /**
     * 记录审计日志
     * 
     * @param log 审计日志
     */
    void log(AuditLog log);

    /**
     * 记录审计日志（简化版）
     * 
     * @param userId 用户ID
     * @param eventType 事件类型
     * @param action 操作
     * @param target 目标
     * @param result 结果
     * @param description 描述
     */
    void log(String userId, String eventType, String action, String target, String result, String description);

    /**
     * 查询审计日志
     * 
     * @param request 分页请求
     * @return 审计日志列表
     */
    PageResult<AuditLog> query(PageRequest request);

    /**
     * 查询审计日志（带过滤条件）
     * 
     * @param request 分页请求
     * @param filter 过滤条件
     * @return 审计日志列表
     */
    PageResult<AuditLog> query(PageRequest request, AuditLogFilter filter);

    /**
     * 获取审计日志详情
     * 
     * @param logId 日志ID
     * @return 审计日志
     */
    AuditLog getLog(String logId);

    /**
     * 导出审计日志
     * 
     * @param filter 过滤条件
     * @param format 格式（csv, json, excel）
     * @return 导出数据
     */
    byte[] export(AuditLogFilter filter, String format);

    /**
     * 清理过期日志
     * 
     * @param beforeTime 清理此时间之前的日志
     * @return 清理数量
     */
    int cleanExpiredLogs(long beforeTime);

    /**
     * 获取审计日志统计
     * 
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 统计信息
     */
    net.ooder.scene.audit.AuditStats getStats(long startTime, long endTime);
}
