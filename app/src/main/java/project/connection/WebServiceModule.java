package project.connection;

import android.text.TextUtils;
import android.util.Log;

import ir.farazist.farazist_app.App;
import project.helper.HelperFile;
import project.helper.HelperString;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.CookieManager;
import java.net.HttpCookie;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

public class WebServiceModule {
    //public static final int PROTOCOL_EXCEPTION = -1;

    private HttpsURLConnection connection;
    private final String IO_EXCEPTION = "IO EXCEPTION";
    private final String UNKNOWN_EXCEPTION = "UNKNOWN EXCEPTION";
    private String data = null;
    private String token = "";
    private CookieManager cookieManager;
    private boolean tokenEnable = false;
    private String authorization = "";
    private String jsonParams = "";
    private boolean timeOut;
    private boolean sentData;

    // *** File Parameters ***

    private String filePath = "";
    private String fileParameterName = "image";
    private String lineEnd = "\r\n";
    private String twoHyphens = "--";
    private String boundary = "*****";

    private int bytesRead, bytesAvailable, bufferSize;

    private int maxBufferSize = 1 * 1024 * 1024;

    public String getData() {
        return data;
    }


    public interface Listener {
        void onSuccess(String data);

        void onFail(String error);
    }

    private Listener listener;
    private String[] params;
    private String cacheFileName;
    private String stringUrl;
    private int connectionTimeout = 10000;


    public WebServiceModule url(String url) {
        this.stringUrl = url;
        return this;
    }

    public WebServiceModule params(String... params) {
        this.params = params;
        return this;
    }


    public WebServiceModule authorization(String authorization_header, String authorization) {
        this.authorization = authorization_header + " " + authorization;
        return this;
    }

    public WebServiceModule listener(Listener listener) {
        this.listener = listener;
        return this;
    }

    public WebServiceModule connectionTimeout(int connectionTimeout) {
        this.connectionTimeout = connectionTimeout * 1000;
        return this;
    }


    public void connect() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                readFromNet();
            }
        }).start();

        new Thread(new Runnable() { // timer for connection timeOut
            @Override
            public void run() {
                try {
                    Thread.sleep(connectionTimeout);
                    if (!sentData && listener != null) {
                        timeOut = true;
                        if(connection!=null){
                            connection.disconnect();
                        }
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
        }).start();
    }

    public void sendJson(String jsonParams) {
        this.jsonParams = jsonParams;
        new Thread(new Runnable() {
            @Override
            public void run() {
                sendJsonData();

            }
        }).start();

        new Thread(new Runnable() { // timer for connection timeOut
            @Override
            public void run() {
                try {
                    Thread.sleep(connectionTimeout);
                    if (!sentData && listener != null) {
                        timeOut = true;
                        if(connection!=null){
                            connection.disconnect();
                        }
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
        }).start();
    }

    private void sendJsonData() {
        try {
            URL url = new URL(stringUrl);
            connection = (HttpsURLConnection) url.openConnection();
            connection.setInstanceFollowRedirects(false);
            if (authorization != null && authorization.length() > 0) {
                connection.addRequestProperty("Authorization", authorization);
            }
            connection.addRequestProperty("Content-Type", "application/json");
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);

            if (jsonParams != null && jsonParams.length() > 0) {
                DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
                byte[] buffer = jsonParams.getBytes("UTF-8");
                outputStream.write(buffer, 0, buffer.length);
                outputStream.flush();
                outputStream.close();
            }
            int code = connection.getResponseCode();
            InputStream inputStream = connection.getInputStream();
            data = inputStreamToString(inputStream);
            connection.disconnect();

            if (listener != null && !timeOut) {
                sentData = true;
                App.getHandler().post(new Runnable() {
                    @Override
                    public void run() {
                        listener.onSuccess(data);
                        return;
                    }
                });

            }
        } catch (IOException e) {
            try{
                connection.disconnect();
            }catch (Exception e2){

            }
            if (listener != null && !timeOut) {
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
        } catch (Exception e) {
            try{
                connection.disconnect();
            }catch (Exception e2){

            }
            if (listener != null && !timeOut) {
                sentData = true;
                App.getHandler().post(new Runnable() {
                    @Override
                    public void run() {
                        Log.i("LOG", "UNKNOWN_EXCEPTION");
                        listener.onFail(UNKNOWN_EXCEPTION);
                    }
                });
            }

        }
    }

    private void readFromNet() {
        try {
            URL url = new URL(stringUrl);
            connection = (HttpsURLConnection) url.openConnection();
            connection.setInstanceFollowRedirects(false);
            if (tokenEnable) {
                getTokens("https://farazist.ir/mobile/get-csrf");
            }
            if (authorization != null && authorization.length() > 0) {
                connection.setRequestProperty("Authorization", authorization);
            }
            connection.setDoInput(true);
            connection.setRequestMethod("POST");

//            if (tokenEnable && cookieManager.getCookieStore().getCookies().size() > 0) {
//                connection.setRequestProperty("Cookie",
//                        TextUtils.join(";", cookieManager.getCookieStore().getCookies()));
//            }

            DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
            if (params != null) {
                String urlParams = convertToParamsFormat(params);
                byte[] buffer = urlParams.getBytes("UTF-8");
                outputStream.write(buffer, 0, buffer.length);
                outputStream.flush();

            }

            outputStream.flush();
            outputStream.close();
            int code = connection.getResponseCode();
            InputStream inputStream = connection.getInputStream();
            data = inputStreamToString(inputStream);
            connection.disconnect();
            if (listener != null && !timeOut) {
                sentData = true;
                App.getHandler().post(new Runnable() {
                    @Override
                    public void run() {
                        listener.onSuccess(data);
                        return;
                    }
                });

            }
        } catch (IOException e) {
            try{
                connection.disconnect();
            }catch (Exception e2){

            }
            if (listener != null && !timeOut) {
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
        } catch (Exception e) {
            try{
                connection.disconnect();
            }catch (Exception e2){

            }
            if (listener != null && !timeOut) {
                sentData = true;
                App.getHandler().post(new Runnable() {
                    @Override
                    public void run() {
                        Log.i("LOG", "UNKNOWN_EXCEPTION");
                        listener.onFail(UNKNOWN_EXCEPTION);
                    }
                });
            }

        }
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

    private String convertToParamsFormat(String[] params) {
        String standardedParamsFormat = "";
        boolean isFirstParameter = true;
        for (int i = 0; i <= params.length - 2; i = i + 2) {
            if (!isFirstParameter) {
                standardedParamsFormat += "&";
            }
            standardedParamsFormat += params[i];
            standardedParamsFormat += "=";
            standardedParamsFormat += params[i + 1];
            isFirstParameter = false;
        }
        if (tokenEnable) {
            standardedParamsFormat += ("&_token=" + token);
        }
        return standardedParamsFormat;

    }


    private void getTokens(String tokenUrl) throws IOException {
        URL url = new URL(tokenUrl);
        HttpsURLConnection tokenConnection = (HttpsURLConnection) url.openConnection();
        tokenConnection.connect();
        InputStream inputStream = tokenConnection.getInputStream();
        token = inputStreamToString(inputStream);
        cookieManager = new CookieManager();

        List<String> cookieHeader = tokenConnection.getHeaderFields().get("Set-Cookie");

        if (cookieHeader != null) {
            for (String cookie : cookieHeader) {
                cookieManager.getCookieStore().add(null, HttpCookie.parse(cookie).get(0));
            }
        }

        tokenConnection.disconnect();
    }


}
