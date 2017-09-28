#include<iostream>
#include<conio.h>
#include<string.h>
#include<vector>
#include<stdio.h>
#include<fstream>
#include<math.h>
#include<io.h>
#include<sstream>

using namespace std;

#define L_V_CHAR 30             // length of varchar is predefined

string query;                  //String to hold the query 
char *cstr;
char *tok;
vector<string> tokens;        //Vector to hold the tokenized query
vector<int> index;     //Vector to hold the index of each token
vector<int> attr_no; // Vector to store the attribute no.'s to be printed in the 'select' query
vector<int> line;     //Vector to store record no's on which first where condition satisfied
vector<int> line1;   //Vector to store record no's on which second where condition satisfied
vector<int> and_line; //Vector to store record no's with 'and' connector
vector<int> or_line;  //Vector to store record no's with 'or' connector
string rel_op[6]={"=","<",">","<=",">=","!="}; //Array of relational operators
vector<string> data_type; //Vector containing predefined datatypes
vector<string> constraint;   //Vector containing predefined constraints
string keywords[29] = {"CREATE", "TABLE", "TABLES", "SELECT", "FROM", "WHERE", "INSERT", "INTO",
                              "DELETE", "DROP", "UPDATE", "SET", "VALUES", "DESCRIBE", "DISPLAY", "ALL", 
                              "EXIT", "NULL", "INT", "CHAR", "VARCHAR", "FLOAT", "BOOLEAN", "DATE", "PRIMARY_KEY" , 
                               "FOREIGN_KEY_REFERENCES", "UNIQUE" , "NOT_NULL" };


    void enter()
    {
       cout<<"\n\n ENTER THE QUERY TERMINATING WITH A SEMICOLON\n\n\n";
       cout<<"OUR-DBMS > ";
       getline(cin,query,';');
       cout<<endl;
       
       //CONVERTING THE QUERY INTO UPPERCASE     
       for(int j=0; j<query.size(); j++)
             query[j]=toupper(query[j]);
         
       //TOKENIZING THE QUERY
       
       string buf; // Have a buffer string
       stringstream ss(query); // Insert the string into a stream
        
       while (ss >> buf)
                tokens.push_back(buf);
        
        
       vector<string>::iterator k;
       int a=0;
            
       for(k=tokens.begin();k!=tokens.end();k++)
       {
            //STORING THE INDEX OF TOKENS
            index.push_back(a);
            a++;   
       }  
       
       getch();
   }


/*  Input: tablename
    Function: checks whether tablename is a keyword or not
    Output: returns 'y' if tablename is a keyword else returns 'n'    */
          
    char keyword_check(string tablename)
    {
        for(int i=0;i<29;i++)
        {
             if(keywords[i]==tablename)
             {
                cout<<"\n\n TABLE NAME CANNOT BE A KEYWORD\n";
                getch();
                return 'y';
             }
        }
        return 'n';
    }
        
        
/* Input: tablename
   Function: checks that the table name exists in Relation_name.txt file 
   Output: returns 'g' if tablename is 'Relations_name'
           else returns 'y' if a table already exists in relations_name file and 'n' otherwise  */
        
   char table_name(string str1)
   {
       char str[80];  // To hold the table name read from the relations file
       char ch='n'; 
             
       //Cannot create a table with name "Relations_name"    
       if(str1=="RELATIONS_NAME")
       {
           cout<<"\n\nA TABLE WITH NAME 'Relations_name' CANNOT BE CREATED\n\n";
           getch();
           exit(0);
       } 
            
       ifstream fin("Relations_name.txt",ios::in);
       
       if(!fin)
       {
           cout<<"\n\n Relations_name FILE DOESNOT EXIST IN MEMORY, CREATE IT FIRST\n\n";
           getchar();
           exit(1);
       }
            
       while(!fin.eof())
       {
           fin.getline(str,80);
           if(str==str1)
           { 
               ch='y';
               break;
           }
       }
       fin.close();
       return ch; 
   }
        
   
        
 /* Class to hold the attribute name, its datatype, size, constraint and field  
    as entered in the create query */
            
   class create
   {
        public:
          string attribute;
          string data_type1;
          string constraint1;
          string constraint2;  // to hold the values for 'foreign key' constraint   
              
          void initialize()
          {
              attribute="NULL";
              data_type1="NULL";
              constraint1="NULL";
              constraint2="NULL";
          }
   };
        
   vector<create> store; /* Vector to temporary store the name,type,size,constraint1, 
                              constraint2 and  additional field(if specified) of each attribute   */
   create obj,obj2;  //Object of class create
        

/* Input: s1 specifies the data type of the foreign key,
          s specifies the table and the attribute referenced in the format table.attribute 
   Function: checks the existence of table and attribute and checks that the attribute has 
             a primary key constraint       
   Output: returns 'y' if foreign key constraint is properly specified 
              else returns 'n'    */
                     
   char parse_foreign_key(string s,string s1)  // 'n' returned in error case and 'y' returned is ok case
   {
       char ch='y';
       size_t found=s.find('.');                  // to find index of '.'
          
       if (found==string::npos)                   //npos is a member function of string class returned when '.' not found
       {
            ch='n';
            cout<<"\n\nINCORRECT FORMAT FOR FOREIGN KEY, NOT SPECFIED AS table_name.attribute\n\n";
            getch();
            return ch;
       }
          
       string table = s.substr(0,found);
       string attribute = s.substr(found+1,s.size()-(found+1)); 
           
               
       //to check whether the table referenced exists or not
       ch=table_name(table);
       if(ch!='y')
       {
           cout<<"\n\nTABLE REFERENCED BY FOREIGN KEY ATTRIBUTE DOES NOT EXIST\n\n";
           getch();
           return ch;
       }
           
          //to check whether attribute referenced exists in the mentioned table and it is a primery key or not
       int chk = -1;
       
       string s2 = table;
       s2 += "_schema.txt";
       const char* p1=s2.c_str();
       
       ifstream f(p1);                    //opening schema file in input mode;
       
       while(!f.eof())
       {
           obj2.initialize();
           f>>obj2.attribute>>obj2.data_type1>>obj2.constraint1>>obj2.constraint2; 
           if(obj2.attribute==attribute)
           {
               chk=1;                           
               break;
           } 
       }
       f.close();
       if(chk==-1)
       {
            cout<<"\n\n ATTRIBUTE REFERENCED BY THE FOREIGN KEY NOT PRESENT IN THE REFERENCED TABLE\n\n";
            ch='n';
            getch();
            return ch;
       } 
       if(chk==1)
       {
           if(obj2.constraint1!="PRIMARY_KEY")
           {
               cout<<"\n\n ATTRIBUTE REFERENCED BY FOREIGN KEY ATTRIBUTE IS NOT A PRIMARY KEY\n\n";
               ch='n';
               getch();
               return ch;
           }
           
           //to check that data type of both the referencing and the referenced attribute matches
           if(obj2.data_type1!=s1)
           {
                 cout<<"\n\n DATA TYPE MISMATCH BETWEEN THE REFERENCING AND THE REFERENCED ATTRIBUTE\n\n";
                 ch='n';
                 getch();
                 return ch;
           } 
       }    
       return ch;            
   } 
 
 /* Input: None
    Function: Pushes the attribute name , predefined datatypes, size , 
              predefined constraints and their respective fields into respective vectors
              and then checks various conditions to parse the create command.
    Output: whether the create query is correct or not     */
                
    int parse_create()
    {
        // storing predefined datatypes
        data_type.push_back("VARCHAR");
        data_type.push_back("DATE");
        data_type.push_back("CHAR");
        data_type.push_back("FLOAT");
        data_type.push_back("INT");
        data_type.push_back("BOOLEAN");
        data_type.push_back("STRING");
        
        // storing predefined constraints
        constraint.push_back("PRIMARY_KEY");
        constraint.push_back("UNIQUE");
        constraint.push_back("NOT_NULL");
        constraint.push_back("FOREIGN_KEY_REFERENCES");
             
        vector<string>::iterator it_datatype,it_constraint;  //Iterators of string vector
        
        int start=3,end,flag=1;
        
        int k=start+1;
        // start variable initialized to the index of "(" in create command
        char ch;
           
        while( k<tokens.size())
        {
            obj.initialize();
               
            for(int i=start+1;i<tokens.size();i++)
            {
                if(tokens[i]==",")     // one attribute completed
                {
                    end=i;
                    break;
                }
                else if(tokens[i]==")" && i==(tokens.size()-1))//When matched with closing parenthesis
                {
                    end=i;
                    break;
                }
            }
            
            // If only attribute name and datatype are entered
            
            if(isalpha(*tokens[k].begin()))  //Attribute name should begin with a character
            {
                it_datatype=find(data_type.begin(),data_type.end(),tokens[k]); //Attribute name should not be a datatype
                it_constraint=find(constraint.begin(),constraint.end(),tokens[k]); //Attribute name should be a constraint
                         
                if(it_datatype==data_type.end() && it_constraint==constraint.end())  // i.e. when attribute is verified
                {
                    obj.attribute=tokens[k];
                }
                else
                {
                    cout<<"\n\n ATTRIBUTE NAME SHOULD NOT BE A PREDEFINED DATATYPE OR CONSTRAINT\n\n";
                    getch();
                    flag=-1;
                    return flag;
                } 
            }
            else
            {
                cout<<"Attribute name should begin with a character"<<endl;
                getch();
                flag=-1;
                return flag;
            }
             
            it_datatype=find(data_type.begin(),data_type.end(),tokens[k+1]);   //tokens[k+1] should be a predefined datatype
             
            if(it_datatype!=data_type.end())  // i.e. when a valid datatype 
            {
                obj.data_type1=tokens[k+1];
            }
            else
            {
                cout<<"\n\nNOT A PREDEFINED DATA TYPE\n\n\n";
                flag=-1;
                return flag;
            }
                         
            if(end-start==3) //attribute and type mentioned
            {
                k=k+3;
            }            
            else if(end-start==4)  //attribute,type and constraint1 mentioned
            {    
                it_constraint=find(constraint.begin(),constraint.end(),tokens[k+2]); //constraint shall be a valid constraint
                if(it_constraint!=constraint.end())
                {
                    if(tokens[k+2]=="PRIMARY_KEY")
                    {
                        for(int j=0;j<store.size();j++)   //check if the relation already have a primary key
                        {
                            //if(j==k+2)  
                              //  continue;
                            if(tokens[k+2]==store[j].constraint1)
                            {
                                cout<<"\n\n PRIMARY KEY CONSTRAINT CAN BE GIVEN WITH ONLY ONE ATTRIBUTE\n\n";  
                                flag=-1;
                                return flag;
                            }
                        }
                    }
                    obj.constraint1=tokens[k+2];
                }
                else
                {   cout<<"\n\nNOT A VALID CONSTRAINT\n\n";
                    flag=-1;
                    return flag;
                }
                k=k+4; 
            } 
            else if(end-start==5)  //attribute,type,constraint1 and cnstraint2 mentioned
            {  
               it_constraint=find(constraint.begin(),constraint.end(),tokens[k+2]);  //constraint shall be a valid constraint
                     
               if(it_constraint==constraint.end())
               {
                   cout<<"\n\n NOT A PREDEFINED CONSTRAINT \n\n";
                   flag=-1;
                   return flag;
               }
                     
               if(it_constraint!=constraint.end())
               {  
                   
                   if(tokens[k+2]=="FOREIGN_KEY_REFERENCES")
                   {
                       obj.constraint1=tokens[k+2];                                     
                       
                       ch=parse_foreign_key(tokens[k+3],tokens[k+1]);
                       
                       if(ch=='y')
                       {
                           obj.constraint2=tokens[k+3];
                       }
                       else
                       {
                           flag=-1;
                           return flag;
                       }
                   }
                   else if(tokens[k+2]=="UNIQUE")
                   { 
                       if(tokens[k+3]=="NOT_NULL")
                       {  
                          obj.constraint1=tokens[k+2];
                          obj.constraint2=tokens[k+3];
                       }
                       else
                       {  
                          flag=-1;
                          return flag;
                       }
                   }
                   else if (tokens[k+2]=="NOT_NULL")
                   { 
                      if(tokens[k+3]=="UNIQUE")
                      {
                        obj.constraint1=tokens[k+2];
                        obj.constraint2=tokens[k+3];
                      }
                      else
                      {  
                         flag=-1;
                         return flag;
                      }
                   }
                   else
                   { 
                      flag=-1;
                      return flag;
                   }
                   k=k+5;
               }
            } 
            else
            {  
               cout<<"INVALID SYNTAX"<<endl;
               getch();
               flag=-1;
               return flag;
            }  
            store.push_back(obj);//Push the attribute details in the store vector
            start=end; 
        }
        return flag;
    }
      
      
    
