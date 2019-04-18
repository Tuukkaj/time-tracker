package tuni.tuukka.activities;

import android.Manifest;
import android.accounts.AccountManager;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
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
import tuni.tuukka.activity_helper.LoadingScreenHelper;
import tuni.tuukka.activity_helper.SheetDataAdapter;
import tuni.tuukka.google.AccountAuthorization;
import tuni.tuukka.google.DriveApi;
import tuni.tuukka.google.Token;

/**
 * @author      Tuukka Juusela <tuukka.juusela@tuni.fi>
 * @version     20190323
 * @since       1.8
 *
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
        createNotificationChannel();
        setContentView(R.layout.activity_authorization);
        Set<String> scopes = DriveScopes.all();
        credential = GoogleAccountCredential.usingOAuth2(getApplicationContext(), scopes).setBackOff(new ExponentialBackOff());
        login();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setContentView(R.layout.activity_authorization);
        login();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.authorization_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_logout: {
                System.out.println("logout");
                break;
            }

            case R.id.menu_about: {
                System.out.println("Menu");
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Calls SheetApi or DriveApi based on what button was clicked.
     * !Placeholder, will be changed in later releases!
     *
     * @param v view that was clicked.
     */
    public void buttonClick(View v) {
        Optional<String> token = Token.getToken();
        switch (v.getId()) {
            case R.id.authorization_btn_authorize:
                login();
                break;
            case R.id.authorization_btn_input:
                if (token.isPresent()) {
                    LoadingScreenHelper.start(this);
                    DriveApi.listFiles(DriveApiHelper.interfaceListFiles(this,credential, SheetDataAdapter.MODE_MANUAL_INPUT));
                } else {
                    AccountAuthorization.authorize(this,credential);
                }
                break;

            case R.id.authorization_btn_show: {
                if(token.isPresent()) {
                    LoadingScreenHelper.start(this);
                    DriveApi.listFiles(DriveApiHelper.interfaceListFiles(this,credential, SheetDataAdapter.MODE_SHOW_TIME));
                } else{
                    AccountAuthorization.authorize(this,credential);
                }

                break;
            }
        }
    }

    private void checkTimer() {
       SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
       String id = preferences.getString(Timer.PREF_SHEETID, "");
       String name = preferences.getString(Timer.PREF_SHEETNAME,"");

       layoutHandleActiveTimer(!id.isEmpty() && !name.isEmpty());
    }

    @SuppressLint("RestrictedApi")
    private void layoutHandleActiveTimer(boolean on) {
        FloatingActionButton btnStart = (FloatingActionButton) findViewById(R.id.authorization_btn_timer);
        TextView txtTimer = (TextView) findViewById(R.id.authorization_txt_timer);

        if (on) {
            txtTimer.setText("Active timer");
            btnStart.setBackgroundTintList(getColorStateList(R.color.colorAccent));
            btnStart.setAnimation(AnimationUtils.loadAnimation(this, R.anim.rotation));

            btnStart.setOnClickListener(view -> {
                Optional<String> token = Token.getToken();
                if(token.isPresent()) {
                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
                    Intent i = new Intent(this, Timer.class);
                    i.putExtra(Timer.EXTRA_SHEETNAME, prefs.getString(Timer.PREF_SHEETNAME, null));
                    i.putExtra(Timer.EXTRA_SHEETID, prefs.getString(Timer.PREF_SHEETID,null));
                    startActivity(i);
                } else {
                    AccountAuthorization.authorize(this,credential);
                }
            });

        } else {
            btnStart.setBackgroundTintList(getColorStateList(R.color.colorPrimaryDark));

            btnStart.setOnClickListener(view -> {
                Optional<String> token = Token.getToken();
                if (token.isPresent()) {
                    LoadingScreenHelper.start(this);
                    DriveApi.listFiles(DriveApiHelper.interfaceListFiles(this,credential, SheetDataAdapter.MODE_TIMER));

                } else {
                    AccountAuthorization.authorize(this,credential);
                }
            });
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
        }

        AccountAuthorization.authorize(this, credential);
        setContentView(R.layout.activity_authorization_authorized);
        setProfileName(credential.getSelectedAccountName());
        checkTimer();
    }

    private void setProfileName(String name) {
        ((TextView) findViewById(R.id.accountName)).setText(name);
        ((ImageView) findViewById(R.id.accountIcon)).setVisibility(View.VISIBLE);
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


    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "time-tracker";
            String description = "Used to inform user about elapsed work time";
            NotificationChannel channel = new NotificationChannel("time-tracker", name, NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public static Intent toParentIntent(Activity activity) {
        Intent i = new Intent(activity, Authorization.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        return i;
    }
}
