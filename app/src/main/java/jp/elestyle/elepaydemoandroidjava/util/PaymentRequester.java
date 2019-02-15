package jp.elestyle.elepaydemoandroidjava.util;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

interface PaymentRequesterResultListener {
    void onJSONResult(JSONObject jsonObject);

    void onError(String message);
}

class PaymentRequester implements Runnable {
    private String url;
    private Map<String, String> headerFields;
    private JSONObject params;
    private PaymentRequesterResultListener resultListener;

    PaymentRequester(String url, Map<String, String> headerFields, JSONObject params, PaymentRequesterResultListener listener) {
        this.url = url;
        this.headerFields = headerFields;
        this.params = params;
        this.resultListener = listener;
    }


    @Override
    public void run() {
        HttpURLConnection connection;
        try {
            connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(5000);
            for (Map.Entry<String, String> entry : headerFields.entrySet()) {
                connection.setRequestProperty(entry.getKey(), entry.getValue());
            }

            OutputStream outputStream = connection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream));
            writer.write(params.toString());
            writer.flush();
            writer.close();
            outputStream.close();

            InputStream inputStream = connection.getErrorStream();
            if (inputStream != null) {
                handleError(inputStream);
            } else {
                inputStream = connection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder result = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }
                JSONObject json = new JSONObject(result.toString());
                resultListener.onJSONResult(json);
            }
        } catch (JSONException | IOException e) {
            e.printStackTrace();
            resultListener.onError(e.getLocalizedMessage());
        }
    }

    private void handleError(InputStream errorStream) {
        StringBuilder rawErrorStr = new StringBuilder();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(errorStream));
            String line;
            while ((line = reader.readLine()) != null) {
                rawErrorStr.append(line);
            }
        } catch (Exception e) {
            rawErrorStr.append(e.toString());
        }

        JSONObject jsonError;
        try {
            jsonError = new JSONObject(rawErrorStr.toString());
        } catch (Exception e) {
            jsonError = new JSONObject();
        }
        resultListener.onError(jsonError.optString("message", ""));
    }
}
