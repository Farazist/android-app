//        fragmentManager = getSupportFragmentManager();
//        fragmentTransaction = fragmentManager.beginTransaction();
//        fragmentTransaction.add(R.id.fragmentContainer, new FragmentMainClient(), App.FRAGMENT_MAIN_CLIENT_ID);
//        fragmentTransaction.commit();


//

//        new Commands().setCompleteListener(new Commands.onCommandCompleteListener() {
//            @Override
//            public void onComplete(String data) {
//                try {
//                    JSONArray sliders = new JSONArray(data);
//                    for (int i = 0; i < sliders.length(); i++) {
//                        JSONObject slider = sliders.getJSONObject(i);
//                        String url = slider.getString("image");
//                        sliderUrls.add(url);
//                    }
//                    init();
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//
//            }
//
//            @Override
//            public void onFail(String error) {
//
//            }
//        }).getSliders();


//        listener = new OnSetMainToolbarListener() {
//            @Override
//            public void onChange() {
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        try {
//                            Thread.sleep(10);
//                            runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    setSupportActionBar(toolbar);
//                                    getSupportActionBar().show();
//                                }
//                            });
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }).start();
//
//
//            }
//        };


// if(filePath!=null && filePath.length()>0){
//         Log.i("LOGGG","1");
//         connection.setDoInput(true);
//         connection.setUseCaches(false);
//         connection.setRequestProperty("Connection", "Keep-Alive");
//         connection.setRequestProperty("Cache-Control", "no-cache");
//         connection.setRequestProperty("ENCTYPE", "multipart/form-data");
////                connection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
//         connection.setRequestProperty("image", filePath);
//         Log.i("LOGGG","We have file");
//         File imageFile = new File(filePath);
//         String fileName = HelperFile.getFileName(filePath);
//         if(!imageFile.exists()){
//         return;
//         }
//         FileInputStream fileInputStream = new FileInputStream(imageFile);
//         outputStream.writeBytes(twoHyphens + boundary + lineEnd);
//         outputStream.writeBytes("Content-Disposition: form-data; name=\"" + fileParameterName
//         + "\"; filename=\"" + fileName + "\"" + lineEnd);
//         outputStream.writeBytes(lineEnd);
//         int bytesRead, bytesAvailable, bufferSize;
//         byte[] buffer;
//         int maxBufferSize = 1 * 1024 * 1024;
//         //returns no. of bytes present in fileInputStream
//         bytesAvailable = fileInputStream.available();
//
//         bufferSize = 4096;
//         buffer = new byte[4096];
//         long size = imageFile.length();
//         bytesRead = fileInputStream.read(buffer, 0, bufferSize);
//         while (bytesRead > 0) {
//         int percentage = (int) ((bytesRead / (float) size) * 100);
//         outputStream.write(buffer, 0, bufferSize);
//         outputStream.flush(); //doesn't help
//         bytesAvailable = fileInputStream.available();
//         bytesRead = fileInputStream.read(buffer, 0, bufferSize);
//         }//This finishes in 1 second
//
//         outputStream.writeBytes(lineEnd);
//         outputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
//         }