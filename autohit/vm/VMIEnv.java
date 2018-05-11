/**
 * .
 * Copyright © 1999 Erich P G.
 *
 */
 
package autohit.vm;

import java.io.Serializable;

/**
 * A Virtual Machine instruction.  Is an Name/value pair for an environment var that
 * is to be passed to a Sim.  Yes, this is a bit of a hack.  :D
 *
 * @see autohit.vm.VMInstruction
 *
 * @author Erich P. Gatejen
 * @version 1.0
 * <i>Version History</i>
 * <code>EPG - Initial - 8Mar99</code> 
 * 
 */
public class VMIEnv extends VMInstruction {
	
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
    public VMIEnv() {
        nToken = VMInstruction.ENV;   
    }

    /**
     *  Dump this Instruction.  Mostly for debugging.
     *
     *  @return a String containing the dump.
     */
    public String toString() {
        StringBuffer d = new StringBuffer();        
        d.append(" VMIEnv ------------------------- \n");
        d.append("    name = " + name + "\n");
        d.append("    value = " + value + "\n");
        return d.toString();                
    }
    
	// --- PRIVATE METHODS ---------------------------------------------------

} 
