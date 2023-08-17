# Client Server Framework

### Introduction
Provides a comprehensive programming and configuration model for Client-Server applications

### Technologies Used
Java Networking, TCP/IP, Java Multithreading, Java Secure Socket Extension (SSL), GSON

### List of features
* Establish a **two-way communication channel** between server and client
* Handling **multiple clients** asynchronously on server
* Enables secure communications with plain or **SSL/TLS** protocol
* Providing communication standards
* **Request mapping**
* Encryption with algorithms: RSA, AES, SHA
* Session Management

### Use

* In Client side:
```java
//init Client
Client client = Client.init(ip, port, sslPort);
//then use Client methods to expand or inject
//for Client logics 
client.setService();
ClientService service = client.getService();
//request mapping for plain protocol
client.setController();
client.addController();
//request mapping for secure protocol
client.setSslController();
client.addSslController();
//to get connection
Worker worker = connect.connect();
//or get connection with Secure
Worker worker = connect.connectSSL();
//In case you want to verify and get Session first, use this(Recommend)
Worker worker = connect.getConnection();
//then use Worker to interact with the Server
```

* In Server side:
```java
//init Server
Server server = Server.init(port, sslPort);
//then use Server methods to expand or inject
//for Server logics 
server.setService();
ServerService service = server.getService();
SessionManager manager = service.getSessionManager();
//request mapping for plain protocol
server.setController();
server.addController();
//request mapping for secure protocol
server.setSslController();
server.addSslController();
//start the Server
server.launch();
//and shutdown
server.shutdown();
```

* Request Mapping:
  - The standard communication is structured as
    ```java
    Packet {
        Request request;
        StatusCode code;
        String message;
        Map<DataKey, ?> data;
    }
    ```
  - So first, declare what you need for both Client and Server
    ```java
    //EXAMPLE
    public enum MyRequest implements Request {
        SEND_MESSAGE
    }    
    public enum MyDataKey implements DataKey {
        MESSAGE_BODY
    }
    public enum MyStatusCode implements StatusCode {
    }
    ```
  - Then 
    ```java
    //EXAMPLE SEND REQUEST IN CLIENT SIDE
    Packet request = new Packet();
    request.setHeader(MyRequest.SEND_MESSAGE);
    request.putData(MyDataKey.MESSAGE_BODY, message);
    String msg = JsonParser.toJson(request);
    Client.getInstance().getWorker().send(msg);
    
    //EXAMPLE CATCH REQUEST IN SERVER SIDE
    Controller example = new Controller() {{
        map(MyRequest.SEND_MESSAGE, (worker, data) -> {
            Optional<Message> message = data.get(MyDataKey.MESSAGE_BODY, Message.class);
            if (!message.isPresent())
                return;
            MyServerService service = (MyServerService) Server.getinstance().getService();
            try {
                service.syncMessage(worker.getUid(), message.get());
            } catch (UnauthenticatedException e) {
                TransferHelper.call(worker).warnUnauthenticated();
            }
        });
    }};
    server.addController(example);
    ```

## Screenshots

Certification <br />
![Alt text](screenshots/1.PNG?raw=true)

Public and Private Key <br />
![Alt text](screenshots/2.PNG?raw=true)

Server communication with Secure and JSON <br />
![Alt text](screenshots/3.PNG?raw=true)

Client communication with Secure and JSON <br />
![Alt text](screenshots/4.PNG?raw=true)

Request Mapping Exmaple <br />
![Alt text](screenshots/5.PNG?raw=true)
