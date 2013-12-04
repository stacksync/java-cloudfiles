package com.rackspacecloud.client.cloudfiles.FilesClientTest;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.LinkedList;
import java.util.List;

import junit.framework.TestCase;

import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.message.BasicNameValuePair;

import com.rackspacecloud.client.cloudfiles.FilesClient;
import com.rackspacecloud.client.cloudfiles.FilesConstants;
import com.rackspacecloud.client.cloudfiles.FilesResponse;
import com.rackspacecloud.client.cloudfiles.expections.FilesContainerExistsException;

public class FilesClientTestCaseNewApi extends TestCase {
	
	int CONNECTION_TIMEOUT = 50000;
	private String getAUTHURL(){
		//String AUTH_URL = "https://10.30.234.94:8080/auth/v1.0";
		//String AUTH_URL = "https://10.30.230.191:8080/auth/v1.0";
		//String AUTH_URL = "http://192.168.124.85:5000/v2.0/tokens";
		
		//String AUTH_URL = "http://192.168.6.163:5000/v2.0/tokens";
		String AUTH_URL = "http://10.30.239.228:5000/v2.0/tokens";
		return AUTH_URL;
	}
	
	private String getUSER1(){
		String USER1 = "tusers:user1";
		//String USER1 = "tester1:user";
		
		return USER1;
	}
		
	private String getContainer(){
		String container = "myfiles1";
		return container;
	}
	
	private String getFile(){
		String file = "/test_file10.h";
		return file;
	}
	
	private String getFileId(){
		String fileId = "1742447237844729635";
		return fileId;
	}
	
	private String getFolder(){
		String folder = "/folder1";
		return folder;
	}
	
