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
 * @version July 2000
 */
public class EchoServer extends AbstractServer 
{
  //Class variables *************************************************
  
  /**
   * The default port to listen on.
   */
  final public static int DEFAULT_PORT = 5555;
  
  String loginId = "loginID";
  
  //Constructors ****************************************************
  
  /**
   * Constructs an instance of the echo server.
   *
   * @param port The port number to connect on.
   */
  public EchoServer(int port) 
  {
    super(port);
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
	  String msgStr = (String)msg;
	  
	  if (msgStr.startsWith("#login") && client.getInfo(loginId) != null) {
		  System.out.println("Attempted to login twice");
		  try {
			client.sendToClient("Attempted to login twice");
			client.close();
		} catch (IOException e) {
		}
		  return;
	  }
	  
	  if (msgStr.startsWith("#login")) {
		  handleLogin(msgStr, client);
		  System.out.println("Message received: #login " + client.getInfo(loginId) + " from null");
		  return;
	  }
    System.out.println("Message received: <" + msg + "> from <" + client.getInfo(loginId) + ">");
    this.sendToAllClients(msg);
  }
    
  
  private void handleLogin(String msg, ConnectionToClient client) {
	  String[] splitCommand = msg.split("\\s+");
	  client.setInfo(loginId, splitCommand[1]);
	
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
  
  

  /**
   * Hook method called each time a new client connection is
   * accepted. The default implementation does nothing.
   * @param client the connection connected to the client.
   */
  @Override
  protected void clientConnected(ConnectionToClient client) {
	  System.out.println("New Client Connected!");
  }
  
  /**
   * Hook method called each time a client disconnects.
   * The default implementation does nothing. The method
   * may be overridden by subclasses but should remains synchronized.
   *
   * @param client the connection with the client.
   */
  @Override
  synchronized protected void clientDisconnected(
    ConnectionToClient client) {
	  System.out.println("<" + client.getInfo(loginId) + "> has disconnected!");
  }


public void handleMessageFromServerUI(String message) {
	if (message.startsWith("#")) {
		handleCommand(message);
		return;
	} else {
		System.out.println("> " + message);
		message = "SERVER MESSAGE: " + message;
		this.sendToAllClients(message);
	}
	
}


private void handleCommand(String message) {
	// TODO Auto-generated method stub
	if (message.equals("#quit")) {
		try {
			this.close();
		} catch (IOException e) {
		}
	} else if (message.equals("#stop")) {
		this.stopListening();
	} else if (message.equals("#close")) {
		this.stopListening();
	      // Close the client sockets of the already connected clients
	      Thread[] clientThreadList = getClientConnections();
	      for (int i=0; i<clientThreadList.length; i++)
	      {
	         try
	         {
	           ((ConnectionToClient)clientThreadList[i]).close();
	         }
	         // Ignore all exceptions when closing clients.
	         catch(Exception ex) {}
	      }
	} else if (message.startsWith("#setport")) {
		if (!this.isListening()) {
			String[] splitCommand = message.split("\\s+");
			  if (splitCommand.length != 2) {
				  System.out.println("Didn't receieve correct number of arguments. Please enter #setport <port>");
				  return;
			  }
			  try {
			      this.setPort(Integer.parseInt(splitCommand[1]));; //Get port from command line
			    }
			    catch(Throwable t) {
			      System.out.println("Error trying to parse the port. Please enter only digits");
			      return;
			    }
			  System.out.println("Set port to " + this.getPort());
			  return;
		} else {
			System.out.println("Server must be stopped to change port");
		}
	} else if (message.equals("#start")) {
		if (!this.isListening()) {
			try {
				this.listen();
			} catch (IOException e) {
			}
		} else {
			System.out.println("Server is not stopped");
		}
	} else if (message.equals("#getport")) {
		System.out.println("Port is " + this.getPort());
	}
}
  
}
//End of EchoServer class
