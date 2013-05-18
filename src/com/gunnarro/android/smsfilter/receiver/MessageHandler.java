package com.gunnarro.android.smsfilter.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.gunnarro.android.smsfilter.custom.CustomLog;
import com.gunnarro.android.smsfilter.service.FilterService;
import com.gunnarro.android.smsfilter.service.impl.FilterServiceImpl;

/**
 * Generic abstract class for handling incoming SMS and MMS.
 * 
 * @author gunnarro
 * 
 */
public abstract class MessageHandler extends BroadcastReceiver {

    private static FilterService filterService = null;

    public abstract void handleMessage(Context context, Intent intent);

    /**
     * {@inheritDoc}
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getExtras() != null) {
            handleMessage(context, intent);
        }
    }

    protected void filter(String phoneNumber) {
        try {
            if (filterService.isBlocked(phoneNumber)) {
                super.abortBroadcast();
            }
        } catch (Exception e) {
            CustomLog.e(MessageHandler.class, e.getMessage());
        }
    }

    protected static FilterService getFilterService(Context context) {
        if (filterService == null) {
            filterService = new FilterServiceImpl(context);
        }
        return filterService;
    }
}
