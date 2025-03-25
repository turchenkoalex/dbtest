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
