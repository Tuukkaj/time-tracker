package tuni.tuukka.activities;

import android.Manifest;
import android.accounts.AccountManager;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.drive.DriveScopes;

import java.util.List;
import java.util.Optional;
import java.util.Set;


import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import tuni.tuukka.R;
import tuni.tuukka.activity_helper.DriveApiHelper;
import tuni.tuukka.activity_helper.SheetApiHelper;
import tuni.tuukka.google.AccountAuthorization;
import tuni.tuukka.google.DriveApi;
import tuni.tuukka.google.SheetApi;
import tuni.tuukka.google.SheetRequestsInfo;
import tuni.tuukka.google.Token;

/**
 * Handles everything related to authorization of Google Account.
 */
public class Authorization extends AppCompatActivity implements EasyPermissions.PermissionCallbacks{

    /**
     * Request code for account picker.
     */
    static final int REQUEST_ACCOUNT_PICKER = 1000;

    /**
     * Request code for authorization.
     */
    static final int REQUEST_AUTHORIZATION = 1001;

    /**
     * Request code for play services.
     */
    static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;

    /**
     * Request code for getting accounts.
     */
    static final int REQUEST_PERMISSION_GET_ACCOUNTS = 1003;

    private static final String PREF_ACCOUNT_NAME = "accountName";

    /**
     * Users credentials. Used for authorization of application and getting OAuth2 token.
     */
    GoogleAccountCredential credential;

