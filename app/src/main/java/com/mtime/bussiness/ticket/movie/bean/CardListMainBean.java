// JSON Java Class Generator
// Written by Bruce Bao
// Used for API: 
package com.mtime.bussiness.ticket.movie.bean;

import com.mtime.base.bean.MBaseBean;

import java.util.List;

public class CardListMainBean extends MBaseBean {
	private List<CardList> creditCardList;
	private List<CardList> debitCardList;

	public List<CardList> getCreditCardList() {
		return creditCardList;
	}

	public void setCreditCardList(List<CardList> creditCardList) {
		this.creditCardList = creditCardList;
	}

	public List<CardList> getDebitCardList() {
		return debitCardList;
	}

	public void setDebitCardList(List<CardList> debitCardList) {
		this.debitCardList = debitCardList;
	}
}
