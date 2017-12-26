package in.walkwithus.eguide.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.io.IOException;

import in.walkwithus.eguide.R;
import in.walkwithus.eguide.adapter.ContentImageDisplayAdapter;
import in.walkwithus.eguide.events.ChangeActionEvent;
import in.walkwithus.eguide.events.ContentIdentified;
import in.walkwithus.eguide.events.PausePlayingEvent;
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
    private static final String TAG = MainActivity.class.getSimpleName();
    Preferences preferences;
    GifImageView infoDisplayingGif,speakerDisplayImage;
    TextView actionText;
    ViewPager ContentImageViewPager;
    LinearLayout ContentActionItems;
    ContentImageDisplayAdapter contentImageDisplayAdapter;
    RelativeLayout searchingLayout;
    ScrollView dataLayout;
    boolean isExitClicked=false;
    CircleIndicator indicator;
    String images[] = {"https://lh4.ggpht.com/mJDgTDUOtIyHcrb69WM0cpaxFwCNW6f0VQ2ExA7dMKpMDrZ0A6ta64OCX3H-NMdRd20=w300",
            "http://s2.thingpic.com/images/2J/YRuJQtWbQFLjHWx7w5MQE9sS.png",
            "https://lh3.googleusercontent.com/X-e8ol99z-1kGJ_EmqqfN-nqDvNMKiTEUlIWtGk-L4NxkVX3-8qThkVJKaUgF5iJFA=w300",
            "http://endofprospecting.com/wp-content/uploads/2014/08/url-small.jpg"};

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
        infoDisplayingGif = (GifImageView) findViewById(R.id.infoDisplayingGif);
        speakerDisplayImage = (GifImageView) findViewById(R.id.speakerDisplayImage);
        ContentImageViewPager = (ViewPager)findViewById(R.id.ContentImageViewPager);
        searchingLayout = (RelativeLayout) findViewById(R.id.searchingLayout);
        dataLayout = (ScrollView) findViewById(R.id.dataLayout);
        ContentActionItems = (LinearLayout) findViewById(R.id.ContentActionItems);
        indicator = (CircleIndicator) findViewById(R.id.indicator);
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
            startService(new Intent(this, GPSService.class));
        }
    }
    @Override
    protected void onStart(){
        super.onStart();
        try {
            EventBus.getDefault().register(this);
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
                actionText.setText("Completed");
                ContentActionItems.setVisibility(View.VISIBLE);
                speakerDisplayImage.setImageResource(R.drawable.speaking_still);
            }
        }else{
            Logger.d(TAG,"Speaking");
            actionText.setText("Speaking");
            //infoDisplayingGif.setImageResource(R.drawable.speaking);
            searchingLayout.setVisibility(View.GONE);
            dataLayout.setVisibility(View.VISIBLE);
            contentImageDisplayAdapter = new ContentImageDisplayAdapter(images);
            ContentImageViewPager.setAdapter(contentImageDisplayAdapter);
            indicator.setViewPager(ContentImageViewPager);
            ContentActionItems.setVisibility(View.GONE);
            speakerDisplayImage.setImageResource(R.drawable.speaking);
        }
    }
    private void showSearching(){
        Logger.d(TAG,"Searching");
        actionText.setText("Searching");
        infoDisplayingGif.setImageResource(R.drawable.loading);
        searchingLayout.setVisibility(View.VISIBLE);
        dataLayout.setVisibility(View.GONE);
        ContentActionItems.setVisibility(View.GONE);
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
}
