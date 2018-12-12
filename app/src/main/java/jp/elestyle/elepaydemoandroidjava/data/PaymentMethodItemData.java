package jp.elestyle.elepaydemoandroidjava.data;

import jp.elestyle.elepaydemoandroidjava.util.PaymentMethod;

public class PaymentMethodItemData {
    private int imageRes;
    private PaymentMethod paymentMethod;

    public PaymentMethodItemData(int imageRes, PaymentMethod paymentMethod) {
        this.imageRes = imageRes;
        this.paymentMethod = paymentMethod;
    }

    public int getImageRes() {
        return imageRes;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }
}
