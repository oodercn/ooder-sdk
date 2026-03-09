package net.ooder.scene.skill.share;

/**
 * 分享验证结果
 *
 * @author ooder
 * @since 2.3
 */
public class ShareValidationResult {
    
    private boolean valid;
    private String errorMessage;
    private ShareInfo shareInfo;
    
    public ShareValidationResult() {
    }
    
    public static ShareValidationResult success(ShareInfo shareInfo) {
        ShareValidationResult result = new ShareValidationResult();
        result.setValid(true);
        result.setShareInfo(shareInfo);
        return result;
    }
    
    public static ShareValidationResult failure(String errorMessage) {
        ShareValidationResult result = new ShareValidationResult();
        result.setValid(false);
        result.setErrorMessage(errorMessage);
        return result;
    }
    
    public boolean isValid() {
        return valid;
    }
    
    public void setValid(boolean valid) {
        this.valid = valid;
    }
    
    public String getErrorMessage() {
        return errorMessage;
    }
    
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
    
    public ShareInfo getShareInfo() {
        return shareInfo;
    }
    
    public void setShareInfo(ShareInfo shareInfo) {
        this.shareInfo = shareInfo;
    }
}
