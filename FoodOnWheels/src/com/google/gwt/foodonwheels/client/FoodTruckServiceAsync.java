package com.google.gwt.foodonwheels.client;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface FoodTruckServiceAsync {

	void addFoodTruck(String name, String address, AsyncCallback<Void> callback);

	void getFoodTruckList(AsyncCallback<List<String>> callback);

	void fetchFoodTruckDataFromYelp(AsyncCallback<Void> callback);

}