/* Input: None
   Function: implements the create command creating table and schema file.
   Output: A tablename.txt file containing attribute names is created
           and a tablename_schema.txt containing the structure of table is created */
                   
   void implement_create()
   {          
       ofstream f1;
                  
       f1.open("Relations_name.txt",ios::app); 
       f1<<tokens[2]<<"\n";
       f1.close();
                
       string s=tokens[2];
       s += ".txt";
       const char* p = s.c_str();
       ofstream SaveFile(p,ios::out);
                 
       for(int j=0;j<store.size();j++)
       {
           SaveFile<<store[j].attribute<<"\t";
       }
       SaveFile<<"\n";   
       SaveFile.close();
                
       string s1=tokens[2];
       s1+="_schema.txt";   
       const char* p1 = s1.c_str();
            
       ofstream File(p1,ios::out);
       
       for(int j=0;j<store.size();j++)
       {
           if(j==store.size()-1)
           {        
              File<<store[j].attribute<<"\t"<<store[j].data_type1<<"\t"<<store[j].constraint1<<"\t"<<store[j].constraint2;
           }
           else 
           {      
               File<<store[j].attribute<<"\t"<<store[j].data_type1<<"\t"<<store[j].constraint1<<"\t"<<store[j].constraint2<<endl;
           }
       }
       File.close();
   }    
 
             
 /*  Input: TABLENAME  
     Function: counts the number of lines in the table including the line 
               having attribute names
     Output: returns the count of number of the records in the table s  */
           
     int count_lines_file(string s)
     {
        int size=0;
        ifstream inp;
        inp.open(s.c_str());
        char line[80];
        while ( !inp.eof() )
        {
           inp.getline(line,80);
           size++;
        } 
        inp.close();
        return size;
     }
            
        
  /* Input: date specified
     Function: checks the validity of date
     Output: returns 1 if date is valid else returns -1 */
          
     int valid_date(string s)
     {
         int day,month,year;
         int days_of_months[12]={31,28,31,30,31,30,31,31,30,31,30,31};
         bool isLeap = false;
    
         if(s.size()==10)              // becoz of format "DD-MM-YYYY"
         {
             if(s.substr(2,1)=="-" && s.substr(5,1)=="-")
             {   
                 const char *y = (s.substr(6,4)).c_str();   //year
                 year = atoi(y);                                             
                 if(year == 0 || year < 1900)
                 {
                     cout<<"INVALID YEAR"<<endl;
                     getch();
                     return -1;
                 } 
                 else if(year % 4 == 0)
                 {
                    isLeap = true;
                 }
                         
                 const char *m = (s.substr(3,2)).c_str();              //month
                 month = atoi(m);
                 
                 if(month < 1 || month > 12)
                 {
                    cout<<"INVALID MONTH"<<endl;
                    getch();
                    return -1;
                 }
                        
                 const char *d = (s.substr(0,2)).c_str();
                 day = atoi(d);  
                        
                 if(day<1 || day>31) 
                 {
                     cout<<"\n\n INCORRECT DAY...";
                     getch();
                     return -1;
                 }
                 else if(month==2 && isLeap==true && day > (days_of_months[month-1])+1)   //february spcl case
                 { 
                     cout<<"\n\n INCORRECT DAY...";
                     getch();
                     return -1;
                 }
                 else if(day > (days_of_months[month-1]))
                 {
                     cout<<"\n\n INCORRECT DAY...";
                     getch();
                     return -1;
                 }
             }
             else
             {
                 cout<<"INVALID DATE FORMAT"<<endl;
                 getch();
                 return -1;
             }
         }
         else
         {
             cout<<"INVALID DATE FORMAT"<<endl;
             getch();
             return -1;
         }
         return 1;
    }      
  
 /*  Input: None
     Function: Parses the insert command
     Output: Returns a flag (1 or -1)   */
           
   int parse_insert()
   {
         int start,end,flag=1;
         for(int i=0;i<tokens.size();i++)
         {
             if(tokens[i]=="(")
             {    
                start=i;
             }
             if(tokens[i]==")")
             {    
                end=i;
             }
         }
             
         if(end-start==1)
         {
            cout<<"\n\n ERROR, NO VALUES ARE SPECIFIED FOR INSERTION \n\n";
            getch();
            return -1;
         }
         
         if(end==tokens.size()-1 && start==4)
         {
             for(int i=5;i<tokens.size();i=i+2)
             { 
                 char ch=*tokens[i].begin();
                 char ch1=*(tokens[i].end()-1);
                 
                 if((isdigit(ch)&& isdigit(ch1)) || (ch=='"' && ch1=='"')|| (ch=='\'' && ch1=='\'') || ch=='T' || ch=='F' || ch=='N')
                 {       
                     if(tokens[i+1]=="," || tokens[i+1]==")")
                         continue;
                     else
                     {
                         flag=-1;
                         cout<<"\n\nDELIMITER BETWEEN VALUES NOT PRESENT\n\n\n";
                         getch();
                         break;
                     }  
                 }          
                 else
                 {
                     flag=-1;
                     break;
                 }
             }    // for loop ends 
         }       // if ends
         else 
         {
            flag=-1;
            cout<<"\n\nWRONG PARENTHESIZATION\n\n";
            getch();
            return flag;
         }           
             
         if(flag==-1)
         {  
              cout<<"\n\n SYNTACTICALLY INCORRECT QUERY \n\n";
         }
         
         return flag;
   }  
  
  
/*  The data from relations schema file is retrieved in an object of class insert */   
         
   class insert
   {
       public:
           string attribute;
           string data_type1;
           string constraint1;
           string constraint2; // TO HOLD THE CHECK CONSTRAINT OR TABLENAME REFRENCED BY FOREIGN KEY 
           
           void initialize()
           {
              attribute="NULL";
              data_type1="NULL";
              constraint1="NULL";
              constraint2="NULL";
           }
   }obj1,ob,obj_temp;
      
   vector<insert> retrieve;
   vector<string> values;// to store the values of the primary key referenced
      
      
/* Input: 
        s1 specifies the data type of the foreign key, s3 is the value to be inserted
        s specifies the table and the attribute referenced in the format table.attribute 
   Function: checks that the foreign key attains a value only from the domain of 
             the primary key being refered
   Output: returns 'y' if constraint is satisfied else returns 'n'   */
          
   char implement_foreign_key(string s, string s3)     //s is the constraint2 of foreign key attr (table.attr)
   {                                                   //and s3 has the value that is inserted
       values.clear();
       char ch = 'y';
       size_t found = s.find('.'); 
  
       string table = s.substr(0,found);
       string attribute = s.substr(found+1,s.size()-(found+1));      
       int chk = -1;
       int att_no = 0;    //to keep track of the attribute no.
          
       string s_schema = table + "_schema.txt";  // remember schema file does not have filename
       const char* p1 = s_schema.c_str();
          
       int size_schema=count_lines_file(s_schema);  //size_schema will have no. of attributes of this file
       ifstream inp(p1,ios::in);
          
       if(!inp)
       {   
           cout<<"CANNOT OPEN "<<s_schema<<" FILE\n\n";
           ch='n';
           getch();
           return ch;
       }
          
       while(!inp.eof())
       {
           obj2.initialize();
           
           inp >> obj2.attribute >> obj2.data_type1 >> obj2.constraint1 >> obj2.constraint2; 
           
           att_no++;
           
           if(obj2.attribute==attribute)    // catching the referred attribute
             {
                chk=1;                           
                break;
             } 
       }
          
       inp.close();
         
       string s_data=table+".txt";
       int size_data=count_lines_file(s_data);  // size_data will have the no. of tuples in referred file
      
       if(size_data<2)
       {
            cout<<"\n\n NO VALUES IN THE REFERENCED TABLE SO CANNOT INSERT VALUE WITH FOREIGN_KEY_CONSTRAINT \n\n";
            ch='n';
            return ch;  
       }
          
       int total = size_data * size_schema;   // total has the total no. of words
       int c = 0; // to maintain the word count
       string val; //to read all the values of the referenced attribute
          
       ifstream fin(s_data.c_str());
          
       for( int h=0;h<size_schema;h++)
       {   
           fin>>val;
       }
       
       while(!fin.eof())
       {
           if(c==total)
              break;
           
           fin>>val;
             
           if(c%size_schema==att_no-1)
               values.push_back(val);          // "values" gets the value of
                                                  // the referenced attribute for all tuples of the referenced file 
           c++;
       }
          
       fin.close();
       int chk1=-1;
          
       for(int i=0;i<values.size();i++)
       {
           if(s3==values[i])
           {
              chk1=1;
              break;
           }      
       }   
           
       if(chk1==-1)
       {
           cout<<"\n\n VALUE NOT AMONG THE SET OF VALUES OF THE PRIMARY KEY REFERENCED, VALUE CANNOT BE INSRTED";
           getch();
           ch='n';
           return ch;
       } 
       return ch;
   }          
      
        
        
        
