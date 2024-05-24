# Getting Started

### Setup Key for JWT Login
+ Open bash shell (recommended bash shell)
+ From Quiz folder run

      cd src/main/resources/Certification
+ Run command for generating keypair.pem

      openssl genrsa -out keypair.pem 2048
+ Run command for generation public.pem

      openssl rsa -in keypair.pem -pubout -out public.pem
+ Run command

      openssl pkcs8 -topk8 -inform PEM -nocrypt -in keypair.pem -out private.pem
+ Afterward you could delete the keypair.pem

### Setup application.properties
You can rename application.yml to application.properties, if you prefer .properties file
+ application.properties  


        spring.application.name=quiz  
        rsa.rsa-public-key=classpath:Certification/public.pem  
        rsa.rsa-private-key=classpath:Certification/private.pem  
        spring.data.mongodb.uri= <your mongodb atlas uri>  
        spring.data.mongodb.database= <your database name>  

+ application.yml


        spring:  
            application:  
                name: quiz  
            data:  
                mongodb:  
                    uri: <your mongodb atlas uri>  
                    database: <your database name>  
        rsa:  
            rsa-public-key: classpath:Certification/public.pem  
            rsa-private-key: classpath:Certification/private.pem  


> #### Note:
> + If you're using application.properties, you can delete application.yml
> + please set up account on <https://www.mongodb.com>
> + copy your mongoDB uri and replace with \<your mongodb atlas uri>



