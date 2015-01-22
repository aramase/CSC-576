import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import static java.lang.Math.round;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

public class SipProject {  
   
      //Queues for P,S and AS.
       static Queue<Integer> queuePcscf=new LinkedList();
       static Queue<Integer> queueScscf=new LinkedList();
       static Queue<Integer> queueAs=new LinkedList();
       
        //timers
       static double MC;
       static double arr=0.05;
       static double pcscf=9999;
       static double scscf=9999;
       static double as=9999;
       static int counter=0;
       static int opcount=1;
       
       //parameters
      static double lambda;
      static double meanp;
      static double means;
      static double meanas;
      static double numdep;
      static double batchsize;
      
      //random generators
        static Random randomGenerator1 = new Random(10000);
        static Random randomGenerator2 = new Random(42000);
        static Random randomGenerator3 = new Random(14420);
        static Random randomGenerator4 = new Random(20021);
       
       static custProc cust[]=new custProc[30102];
       static double[] a=new double[6];
       static int i=0;
       static ArrayList<Double> arrlist = new ArrayList<Double>(30102);
       static ArrayList<Double> arrbatch = new ArrayList<Double>(30102);
       static double[] array1=new double[50];
       static double[] array2=new double[50];
       static double[] array3=new double[31];
       static double[] arraydelay=new double[30102];
       static double[] arraydelay1=new double[30102];
       static double[] arraybatch=new double[30102];
      
   public static void main(String args[]) throws IOException{
       
           BufferedReader buf = new BufferedReader(new FileReader("Input.txt"));
               @SuppressWarnings("UnusedAssignment")
               String line=null;
               while((line=buf.readLine())!=null)
               {
                   a[i]=Double.parseDouble(line);
                   i++;
               }   
       
       lambda=1/a[0];
       meanp=a[1];
       means=a[2];
       meanas=a[3];
       numdep=a[4];
       batchsize=a[5];
      
  while(opcount<=numdep)
  {
      double min1=min(arr,pcscf,scscf,as);
      //System.out.println(MC);
       if(min1==arr)
       {
           if(MC>min1)
               MC=MC;
           else
               MC=min1;
           event1();
           
       }
       else if(min1==pcscf)
       {
           if(MC>min1)
               MC=MC;
           else
               MC=min1;
           event2();
           }
       
       else if(min1==scscf)
       {
           if(MC>min1)
               MC=MC;
           else 
               MC=min1;
           event3();
       }
       
       else if(min1==as)
       {
         if(MC>min1)
             MC=MC;
         else
                 MC=min1;
         event4();
       }
   }
  
  //Computing mean and percentile
  for(i=1;i<=numdep;i++)
  {
   arraydelay[i]=(cust[i].stoptime-cust[i].starttime);
  }
  
  double mean=0;
  for(int j=1;j<=numdep;j++)
  {
      mean=mean+arraydelay[j];
      arrlist.add(arraydelay[j]);
      arraydelay1[j]=arraydelay[j];
  }
  
  //sorting of array for without batch means
  int q,r;
  double s;
  for(q=1;q<numdep;q++)
  {
      for(r=q+1;r<=numdep;r++)
      {
          if(arraydelay1[q]>arraydelay1[r])
          {
              s=arraydelay1[q];
              arraydelay1[q]=arraydelay1[r];
              arraydelay1[r]=s;
          }
      }
  }
  
  //computing percentile for each batch and storing in array3
  int a=100;
  int b;
  int c;
  int d;
  int u=0;
  int v=0;
  for(d=1;d<=batchsize;d++)
  {
      int i=1;
  for(b=a;b<a+(30000/batchsize);b++)
  {
   arraybatch[i]=arraydelay[b];
   i++;
  }
   //arrbatch.sort(null);
   for(q=1;q<(30000/batchsize);q++)
  {
      for(r=q+1;r<=(30000/batchsize);r++)
      {
          if(arraybatch[q]>arraybatch[r])
          {
              s=arraybatch[q];
              arraybatch[q]=arraybatch[r];
              arraybatch[r]=s;
          }
      }
  }
   
  
   array3[d]=arraybatch[(int) (0.95*(30000/batchsize))];
   //arrbatch.clear();
   a+=(30000/batchsize);
   //u+=a;
   v+=a;
  }
  
  //sum of percentiles of 30 batches
  int m;
  double msum=0;
  for(m=1;m<=batchsize;m++)
  {
     msum+=array3[m]; 
  }
 
  
  
  mean=mean/numdep;//mean of end to end delay 
  int var=(int) round(0.95*numdep);
  double percal=arraydelay1[var]; //95th percentile without batch means
  
     double tmean=(msum/batchsize);//Tmean
     double err=(((percal-tmean)/percal)*100);//error percentage
     
     int e;
     double newsum=0;
     for(e=1;e<=batchsize;e++)
     {
         array2[e]=Math.pow((tmean-array3[e]),2);
         newsum=newsum+array2[e]; 
     }
     double sd=Math.pow((newsum/(batchsize-1)),0.5);//standard deviation
     
     double rootn=Math.pow(batchsize,0.5);
     double low=tmean-(1.96*(sd/rootn));//lower limit
     double high=tmean+(1.96*(sd/rootn));//upper limit
     
     
     File peerList=new File("Output.txt");
           BufferedWriter output = new BufferedWriter(new FileWriter(peerList));
               String sentence="Simple mean of end-end delay: " + mean + "\r";
               output.write(sentence);
               output.write("\n");
               output.newLine();
               String sentence1="95th Percentile without batch means: " + percal + "\r";
               output.write(sentence1);
               output.write("\n");
               output.newLine();
               String sentence2="95th Percentile using batch means: " + tmean + "\r";
               output.write(sentence2);
               output.write("\n");
               output.newLine();
               String sentence3="Error Percentage: " + err + "\r";
               output.write(sentence3);
               output.write("\n");
               output.newLine();
               String sentence4="Standard deviation: " + sd + "\r";
               output.write(sentence4);
               output.write("\n");
               output.newLine();
               String sentence5="Lower limit: " + low + "\r";
               output.write(sentence5);
               output.write("\n");
               output.newLine();
               String sentence6="Upper limit: " + high + "\r";
               output.write(sentence6);
               output.write("\n");
               output.newLine();
               output.close();
   }
     
