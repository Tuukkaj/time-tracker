package tuni.tuukka.activity_helper;

import android.app.Activity;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.services.sheets.v4.model.ValueRange;

import java.util.List;

import tuni.tuukka.google.AccountAuthorization;
import tuni.tuukka.google.SheetApi;

public class SheetApiHelper {
    public static SheetApi.ReadRangeInterface interfaceReadRange(Activity activity, GoogleAccountCredential credential) {
        return new SheetApi.ReadRangeInterface() {
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

    public static SheetApi.ReadRangesInterface interfaceReadRanges(Activity activity, GoogleAccountCredential credential) {
        return new SheetApi.ReadRangesInterface() {
            @Override
            public void onFail() {
                AccountAuthorization.authorize(activity,credential);
            }

            @Override
            public void onSuccess(List<ValueRange> values) {
                System.out.println(values);
            }
        };
    }
}
