package com.gunnarro.android.smsfilter.receiver;

import java.util.Calendar;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;

import com.gunnarro.android.smsfilter.custom.CustomLog;
import com.gunnarro.android.smsfilter.domain.SMSLog;
import com.gunnarro.android.smsfilter.service.FilterService;
import com.gunnarro.android.smsfilter.service.impl.FilterServiceImpl;

public class SMSHandler extends BroadcastReceiver {

    // public final static String KEY_SMS_MSG = "message";
    // public final static String KEY_MOBILE_NUMBER = "mobilenumber";
    private static final String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";

    private static FilterService filterService = null;

    /**
     * A PDU is a "protocol description unit", which is the industry format for
     * an SMS message. because SMSMessage reads/writes them you shouldn't need
     * to disect them. A large message might be broken into many, which is why
     * it is an array of objects.
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        CustomLog.i(this.getClass(), "Handle incomming sms...");
        if (bundle != null) {
            if (intent.getAction().equals(SMS_RECEIVED)) {
                handleSMS(context, bundle);
            } else {
                CustomLog.i(this.getClass(), "This was not an sms: " + intent.getAction());
            }
        }
    }

    private void handleSMS(Context context, Bundle bundle) {
        FilterService filterService = getFilterService(context);
        // log all received sms in order to present some statistic
        if (filterService.isLogSMS()) {
            filterService.createLog(new SMSLog(Calendar.getInstance().getTimeInMillis(), "xxxxxxxx", SMSLog.STATUS_SMS_INCOMMING, null));
        }
        if (!filterService.isSMSFilterActivated()) {
            // CustomLog.i(this.getClass(), "SMS filter not activated!");
            return;
        }
        Object[] pdus = (Object[]) bundle.get("pdus");
        SmsMessage[] msgs = new SmsMessage[pdus.length];
        for (int i = 0; i < msgs.length; i++) {
            msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
            try {
                if (filterService.isBlocked(msgs[i].getOriginatingAddress())) {
                    super.abortBroadcast();
                }
            } catch (Exception e) {
                CustomLog.e(this.getClass(), e.getMessage());
            }
        }
    }

    private static FilterService getFilterService(Context context) {
        if (filterService == null) {
            filterService = new FilterServiceImpl(context);
        }
        return filterService;
    }
}
