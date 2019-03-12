package tuni.tuukka.sheets;

import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.os.Bundle;
import android.util.Log;

import java.io.IOException;

public class TokenAcquired implements AccountManagerCallback<Bundle> {
    @Override
    public void run(AccountManagerFuture<Bundle> accountManagerFuture) {
        Log.d("tuksu", "run: ACCOUNTMANAGERCALLBACK");

        try {
            Log.d("tuksu", "run: " + accountManagerFuture.isDone() + accountManagerFuture.isCancelled());

            Bundle bundle = accountManagerFuture.getResult();
            String token = bundle.getString(AccountManager.KEY_AUTHTOKEN);
            Log.d("tuksu", "run: GOT KEY" + token);
        } catch (AuthenticatorException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (OperationCanceledException e) {
            e.printStackTrace();
        }


    }
}
