package com.hp.ucmdb.rest.sample.utils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.*;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustAllStrategy;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import javax.net.ssl.*;
import java.io.*;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import java.net.URI;
public class RestApiConnectionUtils {
    public static final String MEDIA_TYPE_JSON = "application/json";
    public static final String MEDIA_ALL = "*/*";

    public static final String TCP_PROTOCOL = "https://";
    public static final String URI_PREFIX = "/rest-api";
    public static final String RETURNED_A_STATUS_CODE_OF = "Returned a status code of ";
    public static final String RESPONSE_RESULT = "Response Result: ";

    static class HttpDeleteWithBody extends HttpEntityEnclosingRequestBase {
        public static final String METHOD_NAME = "DELETE";

        public String getMethod() { return METHOD_NAME; }

        public HttpDeleteWithBody(final String uri) {
            super();
            setURI(URI.create(uri));
        }
        public HttpDeleteWithBody(final URI uri) {
            super();
            setURI(uri);
        }
        public HttpDeleteWithBody() { super(); }
    }

    public static String loginServer(String serverIP, String userName, String password)throws JSONException, IOException{
        return loginServer(serverIP, userName, password, "8443");
    }

    public static String loginServer(String serverIP, String userName, String password, String port)throws JSONException, IOException{
        //HTTPS protocol, server IP and API type as the prefix of REST API URL.
        if(serverIP == null || serverIP.length() == 0 || userName == null || userName.length() == 0 || password == null || password.length()== 0){
            System.out.println("Please input correct serverIp or userName or password!");
            return null;
        }
        String domainURLAndApiType="https://" + serverIP + ":" + port + "/rest-api/";

        //adminUser has the sample access.
        JSONObject loginJson = new JSONObject();
        loginJson.put("clientContext","1");
        loginJson.put("username",userName);
        loginJson.put("password",password);

        //Put username and password in HTTP request body and invoke REST API(rest-api/authenticate) with POST method to get token.
        System.out.print("Login server request : ");
        String result = doPost(domainURLAndApiType+"authenticate", null, loginJson.toString());
        System.out.println("The response of login is " + result);
        if(result == null || result.length() == 0){
            System.out.println("Failed to connect with the UCMDB server!");
            return result;
        }
        String token = new JSONObject(result).getString("token");

        if(token != null) System.out.println("Connect to server Successfully!");
        return token;
    }

    public static String doPost(String url, String token, String content)throws IOException {
        HttpPost httpPost = getPostRequest(url, token, MEDIA_TYPE_JSON, MEDIA_TYPE_JSON, content);
        return getResponseString(httpPost);
    }

    public static String doGet(String url, String token) {
        HttpGet httpGet = getGetRequest(url, token, MEDIA_TYPE_JSON, MEDIA_TYPE_JSON);
        return getResponseString(httpGet);
    }


    public static String doPatch(String url, String token, String content) throws IOException{
        HttpPatch patchRequest = getPatchRquest(url, token, MEDIA_TYPE_JSON, MEDIA_TYPE_JSON, content);
        return getResponseString(patchRequest);
    }

    public static String doDelete(String url, String token, String content) throws IOException {
        HttpDeleteWithBody httpDeleteWithBody = getDeleteRequest(url, token, MEDIA_TYPE_JSON, MEDIA_TYPE_JSON, content);
        return getResponseString(httpDeleteWithBody);
    }

