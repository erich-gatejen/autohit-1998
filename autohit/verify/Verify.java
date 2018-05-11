/**
 * .
 * Copyright © 1999 Erich P G.
 *
 */
 
package autohit.verify;

import autohit.transport.Response;

/**
 * An interface to a standard verification facillity.
 * <p>
 * this may seem like an unneccessary layer of abstraction, but I have
 * plans for the future...
 *
 * @author Erich P. Gatejen
 * @version 1.0
 * <i>Version History</i>
 * <code>EPG - Initial - 5Feb99</code> 
 * 
 */
public interface Verify {
	
	// --- FINAL FIELDS ------------------------------------------------------

	// --- FIELDS ------------------------------------------------------------

    /**
     *  Test for exceptions.  Prolly not the best place to put this...
     */ 
    public String   psVE_NO_CONTEXT = "No Response Context set."; 

	// --- PUBLIC METHODS ----------------------------------------------------
	
    /**
     *  Create a fresh verification context.
     *  
     *  @param address Address specification.
     *
     *  @throws autohit.transport.VerifyException     
     */     
    public void fresh(Response  context) throws VerifyException;

    /**
     *  Reset the current verification context.
     *  
     *  @param address Address specification.
     *
     *  @throws autohit.transport.VerifyException     
     */     
    public void reset() throws VerifyException;

    /**
     *  A seek operation.  
     *  
     *  @param expected the string to seek.
     *  @return true if it passes verification,otherwise false.
     *
     *  @throws autohit.transport.VerifyException     
     */     
    public boolean seek(String     expected) throws VerifyException;    

    /**
     *  A CRC check operation.  
     *  
     *  @param expected the expected CRC value.
     *  @return true if it passes verification,otherwise false.
     *
     *  @throws autohit.transport.VerifyException     
     */     
    public boolean crc(int     expected) throws VerifyException;        
 
    /**
     *  A size check operation.  
     *  
     *  @param expected the expected size.
     *  @return true if it passes verification,otherwise false.
     *
     *  @throws autohit.transport.VerifyException     
     */     
    public boolean size(int     expected) throws VerifyException;
    
    /**
     *  Returns the numeric difference from the previous operation.  Currently,
     *  if will return the diff between the expected and actual values calculated  
     *  in crc and size.
     *  <p>
     *  It is calculated as actual - expected.
     *
     *  @return numeric delta.  It will be 0, if there was no difference.
     *
     *  @throws autohit.transport.VerifyException     
     */     
    public int lastDelta() throws VerifyException;
    
    /**
     *  Run a sub-executable to perform a verification.  
     *  
     *  @param invocation an invocation string.  used by the specific verification
     *         to determine what to run and how to run it.
     *  @param content passed to the sun-executable as content.
     *  @return true if it passes verification,otherwise false.
     *
     *  @throws autohit.transport.VerifyException     
     */     
    public boolean exec(String  invocation, String  content) throws VerifyException;    

 
	// --- PRIVATE METHODS ---------------------------------------------------	
} 
