package ru.mirea.abdullin.mireaproject.ui;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

import ru.mirea.abdullin.mireaproject.MainActivity;
import ru.mirea.abdullin.mireaproject.R;


public class VoiceRecorderFragment extends Fragment {
    private static final String TAG_AUDIO = MainActivity.class.getSimpleName();
    final String TAG = VoiceRecorderFragment.class.getSimpleName();
    private static final int REQUEST_CODE_PERMISSION = 100;
    private String[] PERMISSIONS = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO
    };
    private boolean isWorkAudio;
    private Button starVoiceRecordButton;
    private Button stopVoiceRecordButton;
    private MediaRecorder mediaRecorder;
    private File audioFile;
    Button buttonPlayRecord;
    Button buttonStopRecord;

    public VoiceRecorderFragment() {
        // Required empty public constructor
    }


    public static VoiceRecorderFragment newInstance(String param1, String param2) {
        VoiceRecorderFragment fragment = new VoiceRecorderFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_voice_recorder, container, false);

        starVoiceRecordButton = view.findViewById(R.id.buttonStartVoiceRecord);
        stopVoiceRecordButton = view.findViewById(R.id.buttonStopVoiceRecord);
        buttonPlayRecord = view.findViewById(R.id.buttonPlayAudio);
        buttonStopRecord = view.findViewById(R.id.buttonStopAudio);

        starVoiceRecordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onRecordStart(view);
            }
        });

        stopVoiceRecordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onStopRecord(view);
            }
        });

        buttonPlayRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonPlayRecord(view);
            }
        });

        buttonStopRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonStopRecord(view);
            }
        });

        // ?????????????????????????? ?????????????? MediaRecorder
        mediaRecorder = new MediaRecorder();
        // ???????????????? ?????????????? ???????????????????? ???? ???????????????????? ?????????????????????? ?? ???????????????????? ???? ?????????? ????????????
        isWorkAudio = hasPermissions(getActivity(), PERMISSIONS);
        if (!isWorkAudio) {
            ActivityCompat.requestPermissions(getActivity(), PERMISSIONS, REQUEST_CODE_PERMISSION);
        }

        audioFile = new File(Environment.getExternalStorageDirectory() + "/mirea.3gp");

        return view;
    }
    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
// ???????????????????????? ???????????????? ?????????????????????? ???????????????????? ???? ???????????????????????? ???? ???????????? ???????????????????? Camera
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // Audio
        if (requestCode == REQUEST_CODE_PERMISSION) {
            // permission granted
            isWorkAudio = grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED;
        }
    }

    // ??????????????????????
    // ?????????????? ???? ???????????? ??????????
    public void onRecordStart(View view) {
        try {
            starVoiceRecordButton.setEnabled(false);
            stopVoiceRecordButton.setEnabled(true);
            stopVoiceRecordButton.requestFocus();
            startRecording();
        } catch (Exception e) {
            Log.e(TAG, "Caught io exception " + e.getMessage());
        }
    }
    // ?????????????? ???? ?????????? ????????
    public void onStopRecord(View view) {
        starVoiceRecordButton.setEnabled(true);
        stopVoiceRecordButton.setEnabled(false);
        starVoiceRecordButton.requestFocus();
        stopRecording();
        processAudioFile();
    }
    private void startRecording() throws IOException {
        // ???????????????? ?????????????????????? sd - ??????????
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            Log.d(TAG, "sd-card success");
            // ?????????? ?????????????????? ??????????
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            // ?????????? ?????????????? ????????????
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            // ?????????? ????????????
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            String path = Environment.getExternalStorageDirectory() + "/mirea.3gp";
            mediaRecorder.setOutputFile(path);
            mediaRecorder.prepare();
            mediaRecorder.start();
            Toast.makeText(getActivity(), "Recording started!", Toast.LENGTH_SHORT).show();
        }
    }
    private void stopRecording() {
        if (mediaRecorder != null) {
            Log.d(TAG, "stopRecording");
            mediaRecorder.stop();
            mediaRecorder.reset();
            mediaRecorder.release();
            Toast.makeText(getActivity(), "You are not recording right now!", Toast.LENGTH_SHORT).show();
        }
    }
    private void processAudioFile() {
        Log.d(TAG, "processAudioFile");
        ContentValues values = new ContentValues(4);
        long current = System.currentTimeMillis();
        // ?????????????????? meta ???????????? ???????????????????? ??????????
        values.put(MediaStore.Audio.Media.TITLE, "audio" + audioFile.getName());
        values.put(MediaStore.Audio.Media.DATE_ADDED, (int) (current / 1000));
        values.put(MediaStore.Audio.Media.MIME_TYPE, "audio/3gpp");
        values.put(MediaStore.Audio.Media.DATA, audioFile.getAbsolutePath());
        ContentResolver contentResolver = getActivity().getContentResolver();
        Log.d(TAG, "audioFile: " + audioFile.canRead());
        Uri baseUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Uri newUri = contentResolver.insert(baseUri, values);
        // ???????????????????? ?????????????? ?? ?????????? ??????????
        getActivity().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, newUri));
    }

    public void buttonPlayRecord(View view) {
        getActivity().startService(new Intent(getActivity(), VoiceRecorderService.class));
        Toast.makeText(getActivity(), "Listening started!", Toast.LENGTH_SHORT).show();
    }
    public void buttonStopRecord(View view) {
        getActivity().stopService(new Intent(getActivity(), VoiceRecorderService.class));
        Toast.makeText(getActivity(), "Stop listening", Toast.LENGTH_SHORT).show();
    }
}