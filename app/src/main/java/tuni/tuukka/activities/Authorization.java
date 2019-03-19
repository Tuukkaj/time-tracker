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
import tuni.tuukka.activity_helper.HelpDriveFolder;
import tuni.tuukka.google.AccountAuthorization;
import tuni.tuukka.google.DriveApi;
import tuni.tuukka.google.SheetReader;
import tuni.tuukka.google.SheetRequestsInfo;
import tuni.tuukka.google.Token;

public class Authorization extends AppCompatActivity implements EasyPermissions.PermissionCallbacks{
    static final int REQUEST_ACCOUNT_PICKER = 1000;
    static final int REQUEST_AUTHORIZATION = 1001;
    static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    static final int REQUEST_PERMISSION_GET_ACCOUNTS = 1003;

    private static final String PREF_ACCOUNT_NAME = "accountName";

    GoogleAccountCredential credential;

    @Override
    public void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_helper);
        Set<String> scopes = DriveScopes.all();
        //scopes.add(SheetsScopes.DRIVE);
       //scopes.add(SheetsScopes.SPREADSHEETS);
        System.out.println(scopes);
        credential = GoogleAccountCredential.usingOAuth2(getApplicationContext(), scopes).setBackOff(new ExponentialBackOff());
    }

    public void buttonClick(View v) {
        Optional<String> token = Token.loadToken(this);
        switch (v.getId()) {
            case R.id.getAccount:
                login();
                break;

            case R.id.getSheets:

                if (token.isPresent()) {
                    new SheetReader(() -> AccountAuthorization.authorize(this, credential))
                            .execute(new SheetRequestsInfo(
                                    "11CrV_44G1pAWHT4SgZ3q7p7xeY-_3L16i4XugniOsqM",
                                    "Sheet1"));

                } else {
                    AccountAuthorization.authorize(this,credential);
                }
                break;

            case R.id.getFiles:
                if (token.isPresent()) {
                    DriveApi.checkFolders(HelpDriveFolder.interfaceGetFiles(this,credential), "time-tracker");
                } else {
                    AccountAuthorization.authorize(this,credential);
                }
                break;
        }
    }
    public void login() {
        if(!isGoogleServicesAvailable()) {
            acquirePlayServices();
            Log.d("tuksu", "AVAIVABLE: " + isGoogleServicesAvailable());
        } else if(credential.getSelectedAccountName() == null) {
            Log.d("tuksu", "Selected account name null" + credential.getSelectedAccountName());
            chooseAccount();
        } else if(!isDeviceOnline()) {
            Toast.makeText(this, "Connect to internet", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Everything works fine! Time to make connection to your Sheets", Toast.LENGTH_SHORT).show();
        }
        ((TextView) findViewById(R.id.accountName)).setText(credential.getSelectedAccountName());
        ((Button) findViewById(R.id.getSheets)).setVisibility(View.VISIBLE);
        ((Button) findViewById(R.id.getFiles)).setVisibility(View.VISIBLE);

    }

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

    private void acquirePlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();

        final int connectionStatusCode = apiAvailability.isGooglePlayServicesAvailable(this);

        if(apiAvailability.isUserResolvableError(connectionStatusCode)) {
            showPlayServiceErrorDialog(connectionStatusCode);
        }
    }

    private void showPlayServiceErrorDialog(final int connectionStatusCode) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        Dialog dialog = apiAvailability.getErrorDialog(this, connectionStatusCode, REQUEST_GOOGLE_PLAY_SERVICES);
        dialog.show();
    }

    //Tests

    private boolean isDeviceOnline() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();
        return (info != null && info.isConnected());
    }

    private boolean isGoogleServicesAvailable() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();

        final int connectionStatusCode = apiAvailability.isGooglePlayServicesAvailable(this);

        return connectionStatusCode == ConnectionResult.SUCCESS;
    }

    // Overridden methods
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQUEST_GOOGLE_PLAY_SERVICES:
                Log.d("tuksu", "OnActivityResult - REQUEST_GOOGLE_PLAY_SERVICES");
                if(requestCode != RESULT_OK) {
                    Toast.makeText(this, "Please install Google Play services from App store. Then relaunch app.", Toast.LENGTH_SHORT).show();
                } else {
                    login();
                }
                break;

            case REQUEST_ACCOUNT_PICKER:
                Log.d("tuksu", "OnActivityResult - REQUEST_ACCOUNT_PICKER");
                if(resultCode == RESULT_OK && data != null && data.getExtras() != null) {
                    String accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    Log.d("tuksu", "OnActivityResult - REQUEST_ACCOUNT_PICKER p2 " + accountName);
                    if(accountName != null) {
                        SharedPreferences settings = getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();

                        editor.putString(PREF_ACCOUNT_NAME, accountName);
                        editor.apply();
                        credential.setSelectedAccountName(accountName);
                        Log.d("tuksu", "OnActivityResult - REQUEST_ACCOUNT_PICKER p2 " + credential.getSelectedAccountName());
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

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(
                requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        //Do nothing
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        // Do nothing
    }
}
