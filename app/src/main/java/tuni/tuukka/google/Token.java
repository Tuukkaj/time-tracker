package tuni.tuukka.google;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import java.util.Optional;

/**
 * @author      Tuukka Juusela <tuukka.juusela@tuni.fi>
 * @version     20190324
 * @since       1.8
 *
 * Holds information of token used to access users account.
 */
public class Token extends Application {
    private static final String TOKEN_NAME= "token";
    private static final String PREFERENCES_NAME = "tuni.tuukka.time-tracker";
    private static Optional<String> token;

    /**
     * Returns token of the user.
     * @return users token.
     */
    public static Optional<String> getToken() {
        return token != null ? token : Optional.ofNullable(null);
    }

    /**
     * Sets token.
     * @param newToken token to be set.
     */
    static void setToken(String newToken) {
        token = Optional.ofNullable(newToken);
    }

    /**
     * Saves token to a file for later usage.
     * @param token Token to save.
     * @param context Context to use for saving.
     */
    static void saveToken(String token, Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(TOKEN_NAME, token);
        editor.apply();
        setToken(token);
    }

    /**
     * Loads token from files.
     * @param context Context to load token with.
     * @return Token retrieved from files.
     */
    public static Optional<String> loadToken(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        String token = preferences.getString(TOKEN_NAME, null);
        setToken(token);
        return Optional.ofNullable(token);
    }
}
