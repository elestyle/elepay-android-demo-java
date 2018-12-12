package jp.elestyle.elepaydemoandroidjava.util;

import jp.elestyle.androidapp.elepay.ElePayError;

public interface PaymentResultHandler {
    void onPaymentSucceeded(String paymentId);
    void onPaymentFailed(String paymentId, ElePayError error);
    void onPaymentCanceled(String paymentId);
}
