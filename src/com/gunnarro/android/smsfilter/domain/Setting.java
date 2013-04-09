package com.gunnarro.android.smsfilter.domain;

public class Setting {

    public enum ConfigNameEnum {

    }

    private String name;
    private String value;

    public Setting(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }
}
