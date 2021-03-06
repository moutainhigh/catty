[![Build Status](https://www.travis-ci.com/zhengrenjie/catty.svg?branch=master)](https://www.travis-ci.com/zhengrenjie/catty)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/pink.catty/catty-all/badge.svg)](https://maven-badges.herokuapp.com/maven-central/pink.catty/catty-all)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

# Catty
Whole new RPC framework!

# Features
* High performance.
* Async & reactor oriented.
* Micro-kernel & Easy to customize
* ProtoBuf supported.

# Project Status
Catty has released a few versions, which means you could use Catty in your own project!

But as the version is senior than 1.0.0, which means catty is not stable and has not adequately 
tested yet, I temporarily do not recommend you to use it in any large distribution system. But I do 
recommend you to use it in a smaller system or point-to-point system, in which cases Catty would 
be more easier to use and control.

# Usage
See example package or test package.

**Maven:**
```xml
<dependency>
    <groupId>pink.catty</groupId>
    <artifactId>catty-all</artifactId>
    <version>0.2.0</version>
</dependency>
```

**build:**
```bash
mvn clean install -DskipTests -Dmaven.javadoc.skip=true -Dgpg.skip
```

### Sync:
#### Server:
```java
ServerConfig serverConfig = ServerConfig.builder()
    .port(20550)
    .build();

Exporter exporter = new Exporter(serverConfig);
exporter.registerService(IService.class, new IServiceImpl());
exporter.export();

```
#### Client:
```java
ClientConfig clientConfig = ClientConfig.builder()
    .addAddress("127.0.0.1:20550")
    .build();

Reference<IService> reference = new Reference<>();
reference.setClientConfig(clientConfig);
reference.setInterfaceClass(IService.class);

IService service = reference.refer();
System.out.println(service.say0());
System.out.println(service.say1("catty"));

```
### Async:
#### Server:
```java
ServerConfig serverConfig = ServerConfig.builder()
    .port(20550)
    .build();

Exporter exporter = new Exporter(serverConfig);
exporter.registerService(IService.class, new IServiceImpl());
exporter.export();
```
#### Client:
```java
ClientConfig clientConfig = ClientConfig.builder()
    .addAddress("127.0.0.1:20550")
    .build();

Reference<IService> reference = new Reference<>();
reference.setClientConfig(clientConfig);
reference.setInterfaceClass(IService.class);

IService service = reference.refer();
CompletableFuture<String> future = service.asyncSay("catty");
future.whenComplete((value, t) -> System.out.println(value));
```

# *Welcome to join me!*
There are lots of things need todo:
* doc
* benchmark
* test
* more useful extensions
* log & annotation
* code review & refactor
