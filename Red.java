/**
*	UDP Red Client Program
*	Connects to the UDP Chat Server
*	Receives a line of input from the keyboard and sends it to the server
*	Receives a response from the server and displays it.
*
*	@author: Elia Eiroa
* 
*/

import java.io.*;
import java.net.*;

class Red {
    public static void main(String args[]) throws Exception
    {

      BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
      DatagramSocket clientSocket = new DatagramSocket();

      InetAddress IPAddress = InetAddress.getByName("localhost");

      byte[] sendData = new byte[1024];


      String sentence ="HELLO red";
      sendData = sentence.getBytes();
	    DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, 9876);

      clientSocket.send(sendPacket);


      boolean connected = false;
      while(connected == false){
        byte[] receiveData = new byte[1024];
        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
        clientSocket.receive(receivePacket);
        String modifiedSentence = new String(receivePacket.getData());
        //WAITING FOR SERVER TO CONFIRM CONNECTION
        if (modifiedSentence.contains("100")){
          System.out.println("FROM SERVER: " + modifiedSentence);
        }
        //CONFIRM BOTH CLIENTS ARE CONNECTED
        if (modifiedSentence.contains("200")){
          System.out.println("FROM SERVER: " + modifiedSentence);
          connected = true;
        }
      }
      while(connected == true){
        byte[] receiveData = new byte[1024];
        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
        clientSocket.receive(receivePacket);
        String modifiedSentence = new String(receivePacket.getData());

        if(modifiedSentence.length() > 7 && modifiedSentence.contains("Goodbye")){
          System.out.println("FROM SERVER: "+modifiedSentence);
          connected = false;
        }
        //CHECK FOR MESSAGE BACK
        else if(modifiedSentence.length() > 4 && modifiedSentence.contains("300")){
          System.out.print("YOUR TURN:  ");
          sentence = inFromUser.readLine();
          sendData = sentence.getBytes();
          sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, 9876);
          //CREATE/ SEND PACKET TO SERVER
          clientSocket.send(sendPacket);
        }
        else{
          System.out.println(modifiedSentence);
        }
      }
      clientSocket.close();
      }
}
