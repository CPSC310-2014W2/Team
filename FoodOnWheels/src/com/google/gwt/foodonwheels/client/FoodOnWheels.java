package com.google.gwt.foodonwheels.client;

import java.util.Arrays;
import java.util.List;

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
import com.google.gwt.foodonwheels.server.FoodTruck;
import com.google.gwt.foodonwheels.shared.FieldVerifier;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class FoodOnWheels implements EntryPoint {
	/**
	 * The message displayed to the user when the server cannot be reached or
	 * returns an error.
	 */
	private static final String SERVER_ERROR = "An error occurred while "
			+ "attempting to contact the server. Please check your network "
			+ "connection and try again.";

	// private HorizontalPanel mainPanel = new HorizontalPanel();
	private VerticalPanel truckListPanel = new VerticalPanel();
	private Button fetchTruckListButton = new Button("fetch YELP data");
	private CellList<String> truckCellList = new CellList<String>(new TextCell());
	private Label lastUpdatedLabel = new Label();
	private LoginInfo loginInfo = null;
	private VerticalPanel loginPanel = new VerticalPanel();
	private Anchor signInLink = new Anchor("Sign In");
	private Anchor signOutLink = new Anchor("Sign Out");
	private Label loginLabel = new Label(
			"Please sign in to see the list of food vendors");

	private final FoodTruckServiceAsync foodTruckService = GWT
			.create(FoodTruckService.class);

	/**
	 * The list of data to display.
	 */
	private static final List<String> DAYS = Arrays.asList("Sunday\r\nagain",
			"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday");

	/**
	 * This is the entry point method.
	 */
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
							loadFoodTruckList();
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

		truckCellList
				.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.ENABLED);
		// Add a selection model to handle user selection.
		final SingleSelectionModel<String> selectionModel = new SingleSelectionModel<String>();
		truckCellList.setSelectionModel(selectionModel);

		selectionModel
				.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
					public void onSelectionChange(SelectionChangeEvent event) {
						String selected = selectionModel.getSelectedObject();
						if (selected != null) {
							Window.alert("You selected: " + selected);
						}
					}
				});

		loadFoodTruckList();

		// // Set the total row count. This isn't strictly necessary, but it
		// affects
		// // paging calculations, so its good habit to keep the row count up to
		// date.
		// truckCellList.setRowCount(DAYS.size(), true);
		//
		// // Push the data into the widget.
		// truckCellList.setRowData(0, DAYS);

		truckListPanel.add(fetchTruckListButton);
		truckListPanel.add(truckCellList);
		truckListPanel.add(lastUpdatedLabel);
		// mainPanel.add(truckListPanel);
		// Add it to the root panel.
		RootPanel.get("foodTruckList").add(truckListPanel);

		// Listen for mouse events on the fetch YELP data button.
		fetchTruckListButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				fetchYelpData();
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

	/**
	 * Fetch food truck data from YELP. Executed when the user clicks
	 * fetchTruckListButton.
	 */
	private void fetchYelpData() {
		// TODO Auto-generated method stub
		// // Set the total row count. This isn't strictly necessary, but it
		// affects
		// // paging calculations, so its good habit to keep the row count up to
		// date.
		// truckCellList.setRowCount(DAYS.size(), true);
		//
		// // Push the data into the widget.
		// truckCellList.setRowData(0, DAYS);
		foodTruckService.fetchFoodTruckDataFromYelp(new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub
				Window.alert(caught.getMessage());
			}

			@Override
			public void onSuccess(Void result) {
				// TODO Auto-generated method stub
				Window.alert("Results from Yelp stored into server.");
			}
		});
	}

	private void loadFoodTruckList() {
		
	    // Set up sign out hyperlink.
	    signOutLink.setHref(loginInfo.getLogoutUrl());
	    
	    // Assemble Main panel.
	    truckListPanel.add(signOutLink);
	    
		foodTruckService.getFoodTruckList(new AsyncCallback<List<String>>() {

			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub
				// Set the total row count. This isn't strictly necessary, but
				// it affects
				// paging calculations, so its good habit to keep the row count
				// up to date.
				truckCellList.setRowCount(DAYS.size(), true);

				// Push the data into the widget.
				truckCellList.setRowData(0, DAYS);
			}

			@Override
			public void onSuccess(List<String> result) {
				// TODO Auto-generated method stub
				// Set the total row count. This isn't strictly necessary, but
				// it affects
				// paging calculations, so its good habit to keep the row count
				// up to date.
				truckCellList.setRowCount(result.size(), true);

				// Push the data into the widget.
				truckCellList.setRowData(0, result);
			}
		});
	}

	private void loadLogin() {
		// Assemble login panel.
		signInLink.setHref(loginInfo.getLoginUrl());
		loginPanel.add(loginLabel);
		loginPanel.add(signInLink);
		RootPanel.get("foodTruckList").add(loginPanel);
	}
}