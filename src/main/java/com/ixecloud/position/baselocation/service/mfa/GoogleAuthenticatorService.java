package com.ixecloud.position.baselocation.service.mfa;

public interface GoogleAuthenticatorService {

    String createMfaCredentials(String deviceId);

    boolean authoriseMfa(String deviceId, int validationCode);
}
