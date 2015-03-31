package com.google.gwt.foodonwheels.client;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.Range;

/**
 * Based on GWT CellTable Filtering by Artiom K.
 * http://www.artiom.pro/2012/09/gwt-celltable-filtering.html
 * @author devinli
 *
 * @param <T> data object to be filtered by the provider
 */
public class FilteredListDataProvider<T> extends ListDataProvider<T> {

	private String keyword;

	public final IFilter<T> filter;

	public FilteredListDataProvider(IFilter<T> filter) {
		this.filter = filter;
	}

	public String getFilter() {
		return keyword;
	}

	public void setFilter(String filter) {
		this.keyword = filter;
		refresh();
	}

	public boolean hasFilter() {
		if (keyword==null || "".equals(keyword))
			return false;
		else
			return true;
	}

	@Override
	protected void updateRowData(HasData<T> display, int start, List<T> values) {
		if (!hasFilter()) {
			super.updateRowData(display, start, values);
		} 
		else {
			int end = start + values.size();
			Range range = display.getVisibleRange();
			int curStart = range.getStart();
			int curLength = range.getLength();
			int curEnd = curStart + curLength;
			if (start == curStart || (curStart < end && curEnd > start)) {
				// Fire the handler with the data that is in the range.
				// Allow an empty list that starts on the page start.
				int realStart = curStart < start ? start : curStart;
				int realEnd = curEnd > end ? end : curEnd;
				int realLength = realEnd - realStart;

				List<T> filteredResults = new ArrayList<T>(realLength);
				for (int i = realStart - start; 
						i < realStart - start + realLength; 
						i++) {
					if ( filter.isValid( values.get(i), getFilter() ) )
						filteredResults.add(values.get(i));
				}
				display.setRowData(realStart, filteredResults);
				display.setRowCount(filteredResults.size());
			}
		}
	}

}
