package com.infa;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.*;

public class getToken {

    public static void main(String[] args) throws JSONException {

        if (args.length == 1 && args[0].equalsIgnoreCase("help")) {
            System.out.println("to login -> java -jar ICSToken.jar <username> <password> <host like dm-us.informaticacloud.com>");
            System.out.println("to logout -> java -jar ICSToken.jar logout <token> <serverUrl>");
            System.exit(0);
        }

        if (args.length == 3 && args[0].equalsIgnoreCase("logout")) {
            getToken.logout(args[1], args[2]);
            System.exit(0);
        }

        String payload;
        int code;
        JSONObject obj = new JSONObject();
        obj.put("@type", "login");
        obj.put("username", args[0]);
        obj.put("password", args[1]);
        //obj.put("username", "test");
        //obj.put("password", "test");
        payload = obj.toString();

        String line;
        StringBuffer jsonString = new StringBuffer();
        try {
            //http://stackoverflow.com/questions/15570656/how-to-send-request-payload-to-rest-api-in-java
            URL url = new URL("https://" + args[2] + "/ma/api/v2/user/login");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("Content-Type", "application/json;");
            OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream(), "UTF-8");
            writer.write(payload);
            writer.close();
            code = connection.getResponseCode();
            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getResponseCode() / 100 == 2 ? connection.getInputStream() : connection.getErrorStream()));
            while ((line = br.readLine()) != null) {
                jsonString.append(line);
            }

            br.close();
            connection.disconnect();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
        String s = jsonString.toString();
        JSONObject jsonObject = new JSONObject(s);   //http://stackoverflow.com/questions/16574482/decoding-json-string-in-java
        if (code != 200) {
            String e = jsonObject.getString("@type");
            if (e.equals("error")) {
                System.out.println(jsonObject.getString("description"));
                System.exit(0);
            }
        }
        System.out.print(jsonObject.getString("icSessionId") + "," + jsonObject.getString("serverUrl"));
    }

    public static void logout(String token, String surl) {

        try {
            String urlr = surl + "/api/v2/user/logout";
            URL url = new URL(urlr);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("Content-Type", "application/json;");
            connection.setRequestProperty("icSessionId", token);
            int statusCode = connection.getResponseCode();
            System.out.println(statusCode);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

}
