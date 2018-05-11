/**
 * .
 * Copyright © 1999 Erich P G.
 *
 */
 
package autohit.vm;

import java.io.Serializable;

/**
 * A Virtual Machine instruction.  This is a verification specification.  It will
 * reset a verification context.  Subsequent verification instructions, such as SEEK and
 * EXEC, will use the fresh context.
 *
 * This can specify a size verification.  CRC is a separate instruction.
 *
 * @see autohit.vm.VMInstruction
 * @see autohit.vm.VMICrc
 *
 * @author Erich P. Gatejen
 * @version 1.0
 * <i>Version History</i>
 * <code>EPG - Initial - 4Feb99</code> 
 * 
 */
public class VMIVerify extends VMInstruction {
	
	// --- FINAL FIELDS ------------------------------------------------------	

    /*
     * Tags the size field as unimportant
     */
     public static final int   NO_SIZE     = 0;       

	// --- FIELDS ------------------------------------------------------------

    /**
     *  Content size.
     *  @serial
     */      	    
    public int           size;
    

	// --- PUBLIC METHODS ----------------------------------------------------	

    /**
     *  Default constructor.
     */ 
    public VMIVerify() {
        nToken = VMInstruction.VERIFY;
        size   = NO_SIZE; 
    }

    /**
     *  Dump this Instruction.  Mostly for debugging.
     *
     *  @return a String containing the dump.
     */
    public String toString() {
        StringBuffer d = new StringBuffer();        
        d.append(" VMIVerify ------------------------ \n");
        d.append("    size = " + size + "\n");
        return d.toString();                
    }
    
	// --- PRIVATE METHODS ---------------------------------------------------

} 
