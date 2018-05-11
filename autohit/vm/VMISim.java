/**
 * .
 * Copyright © 1999 Erich P G.
 *
 */
 
package autohit.vm;

import java.io.Serializable;

/**
 * A Virtual Machine instruction.  This encapsulates a Sim.  it specifies it by
 * name.  it is up to the specific VM to load the Sim given the name.
 *
 * @see autohit.vm.VMInstruction
 *
 * @author Erich P. Gatejen
 * @version 1.0
 * <i>Version History</i>
 * <code>EPG - Initial - 8Mar</code> 
 * 
 */
public class VMISim extends VMInstruction {
	
	// --- FINAL FIELDS ------------------------------------------------------	

	// --- FIELDS ------------------------------------------------------------

    /**
     *  Variable name.
     *  @serial
     */      	    
    public String           name;


    /**
     *  Chute value.  Default is 0.
     *  @serial
     */      	    
    public String           chute;
    
	// --- PUBLIC METHODS ----------------------------------------------------	

    /**
     *  Default constructor.
     */ 
    public VMISim() {
        nToken = VMInstruction.SIM;
    }

    /**
     *  Dump this Instruction.  Mostly for debugging.
     *
     *  @return a String containing the dump.
     */
    public String toString() {
        StringBuffer d = new StringBuffer();        
        d.append(" VMISim ---------------------------- \n");
        d.append("    name = " + name + "\n");
        d.append("    chute = " + chute + "\n");
        return d.toString();                
    }
    
	// --- PRIVATE METHODS ---------------------------------------------------

} 
