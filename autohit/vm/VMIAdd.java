/**
 * .
 * Copyright © 1999 Erich P G.
 *
 */
 
package autohit.vm;

import java.io.Serializable;

/**
 * A Virtual Machine instruction.  Implements and ADD.
 *
 * @see autohit.vm.VMInstruction
 *
 * @author Erich P. Gatejen
 * @version 1.0
 * <i>Version History</i>
 * <code>EPG - Initial - 21Jan99</code> 
 * 
 */
public class VMIAdd extends VMInstruction {
	
	// --- FINAL FIELDS ------------------------------------------------------	

	// --- FIELDS ------------------------------------------------------------

    /**
     *  Variable name.
     *  @serial
     */      	    
    public String           name;
    
    /**
     *  Numeric value and/or variable reference.
     *  @serial
     */      	    
    public String           value;  

	// --- PUBLIC METHODS ----------------------------------------------------	

    /**
     *  Default constructor.
     */ 
    public VMIAdd() {
        nToken = VMInstruction.ADD;   
    }

    /**
     *  Dump this Instruction.  Mostly for debugging.
     *
     *  @return a String containing the dump.
     */
    public String toString() {
        StringBuffer d = new StringBuffer();        
        d.append(" VMIAdd ---------------------------- \n");
        d.append("    name = " + name + "\n");
        d.append("    value = " + value + "\n");
        return d.toString();                
    }
    
	// --- PRIVATE METHODS ---------------------------------------------------

} 
