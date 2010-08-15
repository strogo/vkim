package org.vkim.controller.actions;

import org.eclipse.ecf.presence.roster.IRosterEntry;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.actions.SelectionProviderAction;
import org.vkim.controller.ConversationInput;
import org.vkim.ui.editors.Conversation;

public class OpenAction extends SelectionProviderAction implements IAction {

	IWorkbenchPage page;

	public OpenAction(ISelectionProvider provider, IWorkbenchPage page) {
		super(provider, "Open");
		this.page = page;
	}

	@Override
	public void selectionChanged(IStructuredSelection selection) {
		boolean enabled = getSelection() != null && !getSelection().isEmpty();
		enabled &= (selection.getFirstElement() instanceof IRosterEntry);
		setEnabled(enabled);
	}

	@Override
	public void run() {
		try {
			page.openEditor(
					new ConversationInput(
							(IRosterEntry) ((IStructuredSelection) getSelection())
									.getFirstElement()), Conversation.ID);
		} catch (PartInitException e) {
			e.printStackTrace();
		}

	}

}
