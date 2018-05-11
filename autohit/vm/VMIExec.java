/**
 * .
 * Copyright © 1999 Erich P G.
 *
 */
 
package autohit.vm;

import java.io.Serializable;

/**
 * A Virtual Machine instruction.  This is a Exec verification.  This allows
 * the use of external classes or executables.  The implementation is up to the
 * specific verification method/object.
 *
 * @see autohit.vm.VMInstruction
 *
 * @author Erich P. Gatejen
 * @version 1.0
 * <i>Version History</i>
 * <code>EPG - Initial - 4Feb99</code> 
 * 
 */
public class VMIExec extends VMInstruction {
	
	// --- FINAL FIELDS ------------------------------------------------------	

	// --- FIELDS ------------------------------------------------------------

    /**
     *  The invocation.
     *  @serial
     */      	    
    public String       invocation;
    
    /**
     *  Any content.
     *  @serial
     */      	    
    public String       content;

	// --- PUBLIC METHODS ----------------------------------------------------	

    /**
     *  Default constructor.
     */ 
    public VMIExec() {
        nToken = VMInstruction.EXEC;
    }

    /**
     *  Dump this Instruction.  Mostly for debugging.
     *
     *  @return a String containing the dump.
     */
    public String toString() {
        StringBuffer d = new StringBuffer();        
        d.append(" VMIExec --------------------------- \n");
        d.append("    invocation = [" + invocation + "]\n");
        d.append("    content = [" + content + "]\n");
        return d.toString();                
    }
    
	// --- PRIVATE METHODS ---------------------------------------------------

} 
