package project.structures;

import org.json.JSONArray;

public class StructSystemDelivery {

    public int id;
    public int userId;
    public int definedItemsPrice;
    public String state; // waiting *** accepted *** delivery_rejected *** verification_rejected *** done *** deleted
    public String date;
    public String time;
    public String invoice;
    public JSONArray items;
    public JSONArray customItems;
    public String userProvince;
    public String userCity;
    public String userAddress;
    public String userMobile;
    public String userName;
    public String userAvatar;


}
