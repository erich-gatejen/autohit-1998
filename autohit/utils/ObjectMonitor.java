/**
 * .
 * Copyright © 1999 Erich P G.
 *
 */

package autohit.utils;

/**
 * A simple object monitor that can be used as a mutex.
 *
 * @author Erich P. Gatejen
 * @version 1.0
 * <i>Version History</i>
 * <code>EPG - Initial - 15Jan99</code> 
 * 
 */
public class ObjectMonitor extends Object {

     private int locked;
     

    /**
     *  Default constructor.  It will create the monitor that
     *  is unlocked.
     */
     public ObjectMonitor() {
          this(1);
     }
     
    /**
     *  Contructor.  Allows you to set the lock on creation.
     *  I recommend against using this one...  I bet you
     *  get yourself deadlocked. :D
     */
     public ObjectMonitor(int lock) {
          locked = lock;
     }
     
    /**
     *  Lock or unlock the monitor.  Trying to lock an already
     *  locked monitor will block the thread until the owning
     *  thread unlocks it.  More than one thread can be waiting
     *  for the lock.  So, watchout for deadlock!
     *
     *  @param lock true locks, false unlocks.  
     */
     public synchronized void lock(boolean lock) {
          if (lock) {
               while (locked == 0) {
                    try {
                         wait();
                    } catch (InterruptedException e) { }
               }
               locked--;
          } else {
               locked++;
               notify();
          }       
     }
     
    /**
     *  This is a syncronized notify.  This way any thread can
     *  notify as the owner.
     */     
    public synchronized void ownNotify() {
        this.notify();    
    }     

    /**
     *  This is a syncronized notifyAll.
     */     
    public synchronized void ownNotifyAll() {
        this.notifyAll();    
    }

    /**
     *  This is a syncronized wait.  Interrupt exception is caught
     *  and discarded.
     */     
    public synchronized void ownWait() {
        try {
            this.wait();
        } catch (Exception e) { }
    } 
     
}

