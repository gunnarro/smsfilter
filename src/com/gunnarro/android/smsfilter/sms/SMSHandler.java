package com.gunnarro.android.smsfilter.sms;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

public class SMSHandler extends BroadcastReceiver {

    // public final static String KEY_SMS_MSG = "message";
    // public final static String KEY_MOBILE_NUMBER = "mobilenumber";
    private static final String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";

    /**
     * A PDU is a "protocol description unit", which is the industry format for
     * an SMS message. because SMSMessage reads/writes them you shouldn't need
     * to disect them. A large message might be broken into many, which is why
     * it is an array of objects.
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        Log.i(createLogTag(this.getClass()), "Handle incomming sms...");
        if (bundle != null) {
            if (intent.getAction().equals(SMS_RECEIVED)) {
                handleSMS(context, bundle);
            } else {
                Log.i(createLogTag(this.getClass()), "This was not an sms: " + intent.getAction());
            }
        }
    }

    private void handleSMS(Context context, Bundle bundle) {
        SMSFilter smsFilter = new SMSFilter(context);
        Object[] pdus = (Object[]) bundle.get("pdus");
        SmsMessage[] msgs = new SmsMessage[pdus.length];
        for (int i = 0; i < msgs.length; i++) {
            msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
            try {
                if (smsFilter.isBlocked(msgs[i].getOriginatingAddress())) {
                    super.abortBroadcast();
                }
            } catch (Exception e) {
                Log.e(createLogTag(this.getClass()), e.getMessage(), e);
            }
        }
    }

    public static String createLogTag(Class<?> clazz) {
        return DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.ENGLISH).format(new Date()) + " " + clazz.getSimpleName();
    }
}
