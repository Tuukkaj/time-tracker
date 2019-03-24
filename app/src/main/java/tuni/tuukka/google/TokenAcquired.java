package tuni.tuukka.google;

import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.content.Context;
import android.os.Bundle;

import java.io.IOException;

/**
 * Used to set token when user loads AccountManager gets token from Google.
 */
public class TokenAcquired implements AccountManagerCallback<Bundle> {
    /**
     * Context used for saving token.
     */
    Context context;

    /**
     * Sets parameter to class variable.
     * @param context Context used for saving token.
     */
    public TokenAcquired(Context context) {
        this.context = context;
    }

    /**
     * Saves token when acquired from AccountManager.
     * @param accountManagerFuture Used for getting token.
     */
    @Override
    public void run(AccountManagerFuture<Bundle> accountManagerFuture) {
        try {
            Bundle bundle = accountManagerFuture.getResult();
            String token = bundle.getString(AccountManager.KEY_AUTHTOKEN);
            Token.saveToken(token, context);

        } catch (AuthenticatorException e) {
            e.printStackTrace();

        } catch (IOException e) {
            e.printStackTrace();

        } catch (OperationCanceledException e) {
            e.printStackTrace();
        }
    }
}
