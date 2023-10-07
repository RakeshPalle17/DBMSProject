set serveroutput on

-- Trigger for 4th question
/**
* Trigger to add the tuple top logs table when ever there is an insertion 
* happened in employee table
*/

create or replace trigger employees_trigger
  after insert on employees
  for each row
  begin
      insert into logs (log#, user_name, operation, op_time, table_name, tuple_pkey)
      values (seqlog#.nextval, USER, 'insert', SYSDATE, 'employees', :new.eid);
  end;
/

-- Triggers for 5th Question

/**
* This trigger is fired after an insert on the purchases table.
* It updates the quantity on hand (qoh) of the product with the pid
* specified in the inserted row by subtracting the purchased quantity.
*/

create or replace trigger trig_qoh_update
after insert on purchases
for each row
begin
    update products
    set qoh = qoh - :new.quantity
    where pid = :new.pid;
	-- To show execution of trigger
	dbms_output.put_line('trigger trig_qoh_update completed');
end;
/

/**
* Trigger to check if the quantity on hand (QOH) of a product after a purchase is below the required threshold
* If the QOH is below the threshold, this trigger will update the QOH of the product to the threshold value plus 20
*/

create or replace trigger trig_check_qoh
after insert on purchases
for each row
declare
    v_qoh_threshold products.qoh_threshold%type;  -- variable to store the required threshold of QOH for a product
    v_qoh products.qoh%type; -- variable to store the current qoh of the product with the given ID
begin
    select qoh, qoh_threshold into v_qoh, v_qoh_threshold
    from products
    where pid = :new.pid;
    -- Check if the qoh is below the threshold
    if ((v_qoh - :new.quantity) < v_qoh_threshold) then
        dbms_output.put_line('The current qoh of the product is below the required threshold and new supply is required.');
		-- Update the qoh to the threshold value plus 20
        update products
        set qoh = v_qoh_threshold + 20
        where pid = :new.pid;
		-- Retrieve the updated qoh for the product
        select qoh into v_qoh
        from products
        where pid = :new.pid;
        dbms_output.put_line('The new value of qoh for the product is ' || v_qoh);
    end if;
	-- To show execution of trigger
	dbms_output.put_line('trigger trig_check_qoh completed');
end;
/

/**
* Trigger to update the customer table after each purchase
* This trigger updates the customer table with the details of the purchase made by the customer.
* It increments the number of visits made by the customer by 1 and updates the last_visit_date
* to the date of the latest purchase. The trigger is fired after each row is inserted into the
* purchases table.
*/

create or replace trigger trig_customer_update
after insert on purchases
for each row
begin
    update customers
    set visits_made = visits_made + 1,
        last_visit_date = case
                            when last_visit_date < :new.pur_time then :new.pur_time
                            else last_visit_date
                          end
    where cid = :new.cid;
	-- To show execution of trigger
	dbms_output.put_line('trigger trig_customer_update completed');
end;
/


-- 6th Question Triggers

/**
* Trigger trig_log_last_visit_date_customers_update:
* This trigger fires after an update on the customers table and
* logs the operation if the last visit date of a customer has been updated.
*/

create or replace trigger trig_log_last_visit_date_customers_update
after update on customers
for each row
begin
    dbms_output.enable();
    if :new.last_visit_date <> :old.last_visit_date then
        insert into logs(log#, user_name, operation, op_time, table_name, tuple_pkey)
      	values(seqlog#.nextval, USER, 'update', sysdate, 'customers', :new.cid);
        -- To show execution of trigger
    	dbms_output.put_line('trigger trig_log_last_visit_date_customers_update completed');
	end if;
end;
/

/**
* Trigger to log updates on visits_made column in customers table
* This trigger logs updates on the visits_made column of the customers table by inserting a new row
* into the logs table with the updated customer ID and timestamp of the operation.
*/    

create or replace trigger trig_log_visits_made_customers_update
after update on customers
for each row
begin
    dbms_output.enable();
    if :new.visits_made <> :old.visits_made then
        insert into logs(log#, user_name, operation, op_time, table_name, tuple_pkey)
      	values(seqlog#.nextval, USER, 'update', sysdate, 'customers', :new.cid);
		-- To show execution of trigger
    	dbms_output.put_line('trigger trig_log_visits_made_customers_update completed');
	end if;
end;
/

/**
* Trigger that logs insertions into the 'purchases' table.
* It creates a new entry in the 'logs' table, recording the user, operation, 
* and timestamp of the insertion, as well as the primary key of the new tuple.
*/    

create or replace trigger trig_log_purchases_insert
after insert on purchases
for each row
begin
    dbms_output.enable();
    insert into logs(log#, user_name, operation, op_time, table_name, tuple_pkey)
    values(seqlog#.nextval, USER, 'insert', sysdate, 'purchases', :new.pur#);
	-- To show execution of trigger
	dbms_output.put_line('trigger trig_log_purchases_insert completed');
end;
/

/**
* This trigger logs an entry in the logs table after a successful update on the qoh column of the products table.
* It records the current user, operation type, operation time, table name, and tuple pkey.
*/

create or replace trigger trig_log_products_update
after update on products
for each row
begin
    dbms_output.enable();
    if :new.qoh <> :old.qoh then
        insert into logs(log#, user_name, operation, op_time, table_name, tuple_pkey)
      	values(seqlog#.nextval, USER, 'update', sysdate, 'products', :new.pid);
		-- To show execution of trigger
    	dbms_output.put_line('trigger trig_log_products_update completed');
	end if;
end;
/
