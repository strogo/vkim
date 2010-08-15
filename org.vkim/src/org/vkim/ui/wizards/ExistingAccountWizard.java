package org.vkim.ui.wizards;

import java.util.Collection;
import java.util.Iterator;

import org.eclipse.ecf.core.ContainerCreateException;
import org.eclipse.ecf.core.ContainerFactory;
import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.core.IContainerListener;
import org.eclipse.ecf.core.IContainerManager;
import org.eclipse.ecf.core.events.IContainerConnectedEvent;
import org.eclipse.ecf.core.events.IContainerEvent;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.identity.IDCreateException;
import org.eclipse.ecf.core.identity.IDFactory;
import org.eclipse.ecf.core.security.ConnectContextFactory;
import org.eclipse.ecf.core.security.IConnectContext;
import org.eclipse.ecf.presence.IIMMessageEvent;
import org.eclipse.ecf.presence.IIMMessageListener;
import org.eclipse.ecf.presence.IPresenceContainerAdapter;
import org.eclipse.ecf.presence.im.IChatManager;
import org.eclipse.ecf.presence.im.IChatMessage;
import org.eclipse.ecf.presence.im.IChatMessageEvent;
import org.eclipse.ecf.presence.roster.IRosterEntry;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.vkim.Activator;
import org.vkim.controller.ConnectivityManager;
import org.vkim.controller.ConversationInput;
import org.vkim.ui.View;
import org.vkim.ui.editors.Conversation;

public class ExistingAccountWizard extends Wizard implements INewWizard {

	private static final String ECF_XMPP_CONTAINER_NAME = "ecf.xmpp.smack"; //$NON-NLS-1$

	ExistingAccountWizardPage page;

	private IContainer container;

	private ID targetID;

	private IConnectContext connectContext;

	// TODO username as pageName

	public ExistingAccountWizard() {
		super();
	}

	@Override
	public void addPages() {
		page = new ExistingAccountWizardPage();
		addPage(page);
	}

	private IWorkbench workbench;

	public ConnectivityManager getCM() {
		return Activator.getDefault().getConnectivityManager();
	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		this.workbench = workbench;
		this.container = null;
		try {
			this.container = ContainerFactory.getDefault().createContainer(
					ECF_XMPP_CONTAINER_NAME);
		} catch (final ContainerCreateException e) {
			// None
		}

		setWindowTitle("Add Existing VKontakte Account");
	}

	@Override
	public boolean performCancel() {
		if (container != null) {
			container.dispose();

			IContainerManager containerManager = Activator.getDefault()
					.getContainerManager();
			if (containerManager != null) {
				containerManager.removeContainer(container);
			}

		}

		return super.performCancel();
	}

	@Override
	public boolean performFinish() {
		final String connectID = page.getConnectID();
		final String password = page.getPassword();

		connectContext = ConnectContextFactory
				.createPasswordConnectContext(password);

		try {
			targetID = IDFactory.getDefault().createID(
					container.getConnectNamespace(), connectID);
		} catch (final IDCreateException e) {
			page.updateStatus(NLS.bind("Could not create ID with {0}", //$NON-NLS-1$
					connectID));
			return false;
		}

		final IPresenceContainerAdapter adapter = (IPresenceContainerAdapter) container
				.getAdapter(IPresenceContainerAdapter.class);

		container.addListener(new IContainerListener() {
			public void handleEvent(IContainerEvent event) {
				if (event instanceof IContainerConnectedEvent) {
					Display.getDefault().asyncExec(new Runnable() {
						public void run() {
							openView();
						}
					});
				}
			}
		});

		final IChatManager cm = adapter.getChatManager();

		cm.addMessageListener(new IIMMessageListener() {
			public void handleMessageEvent(IIMMessageEvent e) {
				if (e instanceof IChatMessageEvent)
					displayMessage((IChatMessageEvent) e);
			}
		});

		getCM().connect(container, targetID, connectContext);

		return true;
	}

	@SuppressWarnings("rawtypes")
	private IRosterEntry getInterlocutor(ID fromID) {
		final IPresenceContainerAdapter adapter = (IPresenceContainerAdapter) container
				.getAdapter(IPresenceContainerAdapter.class);

		Collection items = adapter.getRosterManager().getRoster().getItems();
		for (Iterator i = items.iterator(); i.hasNext();) {
			IRosterEntry entry = (IRosterEntry) i.next();
			if (entry.getUser().getID().equals(fromID))
				return entry;
		}

		return null;
	}

	private void displayMessage(final IChatMessageEvent e) {
		final IChatMessage message = e.getChatMessage();
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				final ConversationInput input = new ConversationInput(
						getInterlocutor(e.getFromID()));
				Conversation editor = (Conversation) workbench
						.getActiveWorkbenchWindow().getActivePage()
						.findEditor(input);
				if (editor != null) {
					editor.showMessage(message);
				} else {
					try {
						final IWorkbenchPage page = workbench
								.getActiveWorkbenchWindow().getActivePage();
						editor = (Conversation) page.openEditor(input,
								Conversation.ID);
						editor.showMessage(message);
					} catch (final PartInitException e) {
						e.printStackTrace();
					}
				}
			}

		});
	}

	private void openView() {
		try {
			final View view = (View) workbench.getActiveWorkbenchWindow()
					.getActivePage().showView(View.ID);
			getCM().createAccount(view, container, targetID);

		} catch (PartInitException e) {
			e.printStackTrace();
		}
	}

}
