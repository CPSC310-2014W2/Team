package com.google.gwt.foodonwheels.client;

import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("truck")
public interface FoodTruckService extends RemoteService {
	void addFoodTruck(String name, String address);

	List<String> getFoodTruckList();
	
	void fetchFoodTruckDataFromYelp();
}
