/**
 * .
 * Copyright © 1999 Erich P G.
 *
 */
 
package autohit.vm;

import java.io.Serializable;

/**
 * A Virtual Machine instruction.  It implements a RSCOPE instruction which
 * specifies the removal of a scope.  The VM should remove all scope stack
 * elements up to and including the last SCOPE.  Any variable references
 * found in the removed elements should be removed from the VM's variable
 * map.  
 *
 * @see autohit.vm.VMInstruction
 *
 * @author Erich P. Gatejen
 * @version 1.0
 * <i>Version History</i>
 * <code>EPG - Initial - 20Jan99</code> 
 * 
 */
public class VMIRScope extends VMInstruction {
	
	// --- FINAL FIELDS ------------------------------------------------------	

	// --- FIELDS ------------------------------------------------------------
    
	// --- PUBLIC METHODS ----------------------------------------------------	

    /**
     *  Default constructor.
     */ 
    public VMIRScope() {
        nToken = VMInstruction.RSCOPE;
    }

    /**
     *  Dump this Instruction.  Mostly for debugging.
     *
     *  @return a String containing the dump.
     */
    public String toString() {
        StringBuffer d = new StringBuffer();        
        return " VMIRScope ------------------------- \n";
    }
    
	// --- PRIVATE METHODS ---------------------------------------------------

} 
