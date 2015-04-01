package com.google.gwt.foodonwheels.shared;

import java.io.Serializable;

public class FoodTruckData implements Serializable {
	/**
	 * Default serial version UID
	 */
	private static final long serialVersionUID = 1L;
	
	private static final int UNDEFINED_RANK = 0;
	private String name;
	private String address;
	private double latitude;
	private double longitude;
	private int rank;
	private String url;
	private String count;
	
	/**
	 * Stub required for serialization. 
	 */
	public FoodTruckData() {}

	public FoodTruckData(String name, String address, 
			double latitude, double longitude,
			String url, String count, int rank) {
		this.name = name;
		this.address = address;
		this.latitude = latitude;
		this.longitude = longitude;
		this.rank = rank;
		this.url = url;
		this.count = count;
	}
	
	public FoodTruckData(String name, String address, 
			double latitude, double longitude,
			String url, String count) {
		this(name, address, latitude, longitude, url, count, UNDEFINED_RANK);
	}

	public FoodTruckData(String name, String address, 
			double latitude, double longitude) {
		this(name, address, latitude, longitude, "None", "0", UNDEFINED_RANK);
	}

	@Override
	public String toString() {
		String truckString = name + " " + Double.toString(latitude)
				+ " " + Double.toString(longitude);
		return truckString;
	}

	public int getRank() {
		return rank;
	}

	public void setRank(int rank) {
		this.rank = rank;
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

	public String getUrl() {
		return url;
	}

	public String getCount() {
		return count;
	}
}
