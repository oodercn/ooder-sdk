package net.ooder.spi.facade;

import net.ooder.spi.im.ImService;
import net.ooder.spi.rag.RagEnhanceDriver;
import net.ooder.spi.workflow.WorkflowDriver;
import net.ooder.spi.knowledge.KnowledgeService;
import net.ooder.spi.dict.DictService;
import net.ooder.spi.classifier.KnowledgeClassifier;
import net.ooder.spi.llm.LlmProvider;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

/**
 * SPI 服务门面
 * 提供统一的服务注册和发现机制
 */
public class SpiServices {

    private static SpiServices instance;

    // 核心服务
    private ImService imService;
    private RagEnhanceDriver ragEnhanceDriver;
    private WorkflowDriver workflowDriver;

    // 知识库相关服务
    private KnowledgeService knowledgeService;
    private DictService dictService;
    private KnowledgeClassifier knowledgeClassifier;

    // LLM 服务
    private LlmProvider llmProvider;

    // 多实现支持
    private final Map<String, Object> serviceRegistry = new ConcurrentHashMap<>();

    public static synchronized void init(SpiServices services) {
        instance = services;
    }

    public static SpiServices getInstance() {
        return instance;
    }

    // ========== 核心服务 ==========

    public static ImService getImService() {
        return instance != null ? instance.imService : null;
    }

    public static RagEnhanceDriver getRagEnhanceDriver() {
        return instance != null ? instance.ragEnhanceDriver : null;
    }

    public static WorkflowDriver getWorkflowDriver() {
        return instance != null ? instance.workflowDriver : null;
    }

    // ========== 知识库服务 ==========

    public static KnowledgeService getKnowledgeService() {
        return instance != null ? instance.knowledgeService : null;
    }

    public static DictService getDictService() {
        return instance != null ? instance.dictService : null;
    }

    public static KnowledgeClassifier getKnowledgeClassifier() {
        return instance != null ? instance.knowledgeClassifier : null;
    }

    // ========== LLM 服务 ==========

    public static LlmProvider getLlmProvider() {
        return instance != null ? instance.llmProvider : null;
    }

    // ========== Optional 包装 ==========

    public static Optional<ImService> im() { return Optional.ofNullable(getImService()); }
    public static Optional<RagEnhanceDriver> rag() { return Optional.ofNullable(getRagEnhanceDriver()); }
    public static Optional<WorkflowDriver> workflow() { return Optional.ofNullable(getWorkflowDriver()); }
    public static Optional<KnowledgeService> knowledge() { return Optional.ofNullable(getKnowledgeService()); }
    public static Optional<DictService> dict() { return Optional.ofNullable(getDictService()); }
    public static Optional<KnowledgeClassifier> classifier() { return Optional.ofNullable(getKnowledgeClassifier()); }
    public static Optional<LlmProvider> llm() { return Optional.ofNullable(getLlmProvider()); }

    // ========== 服务注册 ==========

    public void setImService(ImService imService) { this.imService = imService; }
    public void setRagEnhanceDriver(RagEnhanceDriver ragEnhanceDriver) { this.ragEnhanceDriver = ragEnhanceDriver; }
    public void setWorkflowDriver(WorkflowDriver workflowDriver) { this.workflowDriver = workflowDriver; }
    public void setKnowledgeService(KnowledgeService knowledgeService) { this.knowledgeService = knowledgeService; }
    public void setDictService(DictService dictService) { this.dictService = dictService; }
    public void setKnowledgeClassifier(KnowledgeClassifier knowledgeClassifier) { this.knowledgeClassifier = knowledgeClassifier; }
    public void setLlmProvider(LlmProvider llmProvider) { this.llmProvider = llmProvider; }

    // ========== 通用注册/发现 ==========

    /**
     * 注册服务
     */
    public <T> void register(Class<T> serviceType, T implementation) {
        serviceRegistry.put(serviceType.getName(), implementation);
    }

    /**
     * 发现服务
     */
    @SuppressWarnings("unchecked")
    public <T> Optional<T> discover(Class<T> serviceType) {
        T service = (T) serviceRegistry.get(serviceType.getName());
        if (service == null) {
            // 尝试从字段获取
            if (serviceType == KnowledgeService.class) {
                service = (T) getKnowledgeService();
            } else if (serviceType == DictService.class) {
                service = (T) getDictService();
            } else if (serviceType == KnowledgeClassifier.class) {
                service = (T) getKnowledgeClassifier();
            } else if (serviceType == LlmProvider.class) {
                service = (T) getLlmProvider();
            } else if (serviceType == ImService.class) {
                service = (T) getImService();
            } else if (serviceType == RagEnhanceDriver.class) {
                service = (T) getRagEnhanceDriver();
            } else if (serviceType == WorkflowDriver.class) {
                service = (T) getWorkflowDriver();
            }
        }
        return Optional.ofNullable(service);
    }

    /**
     * 注销服务
     */
    public <T> void unregister(Class<T> serviceType) {
        serviceRegistry.remove(serviceType.getName());
    }
}
