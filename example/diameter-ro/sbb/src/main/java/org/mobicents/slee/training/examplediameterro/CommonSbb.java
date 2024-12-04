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

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.slee.CreateException;
import javax.slee.Sbb;
import javax.slee.SbbContext;
import javax.slee.SbbID;
import javax.slee.facilities.Tracer;
import javax.slee.resource.ResourceAdaptorTypeID;
import net.java.slee.resource.diameter.ro.RoAvpFactory;
import net.java.slee.resource.diameter.ro.RoProvider;
import org.mobicents.slee.SbbContextExt;

/**
 * CommonSbb is the base SBB for all the Sbb's used in examples
 * 
 * @author amit bhayani
 * 
 */

public abstract class CommonSbb implements Sbb {

	private String className = null;

	protected Tracer tracer;
	// the identifier for this sbb
	private SbbID sbbId;

	// the sbb's sbbContext
        private SbbContextExt sbbContextExt; // This SBB's SbbContext
        
        //private RoActivityContextInterfaceFactory roAcif;

        private RoAvpFactory roavpFactory;
        protected RoProvider roProvider;

	/** Creates a new instance of BounceSbb */
	public CommonSbb(String className) {
		this.className = className;
	}

	public void setSbbContext(SbbContext sbbContext) {
		
                this.sbbContextExt = (SbbContextExt) sbbContext;
		sbbId = sbbContextExt.getSbb();
		this.tracer = sbbContextExt.getTracer(this.className);
		
		if (this.tracer.isFineEnabled()) {
			this.tracer.fine("setSbbContext() called. " + this);
		}
		
		try {
			Context ctx = (Context) new InitialContext()
					.lookup("java:comp/env");

			// get the reference to the RAFrameProvider class which implements
                        ResourceAdaptorTypeID raroTypeID = new ResourceAdaptorTypeID("Diameter Ro", "java.net", "0.8.1");
                        this.roProvider = (RoProvider) sbbContextExt.getResourceAdaptorInterface(raroTypeID, "DiameterRo");
                        //this.roAcif = (RoActivityContextInterfaceFactory) sbbContextExt.getActivityContextInterfaceFactory(raTypeID);

                        this.roavpFactory = this.roProvider.getRoAvpFactory();
		} catch (NamingException ne) {
			this.tracer.severe("NamingException received ", ne);
		}
	}

	public void unsetSbbContext() {
		if (this.tracer.isFineEnabled()) {
			this.tracer.fine("unsetSbbContext() called. " + this);
		}
	}

	public void sbbCreate() throws javax.slee.CreateException {
		if (this.tracer.isFineEnabled()) {
			this.tracer.fine("sbbCreate() called. " + this);
		}
	}

	public void sbbPostCreate() throws CreateException {
		if (this.tracer.isFineEnabled()) {
			this.tracer.fine("sbbCreate() called " + this);
		}
	}

	public void sbbActivate() {
		if (this.tracer.isFineEnabled()) {
			this.tracer.fine("sbbActivate() called " + this);
		}
	}

	public void sbbPassivate() {
		if (this.tracer.isFineEnabled()) {
			this.tracer.fine("sbbPassivate() called " + this);
		}
	}

	public void sbbRemove() {
		if (this.tracer.isFineEnabled()) {
			this.tracer.fine("sbbRemove() called " + this);
		}
	}

	public void sbbLoad() {
		if (this.tracer.isFineEnabled()) {
			this.tracer.fine("sbbLoad() called " + this);
		}
	}

	public void sbbStore() {
		if (this.tracer.isFineEnabled()) {
			this.tracer.fine("sbbStore() called " + this);
		}
	}

	public void sbbRolledBack(javax.slee.RolledBackContext rolledBackContext) {
		if (this.tracer.isFineEnabled()) {
			this.tracer.fine("sbbRolledBack() called " + this);
		}
	}

	public void sbbExceptionThrown(Exception exception, Object obj,
			javax.slee.ActivityContextInterface activityContextInterface) {
		if (this.tracer.isFineEnabled()) {
			this.tracer.fine("sbbExceptionThrown() called " + this);
		}
	}
}
