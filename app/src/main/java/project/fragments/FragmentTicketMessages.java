package project.fragments;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import ir.farazist.farazist_app.App;
import ir.farazist.farazist_app.R;
import pl.droidsonroids.gif.GifImageView;
import project.activities.ActivityMain;
import project.adapters.AdapterTicketMessageRecycler;
import project.connection.Commands;
import project.connection.FileUploader;
import project.custom.CustomToast;
import project.helper.HelperFile;
import project.helper.HelperString;
import project.helper.HelperView;
import project.helper.PermissionManager;
import project.interfaces.ActivityResult;
import project.structures.StructTicketMessage;

public class FragmentTicketMessages extends Fragment {

    private  AdapterTicketMessageRecycler adapter;
    private  ArrayList<StructTicketMessage> messages;
    private SwipeRefreshLayout swipe;
    private RecyclerView lstMessages;

    private int ticketID;
    private int position;
    private String state;

    private boolean sendEnable;

    private ImageView img;
    private TextView txtDelete;
    private ConstraintLayout layoutImage;
    private boolean sendWithImage;
    private Uri selectedImageUri;

    private static Dialog dialogShowImage;
    private static TextView txtDownload;
    private static ImageView imgAttach;
    private static GifImageView loader;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_ticket_messages,container,false);

        sendEnable = true;
        sendWithImage = false;
        selectedImageUri = null;

        img = root.findViewById(R.id.img);
        layoutImage = root.findViewById(R.id.layoutImage);
        txtDelete = root.findViewById(R.id.txtDelete);
        final EditText edtResponse = (EditText) root.findViewById(R.id.edtResponse);
        ImageView imgSend = (ImageView)root.findViewById(R.id.imgSend);
        TextView txtAttach = (TextView)root.findViewById(R.id.txtAttach);
        lstMessages = (RecyclerView)root.findViewById(R.id.lstMessages);
        LinearLayoutManager layoutManager = new LinearLayoutManager(App.getContext());
        swipe = (SwipeRefreshLayout)root.findViewById(R.id.swipe);
        swipe.setColorSchemeColors(App.getContext().getResources().getColor(R.color.colorAccent),App.getContext().getResources().getColor(R.color.colorPrimary));
        lstMessages.setLayoutManager(layoutManager);

        dialogShowImage = new Dialog(App.getCurrentActivity());
        dialogShowImage.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialogShowImage.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialogShowImage.setContentView(R.layout.dialog_show_image);
        Window window = dialogShowImage.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        window.setGravity(Gravity.CENTER);
        txtDownload = (TextView) dialogShowImage.findViewById(R.id.txtDownload);
        imgAttach        = (ImageView) dialogShowImage.findViewById(R.id.img);
        loader   = (GifImageView) dialogShowImage.findViewById(R.id.loader);

        dialogShowImage.findViewById(R.id.root).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogShowImage.dismiss();
            }
        });
        imgAttach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                return;
            }
        });

        ticketID = getArguments().getInt("TICKET_ID");
        position = getArguments().getInt("POSITION");
        state = getArguments().getString("STATE");
        if(state.equals(App.getContext().getString(R.string.closed))){
            edtResponse.setText(App.getContext().getString(R.string.closed));
            imgSend.setEnabled(false);
            edtResponse.setEnabled(false);
            txtAttach.setVisibility(View.GONE);
        }else{
            edtResponse.setText("");
            imgSend.setEnabled(true);
            edtResponse.setEnabled(true);
            txtAttach.setVisibility(View.VISIBLE);
        }

        getTicketMessages(ticketID,true);

        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(sendEnable) {
                    getTicketMessages(ticketID, true);
                }
            }
        });

        txtDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                layoutImage.setVisibility(View.GONE);
                sendWithImage = false;
            }
        });

        ActivityMain.activityResultListener = new ActivityResult() {
            @Override
            public void onGranted(int requestCode, int resultCode, @Nullable Intent data) {
                    selectedImageUri = data.getData();
                    File file = new File(selectedImageUri.getPath());
                    Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                    img.setImageBitmap(bitmap);
                    layoutImage.setVisibility(View.VISIBLE);
                    sendWithImage = true;
            }
        };

        txtAttach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               selectImage();
            }
        });



        imgSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String message = edtResponse.getText().toString();
                if(!sendEnable){
                    CustomToast.showToast(App.getContext().getString(R.string.pleaseWait));
                    return;
                }
                if(message.length()==0 && !sendWithImage){
                    return;
                }

                sendEnable = false;
                HelperView.hideKeyboard(App.getCurrentActivity());
                StructTicketMessage newMessage = new StructTicketMessage();
                newMessage.type = "send";
                newMessage.dateTime = App.getContext().getString(R.string.sending);
                newMessage.message = message;
                newMessage.imgUrl = "";
                messages.add(0,newMessage);
                adapter.notifyDataSetChanged();
                edtResponse.setText("");
                layoutImage.setVisibility(View.GONE);
                lstMessages.scrollToPosition(0);
                if(sendWithImage){
                    uploadWithImage(message);
                }else{
                    sendMessageOnly(message);
                }
                sendWithImage = false;

            }
        });

        root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                return;
            }
        });
        return root;
    }

    private void getTicketMessages(int ticketID , boolean refreshing){
        if(refreshing) {
            loading();
        }
        messages = new ArrayList<>();
        new Commands().setCompleteListener(new Commands.onCommandCompleteListener() {
            @Override
            public void onComplete(String data) {
                try {
                    JSONArray messageArray = new JSONArray(data);
                    for(int i=messageArray.length()-1;i>=0;i--){
                        JSONObject messageObject = messageArray.getJSONObject(i);
                        StructTicketMessage message = new StructTicketMessage();
                        message.id = messageObject.getInt("id");
                        String dateTime = messageObject.getString("created_at");
                        String date = HelperString.getTransformedDate(dateTime.split(" ")[0]);
                        String time = HelperString.getTransformedTime(dateTime.split(" ")[1]);
                        message.dateTime = date + " - " + time;
                        String imgUrl = messageObject.getString("image");
                        if(imgUrl!=null && !imgUrl.toLowerCase().equals("null") && imgUrl.length()>0){
                            message.imgUrl = App.SERVER_ADDRESS+imgUrl;
                        }else {
                            message.imgUrl = "";
                        }
                        message.type = messageObject.getString("type");
                        String textMessage = messageObject.getString("text");;
                        if(textMessage !=null && !textMessage.toLowerCase().equals("null") && textMessage.length()>0){
                            message.message =textMessage;
                        }else{
                            message.message = "";
                        }

                        messages.add(message);
                    }
                    adapter = new AdapterTicketMessageRecycler(messages);
                    lstMessages.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    loaded();
                    sendEnable = true;
                    seenTicket();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFail(String error) {
                CustomToast.showToast(App.getContext().getString(R.string.connectionError));
                loaded();
                sendEnable = true;
            }
        }).getTicketMessage(ticketID);
    }

    private void loading(){
        swipe.setRefreshing(true);
    }

    private void loaded(){
        swipe.setRefreshing(false);
    }

    private void uploadWithImage(String message){
        File file = new File(selectedImageUri.getPath());
        Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
        String path = HelperString.getPath(App.getContext(), selectedImageUri);
        new FileUploader()
                .url(App.SERVER_ADDRESS + "/api/add-ticket-message")
                .authorization("Bearer", App.accessToken)
                .setListener(new FileUploader.Listener() {
                    @Override
                    public void onComplete(String data) {
                        getTicketMessages(ticketID,false);
                    }

                    @Override
                    public void onFail(String error) {
                        messages.remove(0);
                        adapter.notifyDataSetChanged();
                        CustomToast.showToast(App.getContext().getString(R.string.connectionError_checkYourConnection));
                        sendEnable = true;
                    }

                    @Override
                    public void onProgress(long totalSize, long uploadedSize, int percent) {

                    }
                }).setImage("image", path)
                .maximumFileSize(1024 * 1024 * 3)
                .params("ticket_id", ticketID +"", "type", "send", "text", message)
                .upload();

    }
    private void sendMessageOnly(String message){
        new Commands().setCompleteListener(new Commands.onCommandCompleteListener() {
            @Override
            public void onComplete(String data) {
                getTicketMessages(ticketID,false);
            }

            @Override
            public void onFail(String error) {
                messages.remove(0);
                adapter.notifyDataSetChanged();
                CustomToast.showToast(App.getContext().getString(R.string.connectionError));
                sendEnable = true;
            }
        }).addTicketMessage(ticketID,"send",message);
    }

    public static void showImageDialog(final String url){
        final boolean[] canSave = {false};
        loader.setVisibility(View.VISIBLE);
        dialogShowImage.show();
        Picasso.with(App.getContext()).load(url).into(imgAttach, new Callback() {
            @Override
            public void onSuccess() {
                loader.setVisibility(View.GONE);
                canSave[0] = true;
            }

            @Override
            public void onError() {
                dialogShowImage.dismiss();
                CustomToast.showToast(App.getContext().getString(R.string.connectionError));
            }
        });
        txtDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(canSave[0]){
                    requestPermission(HelperFile.getFileName(url));
                }
            }
        });
    }

    private static void requestPermission(final String name) {
        PermissionManager permissinManager = new PermissionManager(App.getCurrentActivity());
        permissinManager.setOnPermissionGranted(new PermissionManager.OnPermissionGranted() {
            @Override
            public void onGranted() {
                imgAttach.setDrawingCacheEnabled(true);
                imgAttach.buildDrawingCache();
                Bitmap bmap = imgAttach.getDrawingCache();
                BitmapDrawable drawable = (BitmapDrawable) imgAttach.getDrawable();
                Bitmap bitmap = drawable.getBitmap();
                new File(App.DIR_APP).mkdirs();
                new File(App.DIR_GALLERY_FARAZIST).mkdirs();
                saveToGallery(bitmap, name);


            }
        }).setOnPermissionDenied(new PermissionManager.OnPermissionDenied() {
            @Override
            public void onDenied() {
                new AlertDialog.Builder(App.getCurrentActivity())
                        .setTitle(App.getContext().getString(R.string.permission_required))
                        .setMessage(App.getContext().getString(R.string.write_sdcard_required_text))
                        .setPositiveButton(App.getContext().getString(R.string.ask_again), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                requestPermission(name);
                            }
                        })
                        .setNegativeButton(App.getContext().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        }).create().show();
            }
        }).request(Manifest.permission.WRITE_EXTERNAL_STORAGE);


    }
    private static void saveToGallery(Bitmap bitmap, String name) {
        try {
            File imageFile = new File(App.DIR_GALLERY_FARAZIST, name);
            if (imageFile.exists()) {
                imageFile.delete();
            }

            BitmapDrawable drawable = (BitmapDrawable) imgAttach.getDrawable();
            bitmap = drawable.getBitmap();
            FileOutputStream outputStream = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            outputStream.flush();
            outputStream.close();
            MediaScannerConnection.scanFile(App.getContext(), new String[] { imageFile.getPath() }, new String[] { "image/png" }, null);
            dialogShowImage.dismiss();
            App.getHandler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    CustomToast.showToast(App.getContext().getString(R.string.savedInGallery));
                }
            },200);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void selectImage() {
//        CropImage.activity()
//                .setAspectRatio(1, 1)
//                .setActivityTitle(App.getContext().getString(R.string.imageSelection))
//                .setMultiTouchEnabled(true)
//                .setAllowRotation(true)
//                .setAllowCounterRotation(true)
//                .start(App.getCurrentActivity());
        ImagePicker.Companion.with(this)
                .crop()
                .compress(1024)
                .maxResultSize(1080, 1080)
                .start();
    }


    private void seenTicket(){
        new Commands().setCompleteListener(new Commands.onCommandCompleteListener() {
            @Override
            public void onComplete(String data) {
                FragmentContactManagement.updateList(position);
            }

            @Override
            public void onFail(String error) {
            }
        }).seenTicket(ticketID);
    }
}
