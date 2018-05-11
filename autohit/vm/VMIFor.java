/**
 * .
 * Copyright © 1999 Erich P G.
 *
 */
 
package autohit.vm;

import java.io.Serializable;

/**
 * A Virtual Machine instruction.  This impliments the head of a FOR loop.
 * The loop should resolve to a RSCOPE immeadiately following a JUMP
 * instruction.  (The JUMP instruction should point back to THIS
 * instruction).  
 *
 * <pre>
 * ip (Instruction Pointer) Flow
 *
 *                         $count$ == 0
 *                   -----------------------------
 *                   |                           |
 *                   |                           V
 *     [SCOPE][SET][FOR] ..code block.. [JUMP][RSCOPE]
 *                   ^                    |
 *                   |---------------------
 *                        always
 *
 * SET should only be emitted if the init attribute is present.
 *
 * </pre>
 * @see autohit.vm.VMInstruction
 * @see autohit.vm.VMIJump
 *
 * @author Erich P. Gatejen
 * @version 1.0
 * <i>Version History</i>
 * <code>EPG - Initial - 5Jan99</code> 
 * 
 */
public class VMIFor extends VMInstruction {
	
	// --- FINAL FIELDS ------------------------------------------------------	

	// --- FIELDS ------------------------------------------------------------

    /**
     * Count variable.  Will contain a variable reference.  Should
     * the referenced variable = "0", the for should break.
     * @serial
     */ 
    public String   count;


    /**
     * Target for when the FOR is broken.
     * @serial
     */ 
    public int      target;

	// --- PUBLIC METHODS ----------------------------------------------------	

    /**
     *  Default constructor.
     */ 
    public VMIFor() {
        nToken = VMInstruction.FOR;   
    }
    

    /**
     *  Dump this Instruction.  Mostly for debugging.
     *
     *  @return a String containing the dump.
     */
    public String toString() {
        StringBuffer d = new StringBuffer();        
        d.append(" VMIFor ---------------------------- \n");
        d.append("    count = " + count + "\n");
        d.append("    target = " + target + "\n");
        return d.toString();                
    }
	// --- PRIVATE METHODS ---------------------------------------------------	


} 
