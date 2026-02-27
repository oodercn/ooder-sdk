package net.ooder.sdk.llm.security;

import net.ooder.sdk.llm.security.model.AuthRequest;
import net.ooder.sdk.llm.security.model.AuthResult;
import net.ooder.sdk.llm.security.model.AuditInfo;
import net.ooder.sdk.llm.security.model.AuthorizeRequest;
import net.ooder.sdk.llm.security.model.AuthorizeResult;
import net.ooder.sdk.llm.security.model.EncryptedData;
import net.ooder.sdk.llm.security.model.PlainData;

public interface SecurityApi {
    
    AuthResult authenticate(AuthRequest request);
    
    AuthorizeResult authorize(AuthorizeRequest request);
    
    String auditLog(AuditInfo info);
    
    EncryptedData encryptData(PlainData data);
    
    PlainData decryptData(EncryptedData data);
}
