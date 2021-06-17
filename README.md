# Case Study LineQueue

System that is capable of maintaining a queue of text lines for access by clients.

## Architecture
A picture is better than hundreds of words
![Alt text](./docs/schema.png "Architecture")

## 📦 Installation
1. Build the Docker images. One for Kotlin server deployment and the other for Python client
```console
username@hostname:~$ docker build -t queue:<tag> -f ./serverDeploy .
```
```console
username@hostname:~$ docker build -t python_client:<tag> -f ./pythonDockerFile .
```
2. Run docker-compose to setup the architecture
```console
username@hostname:~/Docker$ docker-compose up -d
```
3. BUG: Restart the the restful server container after a dozen of seconds
4. Query the server via any preferred HTTP client.

## Requirements
- [X]  Application should be capable of handling duplicate values
- [X]  Avoid using an additional external database to store the data
- [X]  The application should be able to read the tracking data from a kafka topic
- [X]  The application server should be implemented in any JVM compatible language
- [X]  To aggregate from second statistics to minute statistics an average should be used
- [ ]  The application should be production ready and able to scale to high workloads, a high number of concurrent reads

## Known Issues
1. If the Kafka Topic is not yet set up the server crashes
2. Very hard to do unit test, therefore no testing is done.
3. Everything about time is processing time
4. 

## Scalability Consideration

## Dependencies and Technologies
This project leverages a different set of technologies
- Docker: Fast deployment and portability. It makes easier to development
- Python: Fast development, easy threading. Perfect language for simulating jobs
- Kotlin: JVM Language, fast and very robust.

![Alt text](https://tqrhsn.gallerycdn.vsassets.io/extensions/tqrhsn/vscode-docker-registry-explorer/0.1.3/1533881464222/Microsoft.VisualStudio.Services.Icons.Default "Docker") | ![Alt text](https://icons.iconarchive.com/icons/cornmanthe3rd/plex/128/Other-python-icon.png "Python") | ![Alt text](https://fwcd.gallerycdn.vsassets.io/extensions/fwcd/kotlin/0.2.18/1593283481846/Microsoft.VisualStudio.Services.Icons.Default "Kotlin")