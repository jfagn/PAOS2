// Source code for stack manager:

// Our own exceptions
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;

import CharStackExceptions.*;


public class StackManager
{
          // The Stack
           private static CharStack stack = new CharStack();
           private static final int NUM_ACQREL = 4; // Number of Producer/Consumer threads
           private static final int NUM_PROBERS = 1; // Number of threads dumping stack
           private static int iThreadSteps = 3; // Number of steps they take
          // Semaphore declarations. Insert your code in the following:
          private static Semaphore semStack = new Semaphore(1);
          private static Semaphore semProd = new Semaphore(0);
          private static Semaphore semConsum = new Semaphore(0);
          
          // The main()
          public static void main(String[] argv) throws FileNotFoundException
          {
        	  
        	  
                    // Some initial stats...
                    try
                    {
                              System.out.println("Main thread starts executing.");
                              System.out.println("Initial value of top = " + stack.getTop() + ".");
                              System.out.println("Initial value of stack top = " + CharStack.pick() + ".");
                              System.out.println("Main thread will now fork several threads.");
                    }
                    catch(CharStackEmptyException e)
                    {
                              System.out.println("Caught exception: StackCharEmptyException");
                              System.out.println("Message : " + e.getMessage());
                              System.out.println("Stack Trace : ");
                              e.printStackTrace();
                     }
                    /*
                   * The birth of threads
                    */
                   Consumer ab1 = new Consumer();
                   Consumer ab2 = new Consumer();
                   System.out.println ("Two Consumer threads have been created.");
                  Producer rb1 = new Producer();
                  Producer rb2 = new Producer();
                  System.out.println ("Two Producer threads have been created.");
                  CharStackProber csp = new CharStackProber();
                  System.out.println ("One CharStackProber thread has been created.");
                  /*
                 * start executing
                  */
                 ab1.start();
                 rb1.start();
                 ab2.start();
                 rb2.start();
                 csp.start();
                 /*
                  * Wait by here for all forked threads to die
                 */
                try
                {
                           ab1.join();
                           ab2.join();
                           rb1.join();
                           rb2.join();
                          csp.join();
                          // Some final stats after all the child threads terminated...
                          System.out.println("System terminates normally.");
                          System.out.println("Final value of top = " + stack.getTop() + ".");
                          System.out.println("Final value of stack top = " + CharStack.pick() + ".");
                          System.out.println("Final value of stack top-1 = " + stack.getAt(stack.getTop() - 1) + ".");
                          System.out.println("Stack access count = " + stack.getAccessCounter());
                }
               catch(InterruptedException e)
               {
                      System.out.println("Caught InterruptedException: " + e.getMessage());
                           System.exit(1);
               }
              catch(Exception e)
              {
                           System.out.println("Caught exception: " + e.getClass().getName());
                           System.out.println("Message : " + e.getMessage());
                          System.out.println("Stack Trace : ");
                          e.printStackTrace();
               }
                
        } // main()
        /*
        * Inner Consumer thread class
        */
        static class Consumer extends BaseThread
        {
                 private char copy; // A copy of a block returned by pop()
                 public void run()
                 {
                              System.out.println ("Consumer thread [TID=" + this.iTID + "] starts executing.");
                              for (int i = 0; i < StackManager.iThreadSteps; i++)  {
                                       // Insert your code in the following:
                            	  semConsum.Wait();
                            	  semStack.Wait();
	                                  try {
	                                  	this.copy = CharStack.pop();
	                                  } catch (CharStackEmptyException e) {
	                                      System.out.println("Caught exception: StackCharEmptyException");
	                                      System.out.println("Message : " + e.getMessage());
	                                      System.out.println("Stack Trace : ");
	                                      e.printStackTrace();
	                                  } catch (Exception e) {
	                                      System.out.println("Caught exception: " + e.getClass().getName());
	                                      System.out.println("Message : " + e.getMessage());
	                                     System.out.println("Stack Trace : ");
	                                     e.printStackTrace();
	                                  }
                                      System.out.println("Consumer thread [TID=" + this.iTID + "] pops character =" + this.copy);
                                      semStack.Signal();
                                      semConsum.Signal();
                              }
                              System.out.println ("Consumer thread [TID=" + this.iTID + "] terminates.");
                     }
          } // class Consumer
           /*
          * Inner class Producer
           */
          static class Producer extends BaseThread
          {
                     private char block; // block to be returned
                     public void run()
                     {
                                System.out.println ("Producer thread [TID=" + this.iTID + "] starts executing.");
                                for (int i = 0; i < StackManager.iThreadSteps; i++)  {
                                         // Insert your code in the following:
                                		semStack.Wait();
                                        try {
                                        	this.block = (char) (CharStack.pick()+1);
                                        	CharStack.push(this.block);
                                        } catch (CharStackEmptyException e) {
                                            System.out.println("Caught exception: StackCharEmptyException");
                                            System.out.println("Message : " + e.getMessage());
                                            System.out.println("Stack Trace : ");
                                            e.printStackTrace();
                                        } catch (CharStackFullException e) {
                                        	System.out.println("Caught exception: CharStackFullException");
                                            System.out.println("Message : " + e.getMessage());
                                            System.out.println("Stack Trace : ");
                                            e.printStackTrace();
                                        } catch(Exception e)
                                        {
                                            System.out.println("Caught exception: " + e.getClass().getName());
                                            System.out.println("Message : " + e.getMessage());
                                           System.out.println("Stack Trace : ");
                                           e.printStackTrace();
                                        }
                                        
                                        System.out.println("Producer thread [TID=" + this.iTID + "] pushes character =" + this.block);
                                        semStack.Signal();
                                }
                               System.out.println("Producer thread [TID=" + this.iTID + "] terminates.");
                               
                               if (!semProd.available) {
                            	   semProd.Signal();
                               } else {
                            	   semConsum.Signal();
                               }
                     }
          } // class Producer
            /*
           * Inner class CharStackProber to dump stack contents
            */
           static class CharStackProber extends BaseThread
           {
                     public void run()
                     {
                    	 
                    	/* PrintWriter output = null;
						try {
							output = new PrintWriter(new FileWriter("Grep_3.txt"));
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}*/
                               System.out.println("CharStackProber thread [TID=" + this.iTID + "] starts executing.");
                               for (int i = 0; i < 2 * StackManager.iThreadSteps; i++)
                               {
                                   // Insert your code in the following. Note that the stack state must be
                                   // printed in the required format.
                                      
                            	   semStack.Wait();
                            	   try {
                            		   String stackContent;
                            		   stackContent = "Stack S = (";
                            		   for (int j = 0; j < stack.getSize();j++) {
                            			   stackContent = stackContent + "[" + stack.getAt(j) + "]";
	                            		   if (j != stack.getSize()-1) 
	                            			   stackContent = stackContent + ",";
	                            		   
	                            	   }
                            		   stackContent = stackContent + ")";
                            		   System.out.println(stackContent);
                            	   } catch (CharStackInvalidAceessException e) {
                                       System.out.println("Caught exception: CharStackInvalidAceessException");
                                       System.out.println("Message : " + e.getMessage());
                                       System.out.println("Stack Trace : ");
                                       e.printStackTrace();
                                   } catch(Exception e)
                                   {
                                       System.out.println("Caught exception: " + e.getClass().getName());
                                       System.out.println("Message : " + e.getMessage());
                                      System.out.println("Stack Trace : ");
                                      e.printStackTrace();
                                   }
                            	   semStack.Signal();
                               }
                               //output.close();
                     }
           } // class CharStackProber
} // class StackManager




