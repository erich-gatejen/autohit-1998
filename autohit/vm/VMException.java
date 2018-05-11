/**
 * .
 * Copyright © 1999 Erich P G.
 *
 */
 
package autohit.vm;

/**
 * A VM exception.  The specific error is given in the numeric
 * field.
 *
 * @author Erich P. Gatejen
 * @version 1.0
 * <i>Version History</i>
 * <code>EPG - Initial - 18Jan99</code> 
 * 
 */
public class VMException extends Exception {
	
	// --- FINAL FIELDS ------------------------------------------------------	
     
    /**
     *  Numeric values for the exception.
     */ 
     public static final int   UNKNOWN                 = 0;
     public static final int   INVALID_INSTRUCTION     = 1;
     public static final int   VARIABLE_NOT_DEFINED    = 2;     
     public static final int   SOFTWARE_DETECTED_FAULT = 3;
     public static final int   VARIABLE_TYPE_MISMATCH  = 4;
     public static final int   PREPARE_EXCEPTION       = 5;     
     public static final int   DONE                    = 6;     
     public static final int   SUBSYSTEM_FAULT         = 7;     
     
	// --- FIELDS ------------------------------------------------------------

    /**
     *  Numeric.
     *  @serial
     */      	    
     public int        numeric;
    
	// --- PUBLIC METHODS ----------------------------------------------------	


    /**
     *  Default Constructor.
     */      
     public VMException() {
        
        super("Unknown VMException");
        numeric = UNKNOWN;        
     }


    /**
     *  Numeric only constructor.  This will not set a text message.
     */      
     public VMException(int  num) {
          
          super();
          numeric = num;
     }
     
    /**
     *  Numeric and message constructor
     */      
     public VMException(int num, String message) {
          
          super(message);
          numeric = num;         
     }     

	// --- PRIVATE METHODS ---------------------------------------------------	

} 
