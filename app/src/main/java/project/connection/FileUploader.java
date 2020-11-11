package project.connection;


import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.net.MalformedURLException;
import java.net.URL;


import javax.net.ssl.HttpsURLConnection;

import ir.farazist.farazist_app.App;
import ir.farazist.farazist_app.R;
import project.custom.CustomToast;

public class FileUploader {

    private String lineEnd = "\r\n";
    private String twoHyphens = "--";
    private String boundary = "*****";

    private final String IO_EXCEPTION = "IO EXCEPTION";
    private final String UNKNOWN_EXCEPTION = "UNKNOWN EXCEPTION";
    private String data = null;
    private String authorization = "";
    private boolean timeOut;
    private boolean sentData;

    private double bufferSize = 4096;
    private String filePath = "";
    private String fileKeyName = "";
    private String[] params;
    private String stringUrl = "";
    private Listener listener;
    private int connectionTimeout = 15000;
    private boolean maximumCheck = false;
    private long maximunSize = 0;

    private Thread timeoutThread;

    public FileUploader setImage(String fileKeyName, String filePath) {
        this.fileKeyName = fileKeyName;
        this.filePath = filePath;
        return this;
    }

    public FileUploader params(String... params) {
        this.params = params;
        return this;
    }

    public FileUploader url(String url) {
        this.stringUrl = url;
        return this;
    }

    public FileUploader authorization(String authorization_header, String authorization) {
        this.authorization = authorization_header + " " + authorization;
        return this;
    }

    public FileUploader setListener(Listener listener) {
        this.listener = listener;
        return this;
    }


    public FileUploader connectionTimeout(int connectionTimeout) {
        this.connectionTimeout = connectionTimeout * 1000;
        return this;
    }

    public FileUploader maximumFileSize(long size){
        this.maximunSize = size;
        maximumCheck = true;
        return this;
    }

    public void upload() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    File file = new File(filePath);
                    if(file.length()>maximunSize){
                        App.getHandler().post(new Runnable() {
                            @Override
                            public void run() {
                                sentData = true;
                                listener.onFail("maximum file size");
                                CustomToast.showToast(App.getContext().getString(R.string.maximumImageSize));
                            }
                        });
                        return;
                    }
                    if(!file.exists()){
                        if(listener !=null){
                            App.getHandler().post(new Runnable() {
                                @Override
                                public void run() {
                                    sentData = true;
                                    listener.onFail("file not exist");
                                }
                            });
                        }
                        return;
                    }
                    FileInputStream fileInputStream = new FileInputStream(filePath);
                    URL url = new URL(stringUrl);
                    HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
                    if(authorization!=null && authorization.length()>0) {
                        connection.addRequestProperty("Authorization", authorization);
                    }
                    connection.setDoInput(true);
                    connection.setDoOutput(true);
                    connection.setUseCaches(false);
                    connection.setRequestMethod("POST");
                    connection.setRequestProperty("Connection", "Keep-Alive");
                    connection.setRequestProperty("ENCTYPE", "multipart/form-data");
                    connection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                    DataOutputStream dataOutputStream = new DataOutputStream(connection.getOutputStream());
                    if(params != null && params.length>0) {
                        for (int i = 0; i < params.length - 1; i = i + 2) {
//                            dataOutputStream.writeBytes("--" + boundary + "\r\n");
//                            dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"" + params[i] + "\"\r\n\r\n");
//                            dataOutputStream.writeBytes(params[i + 1] + "\r\n");

                            dataOutputStream.writeBytes("--" + boundary + "\r\n");
                            String data = "Content-Disposition: form-data; name=\"" + params[i] + "\"\r\n\r\n";
                            byte[] buffer = data.getBytes("UTF-8");
                            dataOutputStream.write(buffer, 0, buffer.length);
                            String value = params[i + 1] + "\r\n";
                            buffer = value.getBytes("UTF-8");
                            dataOutputStream.write(buffer, 0, buffer.length);
                        }
                    }
                    dataOutputStream.writeBytes(twoHyphens + boundary + lineEnd);
                    dataOutputStream.writeBytes("Content-Disposition: form-data; name=\""+fileKeyName+"\";filename=\"" + filePath + "\"" + lineEnd);
                    dataOutputStream.writeBytes(lineEnd);

