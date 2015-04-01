package com.google.gwt.foodonwheels.server;

import org.scribe.builder.ServiceBuilder;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.oauth.OAuthService;

public class FourSquareAPI {
	private static final String API_HOST = "api.foursquare.com";
	private static final String SEARCH_PATH = "/v2/venues/search";
	private static final String FOOD_TRUCK = "4bf58dd8d48988d1cb941735";
	private static final String DEFAULT_LOCATION = "Vancouver,BC";
	private static final String DEFAULT_VERSION = "20150328";
	private static final String NUMBER_OF_RESULTS = Integer.toString(50);
	private static final String DEFAULT_INTENT = "browse";
	private static final String NORTHEAST_CORNER = "49.290483,-123.099691";
	private static final String SOUTHWEST_CORNER = "49.270704,-123.146345";
	
	private static final String CLIENT_ID = 
			"O1QG33HHDKFQZSALUEN4PRM3PEUTJVQV2OUONMA3110DSXTC";
	private static final String CLIENT_SECRET = 
			"P1SMAK1RU0CUQYRWNLT25QMEQPMJ1FY3VAQ3HXQF2NUUXQK5";

	OAuthService service;
	Token accessToken;

	public FourSquareAPI(String clientId, String clientSecret) {
		this.service = new ServiceBuilder().provider(TwoStepOAuth.class)
				.apiKey(clientId).apiSecret(clientSecret).build();
	}
	
	public FourSquareAPI() {
		this(CLIENT_ID,CLIENT_SECRET);
	}
	
	public String searchForFoodTrucksInDowntown() {
		OAuthRequest request = 
				new OAuthRequest(Verb.GET, "http://" + API_HOST + SEARCH_PATH);
		
		// Add client id and secret required to gain access.
		request.addQuerystringParameter("client_id", CLIENT_ID);
		request.addQuerystringParameter("client_secret", CLIENT_SECRET);
		
		// Search around the downtown area as defined by bound box (ne, sw).
		request.addQuerystringParameter("intent", DEFAULT_INTENT);
		request.addQuerystringParameter("ne", NORTHEAST_CORNER);
		request.addQuerystringParameter("sw", SOUTHWEST_CORNER);
		
		// Set category id as food truck and limit number of results.
		request.addQuerystringParameter("categoryId", FOOD_TRUCK);
		request.addQuerystringParameter("limit", NUMBER_OF_RESULTS);
		
		// Set the version of the FourSquare API.
		request.addQuerystringParameter("v", DEFAULT_VERSION);
		
		Response response = request.send();
		return response.getBody();
	}
	
	public String searchForFoodTrucksInVancouver() {
		OAuthRequest request = 
				new OAuthRequest(Verb.GET, "http://" + API_HOST + SEARCH_PATH);
		request.addQuerystringParameter("client_id", CLIENT_ID);
		request.addQuerystringParameter("client_secret", CLIENT_SECRET);
		request.addQuerystringParameter("near", DEFAULT_LOCATION);
		request.addQuerystringParameter("categoryId", FOOD_TRUCK);
		request.addQuerystringParameter("limit", NUMBER_OF_RESULTS);
		request.addQuerystringParameter("v", DEFAULT_VERSION);
		Response response = request.send();
		return response.getBody();
	}

}
