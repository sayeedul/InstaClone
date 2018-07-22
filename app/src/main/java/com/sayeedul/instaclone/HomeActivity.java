package com.sayeedul.instaclone;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.SyncStateContract;
import android.support.annotation.Nullable;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {
    Button feed;
    RecyclerView recyclerView;
    UserListAdapter myAdapter;
    List<ItemDataUserList> usernames;
    ProgressBar userProg,shareProg;
    String followingArray,followArrayonLoad,newPassFollo;
    String passFollowing; int i=0;int j=0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        feed = findViewById(R.id.feedBTN);
        userProg = findViewById(R.id.progressBarUser);
        userProg.setVisibility(View.VISIBLE);

        followingArray =""; followArrayonLoad="";  passFollowing="";
        shareProg = findViewById(R.id.progressBarShare);

        recyclerView = (RecyclerView) findViewById(R.id.userListRecycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        usernames = new ArrayList<>();
        myAdapter = new UserListAdapter(this,usernames);

        newPassFollo = "";

        ParseQuery<ParseObject> queryFollow = ParseQuery.getQuery("Users");
        queryFollow.whereEqualTo("username", ParseUser.getCurrentUser().getUsername());
        queryFollow.getFirstInBackground(new GetCallback<ParseObject>() {
            public void done(ParseObject object, ParseException e) {
                if (object == null) {
                    Log.d("follow", "The getFirst request failed.");
                    Toast.makeText(HomeActivity.this, "The getFirst request failed.", Toast.LENGTH_SHORT).show();
                }
                else {
                    Log.d("follow", "Retrieved the object.");
                    followArrayonLoad =  object.getString("following");
                    Toast.makeText(HomeActivity.this, "You are Following : ' "+followArrayonLoad+
                            " ' are Green Ticked.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereNotEqualTo("username",ParseUser.getCurrentUser().getUsername());
        query.addAscendingOrder("username");
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if(e == null)
                { userProg.setVisibility(View.INVISIBLE);
                    if(objects.size()>0)
                    {
                        for(final ParseUser user : objects)
                        {
                            ItemDataUserList itemObj = new ItemDataUserList(R.drawable.tick1,user.getUsername());
                            if(followArrayonLoad.contains(user.getUsername()))
                             {
                               itemObj.setNewImage(R.drawable.tick3);
                             //  Toast.makeText(HomeActivity.this, "Inside if.", Toast.LENGTH_SHORT).show();
                                 newPassFollo = user.getUsername()+" "+ newPassFollo;
                             }
                            usernames.add(itemObj);
                                // End of load user following by the current user.....
                        }
                        myAdapter.notifyDataSetChanged();  // Newly addded
                       recyclerView.setAdapter(myAdapter);
                    }
                }
            }

        });// end of Query.findInBackground() for loading users list ...
        followArrayonLoad="";
          alertFollowOpenning();
        myAdapter.setOnClickListener(new UserListAdapter.OnItemClickListener() {
            @Override   // for clicking perticular item of USER LISTS...
            public void onItemClick(int position) {
                ItemDataUserList got = usernames.get(position);
               // Toast.makeText(HomeActivity.this, "User "+ got.getUsername().toUpperCase()+" Cliked ! ", Toast.LENGTH_SHORT).show();

                 Intent i = new Intent(HomeActivity.this,UniqueUserFeed.class);
                 i.putExtra("SELECTED",got.getUsername());
                 startActivity(i);
            }

            @Override   // for clicking follow checkbox of perticular USER ...
            public void onFollowClick(final int position) {

                //----------------------------------------------------------------------------------------------------------------------
                if(usernames.get(position).getImage() == R.drawable.tick1) // FOLLOWING : adding ticked user to the currents follow list separated by a space.
                {
                    usernames.get(position).setNewImage(R.drawable.tick3);
                    ParseQuery<ParseObject> query = ParseQuery.getQuery("Users");
                    query.whereEqualTo("username", ParseUser.getCurrentUser().getUsername());
                    query.getFirstInBackground(new GetCallback<ParseObject>() {
                        public void done(ParseObject object, ParseException e) {
                            if (object == null) {
                                Log.d("follow", "The getFirst request failed.");
                                Toast.makeText(HomeActivity.this, "The getFirst request failed.", Toast.LENGTH_SHORT).show();
                            } else {
                                Log.d("follow", "Retrieved the object.");

                                followingArray =  usernames.get(position).getUsername()+" "+ object.getString("following");
                                newPassFollo =  usernames.get(position).getUsername()+" "+newPassFollo;
                                //passFollowing = followingArray;
                                object.put("following",followingArray);
                                object.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        if(e==null)
                                        {
                                            Toast.makeText(HomeActivity.this, "SAVED : FOLLOWED "+
                                                    usernames.get(position).getUsername(), Toast.LENGTH_SHORT).show();
                                            followingArray = "";
                                        }
                                        else
                                        {
                                            Toast.makeText(HomeActivity.this, "SAVE ERROR : in following", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        }
                    });
                    // pop up dilogue follow alert box.
                    AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this)
                            .setTitle("* FOLLOW USER *")
                            .setIcon(R.drawable.follow)
                            .setMessage(" Pictures of Followed Users will appear in your feed. ")
                            .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });
                    AlertDialog ok = builder.create();
                    ok.show();
                    //end of elert follow....
                }

                else  // UNFOLLOWING ------------------------------------------------------------------------------------------------------
                {
                    usernames.get(position).setNewImage(R.drawable.tick1);
                    ParseQuery<ParseObject> query = ParseQuery.getQuery("Users");
                    query.whereEqualTo("username", ParseUser.getCurrentUser().getUsername());
                    query.getFirstInBackground(new GetCallback<ParseObject>() {
                        public void done(ParseObject object, ParseException e) {
                            if (object == null) {
                                Log.d("follow", "The getFirst request failed.");
                                Toast.makeText(HomeActivity.this, "The getFirst request failed.", Toast.LENGTH_SHORT).show();
                            } else {
                                Log.d("follow", "Retrieved the object.");

                                followingArray = object.getString("following"); //fetching following list from table.

                                followingArray = followingArray.replace(usernames.get(position).getUsername()+" ","");
                                newPassFollo = newPassFollo.replace(usernames.get(position).getUsername()+" "," ");
                                //passFollowing = followingArray;
                                //Toast.makeText(HomeActivity.this, followingArray, Toast.LENGTH_SHORT).show();

                                object.put("following", followingArray);
                                object.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        if (e == null) {
                                            Toast.makeText(HomeActivity.this, "SAVED : UNFOLLOWED "+
                                                    usernames.get(position).getUsername(), Toast.LENGTH_SHORT).show();
                                            followingArray = "";
                                        } else {
                                            Toast.makeText(HomeActivity.this, "SAVE ERROR: in Unfollow !", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        }

                    });

                    // pop up dilogue follow alert box.
                    AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this)
                            .setTitle("* UNFOLLOW USER *")
                            .setIcon(R.drawable.unfollow)
                            .setMessage(" Pictures of UnFollowed Users will not appear in your feed. ")
                            .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });
                    AlertDialog ok = builder.create();
                    ok.show();
                    //end of elert follow....

                }// ---------------------------END OF UNFOLLOWING ELSE----------------------------------------------------------------------
                myAdapter.notifyItemChanged(position);
            }
        });

    }// end of on Oncreate().

    public void feedClicked(View view)
    {
        Intent i = new Intent(HomeActivity.this,FeedsActivity.class);
        i.putExtra("FollowingUser",newPassFollo); //passFollowing
        startActivity(i);
    }


    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.share:
                // code for share images.

                final CharSequence[] items = { "Take Photo", "Choose from Library", "Cancel" };

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(HomeActivity.this);

                alertDialogBuilder.setTitle("CHOOSE BELOW OPTIONS... ");
                alertDialogBuilder.setIcon(R.drawable.camera);

                alertDialogBuilder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if (items[which].equals("Take Photo"))
                        {
                            Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivityForResult(takePicture, 0);//zero can be replaced with any action code
                        }

                        else if (items[which].equals("Choose from Library"))
                        {
                            Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            startActivityForResult(i,1);
                        }
                        else if (items[which].equals("Cancel"))
                        {
                            dialog.dismiss();
                        }

                    }
                });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
                return true;

            case R.id.logout:
                // code for LOGOUT .
                alertLogout();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        shareProg.setVisibility(View.VISIBLE);

        if(requestCode==1 && resultCode==RESULT_OK && data != null)
        {
            Uri selectedImage = data.getData();

            try
            {
                Bitmap bitmapImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(),selectedImage);
                Log.i("AppInfo","Image Received");

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmapImage.compress(Bitmap.CompressFormat.PNG,100,stream);
                byte[] byteArray = stream.toByteArray();

                ParseFile file = new ParseFile("myimage.png",byteArray);
                ParseObject object = new ParseObject("images");

                object.put("username", ParseUser.getCurrentUser().getUsername());
                object.put("image",file);

                object.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        shareProg.setVisibility(View.INVISIBLE);
                        if(e==null)
                        {
                            Toast.makeText(HomeActivity.this, "YOUR 'GALLARY' IMAGE HAS BEEN POSTED..", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            Toast.makeText(HomeActivity.this, "There Was An Error...Try Again", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    }
                });
            }
            catch(Exception e )
            {
                e.printStackTrace();
            }
        }//end of if

        else if(requestCode==0 && resultCode==RESULT_OK && data != null)
        {
            try
            {
               // Bitmap bitmapImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(),selectedImage);
                Bundle bundle = data.getExtras();
                Bitmap bitmapImage =(Bitmap) bundle.get("data");

                Log.i("AppInfo","Image Received");

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmapImage.compress(Bitmap.CompressFormat.PNG,100,stream);
                byte[] byteArray = stream.toByteArray();

                ParseFile file = new ParseFile("image.png",byteArray);
                ParseObject object = new ParseObject("images");

                object.put("username", ParseUser.getCurrentUser().getUsername());
                object.put("image",file);

                object.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        shareProg.setVisibility(View.INVISIBLE);
                        if(e==null)
                        {
                            Toast.makeText(HomeActivity.this, "YOUR 'CAMERA' IMAGE HAS BEEN POSTED..", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            Toast.makeText(HomeActivity.this, "There Was An Error...Try Again", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    }
                });
            }
            catch(Exception e )
            {
                e.printStackTrace();
            }
        }
        else if(requestCode==0 && resultCode!=RESULT_OK || data == null)
        {
            shareProg.setVisibility(View.INVISIBLE);
            Toast.makeText(this, "Error in uploading camera image", Toast.LENGTH_SHORT).show();
        }
    }//end of onActivtyResult

  private void alertLogout()
  {
      AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(HomeActivity.this);
      alertDialogBuilder.setTitle("LOGOUT");
      alertDialogBuilder.setMessage("Are You Sure To Logout ? ");
      alertDialogBuilder.setIcon(R.drawable.logout2);
      alertDialogBuilder.setPositiveButton("YES,Proceed", new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {

              Toast.makeText(HomeActivity.this, "PLease Wait Logging Out..... ", Toast.LENGTH_SHORT).show();
              ParseUser.logOut(); //logout user
              Intent i = new Intent(HomeActivity.this,MainActivity.class);
              startActivity(i);
          }
      });
      alertDialogBuilder.setNegativeButton("NO,Stay On This Page.", new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {

              Toast.makeText(HomeActivity.this, "Staying On this Page.... ", Toast.LENGTH_SHORT).show();
              dialog.cancel();
          }
      });
      AlertDialog alertDialog = alertDialogBuilder.create();
      alertDialog.show();
  }

    private void alertFollowOpenning()
    {
        String abt = "To Follow or Unfollow a user, just click on the Tick box. Users followed by You are marked GREEN Ticked."+
                        "if the grren marked tick is not coming,close the app and restart again.";

        AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this)
                .setTitle("TO FOLLOW")
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


}// end of class
