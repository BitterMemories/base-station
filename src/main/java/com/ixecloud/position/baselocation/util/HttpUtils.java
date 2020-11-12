package com.ixecloud.position.baselocation.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

@Component
public class HttpUtils {

    private static final Logger logger = LoggerFactory.getLogger(HttpUtils.class);

    @Autowired
    private RestTemplate restTemplate;

    public <T>T mifiPost(String url, String body, Class<T> clazz){
        logger.debug("requestURL:{}", url);
        String sign = HmacUtil.toHexString(HmacUtil.hmacSHA512(body.getBytes(), "8385d4b8014511ea950400163e049e4e88217aeeabd44fe1b510e57286756cfe")).toUpperCase();
        HttpHeaders headers = new HttpHeaders();
        headers.add("uid", "8385a8da-0145-11ea-9504-00163e049e4e");
        headers.add("sign", sign);
        MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
        headers.setContentType(type);
        //将请求头部和参数合成一个请求
        logger.debug("requestBody:{}", body);
        HttpEntity<String> requestEntity = new HttpEntity<>(body, headers);
        //执行HTTP请求，将返回的结构使用ResultVO类格式化
        T t = restTemplate.postForObject(url, requestEntity, clazz);
        logger.debug("response body:{}", t);
        return t;
    }
    /**
     * post 请求封装
     * @param value
     * @param URL
     * @return
     */

    public String post(final Object value, final String URL) {
        logger.debug("URL:{}", URL);
        if (!StringUtils.isNotBlank(URL)) {
            return null;

        }
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
        String requestJson = null;
        try {
            requestJson = objectMapper.writeValueAsString(value);

        } catch (Exception e) {

            e.printStackTrace();

        }

        logger.debug("requestJson:{}", requestJson);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        HttpEntity<String> entity = new HttpEntity<String>(requestJson, headers);
        SimpleClientHttpRequestFactory requestFactory = new  SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(180000);// 设置超时
        requestFactory.setReadTimeout(180000);
        RestTemplate restTemplate = new RestTemplate(requestFactory);
        ResponseEntity<String> response = restTemplate.exchange(URL, HttpMethod.POST, entity, String.class);

        String body = response.getBody();
        logger.debug("response body:{}", body);
        return body;
    }


    public String get(String url){
        logger.debug("requestURL:{}", url);
        String resultJson = restTemplate.getForObject(url, String.class);
        logger.debug("response body: {}", resultJson);
        return resultJson;
    }

    public String doGet(String url) {
        try {
            HttpClient client = new DefaultHttpClient();
            //发送get请求
            HttpGet request = new HttpGet(url);
            HttpResponse response = client.execute(request);

            /**请求发送成功，并得到响应**/
            if (response.getStatusLine().getStatusCode() == 200) {
                /**读取服务器返回过来的json字符串数据**/
                String strResult = EntityUtils.toString(response.getEntity());

                return strResult;
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

}
