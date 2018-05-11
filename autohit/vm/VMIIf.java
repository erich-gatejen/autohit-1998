/**
 * .
 * Copyright © 1999 Erich P G.
 *
 */
 
package autohit.vm;

import java.io.Serializable;

/**
 * A Virtual Machine instruction.  This is an IF instruction.  If the
 * expression matches the value, then the if will pass and the ip
 * should point to the follow instruction (which should be a SCOPE).
 * Otherwise the ip will be changed to point to the instruction
 * following the if block (one beyond the associated RSCOPE).
 * <p>
 * <pre>
 * ip (Instruction Pointer) Flow
 *
 *                      $e$ != value
 *       -------------------------------------------
 *       |                                         |
 *       |                                         V
 *     [IF] [SCOPE]..code block.. [RSCOPE] ... more code..
 *                   
 *
 * </pre>
 *
 *
 * @see autohit.vm.VMInstruction
 * @see autohit.vm.VMIScope
 *
 * @author Erich P. Gatejen
 * @version 1.0
 * <i>Version History</i>
 * <code>EPG - Initial - 14Jan99</code> 
 * 
 */
public class VMIIf extends VMInstruction {
	
	// --- FINAL FIELDS ------------------------------------------------------	

	// --- FIELDS ------------------------------------------------------------

    /**
     *  Expression.
     *  @serial
     */      	    
    public String           e;
    
    /**
     *  Value.
     *  @serial
     */      	    
    public String           value;
    
    /**
     *  False target.  Jump here is expression resolves as false
     *  @serial
     */      	    
    public int              target;      

	// --- PUBLIC METHODS ----------------------------------------------------	

    /**
     *  Default constructor.
     */ 
    public VMIIf() {
        nToken = VMInstruction.IF;   
    }

    /**
     *  Dump this Instruction.  Mostly for debugging.
     *
     *  @return a String containing the dump.
     */
    public String toString() {
        StringBuffer d = new StringBuffer();        
        d.append(" VMIIf ---------------------------- \n");
        d.append("    expression = " + e + "\n");
        d.append("    value = " + value + "\n");
        d.append("    target= " + target + "\n");
        return d.toString();                
    }
    
	// --- PRIVATE METHODS ---------------------------------------------------

} 
