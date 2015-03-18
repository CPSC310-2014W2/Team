package com.google.gwt.foodonwheels.client;

import java.util.ArrayList;

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
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.cellview.client.CellTable;
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
	private FlexTable truckFlexTable = new FlexTable();
	private HorizontalPanel addPanel = new HorizontalPanel();
	private TextBox newSymbolTextBox = new TextBox();
	private Button addStockButton = new Button("Add");
	private Label lastUpdatedLabel = new Label();
	private ArrayList<String> favourites = new ArrayList<String>();
	private LoginInfo loginInfo = null;
	private VerticalPanel loginPanel = new VerticalPanel();
	private Label loginLabel = new Label(
			"Please sign in to your Google Account to access the StockWatcher application.");
	private Anchor signInLink = new Anchor("Sign In");

	public void onModuleLoad() {

		VerticalPanel verticalPanel=new VerticalPanel();
        TextBox filterBox=new TextBox();
        verticalPanel.add(filterBox);
        
        //CellTable<Person> cell=new CellTable<Person>();
        
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
		/*
		 * Table for the list of food vendors
		 */
		// Create table for stock data.
		truckFlexTable.setText(0, 0, "Name");
		truckFlexTable.setText(0, 1, "Address");
		truckFlexTable.setText(0, 2, "Phone");
		truckFlexTable.setText(0, 3, "Favourite");

		// Assemble Add Stock panel.
		addPanel.add(newSymbolTextBox);
		addPanel.add(addStockButton);

		// Assemble Main panel.
		mainPanel.add(truckFlexTable);
		mainPanel.add(addPanel);
		mainPanel.add(lastUpdatedLabel);

		// Associate the Main panel with the HTML host page.
		RootPanel.get("truckList").add(mainPanel);

		// Move cursor focus to the input box.
		newSymbolTextBox.setFocus(true);

		// Listen for mouse events on the Add button.
		addStockButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				addStock();
			}
		});

		// Listen for keyboard events in the input box.
		newSymbolTextBox.addKeyDownHandler(new KeyDownHandler() {
			public void onKeyDown(KeyDownEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
					addStock();
				}
			}
		});
		// Check login status using login service.
		LoginServiceAsync loginService = GWT.create(LoginService.class);
		loginService.login(GWT.getHostPageBaseURL(),
				new AsyncCallback<LoginInfo>() {
					public void onFailure(Throwable error) {
					}

					public void onSuccess(LoginInfo result) {
						loginInfo = result;
						if (loginInfo.isLoggedIn()) {
							// loadStockWatcher();
						} else {
							loadLogin();
						}
					}
				});
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

	/**
	 * Add stock to FlexTable. Executed when the user clicks the addStockButton
	 * or presses enter in the newSymbolTextBox.
	 */
	private void addStock() {
	    final String symbol = newSymbolTextBox.getText().toUpperCase().trim();
	    newSymbolTextBox.setFocus(true);
	    
	    // Stock code must be between 1 and 10 chars that are numbers, letters, or dots.
	    if (!symbol.matches("^[0-9A-Z&#92;&#92;.]{1,10}$")) {
	      Window.alert("'" + symbol + "' is not a valid symbol.");
	      newSymbolTextBox.selectAll();
	      return;
	    }
	    
	    newSymbolTextBox.setText("");
	 }

	private void loadLogin() {
		// Assemble login panel.
		signInLink.setHref(loginInfo.getLoginUrl());
		loginPanel.add(loginLabel);
		loginPanel.add(signInLink);
		RootPanel.get("stockList").add(loginPanel);
	}

}