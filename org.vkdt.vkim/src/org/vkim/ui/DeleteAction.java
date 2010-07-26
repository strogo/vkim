package org.vkim.ui;

import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.actions.SelectionProviderAction;
import org.vkim.Activator;
import org.vkim.controller.Account;

public class DeleteAction extends SelectionProviderAction {

	protected DeleteAction(ISelectionProvider provider) {
		super(provider, "Delete");
	}

	@Override
	public void selectionChanged(IStructuredSelection selection) {
		setEnabled(getSelection() != null && !getSelection().isEmpty());
	}

	@Override
	public void run() {
		IStructuredSelection iss = getStructuredSelection();

		if (iss.getFirstElement() instanceof Account) {
			Activator.getDefault().getConnectivityManager()
					.removeAccount((Account) iss.getFirstElement());
		}

		super.run();
	}

}
