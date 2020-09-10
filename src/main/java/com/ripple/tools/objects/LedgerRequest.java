package com.ripple.tools.objects;

import java.util.List;

public class LedgerRequest {

	private String method;

	private List<LedgerRequestParam> params;

	public LedgerRequest() {

	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public List<LedgerRequestParam> getParams() {
		return params;
	}

	public void setParams(List<LedgerRequestParam> params) {
		this.params = params;
	}
}