/*  Input: None
    Function: Inserts a tuple into the table if it satisfies the datatype and constraint 
              specified in the schema file     
    Output: returns 1 if insertion is successful
               else returns -1      */
                             
    int implement_insert()
    {  
        char ch1,ch2;
        int flag = 1,chk = -1;
        
        string s1=tokens[2];
        s1 += "_schema.txt";
            
        ifstream inp(s1.c_str());
        while(!inp.eof())
        {  
            obj1.initialize();
            inp >> obj1.attribute >> obj1.data_type1 >> obj1.constraint1 >> obj1.constraint2;
            retrieve.push_back(obj1);
        }
        inp.close();      
            
        if(retrieve.size()!= (tokens.size()-5)/2 )
        {   
             cout<<"ATTRIBUTE VALUES ARE LESS OR MORE THAN THE REQUIRED NUMBER\n\n";
             getch();
             flag=-1;
             return flag;
        }   
        
        string s=tokens[2]+".txt";
            
        for(int i=5,j=0;j<retrieve.size();i=i+2,j++)
        {  
             ch1='n';
             ch2='n';
             if(retrieve[j].constraint1 != "NULL")
             {  
                 ch1 = *retrieve[j].constraint1.begin();
             }
             if(retrieve[j].constraint2 !="NULL" && ch1!='F')
             {
                 ch2 =  *retrieve[j].constraint2.begin();
             }
             char ch = *retrieve[j].data_type1.begin();
               
             switch(ch)
             {
                  case 'S' : if ( *tokens[i].begin()=='"' && *(tokens[i].end()-1)=='"' )
                             { 
                                 if( tokens[i].substr(1,4)=="NULL" && tokens[i].size()==6  && (ch1=='N' || ch2=='N' || ch1=='P') )     
                                 { 
                                     flag=-1;
                                     cout<<"\n\n NULL VALUE NOT ALLOWED WITH NOT_NULL CONSTRAINT\n\n";
                                     getch();
                                     return flag;
                                 }
                                 else 
                                 {  
                                      break;
                                 }
                             }
                             else
                             {  
                                cout<<"SEMANTICAL ERROR"<<endl;
                                getch();
                                flag=-1;
                                return flag;
                             }
                                
                  case 'C' : if ( *tokens[i].begin() == '\'' && *(tokens[i].end()-1)=='\'' )
                             { 
                                  if(tokens[i].substr(1,4)=="NULL" && tokens[i].size()==6 && (ch1=='N' || ch2=='N' || ch1=='P' ) )     
                                  { 
                                     flag=-1;
                                     cout<<"\n\n NULL VALUE NOT ALLOWED WITH NOT_NULL CONSTRAINT\n\n";
                                     getch();
                                     return flag;
                                  }
                                  else if( ch1!='N' && ch2!='N' && ch1!='P' && tokens[i].substr(1,4)=="NULL" && tokens[i].size()==6)
                                  {
                                       break;
                                  } 
                                  else if(tokens[i].size()==3)
                                  {
                                       break;
                                  }
                                  else
                                  {  
                                      cout<<"SEMANTICAL ERROR"<<endl;
                                      getch();
                                      flag=-1;
                                      return flag;
                                  }  
                             }
                             else
                             {  
                                cout<<"SEMANTICAL ERROR"<<endl;
                                getch();
                                flag=-1;
                                return flag;
                             }     
                          
                  case 'B':  if((ch1=='N' || ch2=='N' || ch1=='P') && tokens[i].substr(0,4)=="NULL" && tokens[i].size()==4)     
                             { 
                                flag=-1;
                                cout<<"\n\n NULL VALUE NOT ALLOWED WITH NOT_NULL CONSTRAINT\n\n";
                                getch();
                                return flag;
                             }
                             else if(ch1!='N' && ch2!='N' && ch1!='P' && tokens[i].substr(0,4)=="NULL" && tokens[i].size()==4)     
                             {
                                 break;
                             }
                             else if(tokens[i].size()==1 && (*tokens[i].begin()=='T' || *tokens[i].begin()=='F'))
                             {       
                                 break;
                             }
                             else
                             {
                                 cout<<"\n\nSEMANTICAL ERROR\n\n";
                                 getch();
                                 flag=-1;
                                 return flag;
                             }
                            
                  case 'I':  if((ch1=='N' || ch2=='N' || ch1=='P') && tokens[i].substr(0,4)=="NULL" && tokens[i].size()==4)     
                             { 
                                flag=-1;
                                cout<<"\n\n NULL VALUE NOT ALLOWED WITH NOT_NULL CONSTRAINT\n\n";
                                getch();
                                return flag;
                             }
                             else if(ch1!='N' && ch2!='N' && ch1!='P' && tokens[i].substr(0,4)=="NULL" && tokens[i].size()==4)
                             {    
                                 break;
                             }
                             else
                             {   
                                 for(int h=0;h<tokens[i].size();h++)
                                 {
                                    if(isdigit(*(tokens[i].begin()+h)))
                                    {
                                       continue;
                                    }
                                    else
                                    {
                                       flag=-1;
                                       cout<<"\n\n INTEGER VALUES SHOULD BE NUMERIC\n\n";
                                       getch();
                                       return flag;
                                    }
                                 } 
                                 if(atoi(tokens[i].c_str())>65535)
                                 {
                                    cout<<"\n\nINTEGER LIMIT EXCEEDED\n\n";
                                    getch();
                                    flag=-1;
                                    return flag;
                                 }
                                 break;
                              }    
                            
                   case 'F':  if((ch1=='N' || ch2=='N' || ch1=='P') && tokens[i].substr(0,4)=="NULL" && tokens[i].size()==4)     
                              { 
                                  flag=-1;
                                  cout<<"\n\n NULL VALUE NOT ALLOWED WITH NOT_NULL CONSTRAINT\n\n";
                                  getch();
                                  return flag;
                              }
                              else if(ch1!='N' && ch2!='N' && ch1!='P' && tokens[i].substr(0,4)=="NULL" && tokens[i].size()==4)
                              {   
                                  break;
                              }
                              else
                              {  
                                 for(int h=0;h<tokens[i].size();h++)
                                 {
                                    if(*(tokens[i].begin()+h)=='.' && chk!=1)
                                    {  
                                       chk=1;
                                       continue;
                                    }
                                    
                                    if(isdigit(*(tokens[i].begin()+h)))
                                    {  
                                       continue;
                                    }
                                    else
                                    {
                                       cout<<"THE FLOAT MUST HAVE ALL DIGITS\n\n";
                                       flag=-1;
                                       getch();
                                       return flag;
                                    }
                                 }
                                 if(chk!=1)
                                 {
                                    cout<<"\n\nDECIMAL POINT NOT SPECIFIED\n\n";
                                    getch();
                                    flag=-1;
                                    return flag;
                                 }
                                 break;
                             }
                                 
                  case 'V':  if ( *tokens[i].begin()=='"' && *(tokens[i].end()-1)=='"' )
                             { 
                                if((ch1=='N' || ch2=='N' || ch1=='P') && tokens[i].substr(1,4)=="NULL" && tokens[i].size()==6)     
                                {   
                                    flag=-1;
                                    cout<<"\n\n NULL VALUE NOT ALLOWED WITH NOT_NULL CONSTRAINT\n\n";
                                    getch();
                                    return flag;
                                }
                                else if(ch1!='N' && ch2!='N' && ch1!='P' && tokens[i].substr(1,4)=="NULL")
                                {   
                                    if(tokens[i].size()==6)
                                    {
                                        break;
                                    }
                                    else
                                    {  
                                       cout<<"SEMANTICAL ERROR\n\n";
                                       getch();
                                       flag=-1;
                                       return flag;
                                    }
                                 }
                                 else if(tokens[i].size()<= L_V_CHAR+2)
                                 {   
                                     break;
                                 }
                                 else
                                 {
                                    cout<<"SIZE SHALL BE LESS THAN L_V_CHAR\n\n";
                                    getch();
                                    flag=-1;
                                    return flag;
                                 }
                             }
                             else
                             {  
                                cout<<"SEMANTICAL ERROR"<<endl;
                                getch();
                                flag=-1;
                                return flag;
                             }     
                                                      
                  case 'D':  if((ch1=='N' || ch2=='N' || ch1=='P') && tokens[i].substr(0,4)=="NULL" && tokens[i].size()==4)     
                             { 
                                flag=-1;
                                cout<<"\n\n NULL VALUE NOT ALLOWED WITH NOT_NULL CONSTRAINT\n\n";
                                getch();
                                return flag;
                             }
                             else if(ch1!='N' && ch2!='N' && ch1!='P' && tokens[i].substr(0,4)=="NULL" && tokens[i].size()==4)
                                      break;
                             else if(valid_date(tokens[i])==-1)
                             {
                                cout<<"\n\n INVALID DATE \n\n";
                                getch();
                                flag=-1;
                                return flag;
                             }
                             break;
               }
               
               if( ch1=='U' || ch2=='U' || ch1=='P')  
               { 
                  string s1=tokens[i];                                                                                    
                  string str1;
                  int k=0;
                  s=tokens[2]+".txt";
                  ifstream inp(s.c_str());
                  if(!inp)
                  {
                      cout<<"CANNOT OPEN "<<s<<" FILE\n\n";
                      getch();
                      exit(0);
                  }
                  
                  while(!inp.eof())
                  {
                     inp>>str1;
                     if(k%retrieve.size()==j)  // if the current attribute's column reached
                     {
                        if(s1==str1)
                        {
                           flag=-1;
                           cout<<"\n\n DUPLICATE VALUES NOT ALLOWED WITH UNIQUE CONSTRAINT\n\n";
                           getch();
                           return flag;
                        }
                     }
                     k++;
                  }
                  inp.close();
               }
               
               if(ch1=='F')
               {
                  char ch=implement_foreign_key(retrieve[j].constraint2,tokens[i]);     
                  if(ch=='n')
                  {
                     flag=-1;
                     return flag;
                  }
               }
            
          }    
          if(flag==1)
          {
              ofstream outp;
              s = tokens[2]+".txt";
              outp.open(s.c_str(),ios::app);
              int g = count_lines_file(s);
              if (g != 0)
              {    
                   outp<<"\n";
              }
              for(int i=5;i<tokens.size();i=i+2)    
              { 
                  if( *tokens[i].begin() == '"' || *tokens[i].begin()=='\'' )
                      outp<<tokens[i].substr(1,tokens[i].size()-2)<<"\t";
                  else
                      outp<<tokens[i]<<"\t";
              }
         
              outp.close();
              cout<<"\n\nRECORD SUCCESSFULLY INSERTED\n\n";
          }
          getch();
          return 1;
   }
  
  
  
  /* Input: Any string (relational operator)  
     Function: checks that s is a member of array rel_op
     Output: returns 1 if s is a member of array rel_op and 0 otherwise */
           
     int member_rel_op(string s)
     {
        for(int i=0;i<6;i++)
        {
           if(s==rel_op[i])
              return 1;
           else
              continue;
        }
        return 0;
     }
        
  /* Input: Index of the keyword "where" in the tokens vector
     Function: Checks that atmost one connector (and,or) can be present with where clause,
               and also checks that relational operator is necessarily present in the where clause
               and is from the rel_op array.
     Output: Returns flag (1 or -1), 1 if conditions satisfied and -1 otherwise   */
               
     int conditions(int where_index)
     {
        int flag=1;
            
        int diff=((tokens.size()-1)-where_index); /*difference of size of tokens
                                                     vector and index of where   */
        int i= where_index+2; // Index of first relational operator
             
        if(diff<3) 
        {
            flag=-1;
            cout<<"\n\nWHERE CONDITION NOT SPECIFIED PROPERLY\n\n\n";
            getch();
            return flag;
        }
        else if(diff==3)    // //If only one condition after where is entered
        {
            if(member_rel_op(tokens[i]))
            {
                flag=1;
            }
            else
            {
                cout<<"\n\n INVALID RELATIONAL OPREATOR \n\n";
                getch();
                flag=-1;
                return flag;
            }
        }
        else if(diff==7)     //If there are 2 conditions after where
        {
            int j=i+4;     //Index of second relational operator
            if(member_rel_op(tokens[i]) && member_rel_op(tokens[j]))   // Both operators are valid
            {
                flag=1;
            }
            else
            {
               cout<<"\n\n INVALID RELATIONAL OPERATOR \n\n";
               getch();
               flag=-1;
               return flag;
            }
                         
            j=i+2;
            if(tokens[j]=="AND" || tokens[j]=="OR")
            {
                flag=1;
            }
                         
            else
            {
                cout<<"\n\n CONNECTOR (and/or) MISSING\n\n";
                getch();
                flag=-1;
                return flag;
            }
        }     
        else if(diff >7)      //If more than 1 connector (and/or) in where clause is present
        {
            cout<<"\n\n ATMOST 2 PREDICATES ALLOWED WITH WHERE\n\n";
            getch();
            flag=-1;
            return flag;
        }
        else
        {
            flag=-1;
        }
        return flag;
     }
  
  
// Class to create fields that hold the keywords and their index
        
    class selection
    {
        public:
        string str;         //keyword
        int k_index;        //index of keyword
              
        selection()
        {
           k_index=-1;
        }
    };
        
    selection select1,from,where;
          
    vector<selection> kv; //vector for storing the keywords and their index
        
        
