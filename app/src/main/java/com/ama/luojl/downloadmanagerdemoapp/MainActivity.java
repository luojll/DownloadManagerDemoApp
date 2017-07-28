package com.ama.luojl.downloadmanagerdemoapp;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.FileNotFoundException;
import java.util.HashSet;

public class MainActivity extends AppCompatActivity {

    private final String TAG = "DMdemoApp";
    private final String DOWNLOAD_FOLDER_NAME = "DMDemoApp";
    private final String DOWNLOAD_FILE_NAME = "testFile_1MB";

    private final String testURL = "http://www.sample-videos.com/text/Sample-text-file-1000kb.txt";

    private DownloadManager mDM;
    private DownloadReceiver mDownloadReceiver;

    private Button mDownloadBtn;
    private Button mCancelAllBtn;
    private TextView mPromptTextView;

    private HashSet<Long> mEnqueuedIDSet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDM = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);

        initView();

        mEnqueuedIDSet = new HashSet<>();

        mDownloadReceiver = new DownloadReceiver();
        registerReceiver(mDownloadReceiver,
                new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    @Override
    protected void onResume() {
        super.onResume();
        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterByStatus(DownloadManager.STATUS_RUNNING);
        Cursor cursor = mDM.query(query);
        if (cursor != null) {
            showString("running cnt: " + cursor.getCount());
        }

    }

    private void initView() {
        mDownloadBtn = (Button) findViewById(R.id.download_btn);
        mCancelAllBtn = (Button) findViewById(R.id.cancel_all_btn);

        mDownloadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startDownload();
            }
        });
        mCancelAllBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelAllDownload();
            }
        });

        mPromptTextView = (TextView) findViewById(R.id.prompt_textView);
        mPromptTextView.setMovementMethod(new ScrollingMovementMethod());
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mDownloadReceiver);
    }

    private void startDownload() {
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(testURL));
        long id = mDM.enqueue(request);
        mEnqueuedIDSet.add(id);
        showString("ID: " + id + ", enqueued");
    }

    private void cancelAllDownload() {
        for (long id : mEnqueuedIDSet) {
            mDM.remove(id);
            showString("ID: " + id + ", removed");
        }
        mEnqueuedIDSet.clear();
    }

    private class DownloadReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            long downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            String action = intent.getAction();
            if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {
                showString("ID: " + downloadId + ", download complete");
//                ParcelFileDescriptor fileDescriptor;
//                try {
//                    fileDescriptor = mDM.openDownloadedFile(downloadId);
//                    long size = fileDescriptor.getStatSize();
//                } catch (FileNotFoundException e) {
//                    mPromptTextView.setText("FileNotFound");
//                }

            }
        }
    }

    private void showString(String s) {
        mPromptTextView.append(s + "\n");
    }
}
