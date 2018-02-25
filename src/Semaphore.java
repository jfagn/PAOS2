// Source code for semaphore class:  
public class Semaphore {
    private int value;
    public boolean available;
    
    public Semaphore(int value)
    {
    	if (value <= 0) {
    		this.value = 0;
    		this.available = false;
    	}else {
             this.value = value;
             this.available = true;
    	}
    }
    
   public Semaphore()
   {
            this(0);
    }
   
   public synchronized void Wait()
   {
	   this.value--;
             while (!this.available)
             {
                    try
                   {
                          wait();
                    }
                   catch(InterruptedException e)
                   {
                            System.out.println ("Semaphore::Wait() - caught InterruptedException: " + e.getMessage() );
                            e.printStackTrace();
                       }
               }
             if (this.value <= 0) {
            	 this.available = false;
             }
             
      }
   
      public synchronized void Signal()
      {
              ++this.value;
              this.available = true;
              notify();
      }
      public synchronized void P()
      {
              this.Wait();
      }
     public synchronized void V()
     {
              this.Signal();
     }
}
