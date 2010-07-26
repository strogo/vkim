package org.vkim.controller;

import org.eclipse.core.runtime.Assert;
import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.core.security.IConnectContext;
import org.eclipse.ecf.presence.IPresenceContainerAdapter;
import org.eclipse.ecf.presence.roster.IRoster;
import org.eclipse.ecf.presence.roster.IRosterManager;
import org.eclipse.ecf.provider.xmpp.identity.XMPPID;
import org.eclipse.swt.widgets.Display;
import org.vkim.Activator;
import org.vkim.ui.View;

public class Account {

	private View view;

	private XMPPID targetID;

	private IConnectContext connectContext;

	protected IContainer container;

	protected IPresenceContainerAdapter adapter;

	IAccountListener updateListener = new IAccountListener() {

		@Override
		public void handleAccountEntryRemove(final Account entry) {
			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					Account.this.view.removeEntryFromTreeViewer(entry);
				}
			});

		}

		@Override
		public void handleAccountEntryAdd(final Account entry) {
			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					Account.this.view.addEntryToTreeViewer(entry);
				}
			});

		}
	};

	public Account(View view, IContainer container,
			IPresenceContainerAdapter adapter) {
		this.view = view;
		Assert.isNotNull(container);
		Assert.isNotNull(adapter);
		this.container = container;
		this.adapter = adapter;
		Activator.getDefault().getConnectivityManager()
				.addAccountListener(updateListener);
	}

	public IContainer getContainer() {
		return container;
	}

	public IPresenceContainerAdapter getPresenceContainerAdapter() {
		return adapter;
	}

	public IRosterManager getRosterManager() {
		return getPresenceContainerAdapter().getRosterManager();
	}

	public IRoster getRoster() {
		return getRosterManager().getRoster();
	}

	public void setTargetID(XMPPID targetID) {
		this.targetID = targetID;
	}

	public XMPPID getTargetID() {
		return targetID;
	}

	public void setConnectContext(IConnectContext connectContext) {
		this.connectContext = connectContext;
	}

	public IConnectContext getConnectContext() {
		return connectContext;
	}

	public View getView() {
		return view;
	}

}
