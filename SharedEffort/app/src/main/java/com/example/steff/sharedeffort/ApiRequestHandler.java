package com.example.steff.sharedeffort;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;


// Inspired by tutorial found at : https://www.baeldung.com/java-http-request

public class ApiRequestHandler extends Thread{
    private String baseURL;
    private String route;
    private JSONObject jsonObject;
    private IEventNotifier eventNotifier;
    public ApiRequestHandler(String route, JSONObject jsonObject, IEventNotifier eventNotifier){
        this.baseURL = "https://se.scpio.net/api";
        this.route = route;
        this.jsonObject = jsonObject;
        this.eventNotifier = eventNotifier;
    }

    public void run(){

        String apiCall = baseURL + "/" + route;
        HttpURLConnection connection;
        try {
            URL apiUrl = new URL(apiCall);
            connection = (HttpURLConnection) apiUrl.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");

            connection.setDoOutput(true);
            connection.getOutputStream().write(jsonObject.toString().getBytes());
            connection.getOutputStream().flush();


            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(
                    (connection.getInputStream())));

            String response  = bufferedReader.readLine();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                response += line;
            }
            bufferedReader.close();
            try {
                eventNotifier.RequestComplete(new JSONObject(response));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            connection.disconnect();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
