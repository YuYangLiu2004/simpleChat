package edu.seg2105.edu.server.backend;
// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 


import java.io.IOException;

import ocsf.server.*;

/**
 * This class overrides some of the methods in the abstract 
 * superclass in order to give more functionality to the server.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;re
 * @author Fran&ccedil;ois B&eacute;langer
 * @author Paul Holden
 */
public class EchoServer extends AbstractServer 
{
  //Class variables *************************************************
  
  /**
   * The default port to listen on.
   */
  final public static int DEFAULT_PORT = 5555;
  
  //Constructors ****************************************************
  
  /**
   * Constructs an instance of the echo server.
   *
   * @param port The port number to connect on.
   */
  public EchoServer(int port) 
  {
    super(port);
    try {
		listen();
	} catch (IOException e) {
		System.out.println("Couldn't listen to client");
	}
  }
  
  //Instance methods ************************************************
  
  /**
   * This method handles any messages received from the client.
   *
   * @param msg The message received from the client.
   * @param client The connection from which the message originated.
   */
  public void handleMessageFromClient
    (Object msg, ConnectionToClient client)
  {
	  System.out.println("Message received: " + msg + " from " + client.getInfo("login"));
	    String message = (String) msg;
	    if (message.startsWith("#login")) {
	        if (client.getInfo("login") == null) {
	        String[] temp = message.split(" ");
	          String login = temp[1];
	        client.setInfo("login", login);
	        System.out.println(client.getInfo("login") + " has logged in");
	        } else {
	            try {
	                System.out.println("Login is already set");
	                client.sendToClient(("Your login is in the server already"));
	                client.close();
	            } catch (IOException e) {
	                System.out.println("Couldn't send message to client");
	            }
	        }
	    } else {
	        this.sendToAllClients(client.getInfo("login") + "> " + msg);
	    }
}
  
    
  /**
   * This method overrides the one in the superclass.  Called
   * when the server starts listening for connections.
   */
  protected void serverStarted()
  {
    System.out.println
      ("Server listening for connections on port " + getPort());
  }
  
  /**
   * This method overrides the one in the superclass.  Called
   * when the server stops listening for connections.
   */
  protected void serverStopped()
  {
    System.out.println
      ("Server has stopped listening for connections.");
  }
  
  /**
   * Hook method called each time a new client connection is
   * accepted. The default implementation does nothing.
   * @param client the connection connected to the client.
   */
  @Override
  protected void clientConnected(ConnectionToClient client) { 
	  //Sending a nice message when a client has connected
	  System.out.println("Yippie, client connected: " + client.toString());
  }

  
  /**
   * Hook method called each time an exception is thrown in a
   * ConnectionToClient thread.
   * The method may be overridden by subclasses but should remains
   * synchronized.
   *
   * @param client the client that raised the exception.
   * @param Throwable the exception thrown.
   */
  @Override
  synchronized protected void clientDisconnected(
    ConnectionToClient client) {
	  //Sending a nice message when a client has disconnected 
	  System.out.println("Nooooo, client disconnected: " + client.toString());
  }
  
  
  //Class methods ***************************************************
  
  /**
   * This method is responsible for the creation of 
   * the server instance (there is no UI in this phase).
   *
   * @param args[0] The port number to listen on.  Defaults to 5555 
   *          if no argument is entered.
   */
  public static void main(String[] args) 
  {
    int port = 0; //Port to listen on

    try
    {
      port = Integer.parseInt(args[0]); //Get port from command line
    }
    catch(Throwable t)
    {
      port = DEFAULT_PORT; //Set port to 5555
    }
	
    EchoServer sv = new EchoServer(port);
    
    try 
    {
      sv.listen(); //Start listening for connections
    } 
    catch (Exception ex) 
    {
      System.out.println("ERROR - Could not listen for clients!");
    }
  }


  public void handleMessageFromServerUI(String message) {
	if (message.equals("#quit")) {
		try {
			close();
			System.out.println("Server is quit");
			System.exit(0);
		}catch (IOException e) {
			System.out.println("Server cannot be closed");
		}
	} 
	else if (message.equals("#stop")){
		stopListening();
		System.out.println("Server has stopped listening");
	} 
	else if (message.equals("#close")){
		try {
			close();
			System.out.println("Server is closed");
		}catch (IOException e) {
			System.out.println("Server cannot be closed");
		}
	} 
	else if (message.equals("#setport")){
		if (!isListening() && getNumberOfClients() < 1) {
			String [] temp = message.split(" ");
			int clientPort = Integer.parseInt(temp[1]);
			setPort(clientPort);
			System.out.println("Port set to " + clientPort);
		}else
			System.out.println("Server must be closed");
	} 
	else if (message.equals("#start")){
		if (!isListening()) {
			try {
				listen();
			} catch (IOException e) {
				System.out.println("Cannot listen");
			} 
		}else {
			System.out.println("Server is already started");
		}
	} 
	else if (message.equals("#getport")){
			System.out.println("The port in the server is " + getPort());
	}else {
		System.out.println("SERVER MSG> " + message);
	    this.sendToAllClients("SERVER MSG> " + message);
	}
  }
}

//End of EchoServer class
