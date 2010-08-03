package org.vkim.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.presence.IPresenceContainerAdapter;
import org.eclipse.ecf.presence.roster.IRoster;
import org.eclipse.ecf.presence.roster.IRosterItem;
import org.vkim.ui.View;

public class ConnectivityManager implements IAdapterFactory {

	protected static Map<IRosterItem, Account> accounts = new HashMap<IRosterItem, Account>();

	private List<IAccountListener> accountUpdateListeners = new ArrayList<IAccountListener>();

	public Collection<Account> getAccounts() {
		return accounts.values();
	}

	protected boolean containerPresent(IContainer container) {
		for (Iterator<Account> i = accounts.values().iterator(); i.hasNext();) {
			Account existingAccount = i.next();
			if (existingAccount.getContainer().getID()
					.equals(container.getID()))
				return true;
		}
		return false;
	}

	protected boolean addAccount(Account account) {
		if (account == null || account.getRoster() == null
				|| accounts.containsValue(account))
			return false;

		accounts.put(account.getRoster(), account);
		return true;
	}

	public Account createAccount(View view, IContainer container, ID targetID) {
		if (container == null)
			return null;
		IPresenceContainerAdapter containerAdapter = (IPresenceContainerAdapter) container
				.getAdapter(IPresenceContainerAdapter.class);
		if (containerAdapter == null)
			return null;
		if (containerPresent(container))
			return null;
		Account account = new Account(view, container, containerAdapter);
		account.setTargetID(targetID);
		if (!addAccount(account))
			return null;
		fireAccountAdd(account);

		return account;

	}

	public boolean remove(Account account) {
		boolean result;

		synchronized (accounts) {
			if (!accounts.containsKey(account.getRoster()))
				result = false;
			accounts.remove(account.getRoster());
			result = true;
		}
		account.disconnect();
		fireAccountRemove(account);
		account.dispose();

		return result;
	}

	public void disconnect() {
		synchronized (accounts) {
			for (Account account : accounts.values())
				account.disconnect();
		}
	}

	public Collection<IRosterItem> getRosterItems() {
		return accounts.keySet();
	}

	public Account getAccount(IRoster roster) {
		return accounts.get(roster);
	}

	public synchronized void addAccountListener(IAccountListener listener) {
		if (listener != null) {
			synchronized (accountUpdateListeners) {
				accountUpdateListeners.add(listener);
			}
		}
	}

	public synchronized void removeAccountListener(IAccountListener listener) {
		if (listener != null) {
			synchronized (accountUpdateListeners) {
				accountUpdateListeners.remove(listener);
			}
		}
	}

	protected void fireAccountUpdate(Account changedItem) {
		List<IAccountListener> toNotify = null;
		synchronized (accountUpdateListeners) {
			toNotify = new ArrayList<IAccountListener>(accountUpdateListeners);
		}
		for (Iterator<IAccountListener> i = toNotify.iterator(); i.hasNext();)
			i.next().handleAccountUpdate(changedItem);
	}

	protected void fireAccountAdd(Account entry) {
		List<IAccountListener> toNotify = null;
		synchronized (accountUpdateListeners) {
			toNotify = new ArrayList<IAccountListener>(accountUpdateListeners);
		}
		for (Iterator<IAccountListener> i = toNotify.iterator(); i.hasNext();)
			i.next().handleAccountEntryAdd(entry);

	}

	protected void fireAccountRemove(Account entry) {
		List<IAccountListener> toNotify = null;
		synchronized (accountUpdateListeners) {
			toNotify = new ArrayList<IAccountListener>(accountUpdateListeners);
		}
		for (Iterator<IAccountListener> i = toNotify.iterator(); i.hasNext();)
			i.next().handleAccountEntryRemove(entry);

	}

	public void notifyAccountUpdate(Account changedItem) {
		fireAccountUpdate(changedItem);
	}

	@Override
	public Object getAdapter(Object adaptableObject,
			@SuppressWarnings("rawtypes") Class adapterType) {
		if (adapterType == Account.class)
			return getAccount((IRoster) adaptableObject);
		return null;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Class[] getAdapterList() {
		return new Class[] { Account.class };
	}
}
