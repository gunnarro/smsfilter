package com.gunnarro.android.smsfilter.domain;

/**
 * 
 * @author gunnarro
 * 
 */
public class SMSLog extends MsgLog {

    public SMSLog(String key, int count) {
        super(key, count);
    }

    public SMSLog(long receivedTime, String phoneNumber, String status, String filterType) {
        super(receivedTime, phoneNumber, status, filterType);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getMsgType() {
        return SMS.class.getSimpleName();
    }
}