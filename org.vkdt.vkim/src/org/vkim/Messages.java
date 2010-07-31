package org.vkim;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.vkim.messages"; //$NON-NLS-1$
	public static String ApplicationWorkbenchWindowAdvisor_WINDOW_TITLE;
	public static String DeleteAction_TITLE;
	public static String AsynchContainerConnectAction_JOB_NAME;
	public static String AsynchContainerConnectAction_TASK_NAME;

	public static String ExistingAccountWizardPage_LABEL_ID;
	public static String ExistingAccountWizardPage_PASSWORD;
	public static String ExistingAccountWizardPage_DESCRIPTION;
	public static String ExistingAccountWizardPage_STATUS;
	public static String ExistingAccountWizardPage_STATUS_INCOMPLETE;
	public static String ExistingAccountWizardPage_TITLE;

	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
