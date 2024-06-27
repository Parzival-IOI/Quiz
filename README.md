# GETTING STARTED WITH QUIZ APPLICATION

## Setup Key for JWT Login
+ Open bash shell (recommended bash shell for openssl)
+ Run command for generating keypair.pem

      openssl genrsa -out keypair.pem 2048
+ Run command for generation public.pem (public key)

      openssl rsa -in keypair.pem -pubout -out public.pem
+ Run command for generating private.pem (private key)

      openssl pkcs8 -topk8 -inform PEM -nocrypt -in keypair.pem -out private.pem
+ After Complete you can copy private and public key to .env file

## Setup Environment File

#### Due to difficulty running spring boot application on docker, I decided to run the application on IDE and use Docker for MongoDB

#### But the issue arise when running "docker-compose --build", where Mongo URI need to change from "localhost" to service name "mongo"

#### So I decided to break the .env file into two separate files
+ **.env** for running the application on IDE and MongoDB on Docker
+ **.env.local** for running "docker-compose --build" (Multi Services Container)

#### Mongo URI for .env = "mongodb://<(username)>:<(password)>@localhost:27017/"
#### Mongo URI for .env.local = "mongodb://<(username)>:<(password)>@mongo:27017/"

+ .env  

      MONGO_URI= <Mongo URI>
      DATABASE= <Database Name>
      PUBLIC_KEY= <Generated Public Key>
      PRIVATE_KEY= <Generated Private Key>
      EMAIL= <Email>
      EMAIL_PASSKEY= <APP Password for the Email>


+ .env.local

      MONGO_URI= <Mongo URI>
      DATABASE= <Database Name>
      PUBLIC_KEY= <Generated Public Key>
      PRIVATE_KEY= <Generated Private Key>
      EMAIL= <Email>
      EMAIL_PASSKEY= <APP Password for the Email>




> #### Note:
> + Normal MongoDB URI Template : mongodb://<(username)>:<(password)>@localhost:27017/
> + For .env.local MongoDB URI Template : mongodb://<(username)>:<(password)>@mongo:27017/



# API GUIDE

> ### /auth
> + Authentication
> + Required
>   + Username
>   + password
> + Return: JWT Token

> ### /register
> + Register Account
> + Required
>   + username
>   + password
>   + email
>   + role **(constant)**
>     + TEACHER
>     + STUDENT
> + Response: 200 status code

> ### /api/role
> + Get Role User
> + Required
>   + JWT Token Header
> + Response: **(constant)**
>   + ADMIN
>   + TEACHER
>   + STUDENT

> ### /api/quiz/myQuiz
> + Get User Created Quiz
> + Required
>   + JWT Token Header
> + Response:
>   + [
>     + {
>       + "id": "39434592bc04a345345",
>       + "name": "Testing",
>       + "description": "Just Testing",
>       + "visibility": "PUBLIC",
>       + "createdAt": "2024-05-22T09:26:02.773+00:00",
>       + "updatedAt": "2024-05-22T09:26:02.773+00:00"
>     + },
>   + ]
> ### /api/quiz/findAll
> + Get All Quiz With Search, Pagination, OrderBy
> + Required
>   + JWT Token Header
>   + search url query
>   + orderBy url query **(constant)**
>     + NAME
>     + DATE
>   + order url query **(constant)**
>     + ASC
>     + DESC
>   + page url query **(start from page 0)**
>   + size uel query **(constant)**
>     + TEN
>     + FIFTEEN
>     + TWENTY
> + Response:
>   + {
>     + "data": [
>       +  {
>         +  "id": "39434592bc04a345345",
>         +  "name": "Testing",
>         +  "description": "Just Testing",
>         +  "visibility": "PUBLIC",
>         +  "createdAt": "2024-05-28T15:03:59.718+00:00",
>         +  "updatedAt": "2024-05-28T15:03:59.718+00:00"
>       +   },
>     + ],
>     + "columns": 10
>   + }
> ### /api/quiz/find/{id} **(ADMIN Role Only)**
> + Get Individual Quiz
> + Required
>   + JWT Token Header **(ADMIN)**
>   + id url path
> + Response
>   + {
>     + "id": "39434592bc04a345345",
>     + "name": "testing2",
>     + "description": "just for fun",
>     + "visibility": "PUBLIC",
>     + "questions": [
>       + {
>         + "id": "39434592bc04a345345",
>         + "question": "Are you ok ?",
>         + "type": "multiple Choice",
>         + "answers": [
>           + {
>             + "id": "39434592bc04a345345",
>             + "answer": "ok",
>             + "createdAt": "2024-05-22T09:26:03.192+00:00",
>             + "updatedAt": "2024-05-22T09:26:03.192+00:00",
>             + "correct": false
>           + },
>         + ],
>         + "createdAt": "2024-05-22T09:26:03.120+00:00",
>         + "updatedAt": "2024-05-22T09:26:03.120+00:00"},
>       + ],
>     + "createdAt": "2024-05-22T09:26:02.773+00:00",
>     + "updatedAt": "2024-05-22T09:26:02.773+00:00"
>   + }