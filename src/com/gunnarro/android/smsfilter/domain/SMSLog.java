package com.gunnarro.android.smsfilter.domain;

/**
 * 
 * @author gunnarro
 * 
 */
public class SMSLog {

    public static final String STATUS_SMS_BLOCKED = "SMS_RECEIVED_BLOCKED";
    public static final String STATUS_SMS_RECEIVED = "SMS_RECEIVED";
    private long receivedTime;
    private String name;
    private String phoneNumber;
    private String status;
    private String filterType;
    private int count;
    private String key;

    public SMSLog(String key, int count) {
        this.key = key;
        this.count = count;
    }

    public SMSLog(long receivedTime, String phoneNumber, String status, String filterType) {
        this.receivedTime = receivedTime;
        this.phoneNumber = phoneNumber;
        this.status = status;
        this.filterType = filterType;
    }

    public String getName() {
        return name;
    }

    public long getReceivedTime() {
        return receivedTime;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getStatus() {
        return status;
    }

    public String getFilterType() {
        return filterType;
    }

    public int getCount() {
        return count;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return receivedTime + " " + phoneNumber + " " + status + " " + filterType;
    }
}
