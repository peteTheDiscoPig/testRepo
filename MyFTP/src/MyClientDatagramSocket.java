import java.net.*;
import java.io.*;

/**
 * A subclass of DatagramSocket which contains 
 * methods for sending and receiving messages
 * @author M. L. Liu
 */
public class MyClientDatagramSocket extends DatagramSocket {
static final int MAX_LEN = 64 * 1000;  
   MyClientDatagramSocket( ) throws SocketException{
     super( );
   }
   MyClientDatagramSocket(int portNo) throws SocketException{
     super(portNo);
   }
   public void sendMessage(InetAddress receiverHost,
                           int receiverPort,
                           String message)
   		          throws IOException {	
         byte[ ] sendBuffer = message.getBytes( );                                     
         DatagramPacket datagram = 
            new DatagramPacket(sendBuffer, sendBuffer.length, 
                                  receiverHost, receiverPort);
         this.send(datagram);
   } // end sendMessage

   public String receiveMessage()
		throws IOException {		
         byte[ ] receiveBuffer = new byte[MAX_LEN];
         DatagramPacket datagram =
            new DatagramPacket(receiveBuffer, MAX_LEN);
         this.receive(datagram);
         String message = new String(receiveBuffer);
         return message;
   } //end receiveMessage
   
   
	public void login(InetAddress serverHost, int serverPort, String unserName) throws IOException{
		byte[ ] sendBuffer = unserName.getBytes( );                                     
        DatagramPacket datagram = 
           new DatagramPacket(sendBuffer, sendBuffer.length, 
        		   serverHost, serverPort);
        this.send(datagram);
		
	}
	public void upload(InetAddress serverHost, int serverPort, byte[] uploadMessage) throws IOException {
		DatagramPacket datagram = new DatagramPacket(uploadMessage, uploadMessage.length, 
				serverHost, serverPort);
	    this.send(datagram);
	}
	public byte[] receiveFileBytes() throws IOException {
		byte[ ] receiveBuffer = new byte[MAX_LEN];
	    DatagramPacket datagram =
	       new DatagramPacket(receiveBuffer, MAX_LEN);
	    this.receive(datagram);
		return receiveBuffer;
	}
} //end class
