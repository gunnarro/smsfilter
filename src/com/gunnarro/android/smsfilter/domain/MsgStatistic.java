package com.gunnarro.android.smsfilter.domain;

/**
 * 
 * @author gunnarro
 * 
 */
public class MsgStatistic {

    private String value;
    private String type;
    private int numberOfSMS;
    private int numberOfMMS;
    private int totalCount;

    public MsgStatistic(String value, String type, int numberOfSMS, int numberOfMMS, int totalCount) {
        super();
        this.value = value;
        this.type = type;
        this.numberOfSMS = numberOfSMS;
        this.numberOfMMS = numberOfMMS;
        this.totalCount = totalCount;
    }

    public String getValue() {
        return value;
    }

    public String getType() {
        return type;
    }

    public int getNumberOfSMS() {
        return numberOfSMS;
    }

    public int getNumberOfMMS() {
        return numberOfMMS;
    }

    @Override
    public String toString() {
        return "value=" + value + ", type=" + type + ", numberOfSMS=" + numberOfSMS + ", numberOfMMS=" + numberOfMMS + ", totalCount=" + totalCount;
    }

}