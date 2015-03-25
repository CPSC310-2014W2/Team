package com.google.gwt.foodonwheels.server;

import static org.junit.Assert.*;

import org.json.simple.parser.ParseException;
import org.junit.Before;
import org.junit.Test;

public class FourSquareVenueSearchParserTest {
	private FourSquareVenueSearchParser parser = 
			new FourSquareVenueSearchParser();


	@Before
	public void initializeParser() {

	}

	@Test (expected = IllegalArgumentException.class)
	public void testNullString() {
		try {
			parser.parse(null);
			fail("IllegalArgumentException should be have been thrown");
		} catch (ParseException e) {
			fail("IllegalArgumentException should be have been thrown");
		}
	}

	@Test (expected = ParseException.class)
	public void testServerError() throws ParseException {
		String serverReply = "{\"meta\":{\"code\": 400, " 
				+ "\"errorType\": \"invalid_auth\", "
				+"\"errorDetail\": \"Missing access credentials. " 
				+ "See https://developer.foursquare.com/docs/oauth.html "
				+ " for details.\"} \"response\":  {}}";
		parser.parse(serverReply);
		fail("ParseException should be have been thrown");
	}

}
