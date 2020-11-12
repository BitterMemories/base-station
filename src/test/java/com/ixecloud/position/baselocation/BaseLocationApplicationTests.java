package com.ixecloud.position.baselocation;

import com.ixecloud.position.baselocation.domain.BaseLocation;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@SpringBootTest
class BaseLocationApplicationTests {

    @Test
    void contextLoads() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time = "1WEEK 1HOUR 14MINUTE 25SECOND";
        String[] uptimes = time.split(" ");
        int length = uptimes.length;
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        System.out.println(sdf.format(cal.getTime()));
        if(length == 1){
            String strings = uptimes[0];
            int second = Integer.parseInt(strings.replace("SECOND", ""));
            cal.add(Calendar.SECOND, -second);
        }else if(length == 2){
            String strings = uptimes[0];
            int minute = Integer.parseInt(strings.replace("MINUTE", ""));
            cal.add(Calendar.MINUTE, -minute);

            strings = uptimes[1];
            int second = Integer.parseInt(strings.replace("SECOND", ""));
            cal.add(Calendar.SECOND, -second);
        }else if(length == 3){
            String strings = uptimes[0];
            int hour = Integer.parseInt(strings.replace("HOUR", ""));
            cal.add(Calendar.HOUR, -hour);

            strings = uptimes[1];
            int minute = Integer.parseInt(strings.replace("MINUTE", ""));
            cal.add(Calendar.MINUTE, -minute);

            strings = uptimes[2];
            int second = Integer.parseInt(strings.replace("SECOND", ""));
            cal.add(Calendar.SECOND, -second);
        }else if(length == 4){
            String strings = uptimes[0];
            int week = Integer.parseInt(strings.replace("WEEK", ""));
            cal.add(Calendar.WEEK_OF_MONTH, -week);

            strings = uptimes[1];
            int hour = Integer.parseInt(strings.replace("HOUR", ""));
            cal.add(Calendar.HOUR, -hour);

            strings = uptimes[2];
            int minute = Integer.parseInt(strings.replace("MINUTE", ""));
            cal.add(Calendar.MINUTE, -minute);

            strings = uptimes[3];
            int second = Integer.parseInt(strings.replace("SECOND", ""));
            cal.add(Calendar.SECOND, -second);
        }
        String format = sdf.format(cal.getTime());
        System.out.println(format);
    }

}
