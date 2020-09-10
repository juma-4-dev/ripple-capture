package com.ripple.tools.objects;

import com.google.gson.annotations.SerializedName;

public class Ledger {

	private boolean closed;

	@SerializedName("close_time")
	private int closeTime;

	@SerializedName("parent_close_time")
	private int parentCloseTime;

	public Ledger() {

	}

	public int getClosingDuration() {
		return closeTime - parentCloseTime;
	}

	public boolean isClosed() {
		return closed;
	}

	public void setClosed(boolean closed) {
		this.closed = closed;
	}

	public int getCloseTime() {
		return closeTime;
	}

	public void setCloseTime(int closeTime) {
		this.closeTime = closeTime;
	}
}
