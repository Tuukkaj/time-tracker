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
    private final static String APP_NAME = "time-tracker";

    public static void checkFolders(CheckFoldersInterface checkReady) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    Drive drive = DriveService.createDriveService(Token.getToken().get());
                    List<File> files = drive.files().list().setQ("name = '"+APP_NAME+"'").execute().getFiles();

                    try {
                        JSONArray array = new JSONArray(files.toString());
                        JSONObject object = array.getJSONObject(0);
                        String name = object.getString("name");
                        String folderId = object.getString("id");
                        if(name.equalsIgnoreCase(APP_NAME)) {
                            System.out.println("Name: " + name + " Id" + folderId);
                            checkReady.onSuccess(folderId);
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
                    createFolder(drive);
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

    public static void createNewSheet(String name, CreateNewSheetInterface sheetInterface) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    Drive drive = DriveService.createDriveService(Token.getToken().get());
                    List<File> files = drive.files().list().setQ(
                            "name contains '"+APP_NAME+"' and mimeType = 'application/vnd.google-apps.folder' or mimeType = 'application/vnd.google-apps.spreadsheet'"
                    ).execute().getFiles();

                    File folder = null;
                    boolean duplicateFiles = false;

                    for (File file: files) {
                        if(file.getName().equalsIgnoreCase(APP_NAME) &&
                        file.getMimeType().equalsIgnoreCase("application/vnd.google-apps.folder")) {
                            folder = file;
                            break;
                        }
                    }

                    for(File file: files) {
                        if(file.getName().equals(APP_NAME+"-"+name)
                                && file.getMimeType()
                                .equalsIgnoreCase("application/vnd.google-apps.spreadsheet")) {
                            duplicateFiles = true;
                        }
                    }
                    if(duplicateFiles) {
                        System.out.println("DUPLICATE FILES FOUND");
                        sheetInterface.onFileAlreadyCreated();
                    } else if(folder != null) {
                        File file = createSheet(drive, name, folder.getId());
                        sheetInterface.onSuccess(file);
                    } else {
                        File newFolder = createFolder(drive);
                        File file = createSheet(drive,name,newFolder.getId());
                        sheetInterface.onSuccess(file);
                    }

                } catch (IOException e) {
                    sheetInterface.onFail();
                    e.printStackTrace();
                } catch (GeneralSecurityException e) {
                    sheetInterface.onFail();
                    e.printStackTrace();
                }
                return null;
            }
        }.execute();
    }

    private static File createSheet(Drive drive, String name, String parentId) throws IOException{
        File toBeCreated = new File();
        toBeCreated.setParents(Collections.singletonList(parentId));
        toBeCreated.setMimeType("application/vnd.google-apps.spreadsheet");
        toBeCreated.setName("time-tracker-"+name);
        return drive.files().create(toBeCreated).execute();
    }

    private static File createFolder(Drive drive) throws IOException{
        File folder = new File();
        folder.setName(APP_NAME);
        folder.setMimeType("application/vnd.google-apps.folder");
        return drive.files().create(folder).setFields("id").execute();
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

    public interface CreateNewSheetInterface extends DoAfter<File> {
        void onFileAlreadyCreated();
    }

    public interface ListFilesInterface {
        void useFileList(List<File> file);
    }

    public interface CheckFoldersInterface extends DoAfter<String> {
        void onNoFolderFound();
    }
}
