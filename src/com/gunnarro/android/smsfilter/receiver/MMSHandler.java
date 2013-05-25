package com.gunnarro.android.smsfilter.receiver;

import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.gunnarro.android.smsfilter.custom.CustomLog;
import com.gunnarro.android.smsfilter.domain.MMS;
import com.gunnarro.android.smsfilter.domain.MMSLog;
import com.gunnarro.android.smsfilter.service.FilterService;

public class MMSHandler extends MessageHandler {

    private static final String MMS_RECEIVED = "android.provider.Telephony.WAP_PUSH_RECEIVED";
    private static final String MMS_DATA_TYPE = "application/vnd.wap.mms-message";

    /**
     * {@inheritDoc}
     */
    @Override
    public void handleMessage(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
        }
        if (isMMS(intent)) {
            handleMMS(context, bundle);
        } else {
            CustomLog.i(MMSHandler.class, "This was not an mms: action=" + intent.getAction() + " type=" + intent.getType());
        }
    }

    private boolean isMMS(Intent intent) {
        if (intent.getAction().equals(MMS_RECEIVED) && intent.getType().equals(MMS_DATA_TYPE)) {
            return true;
        }
        return false;
    }

    /**
     * The MMS data content is as follows +4711223344/Type
     * 
     * @param context
     * @param bundle
     */
    private void handleMMS(Context context, Bundle bundle) {
        FilterService filterService = getFilterService(context);
        // log all received sms in order to present some statistic
        if (filterService.isLogMsg()) {
            filterService.createLog(new MMSLog(Calendar.getInstance().getTimeInMillis(), "xxxxxxxx", MMSLog.STATUS_MSG_RECEIVED, null));

        }
        if (!filterService.isMsgFilterActivated()) {
            return;
        }
        String phoneNumber = null;
        byte[] data = bundle.getByteArray("data");
        String buffer = new String(data);
        // FIXME: This will also filter out hidden numbers, i.e they will not be
        // blocked
        Pattern pattern = Pattern.compile("[0-9,+]{8,19}");
        Matcher matcher = pattern.matcher(buffer);
        if (matcher.find()) {
            phoneNumber = matcher.group();
            Toast.makeText(context, "MMS filter number!" + phoneNumber, Toast.LENGTH_LONG).show();
            super.filter(new MMS(phoneNumber));
        } else {
            Toast.makeText(context, "No phonenumber found in MMS data part!" + buffer, Toast.LENGTH_LONG).show();
        }
    }
}
