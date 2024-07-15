# USER API Spec

### 1. Login

POST http://localhost:8080/login

Body

```json
{
    "email": "charlie@example.com",
    "password": "12345678"
}
```


### 2. List User

GET http://127.0.0.1:8080/secured/user/list?page=1&size=10

Request Headers

Authorization =
```json lines
Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOjEsInJvbGUiOiJBRE1JTiIsImlhdCI6MTcyMDkzMjAzMDMyNiwiZXhwIjoxNzIwOTM1NjMwMzI2fQ.-XFb75wOdUcTEKVixl2YXqanxuLe0SX_wqEGnm07FrE
```

Query Params (page = 1, size = 10)


### 3. Get User

GET http://127.0.0.1:8080/secured/user/current

Request Headers

Authorization
```json lines
Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOjEsInJvbGUiOiJBRE1JTiIsImlhdCI6MTcyMDkzMjAzMDMyNiwiZXhwIjoxNzIwOTM1NjMwMzI2fQ.-XFb75wOdUcTEKVixl2YXqanxuLe0SX_wqEGnm07FrE
```

### 4. Update Profile

PUT http://127.0.0.1:8080/secured/user/update-profile

Request Headers

Authorization
```json lines
Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOjEsInJvbGUiOiJBRE1JTiIsImlhdCI6MTcyMDkzMjAzMDMyNiwiZXhwIjoxNzIwOTM1NjMwMzI2fQ.-XFb75wOdUcTEKVixl2YXqanxuLe0SX_wqEGnm07FrE
```

Body

```json
{
    "name" : "Update Nama Pembeli",
    "email" : "update@sksk.id"
}
```

### 5. Reset Password

PUT http://localhost:8080/secured/user/reset-password

Request Headers

Authorization
```json lines
Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOjEsInJvbGUiOiJBRE1JTiIsImlhdCI6MTcyMDkzMjAzMDMyNiwiZXhwIjoxNzIwOTM1NjMwMzI2fQ.-XFb75wOdUcTEKVixl2YXqanxuLe0SX_wqEGnm07FrE
```

Body 

```json
{
  "email":"test@sksk.id",
  "newPassword":"password"
}
```

### 6. Delete User (ONLY ADMIN)

DELETE http://localhost:8080/secured/user/delete-user

Request Headers

Authorization
```json lines
Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOjEsInJvbGUiOiJBRE1JTiIsImlhdCI6MTcyMDkzMjAzMDMyNiwiZXhwIjoxNzIwOTM1NjMwMzI2fQ.-XFb75wOdUcTEKVixl2YXqanxuLe0SX_wqEGnm07FrE
```

Body

```json
{
    "name": "Pembeli Baru"
}

```