      //Minimum of simulation times
      public static double min(double a,double b, double c, double d)
      {
          double e=Math.min(Math.min(a, b),Math.min(c,d));
          return e;
      }
      
      //arrival event
      public static void event1()
      {
              counter++;
          if(counter<=numdep)
          {
          cust[counter]=new custProc(counter);
          cust[counter].starttime=arr;
          if(queuePcscf.isEmpty())
          {
            queuePcscf.add(counter);
            double randomDouble=randomGenerator2.nextDouble();
      double log = Math.log(randomDouble);
            pcscf=MC+(meanp*log*(-1));
          }
          else
          {
              queuePcscf.add(counter);
          }
          double randomDouble=randomGenerator1.nextDouble();
      double log = Math.log(randomDouble);
          arr=arr+(lambda*log*(-1));
}
    
      else 
      {
          arr=999999;
      } 
      }
      
      //Completion at PCSCF
      public static void event2()
      {
          int times;
          times= queuePcscf.poll();
          
          if(queuePcscf.isEmpty())
          {
              pcscf=999999;
          }
          else
          {
              double randomDouble=randomGenerator2.nextDouble();
      double log = Math.log(randomDouble);
              pcscf=MC+(means*log*(-1));
          }
          
          if(cust[times].id==0)
          {
             if(queueScscf.isEmpty())
             {
                 queueScscf.add(times);
                 double randomDouble=randomGenerator3.nextDouble();
      double log = Math.log(randomDouble);
                 scscf=MC+(means*log*(-1));
             }
             else
             {
                 queueScscf.add(times);
             }
          }
          
          else 
          {
               opcount++;
              
              if(queuePcscf.isEmpty())
              {
                  cust[times].stoptime=MC;
              }
              else
              {
                  double randomDouble=randomGenerator2.nextDouble();
      double log = Math.log(randomDouble);
                  pcscf=MC+(meanp*log*(-1));
                  cust[times].stoptime=pcscf;
              }
             
          }
      }
      
      //Completion at SCSCF
      public static void event3()
      {
          int time;
          time=queueScscf.poll();
          
          if(queueScscf.isEmpty())
          {
              scscf=999999;
          }
          else
          {
              double randomDouble=randomGenerator3.nextDouble();
      double log = Math.log(randomDouble);
              scscf=MC+(means*log*(-1));
          }
          
          if(cust[time].id==0)
          {
              if(queueAs.isEmpty())
              {
                  queueAs.add(time);
                  double randomDouble=randomGenerator4.nextDouble();
      double log = Math.log(randomDouble);
                  as=MC+(meanas*log*(-1));
              }
              else
              {
                  queueAs.add(time);
              }
          }
          
          else
          {
              if(queuePcscf.isEmpty())
              {
                  queuePcscf.add(time);
                  double randomDouble=randomGenerator2.nextDouble();
      double log = Math.log(randomDouble);
                  pcscf=MC+(meanp*log*(-1));
              }
              else
              {
                  queuePcscf.add(time);
              }
          }
         
      }

      //Completion at AS
      public static void event4()
      {
          int time1;
          time1=queueAs.poll();
          cust[time1].id=1;
          
          if(queueAs.isEmpty())
          {
              as=999999;
              cust[time1].stoptime=MC;
          }
          else
          {
              double randomDouble=randomGenerator4.nextDouble();
      double log = Math.log(randomDouble);
              as=MC+(meanas*log*(-1));
              cust[time1].stoptime=as;
          }
          
          if(queueScscf.isEmpty())
          {
              queueScscf.add(time1);
              double randomDouble=randomGenerator3.nextDouble();
      double log = Math.log(randomDouble);
              scscf=MC+(means*log*(-1));
          }
          else
          {
              queueScscf.add(time1);
          }
      }
}
