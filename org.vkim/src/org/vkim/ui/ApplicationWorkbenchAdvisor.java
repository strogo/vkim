package org.vkim.ui;

import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchAdvisor;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;
import org.eclipse.ui.statushandlers.AbstractStatusHandler;

public class ApplicationWorkbenchAdvisor extends WorkbenchAdvisor {

	private static final String PERSPECTIVE_ID = "org.vkim.ui.perspective"; //$NON-NLS-1$

	public WorkbenchWindowAdvisor createWorkbenchWindowAdvisor(
			IWorkbenchWindowConfigurer configurer) {
		return new ApplicationWorkbenchWindowAdvisor(configurer);
	}

	public String getInitialWindowPerspectiveId() {
		return PERSPECTIVE_ID;
	}

	private static ApplicationStatusHandler statusHandler;

	@Override
	public synchronized AbstractStatusHandler getWorkbenchErrorHandler() {
		if (statusHandler == null)
			statusHandler = new ApplicationStatusHandler();
		return statusHandler;
	}

	public static ApplicationStatusHandler getApplicationStatusHandler() {
		return statusHandler;
	}

}
