INSERT INTO gateways (created_at, updated_at, name, fixed_commission, percentage_commission, daily_limit, processing_time, availability_start, availability_end, available_days, min_transaction_amount, max_transaction_amount, active) VALUES
                     (Now(),NOW(),'Gateway 1',2,1.5,50000,'Instant',NULL,NULL,'SAT,SUN,MON,TUE,WED,THU,FRI',10,5000,true),
                     (Now(),NOW(),'Gateway 2',5,0.8,200000,'24 hours','09:00','17:00','SUN,MON,TUE,WED,THU',100,NULL,true),
                     (Now(),NOW(),'Gateway 3',0,2.5,100000,'2 hours',NULL,NULL,'SAT,SUN,MON,TUE,WED,THU,FRI',50,10000,true);
INSERT INTO billers (created_at, updated_at, name, code, active) VALUES
                    (NOW(),NOW(),'WE Telecom','WE',true),
                    (NOW(),NOW(),'Vodafone Egypt','VOD',true),
                    (NOW(),NOW(),'Orange Egypt','ORG',true);