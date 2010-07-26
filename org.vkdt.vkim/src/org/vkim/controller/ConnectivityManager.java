package org.vkim.controller;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;

import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.core.security.IConnectContext;
import org.eclipse.ecf.presence.IPresenceContainerAdapter;
import org.eclipse.ecf.provider.xmpp.identity.XMPPID;
import org.vkim.ui.View;

public class ConnectivityManager extends Observable {

	private List<IAccountListener> accountUpdateListeners = new ArrayList<IAccountListener>();

	protected List<Account> accounts = new ArrayList<Account>();

	public List<Account> getAccounts() {
		return accounts;
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

	protected boolean containerPresent(IContainer container) {
		for (Iterator<Account> i = accounts.iterator(); i.hasNext();) {
			Account existingAccount = i.next();
			if (existingAccount.getContainer().getID()
					.equals(container.getID()))
				return true;
		}
		return false;
	}

	protected boolean addAccount(Account account) {
		if (account == null)
			return false;
		return accounts.add(account);
	}

	public Account createAccount(View view, IContainer container,
			XMPPID targetID, IConnectContext connectContext) {
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
		account.setConnectContext(connectContext);
		if (!addAccount(account))
			return null;
		fireAccountAdd(account);

		return account;

	}

	public boolean removeAccount(Account account) {
		boolean result;
		synchronized (accounts) {
			if (!accounts.contains(account))
				result = false;
			result = accounts.remove(account);
		}
		fireAccountRemove(account);
		return result;
	}
}
