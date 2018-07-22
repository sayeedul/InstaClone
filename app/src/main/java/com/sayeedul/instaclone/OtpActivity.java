package com.sayeedul.instaclone;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import java.util.Random;

public class OtpActivity extends AppCompatActivity {

    EditText num,otp;
    Button getverify;

    String msg,usr,pwd;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);

        num = findViewById(R.id.numberET);
        otp = findViewById(R.id.otpET);
        getverify = findViewById(R.id.verifyBTN);

        usr = getIntent().getStringExtra("USERNAME");
        pwd = getIntent().getStringExtra("PASSWORD");


    } // end of OnCreate()

    @Override
    public void onBackPressed() {
        openBackAlert();
    }

    public void verifyClick(View view)
    {
        String otpButton = getverify.getText().toString();
        if(otpButton.equalsIgnoreCase("GET OTP"))
        {
            openAlert();
        }
        else
        {
            String OTP = otp.getText().toString();
            if(OTP.equals(msg))
            {
                // signup login for parse here...
                ParseUser userNew = new ParseUser();  // Set the user's username and password, which can be obtained by a forms
                userNew.setUsername(usr);
                userNew.setPassword(pwd);
                userNew.signUpInBackground(new SignUpCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null)
                        {
                            Toast.makeText(OtpActivity.this, "OTP VERIFIED SUCCESSFULLY...", Toast.LENGTH_SHORT).show();
                            AlertDialog.Builder builder = new AlertDialog.Builder(OtpActivity.this)
                                    .setTitle("Successful Sign Up!")
                                    .setIcon(R.drawable.signupwelcome)
                                    .setMessage("Welcome  " + usr.toUpperCase() + "!")
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.cancel();
                                            // don't forget to change the line below with the names of your Activities
                                            Intent intent = new Intent(OtpActivity.this, HomeActivity.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(intent);
                                        }
                                    });
                            AlertDialog okay = builder.create();
                            okay.show();

                        } else{
                            ParseUser.logOut();
                            Toast.makeText(OtpActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
                // saving data to Users class...
                ParseObject obj = new ParseObject("Users");
                obj.put("username",usr);
                obj.put("following","");
                ParseACL acl = new ParseACL();
                acl.setPublicReadAccess(true);
                acl.setPublicWriteAccess(true);
                obj.setACL(acl);
                obj.saveInBackground(); // No exception Handled.

            }
            else
            {
                Toast.makeText(OtpActivity.this, "WROGN OTP ENTERED...", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void openAlert()
    {
        if(num.getText().toString()=="")
        {
            Toast.makeText(this, "PLEASE ENTER YOUR NUMBER...", Toast.LENGTH_SHORT).show();
        }
        else {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(OtpActivity.this);

            alertDialogBuilder.setTitle("CONFIRM SignUP ");
            alertDialogBuilder.setMessage("Are You Sure To sign up ? ");
            alertDialogBuilder.setIcon(R.drawable.signup);

            alertDialogBuilder.setPositiveButton("YES,Proceed.", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    Toast.makeText(OtpActivity.this, "PLease Wait..Sending OTP... ", Toast.LENGTH_SHORT).show();

                    otp.setVisibility(View.VISIBLE);

                    Random rand = new Random();

                    int n = rand.nextInt(9999) + 1000;

                    msg = Integer.toString(n);

                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(num.getText().toString(), null, msg, null, null);
                    Toast.makeText(OtpActivity.this, "OTP SENT SUCCESSFULLY... ", Toast.LENGTH_SHORT).show();

                    getverify.setText("Verify Otp");
                }
            });

            alertDialogBuilder.setNegativeButton("NO, Go to Login Page.", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    Toast.makeText(OtpActivity.this, "Going to Login Page.... ", Toast.LENGTH_SHORT).show();
                    Intent in = new Intent(OtpActivity.this, MainActivity.class);
                    startActivity(in);
                    //dialog.cancel();

                }
            });

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }

    }


    private void openBackAlert()
    {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(OtpActivity.this);

        alertDialogBuilder.setTitle("Go BACK ");
        alertDialogBuilder.setMessage("Are You Sure To Cancel ? ");
        alertDialogBuilder.setIcon(R.drawable.cancel);

        alertDialogBuilder.setPositiveButton("YES,Proceed", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                Toast.makeText(OtpActivity.this, "PLease Wait..... ", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(OtpActivity.this,MainActivity.class);
                startActivity(i);
            }
        });

        alertDialogBuilder.setNegativeButton("NO,Stay On This Page.", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                Toast.makeText(OtpActivity.this, "Staying On this Page.... ", Toast.LENGTH_SHORT).show();
                dialog.cancel();

            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }


} // end of class
