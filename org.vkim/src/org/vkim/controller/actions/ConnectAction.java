package org.vkim.controller.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.actions.SelectionProviderAction;
import org.vkim.controller.Account;
import org.vkim.ui.ApplicationStatusHandler;

public class ConnectAction extends SelectionProviderAction implements IAction {

	public ConnectAction(ISelectionProvider provider) {
		super(provider, "Connect");
	}

	@Override
	public void selectionChanged(IStructuredSelection selection) {
		boolean enabled = getSelection() != null && !getSelection().isEmpty();
		enabled &= (selection.getFirstElement() instanceof Account);
		setEnabled(enabled);
	}

	@Override
	public void run() {
		ApplicationStatusHandler.resetErrorStatus();

		IStructuredSelection iss = getStructuredSelection();

		if (iss != null && iss.getFirstElement() instanceof Account) {
			((Account) iss.getFirstElement()).connect();
		}

		super.run();
	}
}
