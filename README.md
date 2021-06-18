# Case Study LineQueue
System that is capable of maintaining a queue of text lines for access by clients.

## Architecture
Simple Client-Server architecture on 2 physical layers and 3 logical layers.

## 📦 Installation
The application can be deployed on Docker or in a Native enviroment
#### Docker
1. Install docker
```console
username@hostname:~$ apt install -Y docker
```
2. Use the build.sh script to create the needed Docker images
```console
username@hostname:~$ ./build.sh docker
```
3. Use the run.sh script to deploy the architecture
```console
username@hostname:~$ ./run.sh docker
```
#### Native
1. Install the needed package
```console
username@hostname:~$ apt install -Y openjdk-8-jdk python3 pip3
```
```console
username@hostname:~$ wget https://services.gradle.org/distributions/gradle-6.9.0-bin.zip
username@hostname:~$ sudo mkdir /opt/gradle
username@hostname:~$ sudo unzip -d /opt/gradle gradle-6.9.0-bin.zip
username@hostname:~$ export PATH=$PATH:/opt/gradle/gradle-6.9.0/bin
```
2. Use build.sh script to create the jar and install dependencies
```console
username@hostname:~$ ./build.sh
```
3. Use the run.sh script to start both server and client
```console
username@hostname:~$ ./run.sh
```

## Requirements
- [X]  Application should be follow the command protocol defined in the given PDF.
- [X]  The system should perform well as the number of requests per second increases.
- [X]  The server should listen for connections on TCP port 10042.
- [X]  The application server should be implemented in any JVM compatible language
- [X]  The server must support at least a single client at a time. The server may optionally support multiple simultaneous clients.

## Known Issues
1. Input strings are not checked
2. The system is not easy to scale horizontaly

## Consideration
**How does your system work?**
The system holds a single instance of a Queue that client can access by connecting to the service on port 10042. Client can be written in any language as long they respect the API. The systems received commands strings and then parse them to execute the right logic and update the state of the queue.
**How will your system perform as the number of requests per second increases?**
The system will perform as good as the number of thread the machine is able to handle. Relying on Kotlin singleton allow the system to be thread-safe, therefore guaranteeing a consistent state even when req/s increase.  
However, the way the server is build doesn't let the system to be horizontally scalable. Add new server instances is not enough to scale the service as the state is not shared between istances. 
To solve horizontal scaling two path are available:
- Develop any type of consistency (eventual, casual, or other) to make more instances share a common state.
- Use OpenSource source projects, i.g. Kafka, to maintain a consistent, always available state and then deploy few instances that handle the application logic and keep track of the seeking index published as well on a Kafka topic.
**How will your system perform with various queue sizes?** 
As we are using the Java Class ConcurrentLinkedQueue this totally depends on that implementation. My understanding is that retrieving n record would cost *n* * O(1) and add n new elements will cost again *n* * O(1)
**What documentation, websites, papers, etc did you consult in doing this assignment?**
Java 8 Docs, Kotlin Docs
Lot of stackoverflow and tutorial for understanding how to document the code, write Kotlin tests and write bash scripts. 
**What third-party libraries or other tools does the system use?**
I did not want to overdue the assingment. I kept it simple by using basic bricks.
**How long did you spend on this exercise?**
I would say 8 hours +- 1


## Dependencies and Technologies
This project leverages a different set of technologies
- Docker: Fast deployment and portability. It makes easier to development
- Python: Fast development, easy threading. Perfect language for simulating jobs
- Kotlin: JVM Language, fast and very robust.

![Alt text](https://tqrhsn.gallerycdn.vsassets.io/extensions/tqrhsn/vscode-docker-registry-explorer/0.1.3/1533881464222/Microsoft.VisualStudio.Services.Icons.Default "Docker") | ![Alt text](https://icons.iconarchive.com/icons/cornmanthe3rd/plex/128/Other-python-icon.png "Python") | ![Alt text](https://fwcd.gallerycdn.vsassets.io/extensions/fwcd/kotlin/0.2.18/1593283481846/Microsoft.VisualStudio.Services.Icons.Default "Kotlin")