%% Implemented by Anika Jain(3), Tripti Gupta(44), Aditi Mittal(1), Sangeeta Goyal(27)


%No of robots :6
M = 6;
%No of tasks :3
N = 3;
%No of types of resources :5
K = 5;

% RR[i,j] denotes the number of units of resource type j with the ith robot
%(robots X resources)
RR = round((10).*rand(M,K));

%RT[i,j] denotes the number of units of resource type j required by the ith task
%(tasks X resources)
RT = round((10).*rand(N,K));

%TD(i,1) denotes the time deadline of the ith task
TD = 10+round((15).*rand(N,1));  % range 10 to 25

%D[i,j] denotes the distance of the ith task from the jth robot
%(tasks X robots)
D = 11 + floor((10).*rand(N,M)); % range 11 to 20

%S is Robot speed vector of size M
S = 1 + floor((10).*rand(M,1));  % range 1 to 10

%D is Robot maximum distance vector(battery life)
Dmax = 15 + round((10).*rand(M,1));  % range 15 to 25

RR
RT
TD
D
S
Dmax


chr_siz = N*M;
pop_siz = 4;
PF = zeros(pop_siz,3);
PT = zeros(pop_siz,chr_siz);
num_gen = 15;

 for n = 1:pop_siz
     PT_temp = zeros(N,M);
     chr=zeros(1,chr_siz);
     for i = 1:M
         s = round((N-1).*rand(1,1))+1;
         PT_temp(s,i) = 1;
     end
     [chr,pf] = calcu (M,N,K,RR,RT,D,Dmax,S,TD,chr_siz,PT_temp);
     PT(n,:) = chr;
     PF(n,:) = pf;
 end
 t = 0
 PF
 
 for t = 1:num_gen
    [temp,index] = sort(PF(:,1));
    for i = 1:pop_siz
        for j = 1:3
            PF_new(i,j) = PF(index(i,1),j);
        end
    end

    %% Selection operation

    QT = zeros(pop_siz,chr_siz);
    QF = zeros(pop_siz,3);

    % tournament
    for n = 1:pop_siz
        if (n == pop_siz)
            a = mod(n+1,pop_siz);
        else
            a = n+1;
        end
   
        if (PF(n,1) < PF(a,1))
           QT(n,:) = PT(a,:);
           QF(n,:) = PF(a,:);
        elseif(PF(n,1) > PF(a,1))
            QT(n,:) = PT(n,:);
            QF(n,:) = PF(n,:);
        else
            if(PF(n,2) < PF(a,2))
                QT(n,:) = PT(a,:);
                QF(n,:) = PF(a,:);
            elseif(PF(n,2) > PF(a,2))
                QT(n,:) = PT(n,:);
                QF(n,:) = PF(n,:);
            else
                if(PF(n,3) > PF(a,3))
                    QT(n,:) = PT(a,:);
                    QF(n,:) = PF(a,:);
                else
                    QT(n,:) = PT(n,:);
                    QF(n,:) = PF(n,:);
                end
            end
        end           
    end        

    %QT_mutated=zeros(pop_siz,chr_siz);
    tempfitness = zeros(pop_siz,3);

    QT_mutated=QT;

    for n = 1:pop_siz
        i = floor(chr_siz*rand)+1;  % i can take values from 1 to 50
        if(QT_mutated(n,i) == 0)
            QT_mutated(n,i) = 1;
        else
            QT_mutated(n,i) = 0;
        end
    end

    QT_new = zeros(N,M);

    for n = 1:pop_siz
        QT_temp = reshape(QT_mutated(n,:)',M,N);
        QT_new = QT_temp';
    
        [chr,pf]= calcu(M,N,K,RR,RT,D,Dmax,S,TD,chr_siz,QT_new);
        tempfitness(n,:) = pf;
        QT_mutated(n,:) = chr;
    end


    for n = 1:pop_siz
        if(tempfitness(n,1) > QF(n,1))
            QT(n,:) = QT_mutated(n,:);
            QF(n,:) = tempfitness(n,:);
        elseif(tempfitness(n,1) == QF(n,1))
                if(tempfitness(n,2) > QF(n,2))
                    QT(n,:) = QT_mutated(n,:);
                    QF(n,:) = tempfitness(n,:);
                elseif(tempfitness(n,2) == QF(n,2))
                    if(tempfitness(n,3) < QF(n,3))
                        QT(n,:) = QT_mutated(n,:);
                        QF(n,:) = tempfitness(n,:);
                    end
                end
        end
    end

    % K = P U Q
    KT = zeros(2*pop_siz,chr_siz);
    KF = zeros(2*pop_siz,3);

    for n = 1:pop_siz
        KT(n,:) = PT(n,:);
        KT(n+pop_siz,:) = QT(n,:);
    end
    for n = 1:pop_siz
        KF(n,:) = PF(n,:);
        KF(n+pop_siz,:) = QF(n,:);
    end

    tempo=KF;

    % front_array is having final fronts saved 
    front_array = zeros(pop_siz,1);
    arg = 0;
    flag = 1;
    while(arg < pop_siz)
        % step 1 for front calculation
        [wer,index] = sort(tempo(:,1),'descend');
        for n = 1:2*pop_siz
            for j=1:3
                tempo_1(n,j) = tempo(index(n,1),j);
            end
            set_temp_1(n,1) = index(n,1);
        end
        tempo = tempo_1;
        set_temp = set_temp_1;
        % step 2 going on
        [set] = front(2*pop_siz-arg,set_temp,tempo);
        for i = 1:size(set,1)
            if(i+arg > pop_siz) 
                count = pop_siz-arg;
                flag = 0;
                break;
            end
            front_array(i+arg,1) = set(i,1);
        end
        if(flag ~= 0)
            count = size(set,1);
        end
        arg=arg+count;
    
        for i = 1:count
            [set_new,F] = row_delete(set(i,1),set_temp,tempo);
        end
        set_temp = set_new;
        tempo = F;
    end
       
    for n = 1:pop_siz
        PT(n,:) = KT(front_array(n,1),:);
        PF(n,:) = KF(front_array(n,1),:);
    end
    t
    PF
    PT
    
 end
 
 RR
 RT
 for n = 1:pop_siz
    sprintf('Population No. %d',n)
    tool_1 = reshape(PT(n,:)',M,N);
    tool = tool_1';
    for i = 1:N
        a=0;
        for k = 1:M
            if(tool(i,k) == 1)
                a = a + 1;
                indx(a,1) = k;
            end
        end
        arr = sprintf('                    TASK%d',i);
        disp(arr);
        for j = 1:K
            arr = sprintf('    RESOURCE(%d)',j);
            disp(arr);
            arr = sprintf('requirement =%d',RT(i,j));
            disp(arr);
            for k = 1:a
                arr = sprintf('Robot(%d) gives %d', indx(k,1), RR(indx(k,1),j));
                disp(arr);
            end
        end
    end
end