package com.gunnarro.android.smsfilter.receiver;

import java.util.Calendar;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;

import com.gunnarro.android.smsfilter.custom.CustomLog;
import com.gunnarro.android.smsfilter.domain.SMS;
import com.gunnarro.android.smsfilter.domain.SMSLog;
import com.gunnarro.android.smsfilter.service.FilterService;

public class SMSHandler extends MessageHandler {

    private static final String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";

    /**
     * {@inheritDoc}
     */
    @Override
    public void handleMessage(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            if (intent.getAction().equals(SMS_RECEIVED)) {
                handleSMS(context, bundle);
            } else {
                CustomLog.i(SMSHandler.class, "This was not an sms: action=" + intent.getAction() + " type=" + intent.getType());
            }
        }
    }

    /**
     * A PDU is a "protocol description unit", which is the industry format for
     * an SMS message. because SMSMessage reads/writes them you shouldn't need
     * to disect them. A large message might be broken into many, which is why
     * it is an array of objects.
     */
    private void handleSMS(Context context, Bundle bundle) {
        FilterService filterService = getFilterService(context);
        // log all received sms in order to present some statistic
        if (filterService.isLogMsg()) {
            filterService.createLog(new SMSLog(Calendar.getInstance().getTimeInMillis(), "xxxxxxxx", SMSLog.STATUS_MSG_RECEIVED, null));
        }
        if (!filterService.isMsgFilterActivated()) {
            return;
        }
        Object[] pdus = (Object[]) bundle.get("pdus");
        SmsMessage[] msgs = new SmsMessage[pdus.length];
        for (int i = 0; i < msgs.length; i++) {
            msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
            super.filter(new SMS(msgs[i].getOriginatingAddress()));
        }
    }
}