/* Input: None
   Function: Pushes the keywords and there index into the vector kv
   Output: The vector kv contains the keywords with their corresponding index in the query */
           
   void insert_select()
   {
      select1.str="SELECT";
      from.str="FROM";
      where.str="WHERE";   
           
      kv.push_back(select1);
      kv.push_back(from);
      kv.push_back(where);
           
      for(int j=0;j<tokens.size();++j)
      {
         for(int k=0;k<kv.size();++k)
         { 
             if(tokens[j]==kv[k].str)
             {
                 kv[k].k_index=index[j];
                 break;  
             }
         } 
      }
   }
          
   int w_index,f_index;  // global variables
        
/* Input: None
   Function: Parses the select command
   Output: returns 1 if query is correct else returns -1  */
          
   int parse_select()
   {
       int temp,diff,s_index;
       int flag=1;
       
       int index=kv[0].k_index;
       
       if(kv[2].k_index!=-1 && kv[1].k_index>kv[2].k_index)   // // Checks that 'from' is before 'where'
       {
           flag=-1;
           cout<<"Incorrect query";
           getch();
           return flag;
       }  
        
       if(tokens[tokens.size()-1]=="SELECT")
       {
           flag=-1;
           cout<<"\n\nQUERY NOT COMLETELY SPECIFIED\n\n";
           getch();
           return flag;
       }
            
       if(tokens[tokens.size()-1]=="FROM")
       {
           flag=-1;
           cout<<"\n\nTABLE NAME NOT SPECIFIED\n\n";
           getch();
           return flag;
       }
           
       s_index = kv[0].k_index;
       f_index = kv[1].k_index;
            
       // It will work if multiple attribute names are given then it ckecks the existence of the delimiter comma
       for(int i=s_index+2 ; i<f_index ; i=i+2)
       {
           if(tokens[i]!=",")
           {
               cout<<"\n\n DELIMITER BETWEEN ATTRIBUTES NOT SPECIFIED\n\n";
               flag=-1;
               return flag;
           }
       }
            
       w_index=kv[2].k_index;
       diff=w_index-f_index;
                  
      if(w_index==-1 && tokens.size()-1-f_index > 1)
      { 
          flag=-1;
          cout<<"\n\nWITHOUT JOIN CONDITION NOT MORE THAN ONE TABLE ALLOWED\n\n";
          getch();
          return flag;
      }   
                
      if(w_index!=-1)  // means if where specified
      {
          if(diff<2)
          {
                cout<<"\n\nTABLENAME NOT SPECIFIED\n\n";
                getch();
                flag=-1;
                return flag;
          }
           
           char ch1=table_name(tokens[kv[1].k_index+1]); 
           
           if(ch1=='n')
           { 
                 cout<<"\n\n TABLE DOES NOT EXIST \n\n";
                 getch();
                 exit(0);
           }            
          else if(diff==4)   // join case
          {
              if(tokens[f_index+2]!=",")
              {
                  flag=-1;
                  cout<<"\n\nDELIMITER ',' NOT PRESENT BETWEEN TABLE NAMES SPECIFIED \n\n"; 
                  return flag;
              }
          }
          else if(diff>4)
          {
              flag=-1;
              cout<<"\n\nNUMBER OF TABLES TO BE JOINED EXCEEDED (ATMOST TWO TABLES CAN BE JOINED)\n\n";
              getch();    
              return flag;
          }
          flag=conditions(w_index);  
      }
            
      if(flag != 1)
      {     
           cout<<"\n\n SYNTACTICALLY INCORRECT QUERY \n\n";
           getch();
           flag=-1;
           return flag;
      }
      return flag;
   } 
     
  
/* Input: 'attribute' specifies the name of attribute
          'type' specifies the datatype of attribute
   Function: checks whether a particular value is of the specified type 
   Output: returns 'y' if all conditions satisfied otherwise returns 'n'  */
         
   char chk_type_size(string attribute ,string type, string value)
   {
       char c='y';
       char ch =type[0];
       int x=-1;   // to keep track of the decimal point so that it appears only once ,,if case float
       switch(ch)
       {
          case 'C':  if(strlen(value.c_str())!=3 || value[0]!='\'' || value[value.size()-1]!='\'')
                     {
                         cout<<"\n\nvalue is not a character or quotes not correct\n\n";
                         getch();
                         c='n';
                         return c;
                     }                 
                     break;                         
                        
          case 'V':  if(strlen(value.c_str())>L_V_CHAR || value[0]!='"' || value[value.size()-1]!='"')
                     {  cout<<"length"<<strlen(value.c_str());
                        cout<<"value[0]"<<value[0]<<"value[last]"<<value[value.size()-1];
                         cout<<"Either the length of value exceeds the defined VARCHAR length or \" \" not specified\n\n";                                                 
                         getch();
                         c='n';
                         return c;
                     }                      
                     
                    break;
                   
          case 'S': if(value[0]!='"' || value[value.size()-1]!='"')
                    {
                        cout<<" The comparison with string requires \" \" \n\n";                                                 
                        c='n';
                        return c;
                    }      
                    break;
                        
          case 'B': if(strlen(value.c_str())==1)
                    {    if(value[0]!='T' && value[0]!='F')              
                         {
                             cout<<"\n\n BOOLEAN DATATYPE CAN HAVE A VALUE EITHER 'T' OR 'F' \n\n";
                             getch();
                             c='n';
                             return c;
                         }
                    } 
                    else
                    {
                         cout<<"\n\nSIZE OF BOOLEAN VARIABLE SHOULD BE EXACTLY 1\n\n";
                         getch();
                         c='n';
                         return c;
                    }
                    break;
                              
          case 'I':  for(int i=0;i<strlen(value.c_str());i++)
                     {
                          if(!isdigit(value[i]))
                          {
                             c='n';
                             cout<<"\n\n INTEGER VALUES SHOULD BE NUMERIC\n\n";
                             getch();
                             return c;
                          }
                     }
                     if(atoi(value.c_str())>65536)
                     {
                         cout<<"\n\nINTEGER LIMIT EXCEEDED\n\n";
                         getch();
                         c='n';
                         return c;
                     }
                             
                     break;
                           
          case 'F':  for(int i=0;i<strlen(value.c_str());i++)
                     {
                         if(value[i]=='.' && x!=1)
                         {
                             x=1;
                             continue;
                         }
                         if(isdigit(value[i]))
                         {
                             continue;
                         }
                         else
                         {
                             c='n';
                             cout<<"\n\n CHARACTERS OTHER THAN NUMERIC PRESENT\n\n";
                             getch();
                             return c;
                         }
                     }
                     if(x!=1)
                     {
                         cout<<"\n\nDECIMAL POINT NOT SPECIFIED\n\n";
                         getch();
                         c='n';
                         return c;
                     }
                     break;
                               
          case 'D':  int flag1=valid_date(value);
                     if(flag1==-1)
                     {
                         cout<<"\n\nINVALID DATE\n\n";
                         getch();
                         c='n';
                         return c;
                     }
                     break;
          }
          return c;     
   }   
          
          
