package net.ooder.annotation.ui;

public enum UIPanelType implements UIType {
    Audio("ood.UI.Dialog"),
    Block("ood.UI.Block"),
    Panel("ood.UI.Panel"),
    Module("ood.UI.Module"),
    TreeView("ood.UI.TreeView");

//
//    ButtonViews,CheckBox,ColorPicker,ComboInput,DatePicker,Dialog,Echarts,
//    Flash,FoldingList,FoldingTabs,FormLayout,FusionChartsXT,Gallery,Group,HiddenInput,
//    Image,Input,Label,Layout,List,MeuBar,Panel,PopMenu,ProgressBar,RadioBox,Resizer,
//    RichEditor,Slider,Stacks,StatusButtons,SVGPaper,Tabs,TimePicker,ToolBar,TreeBar,TreeGrid,
//    TreeView,UI,Video,APICall,MQTT,Module,DataBinder;

    private String type;

    UIPanelType(String type){
        this.type=type;
    }

    @Override
    public String toString() {
        return type;
    }

    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public static UIPanelType fromType(String typeName) {
        for (UIPanelType type : UIPanelType.values()) {
            if (type.getType().equals(typeName)) {
                return type;
            }
        }

        return null;
    }

}
