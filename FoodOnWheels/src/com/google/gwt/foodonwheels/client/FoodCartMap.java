package com.google.gwt.foodonwheels.client;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.Callback;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.foodonwheels.shared.FoodTruckData;
import com.google.gwt.geolocation.client.Geolocation;
import com.google.gwt.geolocation.client.Position;
import com.google.gwt.geolocation.client.PositionError;
import com.google.gwt.maps.client.InfoWindow;
import com.google.gwt.maps.client.InfoWindowContent;
import com.google.gwt.maps.client.MapWidget;
import com.google.gwt.maps.client.Maps;
import com.google.gwt.maps.client.control.LargeMapControl;
import com.google.gwt.maps.client.event.MarkerClickHandler;
import com.google.gwt.maps.client.geom.LatLng;
import com.google.gwt.maps.client.geom.Point;
import com.google.gwt.maps.client.geom.Size;
import com.google.gwt.maps.client.overlay.Icon;
import com.google.gwt.maps.client.overlay.Marker;
import com.google.gwt.maps.client.overlay.MarkerOptions;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.RootPanel;



public class FoodCartMap {
	
	private boolean mapCanvas;
	private final FoodTruckServiceAsync foodTruckService = 
			GWT.create(FoodTruckService.class);
	private MapWidget currentMap;
	LatLng userPos = null;
	
	
	//new icon to display user on map
	
	
	
	public FoodCartMap(boolean newMap){
		
		this.mapCanvas = newMap;
	}
	
	public boolean getMap(){
		return mapCanvas;
	}
	
	public void setMap(boolean newMap){
		 this.mapCanvas = newMap;
	}
	

		/*
		 * Asynchronously loads the Maps API.
		 *
		 * The first parameter should be a valid Maps API Key to deploy this
		 * application on a public server, but a blank key will work for an
		 * application served from localhost.
		 */


	
	
	public void buildUi() {
		
		System.out.println("Build function started");
		// Open a map centered on Vancouver
		LatLng Vancouver = LatLng.newInstance(49.2827, -123.1207);

		final MapWidget map = new MapWidget(Vancouver, 16);
		map.setSize("500px", "400px");
		// Add some controls for the zoom level
		map.addControl(new LargeMapControl());

		// Add a marker
		//map.addOverlay(new Marker(Vancouver));
		
		
		
		
		//Get Foodtruck location and plot them on map
 
		foodTruckService
		.getFoodTruckDataList(new AsyncCallback<List<FoodTruckData>>() {

			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onSuccess(List<FoodTruckData> result) {
				// TODO Auto-generated method stub
				for (int k = 0; k < result.size(); k++){
					final LatLng cartLatLng =LatLng.newInstance(result.get(k).getLatitude(), result.get(k).getLongitude());
					Marker cartMarker = new Marker(cartLatLng);
					map.addOverlay(cartMarker);
					final InfoWindowContent cartDescription = cartInfo(result.get(k));
						cartMarker.addMarkerClickHandler(new MarkerClickHandler(){
						@Override
						public void onClick(MarkerClickEvent event) {
							// TODO Auto-generated method stub						 
							 map.getInfoWindow().open(cartLatLng, cartDescription);
						}
					});
					 				
				}

			}
		});


		//Gets current user position

		Geolocation.getIfSupported().getCurrentPosition(new  Callback<Position, PositionError>(){

			@Override
			public void onFailure(PositionError reason) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onSuccess(Position result) {
				// Displays userlocation
				Icon userImg = Icon.newInstance("https://lh4.ggpht.com/fHB6uUwZst-SodiFAXEvZ2Ve2fV-3MoZqFPEcf9hs4n1ctrkYcX_BPMOa1eV9pR9Mw=w170");
				userImg.setIconSize(Size.newInstance(30, 35));
				userImg.setIconAnchor(Point.newInstance(8,20));
				userImg.setInfoWindowAnchor(Point.newInstance(1, 5));
				
				MarkerOptions userDisplay = MarkerOptions.newInstance();
				userDisplay.setIcon(userImg);
				
				com.google.gwt.geolocation.client.Position.Coordinates userLoc = result.getCoordinates();
				LatLng userLocation = LatLng.newInstance(userLoc.getLatitude(), userLoc.getLongitude());
				map.addOverlay(new Marker(userLocation, userDisplay));
				
				userPos = userLocation;
				
				 
			}
		});
		
		
		


        currentMap = map;
		RootPanel.get("map-placement").add(map);
		System.out.println("build function ran");
	}
	
//Method to return cart information given a food card.
	public InfoWindowContent cartInfo(FoodTruckData cart){
		return 
	new InfoWindowContent("<p>" + "<strong>" + cart.getName()+ "</strong> " + "<br>" + checkAddress(cart)+ 
									"<br>"+"Check-in count: "+ cart.getCount()+ "<br>" + "Website: " + cart.getUrl() + "<br>"+"Rank = " + cart.getRank()+ "</p>");
		
		
	}
	
//Method to check if  address field of FoodDataCart is null
	public String checkAddress(FoodTruckData address){
		if(address.getAddress().length() == 0){
			return "(No address in database!)";
		}
		return "" + address.getAddress();}
	

//Method opens info window at specific cart location and provides details of cart.
	public void openNewInfoWindow(FoodTruckData cart){
		final InfoWindowContent cartDescription =  cartInfo(cart);
		final LatLng cartLatLng =LatLng.newInstance(cart.getLatitude(), cart.getLongitude());
		currentMap.getInfoWindow().open(cartLatLng, cartDescription);
		
			
	}
	
	
	//Returns cart distance from user
	public double distanceFromUser(FoodTruckData cart){
		final LatLng cartLatLng =LatLng.newInstance(cart.getLatitude(), cart.getLongitude());		
		return userPos.distanceFrom(cartLatLng);}
	

}