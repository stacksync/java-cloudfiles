package com.rackspacecloud.client.cloudfiles.FilesClientTest;

import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.List;

import com.rackspacecloud.client.cloudfiles.FilesClient;
import com.rackspacecloud.client.cloudfiles.FilesObject;
import com.rackspacecloud.client.cloudfiles.expections.UnauthorizeException;

public class Disconnection {
	
	private static int CONNECTION_TIMEOUT = 10000;
	private static String URL = "http://cloudspaes.urv.cat:5000/v2.0/tokens";
	
	
	public static void main(String ... args) {
		
		FilesClient client = new FilesClient("usuario7:usuario7", "usario7");
	    client.setAuthenticationURL(URL);
        client.setConnectionTimeOut(CONNECTION_TIMEOUT);

        List<FilesObject> files;

			try {
				System.out.println(client.loginKeystone());
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				System.out.println("bleee");
				e.printStackTrace();
			} catch (UnauthorizeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SocketException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}		
		
	}

}
