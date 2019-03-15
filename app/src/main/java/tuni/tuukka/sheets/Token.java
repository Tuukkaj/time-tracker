package tuni.tuukka.sheets;

import android.accounts.AccountManager;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import java.util.Optional;

public class Token extends Application {
    private static final String TOKEN_NAME= "token";
    private static final String PREFERENCES_NAME = "tuni.tuukka.time-tracker";
    private static Optional<String> token;
    private static boolean tokenSet = false;

    static Optional<String> getToken() {
        return token;
    }

    static void setToken(String newToken) {
        token = Optional.of(newToken);
    }

    static void saveToken(String token, Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(TOKEN_NAME, token);
        editor.apply();
    }

    static String loadToken(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        return preferences.getString(TOKEN_NAME, null);
    }
}
