package project.services;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

import project.connection.Commands;

public class ServiceCheckWallet extends Service {

    private boolean canCheck = true;

        @Nullable
        @Override
        public IBinder onBind(Intent intent) {
            return null;
        }

        @Override
        public void onCreate() {
            super.onCreate();
        }


        @Override
        public int onStartCommand(final Intent intent, final int flags, final int startId) {
//             new Thread(new Runnable() {
//                 @Override
//                 public void run() {
//                     while (App.getPreferences().getBoolean("LOGIN",false)) {
//                         try {
//                             Thread.sleep(4000);
//                             if(canCheck) {
//                                 canCheck = false;
//                                 new Commands().setCompleteListener(new Commands.onCommandCompleteListener() {
//                                     @Override
//                                     public void onComplete(String wallet) {
//                                        int newWallet = Integer.parseInt(wallet);
//                                        int oldWallet = App.getPreferences().getInt("WALLET",0);
//                                        if(newWallet > oldWallet || newWallet<oldWallet){
//                                            App.account.wallet = newWallet;
//                                            SharedPreferences.Editor editor = App.getPreferences().edit();
//                                            editor.putInt("WALLET",newWallet);
//                                            editor.commit();
//                                            // show notification
//                                            String message = "";
//                                            if(newWallet > oldWallet) {
//                                                message = getString(R.string.amount) + " " + (newWallet-oldWallet) + " " + getString(R.string.deposit);
//                                            }else{
//                                                message = getString(R.string.amount) + " " + (oldWallet - newWallet) + " " + getString(R.string.withdraw);
//                                            }
//                                            Notification notification = new NotificationCompat.Builder(App.getContext(), App.CHANNEL_1_ID)
//                                                    .setSmallIcon(R.drawable.farazist)
//                                                    .setContentTitle(getString(R.string.wallet))
//                                                    .setContentText(message)
//                                                    .setPriority(NotificationCompat.PRIORITY_HIGH)
//                                                    .setCategory(NotificationCompat.CATEGORY_MESSAGE)
//                                                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
//                                                    .setColor(Color.parseColor(getString(R.color.colorPrimary)))
//                                                    .setStyle(new NotificationCompat.BigTextStyle()
//                                                            .bigText(message))
//                                                    .build();
//                                            App.notificationManager.notify(0, notification);
//                                        }
//                                        canCheck = true;
//                                     }
//
//                                     @Override
//                                     public void onFail(String error) {
//                                         canCheck = true;
//                                     }
//                                 }).checkWallet(App.getPreferences().getString("MOBILE_NUMBER","000"));
//                             }
//                         } catch (InterruptedException e) {
//                             e.printStackTrace();
//                         }
//                     }
//                     stopSelf();
//                 }
//             });

            return START_REDELIVER_INTENT;
        }






}
