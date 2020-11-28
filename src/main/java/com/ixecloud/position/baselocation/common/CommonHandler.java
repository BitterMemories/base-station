package com.ixecloud.position.baselocation.common;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommonHandler {
    static public String myCookie= "";

    //通过打开首页获取服务器分配的PHPSessionID
    public void getSessionIdByOpenHome(String getUrl) throws Exception {
        // TODO Auto-generated method stub
        String bodyTwo= "";
        String body= "";
        int i= 0;
        //try {
        // TODO Auto-generated method stub
        String line= "";
        //String getUrl= "http://localhost/Agileone/";
        //HttpURLConnection urlConnection= null;
        URL url= new URL(getUrl);
        //System.out.println(url);
        HttpURLConnection urlConnection= (HttpURLConnection)url.openConnection();
        //urlConnection= (HttpURLConnection)url.openConnection();
        //System.out.println(urlConnection);

        //连接相关参数设置
        urlConnection.setConnectTimeout(10000);
        urlConnection.setReadTimeout(10000);
        urlConnection.setUseCaches(false);
        urlConnection.setRequestMethod("GET");
        //urlConnection.set

        //设置请求的头部信息
        urlConnection.setRequestProperty("User-Agent", "Java-Client");
        urlConnection.setRequestProperty("Cookie", this.myCookie);

        //发送url，建立连接
        urlConnection.connect();
        //String myfield= urlConnection.getHeaderField("server");
        String myCookieHome= urlConnection.getHeaderField("Set-Cookie");
        //System.out.println(myfield);
        //System.out.println(myCookieHome);

        InputStream is= urlConnection.getInputStream();//获取返回页面的字节码
        InputStreamReader isr= new InputStreamReader(is, "utf-8");//将字节码转变成字节流
        BufferedReader br= new BufferedReader(isr);//从缓存获取字节流

        while((line=br.readLine())!=null) {
            body= body + line + "\n";
        }
        //System.out.println(body);
        i++;
        //cookie的处理

        Map<String, List<String>> myCookieLogin= urlConnection.getHeaderFields();
        //System.out.println(myCookieLogin+"健值");
        List<String> cookies= myCookieLogin.get("Set-Cookie");
        //System.out.println(cookies+"健值");
        //System.out.println(cookies);
        if(cookies != null) {
            for(int j=0; j<cookies.size(); j++) {
                //System.out.println(cookies.get(i));
                String[] temp= cookies.get(i).split(";");
                this.myCookie+= temp[0] + ";";
                //System.out.println(temp[0]);
            }
        }
        System.out.println(this.myCookie+"获取PHPSessionID的ID号");

		/*	if(cookies != null) {
				for(int j=0; j<cookies.size(); j++) {
					//System.out.println(cookies.get(i));
					//String[] temp= cookies.get(i).split(";");
					//this.myCookie+= temp[0] + ";";
					System.out.println(cookies.get(i));
					System.out.println(cookies.size());
				}
			}
		*/

        //	return body;
        //} catch (Exception e) {
        // TODO Auto-generated catch block
        //	e.printStackTrace();
        //}finally{
        //	if(i == 0) {
        //		return bodyTwo= "链接不存在";
        //	}else {
        //		return body;
        //	}

        //}

    }
    //通过登录首页获取服务器分配的PHPSessionID对应的用户信息健值对
    public void getSessionContentByloginHome(String postUrl, String postData) throws Exception {
        // TODO Auto-generated method stub
        // TODO Auto-generated method stub
        //String postUrl= "http://localhost/Agileone/index.php/common/login";
        //String postData= "username=admin&password=admin&savelogin=true";
        String line= "";
        String body= "";
        URL url= new URL(postUrl);
        HttpURLConnection urlConnection= (HttpURLConnection)url.openConnection();

        //连接相关参数设置
        urlConnection.setDoOutput(true);
        urlConnection.setConnectTimeout(10000);
        urlConnection.setReadTimeout(10000);
        urlConnection.setUseCaches(false);
        urlConnection.setRequestMethod("POST");

        //设置请求的头部信息
        urlConnection.setRequestProperty("User-Agent", "Java-Client");
        urlConnection.setRequestProperty("Cookie", this.myCookie);

        //正式建立连接
        urlConnection.connect();
        OutputStream os= urlConnection.getOutputStream();
        PrintWriter pw= new PrintWriter(os);
        pw.print(postData);
        pw.flush();

        //获取登录时的后面部分cookie
        Map<String, List<String>> myCookieLogin= urlConnection.getHeaderFields();
        //System.out.println(myCookieLogin+"健值");
        //System.out.println(myCookieLogin);
        List<String> cookies= myCookieLogin.get("Set-Cookie");
        //System.out.println(cookies);
        //System.out.println(cookies);
        if(cookies != null) {
            for(int i=0; i<cookies.size(); i++) {
                //System.out.println(cookies.get(i));
                String[] temp= cookies.get(i).split(";");
                //System.out.println(temp[0]+"后面");
                this.myCookie+= temp[0] + ";";
            }
        }else {
            System.out.println("cookies内容为空");
        }

        System.out.println(this.myCookie + "获取登录时的完整PHPSessionID");

        //InputStream is= urlConnection.getInputStream();
        //InputStreamReader isr= new InputStreamReader(is);
        //BufferedReader br= new BufferedReader(isr);
        //while((line=br.readLine())!=null) {
        //	body+= line + "\n";
        //}

        //System.out.println(body);
        //br.close();
        //isr.close();
        //is.close();
    }
    //获取页面中verify隐藏标签的取值
    public String getVerifyValueByRegular(String getUrl, String regex) {
        // TODO Auto-generated method stub
        String result= "";
        String responseByVerifyUrl= this.sendGet(getUrl);
        //String regex= "(.*value=\")(.+)(\" type.*)";
        //System.out.println(responseByVerifyUrl);//输出verify页面的信息

        Pattern p= Pattern.compile(regex);
        Matcher m= p.matcher(responseByVerifyUrl);
        if(m.find()) {
            result= m.group(2);
        }
        return result;
    }
    //获取页面的fid(保存在字符串数组中)
    public List<String> getFidValueByRegular(String getUrl, String regex) {
        //public List<String> getFidValueByRegular() {
        // TODO Auto-generated method stub
        //String getUrl= "http://localhost/upload/index.php";
        //String regex= "(.*<a href=\"thread.php\\?fid=)(.+?)(\".*)";
        //<a href="thread.php?fid=18">成都理工大学</a>
        String result= "";
        String responseByVerifyUrl= this.sendGet(getUrl);
        //String regex= "(.*value=\")(.+)(\" type.*)";
        //System.out.println(responseByVerifyUrl);//输出verify页面的信息

        Pattern p= Pattern.compile(regex);
        Matcher m= p.matcher(responseByVerifyUrl);
        //if(m.find()) {
        //	result= m.group(0);
        //}
        List<String> fidList= new ArrayList<String>();
        while(m.find()) {
            fidList.add(m.group(2));
        }
        for(int i=0; i<fidList.size(); i++) {
            //System.out.println(fidList.get(i));
        }

        return fidList;
    }
    public List<String> filterResponseToList(String responseTemp, String regex) {
        String result= "";
        String response= responseTemp;
        Pattern p= Pattern.compile(regex);
        Matcher m= p.matcher(responseTemp);
        List<String> fidList= new ArrayList<String>();
        while(m.find()) {
            fidList.add(m.group(2));
        }
        for(int i=0; i<fidList.size(); i++) {
            //System.out.println(fidList.get(i));
        }
        return fidList;
    }
    public String filterResponseToString(String response, String regex) {
        // TODO Auto-generated method stub
        String result= "";
        //String responseByVerifyUrl= this.sendGet(getUrl);
        //String regex= "(.*value=\")(.+)(\" type.*)";
        //System.out.println(responseByVerifyUrl);//输出verify页面的信息

        Pattern p= Pattern.compile(regex, Pattern.DOTALL);
        Matcher m= p.matcher(response);
        if(m.find()) {
            result= m.group(2);
        }
        return result;
    }
    //发送get请求
    public String  sendGet(String getUrl) {
        // TODO Auto-generated method stub
        String bodyTwo= "";
        String body= "";
        int i= 0;
        try {
            // TODO Auto-generated method stub
            String line= "";
            //String getUrl= "http://localhost/Agileone/";
            //HttpURLConnection urlConnection= null;
            URL url= new URL(getUrl);
            //System.out.println(url);
            HttpURLConnection urlConnection= (HttpURLConnection)url.openConnection();
            //urlConnection= (HttpURLConnection)url.openConnection();
            //System.out.println(urlConnection);

            //连接相关参数设置
            urlConnection.setConnectTimeout(10000);
            urlConnection.setReadTimeout(10000);
            urlConnection.setUseCaches(false);
            urlConnection.setRequestMethod("GET");
            //urlConnection.set

            //设置请求的头部信息
            urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:54.0) Gecko/20100101 Firefox/54.0");
            urlConnection.setRequestProperty("Cookie", this.myCookie);

            //发送url，建立连接
            urlConnection.connect();
            String myfield= urlConnection.getHeaderField("server");
            String myCookieHome= urlConnection.getHeaderField("Set-Cookie");
            //System.out.println(myfield);
            //System.out.println(myCookieHome);

            InputStream is= urlConnection.getInputStream();//获取返回页面的字节码
            InputStreamReader isr= new InputStreamReader(is, "utf-8");//将字节码转变成字节流
            BufferedReader br= new BufferedReader(isr);//从缓存获取字节流

            while((line=br.readLine())!=null) {
                body= body + line + "\n";
            }
            //System.out.println(body);
            i++;
            //cookie的处理
            //String[] myCookieHomeProcess= myCookieHome.split(";");
            //this.myCookie= myCookieHomeProcess[0] + ";";
            //System.out.println(this.myCookie + "获取首页的PHPSessionId");
            return body;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }finally{
            if(i == 0) {
                return bodyTwo= "链接不存在";
            }else {
                return body;
            }
        }
    }

    //发送post请求
    public String sendPost(String postUrl, String postData) throws Exception {
        // TODO Auto-generated method stub
        //String postUrl= "http://localhost/Agileone/index.php/common/login";
        //String postData= "username=admin&password=admin&savelogin=true";
        String line= "";
        String body= "";
        URL url= new URL(postUrl);
        HttpURLConnection urlConnection= (HttpURLConnection)url.openConnection();

        //连接相关参数设置
        urlConnection.setDoOutput(true);
        urlConnection.setConnectTimeout(10000);
        urlConnection.setReadTimeout(10000);
        urlConnection.setUseCaches(false);
        urlConnection.setRequestMethod("POST");

        //设置请求的头部信息
        urlConnection.setRequestProperty("User-Agent", "Java-Client");
        urlConnection.setRequestProperty("Cookie", this.myCookie);

        //正式建立连接
        urlConnection.connect();
        OutputStream os= urlConnection.getOutputStream();
        PrintWriter pw= new PrintWriter(os);
        pw.print(postData);
        pw.flush();

        //获取登录时的后面部分cookie
        //Map<String, List<String>> myCookieLogin= urlConnection.getHeaderFields();
        //List<String> cookies= myCookieLogin.get("Set-Cookie");
        //System.out.println(cookies);
        //System.out.println(cookies);
        //if(cookies != null) {
        //	for(int i=0; i<cookies.size(); i++) {
        //		//System.out.println(cookies.get(i));
        //		String[] temp= cookies.get(i).split(";");
        //		this.myCookie+= temp[0] + ";";
        //	}
        //}

        //System.out.println(this.myCookie + "获取登录时的完整PHPSessionID");

        InputStream is= urlConnection.getInputStream();
        InputStreamReader isr= new InputStreamReader(is);
        BufferedReader br= new BufferedReader(isr);
        while((line=br.readLine())!=null) {
            body+= line + "\n";
        }

        //System.out.println(body);
        br.close();
        isr.close();
        is.close();
        return body;
    }

    //通过页面信息找到页面中的学校机构\

    //获取一个超链接的响应码
    public String  getResponseCodeByUrlLink(String getUrl) {
        // TODO Auto-generated method stub
        String responseCode= "";
        String bodyTwo= "";
        String body= "";
        int i= 0;
        try {
            // TODO Auto-generated method stub
            String line= "";
            //String getUrl= "http://localhost/Agileone/";
            //HttpURLConnection urlConnection= null;
            URL url= new URL(getUrl);
            //System.out.println(url);
            HttpURLConnection urlConnection= (HttpURLConnection)url.openConnection();
            //urlConnection= (HttpURLConnection)url.openConnection();
            //System.out.println(urlConnection);

            //连接相关参数设置
            urlConnection.setConnectTimeout(10000);
            urlConnection.setReadTimeout(10000);
            urlConnection.setUseCaches(false);
            urlConnection.setRequestMethod("GET");
            //urlConnection.set

            //设置请求的头部信息
            urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:54.0) Gecko/20100101 Firefox/54.0");
            urlConnection.setRequestProperty("Cookie", this.myCookie);

            //发送url，建立连接
            urlConnection.connect();
            String myfield= urlConnection.getHeaderField("server");
            String myCookieHome= urlConnection.getHeaderField("Set-Cookie");
            responseCode= urlConnection.getResponseCode() + "";
            //System.out.println(myfield);
            //System.out.println(myCookieHome);

            InputStream is= urlConnection.getInputStream();//获取返回页面的字节码
            InputStreamReader isr= new InputStreamReader(is, "utf-8");//将字节码转变成字节流
            BufferedReader br= new BufferedReader(isr);//从缓存获取字节流

            while((line=br.readLine())!=null) {
                body= body + line + "\n";
            }
            //System.out.println(body);
            i++;
            //cookie的处理
            //String[] myCookieHomeProcess= myCookieHome.split(";");
            //this.myCookie= myCookieHomeProcess[0] + ";";
            //System.out.println(this.myCookie + "获取首页的PHPSessionId");
            urlConnection.disconnect();

        } catch (Exception e) {
            //e.printStackTrace();
        }
        return responseCode;
    }
}
