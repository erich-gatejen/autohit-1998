/**
 * .
 * Copyright © 1999 Erich P G.
 *
 */
 
package autohit.vm;

import java.io.Serializable;

/**
 * A Virtual Machine instruction.  Is a header Name/value pair.  It will
 * be placed on the scope stack for use by a GET instruction.
 *
 * @see autohit.vm.VMInstruction
 *
 * @author Erich P. Gatejen
 * @version 1.0
 * <i>Version History</i>
 * <code>EPG - Initial - 14Jan99</code> 
 * 
 */
public class VMIHeader extends VMInstruction {
	
	// --- FINAL FIELDS ------------------------------------------------------	

	// --- FIELDS ------------------------------------------------------------

    /**
     *  Variable name.
     *  @serial
     */      	    
    public String           name;
    
    /**
     *  Variable value.
     *  @serial
     */      	    
    public String           value;  

	// --- PUBLIC METHODS ----------------------------------------------------	

    /**
     *  Default constructor.
     */ 
    public VMIHeader() {
        nToken = VMInstruction.HEADER;   
    }

    /**
     *  Dump this Instruction.  Mostly for debugging.
     *
     *  @return a String containing the dump.
     */
    public String toString() {
        StringBuffer d = new StringBuffer();        
        d.append(" VMIHeader ------------------------- \n");
        d.append("    name = " + name + "\n");
        d.append("    value = " + value + "\n");
        return d.toString();                
    }
    
	// --- PRIVATE METHODS ---------------------------------------------------

} 
