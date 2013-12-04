package com.rackspacecloud.client.cloudfiles.FilesClientTest;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.rackspacecloud.client.cloudfiles.FilesClient;
import com.rackspacecloud.client.cloudfiles.FilesObject;
import com.rackspacecloud.client.cloudfiles.FilesObjectMetaData;



import junit.framework.TestCase;


public class SwiftTestCase_old extends TestCase {
	private String AUTH_URL = "https://10.30.230.191:8080/auth/v1.0";
    private int CONNECTION_TIMEOUT = 5000;
    private FilesClient client1 = null, client2 = null;
    private String dummyFileName = "/files/test/dummyfile";
    private String dummyFolderName = "/files/test";
    
    
    private boolean loginClient1(){
	    boolean connected = false;
	    
    	client1 = new FilesClient("tester:user", "testpass");
        client1.setAuthenticationURL(AUTH_URL);
        client1.setConnectionTimeOut(CONNECTION_TIMEOUT);
                
		try {
			connected = client1.login();
		} catch (Exception e) {
			fail(e.getMessage());
		}
		
		return connected;
    }
    
    
    private boolean loginClient2(){
	    boolean connected = false;
	    
    	client2 = new FilesClient("tester1:user", "testpass");
        client2.setAuthenticationURL(AUTH_URL);
        client2.setConnectionTimeOut(CONNECTION_TIMEOUT);
                
		try {
			connected = client2.login();
		} catch (Exception e) {
			fail(e.getMessage());
		}
		
		return connected;
    }    
    
    
	public void testLogin() {
				
	    System.out.println("1) login --> tester:user");
	    FilesClient client = new FilesClient("tester:user", "testpass");
        client.setAuthenticationURL(AUTH_URL);
        client.setConnectionTimeOut(CONNECTION_TIMEOUT);

		try {
			assertTrue(client.login());			
			System.out.println("1) login --> ok!");
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
	
	
	private boolean testSetPermissions(String container, String object, boolean read, boolean write) {
	        
		boolean result = false;
        String users = "tester1:user, popo";
        System.out.println("2) setPermissions --> r:" + read + " w: " + write);
   
		try {
				
			if(client1.shareObject(container, object, users, read, write)){
				System.out.println("3) getPermissions -->");
				
				boolean found = false;
				boolean correct = false;
				
				JsonArray array = client2.listSharedObjects();								
				String storageUrl = "AUTH_" + client1.getStorageURL().split("AUTH_")[1];
				
				for(JsonElement elem: array){
					JsonObject obj = elem.getAsJsonObject();					
					String pContainer = obj.get("container").getAsString();
					String pObject = obj.get("object").getAsString();
					boolean pRead = Boolean.parseBoolean(obj.get("read").getAsString()) || obj.get("read").getAsString().compareTo("1") == 0;
					boolean pWrite = Boolean.parseBoolean(obj.get("write").getAsString()) || obj.get("write").getAsString().compareTo("1") == 0;
					String pStorageUrl = obj.get("storageurl").getAsString();
					
					if(pContainer.compareTo(container) == 0 && 
					   pObject.compareTo(object) == 0 &&
					   pStorageUrl.compareTo(storageUrl) == 0){
						
						found = true;						
						if(read == pRead && write == pWrite){
							correct = true;
						}
						
						break;
					}					
				}
				
				if(found && correct){
					System.out.println("4) setPermissions --> ok!");
					result = true;
				} else if(!found){
					fail("Permission not found");
				} else if(!correct){
					fail("Permission not correct(read,write)");					
				}
			} else {
				fail("class FilesClient.shareObject --> Error return false");
			}

		} catch (Exception e) {
			fail(e.getMessage());
		}

		return result;
	}
	
	
	public void testPermissions(){
		
		if(loginClient1() && loginClient2()){
			boolean result = true;
			result &= testSetPermissions("myfiles4", "/files/testing/hola1 (copy)", false, false);
			result &= testSetPermissions("myfiles4", "/files/testing/hola1 (copy)", true, false);
			result &= testSetPermissions("myfiles4", "/files/testing/hola1 (copy)", false, true);
			result &= testSetPermissions("myfiles4", "/files/testing/hola1 (copy)", true, true);
			
			assertTrue(result);
		} else{
	    	fail("Clients not loggin");
		}		
	}

	
	public void testUnSetPermissions() {
        
        String users = "tester1:user, popo";
        String container = "myfiles4";        
        String object = "/files/testing/hola1 (copy)";               
        
        System.out.println("5) unsetPermissions -->");
		
	    if(loginClient1() && loginClient2()){
        
			try {
				
				if(client1.unshareObject(container, object, users)){
					JsonArray array = client2.listSharedObjects();			
					
					boolean found = false;
					String storageUrl = "AUTH_" + client1.getStorageURL().split("AUTH_")[1];
					
					for(JsonElement elem: array){
						JsonObject obj = elem.getAsJsonObject();					
						String pContainer = obj.get("container").getAsString();
						String pObject = obj.get("object").getAsString();					
						String pStorageUrl = obj.get("storageurl").getAsString();
						
						if(pContainer.compareTo(container) == 0 && 
						   pObject.compareTo(object) == 0 &&
						   pStorageUrl.compareTo(storageUrl) == 0){
							
							found = true;						
							break;
						}					
					}
					
					assertTrue(!found);	
					System.out.println("5) unsetPermissions --> ok!");
				} else {
					fail("class FilesClient.unshareObject --> Error return false");
				}
	
			} catch (Exception e) {
				fail(e.getMessage());
			}
	    }else{
	    	fail("Clients not loggin");
	    }
	}

	
	private String inputStreamAsString(InputStream stream) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(stream));
		StringBuilder sb = new StringBuilder();
		String line1 = null;

		while ((line1 = br.readLine()) != null) {
			sb.append(line1 + "\n");
		}

		br.close();
		return sb.toString();
	}
	
	
	public void testUploadNormalFile() {

		String content = "dummy-file!\ndummy-file!\ndummy-file!\ndummy-file!\ndummy-file!\n";
		String contentType = "test/test";
		String container = "myfiles4";
		String fileName = dummyFileName;
		
		Map<String, String> meta = new HashMap<String, String>();
		
		for(int i = 0; i<5; i++){
			meta.put("meta" + i, "test" + i);
		}
		
        if(loginClient1()){
        
			try {
				System.out.println("6) uploadFile --> " + content.getBytes().length + " bytes");
	
				if(client1.storeObject(container, content.getBytes(), contentType, fileName, meta)){
					System.out.println("6) uploadFile --> ok!");
					
					InputStream is = client1.getObjectAsStream(container, fileName);
					String contentRead = inputStreamAsString(is);
					
					FilesObjectMetaData tmpMeta = client1.getObjectMetaData(container, fileName);
					
					boolean checkContent = false;
					if(contentRead.compareTo(content) == 0){
						checkContent = true;
					} else {						
						fail("Content is diferent");
					}
					
					boolean checkContentType = false;
					if(tmpMeta.getMimeType().compareTo(contentType) == 0){
						checkContentType = true;
					} else {						
						fail("ContentType is diferent");
					}
					
					boolean checkSize = false;					
					if(tmpMeta.getContentLength().compareTo(String.valueOf(content.getBytes().length)) == 0){
						checkSize = true;
					} else {						
						fail("Length is diferent");
					}
					
					boolean containsMeta = true, equalsMeta = true;
					for (Map.Entry<String,String> entry: meta.entrySet()) {
						
						String key = entry.getKey().substring(0,1).toUpperCase() + entry.getKey().substring(1);
						if(!tmpMeta.getMetaData().containsKey(key)){
							containsMeta = false;
							fail("Missing meta");
						} else{
							if(tmpMeta.getMetaData().get(key).compareTo(entry.getValue()) != 0){
								equalsMeta = false;
								fail("Different meta");
							}
						}
					}					
					
					if(checkContent && checkContentType && checkSize && containsMeta && equalsMeta){
						System.out.println("6) checkFile --> ok!");
					}
				}
				
			} catch (Exception e) {
				fail(e.getMessage());
			}
        }else{
	    	fail("Clients not loggin");
	    }
	}
	
	
	private boolean requestGetFile(){
		
		JsonObject obj = new JsonObject();        
        obj.addProperty("container", "myfiles4");
        obj.addProperty("object", dummyFileName);
        obj.addProperty("storageurl", "AUTH_" + client1.getStorageURL().split("AUTH_")[1]);    		    	
		
        boolean result = false;
		try {
			InputStream is = client2.getObjectAsStream(obj);
			if(is != null){
				result = true;
			}
		} catch (Exception e) {			
			//fail(e.getMessage());
		} 
		
		return result;
	}
	
