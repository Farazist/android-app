package project.helper;


import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.widget.EditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import ir.farazist.farazist_app.App;
import ir.farazist.farazist_app.R;

public class HelperString {

    public static String computeInvoice(JSONArray invoiceJsonArray) {
        String invoice = "";
        String tooman = App.getContext().getString(R.string.tooman);
        String eachUnit = App.getContext().getString(R.string.eachUnit);
        String unitWord = App.getContext().getString(R.string.unit);
        try {
            for (int i = 0; i < invoiceJsonArray.length(); i++) {
                JSONObject object = invoiceJsonArray.getJSONObject(i);

                int count = object.getJSONObject("pivot").getInt("count");
                String name = object.getString("name");
                int unit = Integer.valueOf(object.getString("price"));

                invoice += count + " ";
                invoice += unitWord + " ";
                invoice += name + " ";
                invoice += eachUnit + " ";
                invoice += unit + " ";
                invoice += tooman;
                if (i != invoiceJsonArray.length() - 1) {
                    invoice += "\n";
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return invoice;
    }

    public static String computeCustomInvoice(JSONArray invoiceJsonArray) {
        String invoice = "";
        String kilogram = App.getContext().getString(R.string.kilogram);
        String unitWord = App.getContext().getString(R.string.unit);
        try {
            for (int i = 0; i < invoiceJsonArray.length(); i++) {
                JSONObject object = invoiceJsonArray.getJSONObject(i);
                String name = object.getString("name");
                int count = object.getInt("count");
                int weight = object.getInt("weight");

                if(count>0){
                    invoice += count + " ";
                    invoice += unitWord +" ";
                    invoice += name;
                }else{
                    invoice += weight + " ";
                    invoice += kilogram +" ";
                    invoice += name;
                }

                if (i != invoiceJsonArray.length() - 1) {
                    invoice += "\n";
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return invoice;
    }

    public static int getCustomSum(JSONArray customInvoiceJsonArray){
        int sum = 0;
        for(int i=0;i<customInvoiceJsonArray.length();i++){
            JSONObject object = null;
            try {
                object = customInvoiceJsonArray.getJSONObject(i);
                int count = object.getInt("price");
                sum = sum + count;
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        return sum;
    }

    public static int getSum(JSONArray invoiceJsonArray) {
        int sum = 0;
        try {
            for (int i = 0; i < invoiceJsonArray.length(); i++) {
                JSONObject object = invoiceJsonArray.getJSONObject(i);
                int count = object.getJSONObject("pivot").getInt("count");
                int unit = Integer.valueOf(object.getString("price"));
                sum = sum + (unit * count);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return sum;
    }

    public static String getTransformedTime(String time) {
        return time.substring(0, time.lastIndexOf(":"));
    }

    public static String getTransformedDate(String date) {
        String[] dateArray = date.split("-");
        HelperCalendar helperCalendar = new HelperCalendar();
        helperCalendar.gregorianToPersian(Integer.parseInt(dateArray[0]), Integer.parseInt(dateArray[1]), Integer.parseInt(dateArray[2]));
        int d = helperCalendar.getDay();
        String day = "";
        if (d < 10) {
            day += "0";
        }
        day += d;

        int m = helperCalendar.getMonth();
        String month = "";
        if (m < 10) {
            month += "0";
        }
        month += m;
        return (helperCalendar.getYear() + "/" + month + "/" + day);
    }

    public String decrypt(String text) {
        String decryptedValue = text;
        try {
            SecretKeySpec key = generateKey("farazist_aem7mi&_%rouho7lamini@_!za7rei");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] decodedValue = Base64.decode(text, Base64.DEFAULT);
            byte[] decVal = cipher.doFinal(decodedValue);
            decryptedValue = new String(decVal);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return decryptedValue;
    }

    public String encrypt(String text) {
        String encriptionValue = text;
        try {
            SecretKeySpec key = generateKey("farazist_aem7mi&_%rouho7lamini@_!za7rei");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] encVal = cipher.doFinal(text.getBytes());
            encriptionValue = Base64.encodeToString(encVal, Base64.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return encriptionValue;
    }

    private SecretKeySpec generateKey(String password) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] bytes = password.getBytes("UTF-8");
        digest.update(bytes, 0, bytes.length);
        byte[] key = digest.digest();
        SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
        return secretKeySpec;
    }

    public static String convertToNumberFormat(String number) {
        char[] numArray = number.toCharArray();
        int index = 0;
        String standardNumber = "";
        for (int i = numArray.length - 1; i >= 0; i--) {
            standardNumber  = numArray[i] + standardNumber;
            index++;
            if(index%3==0 && i>0){
                standardNumber = ","+standardNumber;
            }
        }
        return standardNumber;
    }

    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {


            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/"
                            + split[1];
                }
            }
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"),
                        Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[] { split[1] };

                return getDataColumn(context, contentUri, selection,
                        selectionArgs);
            }
        }
        else if ("content".equalsIgnoreCase(uri.getScheme())) {

            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(context, uri, null, null);
        }
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    private static String getDataColumn(Context context, Uri uri,
                                       String selection, String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = { column };

        try {
            cursor = context.getContentResolver().query(uri, projection,
                    selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    private static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri
                .getAuthority());
    }


    private static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri
                .getAuthority());
    }

    private static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri
                .getAuthority());
    }

    private static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri
                .getAuthority());
    }


    public static void currencyFormatEditText(final EditText editText){
        final boolean[] userChangedAmount = {true};
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                if(!userChangedAmount[0]){
                    return;
                }
                String amount = editText.getText().toString().replace(",","");
                amount = HelperString.convertToNumberFormat(amount);
                userChangedAmount[0] = false;
                editText.setText(amount);
                userChangedAmount[0] = true;
                editText.setSelection(editText.getText().length());
            }
        });
    }

}
