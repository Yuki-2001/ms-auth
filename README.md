# ms-auth
CHRYSALIS Application Authentication Microservice

Development Tools and Version -
  
      Java - openjdk 17.0.4
      STS  - sts-4.14.1
      LAMP - 8.0.20-0 
          (or) 
      MySQL version - Ver 15.1 Distrib 10.4.25-MariaDB

**Note : Before run this microservice must run the configuration server**

# Authentication Server Microservice setup.

## Local environment set up with localhost profile.

localhost profile allows you to run the authentication server in local environment and pull the configurations from using configuration server.

### Clone the code repository from git uring git url

https://github.com/emphaticsense/ms-auth.git

### Crete folder structure for external configurations.

```
  /opt/ms/configs
```

**Note : For Linux - Create folders in root, For Windows Create folders in LocalDisk:C**

When you are done, your local folder structure should look like the one below:

```
    /opt/ms/configs/common/
    /opt/ms/configs/db/
    /opt/ms/configs/startup/
 ```   

### Add your database credentials into the properties files from:

```
  /opt/ms/configs/db
```

### Create folder structure for log files

```
  /opt/ms/logs
```

## Create Run configuration.

## Local

```
Name : ms-auth
```

1. **Under Spring Boot Tab**

| Parameter   | Value                              |
| :--------   | :-------                           |
| `Project`   | `ms-auth`                    |
| `Main Type` | `com.user.authentication.ApplicationConfig` |
| `Profile`   | `localhost`                        |

2. **Under Environment Tab**

Add - New variable configuration

| Variable                    | Value                      |
| :--------                   | :-------                   |
| `spring_cloud_config_uri`   | `http://localhost:10080`   |

Click Apply and Run

**Note : Based on the Environment config server url and port will be changed `spring_cloud_config_uri`**

**Properties File Setup & Configuration**
  
   ### Linux 
    
   ## Folder name and path 
   
   ### Update application properties,db and email properties below metioned location
   
  ```
    email - /opt/ms/configs/common/
    eg: email-config-localhost.properties
  ``` 
  ```
    db - /opt/ms/configs/db/
    eg: config-application-db-config-localhost.properties
  ```
  ```
    application properties - /opt/ms/configs/startup/
    eg: ms-auth-config-localhost.properties(application_name-config-localhost.properties)
    
  ```  
  
  ## Swagger Url

http://localhost:8500/application/auth/api/v1/openapi/swagger-ui/index.html#/

## Get Oauth token using postman

1. Install POST MAN from following location https://www.getpostman.com/downloads/

2. Navigate to project directory and go to docs folder  and find json file

3. Load postman collection, ms-chrysalis-authentication.postman_collection.json, into POST MAN
