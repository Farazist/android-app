package project.helper;

import android.content.Context;

public class HelperDimensions {
    public static float convertDpToPx(Context context, float dp) {
        return dp * context.getResources().getDisplayMetrics().density;
    }
    public static float convertPxToDp(Context context, float px) {
        return px / context.getResources().getDisplayMetrics().density;
    }
}