    public static CloseableHttpResponse sendMultiPartRequest(HttpPost httpPost, String fileFullPath,
                                                             String paramatersName, String fileName) {
        File file = new File(fileFullPath);
        InputStream fileInputStream = null;
        try{
            fileInputStream = new FileInputStream(file);
            HttpEntity requestEntity = MultipartEntityBuilder.create()
                    .addBinaryBody(paramatersName, fileInputStream, ContentType.DEFAULT_BINARY, fileName)
                    .build();
            httpPost.setEntity(requestEntity);
            return sendRequest(httpPost);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        } finally {
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    //ignore
                }
            }
        }
        return null;
    }

    public static void close(CloseableHttpResponse httpResponse) {
        if (httpResponse != null) {
            try {
                httpResponse.close();
            } catch (IOException e) {
                //ignore
            }
        }
    }

    public static String printStatusCode(int status){
        String result = "";
        switch (status){
            case 200:
                result = "200 (Successful)";break;
            case 400:
                result = "400 (Bad Request) Syntax/data provided is not valid for the request";break;
            case 401:
                result = "401 (Unauthorized) User not authorized or invalid session token";break;
            case 403:
                result = "403 (Forbidden) Operation is not allowed (for any user)";break;
            case 404:
                result = "404 (Not Found) The URI points to a non-existent resource/collection";break;
            case 405:
                result = "405 (Method Not Allowed) The HTTP method is not allowed for the resource";break;
            case 406:
                result = "406 (Not Acceptable) Media-type specified in the Accept header is not supported";break;
            case 412:
                result = "412 (Precondition Failed) Start and count values cannot be satisfied in a query";break;
            case 415:
                result = "415 (Unsupported Media Type) Media-type specified in the Content-Type header is not supported";break;
            case 500:
                result = "500 (Internal Server Error) An unexpected/server-side error has occurred";break;
            case 501:
                result = "501 (Not Implemented) The HTTP method is not currently implemented for the given resource/collection URI";break;
            case 503:
                result = "503 (Service Unavailable) The server is currently unavailable";break;

        }
        return result;
    }

    public static class TrustAnyHostnameVerifier implements HostnameVerifier {
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    }

    public static HttpGet getGetRequest(String url, String token, String requestContentType, String responseType) {
        HttpGet httpGet = new HttpGet(url);
        setRequestHeader(token, requestContentType, responseType, httpGet);
        return httpGet;
    }

    public static HttpPatch getPatchRquest(String url, String token, String requestContentType, String responseType,
                                           String content) throws UnsupportedEncodingException {
        HttpPatch httpGet = new HttpPatch(url);
        setRequestHeader(token, requestContentType, responseType, httpGet);
        setRequestContent(content, httpGet);
        return httpGet;
    }

    public static HttpPost getPostRequest(String url, String token, String requestContentType, String responseType,
                                          String content) throws UnsupportedEncodingException {
        HttpPost httpPost = new HttpPost(url);
        setRequestHeader(token, requestContentType, responseType, httpPost);
        setRequestContent(content, httpPost);
        return httpPost;
    }

    public static HttpDeleteWithBody getDeleteRequest(String url, String token, String requestContentType,
                                                      String responseType, String content) throws UnsupportedEncodingException {
        HttpDeleteWithBody httpDelete = new HttpDeleteWithBody(url);
        setRequestHeader(token, requestContentType, responseType, httpDelete);
        if(content != null){
            setRequestContent(content, httpDelete);
        }
        return httpDelete;
    }

    public static HttpDelete getDeleteRequest(String url, String token, String requestContentType, String responseType){
        HttpDelete httpDelete = new HttpDelete(url);
        setRequestHeader(token, requestContentType, responseType, httpDelete);
        return httpDelete;
    }

    public static CloseableHttpResponse sendRequest(HttpRequestBase httpRequest) throws IOException {
        SSLConnectionSocketFactory sslConnectionSocketFactory = getSslConnectionSocketFactory();
        CloseableHttpClient c = HttpClients
                .custom()
                .setSSLSocketFactory(sslConnectionSocketFactory)
                .setSSLHostnameVerifier(new RestApiConnectionUtils.TrustAnyHostnameVerifier())
                .build();
        return c.execute(httpRequest);
    }

    public static SSLConnectionSocketFactory getSslConnectionSocketFactory() {
        SSLContext sslcontext = null;
        try {
            // Just sample code
            // Please use TrustSelfSignedStrategy instead of TrustAllStrategy in production code
            sslcontext = SSLContexts.custom().loadTrustMaterial(null, new TrustAllStrategy()).build();
        } catch (NoSuchAlgorithmException | KeyManagementException | KeyStoreException e) {
            e.printStackTrace();
        }
        return new SSLConnectionSocketFactory(sslcontext,
                (s, sslSession) -> true);
    }

    public static void saveFile(HttpEntity entity, String targetFilePath) {
        InputStream is = null;
        FileOutputStream out = null;
        try {
            is = entity.getContent();
            out = new FileOutputStream(targetFilePath);
            byte[] buffer = new byte[4096];
            int readLength = 0;
            while ((readLength = is.read(buffer)) > 0) {
                byte[] bytes = new byte[readLength];
                System.arraycopy(buffer, 0, bytes, 0, readLength);
                out.write(bytes);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {

                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static void setRequestHeader(String token, String requestContentType, String responseType, HttpRequestBase request) {
        if (token != null) {
            request.setHeader("Authorization", "Bearer " + token);
        }
        if (requestContentType != null) {
            request.setHeader("Content-Type", requestContentType + ";charset=utf-8");
        }
        if (responseType != null) {
            request.setHeader("Accept", responseType);
        }
    }

    private static void setRequestContent(String content, HttpEntityEnclosingRequestBase request) throws UnsupportedEncodingException {
        if (content != null && content.length() > 0) {
            StringEntity entity = new StringEntity(content);
            request.setEntity(entity);
        }
    }

    private static String getResponseString(HttpRequestBase request) {
        CloseableHttpResponse httpResponse = null;
        String result = null;
        try {
            httpResponse = sendRequest(request);
            result = EntityUtils.toString(httpResponse.getEntity());
            System.out.println("Returned a status code of " + printStatusCode(httpResponse.getStatusLine().getStatusCode()));
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        } finally {
            close(httpResponse);
        }
        return result;
    }
}