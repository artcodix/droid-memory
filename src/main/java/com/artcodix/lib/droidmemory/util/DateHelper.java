package com.artcodix.lib.droidmemory.util;

import android.content.Context;
import android.text.format.DateUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateHelper {
	
	public static String formatDateTime(Context context, String timeToFormat) {

	    String finalDateTime = "";          

	    SimpleDateFormat iso8601Format = new SimpleDateFormat(
	            "yyyy-MM-dd HH:mm:ss", context.getResources().getConfiguration().locale);

	    Date date = null;
	    if (timeToFormat != null) {
	        try {
	            date = iso8601Format.parse(timeToFormat);
	        } catch (ParseException e) {
	            date = null;
	        }

	        if (date != null) {
	            long when = date.getTime();
	            String dat = iso8601Format.format(new Date(when));
	            int flags = 0;
	            flags |= DateUtils.FORMAT_SHOW_TIME;
	            flags |= DateUtils.FORMAT_SHOW_DATE;
	            flags |= DateUtils.FORMAT_ABBREV_MONTH;
	            flags |= DateUtils.FORMAT_SHOW_YEAR;

	            finalDateTime = DateUtils.formatDateTime(context, when, flags);               
	        }
	    }
	    return finalDateTime;
	}

}
