package com.example.administrator.myapplication;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;

/**
 * Created by Ghanshyam patidar on 23/9/15.
 */
public class PaypalTestActivity extends Activity {

    // set to PaymentActivity.ENVIRONMENT_PRODUCTION to move real money.
    // set to PaymentActivity.ENVIRONMENT_SANDBOX to use your test credentials from https://developer.paypal.com
    // set to PaymentActivity.ENVIRONMENT_NO_NETWORK to kick the tires without communicating to PayPal's servers.
    private static final String CONFIG_ENVIRONMENT = PaymentActivity.ENVIRONMENT_SANDBOX;

    private static final String CONFIG_CLIENT_ID = "AcoiHE2h0dLYeYuaRqwsadadsdhhfgH4XCVXCVHbT-GZRg4FIBy9IkjMGCGbPUxBLBZo";
    private static final String CONFIG_RECEIVER_EMAIL = "mytest1-facilitator@gmail.com";

    Context mContext;

    paymentDetail paydetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);


        mContext = this;


        Intent intent = new Intent(PaypalTestActivity.this,
                PayPalService.class);


        intent.putExtra(PaymentActivity.EXTRA_PAYPAL_ENVIRONMENT,
                CONFIG_ENVIRONMENT);
        intent.putExtra(PaymentActivity.EXTRA_CLIENT_ID, CONFIG_CLIENT_ID);
        intent.putExtra(PaymentActivity.EXTRA_RECEIVER_EMAIL,
                CONFIG_RECEIVER_EMAIL);

        startService(intent);


        Intent payIntent = getIntent();

        try{

            onBuyPressed("1.75", "Exam Fees", "USD");

        }catch(Exception e){

            e.printStackTrace();

            finish();
//            TextMessagePopup.showTextMessage(mContext,"Failed to open Paypal page");
            Toast.makeText(mContext,"Failed to open Paypal page",Toast.LENGTH_LONG).show();

        }

    }


    public void onBuyPressed(String amount,String nameOfPayment,String currencycode) {

//        PayPalPayment thingToBuy = new PayPalPayment(new BigDecimal("1.75"), "USD", "Admission fees");

        PayPalPayment thingToBuy = new PayPalPayment(new BigDecimal(amount), currencycode, nameOfPayment);

        Intent intent = new Intent(this, PaymentActivity.class);

        intent.putExtra(PaymentActivity.EXTRA_PAYPAL_ENVIRONMENT, CONFIG_ENVIRONMENT);
        intent.putExtra(PaymentActivity.EXTRA_CLIENT_ID, CONFIG_CLIENT_ID);
        intent.putExtra(PaymentActivity.EXTRA_RECEIVER_EMAIL, CONFIG_RECEIVER_EMAIL);

        // It's important to repeat the clientId here so that the SDK has it if Android restarts your
        // app midway through the payment UI flow.
//        intent.putExtra(PaymentActivity.EXTRA_CLIENT_ID, "AXJVRhC7DvWS3Br2NNUbF7twDnB-20ggOOx2huKOb2FwH9IG3oiFqtoafze4");

        intent.putExtra(PaymentActivity.EXTRA_CLIENT_ID, CONFIG_CLIENT_ID);
        intent.putExtra(PaymentActivity.EXTRA_PAYER_ID, "your-customer-id-in-your-system");
        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, thingToBuy);

        startActivityForResult(intent, 0);

    }


    @Override
    protected void onActivityResult (int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            PaymentConfirmation confirm = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
            if (confirm != null) {

                try {

                    Log.i("paymentExample", confirm.toJSONObject().toString());

                    JSONObject jsonObj = new JSONObject(confirm.toJSONObject().toString());

                    JSONObject clientJsonObj =  jsonObj.getJSONObject("client");
                    JSONObject paymentJsonObj =  jsonObj.getJSONObject("payment");
                    JSONObject proofofpaymentJsonObj =  jsonObj.getJSONObject("proof_of_payment");
                    JSONObject adaptive_payment = proofofpaymentJsonObj.getJSONObject("adaptive_payment");

                } catch (JSONException e) {

                    Log.e("paymentExample", "an extremely unlikely failure occurred: ", e);

//                    TextMessagePopup.showTextMessage(mContext,"an extremely unlikely failure occurred");
                    finish();

                } catch (Exception e) {

                    Log.e("paymentExample", "an extremely unlikely failure occurred: ", e);
//                    TextMessagePopup.showTextMessage(mContext,"an extremely unlikely failure occurred");
                    finish();
//
                }
            }

        }
        else if (resultCode == Activity.RESULT_CANCELED) {

            Log.i("paymentExample", "The user canceled.");
//            TextMessagePopup.showTextMessage(mContext,"The user canceled.");
            finish();

        }
        else if (resultCode == PaymentActivity.RESULT_PAYMENT_INVALID) {

            Log.i("paymentExample", "An invalid payment was submitted. Please see the docs.");
//            TextMessagePopup.showTextMessage(mContext,"An invalid payment was submitted");
            finish();

        }

    }


    ProgressDialog progress;
    public void showProgress() {
        try {
            if (progress == null)
                progress = new ProgressDialog(this);
            progress.setMessage("Please Wait..");
            progress.setCancelable(false);
            progress.show();
        } catch (Exception e) {
            e.printStackTrace();
            try {
                progress = new ProgressDialog(this);
                progress.setMessage("Please Wait..");
                progress.setCancelable(false);
                progress.show();
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }

    }
    public void hideProgress() {
        if (progress != null) {
            progress.dismiss();
        }
    }


    public class paymentDetail{

        public String student_id;
        public String amount;
        public String admission_id;
        public String course_id;

    }

    @Override
    public void onDestroy() {

        stopService(new Intent(this, PayPalService.class));
        super.onDestroy();

    }
}
