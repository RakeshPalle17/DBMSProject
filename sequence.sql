set serveroutput on
-- drop the sequences if it is created with the same name.
drop sequence seqpur#;
drop sequence seqlog#;

-- The sequence to automatically generate unique values for pur# 
create sequence seqpur#
increment by 1
start with 10001;

-- The sequence to automatically generate unique values for log#. 
create sequence seqlog#
increment by 1
start with 1001;
