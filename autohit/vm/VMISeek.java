/**
 * .
 * Copyright © 1999 Erich P G.
 *
 */
 
package autohit.vm;

import java.io.Serializable;

/**
 * A Virtual Machine instruction.  This is a Seek verification.
 *
 * @see autohit.vm.VMInstruction
 *
 * @author Erich P. Gatejen
 * @version 1.0
 * <i>Version History</i>
 * <code>EPG - Initial - 4Feb99</code> 
 * 
 */
public class VMISeek extends VMInstruction {
	
	// --- FINAL FIELDS ------------------------------------------------------	

	// --- FIELDS ------------------------------------------------------------

    /**
     *  The expected string to seek.
     *  @serial
     */      	    
    public String       expected;
    

	// --- PUBLIC METHODS ----------------------------------------------------	

    /**
     *  Default constructor.
     */ 
    public VMISeek() {
        nToken = VMInstruction.SEEK;
    }

    /**
     *  Dump this Instruction.  Mostly for debugging.
     *
     *  @return a String containing the dump.
     */
    public String toString() {
        StringBuffer d = new StringBuffer();        
        d.append(" VMISeek --------------------------- \n");
        d.append("    expceted = [" + expected + "]\n");
        return d.toString();                
    }
    
	// --- PRIVATE METHODS ---------------------------------------------------

} 
