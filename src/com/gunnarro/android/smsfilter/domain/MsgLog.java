package com.gunnarro.android.smsfilter.domain;

/**
 * 
 * @author gunnarro
 * 
 */
public abstract class MsgLog {

    public static final String STATUS_MSG_BLOCKED = "MSG_RECEIVED_BLOCKED";
    public static final String STATUS_MSG_RECEIVED = "MSG_RECEIVED";

    private long receivedTime;
    private String name;
    private String phoneNumber;
    private String status;
    private String filterType;
    private int count;
    private String key;

    public MsgLog(String key, int count) {
        this.key = key;
        this.count = count;
    }

    public MsgLog(long receivedTime, String phoneNumber, String status, String filterType) {
        this.receivedTime = receivedTime;
        this.phoneNumber = phoneNumber;
        this.status = status;
        this.filterType = filterType;
    }

    public abstract String getMsgType();

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
        return getMsgType() + " " + receivedTime + " " + phoneNumber + " " + status + " " + filterType + " " + count;
    }
}
