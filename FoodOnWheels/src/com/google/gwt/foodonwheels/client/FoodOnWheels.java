package com.google.gwt.foodonwheels.client;

import java.util.Arrays;
import java.util.List;

import com.google.gwt.foodonwheels.server.FoodTruck;
import com.google.gwt.foodonwheels.shared.FieldVerifier;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
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

	/**
	 * Create a remote service proxy to talk to the server-side Greeting service.
	 */
	private final GreetingServiceAsync greetingService = GWT
			.create(GreetingService.class);

	//	private HorizontalPanel mainPanel = new HorizontalPanel();
	private VerticalPanel truckListPanel = new VerticalPanel();
	private Button fetchTruckListButton = new Button("fetch YELP data");
	private CellList<String> truckCellList = 
			new CellList<String>(new TextCell());
	private Label lastUpdatedLabel = new Label();

	private final FoodTruckServiceAsync foodTruckService = 
			GWT.create(FoodTruckService.class);

	/**
	 * The list of data to display.
	 */
	private static final List<String> DAYS = 
			Arrays.asList("Sunday\r\nagain", "Monday",
					"Tuesday", "Wednesday", "Thursday", "Friday", "Saturday");

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		truckCellList.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.ENABLED);
		// Add a selection model to handle user selection.
		final SingleSelectionModel<String> selectionModel = 
				new SingleSelectionModel<String>();
		truckCellList.setSelectionModel(selectionModel);

		selectionModel
		.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
			public void onSelectionChange
			(SelectionChangeEvent event) {
				String selected = selectionModel.getSelectedObject();
				if (selected != null) {
					Window.alert("You selected: " + selected);
				}
			}
		});

		loadFoodTruckList();

		//		// Set the total row count. This isn't strictly necessary, but it affects
		//		// paging calculations, so its good habit to keep the row count up to date.
		//		truckCellList.setRowCount(DAYS.size(), true);
		//
		//		// Push the data into the widget.
		//		truckCellList.setRowData(0, DAYS);

		truckListPanel.add(fetchTruckListButton);
		truckListPanel.add(truckCellList);
		truckListPanel.add(lastUpdatedLabel);
		//	      mainPanel.add(truckListPanel);
		// Add it to the root panel.
		RootPanel.get("foodTruckList").add(truckListPanel);

		// Listen for mouse events on the fetch YELP data button.
		fetchTruckListButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				fetchYelpData();
			}
		});

	}

	/**
	 * Fetch food truck data from YELP. Executed when the user clicks
	 * fetchTruckListButton.
	 */
	private void fetchYelpData() {
		// TODO Auto-generated method stub
		//		// Set the total row count. This isn't strictly necessary, but it affects
		//		// paging calculations, so its good habit to keep the row count up to date.
		//		truckCellList.setRowCount(DAYS.size(), true);
		//
		//		// Push the data into the widget.
		//		truckCellList.setRowData(0, DAYS);
		foodTruckService
		.fetchFoodTruckDataFromYelp(new AsyncCallback<Void>() {

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

//		foodTruckService
//		.addFoodTruck("abc", "123 def", new AsyncCallback<Void>() {
//			public void onFailure(Throwable error) {
//			}
//			public void onSuccess(Void ignore) {
//				Window.alert("Correctly added data");
//			}
//		});

	}

	private void loadFoodTruckList() {
		foodTruckService.getFoodTruckList(new AsyncCallback<List<String>>() {

			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub
				// Set the total row count. This isn't strictly necessary, but it affects
				// paging calculations, so its good habit to keep the row count up to date.
				truckCellList.setRowCount(DAYS.size(), true);

				// Push the data into the widget.
				truckCellList.setRowData(0, DAYS);
			}

			@Override
			public void onSuccess(List<String> result) {
				// TODO Auto-generated method stub
				// Set the total row count. This isn't strictly necessary, but it affects
				// paging calculations, so its good habit to keep the row count up to date.
				truckCellList.setRowCount(result.size(), true);

				// Push the data into the widget.
				truckCellList.setRowData(0, result);

			}

		});
	}

}
