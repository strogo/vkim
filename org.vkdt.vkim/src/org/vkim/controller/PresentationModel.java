package org.vkim.controller;

import org.eclipse.ecf.presence.roster.IRoster;
import org.eclipse.ecf.presence.roster.IRosterEntry;
import org.eclipse.ecf.provider.xmpp.identity.XMPPID;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

public class PresentationModel extends LabelProvider implements
		ITreeContentProvider, ILabelProvider {

	public void inputChanged(Viewer v, Object oldInput, Object newInput) {
	}

	public void dispose() {
	}

	public Object[] getElements(Object parent) {
		if (parent instanceof ConnectivityManager) {
			return ((ConnectivityManager) parent).getAccounts().toArray();
		}

		if (parent instanceof Object[]) {
			return (Object[]) parent;
		}

		return new Object[0];
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof Account)
			return ((Account) parentElement).getRoster().getItems().toArray();

		return null;
	}

	@Override
	public Object getParent(Object element) {
		if (element instanceof Account) {
			return ((Account) element).getParent();
		}

		if (element instanceof IRosterEntry) {
			return ((IRoster) ((IRosterEntry) element).getParent())
					.getAdapter(Account.class);
		}

		return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		if (element instanceof Account) {
			IRoster roster = ((Account) element).getRoster();
			return roster.getItems() != null && !roster.getItems().isEmpty();
		}

		return false;
	}

	public String getName(IRoster roster) {
		if (roster.getUser() != null)
			return roster.getName();
		if (roster.getAdapter(Account.class) != null)
			return ((XMPPID) ((Account) roster.getAdapter(Account.class))
					.getTargetID()).getUsername();
		return "<ID unavailable>"; //$NON-NLS-1$
	}

	public String getName(IRosterEntry entry) {
		return entry.getName();
	}

	@Override
	public String getText(Object element) {

		if (element instanceof Account) {
			return getName(((Account) element).getRoster());
		}

		if (element instanceof IRosterEntry) {
			return getName((IRosterEntry) element);
		}

		return super.getText(element);
	}

	@Override
	public Image getImage(Object obj) {
		return PlatformUI.getWorkbench().getSharedImages()
				.getImage(ISharedImages.IMG_OBJ_ELEMENT);
	}

}
