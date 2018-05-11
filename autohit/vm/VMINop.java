/**
 * .
 * Copyright © 1999 Erich P G.
 *
 */
 
package autohit.vm;

import java.io.Serializable;

/**
 * A Virtual Machine instruction.  A NOP -- no operation...
 *
 * @see autohit.vm.VMInstruction
 *
 * @author Erich P. Gatejen
 * @version 1.0
 * <i>Version History</i>
 * <code>EPG - Initial - 20Jan99</code> 
 * 
 */
public class VMINop extends VMInstruction {
	
	// --- FINAL FIELDS ------------------------------------------------------	

	// --- FIELDS ------------------------------------------------------------
    
	// --- PUBLIC METHODS ----------------------------------------------------	

    /**
     *  Default constructor.
     */ 
    public VMINop() {
        nToken = VMInstruction.NOP;
    }

    /**
     *  Dump this Instruction.  Mostly for debugging.
     *
     *  @return a String containing the dump.
     */
    public String toString() {
        StringBuffer d = new StringBuffer();        
        return " VMINop ---------------------------- \n";
    }
    
	// --- PRIVATE METHODS ---------------------------------------------------

} 
