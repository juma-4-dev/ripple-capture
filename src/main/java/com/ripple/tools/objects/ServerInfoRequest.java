package com.ripple.tools.objects;

import java.util.List;

public class ServerInfoRequest {

	private String method;

	private List<ServerInfoRequestParam> params;

	public ServerInfoRequest() {

	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public List<ServerInfoRequestParam> getParams() {
		return params;
	}

	public void setParams(List<ServerInfoRequestParam> params) {
		this.params = params;
	}
}
