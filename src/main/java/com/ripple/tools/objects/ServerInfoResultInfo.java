package com.ripple.tools.objects;

import com.google.gson.annotations.SerializedName;

public class ServerInfoResultInfo {

	private String time;

	@SerializedName("validated_ledger")
	private ValidatedLedger validatedLedger;

	public ServerInfoResultInfo() {

	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public ValidatedLedger getValidatedLedger() {
		return validatedLedger;
	}

	public void setValidatedLedger(ValidatedLedger validatedLedger) {
		this.validatedLedger = validatedLedger;
	}

}