                    byte[] buffer = new byte[4096];
                    final long fileSize = file.length();
                    long uploadedSize = 0;
                    int len = 0;
                    while ((len = fileInputStream.read(buffer)) > 0) {
                        if(timeOut){
                            break;
                        }
                        dataOutputStream.write(buffer, 0, len);
                        uploadedSize += len;
                        final int percentage = (int) ((100*uploadedSize) /fileSize);
                        if(listener!=null){
                            final long finalUploadedSize = uploadedSize;
                            App.getHandler().post(new Runnable() {
                                @Override
                                public void run() {
                                    listener.onProgress(finalUploadedSize,fileSize,percentage);
                                }
                            });
                        }
                        timeoutThread.interrupt();
                    }
                    dataOutputStream.flush();
                    dataOutputStream.writeBytes(lineEnd);
                    dataOutputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
                    int code = connection.getResponseCode();
                    InputStream inputStream = connection.getInputStream();
                    data = inputStreamToString(inputStream);
                    connection.disconnect();
                    if(listener!=null && !timeOut){
                        sentData = true;
                        App.getHandler().post(new Runnable() {
                            @Override
                            public void run() {
                                listener.onComplete(data);
                            }
                        });
                    }

                } catch (final MalformedURLException e) {
                    if(listener !=null && !timeOut){
                        sentData = true;
                        App.getHandler().post(new Runnable() {
                            @Override
                            public void run() {
                                listener.onFail(e.getMessage());
                            }
                        });
                    }
                    e.printStackTrace();
                } catch (final IOException e) {
                    if(listener !=null && !timeOut){
                        sentData = true;
                        App.getHandler().post(new Runnable() {
                            @Override
                            public void run() {
                                listener.onFail(e.getMessage());
                            }
                        });
                    }
                    e.printStackTrace();
                } catch (final Exception e){
                    sentData = true;
                    if(listener !=null && !timeOut){
                        App.getHandler().post(new Runnable() {
                            @Override
                            public void run() {
                                listener.onFail(e.getMessage());
                            }
                        });
                    }
                    e.printStackTrace();
                }
            }
        }).start();


        timeoutThread = new Thread(new Runnable() { // timer for connection timeOut
            @Override
            public void run() {
                try {
                    Thread.sleep(connectionTimeout);
                    if (!sentData && listener != null) {
                        timeOut = true;
                        App.getHandler().post(new Runnable() {
                            @Override
                            public void run() {
                                if (listener != null) {
                                    listener.onFail("Connection timeout");
                                }
                                return;
                            }
                        });
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        timeoutThread.start();
    }

    private String inputStreamToString(InputStream inputStream) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder stringBuilder = new StringBuilder();

        String line = "";
        try {
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
            reader.close();
        } catch (IOException e) {
            if (!timeOut) {
                sentData = true;
                App.getHandler().post(new Runnable() {
                    @Override
                    public void run() {
                        Log.i("LOG", "IOException");
                        listener.onFail(IO_EXCEPTION);
                    }
                });
            }
            e.printStackTrace();
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                if (!timeOut) {
                    sentData = true;
                    App.getHandler().post(new Runnable() {
                        @Override
                        public void run() {
                            Log.i("LOG", "IOException");
                            listener.onFail(IO_EXCEPTION);
                        }
                    });
                }
                e.printStackTrace();
            }
        }
        return stringBuilder.toString();
    }


    public interface Listener {
        void onComplete(String data);

        void onFail(String error);

        void onProgress(long totalSize,long uploadedSize,int percent);

    }
}
