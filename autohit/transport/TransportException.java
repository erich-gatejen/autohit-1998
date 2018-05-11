/**
 * .
 * Copyright © 1999 Erich P G.
 *
 */
 
package autohit.transport;

/**
 * A Transport exception.  For now, this just encapsulates a plain,
 * old Exception.
 *
 * @author Erich P. Gatejen
 * @version 1.0
 * <i>Version History</i>
 * <code>EPG - Initial - 18Jan99</code> 
 * 
 */
public class TransportException extends Exception {
	
	// --- FINAL FIELDS ------------------------------------------------------	
     
	// --- FIELDS ------------------------------------------------------------

	// --- PUBLIC METHODS ----------------------------------------------------	


    /**
     *  Default Constructor.
     */      
     public TransportException() {
        
        super("Unknown TransportException");       
     }

    /**
     *  Message constructor
     */      
     public TransportException(String message) {
          
          super(message);
      
     }     

	// --- PRIVATE METHODS ---------------------------------------------------	

} 
