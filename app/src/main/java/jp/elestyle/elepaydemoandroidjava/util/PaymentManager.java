package jp.elestyle.elepaydemoandroidjava.util;

import android.util.Base64;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import androidx.appcompat.app.AppCompatActivity;
import jp.elestyle.androidapp.elepay.ElePay;
import jp.elestyle.androidapp.elepay.ElePayConfiguration;
import jp.elestyle.androidapp.elepay.ElePayError;
import jp.elestyle.androidapp.elepay.ElePayResult;
import jp.elestyle.androidapp.elepay.ElePayResultListener;

public class PaymentManager {
    public static String MAKE_CHARGE_DEMO_URL = "https://api.elepay.io/charges";
    public static String INVALID_KEY = "Please use the key generated from elepay admin page.";

    private boolean isTestMode;
    private ElepayAPIKeys apiKeys;
    // Change this url to your own server to request charge object.
    private String paymentUrl;
    private PaymentResultHandler resultHandler;


    public PaymentManager(boolean isTestMode,
                          ElepayAPIKeys apiKeys,
                          String baseUrl,
                          String paymentUrl,
                          PaymentResultHandler handler) {
        this.isTestMode = isTestMode;
        this.apiKeys = apiKeys;
        this.paymentUrl = paymentUrl;
        this.resultHandler = handler;
        setup(apiKeys, baseUrl);
    }

    private void setup(ElepayAPIKeys apiKeys, String baseUrl) {

        String appKey = isTestMode ? apiKeys.publicTestKey : apiKeys.publicLiveKey;
        ElePayConfiguration config = new ElePayConfiguration(appKey, baseUrl);
        ElePay.setup(config);
    }

    public void makePayment(String amount, PaymentMethod paymentMethod, final AppCompatActivity fromActivity) {
        // NOTE: The charge object should be created from your own server.
        // Here just a demo for requesting charge object.

        byte[] key = isTestMode
                ? (apiKeys.secretTestKey + ":").getBytes(Charset.forName("UTF-8"))
                : (apiKeys.secretLiveKey + ":").getBytes(Charset.forName("UTF-8"));
        final String authString = "Basic " + Base64.encodeToString(key, Base64.NO_WRAP);
        Map<String, String> headerFields = new HashMap<String, String>() {{
            put("Authorization", authString);
        }};
        JSONObject params = new JSONObject();
        try {
            params.put("paymentMethod", paymentMethod.getRaw());
            params.put("amount", amount);
            params.put("orderNo", UUID.randomUUID().toString());
            params.put("description", "iCart Store Android app charge");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        new Thread(new PaymentRequester(paymentUrl, headerFields, params, new PaymentRequesterResultListener() {
            @Override
            public void onJSONResult(JSONObject jsonObject) {
                ElePay.processPayment(jsonObject, fromActivity, new ElePayResultListener() {
                    @Override
                    public void onElePayResult(@NotNull ElePayResult elePayResult) {
                        if (elePayResult instanceof ElePayResult.Succeeded) {
                            String id = ((ElePayResult.Succeeded) elePayResult).getPaymentId();
                            resultHandler.onPaymentSucceeded(id);
                        } else if (elePayResult instanceof ElePayResult.Failed) {
                            String id = ((ElePayResult.Failed) elePayResult).getPaymentId();
                            ElePayError error = ((ElePayResult.Failed) elePayResult).getError();
                            resultHandler.onPaymentFailed(id, error);
                        } else if (elePayResult instanceof ElePayResult.Canceled) {
                            String id = ((ElePayResult.Canceled) elePayResult).getPaymentId();
                            resultHandler.onPaymentCanceled(id);
                        }
                    }
                });
            }

            @Override
            public void onError(String message) {
                resultHandler.onPaymentFailed(
                        "Unknown id",
                        new ElePayError.SystemError(123L, message));
            }
        })).start();
    }
}
