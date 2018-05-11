/**
 * .
 * Copyright © 1999 Erich P G.
 *
 */

package autohit.utils;

import java.util.Vector;
import java.util.Date;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileWriter;
import java.io.BufferedWriter;

import autohit.vm.VM;

/**
 * A logging mechanism.  It will add timing information and is syncronized
 * so more than one thread can post.  The data is put in a temp file, so
 * memory usage should not get out of hand ( as does a StringBuffer).
 * <p>
 * All entries will automatically have a LF appended to the end.  Timing
 * information shows up to 7 digits--[0000000].  Granularity is passed to the
 * constructor; note that the timing will automatically be scaled
 * by the VM class.  DO NOT USE the default constructor.
 * <p>
 * Note that while the log timing does support pause() and resume(), it
 * does not account for the timing of client threads, so timing information
 * for items posted by threads may or may not be completely accurate.  (For
 * instance, pausing all the running VMs AND the Log will not happen
 * instantaniously, so there may be some variances in timing when they
 * are all resumed.)
 * <p>
 * To ensure that the temp file is removed, call the close() method when
 * done.
 * <p>
 * Any exceptions that are caught during a put() or putSub() are caught
 * and ignored.  I really don't expect any to come out of those
 * methods, and if they do, then other REAL bad stuff is probibly
 * already underway...
 * <p>
 * This class actually extends the VM base class, as it provides the
 * timing functionality that we want.
 * <p>
 * NOTE!  The timing is started on construction.  If you want to wait,
 * immeadiatly call the pause() method.
 *
 * @see autohit.vm.VM
 *
 * @author Erich P. Gatejen
 * @version 1.0
 * <i>Version History</i>
 * <code>EPG - Initial - 15Jan99</code> 
 * 
 */
public class Log extends VM {
   
	// --- FINAL FIELDS ------------------------------------------------------	
     
    /* 
     *  Speed things up a bit...
     */ 
    String  zeros[] = { "0000000", "000000", "00000", "0000",
                        "000", "00", "0", "" }; 
                          

	// --- FIELDS ------------------------------------------------------------

    /**
     * The temporary file.
     */      	    
     private File        tempFile;
     
    /**
     *  An output writer for the tempFile;
     */           
     private BufferedWriter   out;
     
    /**
     *  An input stream for reading the log temp file.
     */ 
     private FileInputStream     is =  null;

    /**
     *  The object monitor for this log.
     */ 
     private ObjectMonitor        lock;     
     
    /**
     *  Granulatiry for each tick of the VM's clock.  This will
     *  scale the tick value.
     *
     *  A typical value would be 1000, as this would show timing
     *  information in seconds (given that the VM class granularity
     *  is set to milliseconds).
     */ 
     public int     gran;
     
    /**
     *  Constructor.  It throws any exceptions related to creating
     *  the temporary file.
     *
     *  It will start the timing.  See the VM base class to see how
     *  timing is implimented.
     *
     *  @param granulariy granularity for timing information.  This
     *                    should be a Positive number.  If it is
     *                    0, the constructor will throw an Exception
     *                    (since it would cause divide by 0 exceptions
     *                    in other methods of this class).
     *
     *  @throws Exception
     *  @see autohit.vm.VM
     */
     public Log(int   granularity) throws Exception {
         super();
         
         lock = new ObjectMonitor();  
         
         gran = granularity;
         if (gran == 0) { throw new Exception("Zero granularity."); }
         
         try {
              
            // Base the tempfile name on the system time;     
            Date d = new Date();
            tempFile = File.createTempFile
                              (Integer.toString((int)d.getTime()),".tmp");
            
            out = new BufferedWriter(new FileWriter(tempFile));
            
         } catch (Exception e) { throw e; }
         
         this.start();
     }
     
    /**
     *  Put an entry in the log.  It will append timing information.
     *
     *  @param text the text to log.
     */      
     public void put(String   text) {
          
          lock.lock(true);
          
          try {
               if (state == STATE_PAUSED) return;
               
               // Build the timing field
               String num = Integer.toString(this.ticks()/gran);
               int l = num.length();
               if (l < 8) {
                    out.write("[" + zeros[l] + num  + "] ", 0, 10);     
               } else {
                    out.write("[" + num + "] ", 0, l + 3);
               }
               
               out.write(text, 0, text.length());
               out.write("\n", 0, 1);
          
          } catch (Exception e) {}
          
          lock.lock(false);          
     }     

    /**
     *  Put a sub-entry in the log.  It will not append timing
     *  information.  It will be tabbed to match the time text.
     *
     *  @param text the text to log.
     */      
     public void putSub(String   text) {

          lock.lock(true);

          if (state == VM.STATE_PAUSED) return;

          try {
               out.write("          ", 0, 10);          
               out.write(text, 0, text.length());
               out.write("\n", 0, 1);
          
          } catch (Exception e) {}          
          
          lock.lock(false);                               
     }
     
    /**
     *  Done with the log.  It returns an InputStream from which the
     *  log can be read.  Once this method is called, no further
     *  entries will be logged when calling put() or putSub() (though
     *  no error will occur).
     *
     *  This method  (or its overloaded peer) can only be called once.  After that, a null
     *  will be returned, instead of an InputStream.
     *
     *  @return an InputStream presenting the log data.
     */      
     public InputStream done() {
          
          lock.lock(true);

          if (state != VM.STATE_DONE) {
               state = VM.STATE_DONE;

               try {              
                    out.close();
               
                    is = new FileInputStream(tempFile);
          
               } catch (Exception e) {}

          }
          
          lock.lock(false);
          return is;
     }

    /**
     *  Done with the log.  Returns a File pointing to a file where
     *  the log resides.
     *
     *  This method (or its overloaded peer) can only be called once.  After that, a null
     *  will be returned, instead of a File.
     *
     *  @param pathName pathname to the file where the log should be written.
     *
     *  @return a File describing the new log file.
     */      
     public File done(String    pathName) {

            File lf = null;
            
            lock.lock(true);          
            
            if (state != VM.STATE_DONE) {
               state = VM.STATE_DONE;

                // If for any reason something goes wrong, abort the whole affair.
                try {  
                    out.close();                        
                                              
                    File fout = new File(pathName);
                      
                    
                    FileInputStream  fis  = new FileInputStream(tempFile);
                    FileOutputStream fos  = new FileOutputStream(fout);
            
                    try {
                        
                        byte[] buf = new byte[512];
                        int a = fis.read(buf);
                        while (a > 0) {
                            
                            fos.write(buf, 0, a);
                            a = fis.read(buf);                        
                        }                       
                        
                    } catch (EOFException e) {
                        // arggrgr.  The API doc LIES!  sometimes this gets thrown...    
                    } catch (Exception e) {
                        throw e;    
                    }   
                    
                    fis.close();
                    fos.close();
                    lf = fout;
 
                } catch (Exception e) {                
                }
            
            }       
            
            lock.lock(false);              
            return lf;
     }
     
    /**
     *  Close the log and free/close any resources.
     */      
     public void close() {
          
          try {
               if (is != null) is.close();
          } catch (Exception e) {}
          
          try {
               
               tempFile.delete();      
          } catch (Exception e) {}
          
     }         

    /**
     *  Doesn't do a thing.  This isn't an actual VM.  However,
     *  we have to impliment it since Log extends the abstract VM.
     */
     public void execute() { }

}
