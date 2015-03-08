package com.google.gwt.foodonwheels.client;

import com.google.gwt.foodonwheels.shared.FieldVerifier;
import com.google.gwt.maps.client.InfoWindowContent;
import com.google.gwt.maps.client.MapWidget;
import com.google.gwt.maps.client.Maps;
import com.google.gwt.maps.client.control.LargeMapControl;
import com.google.gwt.maps.client.geom.LatLng;
import com.google.gwt.maps.client.overlay.Marker;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class FoodOnWheels implements EntryPoint {
	
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
	    

	    final MapWidget map = new MapWidget(Vancouver, 16);
	    map.setSize("500px", "400px");
	    // Add some controls for the zoom level
	    map.addControl(new LargeMapControl());

	    // Add a marker
	    map.addOverlay(new Marker(Vancouver));

	    // Add an info window to highlight a point of interest
	        map.getInfoWindow().open(map.getCenter(),
	        new InfoWindowContent("Downtown Vancouver"));

	   // final DockLayoutPanel dock = new DockLayoutPanel(Unit.PX);
	   // dock.addNorth(map, 500);

	    // Add the map to the HTML host page
	    RootPanel.get("map-placement").add(map);
	  }
	}