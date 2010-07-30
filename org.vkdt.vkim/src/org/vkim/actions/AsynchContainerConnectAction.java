package org.vkim.actions;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ecf.core.ContainerConnectException;
import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.security.IConnectContext;
import org.eclipse.ecf.core.util.ECFException;
import org.eclipse.ecf.core.util.IExceptionHandler;
import org.eclipse.jface.action.IAction;
import org.eclipse.osgi.util.NLS;
import org.vkim.Activator;

public class AsynchContainerConnectAction extends SynchContainerConnectAction {

	public AsynchContainerConnectAction(IContainer container, ID targetID,
			IConnectContext connectContext, IExceptionHandler exceptionHandler,
			Runnable successBlock) {
		super(container, targetID, connectContext, exceptionHandler,
				successBlock);
	}

	public AsynchContainerConnectAction(IContainer container, ID targetID,
			IConnectContext connectContext, IExceptionHandler exceptionHandler) {
		super(container, targetID, connectContext, exceptionHandler);
	}

	public AsynchContainerConnectAction(IContainer container, ID targetID,
			IConnectContext connectContext) {
		this(container, targetID, connectContext, null);
	}

	public void dispose() {
		this.container = null;
		this.targetID = null;
		this.connectContext = null;
		this.window = null;
	}

	protected IStatus handleException(Throwable e) {
		if (exceptionHandler != null)
			return exceptionHandler.handleException(e);
		else if (e instanceof ECFException) {
			return new MultiStatus(
					Activator.PLUGIN_ID,
					IStatus.ERROR,
					new IStatus[] { getStatusForECFException((ECFException) e) },
					NLS.bind("Connect to {0} failed.", //$NON-NLS-1$
							targetID.getName()), null);
		} else
			return new Status(IStatus.ERROR, Activator.PLUGIN_ID,
					IStatus.ERROR, e.getLocalizedMessage(), null);
	}

	protected IStatus getStatusForECFException(ECFException exception) {
		IStatus status = exception.getStatus();
		Throwable cause = status.getException();
		if (cause instanceof ECFException) {
			return getStatusForECFException((ECFException) cause);
		}
		return status;
	}

	class ContainerMutex implements ISchedulingRule {

		IContainer c;

		public ContainerMutex(IContainer container) {
			this.c = container;
		}

		protected boolean isSameContainer(IContainer other) {
			if (other != null && c.getID().equals(other.getID()))
				return true;
			return false;
		}

		protected IContainer getContainer() {
			return AsynchContainerConnectAction.this.getContainer();
		}

		public boolean isConflicting(ISchedulingRule rule) {
			if (rule == this)
				return true;
			else if (rule instanceof ContainerMutex
					&& isSameContainer(((ContainerMutex) rule).getContainer()))
				return true;
			else
				return false;
		}

		public boolean contains(ISchedulingRule rule) {
			return (rule == this);
		}
	}

	class AsynchActionJob extends Job {

		public AsynchActionJob() {
			super("Container connect");
			setRule(new ContainerMutex(getContainer()));
		}

		public IStatus run(IProgressMonitor monitor) {
			monitor.beginTask(NLS.bind("Connecting to {0}",
					(targetID == null) ? "" : targetID.getName()), 100); //$NON-NLS-1$
			monitor.worked(30);
			try {
				container.connect(targetID, connectContext);
				if (monitor.isCanceled()) {
					container.disconnect();
					return Status.CANCEL_STATUS;
				}
				if (successBlock != null) {
					successBlock.run();
				}
				monitor.worked(60);
				return Status.OK_STATUS;
			} catch (ContainerConnectException e) {
				return handleException(e);
			} finally {
				monitor.done();
			}
		}

	}

	public void run(IAction action) {
		new AsynchActionJob().schedule();
	}

	public void run() {
		this.run(null);
	}

}
