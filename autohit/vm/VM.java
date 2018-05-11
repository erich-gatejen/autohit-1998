/**
 * .
 * Copyright © 1999 Erich P G.
 *
 */
 
package autohit.vm;

import java.util.Date;
import java.util.Hashtable;
import java.util.Stack;

import autohit.vm.VMIScope;

/**
 * The abstract base class for virtual machines.
 *
 * A derived class will implement the abstract execute()
 * method to actually run the VM.  Also, it would normally use the
 * pause() and resume() methods to control timing (rather than
 * overloading and re-implementing them).
 * <p>
 * The derived class MAY overload the method prepare() if it has 
 * anything it wants to do before the FIRST instruction (and only the
 * first) is executed.  For instance, it could add environment variables.
 * <p>
 * The pause() and resume() methods are not designed to be
 * called by external threads.  If you plan to wrap the 
 * derived vm in a threaded class, you may want to overload or 
 * just not use those methods outside of the vm.  Also, these methods
 * only manage state and timing; it is up the he execute() method
 * in the derived class to actually stop execution.
 * <p>
 * This base class offers the following services:
 * - Instruction pointer (ip) field<br>
 * - Variable space allocation (vars).  The variables are put into a hashtable.
 *   some convenience methods are provided.<br>
 * - Scope Stack space and convenience methods.<br>
 * - Scope stack dirty flag.
 * <p>
 * USE <b>THESE</b> SERVICES!  Do not make your own ip, for instance!  Methods of
 * this class depend upon it.
 *
 * @author Erich P. Gatejen
 * @version 1.0
 * <i>Version History</i>
 * <code>EPG - Initial - 15Jan99</code> 
 * 
 */
public abstract class VM {
	
	// --- FINAL FIELDS ------------------------------------------------------	
     
    /**
     *  Granulatiry for each tick of the VM's clock.  It
     *  is used to scale the system time to the vm clock time.
     *
     *  Currently, it is set to 1.  Given the current Java
     *  implementations, this should yield a 1 millisecond
     *  tick.  That is, each VM clock tick will take one
     *  millisecond.
     */ 
     public static final int  TIME_GRAN = 1;

    /**
     *  State values for the VM.
     */ 
     public static final int   STATE_NEW     = 0;
     public static final int   STATE_RUNNING = 1;
     public static final int   STATE_PAUSED  = 2;     
     public static final int   STATE_DONE    = 3;
     public static final int   STATE_NO_VM   = 4;
     
	// --- FIELDS ------------------------------------------------------------

    /**
     *  VM start time.  System start time for this VM.
     *  This is a raw value that has not been scaled with the
     *  TIME_GRAN value.  We don't even want the derived class
     *  to get direct access to this, in case we have to
     *  compensate for a pause.
     */      	    
     private long       time;
    
    /**
     *  Used to compensate the time field after a resume().
     *  Since the system clock doesn't stop during a pause,
     *  we will have to change our perceived system time start
     *  to get the ticks for the VM.
     */      	    
     private long       pauseCompensation;

    /**
     *  VM state.
     */      	    
     protected int      state;
     
    /**
     *  Current instruction address/pointer.  A pointer into the insrtuction Vector.
     */      	    
     public int           ip;
     
    /**
     *  Variable space.  Use the convenience methods to access these...
     */      	    
     protected Hashtable     vars;
     
    /**
     *  Scope stack.
     *
     *  Do NOT use scope.pop() or scope.push() yourself!  We must maintain the
     *  scope cache dirty flag.  However, you can use peek(), empty(), and
     *  search() at your leasure().
     */      	    
     protected Stack       scope;
     
    /**
     *  Scope stack cache dirty flag.  Will be automatically set when any
     *  scope stack methods are used.
     */      	    
     protected boolean     scDirty;                   

	// --- PUBLIC METHODS ----------------------------------------------------	


    /**
     *  Constructor.  This should be called by any derived
     *  class constructors.
     *
     *  It does NOT set any time/clock data, so if you
     *  overload start(), be sure to do it in your start().
     *  (Oh, and don't do it in the constructor.)
     */      
     public VM() {
          
          state = STATE_NEW;
          ip    = 0;                // Always start at home.  :-)
          vars  = new Hashtable(); 
          scope = new Stack();
          
          scDirty = false;    
     }

    /**
     *  Start the VM.  It will set state and timing info, then
     *  call the abstract method execute() to execute the
     *  code.
     *  <p>
     *  Calling this method consecutively will effectively 
     *  reset the state and timing info.  It is probibly a 
     *  REAL BAD IDEA to call this from the execute method.
     *  <p>
     *  It throws any exceptions that are thrown out of execute().
     *
     *  @throws autohit.vm.VMException
     */  
     public void start() throws VMException {
          
          state = STATE_NEW;
          Date d = new Date();
          time = d.getTime();
          
          try {
             prepare();
          
          } catch (Exception e) {
             throw new VMException(VMException.PREPARE_EXCEPTION, e.getMessage());  
          }  
          
          execute();          
     }
     
