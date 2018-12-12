package jp.elestyle.elepaydemoandroidjava.util;

import jp.elestyle.elepaydemoandroidjava.R;

public enum PaymentMethod {
    WECHAT_PAY("wechat"),
    ALIPAY("alipay"),
    CREDIT_CARD("creditcard"),
    UNION_PAY("unionpay"),
    PAYPAL("paypal"),
    GOOGLE_PAY("google");

    private String raw;

    PaymentMethod(String rawValue) {
        raw = rawValue;
    }

    public static PaymentMethod from(String rawValue) {
        switch (rawValue) {
            case "wechat pay":
            case "wechatpay":
            case "wx":
                return WECHAT_PAY;

            case "alipay":
                return ALIPAY;

            case "credit card":
            case "creditcard":
                return CREDIT_CARD;

            case "union pay":
            case "unionpay":
                return UNION_PAY;

            case "paypal":
                return PAYPAL;

            default:
                throw new IllegalArgumentException("Unsupported payment method: " + rawValue);
        }
    }

    public String getRaw() {
        return raw;
    }

    public int associatedLocalisedResrouceId() {
        switch (this) {
            case ALIPAY:
                return R.string.payment_method_alipay;
            case CREDIT_CARD:
                return R.string.payment_method_credit_card;
            case UNION_PAY:
                return R.string.payment_method_union_pay;
            case PAYPAL:
                return R.string.payment_method_paypal;
            default:
                return 0;
        }
    }
}
