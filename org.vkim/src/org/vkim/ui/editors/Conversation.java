package org.vkim.ui.editors;

import org.eclipse.core.runtime.Assert;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.util.ECFException;
import org.eclipse.ecf.presence.im.IChatID;
import org.eclipse.ecf.presence.im.IChatMessage;
import org.eclipse.ecf.presence.im.IChatMessageSender;
import org.eclipse.ecf.presence.roster.IRosterEntry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.vkim.controller.ConversationInput;

public class Conversation extends FormEditorPart {

	public static final String ID = "org.vkim.ui.editors.Conversation"; //$NON-NLS-1$

	private StyledText log;

	private Text input;

	private static final int[] WEIGHTS = { 75, 25 };

	private IChatMessageSender sender;

	public Conversation() {
	}

	@Override
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);
		createForm(getForm());
	}

	@Override
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		super.init(site, input);
		this.sender = ((ConversationInput) input).getMessageSender();
	}

	private void createForm(IManagedForm managedForm) {
		Composite body = managedForm.getForm().getForm().getBody();
		body.setLayout(new FillLayout());

		FormToolkit toolkit = managedForm.getToolkit();
		managedForm.getForm().setText(getTitle());
		toolkit.decorateFormHeading(managedForm.getForm().getForm());

		SashForm sash = new SashForm(body, SWT.VERTICAL);

		log = new StyledText(sash, SWT.BORDER | SWT.WRAP | SWT.V_SCROLL
				| SWT.MULTI | SWT.READ_ONLY);

		input = new Text(sash, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL);

		input.addKeyListener(new KeyListener() {

			@Override
			public void keyReleased(KeyEvent e) {
				switch (e.keyCode) {
				case SWT.CR:
				case SWT.KEYPAD_CR:
					if (e.stateMask == SWT.CTRL) {
						String message = Conversation.this.input.getText();
						if (message.equals(""))
							return;
						Conversation.this.input.setText("");
						try {
							Conversation.this.sender.sendChatMessage(
									getInterlocutor().getUser().getID(),
									message);
						} catch (ECFException e1) {
							e1.printStackTrace();
						}
						Conversation.this.append(getInterlocutor().getRoster()
								.getUser().getID(), message);
					}
					e.doit = false;
					break;
				}
			}

			@Override
			public void keyPressed(KeyEvent e) {

			}
		});

		sash.setWeights(WEIGHTS);

	}

	private boolean isFirstMessage = true;

	protected void append(ID id, String message) {
		if (!isFirstMessage)
			log.append(Text.DELIMITER);
		log.append(getUserName(id) + ": " + message);
		isFirstMessage = false;

	}

	private IRosterEntry getInterlocutor() {
		return ((ConversationInput) Conversation.this.getEditorInput())
				.getInterlocutor();
	}

	private static final String getUserName(ID id) {
		IChatID chatID = (IChatID) id.getAdapter(IChatID.class);
		return chatID == null ? id.getName() : chatID.getUsername();
	}

	@Override
	public void setFocus() {
		if (input != null)
			input.setFocus();
	}

	@Override
	public void dispose() {
		if (log != null)
			log.dispose();
		if (input != null)
			input.dispose();
		super.dispose();
	}

	public synchronized void showMessage(IChatMessage message) {
		Assert.isNotNull(message);
		append(message.getFromID(), message.getBody());

	}

}
