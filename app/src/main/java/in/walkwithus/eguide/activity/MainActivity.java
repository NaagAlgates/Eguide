package in.walkwithus.eguide.activity;

import android.Manifest;
import android.animation.Animator;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.w3c.dom.Text;

import java.io.File;
import java.io.IOException;

import in.walkwithus.eguide.App;
import in.walkwithus.eguide.R;
import in.walkwithus.eguide.adapter.ContentImageDisplayAdapter;
import in.walkwithus.eguide.events.ChangeActionEvent;
import in.walkwithus.eguide.events.ContentIdentified;
import in.walkwithus.eguide.events.PausePlayingEvent;
import in.walkwithus.eguide.events.ShowNoInternetScreenEvent;
import in.walkwithus.eguide.events.StopPlayingEvent;
import in.walkwithus.eguide.helpers.AppConstants;
import in.walkwithus.eguide.helpers.Logger;
import in.walkwithus.eguide.helpers.Preferences;
import in.walkwithus.eguide.service.GPSService;
import me.relex.circleindicator.CircleIndicator;
import pl.droidsonroids.gif.GifImageView;

import static android.app.Service.START_NOT_STICKY;
import static in.walkwithus.eguide.helpers.AppConstants.PERMISSION_RQ;

public class MainActivity extends AppCompatActivity {
    private long backPressedTime = 0;
    private static final String TAG = MainActivity.class.getSimpleName();
    Preferences preferences;
    GifImageView infoDisplayingGif,speakerDisplayImage;
    TextView actionText,searchingText;
    ViewPager ContentImageViewPager;
    LinearLayout ContentActionItems;
    ContentImageDisplayAdapter contentImageDisplayAdapter;
    RelativeLayout searchingLayout,noInternetLayout;
    ScrollView dataLayout;
    boolean isExitClicked=false;
    CircleIndicator indicator;
    String images[];/*= {"https://lh4.ggpht.com/mJDgTDUOtIyHcrb69WM0cpaxFwCNW6f0VQ2ExA7dMKpMDrZ0A6ta64OCX3H-NMdRd20=w300",
            "http://s2.thingpic.com/images/2J/YRuJQtWbQFLjHWx7w5MQE9sS.png",
            "https://lh3.googleusercontent.com/X-e8ol99z-1kGJ_EmqqfN-nqDvNMKiTEUlIWtGk-L4NxkVX3-8qThkVJKaUgF5iJFA=w300",
            "http://endofprospecting.com/wp-content/uploads/2014/08/url-small.jpg"};*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Request permission to save videos in external storage
            ActivityCompat.requestPermissions(
                    this, new String[] {Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_RQ);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(new Intent(this, GPSService.class));
            }else{*/
                startService(new Intent(this, GPSService.class));
            /*}*/
        }
        preferences = new Preferences();
        actionText = (TextView) findViewById(R.id.actionText);
        searchingText = (TextView) findViewById(R.id.searchingText);
        searchingText.setText(getString(R.string.searching_text));
        infoDisplayingGif = (GifImageView) findViewById(R.id.infoDisplayingGif);
        infoDisplayingGif.setImageResource(R.drawable.loading);
        speakerDisplayImage = (GifImageView) findViewById(R.id.speakerDisplayImage);
        ContentImageViewPager = (ViewPager)findViewById(R.id.ContentImageViewPager);
        searchingLayout = (RelativeLayout) findViewById(R.id.searchingLayout);
        noInternetLayout = (RelativeLayout) findViewById(R.id.noInternetLayout);
        dataLayout = (ScrollView) findViewById(R.id.dataLayout);
        ContentActionItems = (LinearLayout) findViewById(R.id.ContentActionItems);
        indicator = (CircleIndicator) findViewById(R.id.indicator);
        YoYo.with(Techniques.FadeOut)
                .duration(AppConstants.animationDefaultDuration)
                .interpolate(new AccelerateDecelerateInterpolator())
                .withListener(new Animator.AnimatorListener() {

                    @Override
                    public void onAnimationStart(Animator animation) {}

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        YoYo.with(Techniques.FadeIn)
                                .duration(AppConstants.animationDefaultDuration)
                                .interpolate(new AccelerateDecelerateInterpolator())
                                .withListener(this).playOn(searchingText);
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {}

                    @Override
                    public void onAnimationRepeat(Animator animation) {}
                }).playOn(searchingText);
        YoYo.with(Techniques.FadeOut)
                .duration(AppConstants.animationDefaultDuration)
                .interpolate(new AccelerateDecelerateInterpolator())
                .withListener(new Animator.AnimatorListener() {

                    @Override
                    public void onAnimationStart(Animator animation) {}

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        YoYo.with(Techniques.FadeIn)
                                .duration(AppConstants.animationDefaultDuration)
                                .interpolate(new AccelerateDecelerateInterpolator())
                                .withListener(this).playOn(actionText);
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {}

                    @Override
                    public void onAnimationRepeat(Animator animation) {}
                }).playOn(actionText);
        if(!App.get().isGPSEnabled()){
            showGPSDisabledAlertToUser();
        }
    }
    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            // Sample was denied WRITE_EXTERNAL_STORAGE permission
            Toast.makeText(
                    this,
                    "App will not not work properly since permission denied for location access.",
                    Toast.LENGTH_LONG)
                    .show();
        }else{
            Logger.d(TAG,"Start Service");
            startService(new Intent(this, GPSService.class));
        }
    }
    @Override
    protected void onResume(){
        super.onResume();
        Logger.d(TAG,"onResume");
        showSearching();
        try {
            EventBus.getDefault().register(this);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    @Override
    protected void onStart(){
        super.onStart();
        try {

        }catch (Exception e){
            e.printStackTrace();
        }
    }
    @Override
    public void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }
    @Subscribe
    public void isSearching(ChangeActionEvent changeActionEvent) throws IOException {
        //EventBus.getDefault().post(new ChangeActionEvent(changeActionEvent.isSearching));
        if(changeActionEvent.isStopped){
            if(isExitClicked || changeActionEvent.isSearching){
                showSearching();
            }else{
                isExitClicked=false;
                actionText.setText(getString(R.string.completed_text));
                noInternetLayout.setVisibility(View.GONE);
                ContentActionItems.setVisibility(View.VISIBLE);
                speakerDisplayImage.setImageResource(R.drawable.speaking_still);
            }
        }else{
            actionText.setText(getString(R.string.playing_text));
            //infoDisplayingGif.setImageResource(R.drawable.speaking);
            searchingLayout.setVisibility(View.GONE);
            noInternetLayout.setVisibility(View.GONE);
            dataLayout.setVisibility(View.VISIBLE);
            String imageName=preferences.getString(AppConstants.PREF_LAST_PLAYED_FILE);
            images= new String[]{imageName + "_1.jpeg", imageName + "_2.jpeg", imageName + "_3.jpeg", imageName + "_4.jpeg"};
            contentImageDisplayAdapter = new ContentImageDisplayAdapter(images);
            ContentImageViewPager.setAdapter(contentImageDisplayAdapter);
            indicator.setViewPager(ContentImageViewPager);
            ContentActionItems.setVisibility(View.GONE);
            speakerDisplayImage.setImageResource(R.drawable.speaking);
        }
    }
    @Subscribe
    public void showNoInternet(ShowNoInternetScreenEvent showNoInternetScreenEvent){
        searchingLayout.setVisibility(View.GONE);
        dataLayout.setVisibility(View.GONE);
        ContentActionItems.setVisibility(View.GONE);
        noInternetLayout.setVisibility(View.VISIBLE);
    }
    private void showSearching(){
        actionText.setText(getString(R.string.searching_text));
        searchingText.setText(getString(R.string.searching_text));
        infoDisplayingGif.setImageResource(R.drawable.loading);
        searchingLayout.setVisibility(View.VISIBLE);
        dataLayout.setVisibility(View.GONE);
        ContentActionItems.setVisibility(View.GONE);
        noInternetLayout.setVisibility(View.GONE);

    }
    public void replayClicked(View view) {
        Preferences preferences = new Preferences();
        String sFileName = preferences.getString(AppConstants.PREF_LAST_PLAYED_FILE);
        EventBus.getDefault().post(new ContentIdentified(sFileName));
    }

    public void moreInfoClicked(View view) {
    }

    public void exitClicked(View view) {
        isExitClicked = true;
        showSearching();
    }
    @Override
    public void onBackPressed() {
        long t = System.currentTimeMillis();
        if (t - backPressedTime > 2000) {    // 2 secs
            backPressedTime = t;
            Toast.makeText(this, "Press back again to logout",
                    Toast.LENGTH_SHORT).show();
        } else {
            super.onBackPressed();
            EventBus.getDefault().unregister(this);
        }
    }


    public void showGPSDisabledAlertToUser(){
        /*android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("GPS is disabled in your device. Please enable it.")
                .setCancelable(false)
                .setPositiveButton("Settings",
                        new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface dialog, int id){
                                Intent callGPSSettingIntent = new Intent(
                                        android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivity(callGPSSettingIntent);
                            }
                        });
        android.app.AlertDialog alert = alertDialogBuilder.create();
        alert.show();*/
        Intent callGPSSettingIntent = new Intent(MainActivity.this,EnableSettings.class);
        startActivity(callGPSSettingIntent);
        finish();
    }
}
