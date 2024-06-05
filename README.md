# GETTING STARTED WITH QUIZ

### Setup Key for JWT Login
+ Open bash shell (recommended bash shell for openssl)
+ Run command for generating keypair.pem

      openssl genrsa -out keypair.pem 2048
+ Run command for generation public.pem (public key)

      openssl rsa -in keypair.pem -pubout -out public.pem
+ Run command for generating private.pem (private key)

      openssl pkcs8 -topk8 -inform PEM -nocrypt -in keypair.pem -out private.pem
+ After Complete you can copy private and public key to .env file

### Setup Environment File

+ .env  

      MONGO_URI= <Mongo URI>
      DATABASE= <Database Name>
      PUBLIC_KEY= <Generated Public Key>
      PRIVATE_KEY= <Generated Private Key>



> #### Note:
> + please set up account on <https://www.mongodb.com>
> + copy your mongoDB uri and replace with \<your mongodb atlas uri>



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
>         +  {
>           +  "id": "39434592bc04a345345",
>           +  "name": "Testing",
>           +  "description": "Just Testing",
>           +  "visibility": "PUBLIC",
>           +  "createdAt": "2024-05-28T15:03:59.718+00:00",
>           +  "updatedAt": "2024-05-28T15:03:59.718+00:00"
>         +   },
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