package org.vkim.controller;

import org.eclipse.core.runtime.Assert;
import org.eclipse.ecf.presence.im.IChatMessageSender;
import org.eclipse.ecf.presence.roster.IRosterEntry;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

public class ConversationInput implements IEditorInput {

	private IRosterEntry entry;

	public ConversationInput(IRosterEntry input) {
		Assert.isNotNull(input);
		this.entry = input;
	}

	@Override
	public Object getAdapter(@SuppressWarnings("rawtypes") Class adapter) {
		if (adapter == IRosterEntry.class)
			return entry;

		return null;
	}

	public IChatMessageSender getMessageSender() {
		return entry.getRoster().getPresenceContainerAdapter().getChatManager()
				.getChatMessageSender();
	}

	@Override
	public boolean exists() {
		return entry != null;
	}

	@Override
	public ImageDescriptor getImageDescriptor() {
		return null;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((entry == null) ? 0 : entry.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ConversationInput other = (ConversationInput) obj;
		if (entry == null) {
			if (other.entry != null)
				return false;
		} else if (!entry.equals(other.entry))
			return false;
		return true;
	}

	@Override
	public String getName() {
		return entry.getName();
	}

	@Override
	public IPersistableElement getPersistable() {
		return null;
	}

	@Override
	public String getToolTipText() {
		if (entry.getUser() != null)
			return NLS.bind("Conversation with {0}", entry.getUser().getName());
		return "";
	}

	public IRosterEntry getInterlocutor() {
		return entry;
	}

}
