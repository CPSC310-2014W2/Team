package com.google.gwt.foodonwheels.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("login")
public interface LoginService extends RemoteService {
	/*
	 * The path annotation "login" is configured in LoginServiceAsync.java
	 */
	public LoginInfo login(String requestUri);
}
