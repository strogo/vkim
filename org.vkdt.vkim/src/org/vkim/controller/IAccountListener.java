package org.vkim.controller;

public interface IAccountListener {

	public void handleAccountEntryAdd(Account entry);

	public void handleAccountEntryRemove(Account entry);

}
