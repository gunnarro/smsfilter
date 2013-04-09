package com.gunnarro.android.smsfilter.domain;

/**
 * Class for holding list items.
 * 
 * @author gunnarro
 * 
 */
public class SMSLog {

    
    private String type;
    private String value;

    public SMSLog(String value, boolean isEnabled) {
        this.value = value;
    }

    public SMSLog(String type, String value, boolean isEnabled) {
        this(value, isEnabled);
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public String getType() {
        return type;
    }
}
