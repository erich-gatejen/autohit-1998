/**
 * .
 * Copyright © 1999 Erich P G.
 *
 */
 
package autohit.vm;

import java.io.Serializable;

/**
 * A Virtual Machine instruction.  This implements a GET query.  It is up to transport
 * registered with the VM to actually perform the GET.  While this instruction is 
 * primarily designed to handle HTTP GET operations, a different transport could use it
 * effectively.
 *
 * @see autohit.vm.VMInstruction
 *
 * @author Erich P. Gatejen
 * @version 1.0
 * <i>Version History</i>
 * <code>EPG - Initial - 5Jan99</code> 
 * 
 */
public class VMIGet extends VMInstruction {
	
	// --- FINAL FIELDS ------------------------------------------------------	

	// --- FIELDS ------------------------------------------------------------
    
    /**
     *  The query string.  For the HTTP Transport this will be a URL without the
     *  domain.
     *  @serial
     */      	    
    public String      qs;
    
	// --- PUBLIC METHODS ----------------------------------------------------	

    /**
     *  Default constructor.
     */ 
    public VMIGet() {
        nToken = VMInstruction.GET;
    }

    /**
     *  Dump this Instruction.  Mostly for debugging.
     *
     *  @return a String containing the dump.
     */
    public String toString() {
        StringBuffer d = new StringBuffer();        
        d.append(" VMIGet ---------------------------- \n");
        d.append("    qs = " + qs + "\n");
        return d.toString();                
    }
    
	// --- PRIVATE METHODS ---------------------------------------------------

} 
