package com.ixecloud.position.baselocation.controller;

import com.ixecloud.position.baselocation.common.Response;
import com.ixecloud.position.baselocation.enums.ResponseCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.UnsupportedEncodingException;


@RestController
@RequestMapping(value = "base-station")
public class BaseStationController {

    private static final Logger logger = LoggerFactory.getLogger(DeviceController.class);

    @PostMapping(value = "notice")
    public Response notice(@RequestBody String requestJson) throws UnsupportedEncodingException {

        logger.debug("#################################################################################");
        logger.debug(java.net.URLDecoder.decode(requestJson, "UTF-8"));

        return new Response(ResponseCode.OK);

    }

}
