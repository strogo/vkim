package org.vkim.ui;

import org.eclipse.ui.statushandlers.AbstractStatusHandler;
import org.eclipse.ui.statushandlers.StatusAdapter;

public class ApplicationStatusHandler extends AbstractStatusHandler {

	@Override
	public void handle(final StatusAdapter statusAdapter, int style) {
		statusAdapter.getStatus().getException().printStackTrace();

	}

}
