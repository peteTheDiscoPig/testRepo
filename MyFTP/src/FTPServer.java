import java.io.*;

/**
 * This module contains the application logic of an echo server
 * which uses a connectionless datagram socket for interprocess 
 * communication.
 * A command-line argument is required to specify the server port.
 * @author M. L. Liu
 */

public class FTPServer {
   public static void main(String[] args) {
      int serverPort = 7;    // default port
      if (args.length == 1 )
         serverPort = Integer.parseInt(args[0]);       
      try {
         // instantiates a datagram socket for both sending
         // and receiving data
   	   MyServerDatagramSocket mySocket = new MyServerDatagramSocket(serverPort); 
         System.out.println("FTP server ready.");  
         while (true) {  // forever loop
        	System.out.println("Awaiting login"); 
        	DatagramMessage loginRequest = mySocket.receiveLogin();
        	String username = loginRequest.getMessage();
            System.out.println("Login received from "+ username.trim());
            FTPServerHelper serverHelper = new FTPServerHelper();
            String returnString = serverHelper.login(username.trim());
            mySocket.sendMessage(loginRequest.getAddress( ), loginRequest.getPort( ), returnString);
            if(!returnString.substring(0, 3).equals("401")){
            	System.out.println("Loged in. Waiting for next command");
            	String request;
            	do{
            		System.out.println("User choosing: Logout, Upload or Download");
	            	DatagramMessage nextRequest = mySocket.receiveMessageAndSender();
	            	request = nextRequest.getMessage().trim();
	            	System.out.println("User requests to "+ request);
	            	//if logout
	            	if(request.substring(0, 1).equals("l")){
	            		System.out.println("Logging out user: "+ username.trim());
	            		String logoutMessage = "200You have been logged out";
	            		mySocket.sendMessage(loginRequest.getAddress( ), loginRequest.getPort( ), logoutMessage);
	            	}//if upload
	            	else if(request.substring(0, 1).equals("u")){
	            		System.out.println("Request for upload received. Awaiting file from "+ username.trim());
	            		String replyToUploadRequest = "200Request for upload received. Awaiting file";
	            		mySocket.sendMessage(loginRequest.getAddress( ), loginRequest.getPort( ), replyToUploadRequest);
	            		//listen for file name
	            		String fileName = mySocket.receiveMessage();
	            		if(!fileName.trim().equals("cancle_upload")){
	            			mySocket.sendMessage(loginRequest.getAddress( ), loginRequest.getPort( ), "file name received");
		            		
	            			System.out.println("file received");
		            		byte[] fileUploadBytes = mySocket.receiveFile();
		            		System.out.println("saving file");
		            		String returnUploadMessage = serverHelper.saveFile(fileUploadBytes, fileName.trim());
		            		mySocket.sendMessage(loginRequest.getAddress( ), loginRequest.getPort( ), returnUploadMessage);
	            		}
	            		else{
	            			System.out.println("Upload cancled");
	            			mySocket.sendMessage(loginRequest.getAddress( ), loginRequest.getPort( ), "Upload cancled");
	            		}	
	            	}//if download
	            	else if(request.substring(0, 1).equals("d")){
	            		System.out.println("Request for download received.\nSending file names.");
	            		String replyToDownloadRequest = "200Request for download received. Awaiting name of file to download\n\n";
//	            		mySocket.sendMessage(loginRequest.getAddress( ), loginRequest.getPort( ), replyToDownloadRequest);
	            		String listOfFileNames = serverHelper.getFileNames(username);
//	            		System.out.println(listOfFileNames);
	            		mySocket.sendMessage(loginRequest.getAddress( ), loginRequest.getPort( ), listOfFileNames);
	            		//if files in folder
	            		if(!listOfFileNames.substring(0, 3).equals("404")){
	            			System.out.println("Awaiting name of file to send");
	            			String fileName = mySocket.receiveMessage().trim();
	            			System.out.println(fileName);
	            			if(!fileName.equals("cancel")){
	            				mySocket.sendMessage(loginRequest.getAddress( ), loginRequest.getPort( ), "Ready to download");
	            				System.out.println("Sending file");
	            				byte[] fileBytes = serverHelper.getFileToSend(fileName);
		            			mySocket.sendFile(loginRequest.getAddress( ), loginRequest.getPort( ), fileBytes);
	            			}
	            			else{
	            				mySocket.sendMessage(loginRequest.getAddress( ), loginRequest.getPort( ), "canceling download");
	            			}
	            			
	            		}
	            		
	            	}
            	}while(!request.substring(0, 1).equals("l"));
            }
            else{
            	System.out.println("Invalid login. Waiting for next login");
            }
            
//            String message = request.getMessage( );
//            System.out.println("message received: "+ message);
//            // Now send the echo to the requestor
//            mySocket.sendMessage(request.getAddress( ),
//               request.getPort( ), message);
		   } //end while
       } // end try
	    catch (Exception ex) {
          ex.printStackTrace( );
	    } // end catch
   } //end main

} // end class      
