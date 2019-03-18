package tuni.tuukka.google;

import android.accounts.AccountManager;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.services.drive.DriveScopes;

public class AccountAuthorization {
    public static void authorize(Activity activity, GoogleAccountCredential credential) {
        activity.runOnUiThread(() -> {
            AccountManager am = AccountManager.get(activity);
            Bundle options = new Bundle();
            am.getAuthToken(credential.getSelectedAccount(), "oauth2:" + DriveScopes.DRIVE,
                    options, activity, new TokenAcquired(activity),
                    new Handler(message -> {
                        Log.d("tuksu", "ErrorHandler: " + message);
                        return true;
                    }));
        });
    }
}
