package org.vkim;

import org.eclipse.core.runtime.Assert;
import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.core.security.IConnectContext;
import org.eclipse.ecf.presence.IPresenceContainerAdapter;
import org.eclipse.ecf.presence.roster.IRoster;
import org.eclipse.ecf.presence.roster.IRosterManager;
import org.eclipse.ecf.provider.xmpp.identity.XMPPID;

public class Account {

	private XMPPID targetID;

	private IConnectContext connectContext;

	protected IContainer container;

	protected IPresenceContainerAdapter adapter;

	public Account(IContainer container, IPresenceContainerAdapter adapter) {
		Assert.isNotNull(container);
		Assert.isNotNull(adapter);
		this.container = container;
		this.adapter = adapter;
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

}
