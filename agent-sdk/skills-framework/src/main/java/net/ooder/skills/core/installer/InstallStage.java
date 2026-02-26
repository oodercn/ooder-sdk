
package net.ooder.skills.core.installer;

public interface InstallStage {
    
    String getName();
    
    void execute(InstallContext context) throws Exception;
}
