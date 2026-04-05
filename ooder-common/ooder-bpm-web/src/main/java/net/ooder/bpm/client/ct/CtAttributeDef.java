package net.ooder.bpm.client.ct;

import net.ooder.annotation.AttributeInterpretClass;
import net.ooder.annotation.Attributetype;
import net.ooder.bpm.client.Attribute;
import net.ooder.bpm.client.AttributeDef;
import net.ooder.common.CommonYesNoEnum;

import java.util.List;

public class CtAttributeDef implements AttributeDef {
    private List<String> childrenIds;

    private  List<Attribute> children;

    private  String value;


    private String propertyId;

    private String propname;

    private String propvalue;

    private AttributeInterpretClass propclass;

    private Attributetype proptype;

    private String parentpropId;

    private Integer isextension;

    private CommonYesNoEnum caninstantiate;



    CtAttributeDef(AttributeDef attribute){


        this.propname=attribute.getName();
        this.value=attribute.getValue();
        this.children=attribute.getChildren();
        this.propertyId=attribute.getId();
        this.propclass=attribute.getInterpretClass();
        this.propvalue=attribute.getValue();
        this.childrenIds=attribute.getChildrenIds();
        this.parentpropId=attribute.getParentId();
        this.proptype=attribute.getType();
        this.isextension=attribute.getIsExtension();
        this.caninstantiate=attribute.getCanInstantiate();

    }


    @Override
    public String getId() {
        return propertyId;
    }

    @Override
    public void setId(String id) {
        this.propertyId=id;
    }

    @Override
    public AttributeInterpretClass getInterpretClass() {
        return propclass;
    }

    @Override
    public Attributetype getType() {
        return proptype;
    }

    @Override
    public String getParentId() {
        return parentpropId;
    }

    @Override
    public void setParentId(String parentId) {
           this.parentpropId=parentId;
    }

    @Override
    public Integer getIsExtension() {
        return isextension;
    }

    @Override
    public CommonYesNoEnum getCanInstantiate() {
        return caninstantiate;
    }

    @Override
    public String getName() {
        return propname;
    }

    @Override
    public void setName(String name) {

        this.propname=name;
    }

    @Override
    public Object getInterpretedValue() {
        return propvalue;
    }

    @Override
    public String getValue() {
        return propvalue;
    }

    @Override
    public void setValue(String value) {
        this.propvalue=value;

    }

    @Override
    public Attribute getParent() {

        Attribute attribute=CtBPMCacheManager.getInstance().getAttributeDefById(this.getParentId());

        return attribute;
    }

    @Override
    public List<Attribute> getChildren() {

        String[] ids=this.getChildrenIds().toArray(new String[this.getChildrenIds().size()]);

        List  attributes=  CtBPMCacheManager.getInstance().getAttributeDefs(ids);

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
        List<Attribute>  attributes=this.getChildren();
        for(Attribute attribute:attributes){
            if (attribute.getName().equals(name)){
                return attribute;
            }
        }
        return null;
    }

}
