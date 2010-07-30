package org.vkim.ui.wizards;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class ExistingAccountWizardPage extends WizardPage {

	Text loginText;

	Text passwordText;

	static Pattern loginPattern = Pattern.compile("[a-zA-Z0-9]+");

	protected ExistingAccountWizardPage() {
		super("existingAccount");
		setTitle("Enter your account credentials");
		setDescription("Check out source code if you don't trust us ;-)");
	}

	private void verify() {
		final String text = loginText.getText();
		if (text.equals("")) { //$NON-NLS-1$
			updateStatus("A valid vkontakte id must be specified.");
		} else {
			final Matcher matcher = loginPattern.matcher(text);
			if (!matcher.matches()) {
				updateStatus("The vkontakte id is malformed.");
			} else {
				updateStatus(null);
			}
		}
	}

	@Override
	public void createControl(Composite parent) {

		parent = new Composite(parent, SWT.NONE);

		parent.setLayout(new GridLayout());

		final GridData fillData = new GridData(SWT.FILL, SWT.CENTER, true,
				false);

		Label label = new Label(parent, SWT.LEFT);
		label.setText("Id:");

		loginText = new Text(parent, SWT.SINGLE | SWT.BORDER);
		loginText.setLayoutData(fillData);
		loginText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				verify();
			}
		});
		loginText.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
				verify();
			}

			public void widgetSelected(SelectionEvent e) {
				verify();
			}
		});

		label = new Label(parent, SWT.LEFT);
		label.setText("Password:");
		passwordText = new Text(parent, SWT.SINGLE | SWT.PASSWORD | SWT.BORDER);
		passwordText.setLayoutData(fillData);

		if (loginText.getText().equals("")) {
			updateStatus(null);
			setPageComplete(false);
		} else if (isPageComplete())
			passwordText.setFocus();

		org.eclipse.jface.dialogs.Dialog.applyDialogFont(parent);
		setControl(parent);
	}

	protected void updateStatus(String message) {
		setErrorMessage(message);
		setPageComplete(message == null);
	}

	String getPassword() {
		return passwordText.getText();
	}

	String getConnectID() {
		return loginText.getText() + "@vk.com";
	}
}
