package tuni.tuukka.google;

import android.os.AsyncTask;

import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
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
                    System.out.println("1");
                    checkReady.onFail();
                } catch (GeneralSecurityException e) {
                    e.printStackTrace();
                    System.out.println("2");
                    checkReady.onFail();
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("3");
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
                    System.out.println("1");
                    e.printStackTrace();
                    folderInterface.onError();
                } catch (GeneralSecurityException e) {
                    System.out.println("2");
                    e.printStackTrace();
                    folderInterface.onError();
                }
                return null;
            }
        }.execute();
    }

    public static void createNewSheet(String name, CheckFoldersInterface doAfter) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    Drive drive = DriveService.createDriveService(Token.getToken().get());
                    List<File> files = drive.files().list().setQ("name = 'time-tracker' and mimeType = 'application/vnd.google-apps.folder'").execute().getFiles();
                    File folder = null;
                    for (File file: files) {
                        if(file.getName().equalsIgnoreCase("time-tracker")) {
                            folder = file;
                            break;
                        }
                    }

                    if(folder != null) {
                        File toBeCreated = new File();
                        toBeCreated.setParents(Collections.singletonList(folder.getId()));
                        toBeCreated.setMimeType("application/vnd.google-apps.spreadsheet");
                        toBeCreated.setName("time-tracker-"+name);
                        File createdFile = drive.files().create(toBeCreated).execute();
                        System.out.println(createdFile.getName());
                    } else {
                        doAfter.onNoFolderFound();
                    }

                } catch (IOException e) {
                    doAfter.onFail();
                    e.printStackTrace();
                } catch (GeneralSecurityException e) {
                    doAfter.onFail();
                    e.printStackTrace();
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
