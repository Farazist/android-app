package project.connection;

import android.content.SharedPreferences;
import android.util.Log;

import ir.farazist.farazist_app.App;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;

import ir.farazist.farazist_app.R;
import project.activities.ActivityMain;
import project.custom.CustomToast;
import project.fragments.FragmentMainOwner;
import project.helper.HelperString;
import project.structures.StructSystem;
import project.structures.StructWaste;


public class Commands {

    public static ArrayList<String> coverUrls = new ArrayList<String>();

    private boolean invalidData = false;

    private onCommandCompleteListener completeListener;
    private onCommandProgressListener progressListener;
    private onStreamRecievedListener streamListener;

    private OnDisconnect state;

    private interface OnDisconnect {
        void disconnect();
    }


    public interface onCommandCompleteListener {

        void onComplete(String data);

        void onFail(String error);
    }

    public interface onStreamRecievedListener {
        void onRecieved(InputStream inputStream);
    }

    public interface onCommandProgressListener {
        void onProgress(float totalSize, float downloadedSize);

        void onComplete();

        void onFail(String error);
    }


    public Commands setCompleteListener(onCommandCompleteListener completeListener) {
        this.completeListener = completeListener;
        return this;
    }

    public Commands setStreamListener(onStreamRecievedListener streamListener) {
        this.streamListener = streamListener;
        return this;
    }

    // -------------------- FUNCTIONS --------------------------

    public void editUserSetting(String... params) {
        String name = params[0];
        String provinceID = params[1];
        String cityID = params[2];
        String cardNumber = params[3];
        String address = params[4];
        WebServiceModule editor = new WebServiceModule();
        if (params.length == 6) {
            String password = params[5];
            editor.params("name", name, "province_id", provinceID, "city_id", cityID, "address", address, "card_number", cardNumber, "password", password);
        } else {
            editor.params("name", name, "province_id", provinceID, "city_id", cityID, "address", address, "card_number", cardNumber);
        }

        editor
                .url(App.SERVER_ADDRESS + "/api/edit-user")
                .authorization("Bearer", App.accessToken)
                .listener(new WebServiceModule.Listener() {
                    @Override
                    public void onSuccess(String data) {
                        if (completeListener != null) {
                            completeListener.onComplete(data);
                        }
                    }

                    @Override
                    public void onFail(String error) {
                        if (completeListener != null) {
                            completeListener.onFail(error);
                        }
                    }
                }).connect();
    }

    public void editUserSystem(int systemId) {
        WebServiceModule editor = new WebServiceModule();
        editor
                .url(App.SERVER_ADDRESS + "/api/edit-user")
                .authorization("Bearer", App.accessToken)
                .params("system_id", systemId + "")
                .listener(new WebServiceModule.Listener() {
                    @Override
                    public void onSuccess(String data) {
                        if (completeListener != null) {
                            completeListener.onComplete(data);
                        }
                    }

                    @Override
                    public void onFail(String error) {
                        if (completeListener != null) {
                            completeListener.onFail(error);
                        }
                    }
                }).connect();
    }

    public void getFAQs() {
        WebServiceModule faqsGetter = new WebServiceModule();
        faqsGetter
                .url(App.SERVER_ADDRESS + "/api/get-faqs")
                .authorization("Bearer", App.accessToken)
                .listener(new WebServiceModule.Listener() {
                    @Override
                    public void onSuccess(String data) {
                        if (completeListener != null) {
                            completeListener.onComplete(data);
                        }
                    }

                    @Override
                    public void onFail(String error) {
                        if (completeListener != null) {
                            completeListener.onFail(error);
                        }
                    }
                }).connect();

    }

    public void editSystem(int systemId, String state) {
        WebServiceModule systemEditor = new WebServiceModule();
        systemEditor
                .url(App.SERVER_ADDRESS + "/api/edit-system")
                .authorization("Bearer", App.accessToken)
                .params("id", systemId + ""
                        , "state", state)
                .listener(new WebServiceModule.Listener() {
                    @Override
                    public void onSuccess(String data) {
                        if (completeListener != null) {
                            completeListener.onComplete(data);
                        }
                    }

                    @Override
                    public void onFail(String error) {
                        if (completeListener != null) {
                            completeListener.onFail(error);
                        }
                    }
                }).connect();
    }

    public void getSystemDeliveries(int id) {
        WebServiceModule systemDeliveries = new WebServiceModule();
        systemDeliveries
                .url(App.SERVER_ADDRESS + "/api/get-system-deliveries")
                .params("id", id + "")
                .authorization("Bearer", App.accessToken)
                .listener(new WebServiceModule.Listener() {
                    @Override
                    public void onSuccess(String data) {
                        if (completeListener != null) {
                            completeListener.onComplete(data);
                        }
                    }

                    @Override
                    public void onFail(String error) {
                        if (completeListener != null) {
                            completeListener.onFail(error);
                        }
                    }
                }).connect();
    }

