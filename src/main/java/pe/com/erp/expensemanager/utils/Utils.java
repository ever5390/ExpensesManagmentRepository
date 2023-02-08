package pe.com.erp.expensemanager.utils;

//import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Utils {

	private static final Logger LOG = LoggerFactory.getLogger(Utils.class);

	public static int getGeneratorNumber() {
		return (int) (Math.random() * (999999999 - 100000000 + 1) + 100000000);
	}
	public static Double roundTwoDecimals(Double amountShow) {
		return Math.round(amountShow * 100) / 100d;
	}

	public static int extracted(Date dateParam) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(dateParam);
		return Calendar.MONTH;
	}

	public static Date getNextLocalDate(Date dateParam, String order, int paramAdd) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(dateParam);
		int paramType = 0;

		paramType = order.equals("final") ? Calendar.MONTH : Calendar.SECOND;
		calendar.add(paramType, paramAdd);

		return calendar.getTime();
	}

	public static Date getDateHourUpate(Date dateParam, String order) {
		Calendar calendar = Calendar.getInstance();
		int cant = 0;
		calendar.setTime(dateParam);
		cant = order.equals("plus") ? 1 : -1;

		calendar.add(Calendar.HOUR, cant * Utils.getOffsetTimeZoneInHours());
		return calendar.getTime();
	}

	public static LocalDateTime updatingHourToLocalDateTime(LocalDateTime localDateTimeRequest, String orderType) {
		LocalDateTime localDateTime = Utils.getLocalDateTimeNow();
		int gmtOffset = Utils.getOffsetTimeZoneInHours();

		if (localDateTimeRequest != null) {
			localDateTime = localDateTimeRequest; // get localDateTime param request
		}

		if (orderType.equals("show")) {
			localDateTime = localDateTime.plusHours(gmtOffset);
		} else {
			localDateTime = localDateTime.minusHours(gmtOffset);// get localDateTime now
		}
		return localDateTime;
	}

	private static LocalDateTime getLocalDateTimeNow() {
		// Create a calender instance.
		Calendar calendar = Calendar.getInstance();
		// Getting the timezone
		TimeZone tz = calendar.getTimeZone();
		// Getting zone id
		ZoneId zoneId = tz.toZoneId();
		// conversion
		LocalDateTime localDateTime = LocalDateTime.ofInstant(calendar.toInstant(), zoneId);

		return localDateTime;
	}

	private static int getOffsetTimeZoneInHours() {
		// Create a calender instance.
		Calendar calendar = Calendar.getInstance();
		TimeZone currentTimeZone = calendar.getTimeZone();
		System.out.println("TIME-ZONE: " + currentTimeZone);
		Calendar currentDt = new GregorianCalendar(currentTimeZone);
		System.out.println("TIME-ZONE currentDt: " + currentDt);
		// Get the Offset from GMT taking DST into account
		int gmtOffset = currentTimeZone.getOffset(currentDt.get(Calendar.ERA), currentDt.get(Calendar.YEAR),
				currentDt.get(Calendar.MONTH), currentDt.get(Calendar.DAY_OF_MONTH),
				currentDt.get(Calendar.DAY_OF_WEEK), currentDt.get(Calendar.MILLISECOND));
		// convert to hours
		gmtOffset = gmtOffset / (60 * 60 * 1000);
		// convert to hours in case negative value
		gmtOffset = (gmtOffset < 0 ? -1 * gmtOffset : gmtOffset);

		return gmtOffset;
	}

	private static String formatDateString() {
		// Create a calender instance.
		Calendar calendar = Calendar.getInstance();
		TimeZone currentTimeZone = calendar.getTimeZone();
		Calendar currentDt = new GregorianCalendar(currentTimeZone);
		return Calendar.YEAR + "-" + Calendar.MONTH + "-" + Calendar.DAY_OF_MONTH + "T" + Calendar.HOUR_OF_DAY + ":"
				+ Calendar.MINUTE + ":" + Calendar.SECOND;
	}

	public static Date convertStringToDate(String inputDate) {

		
	  String DEFAULT_PATTERN = "yyyy-MM-dd'T'HH:mm:ss";
	  SimpleDateFormat formatter = new SimpleDateFormat(DEFAULT_PATTERN);
	  Date myDate = new Date();

	  try {
			myDate = formatter.parse(inputDate);
	  } catch (Exception e) {
		e.printStackTrace();
	  }
	  
	  return myDate;
	  
	}

	public static Date convertStringToDateT(String inputDate) {
		
	  String DEFAULT_PATTERN = "yyyy-MM-dd HH:mm:ss";
	  SimpleDateFormat formatter = new SimpleDateFormat(DEFAULT_PATTERN);
	  Date myDate = new Date();
	  
	  try { 
		  myDate = formatter.parse(inputDate);
	  } catch (Exception e) {
		  e.printStackTrace(); 
	  } 
	  
	  return myDate; 
	}

	public static LocalDateTime convertStringToLocalDateTime(String inputDate) {

		String FORMAT_ONE = "yyyy-MM-dd'T'HH:mm:ss";
		String FORMAT_TWO = "yyyy-MM-dd'T'HH:mm:ss.SSS SS:SS";

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(FORMAT_ONE);
		LocalDateTime joiningDate = LocalDateTime.now();

		try {
			joiningDate = LocalDateTime.parse(inputDate, formatter);

		} catch (Exception e) {
			try {
				formatter = DateTimeFormatter.ofPattern(FORMAT_TWO);
				joiningDate = LocalDateTime.now();
			} catch (Exception e2) {
				e.printStackTrace();

			}
		}

		return joiningDate;
	}

}
