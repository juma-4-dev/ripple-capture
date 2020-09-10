package com.ripple.tools.objects;

import com.google.gson.annotations.SerializedName;

public class LedgerRequestParam {

	@SerializedName("ledger_index")
	private Integer ledgerIndex;

	public LedgerRequestParam() {

	}

	public Integer getLedgerIndex() {
		return ledgerIndex;
	}

	public void setLedgerIndex(Integer ledgerIndex) {
		this.ledgerIndex = ledgerIndex;
	}
}
