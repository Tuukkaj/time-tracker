package tuni.tuukka;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

public class GoogleAcccountHelper {
    public static void login(Context context) {
        Log.d("tuksu", "AVAIVABLE: " +isGoogleServicesAvaivable(context));
    }

    private static boolean isGoogleServicesAvaivable(Context context) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();

        final int connectionStatusCode = apiAvailability.isGooglePlayServicesAvailable(context);

        return connectionStatusCode == ConnectionResult.SUCCESS;
    }
}
