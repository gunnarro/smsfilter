package com.gunnarro.android.smsfilter.utility;

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

}
