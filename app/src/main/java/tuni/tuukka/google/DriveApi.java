package tuni.tuukka.google;

import android.os.AsyncTask;

import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

public class DriveApi {
    public static void checkFolders(CheckFoldersInterface checkReady, String folderName) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    Drive drive = DriveService.createDriveService(Token.getToken().get());
                    List<File> files = drive.files().list().setQ("name = '"+folderName+"'").execute().getFiles();

                    try {
                        JSONArray array = new JSONArray(files.toString());
                        JSONObject object = array.getJSONObject(0);
                        String name = object.getString("name");
                        String folderId = object.getString("id");
                        if(name.equalsIgnoreCase(folderName)) {
                            System.out.println("Name: " + name + " Id" + folderId);
                            checkReady.doAfter(folderId);
                        } else {
                            checkReady.onNoFolderFound();
                        }
                    } catch (JSONException e) {
                        checkReady.onNoFolderFound();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    checkReady.onFail();
                } catch (GeneralSecurityException e) {
                    e.printStackTrace();
                    checkReady.onFail();
                } catch (Exception e) {
                    e.printStackTrace();
                    checkReady.onFail();
                }
                return null;
            }
        }.execute();
    }

    public static void createNewFolder(String name, CreateNewFolderInterface folderInterface) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    Drive drive = DriveService.createDriveService(Token.getToken().get());
                    File folder = new File();
                    folder.setName(name);
                    folder.setMimeType("application/vnd.google-apps.folder");
                    File createdFolder = drive.files().create(folder).setFields("id").execute();
                    System.out.println(createdFolder.getId());
                } catch (IOException e) {
                    e.printStackTrace();
                    folderInterface.onError();
                } catch (GeneralSecurityException e) {
                    e.printStackTrace();
                    folderInterface.onError();
                }
                return null;
            }
        }.execute();
    }

    public static void listFiles(ListFilesInterface time) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    Drive drive = DriveService.createDriveService(Token.getToken().get());
                    List<File> files = drive.files().list().setQ("name contains 'time-tracker' and mimeType = 'application/vnd.google-apps.spreadsheet'").execute().getFiles();
                    time.useFileList(files);
                }catch (IOException e) {
                    e.printStackTrace();
                } catch (GeneralSecurityException e) {
                    e.printStackTrace();
                }

                return null;
            }
        }.execute();
    }

    public interface ListFilesInterface {
        void useFileList(List<File> file);
    }

    public interface CheckFoldersInterface {
        void doAfter(String folderId);
        void onFail();
        void onNoFolderFound();
    }

    public interface CreateNewFolderInterface {
        void onError();
    }
}
