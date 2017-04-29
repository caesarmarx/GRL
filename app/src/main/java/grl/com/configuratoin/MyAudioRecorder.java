package grl.com.configuratoin;

/*
 * Copyright (C) 2009 Rhodri Karim (rk395)
 * Copyright (C) 2012 Daniel Thomas (drt24)
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Environment;
import android.os.StatFs;
import android.text.format.Time;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * An activity that allows the user to record full-quality audio at a variety of sample rates, and
 * save to a WAV file
 *
 * @author Rhodri Karim
 *
 */
public class MyAudioRecorder {

    private Activity context;
    private static final int WAV_HEADER_LENGTH = 44;
    private static final int NOTICE_RECORD = 0;
    private static final String EXTENSION = ".wav";

    private String filename;

    private AlertDialog dialog;

    private File outFile;

    private boolean isListening;

    /**
     * The sample rate at which we'll record, and save, the WAV file.
     */
    public int sampleRate = 16000;
    private NotificationManager notificationManager;

    public MyAudioRecorder(Activity context) {
        this.context = context;
        this.filename = getVoiceFileName();
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    public String getVoiceFileName() {
        Time var2 = new Time();
        var2.setToNow();
        return "record" + EXTENSION;
    }


    public String getVoiceFilePath(String fileName) {
        return FileUtils.getSendDir(context) + "/" + fileName;
    }

    /**
     * End the recording, saving and finalising the file
     */
    public String endRecording() {
        isListening = false;
        Thread thread = new Thread() {
            @Override
            public void run() {

                if (outFile != null) {
                    appendHeader(outFile);

                    Intent scanWav = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                    scanWav.setData(Uri.fromFile(outFile));
                    context.sendBroadcast(scanWav);

                    outFile = null;
                    notificationManager.cancel(NOTICE_RECORD);

                }
            }
        };
        thread.start();
        return getVoiceFilePath(filename);
    }

    /**
     * Begin the recording after verifying that we can, if we can't then tell the user and return
     */
    public void beginRecording() {

        // check that there's somewhere to record to
        String state = Environment.getExternalStorageState();
        Log.d("FS State", state);
        if (state.equals(Environment.MEDIA_SHARED)) {
            showDialog("Unmount USB storage", "Please unmount USB storage before starting to record.");
            return;
        } else if (state.equals(Environment.MEDIA_REMOVED)) {
            showDialog("Insert SD Card", "Please insert an SD card. You need something to record onto.");
            return;
        }

        // check that the user's supplied a file name
        if (filename.equals("") || filename == null) {
            showDialog("Enter a file name", "Please give your file a name. It's the least it deserves.");
            return;
        }
        if (!filename.endsWith(".wav")) {
            filename += ".wav";
        }

        // ask if file should be overwritten
        File userFile = new File(getVoiceFilePath(filename));
        startRecording();
    }


    public void startRecording() {
        isListening = true;
        Thread s = new Thread(new SpaceCheck());
        s.start();
        Thread t = new Thread(new Capture());
        t.start();
    }


    /**
     * Monitors the available SD card space while recording.
     *
     * @author Rhodri Karim
     *
     */
    private class SpaceCheck implements Runnable {
        @Override
        public void run() {
            String sdDirectory = Environment.getExternalStorageDirectory().toString();
            StatFs stats = new StatFs(sdDirectory);
            while (isListening) {
                stats.restat(sdDirectory);
                final long freeBytes = (long) stats.getAvailableBlocks() * (long) stats.getBlockSize();
                if (freeBytes < 5242880) { // less than 5MB remaining

                    return;
                }

                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                }
            }
        }
    }

    /**
     * Capture raw audio data from the hardware and saves it to a buffer in the enclosing class.
     *
     * @author Rhodri Karim
     *
     */
    private class Capture implements Runnable {

        private final int channelConfig = AudioFormat.CHANNEL_CONFIGURATION_MONO;
        private final int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;

        // the actual output format is big-endian, signed

        @Override
        public void run() {
            // We're important...
            android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);

