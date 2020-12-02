package com.ixecloud.position.baselocation.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class FormatDateTime {

    public static String getCurrentTimeForCN() {
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));  // 设置北京时区
        return simpleDateFormat.format(new Date());
    }
}
