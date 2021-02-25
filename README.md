# Spring-Helper
[![Build Status](https://travis-ci.com/github/gcorporationcare/spring-helper.svg?branch=master)](https://travis-ci.com/github/gcorporationcare/spring-helper)
[![Coverage Status](https://coveralls.io/github/gcorporationcare/spring-helper/badge.svg?branch=master)](https://coveralls.io/github/gcorporationcare/spring-helper?branch=master)


Executing database lookup by using keywords in API urls. Also choose which fields will be returned in JSON responses.

Refer [wiki](https://github.com/gcorporationcare/spring-helper/wiki) for more Details.


| Stable Release Version | JDK Version compatibility | Release Date |
| ------------- | ------------- | ------------|
| 1.0.2  | 1.8+ | 02/25/2021 |

## License

Spring-Helper is licensed by **G-Corporation**.

## News
* Version **1.0.2** released on 02/25/2021.
* Version **1.0.1** released on 02/24/2021.
* Version **1.0.0** released on 02/24/2021.

### 1.0.2

* Adding custom fields converters for storing/reading from database.

## Maven Repository

Spring-Helper is deployed at sonatypes open source maven repository. You may use the following repository configuration (if you are interested in snapshots)

```xml
<repositories>
     <repository>
         <id>spring-helper-snapshots</id>
         <snapshots>
             <enabled>true</enabled>
         </snapshots>
         <url>https://oss.sonatype.org/content/groups/public/</url>
     </repository>
</repositories>
```
This repositories releases will be synched to maven central on a regular basis. Snapshots remain at sonatype.

Alternatively you can  pull Spring-Helper from the central maven repository, just add these to your pom.xml file:
```xml
<dependency>
	<groupId>com.github.gcorporationcare</groupId>
	<artifactId>spring-helper</artifactId>
	<version>1.0.2</version>
</dependency>
```

## BUILDING from the sources

As it is maven project, buidling is just a matter of executing the following in your console:

	mvn package

This will produce the spring-helper-VERSION.jar file under the target directory.

## Support
If you need help using Spring-Helper feel free to drop an email or create an issue in github.com (preferred)

## Contributions
To help Spring-Helper development you are encouraged to  
* Provide suggestion/feedback/Issue
* pull requests for new features
* Star :star2: the project