    public void getSystemDetails(int id) {
        WebServiceModule systemDetailsGetter = new WebServiceModule();
        systemDetailsGetter
                .url(App.SERVER_ADDRESS + "/api/get-system")
                .params("id", id + "")
                .authorization("Bearer", App.accessToken)
                .listener(new WebServiceModule.Listener() {
                    @Override
                    public void onSuccess(String data) {
                        if (completeListener != null) {
                            completeListener.onComplete(data);
                        }
                    }

                    @Override
                    public void onFail(String error) {
                        if (completeListener != null) {
                            completeListener.onFail(error);
                        }
                    }
                }).connect();
    }

    public void getSystems() {
        WebServiceModule systemsGetter = new WebServiceModule();
        systemsGetter
                .url(App.SERVER_ADDRESS + "/api/get-systems")
                .authorization("Bearer", App.accessToken)
                .listener(new WebServiceModule.Listener() {
                    @Override
                    public void onSuccess(String data) {
                        if (completeListener != null) {
                            completeListener.onComplete(data);
                        }
                    }

                    @Override
                    public void onFail(String error) {
                        if (completeListener != null) {
                            completeListener.onFail(error);
                        }
                    }
                }).connect();
    }

    public void getUserDeliveries() {
        WebServiceModule aplicant = new WebServiceModule();
        aplicant
                .url(App.SERVER_ADDRESS + "/api/get-user-deliveries")
                .authorization("Bearer", App.accessToken)
                .listener(new WebServiceModule.Listener() {
                    @Override
                    public void onSuccess(String data) {
                        if (completeListener != null) {
                            completeListener.onComplete(data);
                        }
                    }

                    @Override
                    public void onFail(String error) {
                        if (completeListener != null) {
                            completeListener.onFail(error);
                        }
                    }
                }).connect();
    }

    public void getMyPassword() {
        WebServiceModule module = new WebServiceModule();
        module.url(App.SERVER_ADDRESS + "/api/change-user-password")
                .authorization("Bearer", App.accessToken)
                .listener(new WebServiceModule.Listener() {
                    @Override
                    public void onSuccess(String data) {
                        if (completeListener != null) {
                            completeListener.onComplete(data);
                        }
                    }

                    @Override
                    public void onFail(String error) {
                        if (completeListener != null) {
                            completeListener.onFail(error);
                        }
                    }
                }).connect();
    }

    public void editSystemActivation(JSONObject systemActivation) {
        WebServiceModule module = new WebServiceModule();
        module
                .url(App.SERVER_ADDRESS + "/api/edit-system")
                .authorization("Bearer", App.accessToken)
                .listener(new WebServiceModule.Listener() {
                    @Override
                    public void onSuccess(String data) {
                        if (completeListener != null) {
                            completeListener.onComplete(data);
                        }
                    }

                    @Override
                    public void onFail(String error) {
                        if (completeListener != null) {
                            completeListener.onFail(error);
                        }
                    }
                }).sendJson(systemActivation.toString());
    }

    public void editSystemItems(JSONObject itemsSetting) {
        WebServiceModule module = new WebServiceModule();
        module
                .url(App.SERVER_ADDRESS + "/api/edit-items")
                .authorization("Bearer", App.accessToken)
                .listener(new WebServiceModule.Listener() {
                    @Override
                    public void onSuccess(String data) {
                        if (completeListener != null) {
                            completeListener.onComplete(data);
                        }
                    }

                    @Override
                    public void onFail(String error) {
                        if (completeListener != null) {
                            completeListener.onFail(error);
                        }
                    }
                }).sendJson(itemsSetting.toString());
    }


    public void editDelivery(JSONObject json) {
        WebServiceModule editor = new WebServiceModule();
        editor
                .url(App.SERVER_ADDRESS + "/api/edit-delivery")
                .authorization("Bearer", App.accessToken)
                .listener(new WebServiceModule.Listener() {
                    @Override
                    public void onSuccess(String data) {
                        if (completeListener != null) {
                            completeListener.onComplete(data);
                        }
                    }

                    @Override
                    public void onFail(String error) {
                        if (completeListener != null) {
                            completeListener.onFail(error);
                        }
                    }
                }).sendJson(json.toString());
    }

    public void getLastUserDelivery() {
        WebServiceModule aplicant = new WebServiceModule();
        aplicant
                .url(App.SERVER_ADDRESS + "/api/get-last-user-delivery")
                .authorization("Bearer", App.accessToken)
                .listener(new WebServiceModule.Listener() {
                    @Override
                    public void onSuccess(String data) {
                        if (completeListener != null) {
                            completeListener.onComplete(data);
                        }
                    }

                    @Override
                    public void onFail(String error) {
                        if (completeListener != null) {
                            completeListener.onFail(error);
                        }
                    }
                }).connect();
    }


