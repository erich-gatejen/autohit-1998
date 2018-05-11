/**
 * .
 * Copyright © 1999 Erich P G.
 *
 */
 
package autohit;

import java.util.Vector;

import autohit.vm.*;
import autohit.utils.Log;
import autohit.transport.Transport;
import autohit.transport.Response;
import autohit.transport.Query;
import autohit.verify.Verify;

import HTTPClient.NVPair;

/**
 * A VM for a Sim.  This version does not handle scope caches for headers or body
 * elements.  I'm not sure how much faster they would make the VM, so I'll wait.
 * <p>
 * <br>NOTES for Sim coders --------------------------------------------------<br>
 *  
 * A string will only be resolved once, therefore a variable may only be substituted in
 * a string once.  No references to reference.  :-)<br>
 *
 * SET.  Both name and value will be resolved.  All variables preceded with a '!' will be
 * set on the registered transport.  It will *not* be removed from the transport when
 * it falls out of scope!<p>
 * WAIT.  This instruction will block the VM until the time expires. <p>
 *
 * <p>
 * -----------------------------------------------------------------------<br>
 * 
 * This VM expects the following environment variables to be set
 * before the execute() method is called.  If they aren't set, the defaults
 * will be used.
 * <p>
 * <pre>
 * VAR                | DESC                         | DEFAULT            |
 * ------------------------------------------------------------------------
 * $sname$            | Session name.  Added to log  | The Sim.name       |
 *                    | entries                      |                    |
 *
 * </pre>
 *
 * @see autohit.vm.VM
 * @see autohit.Sim
 *
 * @author Erich P. Gatejen
 * @version 1.0
 * <i>Version History</i>
 * <code>EPG - Initial - 5Jan99
 * EPG - Ok, try again using new VM architecture. - 22Jan99</code> 
 * 
 */
public class SimVM extends VM {
	
	// --- FINAL FIELDS ------------------------------------------------------	

	// --- FIELDS ------------------------------------------------------------

    /**
     *  The Sim to execute.
     *
     *  @see autohit.Sim
     */
    private Sim         mySim;
    
    /**
     *  Last response.  Verification instructions will reference THIS
     *  field.
     *
     *  @see autohit.transport.Response
     */
    private Response    lResponse;    

    /**
     *  Session name.  This will be appended to each logged line.
     *  <p>
     *  If a $sname$ environment var was set for this sim, it will be used.
     *  Otherwise, the Sim.name will be used.
     */
    private String      sname;
    
    /**
     *  The logging mechinism.
     *
     *  @see autohit.utils.Log
     */
    public  Log         myLog;        
       
    /**
     *  The transport mechinism.
     *
     *  @see autohit.transport.Transport
     */
    public  Transport   myTransport;
    
    /**
     *  The verificationt mechinism.
     *
     *  @see autohit.verify.Verify
     */
    public  Verify   myVerify;    
    

	// --- PUBLIC METHODS ----------------------------------------------------	

    /**
     *  Constructor.  Will create the Sim VM but will not start execution; use the
     *  start() method for that.
     *  <p>
     *  This class assumes that the logging object has already been started.
     *  <p>
     *  Do NOT use the default constructor!  Call this constructor.
     *  
     *  @param theSim the Sim to be executed.
     *  @param logTarget target for logging.  
     *  @param myTransport transport mechanism to use.  This is for future use; you
     *                     can pass null to it.
     *  @param myVerify verification mechanism.
     */
    public SimVM(Sim    theSim,  Log    logTarget, Transport   theTransport,
                 Verify theVerify ) {
        
        super();
        
        mySim       = theSim;
        myLog       = logTarget;
        myTransport = theTransport;
        myVerify    = theVerify;
    }

    /**
     *  Prepare for execution of the first instruction.  We need to 
     *  add environment variables.  DO NOT call this method directly.
     *
     *  @throws Any exceptions it encounters.
     */     
     public void prepare() throws Exception {
        
        // Check for default environment variables
        if (vars.containsKey("sname")) {
            sname = (String)vars.get("sname");  
        } else {
            vars.put("sname", mySim.name);
            sname = mySim.name;               
        }

        // Set stock environment vars
        vars.put("lastVerify", "0");
        vars.put("transCode", "0");          
       
     }
    
