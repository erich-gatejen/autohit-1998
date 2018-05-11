/**
 * .
 * Copyright © 1999 Erich P G.
 *
 */
 
package autohit.vm;

import autohit.utils.ObjectMonitor;

/**
 * A VM context.  It will wrap a VM in a thread and do all the
 * thread-safe kinda stuff.  A context will handle successive
 * VM runs, so you can use them in a thread pool.
 * <p>
 * BE SURE to .start() this Thread BEFORE any other threads
 * access its methods (particularly execute()).  Failure to heed this
 * warning COULD result in a race condition...  :-)
 * <p>
 * @author Erich P. Gatejen
 * @version 1.0
 * <i>Version History</i>
 * <code>EPG - Initial - 19Jan99 
 * EPG - Fixed bug. - 19Fef99  
 *    - This thread doesn't need to be daemonized.  It seems it wasn't dying properly
 *      because of a rather stupid bug.  I'll leave it a daemon for now, just cause.
 * </code>
 */
public class VMContext extends Thread {
	
	// --- FINAL FIELDS ------------------------------------------------------	
     
	// --- FIELDS ------------------------------------------------------------

    /**
     *  A runnable VM.  This will be a fully implemented derived-class
     *  of VM.
     */      	    
     protected VM       rVM;
     
    /**
     *  An object monitor for blocking.
     */      	    
     private ObjectMonitor   vsBlock;
     
    /**
     *  Alive.  true means we should keep goin'
     */      	    
     private boolean  alive;           

    /**
     *  Request pause
     */      	    
     private boolean  reqPause;  

    /**
     *  Request stop
     */      	    
     private boolean  reqStop;
     
    /**
     *  Request resume
     */      	    
     private boolean  reqResume;
          
    /**
     *  Request kill
     */      	    
     private boolean  reqKill;
    
	// --- PUBLIC METHODS ----------------------------------------------------	


    /**
     *  Constructor.
     */      
     public VMContext() {

          // Make this when we run.  otherwise, we wont own it,
          vsBlock = null;

          // be paranoid
          rVM     = null;
          alive   = true;
          reqKill = false;
          
          this.setDaemon(true);             
     }

    /**
     *  Load and Execute a VM.  If a VM is already running, it will return 
     *  false.  Otherwise, it will return true.
     *
     *  This method can be called by any thread.
     *
     *  @param aVM A fully implimented derived-class of VM.
     *  @return true if successful and execution begun, false if
     *               another VM is already running.
     *
     *  @see autohit.vm.VM    
     */  
     public synchronized boolean execute(VM  aVM) {

          if (rVM != null) return false;

          rVM = aVM;

          // notify() this Thread Object in case it has
          // blocked itself.  Prolly could use notify(), but why risk
          // it.
          this.notifyAll();
          return true;          
     }

    /**
     *  Pause the vm.  This may be called by another thread.
     *
     *  As with vmResume() and vmStop(), it posts a request to the
     *  VMContext.  The context will not heed the request until
     *  the current VM.execute() is complete and the context has
     *  a chance to check for these requests.
     *  <p>
     *  There is no rendezvous; the method will not block.  All successive
     *  requests count as the same request until the context services
     *  it.  There is no guarentee on the timing of the service.  The Context
     *  will not check for requests until AFTER the current instruction is complete.
     *  So, if the vm is executing a long wait instruction, it could indeed be
     *  some time before the request is serviced.
     *  <p>
     *  If you need to make sure that the request worked, then use
     *  the verifyState() method to get the definative state of the
     *  VM/Context.
     *  <p>
     *  As for the return value, "success" merely means that the
     *  request was successfully posted and not that the action
     *  was completed.
     *  <p>
     *  One last thaught: this is not a robust OS implimentation of
     *  a thread context.  You might want to restrict calls to vmPause()
     *  and vmResume() to a single external thread.  Multiple threads
     *  might get confused if they don't cooperate...
     *
     *  @return true is successful.  false if no vm is running or
     *               it is already paused.
     *  @see autohit.vm.VM    
     */  
     public boolean vmPause() {
          if (rVM.getState() == VM.STATE_RUNNING) {
               reqPause = true;
               return true;
          } else return false;
     }     

    /**
     *  Resume the vm.
     *  <p>
     *  See the notes for the pause() method.
     *
     *  @return true is successful.  false if no vm is paused or
     *               it is already running.
     *  @see autohit.vm.VM    
     */  
     public boolean vmResume() {
          if (rVM.getState() == VM.STATE_PAUSED) {
               reqResume = true;
               return true;
          } else return false;          
     }
     
