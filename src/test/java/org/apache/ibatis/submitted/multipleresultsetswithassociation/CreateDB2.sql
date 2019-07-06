

    CREATE PROCEDURE GetOrderDetailsAndHeaders()
        READS SQL DATA
        DYNAMIC RESULT SETS 1
        BEGIN ATOMIC

        DECLARE result1 CURSOR FOR
        SELECT * FROM order_detail
        FOR READ ONLY ;
 
        DECLARE result2 CURSOR FOR
        SELECT * FROM order_header
        FOR READ ONLY ;
  
        OPEN result1 ;
        OPEN result2 ;
    END;
