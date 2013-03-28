package com.gunnarro.android.smsfilter.sms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.gunnarro.android.smsfilter.AppPreferences;

public class SMSReader {

    private AppPreferences appPreferences;

    public SMSReader() {
    }

    /**
     * Not implements
     */
    public List<SMS> getSMSOutboxGroupBy(String viewBy, boolean isGroupByAddress) {
        return null;
    }

    public List<SMS> getSMSBlocked(String period) {
        Map<String, SMS> smsBlockedMap = new HashMap<String, SMS>();
        List<SMS> smsBlocked = new ArrayList<SMS>();
        String blokedSMSasString = appPreferences.getListAsString(AppPreferences.SMS_BLOCKED_LOG);
        if (blokedSMSasString != null) {
            for (String blockedSMS : blokedSMSasString.split(AppPreferences.SEPARATOR)) {
                String[] split = blockedSMS.split(":");
                String time = split[0];
                String number = split[1];
                String key = number;
                if (period.equalsIgnoreCase("period")) {
                    key = time;
                }
                SMS sms = smsBlockedMap.get(key);
                if (sms == null) {
                    sms = new SMS(new Long(time).longValue(), number);
                    smsBlockedMap.put(key, sms);
                }
                sms.increaseNumberOfBlocked();
                smsBlocked.add(sms);
            }
        }
        return smsBlocked;
    }

    public void setAppPreferences(AppPreferences appPreferences) {
        this.appPreferences = appPreferences;
    }

}
