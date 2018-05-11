/**
 * .
 * Copyright © 1999 Erich P G.
 *
 */
 
package autohit.verify;

import autohit.transport.Response;

/**
 * This is a test verification implementation..
 * <p>
 *
 * @author Erich P. Gatejen
 * @version 1.0
 * <i>Version History</i>
 * <code>EPG - Initial - 7Feb99</code> 
 * 
 */
public class TestVerify implements Verify {
	
	// --- FINAL FIELDS ------------------------------------------------------

	// --- FIELDS ------------------------------------------------------------

	// --- PUBLIC METHODS ----------------------------------------------------  
        
    /**
     *  Create a fresh verification context.
     *  
     *  @param address Address specification.
     *
     *  @throws autohit.transport.VerifyException     
     */     
    public void fresh(Response  context) throws VerifyException {
                  
    }

    /**
     *  Reset the current verification context.
     *  
     *  @param address Address specification.
     *
     *  @throws autohit.transport.VerifyException     
     */     
    public void reset() throws VerifyException {
    
    }

    /**
     *  A seek operation.  
     *  
     *  @param expected the string to seek.
     *  @return always true..
     *
     *  @throws autohit.transport.VerifyException     
     */     
    public boolean seek(String     expected) throws VerifyException {
        System.out.println("TEST VERIFY: SEEK.  expected = " + expected);
        return true;        
    }    

    /**
     *  A CRC check operation.  
     *  
     *  @param expected the expected CRC value.
     *  @return always true.
     *
     *  @throws autohit.transport.VerifyException     
     */     
    public boolean crc(int     expected) throws VerifyException {
        System.out.println("TEST VERIFY: CRC.  expected = " + expected);
        return true;        
    }           
 
    /**
     *  A size check operation.  
     *  
     *  @param expected the expected size.
     *  @return true always true.
     *
     *  @throws autohit.transport.VerifyException     
     */     
    public boolean size(int     expected) throws VerifyException {
        System.out.println("TEST VERIFY: SIZE.  expected = " + expected);        
        return true;        
    }           

    /**
     *  Returns the numeric difference from the previous operation.  Currently,
     *  if will return the diff between the expected and actual values calculated  
     *  in crc and size.  
     *
     *  @return Alway returns 0.
     *
     *  @throws autohit.transport.VerifyException     
     */     
    public int lastDelta() throws VerifyException {
        return 0;    
    }
    

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
    public boolean exec(String  invocation, String  content) throws VerifyException {
        System.out.println("TEST VERIFY: EXEC");
        return true;        
    }           
    
	// --- PRIVATE METHODS ---------------------------------------------------	

     

} 
