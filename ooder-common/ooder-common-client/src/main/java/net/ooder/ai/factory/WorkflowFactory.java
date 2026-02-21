/**
 * $RCSfile: WorkflowFactory.java,v $
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
package net.ooder.ai.factory;

import net.ooder.ai.bean.WorkflowBean;
import net.ooder.annotation.WorkflowType;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 工作流工厂类，采用单例模式管理工作流实例
 */
public class WorkflowFactory {
    private static volatile WorkflowFactory instance;
    private final Map<String, WorkflowBean> workflowCache;

    private WorkflowFactory() {
        workflowCache = new ConcurrentHashMap<>();
        initDefaultWorkflows();
    }

    /**
     * 获取工厂单例实例
     */
    public static WorkflowFactory getInstance() {
        if (instance == null) {
            synchronized (WorkflowFactory.class) {
                if (instance == null) {
                    instance = new WorkflowFactory();
                }
            }
        }
        return instance;
    }

    /**
     * 初始化默认工作流
     */
    private void initDefaultWorkflows() {
        // 注册系统默认工作流
        registerDefaultLockDiagnosisWorkflow();
    }

    /**
     * 注册智能门锁诊断默认工作流
     */
    private void registerDefaultLockDiagnosisWorkflow() {
        WorkflowBean workflow = new WorkflowBean();
        workflow.setId("lock-diagnosis-flow");
        workflow.setName("智能门锁诊断工作流");
        workflow.setType(WorkflowType.STATE_MACHINE);
        workflow.setTimeout(450000);
        workflow.setRetryCount(2);
        workflowCache.put(workflow.getId(), workflow);
    }

    /**
     * 注册工作流
     */
    public void registerWorkflow(WorkflowBean workflow) {
        if (workflow != null && workflow.getId() != null) {
            workflowCache.put(workflow.getId(), workflow);
        }
    }

    /**
     * 获取工作流
     */
    public WorkflowBean getWorkflow(String workflowId) {
        return workflowCache.get(workflowId);
    }

    /**
     * 获取工作流类型
     */
    public WorkflowType getWorkflowType(String workflowId) {
        WorkflowBean workflow = workflowCache.get(workflowId);
        return workflow != null ? workflow.getType() : null;
    }

    /**
     * 检查工作流是否存在
     */
    public boolean exists(String workflowId) {
        return workflowCache.containsKey(workflowId);
    }
}