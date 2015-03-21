package com.google.gwt.foodonwheels.server;

import java.util.LinkedList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class YelpParser {

	/**
	 * Parse the String response from server and return a list of FoodTruck.
	 * 
	 * @param searchResponseJSON is the String response from server to be parsed
	 * @return trucks is a list of FoodTruck parsed from server response.
	 * @throws ParseException when the String response can not be parsed.
	 * @throws YelpJSONparserException 
	 */
	public List<FoodTruck> parse(String searchResponseJSON) 
			throws ParseException, YelpJSONparserException {
		List<FoodTruck> trucks = new LinkedList<FoodTruck>();
		JSONParser parser = new JSONParser();
		JSONObject response = null;

		// Throws ParseException when the response can not be parsed.
		response = (JSONObject) parser.parse(searchResponseJSON);

		try{
			JSONArray businesses = (JSONArray) response.get("businesses");
			for(Object business : businesses) {
				JSONObject b = (JSONObject) business;
				String name = b.get("name").toString();
				JSONObject addrJSON = (JSONObject) b.get("location");
				JSONArray addrArray = (JSONArray) addrJSON.get("address");
				String address = addrArray.get(0).toString();
				trucks.add(new FoodTruck(name,address));
			}
		} catch(Exception e){
			String msg = "Failed to find food truck info from Yelp data.";
			throw new YelpJSONparserException(msg);
		}

		return trucks;

	}

	private class YelpJSONparserException extends Exception {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public YelpJSONparserException(String message) {
			super(message);
		}

	}
}
