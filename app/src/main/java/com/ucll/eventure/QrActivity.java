package com.ucll.eventure;

import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.NonNull;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr);

        imagecode = findViewById(R.id.imageView);
        setRenderOption();
    }

    private void setRenderOption(){
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
            if(ex != null){
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
        reference.child("id").setValue(me.getDatabaseID());
        reference.child("accepted").setValue(false);

        //TODO: INCORPORATE ACCEPT BUTTON FRIEND LAYOUT
        //TODO: IN OWN OBJECT THEN CHECK WHEN SOMEONE ACCEPT REUEST AND THEN DELETE THE NODE
        //TODO: ADD TO OWN FRIENDS THEN
        finish();
    }

    public void scanCode(View v){
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
    }
}
