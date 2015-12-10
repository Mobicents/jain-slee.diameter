/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011, Red Hat, Inc. and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package net.java.slee.resource.diameter.s6a.events.avp;

import java.io.Serializable;
import net.java.slee.resource.diameter.base.events.avp.Enumerated;

/**
 * Defines an interface representing the IMS-Voice-Over-PS-Sessions-Supported grouped AVP type.
 * From the Diameter S6a Reference Point Protocol Details (3GPP TS 29.272 V12.8.0) specification:
 * 
 * <pre>
 * 7.3.106 IMS-Voice-Over-PS-Sessions-Supported
 *
 * The IMS-Voice-Over-PS-Sessions-Supported AVP is of type Enumerated. The following values are defined:
 *      NOT_SUPPORTED (0)
 *          This value indicates that "IMS Voice over PS Sessions" is not supported by the UE's most recently used TA
 *          or RA in the serving node.
 *      SUPPORTED (1)
 *          This value indicates that "IMS Voice over PS Sessions" is supported by the UE's most recently used TA or
 *          RA in the serving node.
 * </pre>
 * 
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public class IMSVoiceOverPSSessionsSupported implements Enumerated, Serializable {

  private static final long serialVersionUID = 1L;

  public static final int _NOT_SUPPORTED = 0;
  public static final int _SUPPORTED = 1;

  public static final IMSVoiceOverPSSessionsSupported NOT_SUPPORTED = new IMSVoiceOverPSSessionsSupported(_NOT_SUPPORTED);
  public static final IMSVoiceOverPSSessionsSupported SUPPORTED = new IMSVoiceOverPSSessionsSupported(_SUPPORTED);

  private int value = -1;

  private IMSVoiceOverPSSessionsSupported(int value) {
    this.value = value;
  }

  public static IMSVoiceOverPSSessionsSupported fromInt(int type) {
    switch (type) {
      case _NOT_SUPPORTED:
        return NOT_SUPPORTED;
      case _SUPPORTED:
        return SUPPORTED;
      default:
        throw new IllegalArgumentException("Invalid value: " + type);
    }
  }

  public int getValue() {
    return value;
  }

  @Override
  public String toString() {
    switch (value) {
      case _NOT_SUPPORTED:
        return "NOT_SUPPORTED";
      case _SUPPORTED:
        return "SUPPORTED";
      default:
        return "<Invalid Value>";
    }
  }
}