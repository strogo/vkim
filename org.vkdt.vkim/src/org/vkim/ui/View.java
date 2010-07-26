package org.vkim.ui;

import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.core.security.IConnectContext;
import org.eclipse.ecf.provider.xmpp.identity.XMPPID;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.part.ViewPart;
import org.vkim.Account;
import org.vkim.ConnectivityManager;

public class View extends ViewPart {
	public static final String ID = "org.vkim.ui.view";

	private TreeViewer viewer;

	IAction newAction;

	/**
	 * The content provider class is responsible for providing objects to the
	 * view. It can wrap existing objects in adapters or simply return objects
	 * as-is. These objects may be sensitive to the current input of the view,
	 * or ignore it and always show the same content (like Task List, for
	 * example).
	 */
	class ViewContentProvider implements ITreeContentProvider {
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
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Object getParent(Object element) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public boolean hasChildren(Object element) {
			// TODO Auto-generated method stub
			return false;
		}
	}

	class ViewLabelProvider extends LabelProvider implements ILabelProvider {

		@Override
		public String getText(Object element) {

			if (element instanceof Account) {
				Account account = (Account) element;
				if (account.getRoster().getUser() != null)
					return account.getRoster().getUser().getName();
				return account.getTargetID().getUsername();
			}

			return super.getText(element);
		}

		public Image getImage(Object obj) {
			return PlatformUI.getWorkbench().getSharedImages()
					.getImage(ISharedImages.IMG_OBJ_ELEMENT);
		}
	}

	private void hookContextMenu() {
		MenuManager menuMgr = new MenuManager();
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				fillContextMenu(manager);
			}

		});
		Menu menu = menuMgr.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, viewer);
	}

	private void makeActions() {
		newAction = ActionFactory.NEW_WIZARD_DROP_DOWN.create(getSite()
				.getWorkbenchWindow());
	}

	private void fillContextMenu(IMenuManager manager) {
		manager.add(newAction);
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));

	}

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	public void createPartControl(Composite parent) {
		viewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);

		viewer.setContentProvider(new ViewContentProvider());
		viewer.setLabelProvider(new ViewLabelProvider());
		viewer.setInput(getConnectivityManager().getAccounts());
		makeActions();
		hookContextMenu();
	}

	private ConnectivityManager getConnectivityManager() {
		return org.vkim.Activator.getDefault().getConnectivityManager();
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		viewer.getControl().setFocus();
	}

	public boolean addContainer(IContainer container, XMPPID targetID,
			IConnectContext connectContext) {
		Account account = getConnectivityManager().createAccount(container,
				targetID, connectContext);
		if (account == null)
			return false;

		viewer.add(viewer.getInput(), account);
		return true;

	}
}