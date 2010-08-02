package org.vkim.ui;

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;

/**
 * An action bar advisor is responsible for creating, adding, and disposing of
 * the actions added to a workbench window. Each window will be populated with
 * new actions.
 */
public class ApplicationActionBarAdvisor extends ActionBarAdvisor {

	@Override
	protected void fillStatusLine(IStatusLineManager statusLine) {
		statusLine.setCancelEnabled(true); // ??? not working
		Job.getJobManager().setProgressProvider(
				new ApplicationProgressProvider(statusLine));
		super.fillStatusLine(statusLine);
	}

	public ApplicationActionBarAdvisor(IActionBarConfigurer configurer) {
		super(configurer);
	}

}