	private boolean requestPutFile(){
		JsonObject obj = new JsonObject();        
        obj.addProperty("container", "myfiles4");
        obj.addProperty("object", dummyFileName);
        obj.addProperty("storageurl", "AUTH_" + client1.getStorageURL().split("AUTH_")[1]);    		    	
		
        String content = "dummy-file2!\ndummy-file2!\ndummy-file2!\ndummy-file2!\ndummy-file2!\n";
        
        boolean result = false;
		try {
			client2.storeObjectAsStream(obj, new ByteArrayInputStream(content.getBytes("UTF-8")), "test2/test2");
			result = true;
		} catch (Exception e) {			
			//fail(e.getMessage());
		} 
		
		return result;
	}
	
	
	/// HEAD getFilesObjectFrom
	private boolean requestHeadFile(){
		JsonObject obj = new JsonObject();        
        obj.addProperty("container", "myfiles4");
        obj.addProperty("object", dummyFileName);
        obj.addProperty("storageurl", "AUTH_" + client1.getStorageURL().split("AUTH_")[1]);    		    	
		        
        boolean result = false;
		try {
			/*FilesObject meta =*/ client2.getFilesObjectFrom(obj);
			result = true;
		} catch (Exception e) {			
			//fail(e.getMessage());
		} 
				
		return result;
	}
	
	
	/// update POST metadata updateRemoteObjectMetadata	
	private boolean requestPostFile(){
		JsonObject obj = new JsonObject();        
        obj.addProperty("container", "myfiles4");
        obj.addProperty("object", dummyFileName);
        obj.addProperty("storageurl", "AUTH_" + client1.getStorageURL().split("AUTH_")[1]);    		    	
		        
		Map<String, String> meta = new HashMap<String, String>();
		
		for(int i = 0; i<5; i++){
			meta.put("meta" + i, "test_u2_" + i);
		}        
        
        boolean result = false;
		try {
			result = client2.updateRemoteObjectMetadata(obj, meta);
		} catch (Exception e) {			
			//fail(e.getMessage());
		} 
				
		return result;
	}
	
