package com.google.gwt.foodonwheels.server;

import java.io.Serializable;

public class FoodTruck implements Serializable {

	/**
	 * default serial version ID
	 */
	private static final long serialVersionUID = 1L;

	private String name;
	private String address;
	private String phone;
	private String cuisine;
	private String hours;
	private String website;
	
	public FoodTruck(String name, String address, String location) {
		
	}

	public FoodTruck(String name, String address, String phone, String cuisine,
			String hours, String website) {
		super();
		this.name = name;
		this.address = address;
		this.phone = phone;
		this.cuisine = cuisine;
		this.hours = hours;
		this.website = website;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getCuisine() {
		return cuisine;
	}

	public void setCuisine(String cuisine) {
		this.cuisine = cuisine;
	}

	public String getHours() {
		return hours;
	}

	public void setHours(String hours) {
		this.hours = hours;
	}

	public String getWebsite() {
		return website;
	}

	public void setWebsite(String website) {
		this.website = website;
	}


}
