package com.ixecloud.position.baselocation.service.mfa.impl;

import com.ixecloud.position.baselocation.controller.DeviceController;
import com.ixecloud.position.baselocation.domain.AuthenticatorMfa;
import com.ixecloud.position.baselocation.repository.AuthenticatorMfaRepository;
import com.ixecloud.position.baselocation.service.mfa.GoogleAuthenticatorService;
import com.ixecloud.position.baselocation.util.googleauth.*;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class GoogleAuthenticatorServiceImpl implements GoogleAuthenticatorService {

    private static final Logger logger = LoggerFactory.getLogger(GoogleAuthenticatorServiceImpl.class);

    @Autowired
    private AuthenticatorMfaRepository authenticatorMfaRepository;

    @Override
    public String createMfaCredentials(String deviceId) {
        GoogleAuthenticator googleAuthenticator = new GoogleAuthenticator();
        googleAuthenticator.setCredentialRepository(new ICredentialRepository() {
            @Override
            public String getSecretKey(String deviceId) {
                return deviceId;
            }

            @Override
            public void saveUserCredentials(String deviceId, String secretKey, int validationCode, List<Integer> scratchCodes) {

            }
        });

        final GoogleAuthenticatorKey key =
                googleAuthenticator.createCredentials(deviceId);
        final String secret = key.getKey();

        AuthenticatorMfa authenticatorMfa = new AuthenticatorMfa();
        authenticatorMfa.setMfaCredentials(secret);
        authenticatorMfa.setDeviceId(deviceId);
        authenticatorMfaRepository.save(authenticatorMfa);
        String otpAuthURL = GoogleAuthenticatorQRGenerator.getOtpAuthURL("混合基站定位", deviceId, key);
        logger.debug("Please register (otpauth uri): " + otpAuthURL);
        logger.debug(("Secret key is " + secret));
        return otpAuthURL;
    }

    @Override
    public boolean authoriseMfa(String deviceId, int validationCode) {
        AuthenticatorMfa authenticatorMfa = authenticatorMfaRepository.findAuthenticatorMfaByDeviceId(deviceId);
        if(ObjectUtils.isEmpty(authenticatorMfa)){
            return false;
        }
        GoogleAuthenticatorConfig.GoogleAuthenticatorConfigBuilder gacb =
                new GoogleAuthenticatorConfig.GoogleAuthenticatorConfigBuilder()
                        .setTimeStepSizeInMillis(TimeUnit.SECONDS.toMillis(30))
                        .setWindowSize(5)
                        .setCodeDigits(6);
        GoogleAuthenticator ga = new GoogleAuthenticator(gacb.build());
        ga.setCredentialRepository(new ICredentialRepository() {
            @Override
            public String getSecretKey(String deviceId) {
                return authenticatorMfa.getMfaCredentials();
            }
            @Override
            public void saveUserCredentials(String userEmail, String secretKey, int validationCode, List<Integer> scratchCodes) {

            }
        });
        return ga.authorizeUser(deviceId, validationCode);
    }
}
