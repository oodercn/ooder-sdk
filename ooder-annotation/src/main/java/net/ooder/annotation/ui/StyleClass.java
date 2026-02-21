package net.ooder.annotation.ui;


import net.ooder.annotation.Enumstype;

public enum StyleClass implements Enumstype {

    mobile("ood-uisyle-mobile"),
    touch("ood-cursor-touch"),
    circle("ood-uiborder-circle"),
    uigradient("ood-uigradient"),
    shadow("ood-ui-shadow")
    ,noscroll("ood-css-noscroll");


    private final String name;





    StyleClass(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public String getType() {
        return name();
    }

    @Override
    public String getName() {
        return name;
    }

}
