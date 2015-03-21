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
	private String phone;
	@Persistent
	private String cuisine;
	
	

	public FoodTruck(String name, String address, 
			double latitude, double longitude, 
			String imageUrl, String website, 
			String phone, String cuisine) {
		this.name = name;
		this.address = address;
		this.latitude = latitude;
		this.longitude = longitude;
		this.imageUrl = imageUrl;
		this.website = website;
		this.phone = phone;
		this.cuisine = cuisine;
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

	public String getPhone() {
		return phone;
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
