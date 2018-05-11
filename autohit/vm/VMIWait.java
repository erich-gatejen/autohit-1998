/**
 * .
 * Copyright © 1999 Erich P G.
 *
 */
 
package autohit.vm;

import java.io.Serializable;

/**
 * A Virtual Machine instruction.  This instruction impliments a WAIT.  The
 * VM will stall the Sim for the specified time measured in milliseconds.
 *
 * @see autohit.vm.VMInstruction
 * @see autohit.Sim
 *
 * @author Erich P. Gatejen
 * @version 1.0
 * <i>Version History</i>
 * <code>EPG - Initial - 5Jan99</code> 
 * 
 */
public class VMIWait extends VMInstruction {
	
	// --- FINAL FIELDS ------------------------------------------------------	

	// --- FIELDS ------------------------------------------------------------

    /**
     *  The length of time to wait measured in milliseconds.
     *  This is a String and not an int so that we can do variable
     *  substitution.
     *  @serial
     */      	    
    public String      time;

	// --- PUBLIC METHODS ----------------------------------------------------	

    /**
     *  Default constructor.
     */ 
    public VMIWait() {
        nToken = VMInstruction.WAIT;   
    }

    /**
     *  Dump this Instruction.  Mostly for debugging.
     *
     *  @return a String containing the dump.
     */
    public String toString() {
        StringBuffer d = new StringBuffer();        
        d.append(" VMIWait --------------------------- \n");
        d.append("    time = " + time + "\n");
        return d.toString();                
    }
    
	// --- PRIVATE METHODS ---------------------------------------------------	


} 
