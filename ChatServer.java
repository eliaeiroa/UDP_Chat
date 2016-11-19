/**
*	
* SERVER PROGRAM FOR UDPCHAT
* connects two clients (Red and Blue)
* and facilitates messaging in between the two users
*
*	@author: Megan Boyd
* @Partner: Elia Eiroa
*/

import java.io.*;
import java.net.*;

class ChatServer {
  //CLIENT DATA:
  private String firstUserName = "";
  private InetAddress firstUserAddress = null;
  private int firstUserPort = 0;
  private String secondUserName = "";
  private InetAddress secondUserAddress = null;
  private int secondUserPort = 0;

  //CONNECTS CLIENTS:
  public void ConnectUsers(DatagramSocket serverSocket) throws IOException
  {
     boolean initiateChat = false;
     boolean gotOneUser = false;
     String message = "";

    //GETTING INFO:
    while(initiateChat == false)
    {
      byte[] receiveData = new byte[1024];
      DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
      serverSocket.receive(receivePacket);
      String sentence = new String(receivePacket.getData());

      if(gotOneUser == false)
      {
        firstUserAddress = receivePacket.getAddress();
        firstUserPort = receivePacket.getPort();
        System.out.println("Extracted address " + firstUserAddress.toString() + " and port " + firstUserPort + " of new user.");

        if(sentence.length() > 5 && sentence.substring(0,5).equals("HELLO"))
        {
          firstUserName = sentence.substring(5, sentence.length() - 1);
          System.out.println("now adding user " + firstUserName);
          message = "100 CONNECTED TO SERVER: ";
          gotOneUser = true;
        }
        else
        {
          message = "404 Invalid input";
          System.out.println("Packet with invalid message: " + sentence);
        }
        SendUserOnePacket(message, serverSocket);
      }
      else
      {
        secondUserAddress = receivePacket.getAddress();
        secondUserPort = receivePacket.getPort();
        System.out.println("Extracted address " + secondUserAddress.toString() + " and port " + secondUserPort + " of new user.");

        if(sentence.length() > 5 && sentence.substring(0,5).equals("HELLO"))
        {
          secondUserName = sentence.substring(5, sentence.length() - 1);
          System.out.println("Now adding user " + secondUserName);
          message = "200 BOTH CLIENTS CONNECTED TO SERVER: ";
          SendUserOnePacket(message, serverSocket);
          SendUserTwoPacket(message, serverSocket);
          initiateChat = true;
        }
        else
        {
          message = "404 Invalid input";
          System.out.println("Packet with invalid message: " + sentence);
          SendUserTwoPacket(message, serverSocket);
      }
    }
    }
  }

  //ACTUAL CONVERSATION STARTS
  private void StartChat(DatagramSocket serverSocket) throws IOException
  {
    System.out.println("CHAT IS STARTING: ");
    boolean isDone = false;
    boolean firstUserSentMessage = false;
    String clientAccessMessage = "300";

    while(isDone == false)
    {
      //USERS SWITCH BACK AND FORTH CHATTING UNTIL ONE SENDS "Goodbye"
      if(firstUserSentMessage == false)
      {
         SendUserOnePacket(clientAccessMessage, serverSocket);
         firstUserSentMessage = true;
      }
      else
      {
        SendUserTwoPacket(clientAccessMessage, serverSocket);
        firstUserSentMessage = false;
      }

      //SENDS PACKET FROM ONE CLIENT TO THE OTHER
      byte[] receiveData = new byte[1024];
      DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
      serverSocket.receive(receivePacket);
      String packetMessage = new String(receivePacket.getData());

      if(receivePacket.getPort() == firstUserPort)
      {
        //IF MESSAGE = Goodbye, QUIT
        if(packetMessage.length() > 7 && packetMessage.contains("Goodbye"))
        {
          System.out.println(firstUserName + " ended the chat! \n  GOODBYE EVERYONE!");
          String sentMessage = "Goodbye";
          SendUserOnePacket(sentMessage, serverSocket);
          SendUserTwoPacket(sentMessage, serverSocket);
          isDone = true;
        }
        else
        {
          System.out.println(firstUserName + " to " + secondUserName +": " + packetMessage );
          SendUserTwoPacket(packetMessage, serverSocket);
        }
      }

      //REPEAT - OTHER WAY AROUND
      else if(receivePacket.getPort() == secondUserPort)
      {
        //IF MESSAGE = Goodbye, QUIT
        if(packetMessage.length() > 7 && packetMessage.contains("Goodbye"))
        {
          System.out.println(secondUserName + " ended the chat! \n  GOODBYE EVERYONE!");
          String sentMessage = "Goodbye";
          SendUserOnePacket(sentMessage, serverSocket);
          SendUserTwoPacket(sentMessage, serverSocket);
          isDone = true;
        }
        else
        {
          System.out.println(secondUserName + " to " + firstUserName +": " + packetMessage );
          SendUserOnePacket(packetMessage, serverSocket);
        }
      }
      else
      {
        System.out.println("ANOTHER USER TRIED TO ACCESS CHAT:\nAddress:" + receivePacket.getAddress().toString() +
          "\nPort " + receivePacket.getPort()+"\n");
      }
    }
  }

  // CREATES/ DELIVERS A PACKET TO FIRST USER
  private void SendUserOnePacket(String messageToSend, DatagramSocket serverSocket) throws IOException
  {
    byte[] sendData = new byte[1024];
    sendData = messageToSend.getBytes();
    DatagramPacket toClientOne = new DatagramPacket(sendData, sendData.length, firstUserAddress, firstUserPort);
    serverSocket.send(toClientOne);
  }

  // CREATES/ DELIVERS A PACKET TO SECOND USER
  private void SendUserTwoPacket(String messageToSend, DatagramSocket serverSocket) throws IOException
  {
    byte[] sendData = new byte[1024];
    sendData = messageToSend.getBytes();
    DatagramPacket toClientTwo = new DatagramPacket(sendData, sendData.length, secondUserAddress, secondUserPort);
    serverSocket.send(toClientTwo);
  }

  //THROWS EXCEPTIONS
  public static void main(String args[]) throws Exception
  {
    ChatServer newServer = new ChatServer();
    DatagramSocket serverSocket = null;

    try
  	{
  		serverSocket = new DatagramSocket(9876);
      newServer.ConnectUsers(serverSocket);
      newServer.StartChat(serverSocket);
      serverSocket.close();
  	}
    catch(IOException e)
    {
      	System.out.println("ERROR WHILE SENDING PACKET, GOODBYE!");
    		System.exit(0);
    }
  	catch(Exception e)
  	{
  		System.out.println("ERROR WHILE OPENING PORT, GOODBYE!");
  		System.exit(0);
  	}
  }
}