	public void test1userCreateContainer(){
		String AUTH_URL = getAUTHURL();
	        
	    FilesClient client = new FilesClient(getUSER1(), "testpass");
	    client.setAuthenticationURL(AUTH_URL);
        client.setConnectionTimeOut(CONNECTION_TIMEOUT);

		try {
			client.login();					
			client.createContainer(getContainer());			
			assertTrue(true);
		} catch(FilesContainerExistsException e){
			assertTrue(true);
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
	
	
	
	private String convertStreamToString(InputStream is) throws IOException {
        /*
         * To convert the InputStream to String we use the
         * Reader.read(char[] buffer) method. We iterate until the
         * Reader return -1 which means there's no more data to
         * read. We use the StringWriter class to produce the string.
         */
        if (is != null) {
            Writer writer = new StringWriter();
 
            char[] buffer = new char[1024];
            try {
                Reader reader = new BufferedReader(
                        new InputStreamReader(is, "UTF-8"));
                int n;
                while ((n = reader.read(buffer)) != -1) {
                    writer.write(buffer, 0, n);
                }
            } finally {
                is.close();
            }
            return writer.toString();
        } else {        
            return "";
        }
    }	


	public void testListRepository() {
		String AUTH_URL = getAUTHURL();
	        	    	  
	    FilesClient client = new FilesClient(getUSER1(), "testpass");
	    client.setAuthenticationURL(AUTH_URL);
        client.setConnectionTimeOut(CONNECTION_TIMEOUT);

		try {
			client.login();			
						
			HttpGet method = null;
			try {
				String uri = client.getStorageURL() + "/" + getContainer() + "/metadata";
				method = new HttpGet(uri);
				method.getParams().setIntParameter("http.socket.timeout",CONNECTION_TIMEOUT);
				method.setHeader(FilesConstants.X_AUTH_TOKEN, client.getAuthToken());
				method.setHeader("stacksync-api", "true");
				
				FilesResponse response = new FilesResponse(client.getClientHTTP().execute(method));

				String text = convertStreamToString(response.getResponseBodyAsStream());
				if (response.getStatusCode() == HttpStatus.SC_OK || response.getStatusCode() == HttpStatus.SC_ACCEPTED) {										
					System.out.println(text);
				}else{					
					System.out.println("testListRepository Error -> " + text);
				} 
			} finally {
				if (method != null)
					method.abort();
			}			
									
		} catch (Exception e){
			e.printStackTrace();
		}
        		
		assertTrue(true);
	}
	
	

	public void testListRecursiveRepository() {
		String AUTH_URL = getAUTHURL();
	        	    	  
	    FilesClient client = new FilesClient(getUSER1(), "testpass");
	    client.setAuthenticationURL(AUTH_URL);
        client.setConnectionTimeOut(CONNECTION_TIMEOUT);

		try {
			//client.login();			
			client.loginKeystone();			
			
			HttpGet method = null;
			try {
				LinkedList<NameValuePair> parameters = new LinkedList<NameValuePair>();
				parameters.add(new BasicNameValuePair("recursive", "TRUE"));	
				
				String uri = makeURI(client.getStorageURL() + "/" + getContainer() + "/metadata", parameters);
				method = new HttpGet(uri);
				method.getParams().setIntParameter("http.socket.timeout",CONNECTION_TIMEOUT);
				method.setHeader(FilesConstants.X_AUTH_TOKEN, client.getAuthToken());
				method.setHeader("stacksync-api", "true");
				
				FilesResponse response = new FilesResponse(client.getClientHTTP().execute(method));

				String text = convertStreamToString(response.getResponseBodyAsStream());
				if (response.getStatusCode() == HttpStatus.SC_OK || response.getStatusCode() == HttpStatus.SC_ACCEPTED) {										
					System.out.println(text);
				}else{					
					System.out.println("testListRecursiveRepository Error -> " + text);
				} 
			} finally {
				if (method != null)
					method.abort();
			}			
									
		} catch (Exception e){
			e.printStackTrace();
		}
        		
		assertTrue(true);
	}	

	private String makeURI(String base, List<NameValuePair> parameters) {
		return base + "?" + URLEncodedUtils.format(parameters, "UTF-8");
	}

	// "name": "config.h" "version": "1", "path": "/nautilus-syncany/", "id": "-7269666248008741611"
	public void testGetFile() {
		String AUTH_URL = getAUTHURL();
	        	    	  
	    FilesClient client = new FilesClient(getUSER1(), "testpass");
	    client.setAuthenticationURL(AUTH_URL);
        client.setConnectionTimeOut(CONNECTION_TIMEOUT);

		try {
			client.login();			
						
			HttpGet method = null;
			try {
				LinkedList<NameValuePair> parameters = new LinkedList<NameValuePair>();
				parameters.add(new BasicNameValuePair("prefix", getFile()));				
				//parameters.add(new BasicNameValuePair("version", "file"));
				//parameters.add(new BasicNameValuePair("idfile", "file"));				
				
				String uri = makeURI(client.getStorageURL() + "/" + getContainer() + "/files", parameters);
				method = new HttpGet(uri);
				method.getParams().setIntParameter("http.socket.timeout",CONNECTION_TIMEOUT);
				method.setHeader(FilesConstants.X_AUTH_TOKEN, client.getAuthToken());				
				method.setHeader("stacksync-api", "true");
				
				FilesResponse response = new FilesResponse(client.getClientHTTP().execute(method));

				String text = convertStreamToString(response.getResponseBodyAsStream());
				if (response.getStatusCode() == HttpStatus.SC_OK || response.getStatusCode() == HttpStatus.SC_ACCEPTED) {										
					System.out.println("testGetFile --> " + text);
				}else{					
					System.out.println("testGetFile --> File doesn't exist -> " + text);
				} 
			} finally {
				if (method != null)
					method.abort();
			}			
									
		} catch (Exception e){
			e.printStackTrace();
		}
        		
		assertTrue(true);
	}	
	
	// "name": "config.h" "version": "1", "path": "/nautilus-syncany/", "id": "-7269666248008741611"
	public void testGetFileWithId() {
		String AUTH_URL = getAUTHURL();
	        	    	  
	    FilesClient client = new FilesClient(getUSER1(), "testpass");
	    client.setAuthenticationURL(AUTH_URL);
        client.setConnectionTimeOut(CONNECTION_TIMEOUT);

		try {
			client.login();			
						
			HttpGet method = null;
			try {
				LinkedList<NameValuePair> parameters = new LinkedList<NameValuePair>();
				parameters.add(new BasicNameValuePair("prefix", getFile()));				
				//parameters.add(new BasicNameValuePair("version", "1"));
				parameters.add(new BasicNameValuePair("idfile", getFileId()));				
				
				String uri = makeURI(client.getStorageURL() + "/" + getContainer() + "/files", parameters);
				method = new HttpGet(uri);
				method.getParams().setIntParameter("http.socket.timeout",CONNECTION_TIMEOUT);
				method.setHeader(FilesConstants.X_AUTH_TOKEN, client.getAuthToken());
				method.setHeader("stacksync-api", "true");
				
				FilesResponse response = new FilesResponse(client.getClientHTTP().execute(method));

				String text = convertStreamToString(response.getResponseBodyAsStream());
				if (response.getStatusCode() == HttpStatus.SC_OK || response.getStatusCode() == HttpStatus.SC_ACCEPTED) {										
					System.out.println("testGetFileWithId --> " + text);
				}else{					
					System.out.println("testGetFileWithId --> File doesn't exist -> " + text);
				} 
			} finally {
				if (method != null)
					method.abort();
			}			
									
		} catch (Exception e){
			e.printStackTrace();
		}
        		
		assertTrue(true);
	}
	
	// "name": "config.h" "version": "1", "path": "/nautilus-syncany/", "id": "-7269666248008741611"
	public void testGetFileWithVersion() {
		String AUTH_URL = getAUTHURL();
	        	    	  
	    FilesClient client = new FilesClient(getUSER1(), "testpass");
	    client.setAuthenticationURL(AUTH_URL);
        client.setConnectionTimeOut(CONNECTION_TIMEOUT);

		try {
			client.login();			
						
			HttpGet method = null;
			try {
				LinkedList<NameValuePair> parameters = new LinkedList<NameValuePair>();
				parameters.add(new BasicNameValuePair("prefix", getFile()));				
				parameters.add(new BasicNameValuePair("version", "1"));
				//parameters.add(new BasicNameValuePair("idfile", getFileId()));				
				
				String uri = makeURI(client.getStorageURL() + "/" + getContainer() + "/files", parameters);
				method = new HttpGet(uri);
				method.getParams().setIntParameter("http.socket.timeout",CONNECTION_TIMEOUT);
				method.setHeader(FilesConstants.X_AUTH_TOKEN, client.getAuthToken());				
				method.setHeader("stacksync-api", "true");
				
				FilesResponse response = new FilesResponse(client.getClientHTTP().execute(method));

				String text = convertStreamToString(response.getResponseBodyAsStream());
				if (response.getStatusCode() == HttpStatus.SC_OK || response.getStatusCode() == HttpStatus.SC_ACCEPTED) {										
					System.out.println("testGetFileWithVersion --> " + text);
				}else{					
					System.out.println("testGetFileWithVersion --> File doesn't exist -> " + text);
				}
			} finally {
				if (method != null)
					method.abort();
			}			
									
		} catch (Exception e){
			e.printStackTrace();
		}
        		
		assertTrue(true);
	}	
	
	// "name": "config.h" "version": "1", "path": "/nautilus-syncany/", "id": "-7269666248008741611"
	public void testGetFileWithIdWithVersion() {
		String AUTH_URL = getAUTHURL();
	        	    	  
	    FilesClient client = new FilesClient(getUSER1(), "testpass");
	    client.setAuthenticationURL(AUTH_URL);
        client.setConnectionTimeOut(CONNECTION_TIMEOUT);

		try {
			client.login();			
						
			HttpGet method = null;
			try {
				LinkedList<NameValuePair> parameters = new LinkedList<NameValuePair>();
				parameters.add(new BasicNameValuePair("prefix", getFile()));				
				parameters.add(new BasicNameValuePair("version", "1"));
				parameters.add(new BasicNameValuePair("idfile", getFileId()));				
				
				String uri = makeURI(client.getStorageURL() + "/" + getContainer() + "/files", parameters);
				method = new HttpGet(uri);
				method.getParams().setIntParameter("http.socket.timeout",CONNECTION_TIMEOUT);
				method.setHeader(FilesConstants.X_AUTH_TOKEN, client.getAuthToken());				
				method.setHeader("stacksync-api", "true");
				
				FilesResponse response = new FilesResponse(client.getClientHTTP().execute(method));

				String text = convertStreamToString(response.getResponseBodyAsStream());
				if (response.getStatusCode() == HttpStatus.SC_OK || response.getStatusCode() == HttpStatus.SC_ACCEPTED) {										
					System.out.println("testGetFileWithIdWithVersion --> " + text);
				}else{					
					System.out.println("testGetFileWithIdWithVersion --> File doesn't exist -> " + text);
				}
			} finally {
				if (method != null)
					method.abort();
			}			
									
		} catch (Exception e){
			e.printStackTrace();
		}
        		
		assertTrue(true);
	}	
	
	
	// "name": "config.h" "version": "1", "path": "/nautilus-syncany/", "id": "-7269666248008741611"
	public void testGetFileWithIdOnly() {
		String AUTH_URL = getAUTHURL();
	        	    	  
	    FilesClient client = new FilesClient(getUSER1(), "testpass");
	    client.setAuthenticationURL(AUTH_URL);
        client.setConnectionTimeOut(CONNECTION_TIMEOUT);

		try {
			client.login();			
						
			HttpGet method = null;
			try {
				LinkedList<NameValuePair> parameters = new LinkedList<NameValuePair>();
				//parameters.add(new BasicNameValuePair("prefix", getFile()));				
				parameters.add(new BasicNameValuePair("version", "1"));
				parameters.add(new BasicNameValuePair("idfile", getFileId()));				
				
				String uri = makeURI(client.getStorageURL() + "/" + getContainer() + "/files", parameters);
				method = new HttpGet(uri);
				method.getParams().setIntParameter("http.socket.timeout",CONNECTION_TIMEOUT);
				method.setHeader(FilesConstants.X_AUTH_TOKEN, client.getAuthToken());				
				method.setHeader("stacksync-api", "true");
				
				FilesResponse response = new FilesResponse(client.getClientHTTP().execute(method));

				String text = convertStreamToString(response.getResponseBodyAsStream());
				if (response.getStatusCode() == HttpStatus.SC_OK || response.getStatusCode() == HttpStatus.SC_ACCEPTED) {										
					System.out.println("testGetFileWithIdOnly --> " + text);
				}else{					
					System.out.println("testGetFileWithIdOnly --> File doesn't exist -> " + text);
				}
			} finally {
				if (method != null)
					method.abort();
			}			
									
		} catch (Exception e){
			e.printStackTrace();
		}
        		
		assertTrue(true);
	}		

	
	public void testPUTFolder() {
		String AUTH_URL = getAUTHURL();
	        	    	  
	    FilesClient client = new FilesClient(getUSER1(), "testpass");
	    client.setAuthenticationURL(AUTH_URL);
        client.setConnectionTimeOut(CONNECTION_TIMEOUT);

		try {
			client.login();			
						
			HttpPut method = null;
			try {
				LinkedList<NameValuePair> parameters = new LinkedList<NameValuePair>();
				parameters.add(new BasicNameValuePair("prefix", getFolder()));				
				
				String uri = makeURI(client.getStorageURL() + "/" + getContainer() + "/files", parameters);
				method = new HttpPut(uri);
				method.getParams().setIntParameter("http.socket.timeout",CONNECTION_TIMEOUT);
				method.setHeader(FilesConstants.X_AUTH_TOKEN, client.getAuthToken());
				method.setHeader("Content-Type", "FOLDER");
				method.setHeader("stacksync-api", "true");				
				
				FilesResponse response = new FilesResponse(client.getClientHTTP().execute(method));
				
				if (response.getStatusCode() == HttpStatus.SC_OK || response.getStatusCode() == HttpStatus.SC_CREATED || response.getStatusCode() == HttpStatus.SC_ACCEPTED) {										
					System.out.println("testPUTFolder --> Ok -> ");
				}else{
					System.out.println("testPUTFolder --> Error -> ");
				}
			} finally {
				if (method != null)
					method.abort();
			}			
									
		} catch (Exception e){
			e.printStackTrace();
		}
        		
		assertTrue(true);
	}
	
	
	//PUT
	//curl -k -XPUT -i -H "X-Auth-Token: AUTH_tk186b33baad7049cb99bd3f123a936b02" https://10.30.230.191:8080/v1/AUTH_0e2b0d49-d7e6-4c34-a353-ecbd36c1382c/" + getContainer() + "0/files?prefix=/hola1/hola9/file_eva2.jpg -T /home/guillermo/Escritorio/sync2/Eva_Angelina_Wallpaper_JxHy.jpg
	public void testPutFile() {
		String AUTH_URL = getAUTHURL();
	        	    	  
	    FilesClient client = new FilesClient(getUSER1(), "testpass");
	    client.setAuthenticationURL(AUTH_URL);
        client.setConnectionTimeOut(CONNECTION_TIMEOUT);

		try {
			client.login();			
						
			HttpPut method = null;
			try {
				LinkedList<NameValuePair> parameters = new LinkedList<NameValuePair>();
				parameters.add(new BasicNameValuePair("prefix", getFile()));				
				
				String uri = makeURI(client.getStorageURL() + "/" + getContainer() + "/files", parameters);
				method = new HttpPut(uri);
				method.getParams().setIntParameter("http.socket.timeout",CONNECTION_TIMEOUT);
				method.setHeader(FilesConstants.X_AUTH_TOKEN, client.getAuthToken());				
				method.setHeader("stacksync-api", "true");
				
				File file = new File("/home/gguerrero/java0.log");						
				InputStream data = new FileInputStream(file);
				
				InputStreamEntity entity = new InputStreamEntity(data, -1);
				entity.setChunked(true);				
				method.setEntity(entity);
								
				FilesResponse response = new FilesResponse(client.getClientHTTP().execute(method));

				String text = convertStreamToString(response.getResponseBodyAsStream());
				if (response.getStatusCode() == HttpStatus.SC_OK || response.getStatusCode() == HttpStatus.SC_CREATED || response.getStatusCode() == HttpStatus.SC_ACCEPTED) {										
					System.out.println("testPutFile --> Ok -> " + text);
				}else{
					System.out.println("testPutFile --> Error -> " + text);
				}
			} finally {
				if (method != null)
					method.abort();
			}			
									
		} catch (Exception e){
			e.printStackTrace();
		}
        		
		assertTrue(true);
	}	
	
	
	// "name": "config.h" "version": "1", "path": "/nautilus-syncany/", "id": "-7269666248008741611"
	public void testGetFilePut() {
		String AUTH_URL = getAUTHURL();
	        	    	  
	    FilesClient client = new FilesClient(getUSER1(), "testpass");
	    client.setAuthenticationURL(AUTH_URL);
        client.setConnectionTimeOut(CONNECTION_TIMEOUT);

		try {
			client.login();			
						
			HttpGet method = null;
			try {
				LinkedList<NameValuePair> parameters = new LinkedList<NameValuePair>();
				parameters.add(new BasicNameValuePair("prefix", getFile()));				
				
				String uri = makeURI(client.getStorageURL() + "/" + getContainer() + "/files", parameters);
				method = new HttpGet(uri);
				method.getParams().setIntParameter("http.socket.timeout",CONNECTION_TIMEOUT);
				method.setHeader(FilesConstants.X_AUTH_TOKEN, client.getAuthToken());				
				method.setHeader("stacksync-api", "true");
				
				FilesResponse response = new FilesResponse(client.getClientHTTP().execute(method));

				String text = convertStreamToString(response.getResponseBodyAsStream());
				if (response.getStatusCode() == HttpStatus.SC_OK || response.getStatusCode() == HttpStatus.SC_ACCEPTED) {										
					System.out.println("testGetFilePut --> Ok");
				}else{					
					System.out.println("testGetFilePut --> File doesn't exist -> " + text);
				}
			} finally {
				if (method != null)
					method.abort();
			}			
									
		} catch (Exception e){
			e.printStackTrace();
		}
        		
		assertTrue(true);
	}			
	
	//DELETE
}
