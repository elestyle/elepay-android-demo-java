package jp.elestyle.elepaydemoandroidjava.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import jp.elestyle.androidapp.elepay.ElePayError;
import jp.elestyle.elepaydemoandroidjava.R;
import jp.elestyle.elepaydemoandroidjava.ui.adapter.PaymentMethodListAdapter;
import jp.elestyle.elepaydemoandroidjava.ui.adapter.PaymentMethodListAdapterListener;
import jp.elestyle.elepaydemoandroidjava.util.ElepayAPIKeys;
import jp.elestyle.elepaydemoandroidjava.util.PaymentManager;
import jp.elestyle.elepaydemoandroidjava.util.PaymentMethod;
import jp.elestyle.elepaydemoandroidjava.util.PaymentResultHandler;

public class PaymentActivity extends AppCompatActivity {
    public static String TAG = "PaymentActivity";
    public static String PREFS_NAME = "ElepayDemoAppPrefs";
    public static String INTENT_KEY_AMOUNT = "amount";

    private TextView paymentMethodIndicator;
    private MaterialDialog progressDialog;
    private Switch testModeSwitch;

    // -----------------------------------------------------------------
    //
    //                   /aaaaaaaaa\
    //                  d'          `b
    //                  8  ,aaa,      "Y888a     ,aaaa,     ,aaa,  ,aa,
    //                  8  8' `8          "88baadP""""YbaaadP"""YbdP""Yb
    //                  8  8   8             """        """      ""    8b
    //                  8  8, ,8         ,aaaaaaaaaaaaaaaaaaaaaaaaaaa88P
    //                  8  `"""'      ,d8""
    //                   \           /
    //                    \aaaaaaaaa/
    //
    private String testModePublicKey = PaymentManager.INVALID_KEY;
    private String liveModePublicKey = PaymentManager.INVALID_KEY;
    // The following keys are used to generate charge data.
    // You may consider create your charge data from your server for payment management.
    // So these keys may not live here.
    private String testModeSecretKey = PaymentManager.INVALID_KEY;
    private String liveModeSecretKey = PaymentManager.INVALID_KEY;
    // ↑ Replace your keys above
    // -----------------------------------------------------------------
    // Change this url to your own server to request charge object.
    private String paymentUrl = PaymentManager.MAKE_CHARGE_DEMO_URL;
    private PaymentManager paymentManager;
    private String amount;
    private PaymentMethod paymentMethod = PaymentMethod.CREDIT_CARD;
    private PaymentMethodListAdapterListener paymentMethodListAdapterListener = new PaymentMethodListAdapterListener() {
        @Override
        public void onSelectPaymentMethod(PaymentMethod method) {
            Log.d(PaymentActivity.TAG, "onPaymentMethodSelected(): " + method.getRaw());
            selectPaymnetMethod(method);
        }
    };

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle extra = getIntent().getExtras();
        if (extra != null) {
            amount = extra.getString(PaymentActivity.INTENT_KEY_AMOUNT);
            if (amount == null || amount.isEmpty()) {
                Log.d(PaymentActivity.TAG, "no amount");
                finish();
            }
        }

        setContentView(R.layout.activity_payment);

        progressDialog = new MaterialDialog.Builder(this).progress(true, 0).build();
        TextView amountView = findViewById(R.id.amount);
        amountView.setText("¥" + amount);
        paymentMethodIndicator = findViewById(R.id.paymentMethodIndicator);