/*
   Input:
          s1 specifies file name,
          j specifies the index of operator in rel_op array ,
          index specifies the index of the value of the attribute ,
          i specifies the attribute no,
          vector v specifies the line no satisfying where condition 
   Function: stores the record numbers satisfying the where condition in a vector 
   Output: vector containing the record numbers satisfying the where condition    */

   void implement_operator(string s1,int j,string after_op,int i,vector<int> &v,string datatype)
   {
      int flag=1;
      
      string str,s;
      int k=0,no;
      
      string day1,day2,month1,month2,year1,year2;
      
      int size_file = count_lines_file(s1+".txt");
      
      int size = count_lines_file(s1+"_schema.txt");
      
      int total = size_file*size;//to get the total word count
      
      s = s1 + ".txt";
      ifstream inp(s.c_str());
      
      v.clear();
            
      switch(j)
      {
          case 0: no=0;
                  for (int x=0;x<size;x++)
                  { 
                      inp>>str;
                  }
                  while(!inp.eof())
                  {
                      if(k==total)
                          break;    
                                    
                      inp>>str;
                      if(k%size==i-1)
                      {  
                         no++;
                         if(after_op==str)
                         {   
                             v.push_back(no+1);                                                      
                         }
                      }
                      k++;
                  }
                  break;   
        
          case 1: no=0;
                  
                  for (int x=0;x<size;x++)
                  { 
                      inp>>str;
                  }
                  
                  while(!inp.eof())
                  {
                    if(k==total)
                       break;
                       
                    inp>>str;
                    
                    if(k%size==i-1)
                    {
                       no++;
                    
                       if(datatype == "INT" || datatype == "FLOAT")
                         {
                              if( atof(str.c_str()) < atof(after_op.c_str()))
                              { 
                                  v.push_back(no+1);                                                      
                              }
                         }
                         else if(datatype == "STRING" || datatype == "CHAR" || datatype == "VARCHAR")
                         {
                              if( str < after_op)
                              { 
                                 v.push_back(no+1);                                                      
                              }
                         }
                         else if(datatype == "DATE")
                         {
                             day1=str.substr(0,2);
                             month1=str.substr(3,2);
                             year1=str.substr(6,4);
                             
                             day2=after_op.substr(0,2);
                             month2=after_op.substr(3,2);
                             year2=after_op.substr(6,4);
                             
                             if( atoi(year1.c_str()) < atoi(year2.c_str()))
                             {
                                 v.push_back(no+1);
                             }
                             else if( atoi(year1.c_str()) == atoi(year2.c_str())  && atoi(month1.c_str()) < atoi(month2.c_str()) )
                             {
                                 v.push_back(no+1);     
                             }
                             else if(atoi(year1.c_str()) == atoi(year2.c_str())  && atoi(month1.c_str()) == atoi(month2.c_str()) && atoi(day1.c_str()) < atoi(day2.c_str()) )
                             {
                                v.push_back(no+1);
                             }                    
                         }
                    }
                    k++;
                  }
                  break;
        
          case 2: no=0;
                  
                  for (int x=0;x<size;x++)
                  { 
                      inp>>str;
                  }
                  
                  while(!inp.eof())
                  {
                      if(k==total)
                         break;
                                  
                      inp>>str;
                                  
                      if(k%size==i-1)
                      {  
                         no++;
                         
                         if(datatype == "INT" || datatype == "FLOAT")
                         {
                              if( atof(str.c_str()) > atof(after_op.c_str()))
                              { 
                                  v.push_back(no+1);                                                      
                              }
                         }
                         else if(datatype == "STRING" || datatype == "CHAR" || datatype == "VARCHAR")
                         {
                              if( str > after_op)
                              { 
                                 v.push_back(no+1);                                                      
                              }
                         }
                         else if(datatype == "DATE")
                         {
                             day1=str.substr(0,2);
                             month1=str.substr(3,2);
                             year1=str.substr(6,4);
                             
                             day2=after_op.substr(0,2);
                             month2=after_op.substr(3,2);
                             year2=after_op.substr(6,4);
                             
                             if( atoi(year1.c_str()) > atoi(year2.c_str()))
                             {
                                 v.push_back(no+1);
                             }
                             else if( atoi(year1.c_str()) == atoi(year2.c_str())  && atoi(month1.c_str()) > atoi(month2.c_str()) )
                             {
                                 v.push_back(no+1);     
                             }
                             else if(atoi(year1.c_str()) == atoi(year2.c_str())  && atoi(month1.c_str()) == atoi(month2.c_str()) && atoi(day1.c_str()) > atoi(day2.c_str()) )
                             {
                                v.push_back(no+1);
                             }                    
                         }
                      }
                      k++;
                  }
                  break;
                  
          case 3: no=0;
                  
                  for (int x=0;x<size;x++)
                  { 
                      inp>>str;
                  }
                  
                  while(!inp.eof())
                  {
                     if(k==total)
                        break;
            
                     inp>>str;
                     
                     if(k%size==i-1)
                     {
                         no++;
                         if(datatype == "INT" || datatype == "FLOAT")
                         {
                              if( atof(str.c_str()) <= atof(after_op.c_str()))
                              { 
                                  v.push_back(no+1);                                                      
                              }
                         }
                         else if(datatype == "STRING" || datatype == "CHAR" || datatype == "VARCHAR")
                         {
                              if( str <= after_op)
                              { 
                                 v.push_back(no+1);                                                      
                              }
                         }
                         else if(datatype == "DATE")
                         {
                             day1=str.substr(0,2);
                             month1=str.substr(3,2);
                             year1=str.substr(6,4);
                             
                             day2=after_op.substr(0,2);
                             month2=after_op.substr(3,2);
                             year2=after_op.substr(6,4);
                             
                             if( atoi(year1.c_str()) < atoi(year2.c_str()))
                             {
                                 v.push_back(no+1);
                             }
                             else if( atoi(year1.c_str()) == atoi(year2.c_str())  && atoi(month1.c_str()) < atoi(month2.c_str()) )
                             {
                                 v.push_back(no+1);     
                             }
                             else if(atoi(year1.c_str()) == atoi(year2.c_str())  && atoi(month1.c_str()) == atoi(month2.c_str()) && atoi(day1.c_str()) <= atoi(day2.c_str()) )
                             {
                                v.push_back(no+1);
                             }                            
                         }
                     }
                     k++;
                  }
                  break;
          
          case 4: no=0;
                  for (int x=0;x<size;x++)
                  { 
                      inp>>str;
                  }
                  while(!inp.eof())
                  {
                     if(k==total)
                         break;
            
                     inp>>str;
                     if(k%size==i-1)
                     {
                         no++;
                         if(datatype == "INT" || datatype == "FLOAT")
                         {
                              if( atof(str.c_str()) >= atof(after_op.c_str()))
                              { 
                                  v.push_back(no+1);                                                      
                              }
                         }
                         else if(datatype == "STRING" || datatype == "CHAR" || datatype == "VARCHAR")
                         {
                              if( str >= after_op)
                              { 
                                 v.push_back(no+1);                                                      
                              }
                         }
                         else if(datatype == "DATE")
                         {  
                             day1=str.substr(0,2);
                             month1=str.substr(3,2);
                             year1=str.substr(6,4);
                             
                             
                             day2=after_op.substr(0,2);
                             month2=after_op.substr(3,2);
                             year2=after_op.substr(6,4);
                             
                             if( atoi(year1.c_str()) > atoi(year2.c_str()))
                             {
                                 v.push_back(no+1);
                             }
                             else if( (atoi(year1.c_str()) == atoi(year2.c_str()))  && (atoi(month1.c_str()) > atoi(month2.c_str())) )
                             {
                                 v.push_back(no+1);     
                             }
                             else if((atoi(year1.c_str()) == atoi(year2.c_str()))  && (atoi(month1.c_str()) == atoi(month2.c_str())) && (atoi(day1.c_str()) >= atoi(day2.c_str())) )
                             { 
                                v.push_back(no+1);
                             }                      
                         }
                     }
                     k++;
                   }
                   break;
          
         case 5: no=0;
                 for (int x=0;x<size;x++)
                  { 
                      inp>>str;
                  }
                 while(!inp.eof())
                 {
                    if(k==total)
                        break;
                    inp>>str;
                    if(k%size==i-1)
                    {  
                       no++;
                       
                       if(after_op!= str)
                       {   
                           v.push_back(no+1);                                                      
                       }
                    }
                    k++;
                 }
                 break;
      }
      inp.close();
   }          
        
 
 /* Input: 'index' specifies the index of where 
           's' specifies the file name 
    Function: checks for the existence of the attribute in the where condition and 
              that the attribute and its value have the same datatype
    Output: returns 1 if conditions in 'where' clause is properly satisfied
            else returns -1       */

    int implement_where(int index, string s)
    {
        int flag=1,chk=-1;
        int i=0;
        
        string s2=s+"_schema.txt";
        const char *p=s2.c_str();
        ifstream inp(p);
        
        while(!inp.eof())
        {
            i++;
            obj1.initialize();
            
            inp >> obj1.attribute >> obj1.data_type1 >> obj1.constraint1 >> obj1.constraint2; 
            
            if(obj1.attribute==tokens[index+1])
            {
              chk=1;                           
              break;
            } 
        }        // i   contains the  no. of attributes in file
        inp.close();
         
        if(chk==-1)
        {
           cout<<"\n\n UNDEFINED ATTRIBUTE IN WHERE CONDITION \n\n";
           getch();
           flag=-1;
           return flag;
        } 
            
        
        int j;
        int chk2=-1;
        for(int k=0;k<6;k++)
        {
            if(tokens[index+2]==rel_op[k])
            { 
               chk2=1;
               j=k;
               break;
            }
        }
         
        if(chk2==-1)
        {
             cout<<"\n\nNOT A VALID RELATIONAL OPERATOR IN WHERE CLAUSE\n\n";
             getch();
             flag=-1;
             return flag;
        }
         
        if(obj1.data_type1=="BOOLEAN")
        {
             if(j!=0 && j!=5)
             {
                     cout<<"\n\n WITH BOOLEAN DATATYPE, OPERATOR USED IN THE WHERE CONDITION COULD ONLY BE '=' OR '!='";
                     getch();
                     return -1;
             }
         } 
         else if( (obj1.data_type1=="INT" || obj1.data_type1=="FLOAT" || obj1.data_type1=="DATE") && tokens[index+3]=="NULL" )
         {   
             if(j!=0 && j!=5)
             {
                     cout<<"\n\n WITH VALUE NULL , OPERATOR USED IN THE WHERE CONDITION COULD ONLY BE '=' OR '!='";
                     getch();
                     return -1;
             }
         } 
         else if( (obj1.data_type1=="VARCHAR" || obj1.data_type1=="STRING" || obj1.data_type1=="CHAR") && tokens[index+3].substr(1,4)=="NULL" )
         {   
             if(j!=0 && j!=5)
             {
                     cout<<"\n\n WITH VALUE NULL , OPERATOR USED IN THE WHERE CONDITION COULD ONLY BE '=' OR '!='";
                     getch();
                     return -1;
             }
         } 
         
         
         if(obj1.data_type1=="INT" || obj1.data_type1=="FLOAT" || obj1.data_type1=="DATE" || obj.data_type1=="BOOLEAN")         // as no need to check datatype when null is given in where condition
         {
              if( tokens[index+3]!="NULL")
              { 
                 //to check value and the attribute in the where have same datatype 
                 char ch_k = chk_type_size( obj1.attribute, obj1.data_type1, tokens[index+3] );
             
                 if(ch_k=='n')
                 {
                     cout<<"\n\n DATATYPE MISMATCH BETWEEN THE VALUE AND THE ATTRIBUTE IN WHERE CONDITION \n\n";
                     getch();
                     flag=-1;
                     return flag;
                 }
             }
             implement_operator(s,j,tokens[index+3],i,line,obj1.data_type1);
         }
         else if(obj1.data_type1=="STRING" || obj1.data_type1=="VARCHAR" || obj.data_type1=="CHAR")
         {   
             if( tokens[index+3].substr(1,4)!="NULL")
             { 
                  char ch_k=chk_type_size(obj1.attribute,obj1.data_type1,tokens[index+3]);
             
                  if(ch_k=='n')
                  {
                      cout<<"\n\n DATATYPE MISMATCH BETWEEN THE VALUE AND THE ATTRIBUTE IN WHERE CONDITION \n\n";
                      getch();
                      flag=-1;
                      return flag;
                  }
             }
             implement_operator(s,j,tokens[index+3].substr(1,tokens[index+3].size()-2),i,line,obj1.data_type1);
         }
         
         
         if(tokens.size()-index==4)
         {
           if(line.size()==0)
           {
              flag=-1;
              cout<<"\n\nWHERE CONDITION NOT SATISFIED\n\n";
              getch();
              return flag;
           }
         } 

         
         if (tokens.size()-index==8)
         {
            if(line.size()==0)
            {
                cout<<"\n\n FIRST WHERE CONDITION NOT SATISFIED\n\n";
            }
            
            i=0, chk=-1;
            
            ifstream f(p);
            while(!f.eof())
            {
                 i++;
              
                 obj1.initialize();
              
                 f >> obj1.attribute >> obj1.data_type1>> obj1.constraint1 >> obj1.constraint2; 
              
                 if(obj1.attribute==tokens[index+5])
                 {
                     chk=1;                           
                     break;
                 } 
            }
            f.close();
          
            if(chk==-1)
            {
              cout<<"\n\n SECOND ATTRIBUTE  IN THE WHERE CONDITION UNDEFINED\n\n";
              getch();
              flag=-1;
              return flag;
            } 
            int j;
         
            for(int f=0;f<6;f++)
            {
               if(tokens[index+6]==rel_op[f])
               {
                  j=f;
                  break;
               }
               else
               {
                   cout<<"INVALID OPERATOR USED IN SECOND CONDITION\n\n";
                   getch();
                   flag=-1;
                   return flag;
               }
            }
            if(obj1.data_type1=="BOOLEAN")
            {
                 if(j!=0 && j!=5)
                 {
                     cout<<"\n\n WITH BOOLEAN DATATYPE, OPERATOR USED IN THE WHERE CONDITION COULD ONLY BE '=' OR '!='";
                     getch();
                     return -1;
                 }
             } 
             else if( (obj1.data_type1=="INT" || obj1.data_type1=="FLOAT" || obj1.data_type1=="DATE") && tokens[index+7]=="NULL" )
             {   
                 if(j!=0 && j!=5)
                 {
                     cout<<"\n\n WITH VALUE NULL , OPERATOR USED IN THE WHERE CONDITION COULD ONLY BE '=' OR '!='";
                     getch();
                     return -1;
                 }
             } 
             else if( (obj1.data_type1=="VARCHAR" || obj1.data_type1=="STRING" || obj1.data_type1=="CHAR") && tokens[index+7].substr(1,4)=="NULL" )
             {   
                 if(j!=0 && j!=5)
                 {
                     cout<<"\n\n WITH VALUE NULL , OPERATOR USED IN THE WHERE CONDITION COULD ONLY BE '=' OR '!='";
                     getch();
                     return -1;
                 }
             }
          
         
             if(obj1.data_type1=="INT" || obj1.data_type1=="FLOAT" || obj1.data_type1=="DATE" || obj.data_type1=="BOOLEAN")         // as no need to check datatype when null is given in where condition
             {
                 if( tokens[index+7]!="NULL")
                 { 
                     //to check value and the attribute in the where have same datatype 
                     char ch_k=chk_type_size(obj1.attribute,obj1.data_type1,tokens[index+7]);
             
                     if(ch_k=='n')
                     {
                         cout<<"\n\n DATATYPE MISMATCH BETWEEN THE VALUE AND THE ATTRIBUTE IN WHERE CONDITION \n\n";
                         getch();
                         flag=-1;
                         return flag;
                     }
                 }
             
                 implement_operator(s,j,tokens[index+7],i,line1,obj1.data_type1);
             }
             else if(obj1.data_type1=="STRING" || obj1.data_type1=="VARCHAR" || obj.data_type1=="CHAR")
             {   
                if( tokens[index+3].substr(1,4)!="NULL")
                { 
                     char ch_k=chk_type_size(obj1.attribute,obj1.data_type1,tokens[index+7]);
              
                     if(ch_k=='n')
                     {
                         cout<<"\n\n DATATYPE MISMATCH BETWEEN THE VALUE AND THE ATTRIBUTE IN WHERE CONDITION \n\n";
                         getch();
                         flag=-1;
                         return flag;
                     }
                 }
                 implement_operator(s,j,tokens[index+7].substr(1,tokens[index+7].size()-2),i,line1,obj1.data_type1);
              }
         
              if(line1.size()==0)
              {   
                  cout<<"\n\n SECOND WHERE CONDITION NOT SATISFIED\n\n";
              }
                  
              if(tokens[index+4]=="OR")
              {
                  if(line.size()==0 && line1.size()==0)
                  {
                     flag=-1;
                     cout<<"\n\n BOTH WHERE CONDITIONS NOT SATISFIED FOR ANY RECORD\n\n";
                     getch();
                     return flag;
                  }
                  
                  or_line.insert(or_line.end(),line.begin(),line.end());
            
                  for(int i=0;i<line1.size();i++)
                  {  
                     int f=-1;
               
                     for(int j=0;j<or_line.size();j++)
                     {   
                         if(line1[i]==or_line[j])
                         {
                             f=1;
                             break;
                         }
                     }
                     if(f==-1)
                     {
                         or_line.push_back(line1[i]);
                     }
                   }  
               }
               else if(tokens[index+4]=="AND")
               {
                   if(line.size()==0 || line1.size()==0)
                   {
                      flag=-1;
                      cout<<"\n\n ONE OF THE TWO WHERE CONDITIONS NOT SATISFIED BY ANY RECORD\n\n";
                      getch();
                      return flag;
                   }
                   
                   for(int i=0;i<line.size();i++)
                   {
                      int f=-1;
                      
                      for(int j=0;j<line1.size();j++)
                      {
                          if(line[i]==line1[j])
                          {
                              f=1;
                              break;
                          }
                      }
                      if(f==1)
                      {         
                          and_line.push_back(line[i]);
                      }        
                   }
                   
                   if(and_line.size()==0)
                   {
                       cout<<"\n\n NO RECORDS SATISFYING BOTH CONDITIONS WITH AND CONNECTOR\n\n";
                       getch();
                       flag=-1;
                       return flag;
                   }      
               }
         }
         return flag;
      }

