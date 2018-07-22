package com.sayeedul.instaclone;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class FeedsActivity extends AppCompatActivity {

    String currentUser;
   // ProgressBar feedProg;
    LinearLayout scroll ;
    String UserFollowedByYou ;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        currentUser = ParseUser.getCurrentUser().getUsername();
        scroll = findViewById(R.id.feedLinearLayout);

        UserFollowedByYou = getIntent().getStringExtra("FollowingUser");
        String[] Users = UserFollowedByYou.split(" ");

        Log.i("AppInfo",currentUser);
        setTitle(currentUser+"'s Feed ");

        loadImages(currentUser);
        for(String User : Users)
        {
            loadImages(User);
        }

    }//end of onCreate()

    private void loadImages(String name)
    {

        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("images");

        query.whereEqualTo("username",name);
        query.orderByDescending("createdAt");

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

                            // final String imageUri = file.getUrl();

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

                                        TextView bottom = new TextView(getApplicationContext());
                                        bottom.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                                20));

                                        TextView imagename = new TextView(getApplicationContext());
                                        imagename.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                                25));
                                        imagename.setTextSize(15);
                                        imagename.setTextColor(getResources().getColor(R.color.skyBlue));
                                        imagename.setVisibility(View.VISIBLE);
                                        imagename.setPadding(5,5,5,5);
                                        // imagename.setText(); // TO  USE FOLLOWED USER NAME on PIC.

                                        card.addView(FeedImage);
                                        scroll.addView(card);
                                        scroll.addView(bottom);

                                    }
                                    else
                                    {
                                        Toast.makeText(FeedsActivity.this, "Loading image.Error.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    }
                }
            }
        });

    }



    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_feed, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.about:
               // Toast.makeText(this, "About US OPTION CLICKED", Toast.LENGTH_SHORT).show();
                alertAboutUs();
                return true;

            case android.R.id.home:
                this.finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void alertAboutUs()
    {
        String abt = "This project is built by Sayeedul Rahman Under Summer Training 2018 @Acadview. "+
                     "This app is used to share images among one another user who follow each other. "+
                     "Also, you can Follow/Unfollow perticular user at the moment and , your personal" +
                     " Feed will change accordingly"+ "@copyRight Claim Reserved,2018";

        AlertDialog.Builder builder = new AlertDialog.Builder(FeedsActivity.this)
                .setTitle("ABOUT US")
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
