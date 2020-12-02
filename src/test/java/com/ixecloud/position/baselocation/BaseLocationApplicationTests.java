package com.ixecloud.position.baselocation;

import com.ixecloud.position.baselocation.common.CommonHandler;
import com.ixecloud.position.baselocation.util.FormatDateTime;
import com.ixecloud.position.baselocation.util.HttpUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.*;

@SpringBootTest
class BaseLocationApplicationTests {

    @Autowired
    private HttpUtils httpUtils;

    @Test
    void contextLoads() {

        driverGetLinks();
    }

    @Test
    void test(){
        String string = UUID.randomUUID().toString();
        System.out.println(string);
    }

    private void driverGetLinks() {
        CommonHandler ch = new CommonHandler();

        // TODO Auto-generated method stub
        String getUrl = "https://www.cnblogs.com/kerrycode/p/7423850.html";
        // String responseByGetUrl= ch.sendGet(getUrl);
        // <a href="http://www.woniuxy.com/train/java.html#contact"
        // target="_blank">联系我们</a>
        String regex = "(<a href=\")(.*?)(\")";
        // System.out.println(responseByGetUrl);
        List<String> linkList = ch.getFidValueByRegular(getUrl, regex);
        List<String> newLinkList = new ArrayList<String>();
        // 对返回的链接数组进行处理
        for (int i = 0; i < linkList.size(); i++) {
            String tempCode = "";
            // System.out.println(linkList.get(i));
            String tempLink = linkList.get(i);
            if (tempLink.startsWith("javascript")) {
                System.out.println("__这不是有效的链接" + "____" + tempLink);
            } else if (tempLink.startsWith("http://")) {
                // System.out.println(tempLink);
                // tempCode= ch.getResponseCodeByUrlLink(tempLink);
                // System.out.println(tempCode);
                newLinkList.add(linkList.get(i));
            } else {
                //tempLink = "http://www.woniuxy.com" + tempLink;
                // System.out.println(tempLink);
                // tempCode= ch.getResponseCodeByUrlLink(tempLink);
                // System.out.println(tempCode);
                newLinkList.add(tempLink);
            }
        }

        for (int i = 0; i < newLinkList.size(); i++) {
            // System.out.println(newLinkList.get(i));
            String tempLink = newLinkList.get(i);
            String tempCode = "";
            tempCode = ch.getResponseCodeByUrlLink(tempLink);
            if (tempCode.contains("200")) {
                System.out.println(tempCode + "__正常状态码" + "____" + tempLink);
            } else {
                System.out.println(tempCode + "__不正常状态码" + "____" + tempLink);
            }

        }

    }

}
