package com.gunnarro.android.smsfilter.utility;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Utility {

	public static String formatTime(int hour, int minute) {
		StringBuffer time = new StringBuffer();
		time.append(padTime(hour)).append(":").append(padTime(minute));
		return time.toString();
	}

	/** Add padding to numbers less than ten */
	private static String padTime(int n) {
		if (n >= 10) {
			return String.valueOf(n);
		} else {
			return "0" + String.valueOf(n);
		}
	}

	/**
	 * Extract hour part from time pattern HH:mm
	 * 
	 * @param time
	 * @return
	 */
	public static int getHour(String time) {
		if (time.isEmpty() || time.split(":").length != 2) {
			return 0;
		}
		return Integer.valueOf(time.split(":")[0]);
	}

	/**
	 * Extract hour part from time pattern HH:mm
	 * 
	 * @param time
	 * @return
	 */
	public static int getMinute(String time) {
		if (time.isEmpty() || time.split(":").length != 2) {
			return 0;
		}
		return Integer.valueOf(time.split(":")[1]);
	}

	public static String createSearch(String value) {
		if (value == null || value.isEmpty()) {
			return "";
		}
		String filter = "^" + value.replace("*", "") + ".*";
		if (value.startsWith("+")) {
			filter = "^\\" + value.replace("*", "") + ".*";
		} else if (value.startsWith("hidden")) {
			filter = "[0-9,+]{8,19}";
		}
		return filter;
	}

	public static Date timeToDate(String time, String pattern) {
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		try {
			return sdf.parse(time);
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static boolean isInActiveTimePeriode(String fromTime, String toTime) {
		if (fromTime.isEmpty() || toTime.isEmpty()) {
			// time period not set, return true
			return true;
		}
		Calendar currentTime = Calendar.getInstance();
		Calendar fromTimeCal = Calendar.getInstance();
		Calendar toTimeCal = Calendar.getInstance();
		fromTimeCal.setTime(Utility.timeToDate(fromTime, "HH:mm"));
		toTimeCal.setTime(Utility.timeToDate(toTime, "HH:mm"));
		if (currentTime.after(toTimeCal) && currentTime.before(fromTimeCal)) {
			return true;
		}
		// if ((currentTime.get(Calendar.HOUR_OF_DAY) >
		// fromTimeCal.get(Calendar.HOUR_OF_DAY) &&
		// currentTime.get(Calendar.MINUTE) > fromTimeCal
		// .get(Calendar.MINUTE))
		// && (currentTime.get(Calendar.HOUR_OF_DAY) <
		// toTimeCal.get(Calendar.HOUR_OF_DAY) &&
		// currentTime.get(Calendar.MINUTE) < toTimeCal
		// .get(Calendar.MINUTE))) {
		// return true;
		// }
		return false;
	}

}
