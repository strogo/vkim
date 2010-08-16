package org.vkim.ui;

import org.eclipse.ecf.presence.IPresence.Mode;
import org.eclipse.ecf.presence.roster.IRosterEntry;
import org.eclipse.jface.resource.ColorDescriptor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.LocalResourceManager;
import org.eclipse.jface.resource.ResourceManager;
import org.eclipse.jface.viewers.DecoratingStyledCellLabelProvider;
import org.eclipse.jface.viewers.IDecorationContext;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.StyledString.Styler;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.TextStyle;
import org.eclipse.ui.model.WorkbenchLabelProvider;

public class PresenceLabelProvider extends DecoratingStyledCellLabelProvider {

	private final ColorDescriptor availableColorDescriptor = ColorDescriptor
			.createFrom(new RGB(57, 181, 74));
	private final StyledString.Styler AVAILABLE_MODE_STYLER = new Styler() {

		@Override
		public void applyStyles(TextStyle textStyle) {
			textStyle.foreground = (Color) getResourceManager().get(
					availableColorDescriptor);
		}

	};

	private final ColorDescriptor defaultColorDescriptor = ColorDescriptor
			.createFrom(new RGB(154, 154, 154));

	private final StyledString.Styler DEFAULT_MODE_STYLER = new Styler() {

		@Override
		public void applyStyles(TextStyle textStyle) {
			textStyle.foreground = (Color) getResourceManager().get(
					defaultColorDescriptor);
		}

	};

	@Override
	protected Styler getDecorationStyle(Object element) {
		if (element instanceof IRosterEntry
				&& ((IRosterEntry) element).getPresence().getMode()
						.equals(Mode.AVAILABLE))
			return AVAILABLE_MODE_STYLER;

		return DEFAULT_MODE_STYLER;
	}

	private ResourceManager resourceManager;

	@Override
	public void dispose() {
		if (resourceManager != null)
			resourceManager.dispose();
		resourceManager = null;
		super.dispose();
	}

	private ResourceManager getResourceManager() {
		if (resourceManager == null) {
			resourceManager = new LocalResourceManager(
					JFaceResources.getResources());
		}

		return resourceManager;
	}

	public PresenceLabelProvider(IStyledLabelProvider labelProvider,
			ILabelDecorator decorator, IDecorationContext decorationContext) {
		super(labelProvider, decorator, decorationContext);
		if (decorator instanceof PresenceDecorator)
			((PresenceDecorator) decorator).setLabelProvider(this);
	}

	public static class PresenceStyledLabelProvider extends
			WorkbenchLabelProvider implements IStyledLabelProvider {

		@Override
		public StyledString getStyledText(final Object element) {
			final String text = getText(element);
			if (text != null) {
				return new StyledString(text, new Styler() {

					@Override
					public void applyStyles(TextStyle textStyle) {
						Color foreground = PresenceStyledLabelProvider.this
								.getForeground(element);
						if (foreground != null)
							textStyle.foreground = foreground;

						Color background = PresenceStyledLabelProvider.this
								.getBackground(element);
						if (background != null)
							textStyle.background = background;
					}
				});
			}

			return null;
		}

	}
}