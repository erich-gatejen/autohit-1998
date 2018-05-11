/**
 * .
 * Copyright © 1999 Erich P G.
 *
 */

package autohit.utils;

import java.util.Vector;

/**
 * A locked queue.  This will allow one thread to safely enqueue
 * objects for other threads.  All of the methods are thread-safe.
 *
 * @author Erich P. Gatejen
 * @version 1.0
 * <i>Version History</i>
 * <code>EPG - Initial - 15Jan99</code> 
 * 
 */
public class LockedQueue {
   
	// --- FINAL FIELDS ------------------------------------------------------	

	// --- FIELDS ------------------------------------------------------------

    /**
     * The object monitor for the queue.
     *
     * @see autohit.utils.ObjectMonitor
     */      	    
     private ObjectMonitor  myLock;
     
    /**
     *  The actual queue.
     */           
     private Vector	     queue;
       
    /**
     *  The queue size.  Using this instead of Vector.size() so
     *  that polling is faster.
     */      
     private int         queueSize;

    /**
     *  Constructor.
     */
     public LockedQueue() {
          myLock    = new ObjectMonitor();
          queue     = new Vector();
          queueSize = 0;
     }

    /**
     *  Get an object from the queue.  This method will block
     *  if other threads are enqueuing items, so watch out in
     *  'real-time' threads.
     *
     *  @return an Object reference or null if the queue is empty.
     */
     public Object get() {

          Object c = null;

          // blocks until a message arrives
          myLock.lock(true);
          if (!queue.isEmpty()) {
               c = (Object) queue.elementAt(0);         
               queue.removeElementAt(0);
               queueSize--;
          }
          myLock.lock(false);
          return c;
     }
   
    /**
     *  Put an object in the queue.
     *
     *  @param c the object to enqueue.
     */
     public void put(Object c) {

          myLock.lock(true);
          queue.addElement(c);
          queueSize++;
          myLock.lock(false);
          block(false);
     }
   
    /**
     *  Asks if the queue has any objects.
     *
     *  @return true if there is an object in the queue,
     *  otherwise false.
     */
     public boolean hasObject() {
          if (queueSize > 0) return true;
          else return false;
     }

    /**
     *  Blocks until an object is enqueued by another thread.
     *  Being released fromt he block is no guarentee that an object
     *  available for the thread, if it is possible for another
     *  thread to swoop in an unenqueue one.
     *
     *  @param set pass true to block.  Passing false will
     *             unblock any waiting threads, so be careful!
     */
     public synchronized void block(boolean  set) {

	// Kinda a hack.  set=true means block on this object
	// set = false means release the block
	// put automatically calls this with false to release it.

          if (set == true) {
               try {
                    wait();
               } catch (InterruptedException e) {}
          
          } else {
               notifyAll();
          }
    }
        
}
