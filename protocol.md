**Client**

1.  Send "Hello name" message to the server where "name" is an identifier for the client.
2.  Wait for a response (100 or 200) from the Server
3.  If the client recieves a 100 message from the server as its first message, the client knows it was the first to connect and then
    *  It waits for a second response from the server
    *  When the client receives a second response from the server, it knows tha the second client has connected and the first client 
        sends a message to the second client and waits for a message from the second client.
4.  If the client receives a 200 message from the server as its first message, the client knows it was the second to connect and then
    *  It waits for a message from the first client.
5.  When either client receives a message from the other client, it sends one response message and waits for a reply
6.  When a client receives a "Goodbye" message, it closes the socket and exits.


**Server**

1.  Wait for "Hello name" message from first client.
    *  When message is received, record the name, IP address and port number of the first client
    *   Send 100 message to the first client
2.  Wait for "Hello name" message from second client.
    *  When message is received from second client, record the name, IP address and port nubmer of the second client
    b*  Send 200 message to both clients.
3.  Wait for message from a client.
    *  if message is not "Goodbye" send it to the other clients
    *  If message is "Goodbye" send it to both clients.
    *  Close the socket and exit.
    
