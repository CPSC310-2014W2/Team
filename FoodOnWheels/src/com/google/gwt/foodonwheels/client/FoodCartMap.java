package com.google.gwt.foodonwheels.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.maps.client.InfoWindowContent;
import com.google.gwt.maps.client.MapWidget;
import com.google.gwt.maps.client.Maps;
import com.google.gwt.maps.client.control.LargeMapControl;
import com.google.gwt.maps.client.geom.LatLng;
import com.google.gwt.maps.client.overlay.Marker;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.RootLayoutPanel;

public class FoodCartMap implements EntryPoint{

	 // GWT module entry point method.
	  public void onModuleLoad() {
	   /*
	    * Asynchronously loads the Maps API.
	    *
	    * The first parameter should be a valid Maps API Key to deploy this
	    * application on a public server, but a blank key will work for an
	    * application served from localhost.
	   */
	   Maps.loadMapsApi("AIzaSyCRBwxpSxylsp96KCX96xRHUQxrY6e653I", "2", false, new Runnable() {
	      public void run() {
	        buildUi();
	      }
	    });
	  }

	  private void buildUi() {
	    // Open a map centered on Vancouver
	    LatLng Vancouver = LatLng.newInstance(49.2827, -123.1207);

	    final MapWidget map = new MapWidget(Vancouver, 12);
	    map.setSize("50%", "50%");
	    // Add some controls for the zoom level
	    map.addControl(new LargeMapControl());

	    // Add a marker
	    map.addOverlay(new Marker(Vancouver));

	    // Add an info window to highlight a point of interest
	    map.getInfoWindow().open(map.getCenter(),
	        new InfoWindowContent("Vancouver"));

	    final DockLayoutPanel dock = new DockLayoutPanel(Unit.PX);
	    dock.addNorth(map, 500);

	    // Add the map to the HTML host page
	    RootLayoutPanel.get().add(dock);
	  }
	}