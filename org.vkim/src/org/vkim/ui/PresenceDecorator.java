package org.vkim.ui;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.ecf.presence.IPresence;
import org.eclipse.ecf.presence.roster.IRosterEntry;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ILightweightLabelDecorator;
import org.eclipse.jface.viewers.LabelProviderChangedEvent;
import org.eclipse.osgi.util.NLS;

public class PresenceDecorator implements ILightweightLabelDecorator {

	public static final String ID = "org.vkim.ui.presenceDecorator";

	private List<ILabelProviderListener> labelProviderListeners = new ArrayList<ILabelProviderListener>();

	private IBaseLabelProvider labelProvider;

	public void decoratorChanged() {
		List<ILabelProviderListener> toNotify = null;
		synchronized (labelProviderListeners) {
			toNotify = new ArrayList<ILabelProviderListener>(
					labelProviderListeners);
		}
		LabelProviderChangedEvent event = new LabelProviderChangedEvent(
				labelProvider);
		for (Iterator<ILabelProviderListener> i = toNotify.iterator(); i
				.hasNext();)
			i.next().labelProviderChanged(event);
	}

	@Override
	public synchronized void addListener(ILabelProviderListener listener) {
		if (listener != null) {
			synchronized (labelProviderListeners) {
				labelProviderListeners.add(listener);
			}
		}
	}

	@Override
	public void dispose() {
	}

	@Override
	public boolean isLabelProperty(Object element, String property) {
		System.out.println(property);
		return false;

	}

	@Override
	public void removeListener(ILabelProviderListener listener) {
		if (listener != null) {
			synchronized (labelProviderListeners) {
				labelProviderListeners.remove(listener);
			}
		}
	}

	@Override
	public void decorate(Object element, IDecoration decoration) {
		if (element instanceof IRosterEntry) {
			IPresence presence = ((IRosterEntry) element).getPresence();
			if (presence != null)
				decoration.addSuffix(NLS.bind(" ({0})", presence.getMode()));

		}

	}

	public void setLabelProvider(IBaseLabelProvider labelProvider) {
		this.labelProvider = labelProvider;

	}

	// don't need for a while
	// public IBaseLabelProvider getLabelProvider() {
	// return this.labelProvider;
	//
	// }

}
