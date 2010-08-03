package org.vkim.controller.actions;

import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.actions.SelectionProviderAction;
import org.vkim.Messages;
import org.vkim.controller.Account;
import org.vkim.ui.ApplicationStatusHandler;

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
		ApplicationStatusHandler.resetErrorStatus();

		IStructuredSelection iss = getStructuredSelection();

		if (iss != null && iss.getFirstElement() instanceof Account) {
			((Account) iss.getFirstElement()).remove();
		}

		super.run();
	}

}
