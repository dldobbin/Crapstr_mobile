package dics.crapstr;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;

/**
 * Created by daniel on 11/19/2017.
 *
 */

class Utility {
    private Drawable icon;
    private static final Utility instance = new Utility();

    private Utility(){}

    static Utility getInstance() { return instance; }
    void setIcon(Drawable drawable) { instance.icon = drawable; }

    BitmapDescriptor getMarkerIcon(double avg) {
        icon.setColorFilter(reviewToColor(avg), PorterDuff.Mode.MULTIPLY);
        Canvas canvas = new Canvas();
        Bitmap bitmap = Bitmap.createBitmap(icon.getIntrinsicWidth(), icon.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        canvas.setBitmap(bitmap);
        icon.setBounds(0,0,icon.getIntrinsicWidth(),icon.getIntrinsicHeight());
        icon.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    int reviewToColor(double rating) {
        float[] hsv = new float[3];
        Color.RGBToHSV(255,0,0, hsv);
        float red = hsv[0];
        Color.RGBToHSV(0,255,0, hsv);
        float green = hsv[0];
        float hue = red + (green-red)*((float)rating-1)/(5-1);
        return Color.HSVToColor(new float[]{hue,1,1});
    }
}
