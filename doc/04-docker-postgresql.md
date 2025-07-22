# AWS RDS 연동

```
  datasource:
    driver-class-name: org.postgresql.Driver
    url: jdbc:postgresql://postgresql:5432/pincoin
    username: pincoin
    password: secure_pincoin_password_123
    hikari:
      connection-init-sql: "SET TIME ZONE 'Asia/Seoul'"
```