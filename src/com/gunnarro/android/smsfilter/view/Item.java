package com.gunnarro.android.smsfilter.view;

public class Item {

    private String value;
    private boolean isEnabled;

    public Item(String value, boolean isEnabled) {
        this.value = value;
        this.isEnabled = isEnabled;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean isEnabled) {
        this.isEnabled = isEnabled;
    }

    public String getValue() {
        return value;
    }

    public String toValuePair() {
        return value + ":" + isEnabled;
    }

    /**
     * Creates string:boolean pair Item object
     * 
     * @param s string:boolean pair, f.example 22334455;true
     * @return
     */
    public final static Item createItem(String s) {
        if (s == null || s.equals("")) {
            return null;
        }
        String[] split = s.split(":");
        if (split.length != 2) {
            return null;
        }
        String value = split[0];
        if (split[1].equalsIgnoreCase(Boolean.TRUE.toString()) || split[1].equalsIgnoreCase(Boolean.FALSE.toString())) {
            boolean enabled = Boolean.parseBoolean(split[1]);
            return new Item(value, enabled);
        }
        return null;
    }

    @Override
    public int hashCode() {
        final int multiplier = 23;
        int hashCode = 0;
        if (hashCode == 0) {
            int code = 133;
            code = multiplier * code + this.value.hashCode();
            hashCode = code;
        }
        return hashCode;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Item)) {
            return false;
        }
        final Item other = (Item) obj;
        if (this.value.equals(other.value)) {
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return value;
    }
}