    /**
     *  Implements the inherited abstract method execute(). Call this to execute
     *  a single instruction,  The first call will be automatic after the inherited
     *  start() method is called.  From there, the owning Object/Thread should 
     *  call this method for each successive instruction to execute.
     *  <p>
     *  This method will throw a VMException(VMException.DONE) if there are
     *  no more instructions that can be executed.  (The ip is past the
     *  end of the exec Vector). 
     *  <p>
     *  We shall assume that a VMContext will handle
     *  threading issues.
     *  @see autohit.vm.VMContext
     *  @throws VMException
     *  @see autohit.vm.VMException
     */     
    public void execute() throws VMException {
        
        // Any instructions left to execute?
        if (ip >= mySim.exec.size()) {
            throw new VMException(VMException.DONE);    
        }
        
        try {
            
            VMInstruction ci = (VMInstruction)mySim.exec.get(ip);
            // NOTES:  Each instruction is responsible for advancing the
            // ip.
//DEBUG
//System.out.println("IP = " + ip + " token=" + ci.nToken);   

            switch(ci.nToken) {
             
                case VMInstruction.NOP:
                    // Just burn the cycle
                    ip++;
                    break;
     
                case VMInstruction.GET:
                    handleGet((VMIGet)ci);
                    ip++;       
                    break;
     
                case VMInstruction.FOR:
                    handleFor((VMIFor)ci);
                    break;
     
                case VMInstruction.WHILE:
                    handleWhile((VMIWhile)ci);
                    break;
     
                case VMInstruction.SET:
                    handleSet((VMISet)ci);
                    ip++;       
                    break;
     
                case VMInstruction.WAIT:
                    handleWait((VMIWait)ci);
                    ip++;       
                    break;
     
                case VMInstruction.SCOPE:
                    // Just toss it on the scope stack
                    pushScope(ci);
                    ip++;       
                    break;
     
                case VMInstruction.RSCOPE:
                    // Let the VM unravel the scope stack.
                    discardScopeFrame();
                    ip++;       
                    break;
     
                case VMInstruction.HEADER:
                    // Just toss it on the scope stack
                    pushScope(ci);
                    ip++;       
                    break;
     
                case VMInstruction.IF:
                    handleIf((VMIIf)ci);    
                    break;
     
                case VMInstruction.NV:
                    // Just toss it on the scope stack            
                    pushScope(ci);
                    ip++;                                  
                    break;
     
                case VMInstruction.JUMP:
                    // Change the instruction pointer to the target
                    VMIJump vmij = (VMIJump)ci;
                    ip = vmij.target;
                    break;
     
                case VMInstruction.ADD:
                    handleAdd((VMIAdd)ci);
                    ip++;       
                    break;
                    
                case VMInstruction.VERIFY:
                    handleVerify((VMIVerify)ci);
                    ip++;       
                    break; 
                                     
                case VMInstruction.CRC:
                    handleCrc((VMICrc)ci);
                    ip++;       
                    break;
                              
                case VMInstruction.SEEK:
                    handleSeek((VMISeek)ci);
                    ip++;       
                    break;
                    
                case VMInstruction.EXEC:
                    handleExec((VMIExec)ci);
                    ip++;       
                    break;
                    
                default:
                    String em = new String("Software Detected Fault: Unsupported instruction encounted by autohit.SimVM.  nToken=[" + ci.nToken + "]");
                    wfLogPut(em);
                    throw new VMException(VMException.INVALID_INSTRUCTION, em);
            }  
            
        } catch (VMException e) {
         
            wfLogPut("!FAULT! in SimVM.  Exception #" + e.numeric);
            wfLogSub("message= " + e.getMessage());            
            throw e;
        }       
        
    }      
    

    // == ===============================================================================
    // == =                         INSTRUCTION HANDLERS                                =
    // == ===============================================================================
    
