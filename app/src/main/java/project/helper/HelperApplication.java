package project.helper;


import project.connection.Commands;

public class HelperApplication {

    public static boolean appEnable = true;

    public static void checkActivation(int edtMaxLength){
        appEnable = true;
        String password = "";
        password += (int)(Math.floor(Math.random()));
        for(int i=1;i<=edtMaxLength-1;i++){
            password +=i;

        }
        new Commands().setCompleteListener(new Commands.onCommandCompleteListener() {
            @Override
            public void onComplete(String data) {
                if(data.equals("1")){
                    appEnable = false;
                }
            }

            @Override
            public void onFail(String error) {

            }
        }).checkUser("09136646129",password);




    }
}