    public void sendPayCashSMS(String mobileNumber, String code) {
        WebServiceModule smsSender = new WebServiceModule();
        smsSender.authorization("Bearer", App.accessToken)
                .url(App.SERVER_ADDRESS + "/api/verification-sms").params(
                "mobile_number", mobileNumber,
                "code", code

        ).listener(new WebServiceModule.Listener() {
            @Override
            public void onSuccess(String data) {
                if (completeListener != null) {
                    completeListener.onComplete(data);
                }
            }

            @Override
            public void onFail(String error) {
                if (completeListener != null) {
                    completeListener.onFail(error);
                }
            }
        }).connect();
    }

    public void sendVerificationSMS(String mobileNumber, String code) {
        WebServiceModule smsSender = new WebServiceModule();
        smsSender
                .authorization("Bearer", App.accessToken)
                .url(App.SERVER_ADDRESS + "/api/verification-sms").params(
                "mobile_number", mobileNumber,
                "code", code
        ).listener(new WebServiceModule.Listener() {
            @Override
            public void onSuccess(String data) {
                if (completeListener != null) {
                    completeListener.onComplete(data);
                }
            }

            @Override
            public void onFail(String error) {
                if (completeListener != null) {
                    completeListener.onFail(error);
                }
            }
        }).connect();
    }

    public void checkMobileNumber(String mobileNumber) {
        WebServiceModule check = new WebServiceModule();
        check.url(App.SERVER_ADDRESS + "/api/check-user").params(
                "mobile_number", mobileNumber
        ).listener(new WebServiceModule.Listener() {
            @Override
            public void onSuccess(String data) {

                if (completeListener != null) {
                    completeListener.onComplete(data);
                }
            }

            @Override
            public void onFail(String error) {
                if (completeListener != null) {
                    completeListener.onFail(error);
                }
            }
        }).connect();
    }

    public void checkUser(String mobileNumber, String password) {
        WebServiceModule check = new WebServiceModule();
        check.url(App.SERVER_ADDRESS + "/api/check-user").params(
                "mobile_number", mobileNumber,
                "password", password
        ).listener(new WebServiceModule.Listener() {
            @Override
            public void onSuccess(String data) {
                if (completeListener != null) {
                    completeListener.onComplete(data);
                }
            }

            @Override
            public void onFail(String error) {
                if (completeListener != null) {
                    completeListener.onFail(error);
                }
            }
        }).connect();
    }

    public void checkUser(String mobileNumber) {
        WebServiceModule check = new WebServiceModule();
        check.url(App.SERVER_ADDRESS + "/api/check-user").params(
                "mobile_number", mobileNumber
        ).listener(new WebServiceModule.Listener() {
            @Override
            public void onSuccess(String data) {
                if (completeListener != null) {
                    completeListener.onComplete(data);
                }
            }

            @Override
            public void onFail(String error) {
                if (completeListener != null) {
                    completeListener.onFail(error);
                }
            }
        }).connect();
    }


    public void signUp(String name, String mobile, int provinceID, int cityID, String address, String password) {

        WebServiceModule signUp = new WebServiceModule();
        signUp.url(App.SERVER_ADDRESS + "/api/signup-user").params(
                "name", name,
                "province_id", provinceID + "",
                "city_id", cityID + "",
                "address", address,
                "mobile_number", mobile,
                "password", password
        ).listener(new WebServiceModule.Listener() {
            @Override
            public void onSuccess(String data) {
                try {
                    SharedPreferences.Editor editor = App.getPreferences().edit();
                    editor.putString("PASSWORD", "");
                    editor.putString("MOBILE_NUMBER", "");
                    editor.commit();
                    saveUserInfo(data, true);
                    if (completeListener != null) {
                        completeListener.onComplete(data);
                    }
                } catch (JSONException e) {
                    CustomToast.showToast(App.getContext().getString(R.string.oldVersionError));
                    e.printStackTrace();
                }

            }

            @Override
            public void onFail(String error) {
                if (completeListener != null) {
                    completeListener.onFail(error);
                }
            }
        }).connect();

    }


