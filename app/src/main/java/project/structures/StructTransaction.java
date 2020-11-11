package project.structures;


import ir.farazist.farazist_app.App;
import ir.farazist.farazist_app.R;
import project.enums.TransactionType;

public class StructTransaction {

    public int id;
    public int amount;
    public String date;
    public String time;
    public String desc;
    public String targetName;
    public String targetMobileNumber;
    public TransactionType type;

    //view
    public boolean isExpanded;


}
