package com.ixecloud.position.baselocation.controller;

import com.ixecloud.position.baselocation.common.Response;
import com.ixecloud.position.baselocation.enums.ResponseCode;
import com.ixecloud.position.baselocation.pojo.BaseEntity;
import com.ixecloud.position.baselocation.service.mfa.GoogleAuthenticatorService;
import com.ixecloud.position.baselocation.util.googleauth.QRCodeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@RequestMapping(value = "authenticator")
public class AuthenticatorController {

    @Autowired
    private GoogleAuthenticatorService googleAuthenticatorService;

    @GetMapping(value = "/create-qr-code")
    public void generateQrCode(String data,
                               @RequestParam(defaultValue = "300", required = false) int width,
                               @RequestParam(defaultValue = "300", required = false) int height,
                               HttpServletResponse response) {
        ServletOutputStream outputStream = null;
        try {
            outputStream = response.getOutputStream();
            QRCodeUtil.writeToStream(data, outputStream, width, height);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    @PostMapping(value = "bind-mfa", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Response bindMfa(@RequestBody BaseEntity baseEntity){
        //绑定MFA
        Object otpAuthURL = googleAuthenticatorService.createMfaCredentials(baseEntity.getDeviceId());

        return new Response(ResponseCode.OK, otpAuthURL);
    }


    @GetMapping(value = "verify-qr-code")
    public Response verifyQrCode(@RequestParam(value = "code") Integer code, @RequestParam(value = "deviceId") String deviceId){
        boolean isCodeValid = googleAuthenticatorService.authoriseMfa(deviceId,code);
        if(true){
            return new Response(ResponseCode.OK);
        }else {
            return new Response(ResponseCode.UNAUTHORIZED, "verification failure!");
        }
    }

}
