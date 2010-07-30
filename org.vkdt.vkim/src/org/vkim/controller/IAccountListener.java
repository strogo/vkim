package org.vkim.controller;

public interface IAccountListener {

	public void handleAccountUpdate(final Account changedValue);

	public void handleAccountEntryRemove(final Account entry);

	public void handleAccountEntryAdd(final Account entry);

}
