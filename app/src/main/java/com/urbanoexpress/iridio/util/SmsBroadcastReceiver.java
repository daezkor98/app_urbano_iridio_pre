package com.urbanoexpress.iridio.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.Status;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SmsBroadcastReceiver extends BroadcastReceiver {

    private OTPReceiveListener listener;

    public void initOTPListener(OTPReceiveListener listener) {
        this.listener = listener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (SmsRetriever.SMS_RETRIEVED_ACTION.equals(intent.getAction())) {
            Bundle extras = intent.getExtras();
            Status status = (Status) extras.get(SmsRetriever.EXTRA_STATUS);

            switch(status.getStatusCode()) {
                case CommonStatusCodes.SUCCESS:
                    // Get SMS message contents
                    String message = (String) extras.get(SmsRetriever.EXTRA_SMS_MESSAGE);
                    // Extract one-time code from the message and complete verification
                    // by sending the code back to your server.
                    Pattern pattern = Pattern.compile("(\\d{6})");
                    Matcher matcher = pattern.matcher(message);

                    // Extract one-time code from the message and complete verification
                    String value = "";
                    if (matcher.find()) {
                        value = matcher.group(1);
                    }

                    if (listener != null) listener.onOTPReceived(value);
                    break;
                case CommonStatusCodes.TIMEOUT:
                    // Waiting for SMS timed out (5 minutes)
                    // Handle the error ...
                    if (listener != null) listener.onOTPTimeOut();
                    break;
            }
        }
    }

    public interface OTPReceiveListener {

        void onOTPReceived(String value);
        void onOTPTimeOut();
    }
}
