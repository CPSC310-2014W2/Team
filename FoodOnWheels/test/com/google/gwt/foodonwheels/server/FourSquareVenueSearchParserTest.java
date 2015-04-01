package com.google.gwt.foodonwheels.server;

import static org.junit.Assert.*;

import java.util.List;

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
	
	@Test
	public void testServerReply() {
		String serverReply = "{\"meta\":{\"code\":200},\"response\":{\"venues\""
				+ ":[{\"id\":\"54bd534b498e05024fddb23f\",\"name\":"
				+"\"Street Food City\",\"contact\":{},\"location\":{\"address\""
				+":\"North Plaza, Vancouver Art Gallery\",\"lat\":49.28330293849256,"
				+"\"lng\":-123.12009421056793,\"cc\":\"CA\",\"city\":\"Vancouver\","
				+ "\"state\":\"BC\",\"country\":\"Canada\",\"formattedAddress\":"
				+"[\"North Plaza, Vancouver Art Gallery\",\"Vancouver BC\",\"Canada\"]}"
				+",\"categories\":[{\"id\":\"4bf58dd8d48988d1cb941735\",\"name\":"
				+"\"Food Truck\",\"pluralName\":\"Food Trucks\",\"shortName\":"
				+"\"Food Truck\",\"icon\":{\"prefix\":\"https://ss3.4sqi.net/img/categories_v2/food/streetfood_\","
				+"\"suffix\":\".png\"},\"primary\":true}],\"verified\":false,\"stats\":{\"checkinsCount\":23,\"usersCount\":11,\"tipCount\":0},\"specials\":{\"count\":0,\"items\":[]},\"hereNow\":{\"count\":0,\"summary\":\"Nobody here\",\"groups\":[]},\"referralId\":\"v-1427868531\"}]}}";
		
		try {
			List<FoodTruck> trucks = parser.parse(serverReply);
			FoodTruck truck = trucks.get(0);
			assertEquals("Street Food City",truck.getName());
			assertEquals("North Plaza, Vancouver Art Gallery",truck.getAddress().trim());
			assertEquals(49.28330293849256,truck.getLatitude(),1e-5);
			assertEquals(-123.12009421056793,truck.getLongitude(),1e-5);
		} catch (ParseException e) {
			fail("Should not throw exception!");
		}
	}

}