    private void handleSet(VMISet   instr) throws VMException {

        String name  = instr.name;
        String value = instr.value;
        
        // Resolve any variables.        
        if (instr.iv) {
            name  = subVar(name);
            value = subVar(value);    
        }
        
        // If it isn't already in the vars, toss a reference on the scope stack.
        if (!vars.containsKey(name)) {
            pushScope(name);    
        }
        
        vars.put(name, value);
        
        // Do we need to set it with the transport?       
        if(name.charAt(0) == '!') {

            try {
                myTransport.environment(name.substring(1), value);
        
            } catch (Exception e) {
                throw new VMException(VMException.VARIABLE_TYPE_MISMATCH, "Empty transport environment variable");
            }
        }
    }

    private void handleWait(VMIWait   instr) throws VMException {

        String time     = instr.time;
        int    numeric  = 0;
        
        // Resolve any variables.        
        if (instr.iv) {
            time  = subVar(time);
        }
        
        // try to get a number out of the time field
        try {    
            numeric = Integer.parseInt(time);
        
        } catch (Exception e) {
            throw new VMException(VMException.VARIABLE_TYPE_MISMATCH , "Time is not a numeric. [" + time + "]");
        }
        
        // Wait...
        try {
            wfLogPut("WAIT: start=" + ticks() + " for " + numeric);
            Thread.sleep(numeric);
               
        } catch (Exception e) {
            // dont care if we are interrupted.    
        }

    }

    private void handleGet(VMIGet   instr) throws VMException {
        
        int tstart = 0;
        int tlen   = 0;
        
        try {

            Query q   = buildQuery();
            
            if (instr.iv) {
                q.qs  = subVar(instr.qs);
            } else {
                q.qs = instr.qs;    
            }
            
            tstart = this.ticks();
            lResponse = myTransport.push(q);
            tlen   = this.ticks() - tstart;
            
            wfLogPut("GET:" + "[s=" + tstart +
                      " l=" + tlen + "] tcode=" + lResponse.code +
                      " size=" + lResponse.cLength);
            wfLogSub("url=" + q.qs);
            
            vars.put("transCode", Integer.toString(lResponse.code));               

        } catch (Exception  e) {

            // Prolly should come back and make this exception more informative....
            wfLogPut("GET: Failed do to exception! for url=");
            wfLogSub(instr.qs);
            wfLogSub("Exception =" + e.getMessage());
        }        
    }
    
    private void handleIf(VMIIf   instr) throws VMException {

        String e     = instr.e;
        String value = instr.value;
        
        // Resolve any variables.        
        if (instr.iv) {
            e  = subVar(e);
            value = subVar(value);    
        }
        
        // Are they equal?
        if (e.equals(value)) {      
         
            // Yes.  Advance IP to next instruction
            ip++;
            
        } else {
                     
            // Nope.  Jump over the block
            ip = instr.target;   
        }
    }    

    private void handleFor(VMIFor   instr) throws VMException {

        String count = instr.count;
        
        // automatically resolve any variables.        
        count = subVar(count);
        
        String value = (String)vars.get(count);       

        // Are they equal?
        if (value.equals("0")) {
         
            // Yes.  JJump to the target
            ip = instr.target;
            
        } else {
            
            // Nope.  Next instruction
            ip++;   
        }
    }    

    private void handleWhile(VMIWhile   instr) throws VMException {

        String e     = instr.e;
        String value = instr.value;
        
        // Resolve any variables.        
        if (instr.iv) {
            e  = subVar(e);
            value = subVar(value);    
        }

        // Are they equal?
        if (e.equals(value)) {            
              
            // Yes.  jump back and do the loop again
            ip = instr.target;
            
        } else {
                        
            // Nope.  move out of the block
            ip++;   
        }
    }    

    private void handleAdd(VMIAdd   instr) throws VMException {

        String name  = instr.name;
        String value = instr.value;
        int    result = 0;
        
        // Resolve any variables.        
        if (instr.iv) {
            name  = subVar(name);
            value = subVar(value);    
        }
        
        // Find the variable.
        String varVal = (String)vars.get(name);
        if (varVal == null) {
            throw new VMException(VMException.VARIABLE_NOT_DEFINED , "Variable not defined for ADD. [" + name + "]");            
        }
        
        // Try add them.
        try {    
            result = Integer.parseInt(varVal) + Integer.parseInt(value);
            
        } catch (Exception e) {
            throw new VMException(VMException.VARIABLE_TYPE_MISMATCH , "Type mismatch in add. [" + varVal + "][" + value + "]");
        }
                
        vars.put(name, String.valueOf(result));
    }

