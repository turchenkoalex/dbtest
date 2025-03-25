# dbtest

How to use:

Run kubernetes deployment:

```
kubectl apply -f k8s/deployment.yaml
```

For specifying the database connection string, use the following environment variables: 
* PG_HOST: the host of the database (default: localhost)
* PG_PORT: the port of the database (default: 5432)
* PG_DATABASE: the name of the database (default: test)
* PG_USER: the user of the database (default: postgres)
* PG_PASSWORD: the password of the database (default: empty password)

## What app do:

### Update test

1. Create a table in the database if it does not exist `update_data`
2. Insert a row with the value `0` in the column `value` and the name `update_test` in the column `name` if record not exists
3. Invoke sql in the loop:
   ```sql
   begin;
   update update_data set value = value + 1 where name = 'update_test'`;
   commit;
   ```

### Insert test

1. Create a table in the database if it does not exist `insert_data`
2. Invoke sql in the loop in one transaction:
   ```sql
   begin;
   insert into insert_data (name, value) values ('insert_test_$NUM', '$insert_test_$NUM');
   delete from insert_data where name = 'insert_test_$NUM';
   commit;
   ```
