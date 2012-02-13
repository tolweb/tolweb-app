package org.tolweb.tapestryenhancements;

import java.util.Collection;

import org.apache.tapestry.IActionListener;
import org.apache.tapestry.IForm;
import org.apache.tapestry.IMarkupWriter;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.form.AbstractFormComponent;
import org.apache.tapestry.listener.ListenerInvoker;

/**
 * Copy of AbstractSubmit
 */
public abstract class CustomSubmit extends AbstractFormComponent {

	/**
	 * Determine if this submit component was clicked.
	 * 
	 * @param cycle
	 * @param name
	 * @return true if this submit was clicked
	 */
	protected abstract boolean isClicked(IRequestCycle cycle, String name);

	/**
	 * @see org.apache.tapestry.form.AbstractFormComponent#rewindFormComponent(org.apache.tapestry.IMarkupWriter,
	 *      org.apache.tapestry.IRequestCycle)
	 */
	protected void rewindFormComponent(IMarkupWriter writer, IRequestCycle cycle) {
		if (isClicked(cycle, getName()))
			handleClick(cycle, getForm());
	}

	@SuppressWarnings("unchecked")
	void handleClick(final IRequestCycle cycle, IForm form) {
		if (isParameterBound("selected"))
			setSelected(getTag());

		final IActionListener listener = getListener();
		final IActionListener action = getAction();

		if (listener == null && action == null)
			return;

		final ListenerInvoker listenerInvoker = getListenerInvoker();

		Object parameters = getParameters();
		if (parameters != null) {
			if (parameters instanceof Collection) {
				cycle
						.setListenerParameters(((Collection) parameters)
								.toArray());
			} else {
				cycle.setListenerParameters(new Object[] { parameters });
			}
		}

		// Invoke 'listener' now, but defer 'action' for later
		if (listener != null)
			listenerInvoker.invokeListener(listener, CustomSubmit.this, cycle);

		if (action != null) {
			Runnable notify = new Runnable() {
				public void run() {
					listenerInvoker.invokeListener(action, CustomSubmit.this,
							cycle);
				}
			};

			form.addDeferredRunnable(notify);
		}
	}

	/** parameter */
	public abstract IActionListener getListener();

	/** parameter */
	public abstract IActionListener getAction();

	/** parameter */
	public abstract Object getTag();

	/** parameter */
	public abstract void setSelected(Object tag);

	/** parameter */
	public abstract boolean getDefer();

	/** parameter */
	public abstract Object getParameters();

	/** Injected */
	public abstract ListenerInvoker getListenerInvoker();
}
