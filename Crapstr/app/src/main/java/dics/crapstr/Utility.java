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
        if (rating < 3) {
            return (int)((Color.YELLOW-Color.RED)/2*rating + (3*Color.RED-Color.YELLOW)/2);
        } else {
            return (int)((Color.GREEN-Color.YELLOW)/2*rating + (-3*Color.GREEN+5*Color.YELLOW)/2);
        }
    }
}
