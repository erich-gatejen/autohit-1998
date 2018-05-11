/**
 * .
 * Copyright © 1999 Erich P G.
 *
 */
 
package autohit.vm;

import autohit.utils.LockedQueue;

/**
 * A VM automat.  This is a different kind of VM context.  Basically, it accepts a
 * queue through which it can be controlled.  Send it a Sim to run or an Integer(VM.STATE_*) to 
 * effect the state.  NOTE that these are "requests."  There is no guarentee that the order will
 * be followed.
 * <p>
 * Here is a state diagram which shows some off the effects of control "requests."  kill() is
 * a special case, where another thread calls the Automat's kill() method.
 * <p>
 * <pre>
 *                  | RUNNING      | PAUSED       | NO_VM 
 *  A VM            | ignored      | ignored      | vm is run
 *  STATE_NEW       | ignored      | ignored      | ignored
 *  STATE_RUNNING   | ignored      | vm paused    | ignored
 *  STATE_PAUSED    | vm resumed   | ignored      | ignored
 *  STATE_DONE      | vm discarded | vm discarded | ignored
 *  STATE_NO_VM     | ignored      | ignored      | ignored
 *  die()          | <---  VM Killed and Automat dies ------>  
 * </pre>
 *
 * <p>
 * @author Erich P. Gatejen
 * @version 1.0
 * <i>Version History</i>
 * <code>EPG - Initial - 12Feb99</code> 
 * 
 */
public class VMAutomat extends Thread {
	
	// --- FINAL FIELDS ------------------------------------------------------	
     
	// --- FIELDS ------------------------------------------------------------

    /**
     *  A runnable VM.  This will be a fully implemented derived-class
     *  of VM.
     */      	    
     private VM             rVM;
     

    /**
     *  The control queue.  This thread will watch the queue for things to do.
     */      	    
     private LockedQueue    cQ;


    /**
     *  Flags a kill order.
     */      	    
     private boolean        killed;
          
    
	// --- PUBLIC METHODS ----------------------------------------------------	

    /**
     *  Constructor.
     */      
     public VMAutomat(LockedQueue   controlQueue) {

          // Make this when we run.  otherwise, we wont own it,
          cQ     = controlQueue;
          rVM    = null;
          killed = false;
          
          this.setDaemon(true);             
     }


    /**
     *  Return the last known stable status.  This is not 100% accurate, as the
     *  automate MAY be in the process of shifting status while this is being queried.
     *  It should be close enough for most purposes; the window of possible inaccuracy is 
     *  perdy small...
     *
     *  This method can be called by any thread.
     *
     *  @return a VM.STATUS_* int value.
     *
     *  @see autohit.vm.VM    
     */  
     public synchronized int getLastKnownStatus() {
        
        if (rVM == null) return VM.STATE_NO_VM;
        else return rVM.getState();
     }


    /**
     *  Kills the Automat as soon as possible.
     */      
     public synchronized void kill(LockedQueue   controlQueue) {

          // Make this when we run.  otherwise, we wont own it,
          cQ  = controlQueue;
          rVM = null; 
          
          this.setDaemon(true);             
     }

    /**
     *  Run the automat
     */  
     public void run() {

          Object    dqo;    // temporary dequeued object reference.
          Integer   tio;    // temporary integer object
          
          // Enter a loop until some other thread Kill()s me....
           while (killed == false) {
            
                // Nothing is running.  Wait on the queue for a VM
                if (!cQ.hasObject()) cQ.block(true);
                dqo = cQ.get();
               
                // Only interested in VMs
                if (!(dqo instanceof VM)) continue;               

                // Any VM exception will make the context
                // dump the VM.  This will also catch the NullPointerException
                // that would be caused by a spurious context thread unblock when
                // a VM has not be set for execution.
                try {
                   
                    // Start the VM
                    rVM = (VM)dqo;
                    rVM.start();
                    
                    // Specific VM loop
                    do {
                                 
                        // Check the queue for control objects...  Control objects take precedence
                        // over instruciton execution.
                        if (cQ.hasObject()) {
                         
                            dqo = cQ.get();
                         
                            // Only care about Integers
                            if (dqo instanceof Integer) {
                            
                                tio = (Integer)dqo;
                                switch(tio.intValue()) {
                                    
                                    case VM.STATE_RUNNING:
                                    
                                        // No need to check state.
                                        rVM.resume();
                                        break;                                            
                                    
                                    case VM.STATE_PAUSED:
                                     
                                        // No need to check state.
                                        // Since we have paused, this thread doesnt need
                                        // to do anything until another control arrives.
                                        // Go ahead and block on the queue.
                                        rVM.pause();
                                        cQ.block(true);
                                       break;                                     
                                    
                                    case VM.STATE_DONE:
                                        
                                        // DONE with this VM.  Kill the reference.
                                        rVM = null;
                                    
                                    default:
                                        // ignore all others
                                        break;                                    
                                    
                                } // end switch
                                
                            } // end if Integer
                            
                        } else {

                            // Go ahead and execute an instruction
                            rVM.execute();
                        }                       
                           
                    } while (rVM != null);
               
               } catch (Exception e) {
                    // Don't actuall have to do anything...
               } 
                              
               // The VM will die now... just in case.
               rVM = null;                            
          
          }
          
          // Last out the door turn off the lights...
          // DO NOT put ANY other code beyond the following statements
          // or you are liable to deadlock other Threads.
          yield();
     }
     
	// --- PRIVATE METHODS ---------------------------------------------------	     

} 
