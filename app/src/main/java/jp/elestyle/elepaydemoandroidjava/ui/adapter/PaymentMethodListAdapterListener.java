package jp.elestyle.elepaydemoandroidjava.ui.adapter;

import jp.elestyle.elepaydemoandroidjava.util.PaymentMethod;

public interface PaymentMethodListAdapterListener {
    void onSelectPaymentMethod(PaymentMethod method);
}
