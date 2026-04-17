package net.ooder.skill.capability.controller;

import net.ooder.skill.capability.dto.SelectorItemDTO;
import net.ooder.skill.capability.model.ResultModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/v1/selectors")
@CrossOrigin(originPatterns = "*", allowCredentials = "true", allowedHeaders = "*")
public class SelectorController {

    private static final Logger log = LoggerFactory.getLogger(SelectorController.class);

    @GetMapping("/capability-types")
    public ResultModel<List<SelectorItemDTO>> getCapabilityTypes() {
        log.info("[SelectorController] Get capability types for selector");
        
        List<SelectorItemDTO> types = new ArrayList<>();
        
        SelectorItemDTO type1 = new SelectorItemDTO();
        type1.setId("skill");
        type1.setName("技能");
        types.add(type1);
        
        SelectorItemDTO type2 = new SelectorItemDTO();
        type2.setId("agent");
        type2.setName("Agent");
        types.add(type2);
        
        SelectorItemDTO type3 = new SelectorItemDTO();
        type3.setId("workflow");
        type3.setName("工作流");
        types.add(type3);
        
        SelectorItemDTO type4 = new SelectorItemDTO();
        type4.setId("integration");
        type4.setName("集成");
        types.add(type4);
        
        SelectorItemDTO type5 = new SelectorItemDTO();
        type5.setId("llm");
        type5.setName("大模型");
        types.add(type5);
        
        SelectorItemDTO type6 = new SelectorItemDTO();
        type6.setId("knowledge");
        type6.setName("知识库");
        types.add(type6);
        
        SelectorItemDTO type7 = new SelectorItemDTO();
        type7.setId("tool");
        type7.setName("工具");
        types.add(type7);
        
        return ResultModel.success(types);
    }

    @GetMapping("/capability-categories")
    public ResultModel<List<SelectorItemDTO>> getCapabilityCategories() {
        log.info("[SelectorController] Get capability categories for selector");
        
        List<SelectorItemDTO> categories = new ArrayList<>();
        
        String[][] categoryData = {
            {"sys", "系统管理", "#64748b"},
            {"biz", "业务场景", "#f59e0b"},
            {"llm", "大模型", "#6366f1"},
            {"msg", "消息通知", "#ef4444"},
            {"org", "组织管理", "#3b82f6"},
            {"vfs", "文件存储", "#8b5cf6"},
            {"knowledge", "知识库", "#10b981"},
            {"monitor", "监控", "#06b6d4"},
            {"payment", "支付", "#22c55e"},
            {"media", "媒体", "#ec4899"},
            {"util", "工具", "#78716c"}
        };
        
        for (String[] data : categoryData) {
            SelectorItemDTO cat = new SelectorItemDTO();
            cat.setId(data[0]);
            cat.setName(data[1]);
            cat.setColor(data[2]);
            categories.add(cat);
        }
        
        return ResultModel.success(categories);
    }

    @GetMapping("/skill-forms")
    public ResultModel<List<SelectorItemDTO>> getSkillForms() {
        log.info("[SelectorController] Get skill forms for selector");
        
        List<SelectorItemDTO> forms = new ArrayList<>();
        
        SelectorItemDTO form1 = new SelectorItemDTO();
        form1.setId("PROVIDER");
        form1.setName("服务提供者");
        form1.setIcon("ri-cpu-line");
        form1.setColor("#1890ff");
        forms.add(form1);
        
        SelectorItemDTO form2 = new SelectorItemDTO();
        form2.setId("DRIVER");
        form2.setName("驱动适配器");
        form2.setIcon("ri-steering-line");
        form2.setColor("#52c41a");
        forms.add(form2);
        
        SelectorItemDTO form3 = new SelectorItemDTO();
        form3.setId("SCENE");
        form3.setName("场景应用");
        form3.setIcon("ri-layout-grid-line");
        form3.setColor("#722ed1");
        forms.add(form3);
        
        return ResultModel.success(forms);
    }
}
