package com.google.gwt.foodonwheels.client;

import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;

public interface FoodTruckService extends RemoteService {
	void addFoodTruck(String name, String address);

	List<String> getFoodTruckList();
}
