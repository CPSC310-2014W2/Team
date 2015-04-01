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

import org.json.simple.parser.ParseException;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
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

	@Override
	public List<FoodTruckData> getFoodTruckFilterName(String name) {
		// TODO Auto-generated method stub
		PersistenceManager pm = getPersistenceManager();
		Query query = pm.newQuery(
				"SELECT FROM com.google.gwt.foodonwheels.server.FoodTruck " +
				"WHERE :name.toUpperCase().startsWith(this.abbrev)");

		List<FoodTruck> results = 
				(List<FoodTruck>) query.execute(name.toUpperCase());

		List<FoodTruckData> truckData = new ArrayList<FoodTruckData>();
		Iterator<FoodTruck> iter = results.iterator();
		while (iter.hasNext())
		{
			FoodTruck truck = iter.next();
			truckData.add(truck.convert());
		}

		return truckData;
	}

	@Override
	public List<FoodTruckData> favFoodTruck(FoodTruckData truck) {
		// TODO Auto-generated method stub
		PersistenceManager pm = getPersistenceManager();
		if(truck != null){
			FavFoodTruck fav = new FavFoodTruck(truck);
			pm.makePersistent(fav);
		}
		List<FoodTruckData> trucks = new ArrayList<FoodTruckData>();
		UserService userService = UserServiceFactory.getUserService();
		try {
			Query q = pm.newQuery(FavFoodTruck.class, "user == u");
			q.declareParameters("com.google.appengine.api.users.User u");
			List<FavFoodTruck> fa = (List<FavFoodTruck>) q.execute(userService.getCurrentUser());
			for(FavFoodTruck f : fa){
				trucks.add(f.convert());
			}
		} finally {
			pm.close();
		}
		return trucks;
	}
}