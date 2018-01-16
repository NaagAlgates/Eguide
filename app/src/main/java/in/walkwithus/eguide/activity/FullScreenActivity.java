package in.walkwithus.eguide.activity;

import android.animation.Animator;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import in.walkwithus.eguide.R;
import in.walkwithus.eguide.adapter.ContentImageDisplayAdapter;
import in.walkwithus.eguide.helpers.AppConstants;
import me.relex.circleindicator.CircleIndicator;

/**
 * Updated by bahwan on 1/16/18.
 * Project name: Eguide
 */

public class FullScreenActivity extends AppCompatActivity {
    TextView clueImagesText,meterText;
    private static final String TAG = FullScreenActivity.class.getSimpleName();
    String images[]={"http://www.mobileswall.com/wp-content/uploads/2015/12/1200-Along-With-The-l.jpg",
            "http://www.techagesite.com/320x480-wallpapers/abstract-flower-free-cell-phone-wallpapers-240x320.jpg",
            "https://i.pinimg.com/originals/c4/40/df/c440df3b3f3343d205f1f5c50de54279.jpg",
            "https://static-s.aa-cdn.net/img/gp/20600004418584/9c_nCjEDtcSxp-9-ai72_qHLwjBB7sCHfmjOG5TmIFXGvs5PQQTiIA0HaHEAmLO3jZU=h900"};
    ViewPager ContentImageViewPager;
    CircleIndicator indicator;
    ContentImageDisplayAdapter contentImageDisplayAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen);
        ContentImageViewPager = (ViewPager)findViewById(R.id.ContentImageViewPager);
        indicator = (CircleIndicator) findViewById(R.id.indicator);
        contentImageDisplayAdapter = new ContentImageDisplayAdapter(images);
        ContentImageViewPager.setAdapter(contentImageDisplayAdapter);
        indicator.setViewPager(ContentImageViewPager);
        clueImagesText = (TextView) findViewById(R.id.clueImagesText);
        meterText = (TextView) findViewById(R.id.meterText);
    }

    public void previousClicked(View view) {
        ContentImageViewPager.setCurrentItem(getItem(-1), true);
    }

    public void nextClicked(View view) {
        ContentImageViewPager.setCurrentItem(getItem(+1), true);
    }

    private int getItem(int i) {
        return ContentImageViewPager.getCurrentItem() + i;
    }

    public void expandClue(View view) {
        int textLength = clueImagesText.getText().toString().length();
        if(textLength==0){
            YoYo.with(Techniques.SlideInRight)
                    .duration(AppConstants.animationQuickDuration)
                    .interpolate(new AccelerateDecelerateInterpolator())
                    .withListener(new Animator.AnimatorListener() {

                        @Override
                        public void onAnimationStart(Animator animation) {
                            clueImagesText.setText("Clues are shown as background images.");
                            //clueImagesText.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {}

                        @Override
                        public void onAnimationRepeat(Animator animation) {}
                    }).playOn(clueImagesText);
        }else{
            YoYo.with(Techniques.SlideOutRight)
                    .duration(AppConstants.animationQuickDuration)
                    .interpolate(new AccelerateDecelerateInterpolator())
                    .withListener(new Animator.AnimatorListener() {

                        @Override
                        public void onAnimationStart(Animator animation) {
                            /*Drawable img = getResources().getDrawable( android.R.drawable.ic_dialog_info);
                            img.setBounds( 5, 5, 5, 5 );
                            clueImagesText.setCompoundDrawables( img, null, null, null );*/
                            //clueImagesText.setCompoundDrawablesWithIntrinsicBounds(android.R.drawable.ic_dialog_info, 0, 0, 0);
                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {

                            clueImagesText.setText("");
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {}

                        @Override
                        public void onAnimationRepeat(Animator animation) {}
                    }).playOn(clueImagesText);
        }
    }
}
