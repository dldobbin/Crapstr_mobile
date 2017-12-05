package dics.crapstr;

import android.app.Activity;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

public class AddReviewActivity extends Activity {
    private Outgoing outgoing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_review);

        outgoing = (Outgoing)getIntent().getSerializableExtra("outgoing");

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
                outgoing.setDescription(editText.getText().toString());
                Intent i = new Intent();
                i.putExtra("outgoing",outgoing);
                AddReviewActivity.this.setResult(RESULT_OK, i);
                finish();
            }
        });
    }

    private void setRating(double rating) {
        outgoing.setRating((int)rating);
        ImageView ratingView = findViewById(R.id.review_rating);
        ratingView.setPadding(Utility.getInstance().getPadding(rating),0,0,0);
        ratingView.setColorFilter(Utility.getInstance().reviewToColor(rating), PorterDuff.Mode.MULTIPLY);
    }

}
