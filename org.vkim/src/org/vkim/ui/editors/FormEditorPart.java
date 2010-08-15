package org.vkim.ui.editors;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.ManagedForm;
import org.eclipse.ui.part.EditorPart;

public abstract class FormEditorPart extends EditorPart {

	protected IManagedForm managedForm;

	@Override
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		setSite(site);
		Assert.isNotNull(input);
		setInput(input);
		site.setSelectionProvider(new FormEditorPartSelectionProvider(this));
		setPartName(input.getName());
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		if (managedForm != null)
			managedForm.commit(true);

	}

	@Override
	public void doSaveAs() {

	}

	@Override
	public boolean isDirty() {
		return managedForm != null && managedForm.isDirty();
	}

	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		super.dispose();
	}

	@Override
	public void createPartControl(Composite parent) {
		managedForm = new ManagedForm(parent);
		managedForm.setInput(getEditorInput());
	}

	public IManagedForm getForm() {
		return managedForm;
	}

	@Override
	public void setFocus() {
		managedForm.getForm().setFocus();
	}

}
