package com.gunnarro.android.smsfilter.domain;

import java.util.Calendar;
import java.util.List;

/**
 * Class for holding filter information.
 * 
 * @author gunnarro
 * 
 */
public class Filter {

    private Integer id;
    private String name;
    private String type;
    private boolean isActivated;
    private Calendar fromTime;
    private Calendar toTime;
    private List<Item> itemList;

    public Filter(String name, boolean isActivated) {
        this.name = name;
        this.isActivated = isActivated;
    }

    public Filter(Integer id, String name, boolean isActivated) {
        this(name, isActivated);
        this.id = id;
    }

    public Filter(String name, String type, boolean isActivated, List<Item> items) {
        this(name, isActivated);
        this.type = type;
        this.itemList = items;
    }

    public Integer getId() {
        return id;
    }

    public Boolean isActivated() {
        return isActivated && isInActiveTimePeriode();
    }

    public void setActivated(boolean isActivated) {
        this.isActivated = isActivated;
    }

    public List<Item> getItemList() {
        return itemList;
    }

    public void setItemList(List<Item> itemList) {
        this.itemList = itemList;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public Calendar getFromTime() {
        return fromTime;
    }

    public void setFromTime(Calendar fromTime) {
        this.fromTime = fromTime;
    }

    public Calendar getToTime() {
        return toTime;
    }

    public void setToTime(Calendar toTime) {
        this.toTime = toTime;
    }

    private boolean isInActiveTimePeriode() {
        if (fromTime == null || toTime == null) {
            // time period not set, return true
            return true;
        }
        Calendar currentTime = Calendar.getInstance();
        if (currentTime.get(Calendar.HOUR_OF_DAY) > fromTime.get(Calendar.HOUR_OF_DAY)
                && currentTime.get(Calendar.HOUR_OF_DAY) < toTime.get(Calendar.HOUR_OF_DAY)) {
            return true;
        }
        return false;
    }

    /**
     * {@inheritDoc}
     * 
     * @return
     */
    @Override
    public String toString() {
        return (id != null ? id + ", " : "") + name + ", " + isActivated;
    }
}