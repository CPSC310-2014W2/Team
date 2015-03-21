package com.google.gwt.foodonwheels.shared;

import java.io.Serializable;

public class FoodTruckData implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final int UNDEFINED_RANK = 0;
	private String name;
	private String address;
	private double latitude;
	private double longitude;
	private int rank;
	
	public FoodTruckData() {}

	public FoodTruckData(String name, String address, 
			double latitude, double longitude, int rank) {
		this.name = name;
		this.address = address;
		this.latitude = latitude;
		this.longitude = longitude;
		this.rank = rank;
	}

	public FoodTruckData(String name, String address, 
			double latitude, double longitude) {
		this(name, address, latitude, longitude, UNDEFINED_RANK);
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

}
