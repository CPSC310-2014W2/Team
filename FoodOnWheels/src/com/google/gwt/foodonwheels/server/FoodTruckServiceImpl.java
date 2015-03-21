package com.google.gwt.foodonwheels.server;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import javax.jdo.Extent;
import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.google.gwt.foodonwheels.client.FoodTruckService;
import com.google.gwt.foodonwheels.shared.FoodTruckData;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class FoodTruckServiceImpl 
extends RemoteServiceServlet implements FoodTruckService {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final Logger LOG = 
			Logger.getLogger(FoodTruckServiceImpl.class.getName());
	private static final PersistenceManagerFactory PMF =
			JDOHelper.getPersistenceManagerFactory("transactions-optional");

	@Override
	public void addFoodTruck(String name, String address) {
		// TODO Auto-generated method stub
		PersistenceManager pm = getPersistenceManager();
		try {
			pm.makePersistent(new FoodTruck(name, address));
		} finally {
			pm.close();
		}

	}

	@Override
	public List<String> getFoodTruckList() {
		// TODO Auto-generated method stub
		PersistenceManager pm = getPersistenceManager();
		List<String> symbols = new ArrayList<String>();
		try {
			Extent<FoodTruck> ex = pm.getExtent(FoodTruck.class);
			Iterator<FoodTruck> iter = ex.iterator();
			while (iter.hasNext())
			{
				FoodTruck truck = iter.next();
				symbols.add(truck.getName() +", " + truck.getAddress());
			}
			//			Query q = pm.newQuery(FoodTruck.class);
			//			q.declareParameters("com.google.appengine.api.users.User u");
			//			q.setOrdering("createDate");
			//			List<FoodTruck> trucks = (List<FoodTruck>) q.execute();
			//			for (FoodTruck truck : trucks) {
			//				symbols.add(truck.getName() + " " + truck.getAddress());
			//			}
		} finally {
			pm.close();
		}
		return symbols;
	}

	private PersistenceManager getPersistenceManager() {
		return PMF.getPersistenceManager();
	}

	@Override
	public void fetchFoodTruckDataFromYelp() {
		// TODO Auto-generated method stub
		FourSquareAPI fourSquare = new FourSquareAPI();
		String response = fourSquare.searchForFoodTrucksInVancouver();
		FourSquareVenueSearchParser p = new FourSquareVenueSearchParser();
		try {
			p.parse(response);
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		YelpAPI yelp = new YelpAPI();
		String searchResponseJSON = yelp.searchForFoodTrucksVancouver();
		YelpParser parser = new YelpParser();
		try {
			parser.parse(searchResponseJSON);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//		JSONParser parser = new JSONParser();
		//		JSONObject response = null;
		//		try {
		//			response = (JSONObject) parser.parse(searchResponseJSON);
		//		} catch (ParseException pe) {
		//			System.out.println("Error: could not parse JSON response:");
		//			System.out.println(searchResponseJSON);
		//			System.exit(1);
		//		}
		//		
		//		JSONArray businesses = (JSONArray) response.get("businesses");
		//		for(Object business : businesses) {
		//			JSONObject b = (JSONObject) business;
		//			String name = b.get("name").toString();
		//			JSONObject addrJSON = (JSONObject) b.get("location");
		//			JSONArray addrArray = (JSONArray) addrJSON.get("address");
		//			String address = addrArray.get(0).toString();
		//			
		//			PersistenceManager pm = getPersistenceManager();
		//			try {
		//				pm.makePersistent(new FoodTruck(name, address));
		//			} finally {
		//				pm.close();
		//			}
		//		}
	}

	@Override
	public void fetchFoodTruckDataFromFourSquare() {
		// TODO Auto-generated method stub
		FourSquareAPI fourSquare = new FourSquareAPI();
		String response = fourSquare.searchForFoodTrucksInDowntown();
		FourSquareVenueSearchParser p = new FourSquareVenueSearchParser();
		PersistenceManager pm = getPersistenceManager();
		try {
			List<FoodTruck> trucks = p.parse(response);
			for (FoodTruck truck : trucks) {
				pm.makePersistent(truck);
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			pm.close();
		}
	}

	@Override
	public List<FoodTruckData> getFoodTruckDataList() {
		// TODO Auto-generated method stub
		PersistenceManager pm = getPersistenceManager();
		List<FoodTruckData> truckData = new ArrayList<FoodTruckData>();
		try {
			Extent<FoodTruck> ex = pm.getExtent(FoodTruck.class);
			Iterator<FoodTruck> iter = ex.iterator();
			while (iter.hasNext())
			{
				FoodTruck truck = iter.next();
				truckData.add(truck.convert());
			}
			//			Query q = pm.newQuery(FoodTruck.class);
			//			q.declareParameters("com.google.appengine.api.users.User u");
			//			q.setOrdering("createDate");
			//			List<FoodTruck> trucks = (List<FoodTruck>) q.execute();
			//			for (FoodTruck truck : trucks) {
			//				symbols.add(truck.getName() + " " + truck.getAddress());
			//			}
		} finally {
			pm.close();
		}
		return truckData;
	}

}