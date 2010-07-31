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
import org.vkim.Messages;

public class ExistingAccountWizardPage extends WizardPage {

	Text loginText;

	Text passwordText;

	static Pattern loginPattern = Pattern.compile("\\d+"); //$NON-NLS-1$

	protected ExistingAccountWizardPage() {
		super("existingAccount"); //$NON-NLS-1$
		setTitle(Messages.ExistingAccountWizardPage_TITLE);
		setDescription(Messages.ExistingAccountWizardPage_DESCRIPTION);
	}

	private void verify() {
		final String text = loginText.getText();
		if (text.equals("")) { //$NON-NLS-1$
			updateStatus(Messages.ExistingAccountWizardPage_STATUS);
		} else {
			final Matcher matcher = loginPattern.matcher(text);
			if (!matcher.matches()) {
				updateStatus(Messages.ExistingAccountWizardPage_STATUS_INCOMPLETE);
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
		label.setText(Messages.ExistingAccountWizardPage_LABEL_ID);

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
		label.setText(Messages.ExistingAccountWizardPage_PASSWORD);
		passwordText = new Text(parent, SWT.SINGLE | SWT.PASSWORD | SWT.BORDER);
		passwordText.setLayoutData(fillData);

		if (loginText.getText().equals("")) { //$NON-NLS-1$
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
		return "id" + loginText.getText() + "@vk.com"; //$NON-NLS-1$
	}
}