    private void handleVerify(VMIVerify   instr) throws VMException {

        try {
        
            // freshed the context
            myVerify.fresh(lResponse);
            
            // verify size if we must...
            if (instr.size != VMIVerify.NO_SIZE) {
                
                if (myVerify.size(instr.size)) {
                    wfPass("SIZE.");                    
           
                } else {     
                    wfFail("SIZE.   difference=" + myVerify.lastDelta());
                }               
            }
                         
        } catch (Exception e) {
            throw new VMException(VMException.SUBSYSTEM_FAULT , "Verification Subsystem fault.  Unable to freshen context.  sub=" + e.getMessage());    
        }
    }

    private void handleCrc(VMICrc   instr) throws VMException {

        try {
        
            if (myVerify.crc(instr.expected)) {

                wfPass("CRC.");
       
            } else {
                
                wfFail("CRC.  difference=" + myVerify.lastDelta());
            }
                         
        } catch (Exception e) {
            throw new VMException(VMException.SUBSYSTEM_FAULT , "Verification Subsystem fault.  Unable to verify CRC.  sub=" + e.getMessage());    
        }
    }
    
    private void handleSeek(VMISeek   instr) throws VMException {

        try {
        
            if (myVerify.seek(instr.expected)) {

                wfPass("SEEK.");
       
            } else {
                
                wfFail("SEEK.  expected=" + instr.expected);
            }
                         
        } catch (Exception e) {
            throw new VMException(VMException.SUBSYSTEM_FAULT , "Verification Subsystem fault.  Unable to verify SEEK.  sub=" + e.getMessage());    
        }
    }    

    private void handleExec(VMIExec   instr) throws VMException {

        try {
        
            if (myVerify.exec(instr.invocation, instr.content)) {

                wfPass("EXEC.");
       
            } else {
                
                wfFail("EXEC.");
            }
                         
        } catch (Exception e) {
            throw new VMException(VMException.SUBSYSTEM_FAULT , "Verification Subsystem fault.  Unable to verify EXEC.  sub=" + e.getMessage());    
        }
    }  


    // == ===============================================================================
    // == =                            PRIVATE METHODS                                  =
    // == ===============================================================================
    
    private void wfLogPut(String  text) {

        myLog.put(sname + ":" + text);   
    }
    
 
    private void wfLogSub(String  text) {

        myLog.putSub(sname + ":" + text);   
    }
    
    private void wfPass(String  text) {

        vars.put("lastVerify", "1");
        myLog.putSub("PASS :" + text);   
    }
    
    private void wfFail(String  text) {

        vars.put("lastVerify", "0"); 
        myLog.putSub("FAIL :" + text);   
    }           

    // Does the grunt work of looking throw the scope stack for headers and NVs
    private Query buildQuery() throws VMException {

        Vector  h = new Vector();
        Vector  b = new Vector();
        
        String  name;
        String  value;
        
        Object    so;
        VMIHeader hobj;
        VMINV     nobj;
        
        NVPair  nv;
        
        Query q  = new Query();

        // Run the scope stack.
        int size = scope.size() - 1;
        while (size > 0) {
            
            so = scope.elementAt(size);
            
            if ( so instanceof VMIHeader) {
            
                hobj = (VMIHeader)so;
                
                name  = subVar(hobj.name);
                value = subVar(hobj.value);       
                
                nv = new NVPair(name, value);
                h.add(nv);
            
            } else if (so instanceof VMINV) {
                        
                nobj = (VMINV)so;        
                        
                name  = subVar(nobj.name);
                value = subVar(nobj.value);       
                
                nv = new NVPair(name, value);
                b.add(nv);            
            }
        
            size--;   
        }
        
        // build the Query
        if (h.size() > 0) {

            q.headers = (HTTPClient.NVPair[]) h.toArray(new NVPair[h.size()]);
            
        } else {
            q.headers = null;
        }
        
        if (b.size() > 0) {

            q.body = (HTTPClient.NVPair[]) b.toArray(new NVPair[b.size()]);
            
        } else {
            q.body = null;
        }
        
        return q;
    } 
  
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 


} 
