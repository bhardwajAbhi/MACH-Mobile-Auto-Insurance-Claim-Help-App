package com.abhishekbhardwaj.apps.mach_mobile_auto_claim_help_system.startup;

import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.abhishekbhardwaj.apps.mach_mobile_auto_claim_help_system.MainActivity;
import com.abhishekbhardwaj.apps.mach_mobile_auto_claim_help_system.R;

public class WelcomeScreen extends AppCompatActivity {


    ViewPager vp;
    LinearLayout ll;

    MyPageAdapter p;

    //for dots we have to initialize an array
    TextView[] dots;

    //Buttons
    Button prev, next;

    //variable for current page
    int currentPage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome_screen);

        vp = (ViewPager) findViewById(R.id.viewPager);
        ll = (LinearLayout) findViewById(R.id.button_layout);


        //initialize button
        prev = (Button) findViewById(R.id.prevButton);
        next = (Button) findViewById(R.id.nextButton);

        p = new MyPageAdapter(this);
        vp.setAdapter(p);

        //call the dotsIndicator method
        dotsIndicator(0);

        //do this after implementing onPageChangeListener
        vp.addOnPageChangeListener(listener);



        //onclick listeners
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                vp.setCurrentItem(currentPage + 1);

                if (currentPage == 3)
                {
                    Intent i  = new Intent(WelcomeScreen.this, LoginActivity.class);
                    startActivity(i);
                    finish();
                }
            }
        });

        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                vp.setCurrentItem(currentPage - 1);
            }
        });
    }


    // now we are creating a method to assign dots value to the array
    public void dotsIndicator(int position) {
        dots = new TextView[4]; //as we have four slides so we assigned value 4
        ll.removeAllViews();
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226"));
            dots[i].setTextSize(22);
            dots[i].setTextColor(getResources().getColor(R.color.colorText));
            ll.addView(dots[i]);
        }

        if(dots.length > 0)
        {
            dots[position].setTextColor(getResources().getColor(R.color.colorAccent));
        }
    }



    //now  Implement OnPageChangeListener
    ViewPager.OnPageChangeListener listener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            dotsIndicator(position);

            currentPage = position;

            if(position == 0) //when the first page is opened
            {
                prev.setEnabled(false);
                next.setEnabled(true);
                prev.setVisibility(View.INVISIBLE);

                prev.setText("");
                next.setText("Next");
            }
            else if(position == dots.length - 1)
            {
                prev.setEnabled(true);
                next.setEnabled(true);
                prev.setVisibility(View.VISIBLE);

                prev.setText("Previous");
                next.setText("Got It!");

            }
            else // if page is not the first nor the last
            {
                prev.setEnabled(true);
                next.setEnabled(true);
                prev.setVisibility(View.VISIBLE);

                prev.setText("Previous");
                next.setText("Next");
            }

        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };
}
