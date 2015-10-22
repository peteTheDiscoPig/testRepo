import java.io.*;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * This module contains the presentaton logic of an Echo Client.
 * @author M. L. Liu
 */
public class FTPClient {
   static final String endMessage = ".";
   static JFrame frame;
   public static void main(String[] args) {
	  String unserName;
	  boolean exit = false;
      InputStreamReader is = new InputStreamReader(System.in);
      BufferedReader br = new BufferedReader(is);
      try {
         System.out.println("Welcome to the FTP client.\n" +
                            "What is the name of the server host? blank = localhost");
         String hostName = br.readLine();
         if (hostName.length() == 0) // if user did not enter a name
            hostName = "localhost";  //   use the default host name
         System.out.println("What is the port number of the server host? blank = 7");
         String portNum = br.readLine();
         if (portNum.length() == 0)
            portNum = "7";          // default port number

         while(!exit){
        	 FTPClientHelper helper = new FTPClientHelper(hostName, portNum);
        	 System.out.println("Login or Exit");
        	 String inOrOut = br.readLine();
        	 if(inOrOut.equals("Login")|| inOrOut.equals("login")){
        		 
        		 System.out.println("Login: Enter username");
                 unserName = br.readLine();
            	 
                 String loginStatus = helper.login(unserName);
                 System.out.println(loginStatus.trim());
        		 if(!loginStatus.substring(0, 3).equals("401")){
            		 boolean done = false;
                     //while user does not select logout
            		 while (!done) {
                        System.out.println("What would you like to do?\nUpload (u), download file (d) or logout(l)");
                        String doWhat = br.readLine();
                        if(doWhat.length()==0){doWhat="x";}//prevent blank
                        //if logout return to login/ exit
                        if(doWhat.charAt(0)=='l'){
                        	String replyToLogoutRequest = helper.sendMessage("logout");
                        	System.out.println(replyToLogoutRequest.trim());
                        	done = true;
                        }
                        else if(doWhat.charAt(0)=='u'){
                        	String replyToUploadRequest = helper.sendMessage("upload");
                        	System.out.println(replyToUploadRequest.trim());
                        	frame = new JFrame();
                            frame.setVisible(true);
                            BringToFront();
                            File file = getFile();
                            
                            if(file != null){
                            	String name = file.getName();
                            	//send file name
                                String replyToNameSend = helper.sendMessage(name);
                                
                                System.out.println(replyToNameSend.trim());
                                String uploadResult = helper.upload(file);
                                System.out.println(uploadResult.trim());
                            }
                          //if file select canceled
                            else{
                            	String cancleUploadRequest = helper.sendMessage("cancle_upload");
                            	System.out.println(cancleUploadRequest.trim());
                            }
                            

                        }else if(doWhat.charAt(0)=='d'){
                        	String replyToDownloadRequest = helper.sendMessage("download");
                        	System.out.println(replyToDownloadRequest.trim());
                        	//if files in folder on server
                        	if(!replyToDownloadRequest.substring(0, 3).equals("404")){
                        		System.out.println("Select file name (from list above) to download (include extension). 'cancel' to cancel download");
                                String fileToDownload = br.readLine();
                                helper.sendMessage(fileToDownload);
                                if(!fileToDownload.equals("cancel")){
                                	System.out.println("downloading file");
                                	String fileDownloadstatus = helper.receiveFile(fileToDownload);
                                    System.out.println(fileDownloadstatus);
                                }
                                else{
                                	System.out.println("download canceled");
                                }
                                
                        	}
                        }
                      } // end while
        		 }	 
        	 }
        	 else if(inOrOut.equals("Exit")|| inOrOut.equals("exit")){
        		 exit = true;
        	 } 
         }//end while
         
      } // end try  
      catch (Exception ex) {
         ex.printStackTrace( );
      } // end catch
   } //end main
   
   //refrence http://stackoverflow.com/questions/7494478/jfilechooser-from-a-command-line-program-and-popping-up-underneath-all-windows
   public static File getFile() {
       JFileChooser fc = new JFileChooser();
       if(JFileChooser.APPROVE_OPTION == fc.showOpenDialog(null)){
           frame.setVisible(false);
           return fc.getSelectedFile();
       }else {
           System.out.println("Next time select a file.");
       }
       return null;
   }
   private static void BringToFront() {                  
       frame.setExtendedState(JFrame.ICONIFIED);
       frame.setExtendedState(JFrame.NORMAL);
	
   }
} // end class      
