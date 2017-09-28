%% The main aim is to discriminate healthy people from those with parkinson's disease(PD), acc to status col which is set to 1 for healthy and 2 for PD

%% Loading of files
load labels.txt;
load parkinsons.txt;

%% Initialization 
label_input=labels;                                                         % a nx1 size 2D array                         
x=parkinsons; 
no_clustr=2;
epsilon=0.00001;
threshhold=0.8;
max_iter=20;

%% fcm call
[v,mu]=fcm(parkinsons,no_clustr);
[n,r]=size(x);
v
[maxu,label_u]=max(mu);
% mu




% n is total no. of data points
% r is total no. of dimensions
% no_clustr is total no. of clusters
% v represents cluster centres (no_clustrxr)
% mu is membership matrix of size no_clustrxn




%% Calculating the distance matrix
% d is no_clustrxn 2D array which stores the distance of data points from each clusters
d=zeros(no_clustr,n);                                                              

   
      for iter=1:max_iter
                     
          
            for k=1:n
                 for i=1:no_clustr
                        for dim=1:r
                                d(i,k)=power(x(k,dim)-v(i,dim),2)+d(i,k);                        
                        end
                        d(i,k)=sqrt(d(i,k));
                 end
            end

%% Assigning variables to upper and lower boundaries
            bup=zeros(no_clustr,n);
            blo=zeros(no_clustr,n);
            
            [d1,index]=sort(d,1);
            for k=1:n
                    
                    mini1=d1(1,k);
                    index1=index(1,k);
                    mini2=d1(2,k);
                    index2=index(2,k);
                  
                   
                   if( mini2-mini1 < threshhold)
                       bup(index1,k)=1;
                       bup(index2,k)=1;
                   else
                       blo(index1,k)=1;
                       bup(index1,k)=1;
                   end

            end
            
            
            wlo=0.4;
            wup=0.6;
            temp1=zeros(no_clustr,n);
   
            
            for i=1:no_clustr
                 for k=1:n
                      if(bup(i,k)==1)
                          temp1(i,k)=bup(i,k)-blo(i,k);
                      end
                 end
            end




%% v(i,dim) cases

% 2D arrays for temporary storage
                  
                  temp2=zeros(no_clustr,r);
                  temp3=zeros(no_clustr,r);
                  temp4=zeros(no_clustr,r);
                  temp5=zeros(no_clustr,r);
                  vold=v;
                  
                  
                  for i=1:no_clustr
                         
                     
                      if(sum(blo(i,:))~=0 && sum(temp1(i,:))~=0)
                            
                             for dim=1:r
                                   for k=1:n
                                        if(blo(i,k)==1)
                                            temp2(i,dim)=x(k,dim)+temp2(i,dim);
                                        end
                                        if(temp1(i,k)==1)
                                            temp3(i,dim)=x(k,dim)+temp3(i,dim);
                                        end
                                   end
                             end

                             for dim=1:r
                                   v(i,dim)=wlo*temp2(i,dim)+wup*temp3(i,dim);
                             end


                      elseif(sum(blo(i,:))==0 && sum(temp1(i,:))~=0)
                             
                             for dim=1:r
                                   for k=1:n
                                        if(temp1(i,k)==1)
                                             temp4(i,dim)=x(k,dim)+temp4(i,dim);
                                        end
                                   end
                             end

                             a=sum(temp1(i,:));
               
                            for dim=1:r
                                   v(i,dim)=temp4(i,dim)/a;
                            end

                      else

                            for dim=1:r
                                   for k=1:n
                                        if(blo(i,k)==1)
                                             temp5(i,dim)=x(k,dim)+temp5(i,dim);
                                        end
                                   end
                            end

                            b=sum(blo(i,:));
               
                            for dim=1:r
                                   v(i,dim)=temp5(i,dim)/b;
                            end
                      end
                  end


%% cluster centres are displayed     
  v                                             
  
 label_output=zeros(n,1);    
          
             if(v-vold<epsilon)
                   break;
             end
                        
          
      end
             
  iter 
  
  
%% Plotting Part 
  figure;
   title('Display of cluster centres');
  for p=1:4
  subplot(2,2,p),plot(v(:,p),'r+');
  end
  figure;
    for p=5:8
  subplot(2,2,p),plot(v(:,p),'r+');
    end
  figure;
  for p=9:12
  subplot(2,2,p),plot(v(:,p),'r+');
  end
  figure;
  for p=13:16
  subplot(2,2,p),plot(v(:,p),'r+');
  end
  figure;
  for p=17:20
  subplot(2,2,p),plot(v(:,p),'r+');
  end
  figure;
  for p=21:22
  subplot(2,2,p),plot(v(:,p),'r+');
  end

 
  
  % xlabel('no of data points');
  % ylabel('no. of dimensions');
  
  %% Computing label_output
  no=no_clustr-1;
  for k=1:n
     for i=1:no
         for j=i+1:no_clustr
            if(isequal(blo(i,k),blo(j,k)))                                  % i.e. both are equal to 0
                 if(isequal(bup(i,k),bup(j,k)))                             % i.e. both are equal to 1
                     [min_d,in_d]=min(d(:,k));                               
                     label_output(k,1)=in_d;
                 else                                                       % i.e. either bup(i,k) or bup(i+1,k) is equal to 1
                     [max_up,in_up]=max(bup(:,k));                           
                     label_output(k,1)=in_up;
                 end
            else                                                            % i.e. either blo(i,k) or blo(i+1,k) is equal to 1
              [max_lo,in_lo]=max(blo(:,k));                                  
              label_output(k,1)=in_lo;
            end
         end
     end
  end
  
%% Displaying label_output 
label_output                   

%% Calculating new_label_output
output_cluster=cell(no_clustr,1);
 for i=1:no_clustr
      output_cluster{i,:}=find(label_output==i);
      frequent(i)=mode(label_input(output_cluster{i,:}));
      new_label_output(output_cluster{i, :})=frequent(i);
 end
 
frequent

%% Displaying new_label_output
new_label_output

%% Displaying confusion matrix       
confusionmat(label_input, new_label_output)

%% Analysis Part
classperf(label_input,new_label_output)
  