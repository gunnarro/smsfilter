package com.gunnarro.android.smsfilter.domain;

public class SMS extends Msg {

    public SMS(String phoneNumber) {
        super(phoneNumber);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getType() {
        return SMS.class.getSimpleName();
    }
}
