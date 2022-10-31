// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 

package client;

import ocsf.client.*;
import common.*;
import java.io.*;

/**
 * This class overrides some of the methods defined in the abstract
 * superclass in order to give more functionality to the client.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;
 * @author Fran&ccedil;ois B&eacute;langer
 * @version July 2000
 */
public class ChatClient extends AbstractClient
{
  //Instance variables **********************************************
  
  /**
   * The interface type variable.  It allows the implementation of 
   * the display method in the client.
   */
  ChatIF clientUI; 
  int port;
  String clientId;

  
  //Constructors ****************************************************

/**
   * Constructs an instance of the chat client.
   *
   * @param host The server to connect to.
   * @param port The port number to connect on.
   * @param clientUI The interface type variable.
   */
  
  public ChatClient(String clientId, String host, int port, ChatIF clientUI) 
    throws IOException 
  {
    super(host, port); //Call the superclass constructor
    this.clientUI = clientUI;
    this.port = port;
    this.clientId = clientId;
    openConnection();
  }

  
  //Instance methods ************************************************
    
  /**
   * This method handles all data that comes in from the server.
   *
   * @param msg The message from the server.
   */
  public void handleMessageFromServer(Object msg) 
  {
    clientUI.display(msg.toString());
  }

  /**
   * This method handles all data coming from the UI            
   *
   * @param message The message from the UI.    
   */
  public void handleMessageFromClientUI(String message)
  {
    try
    {
    	if (message.startsWith("#login") && message.length() > 6) {
    		sendToServer(message);
    	} else if (message.startsWith("#")) {
    		handleCommand(message);
    	} else {
    		sendToServer(message);
    	}
    }
    catch(IOException e)
    {
      clientUI.display
        ("Could not send message to server.  Terminating client.");
      quit();
    }
  }
  
  private void handleCommand(String command) {
	  if (command.equals("#quit")) {
		  quit();
	  } else if (command.equals("#logoff")) {
		  try
		    {
		      closeConnection();
		    }
		    catch(IOException e) {}
	  } else if (command.equals("#login")) {
		  if (!isConnected()) {
			  try {
				openConnection();
			} catch (IOException e) {
			}
		  } else {
			  clientUI.display("already logged in");
		  }
	  } else if (command.startsWith("#sethost")) {
		  String[] splitCommand = command.split("\\s+");
		  if (splitCommand.length != 2) {
			  clientUI.display("Didn't receieve correct number of arguments. Please enter #sethost <host>");
			  return;
		  }
		  try {
			  this.setHost(splitCommand[1]);
		  } catch (Error e) {
			  clientUI.display("Error parsing the host");
			  return;
		  }
		  clientUI.display("Set host to " + this.getHost());
		  return;
	  } else if (command.startsWith("#setport")) {
		  String[] splitCommand = command.split("\\s+");
		  if (splitCommand.length != 2) {
			  clientUI.display("Didn't receieve correct number of arguments. Please enter #setport <port>");
			  return;
		  }
		  try {
		      port = Integer.parseInt(splitCommand[1]); //Get port from command line
		    }
		    catch(Throwable t) {
		      clientUI.display("Error trying to parse the port. Please enter only digits");
		      return;
		    }
		  this.setPort(port);
		  clientUI.display("Set port to " + port);
		  return;
	  } else if (command.equals("#gethost")) {
		  clientUI.display("The host is " + this.getHost());
		  return;
	  } else if (command.equals("#getport")) {
		  clientUI.display("The port is " + Integer.toString(port));
		  return;
	  }
  }
  
  /**
   * This method terminates the client.
   */
  public void quit()
  {
    try
    {
      closeConnection();
    }
    catch(IOException e) {}
    System.exit(0);
  }
  
  

/**
 * Hook method called each time an exception is thrown by the client's
 * thread that is waiting for messages from the server. The method may be
 * overridden by subclasses.
 * 
 * @param exception
 *            the exception raised.
 */
@Override
protected void connectionException(Exception exception) {
	clientUI.display("The server has shut down");
	quit();
}


/**
 * Hook method called after the connection has been closed. The default
 * implementation does nothing. The method may be overriden by subclasses to
 * perform special processing such as cleaning up and terminating, or
 * attempting to reconnect.
 */
@Override
protected void connectionClosed() {
	clientUI.display("Connection closed");
}


public String getClientId() {
	return clientId;
}

/**
 * Hook method called after a connection has been established. The default
 * implementation does nothing. It may be overridden by subclasses to do
 * anything they wish.
 */
protected void connectionEstablished() {
	try {
		sendToServer("#login " + getClientId());
		clientUI.display(this.clientId + " has logged on");
	} catch (IOException e) {
	}
}

  
}



//End of ChatClient class
