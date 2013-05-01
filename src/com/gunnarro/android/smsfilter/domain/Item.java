package com.gunnarro.android.smsfilter.domain;

/**
 * Class for holding list items.
 * 
 * @author gunnarro
 * 
 */
public class Item {

    private Integer id;
    private String value;
    private boolean isEnabled;
    private Integer fkFilterId;

    public Item(String value, boolean isEnabled) {
        this.value = value;
        this.isEnabled = isEnabled;
    }

    public Item(Integer id, Integer fkFilterId, String value, boolean isEnabled) {
        this(value, isEnabled);
        this.id = id;
        this.fkFilterId = fkFilterId;
    }

    public Integer getId() {
        return id;
    }

    public Boolean isEnabled() {
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

    public Integer getFkFilterId() {
        return fkFilterId;
    }

    public void setFkFilterId(Integer fkFilterId) {
        this.fkFilterId = fkFilterId;
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

    /**
     * The toString method is called by the ArrayAdapter in the
     * CommonListFragment, which value is viewed in the list.
     */
    @Override
    public String toString() {
        return value;
    }
}