        RecyclerView paymentMethodListView = findViewById(R.id.paymentMethodListView);
        paymentMethodListView.setAdapter(new PaymentMethodListAdapter(paymentMethodListAdapterListener));
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false);
        paymentMethodListView.setLayoutManager(layoutManager);

        testModeSwitch = findViewById(R.id.testModeSwitch);
        testModeSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveTestmodeSetting(testModeSwitch.isChecked());
                showTestModeChangingDialog(R.string.test_mode_switch_restart_prompt);
            }
        });
        testModeSwitch.setChecked(loadTestModeSetting());

        findViewById(R.id.payButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.setContent(R.string.content_processing);
                progressDialog.show();
                performPaying(amount);
            }
        });

        selectPaymnetMethod(PaymentMethod.CREDIT_CARD);

        setupPaymentManager();
    }

    private void setupPaymentManager() {
        String baseUrl;
        String paymentUrl;
        ElepayAPIKeys apiKeys = new ElepayAPIKeys();
        try {
            InputStream fileStream = getAssets().open("config.json");
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream(Math.max(8 * 1024, fileStream.available()));
            byte[] buffer = new byte[8 * 1024];
            int readBytes = fileStream.read(buffer);
            while (readBytes >= 0) {
                outputStream.write(buffer, 0, readBytes);
                readBytes = fileStream.read(buffer);
            }

            JSONObject json = new JSONObject(outputStream.toString());
            baseUrl = json.optString("baseUrl", "");
            paymentUrl = json.optString("paymentUrl", this.paymentUrl);
            apiKeys.publicTestKey = json.optString("pk_test", this.testModePublicKey);
            apiKeys.publicLiveKey = json.optString("pk_live", this.liveModePublicKey);
            apiKeys.secretTestKey = json.optString("sk_test", this.testModeSecretKey);
            apiKeys.secretLiveKey = json.optString("sk_live", this.liveModeSecretKey);
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            baseUrl = "";
            paymentUrl = this.paymentUrl;
            apiKeys.publicTestKey = this.testModePublicKey;
            apiKeys.publicLiveKey = this.liveModePublicKey;
            apiKeys.secretTestKey = this.testModeSecretKey;
            apiKeys.secretLiveKey = this.liveModeSecretKey;
        }

        if (!apiKeys.areAllKeysAvailable()) {
            finishWithoutValidKeys();
        }

        paymentManager = new PaymentManager(testModeSwitch.isChecked(), apiKeys, baseUrl, paymentUrl, new PaymentResultHandler() {
            @Override
            public void onPaymentSucceeded(final String paymentId) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();
                        showResultMessage("Succeeded paying " + paymentMethod.getRaw() + " " + amount);
                    }
                });
            }

            @Override
            public void onPaymentFailed(String paymentId, final ElePayError error) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();
                    }
                });
                final String message;
                if (error instanceof ElePayError.InvalidPayload) {
                    ElePayError.InvalidPayload errorInstance = (ElePayError.InvalidPayload) error;
                    message = errorInstance.getErrorCode() + " " + errorInstance.getMessage();
                } else if (error instanceof ElePayError.SystemError) {
                    ElePayError.SystemError errorInstance = (ElePayError.SystemError) error;
                    message = errorInstance.getErrorCode() + " " + errorInstance.getMessage();
                } else if (error instanceof ElePayError.AlreadyMakingPayment) {
                    ElePayError.AlreadyMakingPayment errorInstance = (ElePayError.AlreadyMakingPayment) error;
                    message = "Payment in processing: " + errorInstance.getPaymentId();
                } else if (error instanceof ElePayError.PaymentFailure) {
                    ElePayError.PaymentFailure errorInstance = (ElePayError.PaymentFailure) error;
                    message = errorInstance.getErrorCode() + " " + errorInstance.getMessage();
                } else if (error instanceof ElePayError.UnsupportedPaymentMethod) {
                    message = "Unsupported " + ((ElePayError.UnsupportedPaymentMethod) error).getPaymentMethod();
                } else if (error instanceof ElePayError.UninitializedPaymentMethod) {
                    ElePayError.UninitializedPaymentMethod errorInstance = (ElePayError.UninitializedPaymentMethod) error;
                    message = errorInstance.getErrorCode() + " " + errorInstance.getPaymentMethod();
                } else if (error instanceof ElePayError.PermissionRequired) {
                    final StringBuilder permissionMsg = new StringBuilder();
                    for (String permission : ((ElePayError.PermissionRequired) error).getPermissions()) {
                        permissionMsg.append(permission);
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showPermissionRequestDialog(permissionMsg.toString());
                        }
                    });
                    message = null;
                } else {
                    message = null;
                }
                if (message != null) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showResultMessage(message);
                        }
                    });
                }
            }

            @Override
            public void onPaymentCanceled(final String paymentId) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();
                        showResultMessage("Canceled paying " + paymentMethod.getRaw() + " " + amount);
                    }
                });
            }
        });
    }

    private void performPaying(String amount) {
        paymentManager.makePayment(amount, paymentMethod, this);
    }

    private boolean loadTestModeSetting() {
        return getSharedPreferences(PaymentActivity.PREFS_NAME, Context.MODE_PRIVATE).getBoolean("test_mode", false);
    }

    @SuppressLint("ApplySharedPref")
    private void saveTestmodeSetting(boolean testMode) {
        getSharedPreferences(PaymentActivity.PREFS_NAME, Context.MODE_PRIVATE)
                .edit()
                .putBoolean("test_mode", testMode)
                .commit();
    }

    private void finishWithoutValidKeys() {
        new AlertDialog.Builder(this)
                .setMessage("No valid keys provided. The keys are generated from elepay's admin page.")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setCancelable(false)
                .create()
                .show();
    }

    private void showResultMessage(String message) {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("OK", null)
                .create()
                .show();
    }

    private void showTestModeChangingDialog(int messageResId) {
        new AlertDialog.Builder(this)
                .setMessage(messageResId)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        recreate();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        testModeSwitch.setChecked(!testModeSwitch.isChecked());
                    }
                })
                .setCancelable(false)
                .create()
                .show();
    }

    private void showPermissionRequestDialog(String permissions) {
        new AlertDialog.Builder(this)
                .setMessage("Following permissions are required.\n" + permissions)
                .setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent();
                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri data = Uri.fromParts("package", getPackageName(), null);
                        intent.setData(data);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    private void selectPaymnetMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;

        int resId = paymentMethod.associatedLocalisedResrouceId();
        if (resId > 0) {
            paymentMethodIndicator.setText(resId);
        }
    }
}
