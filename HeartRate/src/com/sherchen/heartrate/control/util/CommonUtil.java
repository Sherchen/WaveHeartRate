package com.sherchen.heartrate.control.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * The description of use:
 * <br />
 * Created time:2014/6/18 14:22
 * Created by Dave
 */
public class CommonUtil {

    private static final String DATE_TIME_FORMAT  = "yy-M-d a H:m";

    public static String getReadableDateTime(long time){
        //todo if it take bad performance or take much more memory,then the android.text.format.DateFormat will be thought.
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_TIME_FORMAT);
        return sdf.format(new Date(time));
    }
}
