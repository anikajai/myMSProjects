#include<iostream.h>
#include<conio.h>
#include<process.h>
#include<math.h>

class genalgo
{ int a[12][16], b[12[16],f[12[16];
   int c[12];
   double d[12],deci[12];
   int nw[12],q[6];
   int i,j,k,popu_siz;
   public:
          void getinput()
          { 
		cout<<"Enter the initial sample population";
	            	for(i=0;i<12;i++)
	               	{
				gets(a[i]);
                 		for( j=8;j<16;j++)
                    			a[i][j]=-100;
                 	}
            		for(j=0;j<12;j++)
                 		 c[j]=28;
             		for(k=0;k<12;k++)
                   		nw[k]=0;
           }
                
          void fitness()
          { 	
		for(i=0;i<12;i++)
	        {
			for(j=0;j<8;j++)
              		{ 
				for(k=j+1;k<8;k++)
                 		{ 
					if( (a[i][k]==(a[i][j]+(k-j))) || (a[i][k]==(a[i][j]+(j-k))) || (a[i][k]==a[i][j] )
			                       c[i]--;
                       		}
                       }
                }
         }
                       
  
        int size_calc(int z[])
        { 
		popu_siz=0;
               for(k=0;k<12;k++)
                   popu_siz+=z[k]; //size came for new popu
               return popu_siz;        
        }
                   
       void calc()
       { 
		int x=0,index;
                popu_siz=0;
                for(i=0;i<12;i++)
                   x+=c[i];
                for(j=0;j<12;j++)
                  { d[j]=(double)((c[j]/x)*12);
                     if(d[j]>((int)d[j] +0.5))
                        nw[j]=ceil(d[j]);
                     else 
                        nw[j]=floor(d[j]);
                        }  
                        
                for(j=0;j<12;j++)
                   deci[j]=d[j]-(int)d[j];
                   
                sorting(deci[]);
                x=size_calc(nw[]);
                if(x<12)
                   index=0;
                else if(x>12)
                   index=11;
                    
                p1: if(x<12)
                    { nw[index]++;
                       index++;
                       x=size_calc(nw[]);
                       goto p1;
                       }
                    else 
                 p2:   if(x>12)
                      { nw[index]--;
                         index--;
                         x=size_calc(nw[]);
                         goto p2;
                         }    
                         }
                         
           void new_pop()
           {
             l=0;
             for(i=0;i<12;i++)
              { 
                b3:if(nw[i]>0)
                    {  for(j=1;j<8;j++)
                           b[l][j]=a[i][j];
                       for(k=8;k<16;k++)
                          b[l][k]=0;
                    
                      l++;
                      nw[i]--;
                      goto b3;
                   }
                   else 
                      continue;
                }   
                
      cout<<"\n\t\t The new population is \n ";
      for(i=0; i<12; i++)
      {  for(j=0; j<8; j++)
	        cout<<"\t"<<b[i][j];
	     cout<<"\n";
      }
      }
      
      
      void crossover()
         { for(i=0;i<6;i++)
              {h1: q[i]=rand()%8;
                    if(q[i]==0)
                        goto h1;          
                      }
            for( i=0;i<6;i++)
               { for(j=q[i];j<8;j++)
                    { f[2*i][j]=b[2*i+1][j];
                    }
                 for(k=q[i];k<8;k++)
                    {f[2*i+1][k]=b[2*i][k];
                    }
               }
               
               cout<<"\n\t\t The cross over population is \n ";
               for(i=0; i<12; i++)
                  { for(j=0; j<8; j++)
		                cout<<"\t"<<f[i][j];
	                 cout<<"\n";
                   }
           }
         
      void mutate()
       { int p,x;
           for(i=0;i<12;i++)
           { p=rand()%8;
             p2: x=rand()%9;
                 if(x==0)
                    goto p2;
             f[i][p]=x;
             }
             cout<<"\n\t\t The mutated population is \n ";
               for(i=0; i<12; i++)
                  { for(j=0; j<8; j++)
		                cout<<"\t"<<f[i][j];
	                 cout<<"\n";
                   }
             }
      
      void fix()
        { for(i=0;i<12,i++)
            { for(j=0;j<8;j++)
                a[i][j]=f[i][j];
              }
         }
         
       void sorting(double arr[])
        { for(j=1;j<12;j++)
            { key=arr[j];
               i=j-1;
               while(key>0 && arr[i]>key)
                   { arr[i+1]=arr[i];
                     i--;
                     }
               arr[i+1]=key;
            }
          }
          
};

int main()
 { ob genalgo;
    ob.getinput();
    for(int m=0;m<20;m++)
    { ob.fitness();
      ob.calc();
      ob.new_pop();
      ob.crossover();
      if(m%10==0)
         ob.mutate();
      ob.fix();
      }
    getch();
    return 0;
}
                   
