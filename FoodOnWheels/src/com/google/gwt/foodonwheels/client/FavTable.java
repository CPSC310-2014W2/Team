package com.google.gwt.foodonwheels.client;

import com.google.gwt.user.client.ui.*;  
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.dom.client.Style.Unit;

public class FavTable{
	
	public void Display(){
	TabLayoutPanel tab = new TabLayoutPanel(1.5, Unit.EM);
	tab.add(new HTML("this"), "[this]");
	tab.add(new HTML("that"), "[that]");
	tab.add(new HTML("the other"), "[the other]");
	
	RootLayoutPanel root = RootLayoutPanel.get();
	root.add(tab);
	}
}