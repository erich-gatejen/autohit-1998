/**
 * .
 * Copyright © 1999 Erich P G.
 *
 */
 
package autohit.vm;

import java.io.Serializable;

/**
 * A Virtual Machine instruction.  It implements a SCOPE instruction which
 * defines a new variable scope.  It should be pushed onto the scope stack to
 * mark the new scope.  
 *
 * @see autohit.vm.VMInstruction
 *
 * @author Erich P. Gatejen
 * @version 1.0
 * <i>Version History</i>
 * <code>EPG - Initial - 20Jan99</code> 
 * 
 */
public class VMIScope extends VMInstruction {
	
	// --- FINAL FIELDS ------------------------------------------------------	

	// --- FIELDS ------------------------------------------------------------
    
	// --- PUBLIC METHODS ----------------------------------------------------	

    /**
     *  Default constructor.
     */ 
    public VMIScope() {
        nToken = VMInstruction.SCOPE;
    }

    /**
     *  Dump this Instruction.  Mostly for debugging.
     *
     *  @return a String containing the dump.
     */
    public String toString() {
        StringBuffer d = new StringBuffer();        
        return " VMIScope -------------------------- \n";
    }
    
	// --- PRIVATE METHODS ---------------------------------------------------

} 
