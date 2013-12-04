package com.rackspacecloud.client.cloudfiles.FilesClientTest;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.rackspacecloud.client.cloudfiles.FilesClient;
import com.rackspacecloud.client.cloudfiles.FilesObject;
import com.rackspacecloud.client.cloudfiles.expections.FilesContainerExistsException;
import com.rackspacecloud.client.cloudfiles.expections.UnauthorizeException;


import junit.framework.TestCase;

public class FilesClientTestCase extends TestCase {
	
	int CONNECTION_TIMEOUT = 5000;
	private String getAUTHURL(){
		//String AUTH_URL = "https://10.30.230.191:8080/auth/v1.0";
		//String AUTH_URL = "https://10.30.236.96:8080/auth/v1.0";
		
		
		String AUTH_URL = "http://10.30.239.228:5000/v2.0/tokens";
		//String AUTH_URL = "http://10.30.230.164:5000/v2.0/tokens";
		
		//String AUTH_URL = "http://192.168.124.85:5000/v2.0/tokens";
		//String AUTH_URL = "http://10.128.145.11:5000/v2.0/tokens"; //TISSAT
		//String AUTH_URL = "http://192.168.6.163:5000/v2.0/tokens"; //TISSAT
		
		return AUTH_URL;
	}
	
	private String getUSER1(){
		//String USER1 = "tester1:user"; //swauth
		//String USER1 = "tusers:user1";	//keystone
		String USER1 = "tester20:tester20";
		
		return USER1;
	}
	
	private String getUSER2(){
		//String USER2 = "tester2:user"; //swauth
		String USER2 = "tusers2:user2";	//keystone		
		
		return USER2;
	}
	
	private boolean login(FilesClient client) throws IOException, UnauthorizeException{
		//return client.login();
		return client.loginKeystone();
	}
	
    	
	public void testLoginTester() {
		String AUTH_URL = getAUTHURL();
	        	    	  
	    FilesClient client = new FilesClient(getUSER1(), "testpass");
	    client.setAuthenticationURL(AUTH_URL);
        client.setConnectionTimeOut(CONNECTION_TIMEOUT);

        List<FilesObject> files;
		try {
			login(client);			
			files = client.listObjectsStartingWith("myfiles1", "/chunk", "", 20, "");
			System.out.println(files);
			System.out.println(client.getStorageURL());
		} catch (Exception e){
			e.printStackTrace();
		}
        
		try {
			assertTrue(login(client));
		} catch (Exception e) {
			fail(e.getMessage());
		} 
	}
	
