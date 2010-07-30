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
import org.vkim.Activator;

public class PresentationModel extends LabelProvider implements
		ITreeContentProvider, ILabelProvider {

	private ConnectivityManager cm;

	public void inputChanged(Viewer v, Object oldInput, Object newInput) {
	}

	public void dispose() {
	}

	public Object[] getElements(Object parent) {
		if (parent instanceof ConnectivityManager) {
			setConnectivityManager((ConnectivityManager) parent);
			return getConnectivityManager().getAccounts().toArray();
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
			return getConnectivityManager().getAccount(
					(IRoster) ((IRosterEntry) element).getParent());
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
			return roster.getUser().getName();
		if (getConnectivityManager().getAccount(roster) != null)
			return ((XMPPID) getConnectivityManager().getAccount(roster)
					.getTargetID()).getUsername();
		return "<ID unavailable>";
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

	public void setConnectivityManager(ConnectivityManager cm) {
		this.cm = cm;
	}

	public ConnectivityManager getConnectivityManager() {
		if (cm == null)
			cm = Activator.getDefault().getConnectivityManager();

		return cm;
	}

}
