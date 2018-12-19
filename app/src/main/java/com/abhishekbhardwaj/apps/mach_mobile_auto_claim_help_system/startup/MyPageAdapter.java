package com.abhishekbhardwaj.apps.mach_mobile_auto_claim_help_system.startup;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.abhishekbhardwaj.apps.mach_mobile_auto_claim_help_system.R;

public class MyPageAdapter extends PagerAdapter{

    //Create Context and Layout Inflator Objects
    Context context;
    LayoutInflater layoutInflater;


    //now create a constructor
    MyPageAdapter(Context context)
    {
        this.context = context;
    }



    //now create array for images and text to be displayed on the slider layout
    //images array
    int [] imageArray = {   R.drawable.car_crash,
            R.drawable.location,
            R.drawable.emergency,
            R.drawable.policy};

    //heading array
    String [] headingArray = { "Auto-Detects Serious Vehicle Crashes",
            "Send Your Location to Contacts",
            "Highly Reduces Rescue Time",
            "Instant Vehicle Insurance Claim"};




    @Override
    public int getCount() {
        return imageArray.length; // return total number of slides that the pager will hold
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == (RelativeLayout) object; // return this as view == (RelativeLayout) object;
    }


    //override this method
    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {

        //add this line
        layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        // and also this line
        View view = layoutInflater.inflate(R.layout.welcome_slider, container, false);



        //now initialize the components that we created in welcome slider layout
        ImageView i = (ImageView) view.findViewById(R.id.image);
        TextView t = (TextView) view.findViewById(R.id.heading);

        //now set the arrays we created above into these components values as follows
        i.setImageResource(imageArray[position]);
        t.setText(headingArray[position]);


        //finally add container view as
        container.addView(view);


        //then return view object created above
        return view;
    }


    //finally override this method to prevent any errors

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {

        container.removeView((RelativeLayout) object);
    }
}
