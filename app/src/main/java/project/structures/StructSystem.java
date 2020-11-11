package project.structures;

import android.graphics.Bitmap;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.CookieStore;

public class StructSystem {

    public int id;
    public int ownerId;
    public String state; // active - deactive
    public int balance;
    public float rate;
    public String name;
    public int cityId;
    public int provinceID;
    public String cityName;
    public String provinceName;
    public String address;
    public String number;
    public String coverUrl;
    public Bitmap bitmap;
    public String workTime;
    public double lat;
    public double lng;

    //view
    public boolean isExpanded;
    public boolean processing;
}
