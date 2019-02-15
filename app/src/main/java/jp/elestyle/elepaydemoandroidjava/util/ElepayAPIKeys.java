package jp.elestyle.elepaydemoandroidjava.util;

public class ElepayAPIKeys {
    public String publicTestKey;
    public String secretTestKey;
    public String publicLiveKey;
    public String secretLiveKey;

    public boolean areAllKeysAvailable() {
        return !publicTestKey.equals(PaymentManager.INVALID_KEY)
                && !secretTestKey.equals(PaymentManager.INVALID_KEY)
                && !publicLiveKey.equals(PaymentManager.INVALID_KEY)
                && !secretLiveKey.equals(PaymentManager.INVALID_KEY);
    }
}
