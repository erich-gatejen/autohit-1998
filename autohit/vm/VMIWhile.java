/**
 * .
 * Copyright © 1999 Erich P G.
 *
 */
 
package autohit.vm;

import java.io.Serializable;

/**
 * A Virtual Machine instruction.  This impliments the tail of a 
 * while loop.  If the expression matches the value, then the ip
 * will changed to point to the instruction following the 
 * block starting SCOPE.  Otherwise, the ip is incremented to point
 * to the following RSCOPE.
 * <p>
 * <pre>
 * ip (Instruction Pointer) Flow
 *
 *              expression == value
 *              ------------------
 *              |                |
 *              V                |
 *     [SCOPE] ..code block.. [WHILE][RSCOPE]
 *                               |      ^ 
 *                               -------|
 *                            expression != value
 *
 * </pre>
 * @see autohit.vm.VMInstruction
 *
 * @author Erich P. Gatejen
 * @version 1.0
 * <i>Version History</i>
 * <code>EPG - Initial - 19Jan99</code> 
 * 
 */
public class VMIWhile extends VMInstruction {
	
	// --- FINAL FIELDS ------------------------------------------------------	

	// --- FIELDS ------------------------------------------------------------

    /**
     * Expresion
     * @serial
     */ 
    public String   e;
    
    /**
     * Test value.
     * @serial
     */ 
    public String   value;
    
    /**
     * Target if expression true.
     * @serial
     */ 
    public int      target;        

	// --- PUBLIC METHODS ----------------------------------------------------	

    /**
     *  Default constructor.
     */ 
    public VMIWhile() {
        nToken = VMInstruction.WHILE;   
    }
    

    /**
     *  Dump this Instruction.  Mostly for debugging.
     *
     *  @return a String containing the dump.
     */
    public String toString() {
        StringBuffer d = new StringBuffer();        
        d.append(" VMIWhile -------------------------- \n");
        d.append("    e = " + e + "\n");
        d.append("    value = " + value + "\n");
        d.append("    target = " + target + "\n");
        return d.toString();                
    }
	// --- PRIVATE METHODS ---------------------------------------------------	


} 
