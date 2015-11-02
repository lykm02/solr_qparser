package com.tzls.search.solr;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.junit.Test;

public class TimeZoneTest {
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HHmmss");
	@Test
	public void test() throws ParseException{
//		TimeZone zone = TimeZone.getTimeZone("+0800");
		System.out.println(TimeZone.getTimeZone("GMT+08:00"));
		System.out.println(TimeZone.getTimeZone("GMT-0800"));
		Date date = new Date();
		String dateText = sdf.format(date);
		System.out.println("Formated date is : " + dateText);
		sdf.setTimeZone(TimeZone.getTimeZone("GMT-0700"));
		System.out.println("-0800   "+ sdf.parse(dateText));
		Calendar  cal = Calendar.getInstance();
		cal.setTime(date);
		cal.setTimeZone(TimeZone.getTimeZone("GMT-0700"));
		System.out.println("-0700   "+ cal.getTime() +" // " + cal.getTimeZone());
		//if()
		sdf.setTimeZone(TimeZone.getTimeZone("GMT0000"));
		System.out.println("0000   "+ sdf.parse(dateText));
		sdf.setTimeZone(TimeZone.getTimeZone("GMT+0800"));
		System.out.println("+0800   "+ sdf.parse(dateText));
		cal = Calendar.getInstance();
		cal.setTime(date);
		cal.setTimeZone(TimeZone.getTimeZone("GMT+0800"));
		System.out.println("+0800   "+ cal.getTime());
		System.out.println(" ------------------------------------------------------- ");
		cal = Calendar.getInstance(TimeZone.getTimeZone("GMT-0800"));
		cal.setTime(date);
		System.out.println(cal + " //  ");
		
		Calendar cal2 = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
//		cal.setTime(date);
		System.out.println(cal2 + " //  ");
	}
}
