package org.vkim.ui;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ecf.core.util.IExceptionHandler;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.statushandlers.AbstractStatusHandler;
import org.eclipse.ui.statushandlers.StatusAdapter;
import org.vkim.Activator;

public class ApplicationStatusHandler extends AbstractStatusHandler implements
		IExceptionHandler {

	private IStatusLineManager statusLine;
	private IWorkbenchWindow window;

	public ApplicationStatusHandler() {
		window = Activator.getDefault().getWorkbench()
				.getActiveWorkbenchWindow();
		IWorkbenchPartSite site = window.getActivePage().getActivePart()
				.getSite();
		if (site instanceof IViewSite)
			statusLine = ((IViewSite) site).getActionBars()
					.getStatusLineManager();
		if (site instanceof IEditorSite)
			statusLine = ((IEditorSite) site).getActionBars()
					.getStatusLineManager();
	}

	@Override
	public void handle(final StatusAdapter statusAdapter, int style) {
		statusAdapter.getStatus().getException().printStackTrace();

	}

	@Override
	public IStatus handleException(Throwable exception) {
		final IStatus status = new Status(IStatus.ERROR, Activator.PLUGIN_ID,
				IStatus.ERROR, exception.getLocalizedMessage(), exception);
		if (statusLine != null) {
			setStatusLineErrorMessage(status.getMessage());
		} else {
			ErrorDialog.openError(window.getShell(), "Error",
					status.getMessage(), status);
		}
		return status;
	}

	public void setStatusLineErrorMessage(final String message) {
		if (statusLine != null)
			Display.getDefault().asyncExec(new Runnable() {

				@Override
				public void run() {
					statusLine.setErrorMessage(message);

				}
			});

	}

	public static void resetErrorStatus() {
		if (ApplicationWorkbenchAdvisor.getApplicationStatusHandler() != null)
			ApplicationWorkbenchAdvisor.getApplicationStatusHandler()
					.setStatusLineErrorMessage("");

	}

	public void dispose() {
		statusLine = null;
	}
}
