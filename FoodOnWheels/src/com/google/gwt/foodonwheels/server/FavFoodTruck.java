package com.google.gwt.foodonwheels.server;

import java.io.Serializable;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gwt.foodonwheels.shared.FoodTruckData;

@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class FavFoodTruck implements Serializable {

	/**
	 * default serial version ID
	 */
	private static final long serialVersionUID = 1L;

	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Long id;
	@Persistent
	private String name;
	@Persistent
	private String address;
	@Persistent
	private double latitude;
	@Persistent
	private double longitude;
	@Persistent
	private int rank;
	@Persistent
	private String imageUrl;
	@Persistent
	private String website;
	@Persistent
	private int userCounts4square;
	@Persistent
	private String cuisine;
	
	UserService userService = UserServiceFactory.getUserService();

	private User user;

	public FavFoodTruck(FoodTruckData truck){
		this.name = truck.getName();
		this.address = truck.getAddress();
		this.rank = truck.getRank();
		this.latitude = truck.getLatitude();
		this.longitude = truck.getLongitude();
		this.user = userService.getCurrentUser();
	}
	
	public FoodTruckData convert() {
		String name = this.getName();
		String address = this.getAddress();
		double latitude = this.getLatitude();
		double longitude = this.getLongitude();
		String url = this.getWebsite();
		String count = this.getUserCounts4square();
		int rank = this.getRank();

		return new FoodTruckData(name, address, latitude, longitude, url, count, rank);
	}

	public String getName() {
		return name;
	}

	public String getAddress() {
		return address;
	}

	public double getLatitude() {
		return latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public String getUserCounts4square() {
		return Integer.toString(userCounts4square);
	}

	public String getCuisine() {
		return cuisine;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public String getWebsite() {
		return website;
	}

	public int getRank() {
		return rank;
	}

	public void setRank(int rank) {
		this.rank = rank;
	}

}
