package org.vkim.controller.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.actions.SelectionProviderAction;
import org.vkim.Activator;
import org.vkim.controller.Account;

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
		IStructuredSelection iss = getStructuredSelection();

		if (iss != null && iss.getFirstElement() instanceof Account) {
			Activator.getDefault().getConnectivityManager()
					.connect(((Account) iss.getFirstElement()));
		}

		super.run();
	}
}
