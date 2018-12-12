package jp.elestyle.elepaydemoandroidjava.util;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import androidx.appcompat.app.AppCompatActivity;
import jp.elestyle.androidapp.elepay.ElePay;
import jp.elestyle.androidapp.elepay.ElePayError;
import jp.elestyle.androidapp.elepay.ElePayResult;
import jp.elestyle.androidapp.elepay.ElePayResultListener;

public class PaymentManager {
    public static String DEFAULT_PAYMENT_URL = "https://demo.icart.jp/api/orders";
    public static String INVALID_TEST_KEY = "Your test key here. Please use the key generated from elepay admin page.";
    public static String INVALID_LIVE_KEY = "Your test key here. Please use the key generated from elepay admin page.";

    private boolean isTestMode;
    // Change this url to your own server to request charge object.
    private String paymentUrl;
    private PaymentResultHandler resultHandler;


    public PaymentManager(boolean isTestMode,
                          String testModeKey,
                          String liveModeKey,
                          String baseUrl,
                          String paymentUrl,
                          PaymentResultHandler handler) {
        this.isTestMode = isTestMode;
        this.paymentUrl = paymentUrl;
        this.resultHandler = handler;
        setup(testModeKey, liveModeKey, baseUrl);
    }

    private void setup(String testModeKey,
                       String liveModeKey,
                       String baseUrl) {
        String appScheme = "elepaydemojava";

        String appKey = isTestMode ? testModeKey : liveModeKey;
        ElePay.setup(appScheme, appKey, baseUrl);
    }

    public void makePayment(String amount, PaymentMethod paymentMethod, final AppCompatActivity fromActivity) {
        // NOTE: The charge object should be created from your own server.
        // Here just a demo for requesting charge object.

        // You can change this map value to specify the mode forcibly.
        Map<String, String> headerFields = new HashMap<String, String>() {{
            put("live-mode", isTestMode ? "false" : "true");
        }};
        JSONObject params = new JSONObject();
        try {
            params.put("paymentMethod", paymentMethod.getRaw());
            params.put("amount", amount);
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
                            String id = ((ElePayResult.Succeeded)elePayResult).getPaymentId();
                            resultHandler.onPaymentSucceeded(id);
                        } else if (elePayResult instanceof ElePayResult.Failed) {
                            String id = ((ElePayResult.Failed) elePayResult).getPaymentId();
                            ElePayError error = ((ElePayResult.Failed)elePayResult).getError();
                            resultHandler.onPaymentFailed(id, error);
                        } else if (elePayResult instanceof ElePayResult.Canceled) {
                            String id = ((ElePayResult.Canceled) elePayResult).getPaymentId();
                            resultHandler.onPaymentCanceled(id);
                        }
                    }
                });
            }

            @Override
            public void onError() {
                resultHandler.onPaymentFailed("Unknown id", new ElePayError.SystemError(123L, "Failed creating paymnet data."));
            }
        })).start();
    }
}