	public void testShareDummyFile1(){         
		try{
			if(loginClient1() && loginClient2()){
				testUploadNormalFile();
				System.out.println("9) testShareFile --> r:- w:-");
				if(client1.unshareObject("myfiles4", dummyFolderName, "tester1:user")){
					System.out.println("9) testShareFile HEAD --> " + requestHeadFile());
					System.out.println("9) testShareFile GET --> " + requestGetFile());					
					System.out.println("9) testShareFile PUT --> " + requestPutFile());
					System.out.println("9) testShareFile POST --> " + requestPostFile());
				}else{
					fail("No set permissions");
				}				
			} else{
		    	fail("Clients not loggin");
			}		
		}catch (Exception e) {
			fail(e.getMessage());
		}
	}
	
	
	public void testShareDummyFile2(){

		try{
			if(loginClient1() && loginClient2()){
				testUploadNormalFile();
				System.out.println("9) testShareFile --> r:0 w:0");
				if(testSetPermissions("myfiles4", dummyFolderName, false, false)){		
					System.out.println("9) testShareFile HEAD --> " + requestHeadFile());
					System.out.println("9) testShareFile GET --> " + requestGetFile());					
					System.out.println("9) testShareFile PUT --> " + requestPutFile());
					System.out.println("9) testShareFile POST --> " + requestPostFile());
				}else{
					fail("No set permissions --> r:0 w:0");
				}			
			} else{
		    	fail("Clients not loggin");
			}		
		}catch (Exception e) {
			fail(e.getMessage());
		}		
	}
	
