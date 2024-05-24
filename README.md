# Getting Started

### Setup Key for JWT Login
+ Open bash shell (recommended bash shell)
+ Go to "**main/resources/Certification**" file
+ Run command "**openssl genrsa -out keypair.pem 2048**"
+ Run command "**openssl rsa -in keypair.pem -pubout -out public.pem**"
+ Run command "**openssl pkcs8 -topk8 -inform PEM -nocrypt -in keypair.pem -out private.pem**"
+ Afterward you could delete the keypair.pem

### Setup application.properties
+ spring.application.name=quiz
+ rsa.rsa-public-key=classpath:Certification/public.pem
+ rsa.rsa-private-key=classpath:Certification/private.pem
+ spring.data.mongodb.uri= > (your mongodb atlas uri)
+ spring.data.mongodb.database= (your database name)

> Please Copy and Paste the above list to your application.properties file
> #### Note:
> + please set up account on <https://www.mongodb.com>
> + copy your mongoDB uri and replace with \<your uri> 
> 
>       spring.data.mongodb.uri= <your uri>



