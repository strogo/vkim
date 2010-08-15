package org.vkim.ui.editors;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.jface.util.SafeRunnable;
import org.eclipse.jface.viewers.IPostSelectionProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IEditorPart;

public class FormEditorPartSelectionProvider implements IPostSelectionProvider {

	private ListenerList listeners = new ListenerList();

	private ListenerList postListeners = new ListenerList();

	private ISelection globalSelection;

	private FormEditorPart formEditorPart;

	public FormEditorPartSelectionProvider(FormEditorPart formEditor) {
		Assert.isNotNull(formEditor);
		this.formEditorPart = formEditor;
	}

	public FormEditorPart getFormEditorPart() {
		return formEditorPart;
	}

	public ISelection getSelection() {
		IEditorPart editor = getFormEditorPart();
		if (editor != null) {
			ISelectionProvider selectionProvider = editor.getSite()
					.getSelectionProvider();
			if (selectionProvider != null && selectionProvider != this)
				return selectionProvider.getSelection();
		}
		if (globalSelection != null) {
			return globalSelection;
		}
		return StructuredSelection.EMPTY;
	}

	/*
	 * (non-Javadoc) Method declared on <code> ISelectionProvider </code> .
	 */
	public void setSelection(ISelection selection) {
		IEditorPart editor = getFormEditorPart();
		if (editor != null) {
			ISelectionProvider selectionProvider = editor.getSite()
					.getSelectionProvider();
			if (selectionProvider != null && selectionProvider != this)
				selectionProvider.setSelection(selection);
		} else {
			this.globalSelection = selection;
			fireSelectionChanged(new SelectionChangedEvent(this,
					globalSelection));
		}
	}

	public void fireSelectionChanged(final SelectionChangedEvent event) {
		Object[] listeners = this.listeners.getListeners();
		fireEventChange(event, listeners);
	}

	public void firePostSelectionChanged(final SelectionChangedEvent event) {
		Object[] listeners = postListeners.getListeners();
		fireEventChange(event, listeners);
	}

	private void fireEventChange(final SelectionChangedEvent event,
			Object[] listeners) {
		for (int i = 0; i < listeners.length; ++i) {
			final ISelectionChangedListener l = (ISelectionChangedListener) listeners[i];
			SafeRunner.run(new SafeRunnable() {
				public void run() {
					l.selectionChanged(event);
				}
			});
		}
	}

	@Override
	public void addSelectionChangedListener(ISelectionChangedListener listener) {
		listeners.add(listener);

	}

	@Override
	public void removeSelectionChangedListener(
			ISelectionChangedListener listener) {
		listeners.remove(listener);

	}

	@Override
	public void addPostSelectionChangedListener(
			ISelectionChangedListener listener) {
		postListeners.add(listener);

	}

	@Override
	public void removePostSelectionChangedListener(
			ISelectionChangedListener listener) {
		postListeners.remove(listener);

	}
}
