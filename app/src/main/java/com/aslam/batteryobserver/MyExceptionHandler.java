package com.aslam.batteryobserver;

import android.app.Activity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MyExceptionHandler {

    private static boolean isInit = false;

    public static void uncaughtException(final Activity activity) {

        if (!isInit) {
            Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
                @Override
                public void uncaughtException(Thread paramThread, Throwable paramThrowable) {

                    try {

                        String cTime = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss", Locale.getDefault()).format(new Date());
                        String sFolder = "/sdcard/my_logs";
                        File folder = new File(sFolder);
                        if (!folder.exists()) {
                            folder.mkdir();
                        }

                        File file = new File(sFolder, "my_logs");
                        FileOutputStream stream = new FileOutputStream(file, true);

                        String msg = "\n ------------------------- START -------------------------";
                        msg += "\n Class: " + paramThrowable.getStackTrace()[0].getClassName();
                        msg += "\n File: " + paramThrowable.getStackTrace()[0].getFileName();
                        msg += "\n Method: " + paramThrowable.getStackTrace()[0].getMethodName();
                        msg += "\n Line: " + paramThrowable.getStackTrace()[0].getLineNumber();
                        msg += "\n Time: " + cTime;
                        msg += "\n Msg: " + paramThrowable.getMessage();

                        // Logcat
                        String dLogcat = getLogs();
                        String sLogFolder = sFolder + "/logs";
                        File logFolder = new File(sLogFolder);
                        if (!logFolder.exists()) {
                            logFolder.mkdir();
                        }
                        File logFile = new File(sLogFolder, "log_" + cTime);
                        FileOutputStream logStream = new FileOutputStream(logFile, true);
                        logStream.write(dLogcat.getBytes());
                        logStream.close();

                        stream.write(msg.getBytes());
                        stream.close();

                        activity.finish();
                        System.exit(0);

                    } catch (FileNotFoundException e) {
                    } catch (IOException e) {
                    }
                }
            });
            isInit = true;
        }
    }

    private static String getLogs() {
        try {
            Process process = Runtime.getRuntime().exec("logcat -d");
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder log = new StringBuilder();
            String line = "";
            while ((line = bufferedReader.readLine()) != null) {
                if (line.contains(BuildConfig.APPLICATION_ID))
                    log.append(line);
            }
            return log.toString();
        } catch (IOException e) {
        }
        return null;
    }

    public static void storeNote(String note) {

        try {

            // note += "\n ------------------------- END ------------------------- \n";
            File file = new File("/sdcard/my_logs/notes", "note");

            if (!file.exists()) {
                file.getParentFile().mkdirs();
            }

            FileOutputStream logStream = new FileOutputStream(file, true);
            logStream.write(note.getBytes());
            logStream.close();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}

