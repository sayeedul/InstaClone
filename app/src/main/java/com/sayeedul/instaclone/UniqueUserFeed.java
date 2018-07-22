package com.sayeedul.instaclone;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

public class UniqueUserFeed extends AppCompatActivity{

    String selectedUser;
    // ProgressBar feedProg;
    LinearLayout scroll ;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.unique_user_feed);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        scroll = findViewById(R.id.UniqueFeedLinearLayout);
        selectedUser = getIntent().getStringExtra("SELECTED");

        Log.i("AppInfo",selectedUser);

        setTitle(selectedUser+"'s Feed ");

        ParseQuery<ParseObject> query = new ParseQuery<>("images");

        query.whereEqualTo("username",selectedUser);
        query.orderByDescending("createdAt");

        Toast.makeText(this, "Please Wait...Loading "+selectedUser+"'s Feed", Toast.LENGTH_SHORT).show();

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if(e==null)
                {
                    if(objects.size()>0)
                    {
                        for(ParseObject object : objects)
                        {
                            final ParseFile file = (ParseFile)object.get("image");

                            file.getDataInBackground(new GetDataCallback() {
                                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                                @Override
                                public void done(byte[] data, ParseException e) {

                                    if(e == null)
                                    {
                                        Bitmap image = BitmapFactory.decodeByteArray(data,0,data.length);

                                        CardView card = new CardView(getApplicationContext());
                                        card.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                        700));
                                        card.setPadding(10,10,10,10);

                                        ImageView FeedImage = new ImageView(getApplicationContext());
                                        FeedImage.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                                700));

                                        FeedImage.setScaleType(ImageView.ScaleType.CENTER_INSIDE); //CENTER_CROP
                                        FeedImage.setAdjustViewBounds(true);
                                        FeedImage.setImageBitmap(image);

                                        TextView imagename = new TextView(getApplicationContext());
                                        imagename.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                                20));

                                        card.addView(FeedImage);
                                        scroll.addView(card);
                                        scroll.addView(imagename);

                                    }
                                    else
                                    {
                                        Toast.makeText(UniqueUserFeed.this, "Loading image.Error.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                        // feedProg.setVisibility(View.INVISIBLE);
                    }
                }
            }
        });

    }//end of onCreate()


    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_feed, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.about:
                item.setTitle("About User");
                alertAboutUser();
                return true;

            case android.R.id.home:
                this.finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void alertAboutUser()
    {
        String abt = "This box may contain the personal details of "+selectedUser.toUpperCase()+" like Full name, Date of birth , " +
                "place of living, contact as they want to share...";

        AlertDialog.Builder builder = new AlertDialog.Builder(UniqueUserFeed.this)
                .setTitle("ABOUT USER")
                .setMessage(abt)
                .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        AlertDialog ok = builder.create();
        ok.show();
    }



}
