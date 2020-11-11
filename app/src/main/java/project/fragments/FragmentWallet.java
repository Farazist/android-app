package project.fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import ir.farazist.farazist_app.App;
import ir.farazist.farazist_app.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import project.activities.ActivityMain;
import project.adapters.AdapterTransactionsRecycler;
import project.connection.Commands;
import project.custom.CustomToast;
import project.enums.TransactionType;
import project.helper.HelperFragment;
import project.helper.HelperString;
import project.structures.StructTransaction;

public class FragmentWallet extends Fragment {

    public static TextView txtMoney;
    private ViewGroup loader;
    private TextView txtMoneyText;
    private LinearLayout root ;
    private boolean confirmEnable = true;

    private String targetMobileNumber = "";
    private int targetId = 0;
    private long amount = 0;
    private AdapterTransactionsRecycler adapter;
    private static ArrayList<StructTransaction> transactionsArray;
    private boolean userInfoLoaded;
    private boolean transactionsLoaded;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_wallet, container, false);
        root = (LinearLayout) rootView.findViewById(R.id.root);
        loader = (ViewGroup) rootView.findViewById(R.id.loader);
//        lstTransactions = (RecyclerView) rootView.findViewById(R.id.lstTransactions);
//        lstTransactions.setLayoutManager(new LinearLayoutManager(App.getContext()));
//        lstTransactions.setNestedScrollingEnabled(false);
        txtMoney = (TextView) rootView.findViewById(R.id.txtMoney);
        txtMoneyText = (TextView) rootView.findViewById(R.id.txtMoneyText);
        Button btnTransfer = (Button)rootView.findViewById(R.id.btnTransfer);
        Button btnTurnover = (Button)rootView.findViewById(R.id.btnTurnover);
        Button btnRequestMoney = (Button)rootView.findViewById(R.id.btnRequestMoney);

        userInfoLoaded = false;
        transactionsLoaded = false;
        if(App.account.role == 2){ // OWNER
            btnTransfer.setVisibility(View.GONE);
            txtMoneyText.setText(App.getContext().getString(R.string.farazistAccountBalance));
        }else{
            txtMoneyText.setText(App.getContext().getString(R.string.walletBalance));
        }

        firstLoding();
        getUserInfo();
        getTransactions();



//        final Dialog dialogTransfer = new Dialog(App.getCurrentActivity());
//        dialogTransfer.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
//        dialogTransfer.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
//        dialogTransfer.setContentView(R.layout.dialog_transfer_wallet);
//        Window window = dialogTransfer.getWindow();
//        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
//        window.setGravity(Gravity.CENTER);
//        Button btnSend = (Button) dialogTransfer.findViewById(R.id.btnSend);
//        Button btnCancel = (Button) dialogTransfer.findViewById(R.id.btnCancel);
//        final EditText edtMobileNumber = (EditText) dialogTransfer.findViewById(R.id.edtID);
//        final EditText edtAmount = (EditText) dialogTransfer.findViewById(R.id.edtAmount);
//        final LinearProgressBar progressBar = (LinearProgressBar) dialogTransfer.findViewById(R.id.progressBar);
//        progressBar.setProgressColor(Color.parseColor(App.getContext().getString(R.color.colorPrimaryDark)));
//
//
//        final Dialog dialogConfirmTransfer = new Dialog(App.getCurrentActivity());
//        dialogConfirmTransfer.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
//        dialogConfirmTransfer.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
//        dialogConfirmTransfer.setContentView(R.layout.dialog_confirm_transfer_wallet);
//        window = dialogConfirmTransfer.getWindow();
//        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
//        window.setGravity(Gravity.CENTER);
//        Button btnconfirm = (Button) dialogConfirmTransfer.findViewById(R.id.btnConfirm);
//        Button btnCancel2 = (Button) dialogConfirmTransfer.findViewById(R.id.btnCancel);
//        final TextView txtFrom = (TextView) dialogConfirmTransfer.findViewById(R.id.txtFrom);
//        final TextView txtTo = (TextView) dialogConfirmTransfer.findViewById(R.id.txtTo);
//        final TextView txtAmount = (TextView) dialogConfirmTransfer.findViewById(R.id.txtAmount);
//        final TextView txtName = (TextView) dialogConfirmTransfer.findViewById(R.id.txtTitle);
//        final LinearProgressBar progressBar2 = (LinearProgressBar) dialogConfirmTransfer.findViewById(R.id.progressBar);
//        progressBar2.setProgressColor(Color.parseColor(App.getContext().getString(R.color.colorPrimaryDark)));

        btnTransfer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //dialogTransfer.show();
                new HelperFragment()
                        .addToBackStack(App.FRAGMENT_CHARGING_CITIZEN_WALLET_ID)
                        .addFragment(R.id.layoutContainer, new FragmentChargingCitizenWallet(), true)
                        .commit();
            }
        });

        btnTurnover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new HelperFragment()
                        .addToBackStack(App.FRAGMENT_TRANSACTIONS_ID)
                        .addFragment(R.id.layoutContainer,new FragmentTransactions(),true)
                        .commit();
            }
        });

        btnRequestMoney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new HelperFragment()
                        .addToBackStack(App.FRAGMENT_REQUEST_MONEY_ID)
                        .addFragment(R.id.layoutContainer,new FragmentRequestMoney(),true)
                        .commit();
            }
        });