/* Input: k specifies record number
          w_index specifies index of where in the query
   Function: checks that the kTH line in the file is among 
             the record number satisfying the where conditions
   Output: returns 1 if k satisfies the where condition
           else returns -1       */       
            
  int check_no(int k,int w_index)
  {
       int flag=-1;
     
       if(tokens.size()-w_index==4)
       {
           for(int i=0;i<line.size();i++)
           {
              if(k==line[i])
              {
                 flag=1;
                 return flag;
              }
           }
       }
       else if(tokens.size()-w_index==8)
       {
           if(tokens[w_index+4]=="OR")
           {
               for(int i=0;i<or_line.size();i++)
               {
                   if(k==or_line[i])
                   {
                      flag=1;
                      return flag;
                   }
               }
           }
           else if(tokens[w_index+4]=="AND")
           {
              for(int i=0;i<and_line.size();i++)
              {
                 if(k==and_line[i])
                 {
                    flag=1;
                    return flag;
                 }
              }
           }
       }
       return flag;
  }
 
        
 /* Input: None
    Function: displays the selected tuples
    Output: returns -1 if no tuples to be selected
              else returns 1  */
                  
    int implement_select()
    {  
       int flag = 1;
       
       string s=tokens[kv[1].k_index+1]+"_schema.txt";
       int size=count_lines_file(s);  // size contains the no. of attributes in the file
       
       s=tokens[kv[1].k_index+1]+".txt";
       int size1=count_lines_file(s);   // size1 has the no. of tuples in the file
       
       int count = size1 * size;
       
       if(size1<2)   // if no tuples in file
       {
           cout<<"\n\n NO RECORDS TO BE DISPLAYED (OR SELECTED)\n\n";
           getch();
           return 1;        
       }
       
       if (tokens[1]=="*" && tokens.size()==4)
       {
           string str;
           ifstream inp(s.c_str(),ios::in);
           int k=1; // TO MAINTAIN THE WORD COUNT
           while(!inp.eof())
           {
              if(k>count)
              break;
              
              inp>>str;
          
              if(k%size==0)
                cout<<str<<"\n";
              else
                cout<<str<<"\t";           
              
              k++;   
           }              //end of while
           inp.close();
       }  
       
       else if(tokens[1]=="*" && tokens[4]=="WHERE")
       {
             flag = implement_where(4,tokens[kv[1].k_index+1]);
             
             if (flag == -1)
             {
                 return flag;
             }
             
             string str;
             
             ifstream inp(s.c_str());
             
             int k=1; // TO MAINTAIN THE WORD COUNT
             int f=-1;
             int k1=1; // TO MAINTAIN THE TUPLE COUNT
             
             cout<<"\n\n";
             
             while(!inp.eof())
             {
                if(k>count)
                    break;
              
                f = check_no(k1,4);  
                
                inp>>str;
                
                if(k<size)
                   cout<<str<<"\t\t";
                if(k==size)
                   cout<<str<<"\n";
                
                if(f==1)
                {
                  if(k%size==0)
                  { 
                   cout<<str<<"\n";
                  }
                  else
                  {
                   cout<<str<<"\t\t";
                  } 
                }
              
                if(k%size==0)
                    k1++; //INCREMENTING THE LINE COUNT
               
                k++; //INCREMENTING THE WORD COUNT
             }
             inp.close();
       }
       else    // select attributes without where and where case
       {
           int diff = kv[1].k_index-kv[0].k_index;
           s = tokens[kv[1].k_index+1] + "_schema.txt";
           const char* p = s.c_str();
           
           for( int i=1;i<diff;i=i+2)
           {
              int chk=-1;
              int j=0;
              
              ifstream f(p);
              
              while(!f.eof())
              {
                j++; // TO STORE THE ATTRIBUTE NO 
                
                obj1.initialize();
                
                f >> obj1.attribute >> obj1.data_type1 >> obj1.constraint1 >> obj1.constraint2; 
                if(obj1.attribute == tokens[i])
                {
                   chk=1; 
                   attr_no.push_back(j);                          
                   break;
                }  
             }
             f.close();
            
             if(chk==-1)
             {
               cout<<"\n\n ATTRIBUTE " <<tokens[i]<<" TO BE SELECTED IS UNDEFINED\n\n";
               getch();
               flag=-1;
               return flag;
             } 
           }
          
           cout<<"\n\n";
          
          //to print selected attributes when no where condition specified
           if(tokens.size()==kv[1].k_index+2)
           {
               string str;
               s=tokens[kv[1].k_index+1]+".txt";
               ifstream inp(s.c_str(),ios::in);
               int k=1,f; // TO MAINTAIN THE WORD COUNT
               while(!inp.eof())
               {
                   if(k>count)
                      break;
                           
                   f=-1;         
                   for(int i=0;i<attr_no.size();i++)
                   {
                       if((k%size)==attr_no[i]%size)
                       {               
                          f=1;
                          break;
                       }
                   }
                   inp>>str;
               
                   if(f==1)
                   {
                      if(k%size==0)
                      {
                         cout<<str<<"\n";
                      }
                      else
                      cout<<str<<"\t";
                   }  
                   if(k%size==0)
                      cout<<"\n";             
                   k++;
                }
                inp.close();
             return 1;
           }
     
          //to print selected attributes when where condition specified
           if(tokens[kv[1].k_index+2]=="WHERE")
           {
             flag =implement_where(kv[1].k_index+2,tokens[kv[1].k_index+1]);
             
             if (flag ==-1)
                 return flag;
             
             string str;
             s=tokens[kv[1].k_index+1]+".txt";
               
             ifstream inp(s.c_str());
               
             int k=1,f; // TO MAINTAIN THE WORD COUNT
             int k1=1,f1=-1; // TO MAINTAIN THE LINE COUNT
               
             cout<<"\n\n";
               
             while(!inp.eof())
             {
                 if(k>count)
                     break;
                 
                 f=-1;
                 //TO CHECK IF IT IS THE ATTRIBUTE TO BE PRINTED
                 for(int i=0;i<attr_no.size();i++)
                 {
                      if((k%size)==attr_no[i]%size)
                      {               
                          f=1;
                          break;
                      }
                 }
                 f1=check_no(k1,kv[1].k_index+2); //IF IT IS THE VALID LINE NO i.e. IT SATISFIES THE WHERE CONDITION 
                
                 inp>>str;
               
                 if(f==1 && k<size)
                       cout<<str<<"\t";
                 else if(f==1 && k==size)
                       cout<<str<<"\n";
              
                 if(f==1 && f1==1)
                 {
                     if(k%size==0)
                         cout<<str<<"\n";
                     else
                         cout<<str<<"\t\t";
                 }  
                
                 if(k%size==0)
                 {
                    k1++;//INCREMENTING THE LINE COUNT
                    cout<<"\n";
                 }             
                 k++;// INCREMENTING THE WORD COUNT
             }
              
             inp.close();
          }
     }       // end of else        
     return flag;
   }     // end of function             
 
 
 int op_index,att1_num,att2_num;    
 string att1,att2;//TWO ATTRIBUTE IN THE JOIN CONDITION
 
 
