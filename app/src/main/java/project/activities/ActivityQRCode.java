package project.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import ir.farazist.farazist_app.App;
import ir.farazist.farazist_app.R;
import project.custom.CustomToast;
import project.helper.HelperString;
import project.helper.PermissionManager;

public class ActivityQRCode extends CAppCompatActivity {
    private PermissionManager permissionManager;
    private Bitmap QRcodeBitmap;
    private ImageView imgQRcode;
    private boolean share;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        removeActionBar();

        setContentView(R.layout.activity_qrcode);

        share = false;

        imgQRcode = (ImageView) findViewById(R.id.imgQRcode);
        TextView txtTitle = (TextView) findViewById(R.id.txtTitle);
        ViewGroup btnSave = (ViewGroup) findViewById(R.id.btnSave);
        ViewGroup btnShare = (ViewGroup) findViewById(R.id.btnShare);


        String title = getString(R.string.please) + " ";
        title += "QR Code ";
        title += getString(R.string.scan);
        txtTitle.setText(title);

        String QRText = App.account.mobileNumber +" "+App.account.password;
        String encriptMobileNumber = new HelperString().encrypt(QRText);
//        Log.i("LOGGG",encript);
//        String decrypt = new HelperString().decrypt(encript);
//        Log.i("LOGGG",decrypt);

        //setting size of qr code
        int width = 300, height = 300;
        int smallestDimension = width < height ? width : height;
        //setting parameters for qr code

        String charset = "UTF-8";
        Map<EncodeHintType, ErrorCorrectionLevel> hintMap = new HashMap<EncodeHintType, ErrorCorrectionLevel>();
        hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        QRcodeBitmap = CreateQRCode(encriptMobileNumber, charset, hintMap, smallestDimension, smallestDimension);
        if (QRcodeBitmap != null) {
            imgQRcode.setImageBitmap(QRcodeBitmap);
        }


        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                share = false;
                requestPermission();
            }
        });

        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                share = true;
                requestPermission();
            }
        });


    }

    private Bitmap CreateQRCode(String qrCodeData, String charset, Map hintMap, int qrCodeheight, int qrCodewidth) {

        try {
            //generating qr code.
            BitMatrix matrix = new MultiFormatWriter().encode(new String(qrCodeData.getBytes(charset), charset),
                    BarcodeFormat.QR_CODE, qrCodewidth, qrCodeheight, hintMap);
            //converting bitmatrix to bitmap

            int width = matrix.getWidth();
            int height = matrix.getHeight();
            int[] pixels = new int[width * height];
            // All are 0, or black, by default
            for (int y = 0; y < height; y++) {
                int offset = y * width;
                for (int x = 0; x < width; x++) {
                    //for black and white
                    //pixels[offset + x] = matrix.get(x, y) ? BLACK : WHITE;
                    //for custom color
                    pixels[offset + x] = matrix.get(x, y) ?
                            ResourcesCompat.getColor(getResources(), R.color.colorPrimary, null) : android.R.color.white;
                }
            }
            //creating bitmap
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, width, 0, 0, width, height);

            return bitmap;

        } catch (Exception er) {
            Log.e("QrGenerate", er.getMessage());
        }
        return null;
    }

    private void requestPermission() {
        permissionManager = new PermissionManager(ActivityQRCode.this);
        permissionManager.setOnPermissionGranted(new PermissionManager.OnPermissionGranted() {
            @Override
            public void onGranted() {
                // work

                new File(App.DIR_APP).mkdirs();
                new File(App.DIR_GALLERY_FARAZIST).mkdirs();
                saveToGallery(QRcodeBitmap,"QRCode.png");
                if(share){
                    final Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("image/png");
                    final File photoFile = new File(App.DIR_GALLERY_FARAZIST, "QRCode.png");
                    shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(photoFile));
                    startActivity(Intent.createChooser(shareIntent, "Share QRcode image with"));
                }else{
                    CustomToast.showToast(getString(R.string.savedInGallery));
                }

            }
        }).setOnPermissionDenied(new PermissionManager.OnPermissionDenied() {
            @Override
            public void onDenied() {
                new AlertDialog.Builder(ActivityQRCode.this)
                        .setTitle(getString(R.string.permission_required))
                        .setMessage(getString(R.string.write_sdcard_required_text))
                        .setPositiveButton(getString(R.string.ask_again), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                requestPermission();
                            }
                        })
                        .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                                finish();
                            }
                        }).create().show();
            }
        }).request(Manifest.permission.WRITE_EXTERNAL_STORAGE);


    }

    private void saveToGallery(Bitmap bitmap, String name) {
        try {
            File imageFile = new File(App.DIR_GALLERY_FARAZIST, name);
            if (imageFile.exists()) {
                imageFile.delete();
            }

            BitmapDrawable drawable = (BitmapDrawable) imgQRcode.getDrawable();
            bitmap = drawable.getBitmap();
            FileOutputStream outputStream = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            outputStream.flush();
            outputStream.close();

            MediaScannerConnection.scanFile(this, new String[] { imageFile.getPath() }, new String[] { "image/png" }, null);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
