/**
 * .
 * Copyright © 1999 Erich P G.
 *
 */
 
package autohit.vm;

import java.io.Serializable;

/**
 * A Virtual Machine instruction base class.  All vm instructions
 * extend this class.  This class also defines the static/final
 * tokens and related constants used by both the vm and the
 * compiler.
 * <p>
 * We will implement Serializable with this base class, so all
 * final instruction classes will inherit it,
 * <p>
 * I thought about making this an interface, but since this IS
 * a vm, it seem like a needless slowdown of a class that is 
 * going to get banged around enough as it is.
 * <p>
 * This class defines the numeric token for all instructions.
 * So, if you create a new instruction, be sure to add a token
 * for it in this class...  and recompile ALL of the packages.
 * <p>
 * All derived-class constructors must set the numeric token.
 *
 * @author Erich P. Gatejen
 * @version 1.0
 * <i>Version History</i>
 * <code>EPG - Initial - 5Jan99 
 * EPG - Add new instruction tokens - 3Feb99</code> 
 * 
 */
public class VMInstruction implements Serializable {
	
	// --- FINAL FIELDS ------------------------------------------------------	

    /**
     * Numeric token values.
     *
     * This is used as an optimization so we can do an OpCode
     * switch(nToken) in the VM...
     */ 
     public static final int   NOP     = 0;
     public static final int   GET     = 1;
     public static final int   FOR     = 2;
     public static final int   WHILE   = 3;  
     public static final int   SET     = 5;  
     public static final int   WAIT    = 6;
     public static final int   SCOPE   = 7;
     public static final int   RSCOPE  = 8;
     public static final int   HEADER  = 9;
     public static final int   IF      = 10;     
     public static final int   NV      = 11;     
     public static final int   JUMP    = 12;    
     public static final int   ADD     = 13;    
     public static final int   VERIFY  = 14;
     public static final int   CRC     = 15;  
     public static final int   SEEK    = 16;  
     public static final int   EXEC    = 17;  
     public static final int   ENV     = 18;
     public static final int   SIM     = 19;      
     
    /**
     * Imbedded variable text token.
     *
     * You may NOT imbed a variable in another variable "name" (though,
     * the reverse is possible).
     */ 
    public static final char IVToken    = '$';

	// --- FIELDS ------------------------------------------------------------
    /**
     *  Numeric token.
     *  @serial
     */
    public int  nToken;

    /**
     *  Detected imbedded variable.  An optimization,
     *  @serial
     */
    public boolean  iv;

	// --- PUBLIC METHODS ----------------------------------------------------	

    /**
     * Default constructor
     */
    public VMInstruction() {
        iv = false;        
    }     

    /**
     *  OR the IV with tap.   
     *
     *  <pre>
     *  iv    tap  | new iv
     *  T     T    | T
     *  T     F    | T
     *  F     T    | T
     *  F     F    | F
     *  </pre>
     *
     *  @param tap value to OR against.
     */   
    public void orIV(boolean  tap) {
        
        if ((iv != true)&&(tap != true)) iv = false;
        else iv = true;
    }
    
    /**
     *  Dump this Instruction.  Mostly for debugging.  Subclasses should
     *  override thise
     *
     *  @return a String containing the dump.
     */
    public String toString() {
        StringBuffer d = new StringBuffer();        
        d.append(" VMInstr ---------------------------- \n");
        return d.toString();                
    }    
    
	// --- PRIVATE METHODS ---------------------------------------------------	


} 