	public void test1userCreateContainer(){
		String AUTH_URL = getAUTHURL();
	        
	    FilesClient client = new FilesClient(getUSER1(), "testpass");
        client.setAuthenticationURL(AUTH_URL);
        client.setConnectionTimeOut(CONNECTION_TIMEOUT);

		try {
			login(client);					
			client.createContainer("myfiles1");			
			assertTrue(true);
		} catch(FilesContainerExistsException e){
			assertTrue(true);
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
	
	public void test2userCreateContainer(){
		String AUTH_URL = getAUTHURL();
	        
	    FilesClient client = new FilesClient(getUSER2(), "testpass");
        client.setAuthenticationURL(AUTH_URL);
        client.setConnectionTimeOut(CONNECTION_TIMEOUT);

		try {
			login(client);					
			client.createContainer("myfiles11");			
			assertTrue(true);
		} catch(FilesContainerExistsException e){
			assertTrue(true);
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}	
	
	public void testGetWorkspaces(){
		
		String AUTH_URL = getAUTHURL();
        
	    FilesClient client = new FilesClient(getUSER1(), "testpass");
        client.setAuthenticationURL(AUTH_URL);
        client.setConnectionTimeOut(CONNECTION_TIMEOUT);

		try {
			login(client);					
			JsonArray array = client.getWorkspaces();
			System.out.println(array);
			JsonArray array2 = client.getChanges("AUTH_94c1ec3d4be24a8a85e0b2c8c8fd91ab/");
			System.out.println(array2);
			assertTrue(true);
		} catch(FilesContainerExistsException e){
			assertTrue(true);
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
	
	
	public void testUploadFile(){
		String AUTH_URL = getAUTHURL();
	        
	    FilesClient client = new FilesClient(getUSER1(), "testpass");
        client.setAuthenticationURL(AUTH_URL);
        client.setConnectionTimeOut(CONNECTION_TIMEOUT);

		try {
			login(client);
					
	        File file = new File("/home/gguerrero/java0.log");			
			client.storeObjectAs("myfiles1", file, "application/x-Stacksync", "/files/java0.log");
			
			assertTrue(true);
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
	
	
	private boolean isEqual(InputStream i1, InputStream i2) throws IOException {
	    byte[] buf1 = new byte[64 *1024];
	    byte[] buf2 = new byte[64 *1024];
	    try {
	        DataInputStream d2 = new DataInputStream(i2);
	        int len;
	        while ((len = i1.read(buf1)) > 0) {
	            d2.readFully(buf2,0,len);
	            for(int i=0;i<len;i++)
	              if(buf1[i] != buf2[i]) {
	            	  d2.close();
	            	  return false;
	              }
	        }
	        return d2.read() < 0; // is the end of the second file also.
	    } catch(EOFException ioe) {
	        return false;
	    } finally {
	        i1.close();
	        i2.close();
	    }
	}
	
	
	public void testDownloadFile(){
		String AUTH_URL = getAUTHURL();	    
	        
	    FilesClient client = new FilesClient(getUSER1(), "testpass");
        client.setAuthenticationURL(AUTH_URL);
        client.setConnectionTimeOut(CONNECTION_TIMEOUT);

		try {
			login(client);
				
	        File file = new File("/home/gguerrero/java0.log");
			InputStream in1 = new FileInputStream(file);
			InputStream in2 = client.getObjectAsStream("myfiles1", "/files/java0.log");
							
			assertTrue(isEqual(in1, in2));
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
	
	public void testDeleteFile(){
		String AUTH_URL = getAUTHURL();	    
	        
	    FilesClient client = new FilesClient(getUSER1(), "testpass");
        client.setAuthenticationURL(AUTH_URL);
        client.setConnectionTimeOut(CONNECTION_TIMEOUT);

		try {
			login(client);					       

			client.deleteObject("myfiles1", "/files/java0.log");
			assertTrue(true);
			testUploadFile();
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
	

	////////
	//////// Test sharing permissions
	////////
	public void test1userSetPermissions() {
		String AUTH_URL = getAUTHURL();
	        
	    FilesClient client = new FilesClient(getUSER1(), "testpass");
        client.setAuthenticationURL(AUTH_URL);
        client.setConnectionTimeOut(CONNECTION_TIMEOUT);

		try {
			login(client);
			assertTrue(client.shareObject("myfiles1", "/files", getUSER2(), true, true));
		} catch (Exception e) {
			fail(e.getMessage());
		} 
	}
	
	
	public void test2userGetPermissions() {
		String AUTH_URL = getAUTHURL();
	        
	    FilesClient client = new FilesClient(getUSER2(), "testpass");
        client.setAuthenticationURL(AUTH_URL);
        client.setConnectionTimeOut(CONNECTION_TIMEOUT);

		try {
			login(client);
			
			JsonArray array = client.listSharedObjects();			
			
			System.out.println("User -> " + client.getUserName());
			System.out.println("Permissions -> " + array);			
			assertTrue(true);
		} catch (Exception e) {
			fail(e.getMessage());
		} 
	}
	
	public void test2userPutShareObjet() {
		String AUTH_URL = getAUTHURL();
	        
	    FilesClient client = new FilesClient(getUSER2(), "testpass");
        client.setAuthenticationURL(AUTH_URL);
        client.setConnectionTimeOut(CONNECTION_TIMEOUT);

	    FilesClient client2 = new FilesClient(getUSER1(), "testpass");
        client2.setAuthenticationURL(AUTH_URL);
        client2.setConnectionTimeOut(CONNECTION_TIMEOUT);
        
		try {
			login(client);
			login(client2);
		
			File file = new File("/home/gguerrero/java0.log");						
			InputStream is = new FileInputStream(file);
			
			JsonObject obj = new JsonObject();
	        //{"container":"myfiles1","read":1,"object":"foo","storageurl":"AUTH_897b8dab-9b0f-444e-94d5-efeaeac4e592","write":0,"user":"tester1:user"}
	        
			String storageUrl = client2.getStorageURL().split("/v1/")[1];
	        //obj.addProperty("storageurl", "AUTH_2");
			obj.addProperty("storageurl", storageUrl);
	        obj.addProperty("container", "myfiles1");	       
	        obj.addProperty("object", "/files/java1.log");  
	        
	        client.storeObjectAsStream(obj, is, "application/x-Stacksync");
			assertTrue(true);
		} catch (Exception e) {
			fail(e.getMessage());
		} 
	}
	
	public void test2userDownloadShareObjet() {
		String AUTH_URL = getAUTHURL();
        
	    FilesClient client = new FilesClient(getUSER2(), "testpass");
        client.setAuthenticationURL(AUTH_URL);
        client.setConnectionTimeOut(CONNECTION_TIMEOUT);

        
	    FilesClient client2 = new FilesClient(getUSER1(), "testpass");
        client2.setAuthenticationURL(AUTH_URL);
        client2.setConnectionTimeOut(CONNECTION_TIMEOUT);
        
		try {
			login(client);
			login(client2);

			JsonObject obj = new JsonObject();
	        //{"container":"myfiles1","read":1,"object":"foo","storageurl":"AUTH_897b8dab-9b0f-444e-94d5-efeaeac4e592","write":0,"user":"tester1:user"}
	        
			String storageUrl = client2.getStorageURL().split("/v1/")[1];
	        //obj.addProperty("storageurl", "AUTH_2");
			obj.addProperty("storageurl", storageUrl);
	        obj.addProperty("container", "myfiles1");	       
	        obj.addProperty("object", "/files/java1.log");  
	        
			File file = new File("/home/gguerrero/java0.log");						
			InputStream in1 = new FileInputStream(file);
	        InputStream in2 = client.getObjectAsStream(obj);
	        
	        assertTrue(isEqual(in1, in2));
		} catch (Exception e) {
			fail(e.getMessage());
		} 		
	}
	
	
	public void testList1userListObjects(){		
		String AUTH_URL = getAUTHURL();
	        
		String user = getUSER1();
		String container = "myfiles1";
	    FilesClient client = new FilesClient(user, "testpass");
        client.setAuthenticationURL(AUTH_URL);
        client.setConnectionTimeOut(CONNECTION_TIMEOUT);

		try {
			login(client);
			List<FilesObject> objects = client.listObjectsStartingWith(container, "/files", null, -1, null);
			
			System.out.println("User " + user + " -> " + container);
			for(FilesObject obj: objects){
				System.out.println("User " + user + " -> " + container + "/" + obj.getName());
			}
			
			assertTrue(true);
		} catch (Exception e) {
			fail(e.getMessage());
		} 
	}
	
	public void test2userDeleteShareFiles(){
		String AUTH_URL = getAUTHURL();
	    int CONNECTION_TIMEOUT = 5000;
	        
	    FilesClient client = new FilesClient(getUSER2(), "testpass");
        client.setAuthenticationURL(AUTH_URL);
        client.setConnectionTimeOut(CONNECTION_TIMEOUT);

        
	    FilesClient client2 = new FilesClient(getUSER1(), "testpass");
        client2.setAuthenticationURL(AUTH_URL);
        client2.setConnectionTimeOut(CONNECTION_TIMEOUT);        
       
        
		try {
			login(client);
			login(client2);
			
	        JsonObject obj = new JsonObject();
	        //{"container":"myfiles1","read":1,"object":"foo","storageurl":"AUTH_897b8dab-9b0f-444e-94d5-efeaeac4e592","write":0,"user":"tester1:user"}
	        
	        String storageUrl = client2.getStorageURL().split("/v1/")[1];
	        //obj.addProperty("storageurl", "AUTH_2");
	        obj.addProperty("storageurl", storageUrl);
	        obj.addProperty("container", "myfiles1");	       
	        obj.addProperty("object", "/files/java0.log");  
			
			client.deleteSharedObject(obj);
			assertTrue(true);
		} catch (Exception e) {			
			fail(e.getMessage());
		}		
	}		
	
	
	public void testList2userListSharedObjects(){
		String AUTH_URL = getAUTHURL();
        
		String user = getUSER2();
		String container = "myfiles1";
		
	    FilesClient client = new FilesClient(user, "testpass");
        client.setAuthenticationURL(AUTH_URL);
        client.setConnectionTimeOut(CONNECTION_TIMEOUT);

	    FilesClient client2 = new FilesClient(getUSER1(), "testpass");
        client2.setAuthenticationURL(AUTH_URL);
        client2.setConnectionTimeOut(CONNECTION_TIMEOUT);        
        
		try {
			login(client);
			login(client2);
			
			JsonObject objJson = new JsonObject();
	        //{"container":"myfiles1","read":1,"object":"foo","storageurl":"AUTH_897b8dab-9b0f-444e-94d5-efeaeac4e592","write":0,"user":"tester1:user"}
	        
			String storageUrl = client2.getStorageURL().split("/v1/")[1];
	        //objJson.addProperty("storageurl", "AUTH_2");
			objJson.addProperty("storageurl", storageUrl);
			objJson.addProperty("container", "myfiles1");	       
			objJson.addProperty("object", "/files");  
			
			List<FilesObject> objects = client.listObjectsStartingWith(objJson);
			
			System.out.println("User " + user + " -> " + container);
			for(FilesObject obj: objects){
				System.out.println("User " + user + " -> " + container + "/" + obj.getName());				
			}
			
			assertTrue(true);
		} catch (Exception e) {
			fail(e.getMessage());
		} 
				
	}
	
	
	public void test1userRemovePermissions() {
		String AUTH_URL = getAUTHURL();
	    int CONNECTION_TIMEOUT = 5000;
	        
	    FilesClient client = new FilesClient(getUSER1(), "testpass");
        client.setAuthenticationURL(AUTH_URL);
        client.setConnectionTimeOut(CONNECTION_TIMEOUT);

		try {
			login(client);
						
			assertTrue(client.unshareObject("myfiles1", "/files", getUSER2()));
			test2userGetPermissions();
		} catch (Exception e) {
			fail(e.getMessage());
		} 
	}	
		
	
	private void removeFiles(String user, String pass, String container){
		String namePrefix = "";
		String AUTH_URL = getAUTHURL();
	    int CONNECTION_TIMEOUT = 5000;
	        
	    FilesClient client = new FilesClient(user, pass);
        client.setAuthenticationURL(AUTH_URL);
        client.setConnectionTimeOut(CONNECTION_TIMEOUT);

		try {
			login(client);
			List<FilesObject> objects = client.listObjectsStartingWith(container, namePrefix, null, -1, null);
			
			System.out.println("User " + user + " -> " + container);
			for(FilesObject obj: objects){
				System.out.println("User " + user + " --Deleting-- -> " + container + "/" + obj.getName());
				//if(!obj.getName().contains("repository") && !obj.getName().contains("profile")){
				//if(obj.getName().contains("ast3201206191504")){
					client.deleteObject(container, obj.getName());
				//}
			}
			
			assertTrue(true);
		} catch (Exception e) {
			fail(e.getMessage());
		} 
	}
	
	public void testTestRemoveFiles(){
		
		removeFiles(getUSER1(), "testpass", "myfiles1");
		//removeFiles(getUSER2(), "testpass", "myfiles11");		
	}
}