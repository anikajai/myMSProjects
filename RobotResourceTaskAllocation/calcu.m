function [chr,pf] = calcu(M,N,K,RR,RT,D,Dmax,S,TD,chr_siz,temp_chr)

% robots not assigned to more than one task
for j=1:M
    flag=0;
    for i=1:N
        if(flag==1 && temp_chr(i,j)==1)
            temp_chr(i,j)=0;
        elseif(temp_chr(i,j)==1)
            flag=1;
        end
    end
end

%check constraint of battery life
 for i = 1:N
     for j = 1:M
         if(D(i,j) > Dmax(j,1))
             temp_chr(i,j) = 0;
         end
     end
 end

 %check constraint of time deadline of task
 for i = 1:N
     flag = 1;
     while(flag)
          mx = 0;
          k = 0;
          for j = 1:M
              if(temp_chr == 1)
                 tim = D(i,j)/S(j,1);
                 if(mx < tim)
                    mx = tim;
                    k = j;
                 end
              end
          end
          if(TD(i,1) < mx)
             temp_chr(i,k) = 0;
          else
             flag = 0;
          end
     end
 end    
    
 %check constraint of different resource requirement<=given resources by
 %robots
 
 for i = 1:N
     sm=zeros(K,1);
     for j = 1:M
         if(temp_chr(i,j) == 1)
            for k=1:K
                 sm(k,1) = sm(k,1) + RR(j,k);
            end
         end
     end
     for k=1:K
         if(RT(i,k) > sm(k,1))
            for j = 1:M
                temp_chr(i,j) = 0;
            end
            break;
         end
     end
end
    
%% Objective fitness compute

 pf = zeros(1,3);

 % objective function1 = no. of tasks completed(to be maximised) 
 f_task = 0;
 for i = 1:N
     for j = 1:M
         if(temp_chr(i,j) == 1)
            f_task = f_task+1;
            break;
         end
     end
 end
 pf(1,1) = f_task;

 % objective function2 = remaining battery life of robots(to be maximised) 
     
 f_battery = Dmax;
 for i = 1:N
     for j = 1:M
         if(temp_chr(i,j) == 1)
             f_battery(j,1) = Dmax(j,1)-D(i,j); 
         end
     end
 end

 sm = 0;
 for j = 1:M
     sm = sm + f_battery(j,1);
 end
 pf(1,2) = sm;
     
 % objective function3 = time taken to complete all tasks(to be minimised) 
     
 f_time = zeros(N,1);
 for i = 1:N
     mx = 0;
     for j = 1:M
         if(temp_chr(i,j) == 1)
            tim = D(i,j)/S(j,1);
            if(mx < tim)
                mx = tim;
            end
         end
     end
     f_time(i,1) = mx;
 end
    
 mx = 0;
 for i = 1:N
     if(mx < f_time(i,1))
         mx = f_time(i,1);
     end
 end

 pf(1,3) = mx;
 chr = reshape(temp_chr',1,chr_siz);
     
 