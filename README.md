Welcome!

This project presents a straightforward HTTP web server written in Java, designed to delve into and experiment with the concepts outlined in [RFC 9112](https://datatracker.ietf.org/doc/html/rfc9112) for the HTTP/1.1 protocol. The aim is not to introduce groundbreaking features or achieve full RFC compliance but rather let me get back into the Java-world and gain a practical understanding of HTTP/1.1 concepts.

# Scope:

Basic HTTP Server:

- The web server provides a fundamental HTTP service, capable of handling incoming requests.

Partial Implementation of RFC 9112:

- While not all functions outlined in RFC 9112 are implemented, the server incorporates key aspects to ensure a basic level of HTTP/1.1 compatibility.

Concept Exploration:

- The project serves as a playground to explore and experiment with the concepts introduced in the RFC, allowing me to gain practical insights into the workings of HTTP/1.1.

New Java Features:

- This project allows me to get back into the java world. It will gradually change, while i play around with different desgigns and concepts or will explore jdk21 features.

# Implementation Highlights:

HTTP Methods:

- The server supports common HTTP methods such as GET and HEAD, providing a foundation for basic resource retrieval.

Status Codes:

- A subset of HTTP status codes is implemented to handle various responses, including successful requests and common errors.

Headers:

- The server processes and generates HTTP headers, supporting key header fields as specified in RFC 9112.

Asynchronous connection handling:

- Basic asynchronous connection handling mechanisms are in place to manage client-server interactions.

Gzip Compression:

- Gzip compression is implemented to enhance data transfer efficiency by compressing response data before sending it to the client.

Chunked Transfer Encoding:

- The server supports chunked transfer encoding, allowing it to send data in chunks, providing flexibility for streaming large uncertain responses.

# Closing words

This simple HTTP web server serves as a practical exploration of the HTTP/1.1 concepts outlined in RFC 9112, utilizing the capabilities of Java. While the implementation is not exhaustive, it provides a foundation for further experimentation and learning in the realm of web server development.

It's important to note that this project is designed for me as a playground for experimenting with different Java concepts and features. The code, while functional, may not adhere to the strictest standards of cleanliness. Instead, it is intentionally left open for modification and experimentation, so i can explore various approaches and refine the code based on my own insights and requirements.

Feel free to use, copy, and modify this project as needed. Please give feedback or if you have any suggestions for improvement please don't hesitate to let me know.

# Backlog

- logging https://www.baeldung.com/logback
- thread pool/ virtual threads. advantages/disadvantage
- keep alive
- htmx
- identity provider/auth
- tls
