package com.ama.luojl.downloadmanagerdemoapp;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.FileNotFoundException;

public class MainActivity extends AppCompatActivity {

    private final String TAG = "DMdemoApp";
    private final String DOWNLOAD_FOLDER_NAME = "DMDemoApp";
    private final String DOWNLOAD_FILE_NAME = "testFile_1MB";

    private final String testURL = "http://www.sample-videos.com/text/Sample-text-file-1000kb.txt";

    private DownloadManager mDM;
    private DownloadReceiver mDownloadReceiver;

    private Button mDownloadBtn;
    private TextView mPromptTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDM = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);

        mDownloadBtn = (Button) findViewById(R.id.download_btn);
        mPromptTextView = (TextView) findViewById(R.id.prompt_textView);
        initView();

        mDownloadReceiver = new DownloadReceiver();
        registerReceiver(mDownloadReceiver,
                new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    private void initView() {
        mDownloadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startDownload();
            }
        });
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mDownloadReceiver);
    }

    private void startDownload() {
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(testURL));
//        request.setDestinationInExternalPublicDir(DOWNLOAD_FOLDER_NAME, DOWNLOAD_FILE_NAME);
        mDM.enqueue(request);
    }

    private class DownloadReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            long downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            String action = intent.getAction();
            if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {
                Log.i(TAG, "Download Complete for ID: " + downloadId);
                ParcelFileDescriptor fileDescriptor;
                try {
                    fileDescriptor = mDM.openDownloadedFile(downloadId);
                    long size = fileDescriptor.getStatSize();
                    mPromptTextView.setText("File size: " + size);
                } catch (FileNotFoundException e) {
                    mPromptTextView.setText("FileNotFound");
                }

            }
        }
    }
}
