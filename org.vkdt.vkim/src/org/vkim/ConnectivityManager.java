package org.vkim;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.core.security.IConnectContext;
import org.eclipse.ecf.presence.IPresenceContainerAdapter;
import org.eclipse.ecf.provider.xmpp.identity.XMPPID;

public class ConnectivityManager {

	protected List<Account> accounts = new ArrayList<Account>();

	public List<Account> getAccounts() {
		return accounts;
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

	public Account createAccount(IContainer container, XMPPID targetID,
			IConnectContext connectContext) {
		if (container == null)
			return null;
		IPresenceContainerAdapter containerAdapter = (IPresenceContainerAdapter) container
				.getAdapter(IPresenceContainerAdapter.class);
		if (containerAdapter == null)
			return null;
		if (containerPresent(container))
			return null;
		Account account = new Account(container, containerAdapter);
		account.setTargetID(targetID);
		account.setConnectContext(connectContext);
		if (!addAccount(account))
			return null;

		return account;

	}
}
