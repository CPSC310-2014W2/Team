package com.google.gwt.foodonwheels.client;

import com.gargoylesoftware.htmlunit.javascript.host.geo.Coordinates;
import com.google.gwt.foodonwheels.shared.FieldVerifier;
import com.google.gwt.geolocation.client.Geolocation;
import com.google.gwt.geolocation.client.Position;
import com.google.gwt.geolocation.client.PositionError;
import com.google.gwt.maps.client.InfoWindowContent;
import com.google.gwt.maps.client.MapWidget;
import com.google.gwt.maps.client.Maps;
import com.google.gwt.maps.client.control.LargeMapControl;
import com.google.gwt.maps.client.geom.LatLng;
import com.google.gwt.maps.client.overlay.Marker;
import com.google.gwt.core.client.Callback;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
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
	private VerticalPanel mainPanel = new VerticalPanel();
	private FlexTable listFlexTable = new FlexTable();
	private HorizontalPanel addPanel = new HorizontalPanel();
	private TextBox newSymbolTextBox = new TextBox();
	private Button addStockButton = new Button("Add");
	private Label lastUpdatedLabel = new Label();
	private LoginInfo loginInfo = null;
	private VerticalPanel loginPanel = new VerticalPanel();
	private Label loginLabel = new Label(
			"Sign in to your Google Account to make your favourite Food Truck list!");
	private Anchor signInLink = new Anchor("Sign In");
	private Anchor signOutLink = new Anchor("Sign Out");

	public void onModuleLoad() {

		// Check login status using login service.
		LoginServiceAsync loginService = GWT.create(LoginService.class);
		loginService.login(GWT.getHostPageBaseURL(),
				new AsyncCallback<LoginInfo>() {
					public void onFailure(Throwable error) {
					}

					public void onSuccess(LoginInfo result) {
						loginInfo = result;
						if (loginInfo.isLoggedIn()) {
							buildUi();
						} else {
							loadLogin();
						}
					}
				});

		/*
		 * Asynchronously loads the Maps API.
		 * 
		 * The first parameter should be a valid Maps API Key to deploy this
		 * application on a public server, but a blank key will work for an
		 * application served from localhost.
		 */
		Maps.loadMapsApi("AIzaSyCRBwxpSxylsp96KCX96xRHUQxrY6e653I", "2", false,
				new Runnable() {
					public void run() {
						buildUi();
					}
				});
	}

	private void loadFoodOnWheels() {
		// Set up sign out hyperlink.
		signOutLink.setHref(loginInfo.getLogoutUrl());

		// Create table for favourite trucks.
		listFlexTable.setText(0, 0, "Name");
		listFlexTable.setText(0, 1, "Address");
		
		// Add styles to elements in the stock list table.
		listFlexTable.getRowFormatter().addStyleName(0, "watchListHeader");
		listFlexTable.addStyleName("watchList");
		listFlexTable.setCellPadding(3);
		listFlexTable.getCellFormatter().addStyleName(0, 1,
				"watchListNumericColumn");
		listFlexTable.getCellFormatter().addStyleName(0, 2,
				"watchListNumericColumn");
		listFlexTable.getCellFormatter().addStyleName(0, 3,
				"watchListRemoveColumn");
		
		// Assemble Main panel.
	    mainPanel.add(signOutLink);
	    mainPanel.add(listFlexTable);
	    mainPanel.add(addPanel);
	    mainPanel.add(lastUpdatedLabel);
	}

	private void loadLogin() {
		// Assemble login panel.
		signInLink.setHref(loginInfo.getLoginUrl());
		loginPanel.add(loginLabel);
		loginPanel.add(signInLink);
		RootPanel.get("favouriteTruckList").add(loginPanel);
	}

	private void buildUi() {

		// Assemble Main panel.
		// mainPanel.add(signOutLink);

		// Open a map centered on Vancouver
		LatLng Vancouver = LatLng.newInstance(49.2827, -123.1207);

		final MapWidget map = new MapWidget(Vancouver, 16);
		map.setSize("500px", "400px");
		// Add some controls for the zoom level
		map.addControl(new LargeMapControl());

		// Add a marker
		map.addOverlay(new Marker(Vancouver));

		// Gets current user position
		Geolocation.getIfSupported().getCurrentPosition(
				new Callback<Position, PositionError>() {

					@Override
					public void onFailure(PositionError reason) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onSuccess(Position result) {
						// TODO Auto-generated method stub
						com.google.gwt.geolocation.client.Position.Coordinates userLoc = result
								.getCoordinates();
						LatLng userLocation = LatLng.newInstance(
								userLoc.getLatitude(), userLoc.getLongitude());
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
	}
}