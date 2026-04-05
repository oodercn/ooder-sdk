package net.ooder.bpm.client.ct;

import net.ooder.annotation.AttributeInterpretClass;
import net.ooder.annotation.Attributetype;
import net.ooder.bpm.client.Attribute;
import net.ooder.bpm.client.AttributeInst;

import java.util.List;

public class CtAttributeInst implements AttributeInst{

    private List<String> childrenIds;
    private String id;

    private String name;

    String value;

    private AttributeInterpretClass interpretClass;

    private Attributetype type;

    private String parentId;


    CtAttributeInst(AttributeInst attributeInst) {
        this.id = attributeInst.getId();
        this.name = attributeInst.getName();
        this.value = attributeInst.getValue();
        this.type = attributeInst.getType();
        this.parentId = attributeInst.getParentId();
        interpretClass = attributeInst.getInterpretClass();
        this.childrenIds = attributeInst.getChildrenIds();


    }


    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public void setId(String id) {

        this.id=id;
    }

    @Override
    public Attributetype getType() {
        return this.type;
    }

    @Override
    public AttributeInterpretClass getInterpretClass() {
        return this.interpretClass;
    }

    @Override
    public String getParentId() {
        return parentId;
    }

    @Override
    public void setParentId(String parentId) {

        this.parentId = parentId;
    }


    @Override
    public String getName() {
        return name;
    }



    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public Object getInterpretedValue() {
        return this.value;
    }

    @Override
    public String getValue() {
        return this.value;
    }

    @Override
    public void setValue(String value) {

        this.value=value;
    }


    @Override
    public Attribute getParent() {

        Attribute attribute  = CtBPMCacheManager.getInstance().getAttributeInst(this.getParentId());

        return attribute;
    }

    @Override
    public List<Attribute> getChildren() {

        String[] ids = this.getChildrenIds().toArray(new String[this.getChildrenIds().size()]);

        List attributes = CtBPMCacheManager.getInstance().getAttributeInsts(ids);

        return attributes;
    }

    @Override
    public List<String> getChildrenIds() {
        return this.childrenIds;
    }

    @Override
    public void setChildrenIds(List<String> ids) {

        this.childrenIds=ids;
    }

    @Override
    public Attribute getChild(String name) {
        List<Attribute> attributes = this.getChildren();
        for (Attribute attribute : attributes) {
            if (attribute.getName().equals(name)) {
                return attribute;
            }
        }
        return null;
    }
}
