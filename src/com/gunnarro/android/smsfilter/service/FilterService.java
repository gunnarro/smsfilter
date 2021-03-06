package com.gunnarro.android.smsfilter.service;

import java.util.List;

import com.gunnarro.android.smsfilter.domain.Item;
import com.gunnarro.android.smsfilter.domain.Msg;
import com.gunnarro.android.smsfilter.domain.MsgLog;
import com.gunnarro.android.smsfilter.service.impl.FilterServiceImpl.FilterTypeEnum;

public interface FilterService {

	public static final String DEFAULT_VALUE = "";

	/**
	 * Method to search for name in the contact list for a given phone number
	 * 
	 * @param phoneNumber
	 *            to find display name for
	 * @return contact display name
	 */
	public String lookUpContacts(String phoneNumber);

	/**
	 * Method to check if the phone number is blocked or not. If that's the
	 * case, the SMS is ignored and only logged to the SMSFilters blocked log.
	 * Otherwise, the SMS is handled as normal.
	 * 
	 * @param msg
	 *            the message to check
	 * @return true if the phone number is blocked false otherwise.
	 */
	public boolean isBlocked(Msg msg);

	// ******************************************************
	// Filter operations
	// ******************************************************

	/**
	 * Method to get active filter type, which is stored in the internal app
	 * preference table.
	 * 
	 * @return selected filter type
	 */
	public FilterTypeEnum getActiveFilterType();

	/**
	 * Method to activate filter type
	 * 
	 */
	public void activateFilterType(FilterTypeEnum filterType);

	/**
	 * Method to de-activate filter type
	 * 
	 */
	public void deActivateFilterType(FilterTypeEnum filterType);

	// ******************************************************
	// Item operations
	// ******************************************************
	/**
	 * 
	 * @param filterType
	 * @return
	 */
	public List<Item> getItemList(String filterType);

	/**
	 * 
	 * @param item
	 * @return
	 */
	public boolean deleteItem(Item item);

	/**
	 * 
	 * @param item
	 * @return
	 */
	public boolean updateItem(Item item);

	/**
	 * 
	 * @param item
	 * @return
	 */
	public boolean createItem(String filterName, Item item);

	// ******************************************************
	// Log operations
	// ******************************************************

	public List<MsgLog> getLogsStartDateAndEndDate();

	/**
	 * 
	 * @param groupBy
	 * @return
	 */
	public List<MsgLog> getLogs(String groupBy, String msgType);

	/**
	 * 
	 * @param item
	 * @return
	 */
	public boolean createLog(MsgLog log);

	/**
	 * 
	 * @param item
	 * @return
	 */
	public boolean removeAllLog(String msgType);

	// ******************************************************
	// Settings operations
	// ******************************************************

	/**
	 * Method to check if the message filter and the filter period is active.
	 * 
	 * @return
	 */
	public boolean isMsgFilterActive();

	/**
	 * Tells whether the message filter is activated or not.
	 * 
	 * @return true id activated, false otherwise
	 */
	public boolean isMsgFilterActivated();

	/**
	 * Tells whether the message filter period is activated or not.
	 * 
	 * @return true id activated, false otherwise
	 */
	public boolean isMsgFilterPeriodActivated();

	/**
	 * Method to turn message filter on
	 */
	public void activateMsgFilter();

	/**
	 * Method to turn message filter off
	 */
	public void deactivateMsgFilter();

	/**
	 * Method to turn message filter time period on
	 */
	public void activateMsgFilterPeriod();

	/**
	 * Method to turn message filter time period off
	 */
	public void deactivateMsgFilterPeriod();

	/**
	 * 
	 * @param time
	 */
	public void updateMsgFilterPeriodFromTime(String time);

	/**
	 * 
	 * @param time
	 */
	public void updateMsgFilterPeriodToTime(String time);

	/**
	 * 
	 * @return
	 */
	public String getMsgFilterPeriodFromTime();

	/**
	 * 
	 * @return
	 */
	public String getMsgFilterPeriodToTime();

	/**
	 * Method to check if all sms should be logged or not.
	 * 
	 * @return
	 */
	public boolean isLogMsg();
}