package org.vkim.controller.actions;

import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.actions.SelectionProviderAction;
import org.vkim.Activator;
import org.vkim.Messages;
import org.vkim.controller.Account;

public class DeleteAction extends SelectionProviderAction {

	public DeleteAction(ISelectionProvider provider) {
		super(provider, Messages.DeleteAction_TITLE);
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
					.remove((Account) iss.getFirstElement());
		}

		super.run();
	}

}
