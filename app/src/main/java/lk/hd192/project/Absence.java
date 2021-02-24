package lk.hd192.project;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.florent37.singledateandtimepicker.SingleDateAndTimePicker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import lk.hd192.project.Utils.GetSafeBase;
import lk.hd192.project.Utils.GetSafeServices;

import lk.hd192.project.Utils.SwappableViewPager;
import lk.hd192.project.Utils.VolleyJsonCallback;

public class Absence extends GetSafeBase {

    private SwappableViewPager swappableViewPager;
public static boolean isNewAbsentAdded=false;
    MarkAbsent markAbsent;
    ViewAbsent viewAbsent;
    View mark_indicator, review_indicator;
    TextView txt_current_name, heading;

    @SuppressLint("SimpleDateFormat")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_absence);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        markAbsent = new MarkAbsent();
        viewAbsent = new ViewAbsent();

        findViewById(R.id.btn_absence_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                viewAbsent.removeDates();
                onBackPressed();
            }
        });
        findViewById(R.id.txt_mark_absent).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                swappableViewPager.setCurrentItem(0);
            }
        });
        findViewById(R.id.txt_review_absent).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                swappableViewPager.setCurrentItem(1);
                if (tinyDB.getBoolean("isStaffAccount"))
                    viewAbsent.getUserAbsent();
                else
                    viewAbsent.getChildAbsent();
            }
        });
        txt_current_name = findViewById(R.id.txt_current_name);
        swappableViewPager = findViewById(R.id.view_pager_container);
        mark_indicator = findViewById(R.id.mark_indicator);
        review_indicator = findViewById(R.id.review_indicator);
        heading = findViewById(R.id.heading);
        swappableViewPager.setOffscreenPageLimit(1);
        setupViewPager(swappableViewPager);

        if (tinyDB.getBoolean("isStaffAccount")) {
            txt_current_name.setVisibility(View.INVISIBLE);

        } else {
            txt_current_name.setText("Kid Name :" + tinyDB.getString("selectedChildName"));

        }

        swappableViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    mark_indicator.setVisibility(View.VISIBLE);
                    review_indicator.setVisibility(View.INVISIBLE);
                    heading.setText("Mark Absent");
                } else {
                    mark_indicator.setVisibility(View.INVISIBLE);
                    review_indicator.setVisibility(View.VISIBLE);
                    heading.setText("Review Absent");
                    if (tinyDB.getBoolean("isStaffAccount")&isNewAbsentAdded) {
                        viewAbsent.removeDates();
                        viewAbsent.getUserAbsent();
                        isNewAbsentAdded=false;
                    } else if (!tinyDB.getBoolean("isStaffAccount")&isNewAbsentAdded){
                        isNewAbsentAdded=false;
                        viewAbsent.removeDates();
                        viewAbsent.getChildAbsent();
                    }
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        private final List<Fragment> mFragmentList = new ArrayList<>();

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            return mFragmentList.get(position);

        }

        public void addFragment(Fragment fragment) {
            mFragmentList.add(fragment);
        }

        @Override
        public int getCount() {

            return 2;
        }
    }

    private void setupViewPager(ViewPager viewPager) {
        SectionsPagerAdapter sectionsPager = new SectionsPagerAdapter(getSupportFragmentManager());

        sectionsPager.addFragment(markAbsent);
        sectionsPager.addFragment(viewAbsent);


        viewPager.setAdapter(sectionsPager);
    }

}