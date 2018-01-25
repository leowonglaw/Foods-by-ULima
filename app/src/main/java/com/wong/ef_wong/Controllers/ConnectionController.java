package com.wong.ef_wong.Controllers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpRetryException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Leo on 03/12/2016.
 */

public class ConnectionController {

    private static final String ROOT = "https://efwong-d5a9a.firebaseio.com";

    public ConnectionController() {
    }

    public ConnectionController getController() {
        return this;
    }
    public String getDATA(String ruta) throws IOException {
        URL url = new URL(ROOT + ruta);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        con.setRequestProperty("Accept", "application/json");
        con.setDoInput(true);
        con.setConnectTimeout(10000);
        con.setReadTimeout(10000);
        con.setRequestMethod("GET");
        con.connect();

        int httpResult = con.getResponseCode();
        if (httpResult == HttpURLConnection.HTTP_OK) {
            return convertInputStreamToString(con.getInputStream());
        } else {
            throw new HttpRetryException("ERROR "+httpResult, httpResult);
        }
    }
    private String convertInputStreamToString(InputStream is) throws IOException {
        StringBuilder sb = new StringBuilder();
        BufferedReader br = new BufferedReader(new InputStreamReader(is, "utf-8"));
        String line = null;
        while ((line = br.readLine()) != null) {
            sb.append(line + "\n");
        }
        br.close();
        System.out.println("---------------------TESTING---------------------");
        System.out.println(sb.toString());
        return sb.toString();
    }


}
