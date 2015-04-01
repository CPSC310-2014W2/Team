package com.google.gwt.foodonwheels.client;

import java.util.Comparator;
import java.util.List;

import com.google.gwt.foodonwheels.shared.FoodTruckData;
import com.google.gwt.maps.client.Maps;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
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

	private VerticalPanel truckListPanel = new VerticalPanel();
	// Button for admin to get data into app engine
	private Button fetchTruckListButton = new Button("get data from provider");
	private List<FoodTruckData> allTrucks;

	// Search panel and parts
	private HorizontalPanel searchPanel = new HorizontalPanel();
	private Label searchLabel = new Label("Search food trucks by name:");
	private SearchTextBox filterBox = new SearchTextBox();
	private TabLayoutPanel tabLayout = new TabLayoutPanel(2.5, Unit.EM);

	private HorizontalPanel closeByPanel = new HorizontalPanel();
	private Label closeByLabel = new Label("Limit distance (m) to within:");
	private TextBox closeByBox = new TextBox();
	private Button closeByButton = new Button("Apply");

	// Food truck table and parts
	final SingleSelectionModel<FoodTruckData> 
	selectionModel = new SingleSelectionModel<FoodTruckData>();
	private CellTable<FoodTruckData> 
	truckCellTable = new CellTable<FoodTruckData>(50);
	private ScrollPanel scrollPanel = new ScrollPanel(truckCellTable);

	// Favourite table and parts
	private CellTable<FoodTruckData> 
	FavCellTable = new CellTable<FoodTruckData>(50);
	private ScrollPanel favScrollPanel = new ScrollPanel(FavCellTable);
	private Button saveButton = new Button("Save");

	// Login interface and parts
	private LoginInfo loginInfo = null;
	private VerticalPanel loginPanel = new VerticalPanel();
	private Anchor signInLink = new Anchor("Sign In");
	private Anchor signOutLink = new Anchor("Sign Out");
	private Label loginLabel = new Label("Sign in to customize your favourite "
			+ "food vendors list!");


	//Needed to create FoodCartMap object
	private FoodCartMap cartMap = new FoodCartMap(true);

	/**
	 * Remote remote service proxy to talk to the server-side FoodTruckService
	 */
	private final FoodTruckServiceAsync	
	foodTruckService = GWT.create(FoodTruckService.class);


	private final FilteredListDataProvider<FoodTruckData> 
	truckDataProvider = new FilteredListDataProvider<FoodTruckData>(
			new IFilter<FoodTruckData>() {
				// Return true if name contains the keyword
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

	private final FilteredListDataProvider<FoodTruckData> 
	FavTruckDataProvider = new FilteredListDataProvider<FoodTruckData>(
			new IFilter<FoodTruckData>() {
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


	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		//Needed to load map
		Maps.loadMapsApi("AIzaSyCRBwxpSxylsp96KCX96xRHUQxrY6e653I", "2", false, 
				new Runnable() {
			public void run() {
				cartMap.buildUi();
			}
		});

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
					if (loginInfo.isUserAdmin()){
						setAdminView();
					} else {
						setUserView();
					}
				} else {
					loadLogin();
				}
			}
		});

		foodTruckService.favFoodTruck(null, 
				new AsyncCallback<List<FoodTruckData>>(){
			@Override
			public void onFailure(Throwable caught) {
				Window.alert(SERVER_ERROR);
			}

			@Override
			public void onSuccess(List<FoodTruckData> result) {
				FavTruckDataProvider.setList(result);
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
		truckCellTable.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.DISABLED);

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
		sortHandler.setComparator(nameColumn, new Comparator<FoodTruckData>() {
			@Override
			public int compare(FoodTruckData o1, FoodTruckData o2) {
				return o1.getName().compareTo(o2.getName());
			}
		});
		truckCellTable.addColumn(nameColumn, "Food Vendor Name");

		// Add a text column to show the number of user check in.
		TextColumn<FoodTruckData> 
		userCountColumn = new TextColumn<FoodTruckData>() {
			@Override
			public String getValue(FoodTruckData object) {
				return object.getCount();
			}
		};
		userCountColumn.setSortable(true);

		// Set comparator used for sorting the user check in count column.
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
		truckCellTable.setSelectionModel(selectionModel);

		selectionModel.addSelectionChangeHandler(
				new SelectionChangeEvent.Handler() {
					@Override
					public void onSelectionChange(SelectionChangeEvent event) {
						FoodTruckData 
						selected = selectionModel.getSelectedObject();

						if (selected != null) {
							cartMap.openNewInfoWindow(selected);

						}
					}
				});
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
						truckDataProvider.setList(result);
						allTrucks = result;
						setUpTruckCellTable();
					}
				});
	}

	private void setAdminView() {
		truckListPanel.add(fetchTruckListButton);
		// Listen for mouse events on the fetch FourSquare data button.
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
		setUpFavCellTable();
		setTruckListPanel();
	}

	private void setTruckListPanel() {
		searchPanel.add(searchLabel);
		searchPanel.add(filterBox);
		truckListPanel.add(searchPanel);

		closeByPanel.add(closeByLabel);
		closeByPanel.add(closeByBox);
		
		closeByButton.addClickHandler(new ClickHandler(){
			@Override
			public void onClick(ClickEvent event) {
				applyCloseByLimit();
			}
		});
		closeByPanel.add(closeByButton);
		truckListPanel.add(closeByPanel);

		scrollPanel.setSize("430px", "300px");
		favScrollPanel.setSize("430px", "300px");

		VerticalPanel panel = new VerticalPanel();
		panel.add(scrollPanel);
		panel.add(saveButton);
		tabLayout.add(panel, "All Food Vendors");

		tabLayout.add(favScrollPanel, "Favourites");
		tabLayout.setAnimationDuration(200);
		tabLayout.getElement().getStyle().setMargin(10, Unit.PX);
		tabLayout.setSize("530px", "400px");
		truckListPanel.add(tabLayout);


		// Set up sign out hyperlink.
		signOutLink.setHref(loginInfo.getLogoutUrl());
		truckListPanel.add(signOutLink);

		// Add it to the root panel.
		RootPanel.get("foodTruckList").add(truckListPanel);
	}

	private void applyCloseByLimit() {
		String input = closeByBox.getText();
		if (input == null || input.equalsIgnoreCase("")) {
			truckDataProvider.setList(allTrucks);
			return;
		}
		try {
			double limit = Double.parseDouble(input);
			List<FoodTruckData> 
			closeByTrucks = cartMap.showCloseByTrucks(allTrucks, limit);

			truckDataProvider.setList(closeByTrucks);
		} catch (NumberFormatException e) {
			Window.alert("Please enter a valid number as the distance limit!");
			closeByBox.setText("");
			truckDataProvider.setList(allTrucks);
		}
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

	private void setUpFavCellTable(){
		saveButton.addClickHandler(
				new ClickHandler() {
					public void onClick(ClickEvent event) {
						FoodTruckData selected = selectionModel.getSelectedObject();
						foodTruckService.favFoodTruck(selected, new AsyncCallback<List<FoodTruckData>>(){

							@Override
							public void onFailure(Throwable caught) {
								Window.alert("Save fail.");
							}

							@Override
							public void onSuccess(List<FoodTruckData> result) {
								FavTruckDataProvider.setList(result);
							}

						});
					};
				});


		FavTruckDataProvider.addDataDisplay(FavCellTable);
		filterBox.addValueChangeHandler(new IStringValueChanged() {

			@Override
			public void valueChanged(String newValue) {
				FavTruckDataProvider.setFilter(newValue);
				FavTruckDataProvider.refresh();
			}
		});

		FavCellTable.setAutoHeaderRefreshDisabled(true);
		FavCellTable.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.DISABLED);

		// Attach a column sort handler to the ListDataProvider to sort the list.
		ListHandler<FoodTruckData> sortHandler = new ListHandler<FoodTruckData>(truckDataProvider.getList());
		FavCellTable.addColumnSortHandler(sortHandler);

		// Add a text column to show the name.
		TextColumn<FoodTruckData> nameColumn = new TextColumn<FoodTruckData>() {
			@Override
			public String getValue(FoodTruckData object) {
				return object.getName();
			}
		};
		nameColumn.setSortable(true);

		// Set comparator used for sorting the name column.
		sortHandler.setComparator(nameColumn, new Comparator<FoodTruckData>() {
			@Override
			public int compare(FoodTruckData o1, FoodTruckData o2) {
				return o1.getName().compareTo(o2.getName());
			}
		});
		FavCellTable.addColumn(nameColumn, "Food Truck Name");

		// Add a text column to show the number of user check in.
		TextColumn<FoodTruckData> userCountColumn = new TextColumn<FoodTruckData>() {
			@Override
			public String getValue(FoodTruckData object) {
				return String.valueOf(object.getRank());
			}
		};
		userCountColumn.setSortable(true);

		// Set comparator used for sorting the user check in count column.
		sortHandler.setComparator(userCountColumn, 
				new Comparator<FoodTruckData>(){
			@Override
			public int compare(FoodTruckData o1, FoodTruckData o2) {
				int diff = Integer.parseInt(o1.getCount()) 
						- Integer.parseInt(o2.getCount());
				return Integer.signum(diff);
			}
		});
		FavCellTable.addColumn(userCountColumn,"Rank");

		// Add a selection model to handle user selection.
		final SingleSelectionModel<FoodTruckData> selectionModel = new SingleSelectionModel<FoodTruckData>();

		FavCellTable.setSelectionModel(selectionModel);

		selectionModel.addSelectionChangeHandler(
				new SelectionChangeEvent.Handler() {

					@Override
					public void onSelectionChange(SelectionChangeEvent event) {
						FoodTruckData selected = selectionModel.getSelectedObject();

						if (selected != null) {
							//		Window.alert("You selected: " + selected);
							cartMap.openNewInfoWindow(selected);

						}
					}
				});
	}
}