    /**
     *  Get VM state.  Reports the state of the vm using the
     *  STATE_* values.
     *  <p>
     *  You may call this from another thread, but it isn't
     *  very reliable.
     *
     *  @return a STATE_* value
     */  
     public int getState() {
          return state;      
     }     
     
    /**
     *  Pause execution in the VM.  This should NOT be called
     *  by another thread.
     *  <p>
     *  It will only pause if the VM is running.
     */  
     public void pause() {
          
          if (state == STATE_RUNNING) {               
               state = STATE_PAUSED;
               Date d = new Date();
               pauseCompensation = -(d.getTime() - time);
          }
     }
     
    /**
     *  Resume execution in the VM.  This should NOT be called
     *  by another thread.
     *  <p>
     *  It will only resume if the VM is paused.
     */ 
     public void resume() {
          
          if (state == STATE_PAUSED) {               
               state = STATE_RUNNING;
               Date d = new Date();
               time = d.getTime() - pauseCompensation;
               pauseCompensation = d.getTime() - time;
          }          
     }     

    /**
     *  Number of ticks the VM has been running.  It will
     *  be scaled according to the TIME_GRAN field.
     *  <p>
     *  Note that it returns an int rather than a long like 
     *  system time usually is.  This means that the VM timing.
     *  This technically could cause some overflow problems, but
     *  I doubt a VM would ever run that long.
     *
     *  @return number of ticks the VM has run.
     */ 
     public int ticks() {

          Date d = new Date();
          
          long sticks = d.getTime() - time;
          
          return (int) (sticks / TIME_GRAN);  
          
          // If the compiler has half of a brain, this division
          // should be optimised out given the current
          // granularity.
     }

    
    /**
     *  Set a variable.  If the variable doesn't exist, it will create it.  Once
     *  created, a variable stays in scope for the rest of execution.
     *
     *  @param name the variable name.
     *  @param value the variable value given as a string.
     */ 
     public void setVar(String name, String value) {
        
        vars.put(name, value);        
     }
     
    /**
     *  Set a variable object.  If the variable doesn't exist, it will create it.  Once
     *  created, a variable stays in scope for the rest of execution.  The variable value is
     *  an object.
     *
     *  @param name the variable name.
     *  @param value the variable object.
     */ 
     public void setVar(String name, Object value) {
        
        vars.put(name, value);        
     }
     

    /**
     *  Remove a variable.  If the variable is not present, no error
     *  occurs.
     *
     *  @param name the variable name.
     */ 
     public void removeVar(String name) {
        
        vars.remove(name);        
     }     
     
    /**
     *  Get a string variable.  it will throw a VMException if the variable
     *  has not been set.
     *
     *  @param name the variable name.
     *  @return the value as a String
     *  @throws VMException
     *  @see VMException
     */ 
     public String getVar(String name) throws VMException {
        
        Object var = vars.get(name);
        if (var == null) { 
            throw new VMException( VMException.VARIABLE_NOT_DEFINED, "Variable " + name + " not defined.");
        }
        
        return (String)var;
    }
    

    /**
     *  Get a object variable.  it will throw a VMException if the variable
     *  has not been set.
     *
     *  @param name the variable name.
     *  @return the value as a String
     *  @throws VMException
     *  @see VMException
     */ 
     public Object getVarObject(String name) throws VMException {
        
        Object var = vars.get(name);
        if (var == null) { 
            throw new VMException( VMException.VARIABLE_NOT_DEFINED, "Variable " + name + " not defined.");
        }
        
        return var;
    }

    /**
     *  Get an Integer variable.  it will throw a VMException if the variable
     *  has not been set or is not a parse-able integer.
     *
     *  @param name the variable name.
     *  @return the value as an int
     *  @throws VMException
     *  @see VMException
     */ 
     public int getIntegerVar(String name) throws VMException {
        
        int value;
        String var = (String)vars.get(name);
        if (var == null) { 
            throw new VMException( VMException.VARIABLE_NOT_DEFINED, "Variable " + name + " not defined.");
        }
        
        try {
            value = Integer.parseInt(var);
        
        } catch (Exception e) { 
            throw new VMException( VMException.VARIABLE_TYPE_MISMATCH, "Variable " + name + " type mismatch.  Expecting integer, but it is a string.");
        }
        
        return value;
    }    

