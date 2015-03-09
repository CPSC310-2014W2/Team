package com.google.gwt.foodonwheels.server;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;

import com.google.gwt.foodonwheels.client.FoodTruckService;
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
			Query q = pm.newQuery(FoodTruck.class);
			q.declareParameters("com.google.appengine.api.users.User u");
			q.setOrdering("createDate");
			List<FoodTruck> trucks = (List<FoodTruck>) q.execute();
			for (FoodTruck truck : trucks) {
				symbols.add(truck.getName() + " " + truck.getAddress());
			}
		} finally {
			pm.close();
		}
		return null;
	}

	private PersistenceManager getPersistenceManager() {
		return PMF.getPersistenceManager();
	}

}
