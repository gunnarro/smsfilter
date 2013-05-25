package com.gunnarro.android.smsfilter.domain;

/**
 * 
 * @author gunnarro
 * 
 */
public class MMSLog extends MsgLog {

    public static final String MSG_TYPE = "MMS";

    public MMSLog(String key, int count) {
        super(key, count);
    }

    public MMSLog(long receivedTime, String phoneNumber, String status, String filterType) {
        super(receivedTime, phoneNumber, status, filterType);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getMsgType() {
        return MMS.class.getSimpleName();
    }
}
