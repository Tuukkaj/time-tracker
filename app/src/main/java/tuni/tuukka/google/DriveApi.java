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

/**
 * @author      Tuukka Juusela <tuukka.juusela@tuni.fi>
 * @version     20190323
 * @since       1.8
 *
 * Holds static methods and interfaces for calling Drive Api. All calls to Google are made in
 * AsyncTask. Interfaces are used to act accordingly to results from Google.
 */
public class DriveApi {
    /**
     * Name of app in Google Drive
     */
    private final static String APP_NAME = "time-tracker";

    /**
     * Checks if folder named APP_NAME is present in users files. If successful
     * parameter CheckFoldersInterface.onSuccess() is called with gotten file id.
     * If something goes wrong, parameter CheckFoldersInterface.onFail() is called.
     * @param checkReady Interface to react to results from Google.
     */
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

    /**
     * Creates new folder with name from APP_NAME
     * @param onFail interface to react to failure.
     */
    public static void createNewFolder(OnFail onFail) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    Drive drive = DriveService.createDriveService(Token.getToken().get());
                    createFolder(drive);
                } catch (IOException e) {
                    System.out.println("1");
                    e.printStackTrace();
                    onFail.onFail();
                } catch (GeneralSecurityException e) {
                    System.out.println("2");
                    e.printStackTrace();
                    onFail.onFail();
                }
                return null;
            }
        }.execute();
    }

    /**
     * Creates new sheet to users APP_NAME folder. If folder named APP_NAME is not present in
     * users drive, one is created. After that new sheet is created. Checks if user has duplicate
     * spreadsheets with same name. If there is, creation of sheet is halted.
     *
     * @param name Name of the to be created sheet
     * @param sheetInterface Interface to react to success, failure and if file with same name
     *                       is already created.
     */
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
                        SheetApi.appendTab(file.getId(), SheetRequestsInfo.WORK_TAB,
                                SheetRequestsInfo.CATEGORIES_TAB, sheetInterface);
                    } else {
                        File newFolder = createFolder(drive);
                        File file = createSheet(drive,name,newFolder.getId());
                        SheetApi.appendTab(file.getId(), SheetRequestsInfo.WORK_TAB,
                                SheetRequestsInfo.CATEGORIES_TAB, sheetInterface);
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

    /**
     * Creates new sheet to given parent file with given name.
     * @param drive Drive service to use to connect into users Drive.
     * @param name Name of the sheet to create.
     * @param parentId Parent folders id.
     * @return File just created.
     * @throws IOException If something goes wrong in creation, this is thrown.
     */
    private static File createSheet(Drive drive, String name, String parentId) throws IOException{
        File toBeCreated = new File();
        toBeCreated.setParents(Collections.singletonList(parentId));
        toBeCreated.setMimeType("application/vnd.google-apps.spreadsheet");
        toBeCreated.setName("time-tracker-"+name);
        return drive.files().create(toBeCreated).execute();
    }

    /**
     * Creates new folder to users drive with APP_NAME as it's name.
     * @param drive Drive service to use to connect into users Drive.
     * @return created File.
     * @throws IOException If something goes wrong in creation, this is thrown.
     */
    private static File createFolder(Drive drive) throws IOException{
        File folder = new File();
        folder.setName(APP_NAME);
        folder.setMimeType("application/vnd.google-apps.folder");
        return drive.files().create(folder).setFields("id").execute();
    }

    /**
     * Creates List<File> users spreadsheet files in Drive.
     * @param listFiles Interface to list results from Google.
     */
    public static void listFiles(DoAfter<List<File>> listFiles) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    Drive drive = DriveService.createDriveService(Token.getToken().get());
                    List<File> files = drive.files().list().setQ("name contains 'time-tracker' and mimeType = 'application/vnd.google-apps.spreadsheet'").execute().getFiles();
                    listFiles.onSuccess(files);
                }catch (IOException e) {
                    System.out.println(1);
                    listFiles.onFail();
                    e.printStackTrace();
                } catch (GeneralSecurityException e) {
                    System.out.println(2);
                    listFiles.onFail();
                    e.printStackTrace();
                }

                return null;
            }
        }.execute();
    }

    /**
     * Interface used when creating new sheet.
     */
    public interface CreateNewSheetInterface extends DoAfter<String> {
        /**
         * Called if file is already created.
         */
        void onFileAlreadyCreated();
    }


    /**
     * Interface for checking users folders.
     */
    public interface CheckFoldersInterface extends DoAfter<String> {
        /**
         * Called if no folder is found.
         */
        void onNoFolderFound();
    }
}
