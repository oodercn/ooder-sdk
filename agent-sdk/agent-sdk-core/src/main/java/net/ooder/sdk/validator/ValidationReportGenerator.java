package net.ooder.sdk.validator;

public interface ValidationReportGenerator {
    
    String generateYaml(ValidationResult result);
    
    String generateJson(ValidationResult result);
    
    String generateHtml(ValidationResult result);
    
    String generateMarkdown(ValidationResult result);
}