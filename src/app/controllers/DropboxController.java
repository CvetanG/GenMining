package app.controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;

import com.dropbox.core.DbxApiException;
import com.dropbox.core.DbxAuthInfo;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.json.JsonReader;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.ListFolderErrorException;
import com.dropbox.core.v2.files.ListFolderResult;
import com.dropbox.core.v2.files.Metadata;
import com.dropbox.core.v2.files.UploadErrorException;
import com.dropbox.core.v2.files.WriteMode;

public class DropboxController {
	
	private static DropboxController instance;
    
	public DropboxController(){}
    
    public static DropboxController getInstance(){
        if(instance == null){
            instance = new DropboxController();
        }
        return instance;
    }
	
	public File getFile(DbxClientV2 dbxClient, String localPath, String dropboxPath) {
		File localFile = new File(localPath);
		try (
				InputStream inputStream = dbxClient.files().download(dropboxPath).getInputStream();
				OutputStream outputStream = new FileOutputStream(localFile)
						) {
		
			int read = 0;
			byte[] bytes = new byte[1024];
	
			while ((read = inputStream.read(bytes)) != -1) {
				outputStream.write(bytes, 0, read);
			}
		} catch (DbxException e) {
			e.printStackTrace();
		} catch (IOException e) {
		e.printStackTrace();
		} 
		System.out.println("Done getting file from Dropbox!");
		return localFile;
	}
	
	public void uploadFile(DbxClientV2 dbxClient, File localFile, String dropboxPath) {
        try (InputStream in = new FileInputStream(localFile)) {
            FileMetadata metadata = dbxClient.files().uploadBuilder(dropboxPath)
//                .withMode(WriteMode.ADD) // to change the mode to OVERWRITE
                .withMode(WriteMode.OVERWRITE) // to change the mode to OVERWRITE
                .withClientModified(new Date(localFile.lastModified()))
                .uploadAndFinish(in);

            System.out.println(metadata.toStringMultiline());
        } catch (UploadErrorException ex) {
            System.err.println("Error uploading to Dropbox: " + ex.getMessage());
            System.exit(1);
        } catch (DbxException ex) {
            System.err.println("Error uploading to Dropbox: " + ex.getMessage());
            System.exit(1);
        } catch (IOException ex) {
            System.err.println("Error reading from file \"" + localFile + "\": " + ex.getMessage());
            System.exit(1);
        }
        System.out.println("Done uploading file in Dropbox!");
	}
	
	// Get files and folder metadata from Dropbox root directory
	public void getRootDir(DbxClientV2 client) throws ListFolderErrorException, DbxException {
	    ListFolderResult result = client.files().listFolder("");
	    while (true) {
	        for (Metadata metadata : result.getEntries()) {
	            System.out.println(metadata.getPathLower());
	        }
	
	        if (!result.getHasMore()) {
	            break;
	        }
	
	        result = client.files().listFolderContinue(result.getCursor());
	    }
	}
	
	public DbxAuthInfo createAuth(String argAuthFileOutput) {
		DbxAuthInfo authInfo;
	    try {
	        authInfo = DbxAuthInfo.Reader.readFromFile(argAuthFileOutput);
	        return authInfo;
	    } catch (JsonReader.FileLoadException ex) {
	        System.err.println("Error loading <auth-file>: " + ex.getMessage());
	        System.exit(1);
	        return null;
	    }
	}
	
	// Create a DbxClientV2, which is what you use to make API calls.
	public DbxClientV2 createClient(DbxAuthInfo authInfo) throws DbxApiException, DbxException {
		DbxRequestConfig requestConfig = new DbxRequestConfig("examples-upload-file");
        DbxClientV2 client = new DbxClientV2(requestConfig, authInfo.getAccessToken(), authInfo.getHost());
        System.out.println("Linked account: " + client.users().getCurrentAccount());
        return client;
	}
	
	public static void main(String[] args) throws IOException, DbxException {
		String localPath = "temp.txt";
		
		String dropboxPath = "/Finance/" + localPath;
		
    	String argAuthFileOutput = "authFile.app";
    	
    	DropboxController myDropbox = new DropboxController();
        
        DbxAuthInfo authInfo = myDropbox.createAuth(argAuthFileOutput);
        
        DbxClientV2 client = myDropbox.createClient(authInfo);
        
//        getRootDir(client);
        File localFile = myDropbox.getFile(client, localPath, dropboxPath);
        myDropbox.uploadFile(client, localFile, dropboxPath);
        
    }
}