package app;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.dropbox.core.DbxAuthInfo;
import com.dropbox.core.DbxException;
import com.dropbox.core.v2.DbxClientV2;

import app.controllers.DropboxController;
import app.controllers.ExcelController;
import app.controllers.WebSitesParser;
import app.entities.Utils;
import app.kraken.Kraken;

public class RunMe {
	
	private static final String LOCAL_PATH = "GenesisMining.xlsx";
	private static final String DROPBOX_PATH = "/Finance/Genesis_Mining/" + LOCAL_PATH;
	private static final String ARG_AUTH_FILE_OUTPUT = "authFile.app";

	public static void main(String[] args) throws IOException, DbxException {
		
		System.out.println("Start Program Genesis-Mining");
		long startTime = System.currentTimeMillis();
		
    	
    	DropboxController myDropbox = DropboxController.getInstance();
        
        DbxAuthInfo authInfo = myDropbox.createAuth(ARG_AUTH_FILE_OUTPUT);
        
        DbxClientV2 client = myDropbox.createClient(authInfo);
        
        File localFile = myDropbox.getFile(client, LOCAL_PATH, DROPBOX_PATH);
        
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
		
		myDropbox.uploadFile(client, localFile, DROPBOX_PATH);
		
		List<String> pairs = new ArrayList<>();
		pairs.add(PairsConstant.XMR_USD);
		pairs.add(PairsConstant.XRP_USD);

		for (String p : pairs) {
			Kraken xmr20 = new Kraken(p, 20);
			xmr20.init();
			Kraken xmr55 = new Kraken(p, 55);
			xmr55.init();
		}
		
        long endTime = System.currentTimeMillis();
		System.err.println(Utils.duration(startTime, endTime));
		System.out.println("All Done!!!");

	}

}
