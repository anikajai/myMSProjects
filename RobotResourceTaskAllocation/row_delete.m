function [set,F] = row_delete(row_no,set_temp,FF)

for i = 1:row_no-1
    set(i,1) = set_temp(i,1);
    F(i,:) = FF(i,:);
end

for i = row_no+1:size(set_temp,1)
    set(i-1,1) = set_temp(i,1);
    F(i-1,:) = FF(i,:);
end