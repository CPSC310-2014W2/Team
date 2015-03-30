package com.google.gwt.foodonwheels.client;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.ui.TextBox;

/**
 * Based on GWT CellTable Filtering by Artiom K.
 * http://www.artiom.pro/2012/09/gwt-celltable-filtering.html
 * @author devinli
 *
 */
public class SearchTextBox extends TextBox {

	private List<IStringValueChanged> 
	subscribers = new ArrayList<IStringValueChanged>(1);

	protected String currentValue;


	public SearchTextBox() {
		super();
		configureHandlers();
	}

	public void addValueChangeHandler(IStringValueChanged valueChanged) {
		subscribers.add(valueChanged);
	}

	private void configureHandlers() {

		addKeyUpHandler(new KeyUpHandler() {
			@Override
			public void onKeyUp(KeyUpEvent event) {
				onValueChanged();
			}
		});

		addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				onValueChanged();
			}
		});
	}

	private void onValueChanged() {
		String newValue = getValue() == null ? "" : getValue();
		if (newValue.equals(currentValue))
			return;
		currentValue = newValue;
		for (IStringValueChanged item : subscribers) {
			item.valueChanged(currentValue);
		}
	}

}
