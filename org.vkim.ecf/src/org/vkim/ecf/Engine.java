package org.vkim.ecf;

import java.net.URISyntaxException;

import org.eclipse.ecf.core.ContainerConnectException;
import org.eclipse.ecf.core.ContainerCreateException;
import org.eclipse.ecf.core.ContainerFactory;
import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.core.security.ConnectContextFactory;
import org.eclipse.ecf.presence.IPresenceContainerAdapter;
import org.eclipse.ecf.presence.roster.IRosterManager;
import org.eclipse.ecf.provider.xmpp.identity.XMPPID;

public class Engine {

	private static final String ECF_XMPP_CONTAINER_NAME = "ecf.xmpp.smack"; //$NON-NLS-1$

	private static IContainer container;

	public static Object[] getContent(String login, Object passwd) {
		Object[] items = new String[] { "One", "Two", "Three" };
		try {
			final IPresenceContainerAdapter presenceContainerAdapter = (IPresenceContainerAdapter) getContainer()
					.getAdapter(IPresenceContainerAdapter.class);
			final IRosterManager rosterManager = presenceContainerAdapter
					.getRosterManager();
			if (rosterManager != null) {

				getContainer()
						.connect(
								new XMPPID(
										getContainer().getConnectNamespace(),
										login),
								ConnectContextFactory
										.createUsernamePasswordConnectContext(
												login, passwd));
				items = rosterManager.getRoster().getItems().toArray();
				// getContainer().disconnect();
			}
		} catch (ContainerConnectException e) {
			e.printStackTrace();
		} catch (ContainerCreateException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return items;

	}

	public static IContainer getContainer() throws ContainerCreateException {
		if (container == null)
			container = ContainerFactory.getDefault().createContainer(
					ECF_XMPP_CONTAINER_NAME);
		return container;
	}
}
