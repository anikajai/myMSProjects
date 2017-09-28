function [m] = front(pop_siz,set,FF)


% step 2
if(pop_siz == 1)
    m = set;
else
    siz1 = floor(pop_siz/2);
    FF1 = zeros(siz1,3);
    for n = 1:siz1
        set1(n,1) = set(n,1);
        FF1(n,:) = FF(n,:);
    end
    [top] = front(siz1,set1,FF1);
    
    siz2 = pop_siz-siz1;
    FF2 = zeros(siz2,3);
    for n = 1:siz2
        set2(n,1) = set(n+siz1,1);
        FF2(n,:) = FF(n+siz1,:);
    end
    [bottom] = front(siz2,set2,FF2);

    for i = 1:size(bottom,1)
        flag = 1;
        for j = 1:size(top,1)
            if(FF2(i,1) < FF1(j,1) && FF2(i,2) < FF1(j,2) && FF2(i,3) > FF1(j,3))  % if bottom element i is dominated by any element of top j
               flag = 0;
               break;
            end
        end
        if(flag == 0)   
           continue;
        else
            set1(size(top,1)+1,1) = set2(i,1);
            FF1(size(top,1)+1,:) = FF2(i,:);
        end
    end
    
    m = set1;
end