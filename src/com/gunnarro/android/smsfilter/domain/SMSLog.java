package com.gunnarro.android.smsfilter.domain;

/**
 * Class for holding list items.
 * 
 * @author gunnarro
 * 
 */
public class SMSLog {

    public static final String STATUS_SMS_BLOCKED = "blocked";
    public static final String STATUS_SMS_INCOMMING = "incomming";
    private long receivedTime;
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
    // 04-30 23:56:44.196:
    // I/FilterServiceImpl.getLogsStartDateAndEndDate:247(936): [1367359004196
    // 1554928872 45465500 blocked, 1367359004196 1554928872 45465503 blocked]

}