    /**
     *  Stop the vm.  This will kill it permanently, so be careful.
     *  <p>
     *  See the notes for the pause() method.
     *
     *  @return true is successful.  false if no vm is running or
     *               paused.
     *  @see autohit.vm.VM    
     */  
     public boolean vmStop() {
          if (rVM == null) return false;
          else {
               reqStop = true;
               return true;
          } 
     }

    /**
     *  Kill this context.  This is a request, so it may not happen
     *  immeadiately.  Once it does, it is irrevocable as the Thread
     *  run() method will be allowed to return...
     *  <p>
     *  See the notes for the pause() method.
     *
     *  @return always returns true.
     *  @see autohit.vm.VM    
     */  
     public synchronized boolean kill() {
          reqKill = true;
          alive   = false;         
          this.notify();  // in case it is waiting for execute().         
          return true;
     }
     
    /**
     *  Verify the state of the VM.  It will report a VM state 
     *  value as defined in the VM class--VM.State_*.  This will
     *  be the authorative state, as this method blocks until the
     *  VM has chance to clear requests and unblock it.
     *  <p>
     *  (And, never EVER call this method from within THIS thread.
     *   You'll almost certainly deadlock it.)
     *  <p>
     *  The following describes each state:
     *  <pre>
     *         STATE_NEW          = VM is loaded bu not started
     *         STATE_RUNNING      = VM is actively running.
     *         STATE_PAUSED       = VM is paused.
     *         STATE_DONE         = VM finished execution.
     *                              This is rare, as the VM will
     *                              be automatically unloaded when
     *                              finished.
     *         STATE_NO_VM        = No VM is loaded into this context.
     *  </pre>
     * 
     *  @return a VM.State_* value.
     *  @see autohit.vm.VM    
     */  
     public int verifyState() {
        
        try {  
            vsBlock.ownWait();
        } catch (Exception  e) {
            // Dont care if we are interrupted.    
        }
        return this.getState();  
     }                

    /**
     *  A simple request for state.  It may or not be stale be the
     *  time the calling thread gets it.  If you msut have THE
     *  AUTHORATIVE state, then call verifyState()
     *  
     *  @return a VM.State_* value.
     *  @see autohit.vm.VM    
     */  
     public int getState() {
           
           if (rVM == null) return VM.STATE_NO_VM;         
           else return rVM.getState();
     }      

    /**
     *  Run the context
     */  
     public void run() {
          
          // Make this so that it that is is owned by this thread.
          vsBlock = new ObjectMonitor();          

          // Ok, there is a SLIGHT danger of a race condition here,
          // if someone was able to call execute() after construction
          // but before the this Thread is .start()'ed.
          
          // We shouldn't have anything to do but wait for a VM
          // to execute.
          blockTillExecute();
          
          // Varying VM loop  (19Feb -- what the hell does "varying" mean?)
          do {

               // Any VM exception will make the context
               // dump the VM.  This will also catch the NullPointerException
               // that would be caused by a spurious context thread unblock when
               // a VM has not be set for execution.
               try {
            
                    // Clear the VM requests...  don't care if there are any
                    // pending.
                    reqPause  = false;
                    reqStop   = false;
                    reqResume = false;
                   
                    // Start the VM
                    rVM.start();
                    
                    // Specific VM loop
                    do {
                                 
                         // Stablize state and unblock all waiting for verifyState()
                         if ((reqKill == true)||(reqStop == true)) {
                              // alive = false is already set if reqKill                              
                              vsBlock.ownNotifyAll();
                              break;                        
                         }
                         if (reqPause == true) {
                              rVM.pause();    
                         }
                         if (reqResume == true) {
                              rVM.resume();
                         }                                     
                         vsBlock.ownNotifyAll();
                        
                         // Execute an instruciton.  An exception
                         // kills this VM
                         rVM.execute();             
                        
                    } while (alive == true);  // EPG 18Feb99 I can't beleive I coded this as (alive = true).  This is what
                                              // was keeping the context from dying properly...
               
               } catch (Exception e) {
                    // Don't actuall have to do anything...
               } 
                              
               // The VM will die now.
               rVM = null;
               vsBlock.ownNotifyAll();                            
               
               // Before we sleep, lets make sure a Kill request
               // didnt arrive
               if (alive == false) break;
               blockTillExecute(); 
          
          } while (alive == true);  // EPD 18Feb99 Did the same dumb trick here, too.
          
          // Last out the door turn off the lights...
          // DO NOT put ANY other code beyond the following statements
          // or you are liable to deadlock other Threads.
          vsBlock.ownNotifyAll();
          yield();
     }
     
	// --- PRIVATE METHODS ---------------------------------------------------	

    /**
     *  Wait until someone calls our execute() method.
     */  
     public void blockTillExecute() {
          
        try {
            wait();          
        } catch (Exception e) {
            // Don't care if we are interrupted...
        }

     }
     

} 
