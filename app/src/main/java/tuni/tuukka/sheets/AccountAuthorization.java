package tuni.tuukka.sheets;

import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.services.sheets.v4.SheetsScopes;

public class AccountAuthorization {
    public static void authorize(Activity activity, GoogleAccountCredential credential) {
        activity.runOnUiThread(() -> {
            AccountManager am = AccountManager.get(activity);
            Bundle options = new Bundle();
            am.getAuthToken(credential.getSelectedAccount(), "oauth2:" + SheetsScopes.SPREADSHEETS,
                    options, activity, new TokenAcquired(activity),
                    new Handler(message -> {
                        Log.d("tuksu", "ErrorHandler: " + message);
                        return true;
                    }));
        });
    }
}
