package com.google.gwt.foodonwheels.client;

import java.util.List;

import com.google.gwt.foodonwheels.shared.FoodTruckData;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface FoodTruckServiceAsync {

	void addFoodTruck(String name, String address, AsyncCallback<Void> callback);

	void getFoodTruckList(AsyncCallback<List<String>> callback);

	void fetchFoodTruckDataFromYelp(AsyncCallback<Void> callback);

	void fetchFoodTruckDataFromFourSquare(AsyncCallback<Void> callback);

	void getFoodTruckDataList(AsyncCallback<List<FoodTruckData>> callback);

	void getFoodTruckFilterName(String name,
			AsyncCallback<List<FoodTruckData>> callback);

}
