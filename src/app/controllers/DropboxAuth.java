package app.controllers;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import com.dropbox.core.DbxAppInfo;
import com.dropbox.core.DbxAuthFinish;
import com.dropbox.core.DbxAuthInfo;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.DbxWebAuth;
import com.dropbox.core.json.JsonReader;

public class DropboxAuth {
	
	// Read app info file (contains app key and app secret)
	public static DbxAppInfo readApp (String argAppInfoFile) {
		DbxAppInfo appInfo;
		try {
			appInfo = DbxAppInfo.Reader.readFromFile(argAppInfoFile);
			return appInfo;
		} catch (JsonReader.FileLoadException ex) {
			System.err.println("Error reading <app-info-file>: " + ex.getMessage());
			System.exit(1);
			return null;
		}
	}

	// Run through Dropbox API authorization process
	public static DbxAuthFinish authorization (DbxAppInfo appInfo) throws IOException {
		DbxRequestConfig requestConfig = new DbxRequestConfig("examples-authorize");
		DbxWebAuth webAuth = new DbxWebAuth(requestConfig, appInfo);
		DbxWebAuth.Request webAuthRequest = DbxWebAuth.newRequestBuilder().withNoRedirect().build();
	
		String authorizeUrl = webAuth.authorize(webAuthRequest);
		System.out.println("1. Go to " + authorizeUrl);
		System.out.println("2. Click \"Allow\" (you might have to log in first).");
		System.out.println("3. Copy the authorization code.");
		System.out.print("Enter the authorization code here: ");
	
		String code = new BufferedReader(new InputStreamReader(System.in)).readLine();
		if (code == null) {
			System.exit(1);
			return null;
		}
		code = code.trim();
	
		DbxAuthFinish authFinish;
		try {
			authFinish = webAuth.finishFromCode(code);
			System.out.println("Authorization complete.");
			System.out.println("- User ID: " + authFinish.getUserId());
			System.out.println("- Access Token: " + authFinish.getAccessToken());
			return authFinish;
		} catch (DbxException ex) {
			System.err.println("Error in DbxWebAuth.authorize: " + ex.getMessage());
			System.exit(1);
			return null;
		}
	
	}

	// Save auth information to output file.
	public static void saveAuth (DbxAuthFinish authFinish, DbxAppInfo appInfo, String argAuthFileOutput) throws IOException {
		DbxAuthInfo authInfo = new DbxAuthInfo(authFinish.getAccessToken(), appInfo.getHost());
		
		File output = new File(argAuthFileOutput);
		try {
			DbxAuthInfo.Writer.writeToFile(authInfo, output);
			System.out.println("Saved authorization information to \"" + output.getCanonicalPath() + "\".");
		} catch (IOException ex) {
			System.err.println("Error saving to <auth-file-out>: " + ex.getMessage());
			System.err.println("Dumping to stderr instead:");
			DbxAuthInfo.Writer.writeToStream(authInfo, System.err);
			System.exit(1);
			return;
		}
	}
	
	public static void main(String[] args) throws IOException {
		String argAppInfoFile = "test.app";
		String argAuthFileOutput = "authFile.app";
		
		DbxAppInfo appInfo = readApp(argAppInfoFile);
		DbxAuthFinish authFinish = authorization (appInfo);
		saveAuth (authFinish, appInfo, argAuthFileOutput);
	}
}