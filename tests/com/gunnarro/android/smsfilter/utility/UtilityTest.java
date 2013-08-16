package com.gunnarro.android.smsfilter.utility;

import static org.junit.Assert.*;

import org.junit.Test;

public class UtilityTest {

	@Test
	public void formatTime() {
		assertEquals("22:23", Utility.formatTime(22, 23));
		assertEquals(23, Utility.getHour("23:45"));
		assertEquals(45, Utility.getMinute("23:45"));
	}

	@Test
	public void createSearch() {
		assertEquals("22:23", Utility.createSearch("23233456"));
	}

	@Test
	public void isInActiveTimePeriode() {
		assertTrue(Utility.isInActiveTimePeriode("01:00", "24:00"));
	}

}