//        btnSend.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                try {
//                    HelperView.hideKeyboard(App.getCurrentActivity());
//                    if (!confirmEnable) {
//                        return;
//                    }
//                    targetMobileNumber = edtMobileNumber.getText().toString();
//                    amount = 0;
//                    if (edtAmount.getText().toString().length() > 0) {
//                        amount = Integer.parseInt(edtAmount.getText().toString());
//                    }
//                    if (targetMobileNumber.length() != 11 || !targetMobileNumber.substring(0, 2).equals("09")) {
//                        CustomToast.showToast(App.getContext().getString(R.string.invalidMobileNumber));
//                        confirmEnable = true;
//
//                    } else if (targetMobileNumber.equals(App.account.mobileNumber)) {
//                        CustomToast.showToast(App.getContext().getString(R.string.equalOriginAndDestination));
//                        confirmEnable = true;
//
//                    } else if (amount < 200) {
//                        CustomToast.showToast(App.getContext().getString(R.string.lowTransferMoney));
//                        confirmEnable = true;
//                    } else if (amount > App.account.wallet) {
//                        CustomToast.showToast(App.getContext().getString(R.string.notEnoughWallet));
//                        confirmEnable = true;
//
//                    } else {
//                        confirmEnable = false;
//                        progressBar.start();
//                        new Commands().setCompleteListener(new Commands.onCommandCompleteListener() {
//                            @Override
//                            public void onComplete(String data) {
//                                progressBar.stop();
//                                try {
//                                    confirmEnable = true;
//                                    JSONObject targetInfo = new JSONObject(data);
//                                    targetId = targetInfo.getInt("id");
//                                    String name = targetInfo.getString("name");
//                                    txtFrom.setText(App.account.mobileNumber);
//                                    txtTo.setText(targetMobileNumber);
//                                    txtName.setText(name);
//                                    txtAmount.setText(edtAmount.getText().toString() + " " + getString(R.string.tooman));
//                                    dialogTransfer.dismiss();
//                                    dialogConfirmTransfer.show();
//                                } catch (JSONException e) {
//                                    confirmEnable = true;
//                                    CustomToast.showToast(App.getContext().getString(R.string.unregisteredUser));
//                                    e.printStackTrace();
//                                } catch (Exception e) {
//
//                                }
//
//                            }
//
//                            @Override
//                            public void onFail(String error) {
//                                confirmEnable = true;
//                                progressBar.stop();
//                                CustomToast.showToast(App.getContext().getString(R.string.connectionError));
//                                dialogTransfer.dismiss();
//                                return;
//                            }
//                        }).getTargetUserInfo(targetMobileNumber);
//                    }
//                }catch (Exception e){
//
//                }
//            }
//
//        });
//
//        btnconfirm.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                    if (!confirmEnable) {
//                        return;
//                    }
//                    App.canBackPress = false;
//                    confirmEnable = false;
//                    progressBar2.start();
//                    new Commands().setCompleteListener(new Commands.onCommandCompleteListener() {
//                        @Override
//                        public void onComplete(String data) {
//                            if (data.equals("0")) {
//                                CustomToast.showToast(App.getContext().getString(R.string.lessMoney));
//                                progressBar2.stop();
//                            } else {
//                                getUserInfo();
//                                CustomToast.showToast(App.getContext().getString(R.string.successfullyTransferred));
//                                progressBar2.stop();
//                                dialogConfirmTransfer.dismiss();
//                                App.canBackPress = true;
//
//                            }
//
//                        }
//
//                        @Override
//                        public void onFail(String error) {
//                            confirmEnable = true;
//                            CustomToast.showToast(App.getContext().getString(R.string.connectionError));
//                            App.canBackPress = true;
//                        }
//                    }).transfer(targetId, (int) amount, App.getContext().getString(R.string.transfer));
//
//            }
//        });
//
//        btnCancel.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                edtAmount.setText("");
//                edtMobileNumber.setText("");
//                progressBar.stop();
//                confirmEnable = true;
//                dialogTransfer.dismiss();
//            }
//        });
//
//        btnCancel2.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                progressBar2.stop();
//                confirmEnable = true;
//                dialogConfirmTransfer.dismiss();
//            }
//        });
//
//
//        DialogInterface.OnCancelListener onCancelListener = new DialogInterface.OnCancelListener() {
//            @Override
//            public void onCancel(DialogInterface dialogInterface) {
//                confirmEnable = true;
//            }
//        };
//
//        dialogTransfer.setOnCancelListener(onCancelListener);
//        dialogConfirmTransfer.setOnCancelListener(onCancelListener);

        rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                return;
            }
        });

        return rootView;
    }

    private void getUserInfo() {
        new Commands().setCompleteListener(new Commands.onCommandCompleteListener() {
            @Override
            public void onComplete(String data) {
                userInfoLoaded = true;
                txtMoney.setText(HelperString.convertToNumberFormat(App.account.wallet+"") + "  " + App.getContext().getString(R.string.tooman));
                ActivityMain.setupConfiguration();
                confirmEnable = true;
                if(userInfoLoaded&&transactionsLoaded){
                    loaded();
                }
            }

            @Override
            public void onFail(String error) {
                loader.setVisibility(View.GONE);
                CustomToast.showToast(App.getContext().getString(R.string.connectionError));
            }
        }).getUserInfo();
    }

    private void getTransactions(){
        transactionsArray = new ArrayList<StructTransaction>();
        transactionsArray.clear();
        new Commands().setCompleteListener(new Commands.onCommandCompleteListener() {
            @Override
            public void onComplete(String data) {
                try {
//                    Log.i("LOGGG",data);
                    JSONArray transactions = new JSONArray(data);
                    for (int i = 0; i < transactions.length(); i++) {
                        JSONObject transaction = transactions.getJSONObject(i);
                        StructTransaction item = new StructTransaction();
                        item.id = transaction.getInt("id");
                        String dateTime = transaction.getString("created_at");
                        if (dateTime == null || dateTime.equals("null")) {
                            item.time = App.getContext().getString(R.string.unknown);
                            item.date = App.getContext().getString(R.string.unknown);
                        } else {
                            item.date = HelperString.getTransformedDate(dateTime.split(" ")[0]);
                            item.time = HelperString.getTransformedTime(dateTime.split(" ")[1]);
                        }
                        int amount = transaction.getInt("amount");
                        item.amount = amount;
                        item.desc = transaction.getString("description");
                        if (amount < 0) {
                            item.type = TransactionType.WITHDRAW;
                        } else {
                            item.type = TransactionType.DEPOSITE;
                        }

                        item.targetName = transaction.getJSONObject("target_user").getString("name");
                        item.targetMobileNumber = transaction.getJSONObject("target_user").getString("mobile_number");
                        item.isExpanded = false;
                        transactionsArray.add(item);
                    }


                    adapter = new AdapterTransactionsRecycler(transactionsArray);
//                    lstTransactions.setAdapter(adapter);
                    transactionsLoaded = true;
                    if(transactionsLoaded && userInfoLoaded){
                        loaded();
                    }
                } catch (JSONException e) {
                    CustomToast.showToast(getString(R.string.oldVersionError));
                    loader.setVisibility(View.GONE);
                    e.printStackTrace();
                }

            }

            @Override
            public void onFail(String error) {
                CustomToast.showToast(App.getContext().getString(R.string.connectionError));
                ActivityMain.fragmentManager.popBackStack();
            }
        }).getTransactions();

    }

    private void firstLoding(){
        root.setVisibility(View.GONE);
        loader.setVisibility(View.VISIBLE);
    }

    private void loading(){
        root.setVisibility(View.GONE);
    }

    private void loaded(){
        root.setVisibility(View.VISIBLE);
        loader.setVisibility(View.GONE);
    }

}
