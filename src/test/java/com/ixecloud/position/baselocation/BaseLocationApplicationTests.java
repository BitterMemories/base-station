package com.ixecloud.position.baselocation;

import com.ixecloud.position.baselocation.common.CommonHandler;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import java.util.*;

@SpringBootTest
class BaseLocationApplicationTests {

    @Test
    void contextLoads() {

        driverGetLinks();
    }

    @Test
    void test(){
        List<Integer> mylist = Arrays.asList(1,2,3);
        //Map<Integer, Integer> mymap = new HashMap<Integer, Integer>();
        List<String> list = new ArrayList<String>();
        for(int i=0;i< mylist.size()-1;i++)
        {
            for(int j=i+1;j< mylist.size();j++)
            {
                list.add(mylist.get(i)+":"+mylist.get(j));
            }
        }
        for(int k = 0; k< list.size(); k++)
        {
            System.out.println(list.get(k));
        }
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
