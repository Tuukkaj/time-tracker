package tuni.tuukka.activity_helper;

import android.app.Activity;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;

import java.util.List;

import tuni.tuukka.google.AccountAuthorization;
import tuni.tuukka.google.SheetApi;

public class SheetApiHelper {
    public static SheetApi.ReadSheetInterface interfaceReadSheet(Activity activity, GoogleAccountCredential credential) {
        return new SheetApi.ReadSheetInterface() {
            @Override
            public void onFail() {
                AccountAuthorization.authorize(activity, credential);
            }

            @Override
            public void onSuccess(List<List<Object>> values) {
                System.out.println(values);
            }
        };
    }
}