    /**
     * Sets up users GoogleAccountCredential
     *
     * @param savedInstanceState Saved instance. Not in use.
     */
    @Override
    public void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_helper);
        Set<String> scopes = DriveScopes.all();
        credential = GoogleAccountCredential.usingOAuth2(getApplicationContext(), scopes).setBackOff(new ExponentialBackOff());
    }

    /**
     * Calls SheetApi or DriveApi based on what button was clicked.
     * !Placeholder, will be changed in later releases!
     *
     * @param v view that was clicked.
     */
    public void buttonClick(View v) {
        Optional<String> token = Token.loadToken(this);
        switch (v.getId()) {
            case R.id.getAccount:
                login();
                break;

            case R.id.getSheets:
                if (token.isPresent()) {
                    SheetApi.readRange(
                            new SheetRequestsInfo("1OUgPgh272sTLKeDw1WB54-NSQl_3QbSdIbC5AFQ3v9", SheetRequestsInfo.TIME_RANGE),
                            SheetApiHelper.interfaceReadRange(this, credential));
                } else {
                    AccountAuthorization.authorize(this,credential);
                }
                break;

            case R.id.getFiles:
                if (token.isPresent()) {
                    DriveApi.listFiles(files -> files.forEach(file -> System.out.println(file)));
                } else {
                    AccountAuthorization.authorize(this,credential);
                }
                break;

            case R.id.getFolder: {
                if(token.isPresent()) {
                    DriveApi.createNewSheet("SheetNameHere", DriveApiHelper.interfaceCreateSheet(this, credential));
                } else{
                    AccountAuthorization.authorize(this,credential);
                }

                break;
            }
        }
    }

    /**
     * Checks if:
     *  - Google Play Services are available.
     *  - GoogleAccountCredentials are okay.
     *  - Device is online
     *
     * Sets buttons visible and authorizes account when all checks are ready.
     */
    public void login() {
        if(!isGoogleServicesAvailable()) {
            acquirePlayServices();
            Log.d("tuksu", "AVAIVABLE: " + isGoogleServicesAvailable());
            return;
        } else if(credential.getSelectedAccountName() == null) {
            Log.d("tuksu", "Selected account name null" + credential.getSelectedAccountName());
            chooseAccount();
            return;

        } else if(!isDeviceOnline()) {
            Toast.makeText(this, "Connect to internet", Toast.LENGTH_SHORT).show();
            return;

        } else {
            Toast.makeText(this, "Everything works fine! Time to make connection to your Sheets", Toast.LENGTH_SHORT).show();
        }

        AccountAuthorization.authorize(this, credential);
        ((TextView) findViewById(R.id.accountName)).setText(credential.getSelectedAccountName());
        ((Button) findViewById(R.id.getSheets)).setVisibility(View.VISIBLE);
        ((Button) findViewById(R.id.getFiles)).setVisibility(View.VISIBLE);
        ((Button) findViewById(R.id.getFolder)).setVisibility(View.VISIBLE);
    }

    /**
     * Checks if user has selected account to use before. If has saved account loads it, if not
     * starts startActivityForResult()
     */
    @AfterPermissionGranted(REQUEST_PERMISSION_GET_ACCOUNTS)
    private void chooseAccount() {
        if(EasyPermissions.hasPermissions(this, Manifest.permission.GET_ACCOUNTS)) {
            String accountName = getPreferences(Context.MODE_PRIVATE).getString(PREF_ACCOUNT_NAME, null);

            if(accountName != null) {
                credential.setSelectedAccountName(accountName);
                Log.d("tuksu", "GET ACCOUNT NAME " + credential.getSelectedAccountName());
                login();
            } else {
                startActivityForResult(credential.newChooseAccountIntent(), REQUEST_ACCOUNT_PICKER);
            }
        } else {
            EasyPermissions.requestPermissions(this, "This app needs to access your Google account (via Contacts)",
                    REQUEST_PERMISSION_GET_ACCOUNTS, Manifest.permission.GET_ACCOUNTS);
        }
    }

    /**
     * Checks if Google Play Services are available.
     */
    private void acquirePlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();

        final int connectionStatusCode = apiAvailability.isGooglePlayServicesAvailable(this);

        if(apiAvailability.isUserResolvableError(connectionStatusCode)) {
            showPlayServiceErrorDialog(connectionStatusCode);
        }
    }

    /**
     * Shows error dialog if Play services are unavailable.
     * @param connectionStatusCode Status code to show in dialog.
     */
    private void showPlayServiceErrorDialog(final int connectionStatusCode) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        Dialog dialog = apiAvailability.getErrorDialog(this, connectionStatusCode, REQUEST_GOOGLE_PLAY_SERVICES);
        dialog.show();
    }

    /**
     * Checks if device is online.
     * @return True if device is online. False if not.
     */
    private boolean isDeviceOnline() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();
        return (info != null && info.isConnected());
    }

    /**
     * Checks if Play services are available.
     * @return True if Play services are available. False if not.
     */
    private boolean isGoogleServicesAvailable() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();

        final int connectionStatusCode = apiAvailability.isGooglePlayServicesAvailable(this);

        return connectionStatusCode == ConnectionResult.SUCCESS;
    }

    /**
     * Receives calls when activity for results are finished. Has switch case for Authorizations
     * request codes. If code is
     * - REQUEST_GOOGLE_PLAY_SERVICES: Creates toast for user
     * - REQUEST_ACCOUNT_PICKER: Saves account name key in preferences. Proceeds login if OK.
     * - REQUEST_AUTHORIZATION: Proceeds login if OK.
     * @param requestCode Request code for startActivityForResult
     * @param resultCode Result code to check if result is OK
     * @param data possible data from activity.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQUEST_GOOGLE_PLAY_SERVICES:
                if(requestCode != RESULT_OK) {
                    Toast.makeText(this, "Please install Google Play services from App store. Then relaunch app.", Toast.LENGTH_SHORT).show();
                } else {
                    login();
                }
                break;

            case REQUEST_ACCOUNT_PICKER:
                if(resultCode == RESULT_OK && data != null && data.getExtras() != null) {
                    String accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);

                    if(accountName != null) {
                        SharedPreferences settings = getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();

                        editor.putString(PREF_ACCOUNT_NAME, accountName);
                        editor.apply();
                        credential.setSelectedAccountName(accountName);

                        login();
                    }
                }
                break;

            case REQUEST_AUTHORIZATION:
                if(resultCode == RESULT_OK) {
                    login();
                }
                break;
        }
    }

    /**
     * Calls EasyPermissions if permissions were granted
     * @param requestCode Request code given
     * @param permissions Permissions given
     * @param grantResults Granted results
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(
                requestCode, permissions, grantResults, this);
    }

    /**
     * Does nothing when called
     * @param requestCode Request code given
     * @param perms Permissions given
     */
    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        //Do nothing
    }

    /**
     * Does nothing when called
     * @param requestCode Request code given
     * @param perms Permissions given
     */
    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        // Do nothing
    }
}
