package project.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;


import com.github.dhaval2404.imagepicker.ImagePicker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import ir.farazist.farazist_app.App;
import ir.farazist.farazist_app.R;
import project.activities.ActivityMain;
import project.adapters.AdapterTicketTitleRecycler;
import project.connection.Commands;
import project.connection.FileUploader;
import project.custom.CustomToast;
import project.custom.LinearProgressBar;
import project.custom.ProgressButton;
import project.helper.HelperString;
import project.interfaces.ActivityResult;
import project.structures.StructTicketTitle;

public class FragmentNewTicket extends Fragment {

    public static OnTitleSelectedListener listener;
    private ArrayList<StructTicketTitle> titles;
    private LinearProgressBar progressBar;
    private RecyclerView lstTitles;
    private ProgressButton btnSend;
    private AdapterTicketTitleRecycler adapter;

    private String title;
    private int titleID;
    private String subject;
    private String text;

    private final String OPEN = "open";
    private final String CLOSE = "close";
    private final String SEND = "send";


    private ImageView img;
    private TextView txtDelete;
    private ViewGroup layoutImage;
    private boolean sendWithImage;
    private Uri selectedImageUri;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_new_ticket, container, false);

        title = "";
        titleID = -1;

        sendWithImage = false;
        selectedImageUri = null;

        img = root.findViewById(R.id.img);
        layoutImage = root.findViewById(R.id.layoutImage);
        txtDelete = root.findViewById(R.id.txtDelete);
        final Button btnTitle = (Button) root.findViewById(R.id.btnTitle);
        btnSend = (ProgressButton) root.findViewById(R.id.btnSend);
        final EditText edtSubject = (EditText) root.findViewById(R.id.edtSubject);
        final EditText edtDesc = (EditText) root.findViewById(R.id.edtDesc);
        TextView txtAttach = (TextView)root.findViewById(R.id.txtAttach);
        final Dialog dialogTicketSubjects = new Dialog(App.getCurrentActivity());
        dialogTicketSubjects.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialogTicketSubjects.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialogTicketSubjects.setContentView(R.layout.dialog_ticket_titles);
        Window window = dialogTicketSubjects.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        window.setGravity(Gravity.CENTER);
        lstTitles = (RecyclerView) dialogTicketSubjects.findViewById(R.id.lstSubjects);
        progressBar = (LinearProgressBar) dialogTicketSubjects.findViewById(R.id.progressBar);

        getTicketTitles();
        lstTitles.setLayoutManager(new LinearLayoutManager(App.getContext()));
        adapter = new AdapterTicketTitleRecycler(titles);
        lstTitles.setAdapter(adapter);

        btnTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogTicketSubjects.show();
            }
        });

        txtDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                layoutImage.setVisibility(View.GONE);
                sendWithImage = false;
            }
        });

        txtAttach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                CropImage.activity()
//                        .setActivityTitle(App.getContext().getString(R.string.imageSelection))
//                        .setMultiTouchEnabled(true)
//                        .setAllowRotation(true)
//                        .setAllowCounterRotation(true)
//                        .start(App.getCurrentActivity());
               selectImage();
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

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String subject = edtSubject.getText().toString();
                String desc = edtDesc.getText().toString();
                if (titleID < 0) {
                    CustomToast.showToast(App.getContext().getString(R.string.selectTicketTitle));
                    return;
                } else if (subject.length() < 1 || (desc.length() < 1 && !sendWithImage)) {
                    CustomToast.showToast(App.getContext().getString(R.string.fillAllRequiredFields));
                    return;
                } else {
                    btnSend.loading();
                    subject = edtSubject.getText().toString();
                    text = edtDesc.getText().toString();
                    createTicket(titleID, subject);
                }
            }
        });


        listener = new OnTitleSelectedListener() {
            @Override
            public void onItemSelected(int id, String selectedTitle) {
                dialogTicketSubjects.dismiss();
                titleID = id;
                title = selectedTitle;
                btnTitle.setText(title);
            }
        };


        root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                return;
            }
        });

        return root;
    }

    private void getTicketTitles() {
        progressBar.start();
        titles = new ArrayList<>();
        new Commands().setCompleteListener(new Commands.onCommandCompleteListener() {
            @Override
            public void onComplete(String data) {
                try {
                    JSONArray titleArray = new JSONArray(data);
                    for (int i = 0; i < titleArray.length(); i++) {
                        JSONObject titleObject = titleArray.getJSONObject(i);
                        StructTicketTitle title = new StructTicketTitle();
                        title.id = titleObject.getInt("id");
                        title.text = titleObject.getString("title");
                        titles.add(title);
                    }
                    adapter.notifyDataSetChanged();
                    progressBar.stop();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFail(String error) {
                CustomToast.showToast(App.getContext().getString(R.string.connectionError));
            }
        }).getTicketTitles();

    }

    private void createTicket(int titleID, String subject) {
            new Commands().setCompleteListener(new Commands.onCommandCompleteListener() {
                @Override
                public void onComplete(String data) {
                    try {
                        JSONObject ticketObject = new JSONObject(data);
                        int ticketID = ticketObject.getInt("id");
                        addMessageToTicket(ticketID, text);
                    } catch (JSONException e) {
                        btnSend.reset();
                        CustomToast.showToast(App.getContext().getString(R.string.oldVersionError));
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFail(String error) {
                    btnSend.reset();
                    CustomToast.showToast(App.getContext().getString(R.string.connectionError));
                }
            }).addNewTicket(titleID, subject);
        }



    private void addMessageToTicket(int ticketID, String text) {
        if(sendWithImage){
            uploadWithImage(ticketID,text);
        }else{
            sentMessageOnly(ticketID , text);
        }

    }

    private void uploadWithImage(int ticketID,String message){
        Log.i("LOGGG","with image");
        File file = new File(selectedImageUri.getPath());
        Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
        String path = HelperString.getPath(App.getContext(), selectedImageUri);
        new FileUploader()
                .url(App.SERVER_ADDRESS + "/api/add-ticket-message")
                .authorization("Bearer", App.accessToken)
                .setListener(new FileUploader.Listener() {
                    @Override
                    public void onComplete(String data) {
                        btnSend.loaded();
                        CustomToast.showToast(App.getContext().getString(R.string.yourTicketCreated));
                        App.getHandler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                ActivityMain.fragmentManager.popBackStack();
                                ActivityMain.fragmentManager.popBackStack();
                            }
                        }, 500);
                    }

                    @Override
                    public void onFail(String error) {
                        btnSend.reset();
                        CustomToast.showToast(App.getContext().getString(R.string.connectionError_checkYourConnection));

                    }

                    @Override
                    public void onProgress(long totalSize, long uploadedSize, int percent) {

                    }
                }).setImage("image", path)
                .maximumFileSize(1024*1024*3)
                .params("ticket_id", ticketID + "", "type", SEND, "text", message)
                .upload();

    }

    private void sentMessageOnly(int ticketID , String text){
        new Commands().setCompleteListener(new Commands.onCommandCompleteListener() {
            @Override
            public void onComplete(String data) {
                btnSend.loaded();
                CustomToast.showToast(App.getContext().getString(R.string.yourTicketCreated));
                App.getHandler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ActivityMain.fragmentManager.popBackStack();
                        ActivityMain.fragmentManager.popBackStack();
                    }
                }, 500);
            }

            @Override
            public void onFail(String error) {
                btnSend.reset();
                CustomToast.showToast(App.getContext().getString(R.string.connectionError));
            }
        }).addTicketMessage(ticketID, SEND, text);
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

    public interface OnTitleSelectedListener {
        void onItemSelected(int id, String title);
    }
}
