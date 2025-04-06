package com.utils;

import com.enums.EnumRequestType;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by Alvin on 10/28/2018.
 */
public class ConnectionUtils {

    public String sendRequest(EnumRequestType requestType, String requestUrl, HashMap<String, String> params) {
        StringBuilder sb = new StringBuilder();
        try {
            URL url = new URL(requestUrl);
            HttpURLConnection urlConnection = null;
            //HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
            if (url.getProtocol().equals("https")) {
                urlConnection = (HttpsURLConnection) url.openConnection();
            } else if (url.getProtocol().equals("http")) {
                urlConnection = (HttpURLConnection) url.openConnection();
            }
            urlConnection.setRequestProperty("Accept-Encoding", "identity");
            //CertificateUtils.disableSSLCertificateChecking();
            switch (requestType) {
                case GET:
                    urlConnection.setRequestMethod("GET");
                    break;
                case POST:
                    urlConnection.setRequestMethod("POST");
                    break;
            }
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);
            urlConnection.setConnectTimeout(5000);
/*            urlConnection.setRequestProperty("Connection", "Keep-Alive");
            urlConnection.setRequestProperty("ENCTYPE", "multipart/form-data");
            urlConnection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);*/
            //urlConnection.setRequestProperty("bill", sourceFileUri);

            OutputStream os = urlConnection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            writer.write(getPostDataString(params));
            writer.flush();
            writer.close();

            int responseCode = urlConnection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                InputStream inputStream = urlConnection.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
                sb = new StringBuilder();
                String response;

                while ((response = br.readLine()) != null) {
                    sb.append(response);
                }
            } else {
                sb.append(String.format("{\"result\":\"false\", message:\"%s\"}","Koneksi gagal, harap coba lagi."));
            }
        } catch (UnsupportedEncodingException e) {
            sb.append(String.format("{\"result\":\"false\", message:\"%s\"}",e.getMessage().replace("\"", "")));
            e.printStackTrace();
        } catch (ProtocolException e) {
            sb.append(String.format("{\"result\":\"false\", message:\"%s\"}",e.getMessage().replace("\"", "")));
            e.printStackTrace();
        } catch (MalformedURLException e) {
            sb.append(String.format("{\"result\":\"false\", message:\"%s\"}",e.getMessage().replace("\"", "")));
            e.printStackTrace();
        } catch (IOException e) {
            sb.append(String.format("{\"result\":\"false\", message:\"%s\"}",e.getMessage().replace("\"", "")));
            e.printStackTrace();
        }
        return sb.toString();
    }

    private String getPostDataString(HashMap<String, String> postDataParams) {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, String> entry : postDataParams.entrySet()) {
            if (first) {
                first = false;
            } else {
                result.append("&");
            }
            try {
                result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
                result.append("=");
                result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result.toString();
    }
}
