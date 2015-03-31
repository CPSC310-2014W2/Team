package com.google.gwt.foodonwheels.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import com.gargoylesoftware.htmlunit.javascript.host.geo.Coordinates;
import com.google.gwt.foodonwheels.shared.FieldVerifier;
import com.google.gwt.foodonwheels.shared.FoodTruckData;
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
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DecoratorPanel;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.view.client.ListDataProvider;
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
	private Button fetchTruckListButton = new Button("get data from provider");
	private HorizontalPanel searchPanel = new HorizontalPanel();
	private Label searchLabel = new Label("Search food trucks by name:");
	private SearchTextBox filterBox = new SearchTextBox();

	//	private final ListDataProvider<FoodTruckData> 
	//	truckDataProvider = new ListDataProvider<FoodTruckData>();

	private final FilteredListDataProvider<FoodTruckData>
	truckDataProvider = new FilteredListDataProvider<FoodTruckData>(
			new IFilter<FoodTruckData>() {
				/* (non-Javadoc)
				 * @see com.google.gwt.foodonwheels.client.IFilter#
				 * isValid(java.lang.Object, java.lang.String)
				 * 
				 * A FoodTruckData object is valid if its name contains
				 * the search keyword.
				 */
				@Override
				public boolean isValid(FoodTruckData value, String keyword) {
					if(keyword==null || value==null) {
						return true;
					}
					else {
						boolean isHit = value.getName().trim().toLowerCase()
								.contains(keyword.trim().toLowerCase());
						return isHit;
					}
				}
			});

	private TabLayoutPanel tabLayout = new TabLayoutPanel(2.5, Unit.EM);

	private CellTable<FoodTruckData> 
	truckCellTable = new CellTable<FoodTruckData>(50);
	private ScrollPanel scrollPanel = new ScrollPanel(truckCellTable);
	private ScrollPanel favScrollPanel = new ScrollPanel();

	//	private CellList<String> truckCellList = 
	//			new CellList<String>(new TextCell());
	private Label lastUpdatedLabel = new Label();
	private LoginInfo loginInfo = null;
	private VerticalPanel loginPanel = new VerticalPanel();
	private Anchor signInLink = new Anchor("Sign In");
	private Anchor signOutLink = new Anchor("Sign Out");
	private Label loginLabel = new Label(
			"Sign in to customize your favourite food vendors list!");

	/**
	 * Remote remote service proxy to talk to the server-side FoodTruckService
	 */
	private final FoodTruckServiceAsync 
	foodTruckService = GWT.create(FoodTruckService.class);

	//Needed to create FoodCartMap object
	FoodCartMap cartMap = new FoodCartMap(true);

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {

		// Check login status using login service.
		LoginServiceAsync loginService = GWT.create(LoginService.class);
		loginService.login(GWT.getHostPageBaseURL(),
				new AsyncCallback<LoginInfo>() {
			public void onFailure(Throwable error) {
				Window.alert(SERVER_ERROR);
			}

			public void onSuccess(LoginInfo result) {
				loginInfo = result;
				if (loginInfo.isLoggedIn()) {
					if (loginInfo.isUserAdmin())
						setAdminView();
					else
						setUserView();
				} else {
					loadLogin();
				}
			}
		});

		//Needed to load map

		Maps.loadMapsApi("AIzaSyCRBwxpSxylsp96KCX96xRHUQxrY6e653I", "2", false, new Runnable() {
			public void run() {
				cartMap.buildUi();

			}});

		//		setUpTruckCellTable();

		//		truckCellList.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.ENABLED);
		//		// Add a selection model to handle user selection.
		//		final SingleSelectionModel<String> selectionModel = new SingleSelectionModel<String>();
		//		truckCellList.setSelectionModel(selectionModel);
		//
		//		selectionModel.addSelectionChangeHandler(
		//				new SelectionChangeEvent.Handler() {
		//					public void onSelectionChange
		//					(SelectionChangeEvent event) {
		//						String selected = selectionModel.getSelectedObject();
		//						if (selected != null) {
		//							Window.alert("You selected: " + selected);
		//						}
		//					}
		//				});
	}

	private void setUpTruckCellTable() {
		truckDataProvider.addDataDisplay(truckCellTable);
		filterBox.addValueChangeHandler(new IStringValueChanged() {
			/* (non-Javadoc)
			 * @see com.google.gwt.foodonwheels.client.IStringValueChanged#
			 * valueChanged(java.lang.String)
			 * 
			 * Use the keyword entered by user in SearchTextBox 
			 * to filter the FoodTruckData objects.
			 */
			@Override
			public void valueChanged(String newValue) {
				truckDataProvider.setFilter(newValue);
				truckDataProvider.refresh();
			}
		});

		truckCellTable.setAutoHeaderRefreshDisabled(true);
		truckCellTable
		.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.DISABLED);

		// Attach a column sort handler to the ListDataProvider to sort the list.
		ListHandler<FoodTruckData> 
		sortHandler = new ListHandler<FoodTruckData>(truckDataProvider.getList());
		truckCellTable.addColumnSortHandler(sortHandler);

		// Add a text column to show the name.
		TextColumn<FoodTruckData> 
		nameColumn = new TextColumn<FoodTruckData>() {
			@Override
			public String getValue(FoodTruckData object) {
				return object.getName();
			}
		};
		nameColumn.setSortable(true);

		// Set comparator used for sorting the name column.
		sortHandler.setComparator(nameColumn, 
				new Comparator<FoodTruckData>() {
			@Override
			public int compare(FoodTruckData o1,
					FoodTruckData o2) {
				return o1.getName().compareTo(o2.getName());
			}
		});
		truckCellTable.addColumn(nameColumn, "Food Truck Name");

		// Add a text column to show the number of user checkin.
		TextColumn<FoodTruckData> 
		userCountColumn = new TextColumn<FoodTruckData>() {
			@Override
			public String getValue(FoodTruckData object) {
				return object.getCount();
			}
		};
		userCountColumn.setSortable(true);

		// Set comparator used for sorting the user checkin count column.
		sortHandler.setComparator(userCountColumn, 
				new Comparator<FoodTruckData>(){
			@Override
			public int compare(FoodTruckData o1,
					FoodTruckData o2) {
				int diff = Integer.parseInt(o1.getCount()) 
						- Integer.parseInt(o2.getCount());
				return Integer.signum(diff);
			}
		});
		truckCellTable.addColumn(userCountColumn,"Checkin Count");

		// Add a selection model to handle user selection.
		final SingleSelectionModel<FoodTruckData> 
		selectionModel = new SingleSelectionModel<FoodTruckData>();

		truckCellTable.setSelectionModel(selectionModel);

		selectionModel.addSelectionChangeHandler(
				new SelectionChangeEvent.Handler() {

					@Override
					public void onSelectionChange(SelectionChangeEvent event) {
						// TODO Auto-generated method stub
						FoodTruckData 
						selected = selectionModel.getSelectedObject();

						if (selected != null) {
							//		Window.alert("You selected: " + selected);
							cartMap.openNewInfoWindow(selected);

						}
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

	private void loadFoodTruckData() {
		foodTruckService.getFoodTruckDataList(
				new AsyncCallback<List<FoodTruckData>>() {

					@Override
					public void onFailure(Throwable caught) {
						Window.alert("Failed to get truck data from server.");
					}

					@Override
					public void onSuccess(List<FoodTruckData> result) {
						//TODO
						//						List<String> values = new ArrayList<String>();
						//						for (int k = 0; k < result.size(); k++)
						//							values.add(result.get(k).toString());
						//						truckCellList.setRowCount(values.size(), true);
						//						truckCellList.setRowData(0, values);

						truckDataProvider.setList(result);
						setUpTruckCellTable();
						//						truckCellTable.setRowCount(values.size(), true);
						//						truckCellTable.setRowData(0, result);
					}
				});
	}
	
	private void setAdminView() {
		truckListPanel.add(fetchTruckListButton);
		// Listen for mouse events on the fetch YELP data button.
		fetchTruckListButton.addClickHandler(
				new ClickHandler() {
					public void onClick(ClickEvent event) {
						fetchDataFromProvider();
					};
				});
		setUserView();
	}

	private void setUserView() {
		loadFoodTruckData();
		setTruckListPanel();
	}

	private void setTruckListPanel() {
		searchPanel.add(searchLabel);
		searchPanel.add(filterBox);
		truckListPanel.add(searchPanel);
		scrollPanel.setSize("500px", "400px");
		tabLayout.add(scrollPanel, "All Food Vendors");
		tabLayout.add(favScrollPanel, "Favourites");
		tabLayout.setAnimationDuration(1000);
		tabLayout.getElement().getStyle().setMargin(10, Unit.PX);
		tabLayout.setSize("500px", "400px");
		truckListPanel.add(tabLayout);
		//		truckListPanel.add(truckCellList);
		truckListPanel.add(lastUpdatedLabel);

		// Set up sign out hyperlink.
		signOutLink.setHref(loginInfo.getLogoutUrl());
		truckListPanel.add(signOutLink);

		// Add it to the root panel.
		RootPanel.get("foodTruckList").add(truckListPanel);
	}

	/**
	 * Fetch food truck data from provider. Executed when the user clicks
	 * fetchTruckListButton. This should be only accessible to the admin.
	 */
	private void fetchDataFromProvider() {
		foodTruckService.fetchFoodTruckDataFromFourSquare(
				new AsyncCallback<Void>() {

					@Override
					public void onFailure(Throwable caught) {
						Window.alert(caught.getMessage());
					}

					@Override
					public void onSuccess(Void result) {
						Window.alert("Food truck data from provider stored " 
								+ "into app engine datastore.");
						setUserView();
					}
				});
	}
}
