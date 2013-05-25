package com.gunnarro.android.smsfilter.domain;

public class MMS extends Msg {

    public MMS(String phoneNumber) {
        super(phoneNumber);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getType() {
        return MMS.class.getSimpleName();
    }
}
