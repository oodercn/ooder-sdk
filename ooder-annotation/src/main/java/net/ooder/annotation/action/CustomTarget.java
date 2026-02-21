package net.ooder.annotation.action;

import net.ooder.annotation.Enumstype;

public enum CustomTarget implements Enumstype {
    CALLBACK("callback"),
    DYNCURRMODULENAME("@{CurrModule!=null?CurrModule.realClassName}"),
    DYNCURRTOPCOMPONENTNAME("@{(this.topModuleComponent!=null && this.topModuleComponent.topComponentBox!=null) ? this.topModuleComponent.topComponentBox.alias: (this.moduleComponent!=null && this.moduleComponent.topComponentBox!=null)?this.moduleComponent.topComponentBox.alias}"),
    PARENTMODULE("{page.parentModule}"),
    DYNCOMPONENTNAME("@{(this.moduleComponent!=null&&this.moduleComponent.navComponent!=null)?this.moduleComponent.navComponent.alias:(currComponent!=null?currComponent.alias)}"),
    TOPMODULE("@{TopModule.realClassName}"),
    DYNADDMODULENAME("@{(this.moduleComponent!=null&&this.moduleComponent.addModule!=null)?this.moduleComponent.addModule.realClassName:this.moduleComponent.editorModule}"),
    ADDMODULEDIO("@{(this.moduleComponent!=null&&this.moduleComponent.addModule!=null)?this.moduleComponent.addModule.component.dio:true}"),
    EDITERMODULEDIO("@{(this.moduleComponent!=null&&this.moduleComponent.editorModule!=null)?this.moduleComponent.editorModule.component.dio:true}"),
    MOREMODULEDIO("@{(this.moduleComponent!=null&&this.moduleComponent.moreModule!=null)?this.moduleComponent.moreModule.component.dio:true}"),
    DYNEDITORMODULETARGET("@{(this.moduleComponent!=null&&this.moduleComponent.editorModule!=null)?this.moduleComponent.editorModule.component.target:this.moduleComponent.target}"),
    DYNEDITORMODULENAME("@{(this.moduleComponent!=null&&this.moduleComponent.editorModule!=null)?this.moduleComponent.editorModule.realClassName:CurrModule.realClassName}");

    private final String name;

    CustomTarget(String name) {
        this.name = name;
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
