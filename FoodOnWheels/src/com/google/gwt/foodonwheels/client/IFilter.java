package com.google.gwt.foodonwheels.client;

/**
 * Based on GWT CellTable Filtering by Artiom K.
 * http://www.artiom.pro/2012/09/gwt-celltable-filtering.html
 * @author devinli
 *
 * @param <T> The data object to be filtered
 */
public interface IFilter<T> {
	
	boolean isValid(T value, String filter);

}