/* Input: None
   Function: parses the syntax of join condition and checks that the tables being joined 
             share primary-foreign key relation  
   Output: returns 1 if synatx is correct else returns -1    */
       
   int parse_join()
   { 
      string table1, table2;
     
      //Breaking table1.attribute1 into table1 and attribute1
      string s = tokens[w_index+1];
      const char *p = s.c_str();
  
      int l = strcspn(p,"."); //to find the index of '.'
      
      table1 = s.substr(0,l);
      att1=s.substr(l+1,s.size()-1-l);
      
     
      // Breaking table2.attribute2 into table2 and attribute2        
      string s1=tokens[w_index+3];
      const char *p1=s1.c_str();
        
      int l1=strcspn(p1,".");//to find the index of .
      
      table2 = s1.substr(0,l1);
      att2 = s1.substr(l1+1,s1.size()-1-l1);

      if((tokens[f_index+1]!=table1 && tokens[f_index+1]!=table2) || (tokens[f_index+3]!=table2 && tokens[f_index+3]!=table1)) 
      {
         cout<<"\n\n INCORRECT TABLE NAME\n\n";
         getch();
         return -1;
      }
          
      int chk=-1,chk1=-1,i=0;
      
      string s2=table1+"_schema.txt";
      ifstream f(s2.c_str());
      
      while(!f.eof())
      {
         i++;
         obj1.initialize();
         f >> obj1.attribute >> obj1.data_type1 >> obj1.constraint1 >> obj1.constraint2; 
         if(obj1.attribute==att1)
         {
            att1_num=i;
            chk=1;       
            break;                    
         } 
      }
      f.close();
         
      if(chk==-1)
      {
        cout<<"\n\nFIRST ATTRIBUTE IN THE JOIN CONDITION UNDEFINED\n\n";
        getch();
        return -1;
      } 
         
      i=0;
      
      s2=table2+"_schema.txt";
      ifstream inp(s2.c_str());
      
      while(!inp.eof())
      {
          i++;
        
          ob.initialize();
        
          inp >> ob.attribute >> ob.data_type1 >> ob.constraint1 >> ob.constraint2; 
        
          if(ob.attribute==att2)
          {
             att2_num=i;
             chk1=1;       
             break;                    
          } 
      }
      inp.close();
         
      if(chk1==-1)
      {
         cout<<"\n\nSECOND ATTRIBUTE IN THE JOIN CONDITION UNDEFINED\n\n";
         getch();
         return -1;
      } 
        
      if(obj1.data_type1!=ob.data_type1)
      {
         cout<<"\n\nDATATYPE MISMATCH FOR JOIN ATTRIBUTE\n\n";
         getch();
         return -1;
      }

      if(obj1.constraint1=="PRIMARY_KEY")
      {
         if(ob.constraint1!="FOREIGN_KEY_REFERENCES")
         {
             cout<<"\n\n JOIN ATTRIBUTES DO NOT POSSESS PRIMARY KEY , FOREIGN KEY RELATION, JOIN NOT POSSIBLE \n\n";
             getch();
             return -1;
         }
         else
         {
            if(ob.constraint2!=tokens[w_index+1])
            {
               cout<<"\n\nWRONG TABLE REFERENCED BY THE FOREIGN KEY\n\n\n";
               getch();
               return -1;
            }  
         }
     }     
     else if(ob.constraint1=="PRIMARY_KEY")
     {
         if(obj1.constraint1!="FOREIGN_KEY_REFERENCES")
         {
             cout<<"\n\n JOIN ATTRIBUTES DO NOT HAVE PRIMARY KEY , FOREIGN KEY RELATION, JOIN NOT POSSIBLE \n\n";
             getch();
             return -1;
         }
         else
         {
             if(obj1.constraint2!=tokens[w_index+3])
             {
                 cout<<"\n\nWRONG TABLE REFERENCED BY THE FOREIGN KEY\n\n\n";
                 getch();
                 return -1;
             }  
         }
     }   
        
     for(int j=0;j<6;j++)
     {
         if(rel_op[j]==tokens[w_index+2])
         {
             op_index = j;
             break;
         }
     }
     
     if(tokens[f_index+1]!=table1 && tokens[f_index+1]==table2)
     {
           int t1=att1_num;
           att1_num=att2_num;
           att2_num=t1;
     }
        
     if(ob.data_type1=="BOOLEAN")
     {
         if(op_index!=0 && op_index!=5)
         {
             cout<<"\n\n WITH DATATYPES OTHER THAN INT AND FLOAT, JOIN CONDITION COULD ONLY BE '=' OR '!='";
             getch();
             return -1;
         }
     }
     return 1;
  }
  
    void join_tuples()
    {
       
        line.clear(); 
        
        string s = tokens[f_index+1] + "_" + tokens[f_index+3] + ".txt";
        ifstream f(s.c_str(),ios::in);
        
        int size_tab1 = count_lines_file(tokens[f_index+1]+"_schema.txt");
        
        int size=count_lines_file(tokens[f_index+1] + "_" + tokens[f_index+3] + "_schema.txt");      //size of join file
     
        att2_num = att2_num + size_tab1;
        
        int w_count=0,line_no=1;       
        string day1, day2, month1, month2, year1, year2;
        string str,str_att1,str_att2;
         
        switch(op_index)
        {
            case 0:  while(!f.eof())
                     {  
                        f>>str;
                    
                        if(w_count%size==att1_num-1)
                        {
                          str_att1=str;
                        }
                        if(w_count%size==att2_num-1)
                        {
                          str_att2=str;
                        }
                        if(w_count%size==size-1)
                        {
                          if(str_att1==str_att2 && line_no!=1)
                            line.push_back(line_no);
                           
                          line_no++;
                        }
                     
                        w_count++;
                     }
                     f.close();
                     break;
        
            case 1:  while(!f.eof())
                     {
                        f>>str;
                        if(w_count%size==att1_num-1)
                        {
                            str_att1=str;
                        }
                        if(w_count%size==att2_num-1)
                        {
                           str_att2=str;
                        }
                        if(w_count%size==size-1)
                        {  
                           if( ob.data_type1=="CHAR" || ob.data_type1=="VARCHAR" || ob.data_type1=="STRING")
                           {   
                               if(str_att1 < str_att2 && line_no!=1)
                               {
                                  line.push_back(line_no);
                               }
                           }
                           else if( ob.data_type1=="INT" || ob.data_type1=="FLOAT")
                           {
                                if(atof(str_att1.c_str()) < atof(str_att2.c_str()) && line_no!=1)
                                {
                                    line.push_back(line_no);
                                }
                           }
                           else if( ob.data_type1=="DATE" && line_no!=1)
                           { 
                             day1=str_att1.substr(0,2);
                             month1=str_att1.substr(3,2);
                             year1=str_att1.substr(6,4);
                             
                             day2=str_att2.substr(0,2);
                             month2=str_att2.substr(3,2);
                             year2=str_att2.substr(6,4);
                             
                             if( atoi(year1.c_str()) < atoi(year2.c_str()))
                             {
                                 line.push_back(line_no);
                             }
                             else if( atoi(year1.c_str()) == atoi(year2.c_str())  && atoi(month1.c_str()) < atoi(month2.c_str()) )
                             {
                                  line.push_back(line_no);
                             }
                             else if(atoi(year1.c_str()) == atoi(year2.c_str())  && atoi(month1.c_str()) == atoi(month2.c_str()) && atoi(day1.c_str()) < atoi(day2.c_str()) )
                             {
                                line.push_back(line_no);
                             }         
                          }   
                          line_no++;
                        }
                        w_count++;
                     }
                     f.close();
                     break;
        
            case 2:  while(!f.eof())
                     {
                        f>>str;
                        if(w_count%size==att1_num-1)
                        {
                           str_att1=str;
                        }
                        if(w_count%size==att2_num-1)
                        {
                            str_att2=str;
                        }
                        if(w_count%size==size-1)
                        {
                           if( ob.data_type1=="CHAR" || ob.data_type1=="VARCHAR" || ob.data_type1=="STRING")
                           {   
                               if(str_att1 > str_att2 && line_no!=1)
                               {
                                  line.push_back(line_no);
                               }
                           }
                           else if( ob.data_type1=="INT" || ob.data_type1=="FLOAT")
                           {
                                if(atof(str_att1.c_str()) > atof(str_att2.c_str()) && line_no!=1)
                                {
                                    line.push_back(line_no);
                                }
                           }
                           else if( ob.data_type1=="DATE" && line_no!=1)
                           { 
                             day1=str_att1.substr(0,2);
                             month1=str_att1.substr(3,2);
                             year1=str_att1.substr(6,4);
                             
                             day2=str_att2.substr(0,2);
                             month2=str_att2.substr(3,2);
                             year2=str_att2.substr(6,4);
                             
                             if( atoi(year1.c_str()) > atoi(year2.c_str()))
                             {
                                 line.push_back(line_no);
                             }
                             else if( atoi(year1.c_str()) == atoi(year2.c_str())  && atoi(month1.c_str()) > atoi(month2.c_str()) )
                             {
                                  line.push_back(line_no);
                             }
                             else if(atoi(year1.c_str()) == atoi(year2.c_str())  && atoi(month1.c_str()) == atoi(month2.c_str()) && atoi(day1.c_str()) > atoi(day2.c_str()) )
                             {
                                line.push_back(line_no);
                             }
                          }            
                          line_no++;
                        }
                        w_count++;
                     }
                     f.close();
                     break;
        
            case 3:  while(!f.eof())
                     {
                     f>>str;
                    
                     if(w_count%size==att1_num-1)
                     {
                        str_att1=str;
                      
                     }
                     if(w_count%size==att2_num-1)
                     {
                          str_att2=str;
                     }
                     if(w_count%size==size-1)
                     {
                           if( ob.data_type1=="CHAR" || ob.data_type1=="VARCHAR" || ob.data_type1=="STRING")
                           {   
                               if(str_att1 <= str_att2 && line_no!=1)
                               {
                                  line.push_back(line_no);
                               }
                           }
                           else if( ob.data_type1=="INT" || ob.data_type1=="FLOAT")
                           {
                                if(atof(str_att1.c_str()) <= atof(str_att2.c_str()) && line_no!=1)
                                {
                                    line.push_back(line_no);
                                }
                           }
                           else if( ob.data_type1=="DATE" && line_no!=1)
                           { 
                             day1=str_att1.substr(0,2);
                             month1=str_att1.substr(3,2);
                             year1=str_att1.substr(6,4);
                             
                             day2=str_att2.substr(0,2);
                             month2=str_att2.substr(3,2);
                             year2=str_att2.substr(6,4);
                             
                             if( atoi(year1.c_str()) <  atoi(year2.c_str()))
                             {
                                 line.push_back(line_no);
                             }
                             else if( atoi(year1.c_str()) == atoi(year2.c_str())  && atoi(month1.c_str()) < atoi(month2.c_str()) )
                             {
                                  line.push_back(line_no);
                             }
                             else if(atoi(year1.c_str()) == atoi(year2.c_str())  && atoi(month1.c_str()) == atoi(month2.c_str()) && atoi(day1.c_str()) <= atoi(day2.c_str()) )
                             {
                                line.push_back(line_no);
                             }            
                           }  
                           line_no++;
                        }
                        w_count++;
                     }
                     f.close();
                     break;
        
            case 4:  while(!f.eof())
                     {
                        f>>str;
                    
                     if(w_count%size==att1_num-1)
                     {
                        str_att1=str;
                      
                     }
                     if(w_count%size==att2_num-1)
                     {
                          str_att2=str;
                     }
                     if(w_count%size==size-1)
                     {
                          if( ob.data_type1=="CHAR" || ob.data_type1=="VARCHAR" || ob.data_type1=="STRING")
                           {   
                               if(str_att1 >= str_att2 && line_no!=1)
                               {
                                  line.push_back(line_no);
                               }
                           }
                           else if( ob.data_type1=="INT" || ob.data_type1=="FLOAT")
                           {
                                if(atof(str_att1.c_str()) >= atof(str_att2.c_str()) && line_no!=1)
                                {
                                    line.push_back(line_no);
                                }
                           }
                           else if( ob.data_type1=="DATE" && line_no!=1)
                           { 
                             day1=str_att1.substr(0,2);
                             month1=str_att1.substr(3,2);
                             year1=str_att1.substr(6,4);
                             
                             day2=str_att2.substr(0,2);
                             month2=str_att2.substr(3,2);
                             year2=str_att2.substr(6,4);
                             
                             if( atoi(year1.c_str()) > atoi(year2.c_str()))
                             {
                                 line.push_back(line_no);
                             }
                             else if( atoi(year1.c_str()) == atoi(year2.c_str())  && atoi(month1.c_str()) > atoi(month2.c_str()) )
                             {
                                  line.push_back(line_no);
                             }
                             else if(atoi(year1.c_str()) == atoi(year2.c_str())  && atoi(month1.c_str()) == atoi(month2.c_str()) && atoi(day1.c_str()) >= atoi(day2.c_str()) )
                             {
                                line.push_back(line_no);
                             }
                           }            
                           line_no++;
                        }
                        w_count++;
                     }
                     f.close();
                     break;
        
        
        case 5: 
                 while(!f.eof())
                 {
                     f>>str;
                    
                     if(w_count%size==att1_num-1)
                     {
                        str_att1=str;
                      
                     }
                     if(w_count%size==att2_num-1)
                     {
                          str_att2=str;
                     }
                     if(w_count%size==size-1)
                     {
                          if(str_att1 != str_att2 && line_no!=1)
                            line.push_back(line_no);
                          line_no++;
                     }
                     w_count++;
                  }
                  f.close();
                  break;
          } 
    }   
      

