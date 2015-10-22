import java.net.*;
import java.io.*;

/**
 * This class is a module which provides the application logic
 * for an Echo client using connectionless datagram socket.
 * @author M. L. Liu
 */
public class FTPClientHelper {
   private MyClientDatagramSocket mySocket;
   private InetAddress serverHost;
   private int serverPort;

   FTPClientHelper(String hostName, String portNum) 
      throws SocketException, UnknownHostException { 
  	   this.serverHost = InetAddress.getByName(hostName);
  		this.serverPort = Integer.parseInt(portNum);
      // instantiates a datagram socket for both sending
      // and receiving data
  		this.mySocket = new MyClientDatagramSocket(); 
   	
  		File theDir = new File("C:/FTP_Downloads");
  		if (!theDir.exists()) {
		    System.out.println("Creating downloads folder on your local machine.\nLocation C:/FTP_Downloads");
		    
		    try{
		        theDir.mkdirs();
		    } 
		    catch(SecurityException se){
		        //handle it
		    }        
		}
   } 
   
   public void done( ) throws SocketException {
      mySocket.close( );
   }  //end done

	public String login(String unserName)throws SocketException, IOException {
		mySocket.login( serverHost, serverPort, unserName);
		return mySocket.receiveMessage();
	}
	
	public String sendMessage(String message)throws SocketException, IOException {
		mySocket.login( serverHost, serverPort, message);
		return mySocket.receiveMessage();
	}
	//reference http://www.programcreek.com/2009/02/java-convert-a-file-to-byte-array-then-convert-byte-array-to-a-file/
	public String upload(File file) throws SocketException, IOException{
		FileInputStream fis = new FileInputStream(file);
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		
        byte[] buf = new byte[1024];
        try {
            for (int readNum; (readNum = fis.read(buf)) != -1;) {
                bos.write(buf, 0, readNum);
            }
        } catch (IOException ex) {
            
        }
        byte[] bytes = bos.toByteArray();
		mySocket.upload(serverHost, serverPort, bytes);
		return mySocket.receiveMessage();
	}

	public String receiveFile(String fileToDownload) throws SocketException, IOException {
		String message = "";
		mySocket.sendMessage(serverHost, serverPort, fileToDownload);
		byte[] fileDownloadedBytes = mySocket.receiveFileBytes();
		String saveFileMessage = saveFile(fileDownloadedBytes, fileToDownload);
		
		return saveFileMessage;
	}

	private String saveFile(byte[] fileDownloadedBytes, String fileToDownload) {
		File outputFile = new File("C:/FTP_Downloads/"+ fileToDownload);

	    try ( 
	    		FileOutputStream outputStream = new FileOutputStream(outputFile); 
	    	) {

	        outputStream.write(fileDownloadedBytes);

	    } catch (Exception e) {
	        return "An error occured while downloading your file";
	    }
		return "file downloaded";
	}

} //end class
