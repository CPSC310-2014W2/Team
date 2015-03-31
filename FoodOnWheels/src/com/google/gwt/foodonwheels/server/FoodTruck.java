package com.google.gwt.foodonwheels.server;

import java.io.Serializable;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.gwt.foodonwheels.shared.FoodTruckData;

@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class FoodTruck implements Serializable {

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
	private int score;
	@Persistent
	private String imageUrl;
	@Persistent
	private String website;
	@Persistent
	private int userCounts4square;
	@Persistent
	private String cuisine;



	public FoodTruck(String name, String address, 
			double latitude, double longitude, 
			String website, String count, 
			String imageUrl, String cuisine) {
		this.name = name;
		this.address = address;
		this.latitude = latitude;
		this.longitude = longitude;
		this.imageUrl = imageUrl;
		this.website = website;
		try {
			this.userCounts4square = Integer.parseInt(count);
		} catch (NumberFormatException e) {
			this.userCounts4square = 0;
		}
		this.cuisine = cuisine;
	}
	
	public FoodTruck(String name, String address, 
			double latitude, double longitude, 
			String website, String count) {
		this(name, address, latitude, longitude, website, count, "", "");
	}

	public FoodTruck(String name, String address, 
			double latitude, double longitude) {
		this(name, address, latitude, longitude, "", "", "", "");
	}

	public FoodTruck(String name, String address) {
		this(name, address, 0, 0);
	}

	public FoodTruckData convert() {
		String name = this.getName();
		String address = this.getAddress();
		double latitude = this.getLatitude();
		double longitude = this.getLongitude();

		return new FoodTruckData(name, address, latitude, longitude);
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

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

}
