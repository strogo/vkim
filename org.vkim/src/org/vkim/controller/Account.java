package org.vkim.controller;

import org.eclipse.core.runtime.Assert;
import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.presence.IPresenceContainerAdapter;
import org.eclipse.ecf.presence.roster.IRoster;
import org.eclipse.ecf.presence.roster.IRosterEntry;
import org.eclipse.ecf.presence.roster.IRosterItem;
import org.eclipse.ecf.presence.roster.IRosterListener;
import org.eclipse.ecf.presence.roster.IRosterManager;
import org.eclipse.swt.widgets.Display;
import org.vkim.Activator;
import org.vkim.ui.View;

public class Account {

	private View view;

	private ID targetID;

	protected IContainer container;

	protected IPresenceContainerAdapter adapter;

	IAccountListener updateAccountListener = new IAccountListener() {

		@Override
		public void handleAccountUpdate(final Account changedValue) {
			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					Account.this.view.refreshTreeViewer(changedValue, true);
				}
			});
		}

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

	IRosterListener updateRosterListener = new IRosterListener() {

		@Override
		public void handleRosterUpdate(final IRoster roster,
				final IRosterItem changedValue) {
			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					Account.this.view.refreshTreeViewer(changedValue, true);
				}
			});
		}

		@Override
		public void handleRosterEntryRemove(final IRosterEntry entry) {
			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					Account.this.view.removeEntryFromTreeViewer(entry);
				}
			});

		}

		@Override
		public void handleRosterEntryAdd(final IRosterEntry entry) {
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
		getCM().addAccountListener(updateAccountListener);
		getRosterManager().addRosterListener(updateRosterListener);
	}

	public IContainer getContainer() {
		return container;
	}

	private ConnectivityManager getCM() {
		return Activator.getDefault().getConnectivityManager();
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

	public void setTargetID(ID targetID) {
		this.targetID = targetID;
	}

	public ID getTargetID() {
		return targetID;
	}

	public View getView() {
		return view;
	}

	public void disconnect() {
		if (container != null)
			container.disconnect();
	}

	public void dispose() {
		getCM().removeAccountListener(updateAccountListener);
		getRosterManager().removeRosterListener(updateRosterListener);
	}

	public Object getParent() {
		return getCM();
	}

	public boolean remove() {
		return getCM().remove(this);
	}

}
