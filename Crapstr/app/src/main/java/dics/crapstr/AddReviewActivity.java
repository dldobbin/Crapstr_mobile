package dics.crapstr;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import java.util.ArrayList;

public class AddReviewActivity extends Activity {
    private Reviews reviews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_review);

        reviews = new Reviews(getIntent().getStringExtra("placeId"));
        reviews.getReviews().add(new Review(1, ""));

        setRating(1);
        findViewById(R.id.review_1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { setRating(1); }
        });
        findViewById(R.id.review_2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { setRating(2); }
        });
        findViewById(R.id.review_3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { setRating(3); }
        });
        findViewById(R.id.review_4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { setRating(4); }
        });
        findViewById(R.id.review_5).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { setRating(5); }
        });
        findViewById(R.id.ok_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText editText = findViewById(R.id.review_description);
                reviews.getReviews().get(0).setDescription(editText.getText().toString());
                Intent i = new Intent();
                i.putExtra("reviews",reviews);
                AddReviewActivity.this.setResult(RESULT_OK, i);
                finish();
            }
        });
    }

    private void setRating(double rating) {
        reviews.getReviews().get(0).setRating((int)rating);
        ImageView ratingView = findViewById(R.id.review_rating);
        ratingView.setPadding(Utility.getInstance().getPadding(rating),0,0,0);
        ratingView.setColorFilter(Utility.getInstance().reviewToColor(rating), PorterDuff.Mode.MULTIPLY);
    }

}
