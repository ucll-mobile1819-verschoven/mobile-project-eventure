package com.ucll.eventure;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.dlazaro66.qrcodereaderview.QRCodeReaderView;
import com.github.sumimakito.awesomeqr.AwesomeQrRenderer;
import com.github.sumimakito.awesomeqr.RenderResult;
import com.github.sumimakito.awesomeqr.option.RenderOption;
import com.github.sumimakito.awesomeqr.option.background.StillBackground;
import com.github.sumimakito.awesomeqr.option.color.Color;
import com.github.sumimakito.awesomeqr.option.logo.Logo;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.zxing.Result;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.ucll.eventure.Data.User;
import com.ucll.eventure.Data.UserDatabase;

public class QrActivity extends AppCompatActivity implements QRCodeReaderView.OnQRCodeReadListener {
    private ImageView imagecode;
    private QRCodeReaderView qrCodeReaderView;
    private static final int PERMISSION_REQUEST_CODE = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr);

        imagecode = findViewById(R.id.imageView);
        setRenderOption();
    }

    private void setRenderOption() {
        User me = new UserDatabase(getApplicationContext()).readFromFile();
        RenderOption renderOption = new RenderOption();
        renderOption.setContent(me.getDatabaseID()); // content to encode
        renderOption.setSize(800); // size of the final QR code image
        renderOption.setBorderWidth(20); // width of the empty space around the QR code
        renderOption.setEcl(ErrorCorrectionLevel.M); // (optional) specify an error correction level
        renderOption.setPatternScale(0.35f); // (optional) specify a scale for patterns
        renderOption.setRoundedPatterns(true); // (optional) if true, blocks will be drawn as dots instead
        renderOption.setClearBorder(true); // set a logo, keep reading to find more about it

        try {
            RenderResult ex = AwesomeQrRenderer.render(renderOption);
            if (ex != null) {
                imagecode.setImageBitmap(ex.getBitmap());
            }
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onQRCodeRead(String text, PointF[] points) {
        User me = new UserDatabase(getApplicationContext()).readFromFile();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("friendRequests").child(text).child(me.getDatabaseID());
        reference.child("name").setValue(me.getName());
        reference.child("userID").setValue(me.getDatabaseID());
        reference.child("accepted").setValue(false);

        Toast.makeText(getApplicationContext(), getString(R.string.friend_added), Toast.LENGTH_LONG).show();

        //TODO: INCORPORATE ACCEPT BUTTON FRIEND LAYOUT
        //TODO: IN OWN OBJECT THEN CHECK WHEN SOMEONE ACCEPT REUEST AND THEN DELETE THE NODE
        //TODO: ADD TO OWN FRIENDS THEN
        finish();
    }

    public void scanCode(View v) {
        if (checkPermission()) {
            //main logic or main code


            setContentView(R.layout.activity_qr_scan);
            qrCodeReaderView = findViewById(R.id.qrdecoderview);
            qrCodeReaderView.setOnQRCodeReadListener(this);

            // Use this function to enable/disable decoding
            qrCodeReaderView.setQRDecodingEnabled(true);

            // Use this function to change the autofocus interval (default is 5 secs)
            qrCodeReaderView.setAutofocusInterval(2000L);

            // Use this function to enable/disable Torch
            qrCodeReaderView.setTorchEnabled(true);

            // Use this function to set front camera preview
            qrCodeReaderView.setFrontCamera();

            // Use this function to set back camera preview
            qrCodeReaderView.setBackCamera();

        } else {
            requestPermission();
        }
    }

    private boolean checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            return false;
        }
        return true;
    }

    private void requestPermission() {

        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.CAMERA},
                PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getApplicationContext(), "Permission Granted", Toast.LENGTH_SHORT).show();

                    // main logic
                } else {
                    Toast.makeText(getApplicationContext(), "Permission Denied", Toast.LENGTH_SHORT).show();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                                != PackageManager.PERMISSION_GRANTED) {
                            showMessageOKCancel("You need to allow access permissions",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                requestPermission();
                                            }
                                        }
                                    });
                        }
                    }
                }
                break;
        }
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(QrActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }
}