/* Input: None
   Function: displays the tuples satisfying the join condition
   Output: returns 1 if tuple satisfying the join condition displayed properly
           else returns -1      */
    
    int display_select_join()
    {
       int flag=1;
       
       string s=tokens[f_index+1]+"_"+tokens[f_index+3]+"_schema.txt";
       int size=count_lines_file(s);
       
       s=tokens[f_index+1]+"_"+tokens[f_index+3]+".txt";;
       int size1=count_lines_file(s);
       
       int count=size1*size;

       //WHEN * IS SPECIFIED 
       if(tokens[1]=="*")
       {
          string str;
          ifstream inp(s.c_str());
          int f;
          int k1=1; // TO MAINTAIN THE LINE COUNT
          cout<<"\n\n";
          while(!inp.eof())
          {
                if(k1 > size1)
                   break;
                
                f=-1;             
                for(int i=0;i<line.size();i++)
                {
                  if(k1==line[i])
                  {
                    f=1;
                    break;
                  }
                }  
                
                getline(inp,str);
                
                if(f==1)
                {
                  if(k1==size1)
                     cout<<str;
                  else
                     cout<<str<<"\n";
                }
                k1++; //INCREMENTING THE LINE COUNT
             }
           inp.close();
       }
      //when we want to display only selected attributes from tuples that satisfy the join condition 
      else
      {
           attr_no.clear();
           
           int diff = kv[1].k_index - kv[0].k_index;
           
           s=tokens[f_index+1]+"_"+tokens[f_index+3]+"_schema.txt";
           const char* p = s.c_str();
           
           for( int i=1;i<diff;i=i+2)
           {
                int chk=-1;
                ifstream inp(p);
                int j=0;
                while(!inp.eof())
                {
                    j++; // TO STORE THE ATTRIBUTE NOs 
                  
                    obj1.initialize();
                  
                    inp >> obj1.attribute >> obj1.data_type1 >> obj1.constraint1 >> obj1.constraint2; 
                    if(obj1.attribute==tokens[i])
                    {
                        chk=1; 
                        attr_no.push_back(j);                          
                        break;
                    } 
                }
                inp.close();
             
                if(chk==-1)
                {
                     cout<<"\n\n ATTRIBUTE " <<tokens[i]<<" TO BE SELECTED IS UNDEFINED\n\n";
                     getch();
                     flag=-1;
                     return flag;
                } 
           }
           cout<<"\n\n";
       
           //to print selected attributes 
           string str;
           s = tokens[f_index+1]+"_"+tokens[f_index+3]+".txt";
           
           ifstream inp(s.c_str());
           
           int k=1,f; // TO MAINTAIN THE WORD COUNT
           int k1=1,f1; // TO MAINTAIN THE LINE COUNT
           while(!inp.eof())
           {
               if(k>count)
                  break;
               f=-1;
               f1=-1;
                 
               //TO CHECK IF IT IS THE ATTRIBUTE TO BE PRINTED
               for(int i=0;i<attr_no.size();i++)
               {
                   if((k%size)==attr_no[i]%size)
                   {               
                      f=1;
                      break;
                   }
               }
                
               //IF IT IS THE VALID LINE NO i.e. IT SATISFIES THE WHERE CONDITION 
               for(int i=0;i<line.size();i++)
               {
                  if(k1==line[i])
                  {
                    f1=1;
                    break;
                  }
               }  
                 
               inp>>str;
               
               if(f==1 && f1==1)
               {
                  if(k%size==0)
                     cout<<str<<"\n";
                  else
                     cout<<str<<"\t";
               }  
                
               if(k%size==0)
               {
                  k1++;//INCREMENTING THE LINE COUNT
               }             
               k++;// INCREMENTING THE WORD COUNT
             }
             inp.close();
          } 
          getch();
          return 1;
    }
  
  
/* Input: None
   Function: displays the tuples satisfying the join condition
   Output: returns -1 if no tuples to be selected
             else returns 1  */
  
   int implement_select_join()
   {
        int flag=1,count1,count2;
        
        char ch=table_name(tokens[f_index+1]);
        if(ch=='n')
        {
          flag=-1;
          cout<<"\n\n"<<tokens[f_index+1]<<"TABLE DOES NOT EXIST\n\n";   
          getch();
          return flag;
        }
        
        ch=table_name(tokens[f_index+3]);
        
        if(ch=='n')
        {
           flag=-1;
           cout<<"\n\n"<<tokens[f_index+3]<<" TABLE DOES NOT EXIST\n\n";   
           getch();
           return flag;
        }
        
        flag=parse_join();
        
        if(flag==-1)
        {
           return flag;
        }
        
        string str;
        
        string file1=tokens[f_index+1];
        string file2=tokens[f_index+3];
        string file=file1+"_"+file2;
        
        
        //For combining the schema of both the files to get a new combined schema file 
        //for copying schema file of file1 into new combined schema file
        
        string temp1=file1+"_schema.txt";
        string s=file+"_schema.txt";
        
       
        ofstream f(s.c_str(),ios::out);
        
        ifstream f1(temp1.c_str(),ios::in);
        
        while(!f1.eof())
        {
          getline(f1,str);
          f << str << "\n";
        }  
        f1.close();

       //for copying schema file of file2 into new combined schema file
       string temp2 = file2+"_schema.txt";
       count2 = count_lines_file(temp2);
       
       ifstream f2(temp2.c_str());
       
       int k=0;    
       
       while(!f2.eof())
       {
          k++;
          getline(f2,str);
          if(k<count2) //if it is not the last line to be written in the combined file
              f<<str<<"\n";
          else if(k==count2)//if it is the last line to be written in the combined file
              f<<str;
       }  
      
       f2.close();
       f.close();
                            //to check for duplicacy of attributes i.e. attribute names in both the files to be joined must be distinct
       vector<string> dup; //to check for duplicacy of attributes
       
       dup.clear();
       
       ifstream fi(s.c_str());
       
       while(!fi.eof())
       {
          obj1.initialize();
          fi >> obj1.attribute >> obj1.data_type1 >> obj1.constraint1 >> obj1.constraint2;
          
          //to check for duplicacy of attributes in the combined schema file other than the attributes in the join condition
          for(int i=0;i<dup.size();i++)
          {
             if(obj1.attribute==att1 ||obj1.attribute==att2)
             {
                 continue;
             }
             
             if(obj1.attribute==dup[i])
             {
                 flag=-1;
                 cout<<"\n\n DUPLICATE ATTRIBUTES IN THE JOINED FILE NOT ALLOWED\n\n";
                 getch();
                 fi.close();
                 return flag;
              }
          } 
          dup.push_back(obj1.attribute);
       }
       fi.close();           
                  
       //For combining the tuples of both the files
       //for copying records of both file1 and file2 into new combined file
       temp1=file1+".txt";
       count1=count_lines_file(temp1); //to get the no of tuples in the first file
       
       if(count1==1)
       {
           flag=-1;
           cout<<"\n\n NO RECORDS IN THE FIRST TABLE TO BE JOINED\n\n";
           getch();
           return flag;
       }
        
       temp2=file2+".txt";
       count2=count_lines_file(temp2);//to get the no of records in the second file
         
       if(count2==1)
       {
           flag=-1;
           cout<<"\n\nNO RECORDS IN THE SECOND TABLE TO BE JOINED\n\n";
           getch();
           return flag;
       }
        
       ifstream fin(temp1.c_str(),ios::in);   //1st file opened
       
       s=file+".txt";
       ofstream fout(s.c_str(),ios::out);  // output file opened    
        
       string srg1,srg2;//for read the lines from both the files
        
       //to store the attributes name from both the files in the combined file        
       int c1=0;
       int c2;
       while(!fin.eof())
       {
           c1++;
           c2=0;
           
           getline(fin,srg1);   
           ifstream inp(temp2.c_str(),ios::in);            
           
           while(!inp.eof())
           {
              c2++;
              getline(inp,srg2);
              if(c1==1 && c2==1)
              {
                  fout<<srg1;
                  fout<<srg2<<"\n";
                  break;
              } 
            
              if(c1>1 && c2==1)
              {
                  continue;
              }
            
              fout<<srg1;
              
              if(c1==count1)
              {
                  fout<<"\t";
              }
              if(c1==count1 && c2==count2)
                 fout<<srg2;
              else 
                 fout<<srg2<<"\n";
           }
           inp.close();
        }  
        fin.close();       
        fout.close();
         
        join_tuples();
        
        if(line.size()==0)
        {
           cout<<"\n\nNO RECORDS SATISFY THE JOIN CONDITION\n\n";
           getch();
           flag=-1;
           return flag;
        } 
               
        flag=display_select_join();
        return flag;  
    }
    
       
      
   int main()
   {
       
              tokens.clear();
              enter();
              
              getch();
              char ch1;
              int chk;
          
              if(tokens[0]=="EXIT")
              {   
                  exit(1);
              }
            
              //Parse create
              if(tokens.size()>=4 && tokens[0]=="CREATE" && tokens[1]=="TABLE" && tokens[3]=="(" && tokens[tokens.size()-1]==")")
              {  
                  char c=keyword_check(tokens[2]);
                  if(c=='y')
                  {  
                      getch();
                      exit(0);
                  }
                  
                  ch1=table_name(tokens[2]);
                  
                  if(ch1=='y')
                  {
                       cout<<"\n\n\nTABLE ALREADY EXISTS, DUPLICACY NOT ALLOWED\n\n\n"; 
                       getch();
                       exit(0);
                  }
                  else
                  {  
                       if(isalpha(*tokens[2].begin()))//Table name should begin with a character            {
                       {                                            
                           chk=parse_create(); 
                           if(chk==1)
                           {
                              implement_create();
                              cout<<"\n\n TABLE SUCCESSFULLY CREATED\n\n";
                              getch();
                           }
                           else if(chk==-1)
                           {
                               cout<<"\n\n INCORRECT QUERY.. \n\n\n";
                               getch();
                               exit(0);
                           }
                       } 
                       else
                       {
                           cout<<"\n\n TABLE NAME SHOULD BEGIN WITH A CHARACTER \n\n"; 
                           getch();    
                           exit(0);
                       }
                  }  
              }
        
              // Parse insert
              else if(tokens.size()>=4 && tokens[0]=="INSERT" && tokens[1]=="INTO" && tokens[3]=="VALUES")
              {
                  ch1=table_name(tokens[2]);
                  
                  if(ch1=='y')
                  {
                      int chk,chk2;
                      
                      chk=parse_insert();
                      
                      if(chk==1)
                      {
                          chk2=implement_insert();
                          
                          if(chk2==1)
                          {  
                             getch();
                          }
                          else if(chk2==-1)
                          {  
                               char lin[80];
                               cin.getline(lin, 80);
                               cout<<"\n\nSEMANTICALLY INCORRECT QUERY\n\n";
                               getch();
                               exit(0);
                          }
                      }
                      else
                      {  
                         cout<<"INCORRECT INSERTION\n\n"; 
                         getch();
                         exit(0);
                      }
                  }   
                  else
                  {
                      cout<<"\n\n TABLE DOES NOT EXIST \n\n";
                      getch();
                      exit(0);
                  } 
              }
                
              // Parse select
              else if(tokens[0]=="SELECT" && tokens.size()>3)
              {   
                   insert_select();
                  
                   if(kv[1].k_index!=-1)
                   {
                        chk=parse_select();
                        
                        vector<string>::iterator it;
                        
                        it = find(tokens.begin(),tokens.end() ,"*");
                        
                        if(it!=tokens.end())
                        {   
                           if(tokens[1]!="*" )
                           { 
                               cout<<"\n\nINVALID * SYMBOL SPECIFIED\n\n";
                               getch();
                               exit(0);
                           }
                           if(f_index!=2)
                           {
                               cout<<"\n\nWITH '*' OTHER ATTRIBUTES NOT ALLOWED \n\n\n";
                               getch();                          
                               exit(0);
                           }
                        }
                        if(chk==1)
                        {
                             int chk1;
                             
                             if(w_index-f_index==2 || w_index==-1)  // no where clause
                             { 
                                chk1=implement_select();
                                getch();
                             }
                             else if(w_index-f_index==4)
                             {       
                                 chk1=implement_select_join();
                             }
                             if(chk1==-1)
                             {
                                 cout<<"\n\n SEMANTICALLY INCORRECT QUERY\n\n";
                                 getch();
                                 exit(0);
                             }
                             getch();
                        }
                        else if(chk==-1)
                        {
                             cout<<"\n\n INCORRECT QUERY..\n\n";
                             exit(0);
                        }
                   }
                   else
                   {
                        cout<<"\n\n FROM MISSING IN SQL QUERY\n\n";
                        getch();
                        exit(0);
                   }
              }   
              else
              {   
                  cout<<"invalid query";
              }
              
              getch();
              return 0;  
     }
 