    /**
     *  Variable substitution.
     *  <p>
     *  There won't be any errors if a substitution isn't found.
     *  It will only do one level of substitution.  Either call this again
     *  to resolve a variable in the newly substituted text, or just
     *  assume it is plain text.  
     *  <p>
     *  It will throw a VMException if the variable isn't set.
     *  <p>
     *  Performing substitution on Object variables will yield "undefined" results.  
     *
     *  @param in text to find substitution.
     *  @return string with substitutions.
     *  @throws VMException
     *  @see VMException
     */ 

     public String subVar(String in) throws VMException {
       
        StringBuffer    temp = new StringBuffer();
        StringBuffer    var  = temp;
        String          varValue;
        int  rover = 0;
        int  sl    = in.length();
        boolean     replacing = false;
        char        cur;

        while (rover < sl) {
            
            cur = in.charAt(rover);
            if (cur == VMInstruction.IVToken) {
                
                if (replacing) {
                 
                  // the token is NOT allowed in a variable name...
                  // so go ahead and assume this is a close-out
                    varValue = (String)vars.get(var.toString());
                    if (varValue == null)  {
                        throw new VMException( VMException.VARIABLE_NOT_DEFINED, "Variable " + var.toString() + " not defined.");
                    }
                    temp.append(varValue);
                    replacing = false;
                    
                } else {
                    
                    // a double token is just escaping the token.
                    if (rover == (sl - 1)) {
                        // Abhorant case.  A lone token at the end of
                        // the string.
                        throw new VMException( VMException.VARIABLE_NOT_DEFINED, "Malformed string has a VARIABLE key character as the last character of the string.");    

                    } else {
                    
                        if (in.charAt(rover+1) == VMInstruction.IVToken) {
                            // Just escaped the token
                            temp.append(VMInstruction.IVToken);
                            rover++;
                        
                        } else {
                            // OK.  a NEW variable replace.
                            var = new StringBuffer();
                            replacing = true;
                        }

                    } // end if escaping
                     
                } // end if replacing.
                
            } else {
            
                if (replacing) { 
                    var.append(cur);
                    
                } else {
                    temp.append(cur);    
                }
                
            } //end if IVToken
            
            rover++;
        
        } // end while
        
        // Bad thing if a variable was not closed.
        if (replacing) {
            throw new VMException( VMException.VARIABLE_NOT_DEFINED, "Malformed string gives an unbounded variable name [" + in + "]");             
        }
        
        return temp.toString();
    }    


    /**
     *  Push an object onto the scope stack.
     *
     *  @param i the object
     */ 
     public void pushScope(Object  o) {
        
        scDirty = true;

        scope.push(o);
    }


    /**
     *  Pop an object off the stack.  USE THIS instead of scope.pop()!!!
     *  Have to dirty the cache flag...
     *
     *  It'll throw any exception it encounters--most likely a EmptyStackException.
     *  @return an object reference
     */ 
     public Object popScope() throws Exception{
        
        scDirty = true;

        return scope.pop();
    }

    /**
     *  Discard scope frame.  This will remove all items on the scope to and 
     *  including the top-most recent VMIScope object.
     *  <p>
     *  If it encounters any variable references, the variable will
     *  discarded.
     *  <p>
     *  It will pop the whole damned stack if it doesn't find one...
     *
     */ 
     public void discardScopeFrame() {
        
        scDirty = true;
        
        Object  item;

        try {
        
           while( !(scope.peek() instanceof VMIScope) ) {  // perhaps just look at the token instead?  faster?
                item = scope.pop();
                if (item instanceof String) {
                    // Remove it.  Don;'t care if it isnt actually there
                    vars.remove((String)item);    
                }
           }
           scope.pop();  // get the SCOPE too.           
           
        } catch (Exception e) {
             // looks like we emptied the whole stack.    
        }
    }

    /**
     *  Absract method for VM execution.  The derived class
     *  must implement the actual execution.  This method will
     *  be automatically called by start().  Therefore, you
     *  probibly should not call start() from within this 
     *  method.
     *  <p>
     *  The implimentation of this method should only execute
     *  ONE INSTRUCTION.  Successive calls would then execute
     *  the entire program.  If you do not impliment it this way,
     *  you are likely to ghost the vm's.
     *  <p>
     *  NOTE!  An implementing method MUST throw a 
     *  VMException(VMException.DONE) when it reaches the
     *  end of execution.
     *  <p>
     *  If the derived-class VM encounters an instruction that
     *  it does now support, it should throw a 
     *  VMException.INVALID_INSTRUCTION.
     *
     *  @see autohit.vm.VMException
     */     
     public abstract void execute() throws VMException;

    /**
     *  Prepare for execution of the first instruction.  The derived
     *  class may overload this if it has any stuff it wants to do
     *  before execute() is called the first time.
     *
     *  @throws Any exceptions it encounters.
     */     
     public void prepare() throws Exception {
        
        // The base class doesn't wanna do anything...   
     }

    
	// --- PRIVATE METHODS ---------------------------------------------------	

} 
