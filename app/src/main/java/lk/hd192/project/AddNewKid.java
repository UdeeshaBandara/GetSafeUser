package lk.hd192.project;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import lk.hd192.project.Utils.AddKidSecondFragment;
import lk.hd192.project.Utils.NonSwappableViewPager;

public class AddNewKid extends GetSafeBase {


    private NonSwappableViewPager nonSwappableViewPager;
    FloatingActionButton fabNext;
    TextView txtSubHeading;
    ImageView icon_kid_loc;
    boolean firstCompleted = true, secondCompleted = false;

    AddKidFirstFragment addKidFirstFragment;
    AddKidSecondFragment addKidSecondFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_kid);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);


        nonSwappableViewPager = findViewById(R.id.view_pager_container);
        fabNext = findViewById(R.id.fab_next);
        txtSubHeading = findViewById(R.id.txt_sub_heading);
        icon_kid_loc = findViewById(R.id.icon_kid_loc);

        addKidFirstFragment = new AddKidFirstFragment();
        addKidSecondFragment = new AddKidSecondFragment();
        nonSwappableViewPager.setSwappable(false);

        setupViewPager(nonSwappableViewPager);

        fabNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (firstCompleted) {
//                    nonSwappableViewPager.setCurrentItem(0);
//                    firstCompleted=false;
//                }
//                else if (!secondCompleted)
                nonSwappableViewPager.setCurrentItem(1);
            }
        });


        nonSwappableViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {
                switch (state) {
                    case 1:
                        txtSubHeading.setText("Location Details");
                        break;

                }

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
            // Show 3 total pages.
            return 2;
        }
    }

    private void setupViewPager(ViewPager viewPager) {
        SectionsPagerAdapter sectionsPager = new SectionsPagerAdapter(getSupportFragmentManager());

        sectionsPager.addFragment(addKidFirstFragment);
        sectionsPager.addFragment(addKidSecondFragment);

        viewPager.setAdapter(sectionsPager);
    }
}