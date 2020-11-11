package project.enums;

import ir.farazist.farazist_app.App;
import ir.farazist.farazist_app.R;

public enum TransactionType {

    DEPOSITE(App.getContext().getString(R.string.deposit)),
    WITHDRAW(App.getContext().getString(R.string.withdraw)),
    WASTE_DELIVERY(App.getContext().getString(R.string.wasteDelivery)),
    PACKAGE_DELIVERY(App.getContext().getString(R.string.packageDelivery));

    private String stringValue;

    private TransactionType(String stringValue){
        this.stringValue = stringValue;
    }

    @Override
    public String toString() {
        return stringValue;
    }
}
