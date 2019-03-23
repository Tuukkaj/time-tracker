package tuni.tuukka.google;

import android.accounts.AccountManager;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.services.drive.DriveScopes;
/**
 * @author      Tuukka Juusela <tuukka.juusela@tuni.fi>
 * @since       1.8
 *
 * Asks users permissions for using scope DriveScopes.DRIVE in Google APIs.
 */
public class AccountAuthorization {
    /**
     * Authorizes given credential to use scope DriveScopes.DRIVE. Does this in given activitys
     * UI thread
     * @param activity Activity to ask permissions
     * @param credential Credentials to authorize
     */
    public static void authorize(Activity activity, GoogleAccountCredential credential) {
        activity.runOnUiThread(() -> {
            AccountManager am = AccountManager.get(activity);
            Bundle options = new Bundle();
            am.getAuthToken(credential.getSelectedAccount(), "oauth2:" + DriveScopes.DRIVE,
                    options, activity, new TokenAcquired(activity),
                    new Handler(message -> {
                        System.out.println(message);
                        return true;
                    }));
        });
    }
}