            // Allocate Recorder and Start Recording...
            int minBufferSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioEncoding);
            if (AudioRecord.ERROR_BAD_VALUE == minBufferSize || AudioRecord.ERROR == minBufferSize){

                return;
            }
            int bufferSize = 2 * minBufferSize;
            AudioRecord recordInstance =
                    new AudioRecord(MediaRecorder.AudioSource.MIC, sampleRate, channelConfig, audioEncoding,
                            bufferSize);
            if (recordInstance.getState() != AudioRecord.STATE_INITIALIZED) {

                return;
            }

            byte[] tempBuffer = new byte[bufferSize];

            outFile = new File(getVoiceFilePath(filename));
            if (outFile.exists())
                outFile.delete();

            FileOutputStream outStream = null;
            try {
                outFile.createNewFile();
                outStream = new FileOutputStream(outFile);
                outStream.write(createHeader(0));// Write a dummy header for a file of length 0 to get updated later
            } catch (Exception e) {

                return;
            }

            recordInstance.startRecording();

            try {
                while (isListening) {
                    recordInstance.read(tempBuffer, 0, bufferSize);
                    outStream.write(tempBuffer);
                }
            } catch (final IOException e) {

            } catch (OutOfMemoryError om) {

            }

            // we're done recording
            Log.d("Capture", "Stopping recording");
            recordInstance.stop();
            try {
                outStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void showDialog(String title, String message){
        dialog.setTitle(title);
        dialog.setMessage(message);
        dialog.show();
    }

    /**
     * Appends a WAV header to a file containing raw audio data. Uses different strategies depending
     * on amount of free disk space.
     *
     * @param file The file containing 16-bit little-endian PCM data.
     */
    public void appendHeader(File file) {

        int bytesLength = (int) file.length();
        byte[] header = createHeader(bytesLength - WAV_HEADER_LENGTH);

        try {
            RandomAccessFile ramFile = new RandomAccessFile(file, "rw");
            ramFile.seek(0);
            ramFile.write(header);
            ramFile.close();
        } catch (FileNotFoundException e) {
            Log.e("Hertz", "Tried to append header to invalid file: " + e.getLocalizedMessage());
            return;
        } catch (IOException e) {
            Log.e("Hertz", "IO Error during header append: " + e.getLocalizedMessage());
            return;
        }

    }


    public byte[] createHeader(int bytesLength) {

        int totalLength = bytesLength + 4 + 24 + 8;
        byte[] lengthData = intToBytes(totalLength);
        byte[] samplesLength = intToBytes(bytesLength);
        byte[] sampleRateBytes = intToBytes(this.sampleRate);
        byte[] bytesPerSecond = intToBytes(this.sampleRate * 2);

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            out.write(new byte[] {'R', 'I', 'F', 'F'});
            out.write(lengthData);
            out.write(new byte[] {'W', 'A', 'V', 'E'});

            out.write(new byte[] {'f', 'm', 't', ' '});
            out.write(new byte[] {0x10, 0x00, 0x00, 0x00}); // 16 bit chunks
            out.write(new byte[] {0x01, 0x00, 0x01, 0x00}); // mono
            out.write(sampleRateBytes); // sampling rate
            out.write(bytesPerSecond); // bytes per second
            out.write(new byte[] {0x02, 0x00, 0x10, 0x00}); // 2 bytes per sample

            out.write(new byte[] {'d', 'a', 't', 'a'});
            out.write(samplesLength);
        } catch (IOException e) {
            Log.e("Create WAV", e.getMessage());
        }

        return out.toByteArray();
    }

    /**
     * Turns an integer into its little-endian four-byte representation
     *
     * @param in The integer to be converted
     * @return The bytes representing this integer
     */
    public static byte[] intToBytes(int in) {
        byte[] bytes = new byte[4];
        for (int i = 0; i < 4; i++) {
            bytes[i] = (byte) ((in >>> i * 8) & 0xFF);
        }
        return bytes;
    }


}