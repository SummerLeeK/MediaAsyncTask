package com.example.lee.myapplication;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.LoadBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.edit_cmd)
    EditText editCmd;
    @BindView(R.id.btn_do)
    Button btnDo;
    @BindView(R.id.command_output)
    LinearLayout commandOutput;

    private static final String TAG = MainActivity.class.getSimpleName();

    private FFmpeg fFmpeg;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        fFmpeg = FFmpeg.getInstance(this);

        loadBinary();
        initUI();

    }


    private void loadBinary() {
        try {
            fFmpeg.loadBinary(new LoadBinaryResponseHandler() {
                @Override
                public void onFailure() {
                    showUnsupportedExceptionDialog();
                }

            });
        } catch (FFmpegNotSupportedException e) {
            showUnsupportedExceptionDialog();
        }
    }

    private void showUnsupportedExceptionDialog() {
        new AlertDialog.Builder(MainActivity.this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("设备不支持")
                .setMessage("设备不支持")
                .setCancelable(false)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        MainActivity.this.finish();
                    }
                })
                .create()
                .show();

    }

    private void initUI() {
        btnDo.setOnClickListener(this);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(null);
        progressDialog.setCanceledOnTouchOutside(false);
    }

    private void execFFmpegBinary(final String[] command) {
        try {
            fFmpeg.execute(command, new ExecuteBinaryResponseHandler() {
                @Override
                public void onFailure(String s) {
                    addTextViewToLayout("FAILED with output : " + s);
                }

                @Override
                public void onSuccess(String s) {
                    addTextViewToLayout("SUCCESS with output : " + s);
                }

                @Override
                public void onProgress(String s) {
                    Log.d(TAG, "Progress command : ffmpeg " + s);
//                    addTextViewToLayout("progress : " + s);
                    progressDialog.setMessage("Processing\n" + s);
                }

                @Override
                public void onStart() {
                    commandOutput.removeAllViews();

                    Log.d(TAG, "Started command : ffmpeg " + command);
                    progressDialog.setMessage("Processing...");
                    progressDialog.show();
                }

                @Override
                public void onFinish() {
                    Log.d(TAG, "Finished command : ffmpeg " + command);
                    progressDialog.dismiss();
                }
            });
        } catch (FFmpegCommandAlreadyRunningException e) {
            // do nothing for now
        }
    }

    private void addTextViewToLayout(String text) {
        TextView textView = new TextView(MainActivity.this);
        textView.setText(text);
        commandOutput.addView(textView);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_do:
                String cmd = editCmd.getText().toString();
                String[] command = cmd.split(" ");
                if (command.length != 0) {
                    execFFmpegBinary(command);
                } else {
                    Toast.makeText(MainActivity.this, "空命令~", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }
}
