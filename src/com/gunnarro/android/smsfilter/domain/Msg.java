package com.gunnarro.android.smsfilter.domain;

public abstract class Msg {
    private String phoneNumber;
    private String content;

    public Msg(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Msg(String phoneNumber, String content) {
        this(phoneNumber);
        this.content = content;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getContent() {
        return content;
    }

    public abstract String getType();

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "number=" + phoneNumber + " type=" + getType();
    }
}