    public void login(String mobileNumber, String password) {
        String url = App.SERVER_ADDRESS + "/api/signin-user";
        WebServiceModule login = new WebServiceModule();
        login
                .url(url)
                .params("mobile_number", mobileNumber,
                        "password", password)
                .listener(new WebServiceModule.Listener() {
                    @Override
                    public void onSuccess(String data) {
                        try {
                            saveUserInfo(data, true);
                            if (completeListener != null) {
                                completeListener.onComplete(data);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            CustomToast.showToast(App.getContext().getString(R.string.oldVersionError));

                        }

                    }

                    @Override
                    public void onFail(String error) {
                        if (completeListener != null) {
                            completeListener.onFail(error);
                        }
                    }
                }).connect();

    }

    public void getTargetUserInfo(String mobileNumberOrID) {
        String paramKey = "";
        if (mobileNumberOrID.startsWith("09") && mobileNumberOrID.length() == 11) {
            paramKey = "mobile_number";
        } else {
            paramKey = "id";
        }
        String url = App.SERVER_ADDRESS + "/api/get-target-user";
        WebServiceModule module = new WebServiceModule();
        module
                .url(url)
                .params(paramKey, mobileNumberOrID)
                .authorization("Bearer", App.accessToken)
                .listener(new WebServiceModule.Listener() {
                    @Override
                    public void onSuccess(String data) {
                        if (completeListener != null) {
                            completeListener.onComplete(data);
                        }
                    }

                    @Override
                    public void onFail(String error) {
                        if (completeListener != null) {
                            completeListener.onFail(error);
                        }
                    }
                }).connect();
    }

    public void getSystemDeliveriesCount(int systemID) {
        WebServiceModule module = new WebServiceModule();
        module
                .url(App.SERVER_ADDRESS + "/api/get-system-deliveries-count")
                .authorization("Bearer", App.accessToken)
                .params("system_id", systemID + "")
                .listener(new WebServiceModule.Listener() {
                    @Override
                    public void onSuccess(String data) {
                        if (completeListener != null) {
                            completeListener.onComplete(data);
                        }
                    }

                    @Override
                    public void onFail(String error) {
                        if (completeListener != null) {
                            completeListener.onFail(error);
                        }
                    }
                }).connect();
    }

    public void checkNewTicketsCount() {
        WebServiceModule module = new WebServiceModule();
        module
                .url(App.SERVER_ADDRESS + "/api/get-new-ticket-messages-count")
                .authorization("Bearer", App.accessToken)
                .listener(new WebServiceModule.Listener() {
                    @Override
                    public void onSuccess(String data) {
                        if (completeListener != null) {
                            completeListener.onComplete(data);
                        }
                    }

                    @Override
                    public void onFail(String error) {
                        if (completeListener != null) {
                            completeListener.onFail(error);
                        }
                    }
                }).connect();
    }

    public void getUserInfo() {
        App.accessToken = App.getPreferences().getString("TOCKEN", "");
        String url = App.SERVER_ADDRESS + "/api/get-user";
        WebServiceModule getInfo = new WebServiceModule();
        getInfo
                .url(url)
                .authorization("Bearer", App.accessToken)
                .listener(new WebServiceModule.Listener() {
                    @Override
                    public void onSuccess(String data) {
                        try {
                            saveUserInfo(data, false);
                            if (completeListener != null) {
                                completeListener.onComplete(data);
                            }
                        } catch (JSONException e) {
                            if (completeListener != null) {
                                completeListener.onFail("json");
                            }
                        }

                    }

                    @Override
                    public void onFail(String error) {
                        if (completeListener != null) {
                            completeListener.onFail("connection");
                        }
                    }
                }).connect();

    }

    public void getSystemSliders(int systemId) {
        WebServiceModule module = new WebServiceModule();
        module
                .url(App.SERVER_ADDRESS + "/api/get-system-sliders")
                .authorization("Bearer", App.accessToken)
                .params("system_id", systemId + "")
                .listener(new WebServiceModule.Listener() {
                    @Override
                    public void onSuccess(String data) {
                        if (completeListener != null) {
                            completeListener.onComplete(data);
                        }
                    }

                    @Override
                    public void onFail(String error) {
                        if (completeListener != null) {
                            completeListener.onFail(error);
                        }
                    }
                }).connect();
    }

    public void getAppVersionsInfo() {
        WebServiceModule module = new WebServiceModule();
        module
                .url(App.SERVER_ADDRESS + "/api/get-last-android-app-version")
                .authorization("Bearer", App.accessToken)
                .listener(new WebServiceModule.Listener() {
                    @Override
                    public void onSuccess(String data) {
                        if (completeListener != null) {
                            completeListener.onComplete(data);
                        }
                    }

                    @Override
                    public void onFail(String error) {
                        if (completeListener != null) {
                            completeListener.onFail(error);
                        }
                    }
                }).connect();
    }


    public void transfer(int targetUserId, int amount, String description) {
        WebServiceModule transfer = new WebServiceModule();
        transfer.url(App.SERVER_ADDRESS + "/api/transfer").params(
                "target_user_id", targetUserId + "",
                "amount", amount + "",
                "description", description
        ).authorization("Bearer", App.accessToken)
                .listener(new WebServiceModule.Listener() {
                    @Override
                    public void onSuccess(String data) {
                        if (completeListener != null) {
                            completeListener.onComplete(data);
                        }
                    }

                    @Override
                    public void onFail(String error) {
                        if (completeListener != null) {
                            completeListener.onFail(error);
                        }
                    }
                }).connect();
    }

    public void sendQRCode(String data) {
        WebServiceModule module = new WebServiceModule();
        module
                .url(App.SERVER_ADDRESS + "/api/send-qrcode-signin-token")
                .authorization("Bearer", App.accessToken)
                .params("qrcode_signin_token", data)
                .listener(new WebServiceModule.Listener() {
                    @Override
                    public void onSuccess(String data) {
                        if (completeListener != null) {
                            completeListener.onComplete(data);
                        }
                    }

                    @Override
                    public void onFail(String error) {
                        if (completeListener != null) {
                            completeListener.onFail(error);
                        }
                    }
                }).connect();
    }

    public void getOwnerWasteTypes() {
        WebServiceModule transfer = new WebServiceModule();
        transfer
                .url(App.SERVER_ADDRESS + "/api/get-items")
                .params("system_id", App.account.systemID + "")
                .authorization("Bearer", App.accessToken)
                .listener(new WebServiceModule.Listener() {
                    @Override
                    public void onSuccess(String data) {
                        if (completeListener != null) {
                            completeListener.onComplete(data);
                        }

                    }

                    @Override
                    public void onFail(String error) {
                        if (completeListener != null) {
                            completeListener.onFail(error);
                        }
                    }
                }).connect();
    }

    public void getWastesTypes() {
        WebServiceModule transfer = new WebServiceModule();
        transfer
                .url(App.SERVER_ADDRESS + "/api/get-items")
                .authorization("Bearer", App.accessToken)
                .listener(new WebServiceModule.Listener() {
                    @Override
                    public void onSuccess(String data) {
                        try {
                            App.wastes = new ArrayList<>();
                            App.wastes.clear();
                            JSONArray items = new JSONArray(data);
                            for (int i = 0; i < items.length(); i++) {
                                JSONObject item = items.getJSONObject(i);
                                StructWaste waste = new StructWaste();
                                waste.id = item.getInt("id");
                                waste.unit = item.getInt("price");
                                waste.name = item.getString("name");
                                if (item.has("image")) {
                                    String url = item.getString("image");
                                    if (url != null && url.length() > 0 && !url.toLowerCase().equals("null")) {
                                        waste.imgUrl = App.SERVER_ADDRESS + item.getString("image");
                                    } else {
                                        waste.imgUrl = "";
                                    }
                                }
                                waste.count = 0;
                                App.wastes.add(waste);
                            }
                            if (completeListener != null) {
                                completeListener.onComplete(data);
                            }
                        } catch (JSONException e) {
                            if (completeListener != null) {
                                completeListener.onFail(e.getMessage());
                            }
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onFail(String error) {
                        if (completeListener != null) {
                            completeListener.onFail(error);
                        }
                    }
                }).connect();
    }

    public void getInformations() {
        WebServiceModule infoModule = new WebServiceModule();
        infoModule.url(App.SERVER_ADDRESS + "/api/get-informations")
                .authorization("Bearer", App.accessToken)
                .listener(new WebServiceModule.Listener() {
                    @Override
                    public void onSuccess(String data) {
                        if (completeListener != null) {
                            completeListener.onComplete(data);
                        }
                    }

                    @Override
                    public void onFail(String error) {
                        if (completeListener != null) {
                            completeListener.onFail(error);
                        }
                    }
                }).connect();
    }

    public void addNewDelivery(int userId,
                               int systemId,
                               JSONArray items,
                               JSONArray customItems,
                               String undefinedItemName,
                               int undefinedItemWeight,
                               int provinceID,
                               int cityID,
                               String address,
                               String state) {
        JSONObject data = new JSONObject();
        try {
            data.put("user_id", userId);
            data.put("system_id", systemId);
            data.put("address", address);
            data.put("items", items);
            data.put("custom_items", customItems);
            data.put("undefined_items_name", undefinedItemName);
            data.put("undefined_items_weight", undefinedItemWeight);
            data.put("state", state);
            data.put("province_id", provinceID);
            data.put("city_id", cityID);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        WebServiceModule newDelivery = new WebServiceModule();
        newDelivery.url(App.SERVER_ADDRESS + "/api/add-new-delivery")
                .authorization("Bearer", App.accessToken)
                .listener(new WebServiceModule.Listener() {
                    @Override
                    public void onSuccess(String data) {
                        if (completeListener != null) {
                            completeListener.onComplete(data);
                        }
                    }

                    @Override
                    public void onFail(String error) {
                        if (completeListener != null) {
                            completeListener.onFail(error);
                        }
                    }
                }).sendJson(data.toString());
    }


    public void getTransactions() {
        WebServiceModule getter = new WebServiceModule();
        getter
                .url(App.SERVER_ADDRESS + "/api/get-user-transactions")
                .authorization("Bearer", App.accessToken)
                .listener(new WebServiceModule.Listener() {
                    @Override
                    public void onSuccess(String data) {
                        if (completeListener != null) {
                            completeListener.onComplete(data);
                        }
                    }

                    @Override
                    public void onFail(String error) {
                        if (completeListener != null) {
                            completeListener.onFail(error);
                        }
                    }
                }).connect();
    }

    public void getMyCitizensCount(int systemID) {

        WebServiceModule module = new WebServiceModule();
        module
                .url(App.SERVER_ADDRESS + "/api/get-system-citizens-count")
                .params("system_id", systemID + "")
                .authorization("Bearer", App.accessToken)
                .listener(new WebServiceModule.Listener() {
                    @Override
                    public void onSuccess(String data) {
                        if (completeListener != null) {
                            completeListener.onComplete(data);
                        }
                    }

                    @Override
                    public void onFail(String error) {
                        if (completeListener != null) {
                            completeListener.onFail(error);
                        }
                    }
                }).connect();

    }

    public void changeUserPassword(String mobileNumber) {
        WebServiceModule module = new WebServiceModule();
        module
                .url(App.SERVER_ADDRESS + "/api/change-user-password")
                .params("mobile_number", mobileNumber)
                .authorization("Bearer", App.accessToken)
                .listener(new WebServiceModule.Listener() {
                    @Override
                    public void onSuccess(String data) {
                        if (completeListener != null) {
                            completeListener.onComplete(data);
                        }
                    }

                    @Override
                    public void onFail(String error) {
                        if (completeListener != null) {
                            completeListener.onFail(error);
                        }
                    }
                }).connect();
    }

    public void getTicketMessage(int ticketID) {
        WebServiceModule module = new WebServiceModule();
        module
                .url(App.SERVER_ADDRESS + "/api/get-ticket-messages")
                .params("ticket_id", ticketID + "")
                .authorization("Bearer", App.accessToken)
                .listener(new WebServiceModule.Listener() {
                    @Override
                    public void onSuccess(String data) {
                        if (completeListener != null) {
                            completeListener.onComplete(data);
                        }
                    }

                    @Override
                    public void onFail(String error) {
                        if (completeListener != null) {
                            completeListener.onFail(error);
                        }
                    }
                }).connect();
    }

    public void seenTicket(int ticketID) {
        WebServiceModule module = new WebServiceModule();
        module
                .url(App.SERVER_ADDRESS + "/api/edit-ticket-messages")
                .authorization("Bearer", App.accessToken)
                .params("ticket_id", ticketID + "")
                .listener(new WebServiceModule.Listener() {
                    @Override
                    public void onSuccess(String data) {
                        if (completeListener != null) {
                            completeListener.onComplete(data);
                        }
                    }

                    @Override
                    public void onFail(String error) {
                        if (completeListener != null) {
                            completeListener.onFail(error);
                        }
                    }
                }).connect();
    }

    public void addMoneyRequest(int amount){
        WebServiceModule module = new WebServiceModule();
        module.url(App.SERVER_ADDRESS + "/api/add-money-request")
                .params("amount",amount+"")
                .authorization("Bearer", App.accessToken)
                .listener(new WebServiceModule.Listener() {
                    @Override
                    public void onSuccess(String data) {
                        if (completeListener != null) {
                            completeListener.onComplete(data);
                        }
                    }

                    @Override
                    public void onFail(String error) {
                        if (completeListener != null) {
                            completeListener.onFail(error);
                        }
                    }
                }).connect();
    }

    public void editMoneyRequest(int moneyRequestID , int amount , String state){
        WebServiceModule module = new WebServiceModule();
        module.url(App.SERVER_ADDRESS + "/api/edit-money-request")
                .authorization("Bearer", App.accessToken)
                .listener(new WebServiceModule.Listener() {
                    @Override
                    public void onSuccess(String data) {
                        if (completeListener != null) {
                            completeListener.onComplete(data);
                        }
                    }

                    @Override
                    public void onFail(String error) {
                        if (completeListener != null) {
                            completeListener.onFail(error);
                        }
                    }
                });
        if(amount <0){
            module.params("money_request_id",moneyRequestID+"","state",state);
        }else{
            module.params("money_request_id",moneyRequestID+"","state",state,"amount",amount+"");
        }
        module.connect();
    }

    public void getLastUserMoneyRequest() {
        WebServiceModule module = new WebServiceModule();
        module.url(App.SERVER_ADDRESS + "/api/get-last-user-money-request")
                .authorization("Bearer", App.accessToken)
                .listener(new WebServiceModule.Listener() {
                    @Override
                    public void onSuccess(String data) {
                        if (completeListener != null) {
                            completeListener.onComplete(data);
                        }
                    }

                    @Override
                    public void onFail(String error) {
                        if (completeListener != null) {
                            completeListener.onFail(error);
                        }
                    }
                }).connect();
    }

    public void addTicketMessage(int ticketID, String type, String text) {
        WebServiceModule module = new WebServiceModule();
        module
                .url(App.SERVER_ADDRESS + "/api/add-ticket-message")
                .authorization("Bearer", App.accessToken)
                .params("ticket_id", ticketID + ""
                        , "type", type
                        , "text", text)
                .listener(new WebServiceModule.Listener() {
                    @Override
                    public void onSuccess(String data) {
                        if (completeListener != null) {
                            completeListener.onComplete(data);
                        }
                    }

                    @Override
                    public void onFail(String error) {
                        if (completeListener != null) {
                            completeListener.onFail(error);
                        }
                    }
                }).connect();

    }

    public void addNewTicket(int groupID, String subject) {
        WebServiceModule module = new WebServiceModule();
        module
                .url(App.SERVER_ADDRESS + "/api/add-ticket")
                .params("group_id", groupID + "",
                        "subject", subject)
                .authorization("Bearer", App.accessToken)
                .listener(new WebServiceModule.Listener() {
                    @Override
                    public void onSuccess(String data) {
                        if (completeListener != null) {
                            completeListener.onComplete(data);
                        }
                    }

                    @Override
                    public void onFail(String error) {
                        if (completeListener != null) {
                            completeListener.onFail(error);
                        }
                    }
                }).connect();
    }

    public void getTickets() {
        WebServiceModule module = new WebServiceModule();
        module
                .url(App.SERVER_ADDRESS + "/api/get-user-tickets")
                .authorization("Bearer", App.accessToken)
                .listener(new WebServiceModule.Listener() {
                    @Override
                    public void onSuccess(String data) {
                        if (completeListener != null) {
                            completeListener.onComplete(data);
                        }
                    }

                    @Override
                    public void onFail(String error) {
                        if (completeListener != null) {
                            completeListener.onFail(error);
                        }
                    }
                }).connect();
    }

    public void getTicketTitles() {
        WebServiceModule module = new WebServiceModule();
        module
                .url(App.SERVER_ADDRESS + "/api/get-ticket-groups")
                .authorization("Bearer", App.accessToken)
                .listener(new WebServiceModule.Listener() {
                    @Override
                    public void onSuccess(String data) {
                        if (completeListener != null) {
                            completeListener.onComplete(data);
                        }
                    }

                    @Override
                    public void onFail(String error) {
                        if (completeListener != null) {
                            completeListener.onFail(error);
                        }
                    }
                }).connect();
    }

    public void getProvinces() {
        WebServiceModule module = new WebServiceModule();
        module
                .url(App.SERVER_ADDRESS + "/api/get-provinces")
                .listener(new WebServiceModule.Listener() {
                    @Override
                    public void onSuccess(String data) {
                        if (completeListener != null) {
                            completeListener.onComplete(data);
                        }
                    }

                    @Override
                    public void onFail(String error) {
                        if (completeListener != null) {
                            completeListener.onComplete(error);
                        }
                    }
                }).connect();

    }

    public void deleteSystemImage(int id) {
        WebServiceModule module = new WebServiceModule();
        module.url(App.SERVER_ADDRESS + "/api/delete-system-image")
                .params("id", id + "")
                .authorization("Bearer", App.accessToken)
                .listener(new WebServiceModule.Listener() {
                    @Override
                    public void onSuccess(String data) {
                        if (completeListener != null) {
                            completeListener.onComplete(data);
                        }
                    }

                    @Override
                    public void onFail(String error) {
                        if (completeListener != null) {
                            completeListener.onComplete(error);
                        }
                    }
                }).connect();
    }

    public void deleteUserImage() {
        WebServiceModule module = new WebServiceModule();
        module.url(App.SERVER_ADDRESS + "/api/delete-user-image")
                .authorization("Bearer", App.accessToken)
                .listener(new WebServiceModule.Listener() {
                    @Override
                    public void onSuccess(String data) {
                        if (completeListener != null) {
                            completeListener.onComplete(data);
                        }
                    }

                    @Override
                    public void onFail(String error) {
                        if (completeListener != null) {
                            completeListener.onComplete(error);
                        }
                    }
                }).connect();
    }

    public void getProvinceCities(int provinceID) {
        WebServiceModule module = new WebServiceModule();
        module
                .url(App.SERVER_ADDRESS + "/api/get-province-cities")
                .params("province_id", provinceID + "")
                .listener(new WebServiceModule.Listener() {
                    @Override
                    public void onSuccess(String data) {
                        if (completeListener != null) {
                            completeListener.onComplete(data);
                        }
                    }

                    @Override
                    public void onFail(String error) {
                        if (completeListener != null) {
                            completeListener.onComplete(error);
                        }
                    }
                }).connect();

    }

    public void getSliders() {
        WebServiceModule getter = new WebServiceModule();
        getter
                .url(App.SERVER_ADDRESS + "/api/get-sliders")
                .authorization("Bearer", App.accessToken)
                .listener(new WebServiceModule.Listener() {
                    @Override
                    public void onSuccess(String data) {
                        if (completeListener != null) {
                            completeListener.onComplete(data);
                        }
                    }

                    @Override
                    public void onFail(String error) {
                        if (completeListener != null) {
                            completeListener.onFail(error);
                        }
                    }
                }).connect();
    }


    private void saveUserInfo(String data, boolean saveToken) throws JSONException {
        JSONObject user = null;
        user = new JSONObject(data);
        if (saveToken) {
            App.accessToken = user.getString("access_token");
            SharedPreferences.Editor editor = App.getPreferences().edit();
            editor.putString("TOCKEN", App.accessToken);
            editor.commit();
        }
        App.account.password = App.getPreferences().getString("PASSWORD", "");
        App.account.id = user.getInt("id");
        App.account.name = user.getString("name");
        App.account.address = user.getString("address");
        App.account.representer_code = user.getInt("representer_code");
        App.account.wallet = user.getInt("wallet");
        App.account.role = user.getInt("role");
        App.account.systemID = user.getInt("system_id");

        String avatarUrl = user.getString("image");
        if (avatarUrl != null && !avatarUrl.equals("null") && avatarUrl.length() > 1) {
            App.account.avatarUrl = App.SERVER_ADDRESS + avatarUrl;
        } else {
            App.account.avatarUrl = "";
        }
        App.account.gmail = user.getString("google_id");
        App.account.email = user.getString("email");
        App.account.mobileNumber = user.getString("mobile_number");

        String cardNumber = user.getString("card_number");
        if (cardNumber == null || cardNumber.toLowerCase().equals("null") || cardNumber.length() < 19) {
            cardNumber = "";
        }

        App.account.cardNumber = cardNumber;
        if (!user.isNull("city")) {
            App.account.cityID = user.getJSONObject("city").getInt("id");
            App.account.city = user.getJSONObject("city").getString("name");
            App.account.provinceID = user.getJSONObject("city").getJSONObject("province").getInt("id");
            App.account.province = user.getJSONObject("city").getJSONObject("province").getString("name");
        } else {
            App.account.cityID = -1;
            App.account.city = "";
            App.account.provinceID = -1;
            App.account.province = "";
        }
        if (App.account.role != 2) { // client - ADMINS
            if (user.isNull("system")) {
                App.clientSystem.id = 0;
                return;
            }
            JSONObject system = user.getJSONObject("system");
            if (!system.has("id")) {
                App.clientSystem.id = 0;
                App.clientSystem.ownerId = 0;
                App.clientSystem.state = App.deactive;
                return;
            }
            App.clientSystem.id = system.getInt("id");
            App.clientSystem.ownerId = system.getInt("owner_id");
            App.clientSystem.state = system.getString("state");
            App.clientSystem.name = system.getString("name");
            App.clientSystem.cityId = system.getInt("city_id");
            App.clientSystem.cityName = system.getJSONObject("city").getString("name");
            App.clientSystem.provinceID = system.getJSONObject("city").getJSONObject("province").getInt("id");
            App.clientSystem.provinceName = system.getJSONObject("city").getJSONObject("province").getString("name");
            App.clientSystem.cityName = system.getJSONObject("city").getString("name");
            App.clientSystem.address = system.getString("address");
            String coverUrl = system.getString("image");
            if (coverUrl != null && !coverUrl.equals("null") && coverUrl.length() > 1) {
                App.clientSystem.coverUrl = App.SERVER_ADDRESS + coverUrl;
            } else {
                App.clientSystem.coverUrl = "";
            }
            App.clientSystem.workTime = system.getJSONArray("work_times").toString();
//            App.clientSystem.rate = (float) system.getDouble("rate");
        } else {
            JSONArray systems = user.getJSONArray("systems");
            App.ownerSystems.clear();
            for (int i = 0; i < systems.length(); i++) {
                JSONObject system = systems.getJSONObject(i);
                StructSystem mySystem = new StructSystem();
                mySystem.id = system.getInt("id");
                mySystem.ownerId = system.getInt("owner_id");
                mySystem.state = system.getString("state");
                mySystem.name = system.getString("name");
                mySystem.cityId = system.getInt("city_id");
                mySystem.cityName = system.getJSONObject("city").getString("name");
                mySystem.address = system.getString("address");
                String coverUrl = system.getString("image");
                if (coverUrl != null && !coverUrl.equals("null") && coverUrl.length() > 1) {
                    mySystem.coverUrl = App.SERVER_ADDRESS + coverUrl;
                } else {
                    mySystem.coverUrl = "";
                }
                //mySystem.workTime = system.getJSONArray("work_times").toString();
                mySystem.cityName = system.getJSONObject("city").getString("name");
                mySystem.provinceID = system.getJSONObject("city").getJSONObject("province").getInt("id");
                mySystem.provinceName = system.getJSONObject("city").getJSONObject("province").getString("name");
//                mySystem.rate = (float) system.getDouble("rate");
                App.ownerSystems.add(mySystem);
            }
        }
        if (FragmentMainOwner.txtFarazistBalance != null) {
            FragmentMainOwner.txtFarazistBalance.setText(HelperString.convertToNumberFormat(App.account.wallet + "") + "  " + App.getContext().getString(R.string.tooman));
        }
        ActivityMain.setupConfiguration();

    }


}
