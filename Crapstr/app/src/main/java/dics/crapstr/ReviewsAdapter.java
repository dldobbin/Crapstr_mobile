package dics.crapstr;

import android.content.Context;
import android.graphics.PorterDuff;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by daniel on 11/16/2017.
 *
 */

public class ReviewsAdapter extends ArrayAdapter<Review> {
    ReviewsAdapter(Context context, ArrayList<Review> reviews) {
        super(context, 0, reviews);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        Review review = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.review, parent, false);
        }
        ImageView rating = (ImageView)convertView.findViewById(R.id.rating);
        TextView description = (TextView)convertView.findViewById(R.id.description);
        if (review != null) {
            int padding = (review.getRating() + 2) * getContext().getResources().getDimensionPixelSize(R.dimen.rating_padding_interval);
            rating.setPadding(padding, 0, 0, 0);
            rating.setColorFilter(Utility.getInstance().reviewToColor(review.getRating()), PorterDuff.Mode.MULTIPLY);
            description.setText(review.getDescription());
        }
        return convertView;
    }
}
