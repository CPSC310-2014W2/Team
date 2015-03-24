package com.google.gwt.foodonwheels.server;

import java.util.LinkedList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class FourSquareVenueSearchParser {

	public List<FoodTruck> parse(String responseJSON) throws ParseException {
		
		if (responseJSON == null)
			throw new IllegalArgumentException("Input string can not be null.");
		
		List<FoodTruck> trucks = new LinkedList<FoodTruck>();
		JSONParser parser = new JSONParser();
		JSONObject serverReply = null;

		// Throws ParseException when the response can not be parsed.
		serverReply = (JSONObject) parser.parse(responseJSON);
		try {
			JSONObject response = (JSONObject) serverReply.get("response");
			JSONArray venues = (JSONArray) response.get("venues");
			for (Object venue : venues) {
				JSONObject v =(JSONObject) venue;
				String name = v.get("name").toString();
				JSONObject location = (JSONObject) v.get("location");
				String address;
				if (location.get("address") != null) {
					address = location.get("address").toString();
				} else {
					if (location.get("crossStreet") != null)
						address = location.get("crossStreet").toString();
					else
						address = "";
				}
				double lat = Double.parseDouble(location.get("lat").toString());
				double lng = Double.parseDouble(location.get("lng").toString());
				
				trucks.add(new FoodTruck(name, address, lat, lng));
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			throw new ParseException(ParseException.ERROR_UNEXPECTED_EXCEPTION);
		}

		return trucks;
	}

}