	public void testShareDummyFile3(){

		try{
			if(loginClient1() && loginClient2()){
				testUploadNormalFile();
				System.out.println("10) setPermissions --> r:1 w:0");
				if(testSetPermissions("myfiles4", dummyFolderName, true, false)){
					System.out.println("9) testShareFile HEAD --> " + requestHeadFile());
					System.out.println("9) testShareFile GET --> " + requestGetFile());					
					System.out.println("9) testShareFile PUT --> " + requestPutFile());
					System.out.println("9) testShareFile POST --> " + requestPostFile());
				}else{
					fail("No set permissions --> r:1 w:0");
				}		
			} else{
		    	fail("Clients not loggin");
			}		
		}catch (Exception e) {
			fail(e.getMessage());
		}
	}
	
	
	public void testShareDummyFile4(){

		try{
			if(loginClient1() && loginClient2()){
				testUploadNormalFile();
				System.out.println("11) setPermissions --> r:0 w:1");
				if(testSetPermissions("myfiles4", dummyFileName, false, true)){
					System.out.println("9) testShareFile HEAD --> " + requestHeadFile());
					System.out.println("9) testShareFile GET --> " + requestGetFile());					
					System.out.println("9) testShareFile PUT --> " + requestPutFile());
					System.out.println("9) testShareFile POST --> " + requestPostFile());
				} else{
					fail("No set permissions --> r:0 w:1");
				}	
			} else{
		    	fail("Clients not loggin");
			}		
		}catch (Exception e) {
			fail(e.getMessage());
		}
	}
	
	public void testShareDummyFile5(){

		try{
			if(loginClient1() && loginClient2()){
				testUploadNormalFile();
				System.out.println("12) setPermissions --> r:1 w:1");
				if(testSetPermissions("myfiles4", dummyFolderName, true, true)){
					System.out.println("9) testShareFile HEAD --> " + requestHeadFile());
					System.out.println("9) testShareFile GET --> " + requestGetFile());					
					System.out.println("9) testShareFile PUT --> " + requestPutFile());
					System.out.println("9) testShareFile POST --> " + requestPostFile());
				}else{
					fail("No set permissions --> r:1 w:1");
				}	
			} else{
		    	fail("Clients not loggin");
			}		
		}catch (Exception e) {
			fail(e.getMessage());
		}
	}

	
	
	public void testListDirectory() {
        if(loginClient1()){
        
        	System.out.println("7) listFolder -->");        	
            JsonObject obj = new JsonObject();
            obj.addProperty("container", "myfiles4");
            obj.addProperty("object", "/files/test/");
            obj.addProperty("storageurl", "AUTH_" + client1.getStorageURL().split("AUTH_")[1]);
        	
        	try{
        		boolean contains = false;
        		List<FilesObject> files = client1.listObjectsStartingWith(obj);
        		for(FilesObject containerObj: files){
        			if(containerObj.getName().compareTo(dummyFileName) == 0){
        				contains = true;
        				break;
        			}
        		}
	        
        		if(contains){     		
        			System.out.println("7) listFolder --> ok!");
        		}else{
        			fail("Not found exist!!!");
        		}
        		
			} catch (Exception e) {
				fail(e.getMessage());
			}	
				
	    }else{
	    	fail("Clients not loggin");
	    }
	}
	
	public void testDeleteNormalFile() {

        if(loginClient1()){

        	try{
            	System.out.println("8) deleteFile -->");
        		client1.deleteObject("myfiles4", dummyFileName);        		        		
    			System.out.println("8) deleteFile --> ok!");
        		
			} catch (Exception e) {
				fail(e.getMessage());
			}
	    }else{
	    	fail("Clients not loggin");
	    }
	}
}