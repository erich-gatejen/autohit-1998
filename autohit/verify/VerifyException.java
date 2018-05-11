/**
 * .
 * Copyright © 1999 Erich P G.
 *
 */
 
package autohit.verify;

/**
 * A verification exception.  For now, this just encapsulates a plain,
 * old Exception.
 *
 * @author Erich P. Gatejen
 * @version 1.0
 * <i>Version History</i>
 * <code>EPG - Initial - 5Feb99</code> 
 * 
 */
public class VerifyException extends Exception {
	
	// --- FINAL FIELDS ------------------------------------------------------	
     
	// --- FIELDS ------------------------------------------------------------

	// --- PUBLIC METHODS ----------------------------------------------------	


    /**
     *  Default Constructor.
     */      
     public VerifyException() {
        
        super("Unknown VerifiyException");       
     }

    /**
     *  Message constructor
     */      
     public VerifyException(String message) {
          
          super(message);
      
     }     

	// --- PRIVATE METHODS ---------------------------------------------------	

} 
