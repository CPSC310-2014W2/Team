package com.google.gwt.foodonwheels.server;

import java.io.Serializable;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.gwt.maps.client.geom.LatLng;

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
	private String phone;
	@Persistent
	private String cuisine;
	@Persistent
	private String imageUrl;
	@Persistent
	private String website;
	@Persistent
	private LatLng geoLocation;
	@Persistent
	private int score;


	public FoodTruck(String name, String address) {
		super();
		this.name = name;
		this.address = address;
		this.phone = "";
		this.cuisine = "";
		this.imageUrl = "";
		this.website = "";
	}

	public FoodTruck(String name, String address, String phone, String cuisine,
			String imageUrl, String website, double latitude, double longitude) {
		super();
		this.name = name;
		this.address = address;
		this.phone = phone;
		this.cuisine = cuisine;
		this.imageUrl = imageUrl;
		this.website = website;
		this.geoLocation = LatLng.newInstance(latitude, longitude);
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getAddress() {
		return address;
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

	public LatLng getGeoLocation() {
		return geoLocation;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

}
