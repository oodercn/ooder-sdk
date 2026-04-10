package net.ooder.annotation.ui;

public enum UIFormType implements UIType {
    CheckBox("ood.UI.CheckBox"),
    ComboInput("ood.UI.ComboInput"),
    HiddenInput("ood.UI.HiddenInput"),
    Input("ood.UI.Input"),
    RadioBox("ood.UI.RadioBox"),
    RichEditor("ood.UI.RichEditor"),
    ColorPicker("ood.UI.ColorPicker"),
    DatePicker("ood.UI.DatePicker"),
    FormLayout("ood.UI.FormLayout"),

    Slider("ood.UI.Slider"),
    StatusButtons("ood.UI.StatusButtons"),
    TimePicker("ood.UI.TimePicker"),
    UI("ood.UI.UI"),


    ProgressBar("ood.UI.ProgressBar");

//v
//    ButtonViews,CheckBox,ColorPicker,ComboInput,DatePicker,Dialog,Echarts,
//    Flash,FoldingList,FoldingTabs,FormLayout,FusionChartsXT,Gallery,Group,HiddenInput,
//    Image,Input,Label,Layout,List,MeuBar,Panel,PopMenu,ProgressBar,RadioBox,Resizer,
//    RichEditor,Slider,Stacks,StatusButtons,SVGPaper,Tabs,TimePicker,ToolBar,TreeBar,TreeGrid,
//    TreeView,UI,Video,APICall,MQTT,Module,DataBinder;

    private String type;

    UIFormType(String type){
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
    public static UIFormType fromType(String typeName) {
        for (UIFormType type : UIFormType.values()) {
            if (type.getType().equals(typeName)) {
                return type;
            }
        }

        return null;
    }

}
