package org.vkim.ui;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.ProgressProvider;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.swt.widgets.Display;

public class ApplicationProgressProvider extends ProgressProvider {

	private IStatusLineManager statusLine;

	ApplicationProgressProvider(IStatusLineManager statusLine) {
		this.statusLine = statusLine;
	}

	@Override
	public IProgressMonitor createMonitor(Job job) {
		Display.getDefault().asyncExec(new Runnable() {

			@Override
			public void run() {
				statusLine.setErrorMessage("");

			}
		});
		return new DelegatingProgressMonitor(statusLine.getProgressMonitor());
	}

	class DelegatingProgressMonitor implements IProgressMonitor {

		IProgressMonitor delegate;

		public DelegatingProgressMonitor(IProgressMonitor delegate) {
			this.delegate = delegate;
		}

		@Override
		public void beginTask(final String name, final int totalWork) {
			Display.getDefault().asyncExec(new Runnable() {

				@Override
				public void run() {
					delegate.beginTask(name, totalWork);

				}
			});

		}

		@Override
		public void done() {
			Display.getDefault().asyncExec(new Runnable() {

				@Override
				public void run() {
					delegate.done();

				}
			});

		}

		@Override
		public void internalWorked(final double work) {
			Display.getDefault().asyncExec(new Runnable() {

				@Override
				public void run() {
					delegate.internalWorked(work);

				}
			});

		}

		@Override
		public boolean isCanceled() {
			return delegate.isCanceled();
		}

		@Override
		public void setCanceled(final boolean value) {
			Display.getDefault().asyncExec(new Runnable() {

				@Override
				public void run() {
					delegate.setCanceled(value);

				}
			});

		}

		@Override
		public void setTaskName(final String name) {
			Display.getDefault().asyncExec(new Runnable() {

				@Override
				public void run() {
					delegate.setTaskName(name);

				}
			});

		}

		@Override
		public void subTask(final String name) {
			Display.getDefault().asyncExec(new Runnable() {

				@Override
				public void run() {
					delegate.subTask(name);

				}
			});

		}

		@Override
		public void worked(final int work) {
			Display.getDefault().asyncExec(new Runnable() {

				@Override
				public void run() {
					delegate.worked(work);

				}
			});

		}

	}

}
