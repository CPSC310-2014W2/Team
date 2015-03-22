package com.google.gwt.foodonwheels.client;

import com.google.gwt.core.client.Callback;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.geolocation.client.Geolocation;
import com.google.gwt.geolocation.client.Position;
import com.google.gwt.geolocation.client.PositionError;
import com.google.gwt.maps.client.InfoWindowContent;
import com.google.gwt.maps.client.MapWidget;
import com.google.gwt.maps.client.Maps;
import com.google.gwt.maps.client.control.LargeMapControl;
import com.google.gwt.maps.client.geom.LatLng;
import com.google.gwt.maps.client.overlay.Marker;
import com.google.gwt.user.client.ui.RootPanel;



public class FoodCartMap implements EntryPoint{
	
	private boolean mapCanvas;
	

	public FoodCartMap(boolean newMap){
		
		this.mapCanvas = newMap;
	}
	
	public boolean getMap(){
		return mapCanvas;
	}
	
	public void setMap(boolean newMap){
		 this.mapCanvas = newMap;
	}
	
	@Override
	public void onModuleLoad() {
		/*
		 * Asynchronously loads the Maps API.
		 *
		 * The first parameter should be a valid Maps API Key to deploy this
		 * application on a public server, but a blank key will work for an
		 * application served from localhost.
		 */

	//The following is responsible for creating  map
	Maps.loadMapsApi("AIzaSyCRBwxpSxylsp96KCX96xRHUQxrY6e653I", "2", false, new Runnable() {
			public void run() {
				buildUi();
			}
		});
		
	}
	
	
	public void buildUi() {
		
		System.out.println("Build function started");
		// Open a map centered on Vancouver
		LatLng Vancouver = LatLng.newInstance(49.2827, -123.1207);

		final MapWidget map = new MapWidget(Vancouver, 16);
		map.setSize("500px", "400px");
		// Add some controls for the zoom level
		map.addControl(new LargeMapControl());

		// Add a marker
		map.addOverlay(new Marker(Vancouver));


		//Gets current user position

		Geolocation.getIfSupported().getCurrentPosition(new  Callback<Position, PositionError>(){

			@Override
			public void onFailure(PositionError reason) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onSuccess(Position result) {
				// TODO Auto-generated method stub
				com.google.gwt.geolocation.client.Position.Coordinates userLoc = result.getCoordinates();
				LatLng userLocation = LatLng.newInstance(userLoc.getLatitude(), userLoc.getLongitude());
				map.addOverlay(new Marker(userLocation));
			}
		});

		// Add an info window to highlight a point of interest
		map.getInfoWindow().open(map.getCenter(),
				new InfoWindowContent("Downtown Vancouver"));

		// final DockLayoutPanel dock = new DockLayoutPanel(Unit.PX);
		// dock.addNorth(map, 500);
		// Add the map to the HTML host page
		RootPanel.get("map-placement").add(map);
		System.out.println("build function ran");
	}

	

}
