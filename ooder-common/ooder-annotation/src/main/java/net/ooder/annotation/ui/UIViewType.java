package net.ooder.annotation.ui;

public enum UIViewType implements UIType {
    Layout("ood.UI.Layout"),
    FoldingList("ood.UI.FoldingList"),
    FormLayout("ood.UI.FormLayout"),
    FusionChartsXT("ood.UI.FusionChartsXT"),
    ComboInput("ood.UI.ComboInput"),
    Gallery("ood.UI.Gallery"),
    Stacks("ood.UI.Stacks"),
    SVGPaper("ood.UI.SVGPaper"),
    Tabs("ood.UI.Tabs");

    private String type;

    UIViewType(String type){
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
    public static UIViewType fromType(String typeName) {
        for (UIViewType type : UIViewType.values()) {
            if (type.getType().equals(typeName)) {
                return type;
            }
        }

        return Layout;
    }

}
