
### Cara Menjalankan Program

* Jalankan database migration tools
```bash
./mvnw clean flyway:migrate -Dflyway.configFiles=tools/db/migrations.conf
```

* Jalankan aplikasi
```bash
./mvnw spring-boot:run 
```

Postman Docs -> https://documenter.getpostman.com/view/14437092/2sA3kPpjMo
