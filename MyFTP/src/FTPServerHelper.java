import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.regex.Pattern;

public class FTPServerHelper {
	private String username;
	public String login(String username) {
		String message;
		this.username = username;
		if(isValid(username)){
			File theDir = new File("C:/FTP_Folder/"+username);
			// if the directory does not exist, create it
			if (!theDir.exists()) {
			    System.out.println("creating folder for: " + username);
			    boolean result = false;

			    try{
			        theDir.mkdirs();
			        result = true;
			    } 
			    catch(SecurityException se){
			        //handle it
			    }        
			    if(result) {    
			        System.out.println("DIR created");  
			    }
			    message = "201Welcome to the FTP server, "+username+". A folder has been created for you.";
			}
			else{
				message = "200Welcome back to the FTP server, " + username;
			}
		}
		else{
			message = "401Invalid username: minimum 5 characters, max 20 characters and letters only";
		}
		
		return message;
	}

	private boolean isValid(String username) {
		if(username.length()<5)
		{
			System.out.println("username too short");
			return false;
		}
		else if(username.length()>20)
		{
			System.out.println("username too long");
			return false;
		}
		else if(containsInvalidChars(username)){
			System.out.println("username contains invalid characters");
			return false;
		}
		else{
			return true;
		}
	}

	private boolean containsInvalidChars(String username) {
		boolean lettersOnly = Pattern.matches("[a-zA-Z]+", username);
		return !lettersOnly;
	}

	public String saveFile(byte[] fileUploadBytes, String fileName) {
		File outputFile = new File("C:/FTP_Folder/"+username+"/"+ fileName);

	    try ( 
	    		FileOutputStream outputStream = new FileOutputStream(outputFile); 
	    	) {

	        outputStream.write(fileUploadBytes);

	    } catch (Exception e) {
	        return "An error occured while uploading your file";
	    }
		return "file uploaded";
	}

	public String getFileNames(String nameOnFolder) {
		File folder = new File("C:/FTP_Folder/" + nameOnFolder.trim()+"/");
		File[] files = folder.listFiles();
		String message = "";
		if(files.length!=0){
			for(int i =0; i<files.length; i++){
				if(files[i].isFile()){//don't want directories
					message += files[i].getName()+"\n";
				}
			}
		}
		else{
			message = "404No files found";
		}
		return message;
	}

	public byte[] getFileToSend(String fileName) throws FileNotFoundException {
		File file = new File("C:/FTP_Folder/"+username+"/"+fileName);
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
		return bytes;
	}

}
