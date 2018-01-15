package in.walkwithus.eguide.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.squareup.picasso.Picasso;

import in.walkwithus.eguide.App;
import in.walkwithus.eguide.R;

/**
 * Updated by bahwan on 12/26/17.
 * Project name: Eguide
 */

public class ContentImageDisplayAdapter extends PagerAdapter {
    private String images[];
    private LayoutInflater layoutInflater;


    public ContentImageDisplayAdapter(String imagesURL[]) {
        this.images = imagesURL;
        layoutInflater = (LayoutInflater) App.get().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return images.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((LinearLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        View itemView = layoutInflater.inflate(R.layout.item_image, container, false);

        ImageView imageView = (ImageView) itemView.findViewById(R.id.contentImage);

        Picasso.with(App.get())
                .load("file:///android_asset/"+images[position])
                .fit()
                .centerCrop()
                .placeholder(R.mipmap.ic_launcher)
                .error(R.mipmap.ic_launcher_round)
                .into(imageView);

        //imageView.setImageResource(images[position]);

        container.addView(itemView);

        //listening to image click
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(context, "you clicked image " + (position + 1), Toast.LENGTH_LONG).show();
            }
        });

        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout) object);
    }
}