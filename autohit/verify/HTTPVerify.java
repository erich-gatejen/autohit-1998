/**
 * .
 * Copyright © 1999 Erich P G.
 *
 */
 
package autohit.verify;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;

import autohit.transport.Response;
import autohit.utils.CRC;


/**
 * A simple HTTP Verify implementation.
 * <p>
 * Current issues: <br>
 * 1- Doesn't handle 'serious' encodings.<br>
 *
 * @author Erich P. Gatejen
 * @version 1.0
 * <i>Version History</i>
 * <code>EPG - Initial - 5Feb99</code> 
 * 
 */
public class HTTPVerify implements Verify {
	
	// --- FINAL FIELDS ------------------------------------------------------

	// --- FIELDS ------------------------------------------------------------

    /** 
     * Current response context.
     */
    private Response  rContext;
    
    /**
     * Reader for the content.
     */ 
    private InputStreamReader       cin;

    /**
     * CRC machine.
     */ 
    private CRC     crcMachine;
    
    /**
     * Last delta.
     */ 
    private int     delta;          
    

	// --- PUBLIC METHODS ----------------------------------------------------
	
    /**
     *  Default constructor.
     */     
    public HTTPVerify() {
        
        rContext   = null;    // be paranoid
        crcMachine = new CRC();
    }        
        
    /**
     *  Create a fresh verification context.
     *  
     *  @param address Address specification.
     *
     *  @throws autohit.transport.VerifyException     
     */     
    public void fresh(Response  context) throws VerifyException {
        
        rContext = context;
        delta = 0;
        this.reset();                
    }

    /**
     *  Reset the current verification context.
     *  
     *  @param address Address specification.
     *
     *  @throws autohit.transport.VerifyException     
     */     
    public void reset() throws VerifyException {

        if (rContext == null) throw new VerifyException(Verify.psVE_NO_CONTEXT); 

        // Build a reader for the content.
        cin = new InputStreamReader(new ByteArrayInputStream(rContext.content));
    }

    /**
     *  A seek operation.
     *  <p>
     *  This will always return true if the expected string is empty.
     *  
     *  @param expected the string to seek.
     *  @return true if it passes verification,otherwise false.
     *
     *  @throws autohit.transport.VerifyException     
     */     
    public boolean seek(String     expected) throws VerifyException {

        int rover = 0;
        int end   = expected.length();

        if (rContext == null) throw new VerifyException(Verify.psVE_NO_CONTEXT);        
        if (end == 0) return true;
        
        // Trap all exceptions.  Any will return a verification fail.
        // This will most certainly happen if the reader is depleted.        
        try {
    
            //  Bleh.  Sometimes this throws an expection, sometimes
            //  it doesnt.  anyways, lookf or EOF.
            int curr = cin.read(); 
            while (curr != -1) {
            
                if (curr == expected.charAt(rover)) {
                    // So far, so good.  Look at the next.
                    rover++;    
                } else {
                    // ACK!  no good.  Jump back to the start of the
                    // expected string.
                    rover = 0;    
                }
                
                // See if we've roved the entire string.  If it did, it
                // passed verification.
                if (rover == end) return true;
      
                curr = cin.read();
            }

        } catch (Exception e) {
        
            // Prolly got here cause we depleted the cin.
            // Pass through and let this method return a fail.
        }
        
        // If for any reason the code gets here, verification failed.
        return false;
    }            

    /**
     *  A CRC check operation.  
     *  
     *  @param expected the expected CRC value.
     *  @return true if it passes verification,otherwise false.
     *
     *  @throws autohit.transport.VerifyException     
     */     
    public boolean crc(int     expected) throws VerifyException {
        
        if (rContext == null) throw new VerifyException(Verify.psVE_NO_CONTEXT);        

        int c = crcMachine.calc(rContext.content, 0, rContext.content.length);

// DEBUG
//System.out.println("CRC:  c=" + c + "  expected=" + expected);

        delta = c - expected;

        if (delta == 0) return true;
        else return false;
    }            
 
    /**
     *  A size check operation.  
     *  
     *  @param expected the expected size.
     *  @return true if it passes verification,otherwise false.
     *
     *  @throws autohit.transport.VerifyException     
     */     
    public boolean size(int     expected) throws VerifyException {
        
        if (rContext == null) throw new VerifyException(Verify.psVE_NO_CONTEXT);        

        delta = rContext.cLength - expected;

        if (delta == 0) return true;
        else return false;
    }
    
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
    public int lastDelta() throws VerifyException {

        if (rContext == null) throw new VerifyException(Verify.psVE_NO_CONTEXT);         
        return delta;    
    }           

    /**
     *  Run a sub-executable to perform a verification.
     *  <p>
     *  Scrubbed for now...  I need to ponder this entire mechanism.  It will
     *  always return TRUE.
     *  
     *  @param invocation an invocation string.  used by the specific verification
     *         to determine what to run and how to run it.
     *  @param content passed to the sun-executable as content.
     *  @return true if it passes verification,otherwise false.
     *
     *  @throws autohit.transport.VerifyException     
     */     
    public boolean exec(String  invocation, String  content) throws VerifyException {

        if (rContext == null) throw new VerifyException(Verify.psVE_NO_CONTEXT);
        
        return true;       


    }        

 
	// --- PRIVATE METHODS ---------------------------------------------------	
} 
