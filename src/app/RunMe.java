package app;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.dropbox.core.DbxApiException;
import com.dropbox.core.DbxAuthInfo;
import com.dropbox.core.DbxException;
import com.dropbox.core.v2.DbxClientV2;

import app.controllers.DropboxController;
import app.controllers.ExcelController;
import app.controllers.WebSitesParser;
import app.entities.GMRowEntry;
import app.entities.Utils;

public class RunMe {

	public static void main(String[] args) throws IOException, DbxApiException, DbxException {
		
		System.out.println("Start Program Genesis-Mining");
		long startTime = System.currentTimeMillis();
		
		String localPath = "GenesisMining.xlsx";
		
		String dropboxPath = "/Finance/Genesis_Mining/" + localPath;
		
    	String argAuthFileOutput = "authFile.app";
    	
    	DropboxController myDropbox = new DropboxController();
        
        DbxAuthInfo authInfo = myDropbox.createAuth(argAuthFileOutput);
        
        DbxClientV2 client = myDropbox.createClient(authInfo);
        
        File localFile = myDropbox.getFile(client, localPath, dropboxPath);
        
		FileInputStream fsIP = new FileInputStream(localFile);
				
		//Access the workbook                  
		Workbook wb = new XSSFWorkbook(fsIP);
		
		WebSitesParser myParser = new WebSitesParser();
		
		myParser.getMoneroInfo();
		
		System.out.println("Done Parsing Websites!!!");
		
//		int zeroRow = myEntries.size();
		
		ExcelController myPOI = new ExcelController();
		
		myPOI.writeInExcel(wb, myParser.getRowEntry());
		
		System.out.println("Done Inserting Rows in Excel File!!!");
		
		//Close the InputStream  
		fsIP.close();
		
		//Open FileOutputStream to write updates
		FileOutputStream output_file =new FileOutputStream(localFile);  
		
		//write changes
		wb.write(output_file);
		
		//close the stream
		output_file.close();
		
		myDropbox.uploadFile(client, localFile, dropboxPath);
        
        long endTime   = System.currentTimeMillis();
		System.err.println(Utils.duration(startTime, endTime));
		System.out.println("All Done!!!");

	}

}
