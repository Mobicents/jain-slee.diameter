/*
 * ***************************************************
 *                                                 *
 *  Mobicents: The Open Source JSLEE Platform      *
 *                                                 *
 *  Distributable under LGPL license.              *
 *  See terms of license at gnu.org.               *
 *                                                 *
 ***************************************************
 */
package org.mobicents.slee.training.examplediameterro;

import javax.slee.ActivityContextInterface;

import net.java.slee.resource.diameter.ro.events.RoCreditControlRequest;

/**
 * @author Anh Dao
 */
public abstract class BounceSbb extends CommonSbb {

	public BounceSbb() {
		super(BounceSbb.class.getSimpleName());
	}

	public void sbbRolledBack(javax.slee.RolledBackContext rolledBackContext) {
		tracer.info("sbbRolledBack called.");
		tracer.info("--> "+rolledBackContext.getActivityContextInterface());
		tracer.info("--> "+rolledBackContext.getEvent());
		tracer.info("--> "+rolledBackContext.isRemoveRolledBack());
	}

	public void sbbExceptionThrown(Exception exception, Object obj,
			javax.slee.ActivityContextInterface activityContextInterface) {
		tracer.info("sbbExceptionThrown called.");
		exception.printStackTrace();
		tracer.info("--> "+obj);
		tracer.info("--> "+activityContextInterface);
	}
        public void onCreditControlRequest(RoCreditControlRequest event, ActivityContextInterface ac) {
            tracer.info("onCreditControlRequest called.");
            tracer.info("--> "+event.getSessionId());
            tracer.info("--> "+event.toString());
        }
}
