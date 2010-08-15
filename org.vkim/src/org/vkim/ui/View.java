package org.vkim.ui;

import org.eclipse.ecf.presence.roster.IRoster;
import org.eclipse.ecf.presence.roster.IRosterEntry;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.part.ViewPart;
import org.vkim.controller.Account;
import org.vkim.controller.ConnectivityManager;
import org.vkim.controller.PresentationModel;
import org.vkim.controller.actions.ConnectAction;
import org.vkim.controller.actions.DeleteAction;
import org.vkim.controller.actions.OpenAction;

public class View extends ViewPart {

	public static final String ID = "org.vkim.ui.view"; //$NON-NLS-1$

	private TreeViewer viewer;

	protected static final int DEFAULT_EXPAND_LEVEL = 3;

	IAction newAction;

	IAction deleteAction;

	IAction connectAction;

	IAction openAction;

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
		IWorkbenchWindow window = getSite().getWorkbenchWindow();

		newAction = ActionFactory.NEW_WIZARD_DROP_DOWN.create(window);

		// TODO extract to my action factory
		deleteAction = new DeleteAction((ISelectionProvider) viewer);
		deleteAction.setAccelerator(SWT.DEL); // XXX not working
		deleteAction.setEnabled(false);
		ISharedImages sharedImages = window.getWorkbench().getSharedImages();
		deleteAction.setImageDescriptor(sharedImages
				.getImageDescriptor(ISharedImages.IMG_TOOL_DELETE));
		deleteAction.setDisabledImageDescriptor(sharedImages
				.getImageDescriptor(ISharedImages.IMG_TOOL_DELETE_DISABLED));

		connectAction = new ConnectAction((ISelectionProvider) viewer);
		connectAction.setEnabled(false);

		openAction = new OpenAction((ISelectionProvider) viewer, getSite()
				.getPage());
	}

	private void fillContextMenu(IMenuManager manager) {
		manager.add(newAction);
		manager.add(new Separator());
		manager.add(deleteAction);
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
		manager.add(connectAction);

	}

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	public void createPartControl(Composite parent) {
		viewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		PresentationModel pm = new PresentationModel();
		viewer.setContentProvider(pm);
		viewer.setLabelProvider(pm);
		viewer.setInput(getCM());

		initListeners();
		makeActions();
		hookContextMenu();
		viewer.expandToLevel(DEFAULT_EXPAND_LEVEL);
	}

	private void initListeners() {
		viewer.addDoubleClickListener(new IDoubleClickListener() {

			@Override
			public void doubleClick(DoubleClickEvent event) {

				IStructuredSelection selection = (IStructuredSelection) event
						.getSelection();
				Object selected = selection.getFirstElement();

				if (viewer.isExpandable(selected))
					viewer.setExpandedState(selected,
							!viewer.getExpandedState(selected));

				if (selection.size() == 1 && openAction.isEnabled())
					openAction.run();
			}

		});

		viewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				getSite().setSelectionProvider(viewer);
			}
		});
	}

	private ConnectivityManager getCM() {
		return org.vkim.Activator.getDefault().getConnectivityManager();
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		viewer.getControl().setFocus();
	}

	public void refreshTreeViewer(Object val, boolean labels) {
		if (viewer != null) {
			Control c = viewer.getControl();
			if (c != null && !c.isDisposed()) {
				if (val != null) {
					viewer.refresh(val, labels);
					ViewerFilter[] filters = viewer.getFilters();
					if (filters.length != 0) {
						viewer.refresh(labels);
					}
				} else {
					viewer.refresh(labels);
				}
				viewer.expandToLevel(DEFAULT_EXPAND_LEVEL);
			}
		}
	}

	public void removeEntryFromTreeViewer(Object entry) {
		if (viewer != null)
			viewer.remove(entry);

	}

	public void addEntryToTreeViewer(IRosterEntry entry) {
		if (viewer != null)
			viewer.add(((IRoster) entry.getParent()).getAdapter(Account.class),
					entry);

	}

	public void addEntryToTreeViewer(Account entry) {
		if (viewer != null)
			viewer.add(entry.getParent(), entry);

	}

}