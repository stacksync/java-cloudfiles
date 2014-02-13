package com.rackspacecloud.client.cloudfiles.FilesClientTest;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import com.rackspacecloud.client.cloudfiles.FilesClient;
import com.rackspacecloud.client.cloudfiles.FilesObjectMetaData;



import junit.framework.TestCase;

public class SwiftTestCase_old extends TestCase {

    private String AUTH_URL = "https://10.30.230.191:8080/auth/v1.0";
    private int CONNECTION_TIMEOUT = 5000;
    private FilesClient client1 = null, client2 = null;
    private String dummyFileName = "/files/test/dummyfile";
    private String dummyFolderName = "/files/test";

    private boolean loginClient1() {
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

    private boolean loginClient2() {
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

        for (int i = 0; i < 5; i++) {
            meta.put("meta" + i, "test" + i);
        }

        if (loginClient1()) {

            try {
                System.out.println("6) uploadFile --> " + content.getBytes().length + " bytes");

                if (client1.storeObject(container, content.getBytes(), contentType, fileName, meta)) {
                    System.out.println("6) uploadFile --> ok!");

                    InputStream is = client1.getObjectAsStream(container, fileName);
                    String contentRead = inputStreamAsString(is);

                    FilesObjectMetaData tmpMeta = client1.getObjectMetaData(container, fileName);

                    boolean checkContent = false;
                    if (contentRead.compareTo(content) == 0) {
                        checkContent = true;
                    } else {
                        fail("Content is diferent");
                    }

                    boolean checkContentType = false;
                    if (tmpMeta.getMimeType().compareTo(contentType) == 0) {
                        checkContentType = true;
                    } else {
                        fail("ContentType is diferent");
                    }

                    boolean checkSize = false;
                    if (tmpMeta.getContentLength().compareTo(String.valueOf(content.getBytes().length)) == 0) {
                        checkSize = true;
                    } else {
                        fail("Length is diferent");
                    }

                    boolean containsMeta = true, equalsMeta = true;
                    for (Map.Entry<String, String> entry : meta.entrySet()) {

                        String key = entry.getKey().substring(0, 1).toUpperCase() + entry.getKey().substring(1);
                        if (!tmpMeta.getMetaData().containsKey(key)) {
                            containsMeta = false;
                            fail("Missing meta");
                        } else {
                            if (tmpMeta.getMetaData().get(key).compareTo(entry.getValue()) != 0) {
                                equalsMeta = false;
                                fail("Different meta");
                            }
                        }
                    }

                    if (checkContent && checkContentType && checkSize && containsMeta && equalsMeta) {
                        System.out.println("6) checkFile --> ok!");
                    }
                }

            } catch (Exception e) {
                fail(e.getMessage());
            }
        } else {
            fail("Clients not loggin");
        }
    }

    public void testDeleteNormalFile() {

        if (loginClient1()) {

            try {
                System.out.println("8) deleteFile -->");
                client1.deleteObject("myfiles4", dummyFileName);
                System.out.println("8) deleteFile --> ok!");

            } catch (Exception e) {
                fail(e.getMessage());
            }
        } else {
            fail("Clients not loggin");
        }
    }
}