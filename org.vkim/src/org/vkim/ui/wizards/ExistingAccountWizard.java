package org.vkim.ui.wizards;

import org.eclipse.ecf.core.ContainerCreateException;
import org.eclipse.ecf.core.ContainerFactory;
import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.core.IContainerManager;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.identity.IDCreateException;
import org.eclipse.ecf.core.identity.IDFactory;
import org.eclipse.ecf.core.security.ConnectContextFactory;
import org.eclipse.ecf.core.security.IConnectContext;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PartInitException;
import org.vkim.Activator;
import org.vkim.controller.Account;
import org.vkim.ui.View;

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

		Account account = null;
		try {
			final View view = (View) workbench.getActiveWorkbenchWindow()
					.getActivePage().showView(View.ID);
			account = view.addContainer(container, targetID);
		} catch (PartInitException e) {
			e.printStackTrace();
		}

		account.connect(connectContext);

		return true;
	}
}
