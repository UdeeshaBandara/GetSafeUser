package lk.hd192.project;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.animation.Animator;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;


import java.util.ArrayList;
import java.util.List;

import lk.hd192.project.Utils.GetSafeBase;
import lk.hd192.project.Utils.NonSwappableViewPager;

public class AddNewKid extends GetSafeBase {


    private NonSwappableViewPager nonSwappableViewPager;

    TextView txtSubHeading;
    LottieAnimationView kidAnimation, locationAnimation, doneAnimation, btnNext, mainSaveAnimation;
    public static boolean firstCompleted = false, secondCompleted = false;
    int currentPage = 1;
    AddKidFirstFragment addKidFirstFragment;
    AddKidSecondFragment addKidSecondFragment;
    AddKidThirdFragment addKidThirdFragment;
    Button btnSave;
    public static boolean isEditing = false, isLocationRemembered;

    public static String FirstName, LastName, SchoolName, Gender="null", Birthday, AddOne, AddTwo, City, PinnedLoc,kidId,kidLocId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_kid);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);


        nonSwappableViewPager = findViewById(R.id.view_pager_container);
        btnNext = findViewById(R.id.btn_next);
        txtSubHeading = findViewById(R.id.txt_sub_heading);
        kidAnimation = findViewById(R.id.kid_animation);
        doneAnimation = findViewById(R.id.done_animation);
        locationAnimation = findViewById(R.id.location_animation);

        mainSaveAnimation = findViewById(R.id.main_save_animation);
        btnSave = findViewById(R.id.btn_save);

        addKidFirstFragment = new AddKidFirstFragment();
        addKidSecondFragment = new AddKidSecondFragment();
        addKidThirdFragment = new AddKidThirdFragment();


        kidAnimation.setSpeed(0.5f);
        doneAnimation.setSpeed(0.5f);
        kidAnimation.setMinAndMaxProgress(0.0f, 0.5f);

        doneAnimation.setRepeatCount(2);

        nonSwappableViewPager.setOffscreenPageLimit(1);
        setupViewPager(nonSwappableViewPager);

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (currentPage == 1) {
                    addKidFirstFragment.validateFields();
                    if (firstCompleted & currentPage == 1) {
                        addKidFirstFragment.addKidBasicDetails();

                        nonSwappableViewPager.setCurrentItem(1);
                        txtSubHeading.setText("Location Details");
                        kidAnimation.setVisibility(View.GONE);
                        doneAnimation.setVisibility(View.GONE);
                        locationAnimation.setVisibility(View.VISIBLE);
                        currentPage = 2;
                    }
                } else if (currentPage == 2) {

                    addKidSecondFragment.validateFields();

                    if (secondCompleted) {
                        addKidSecondFragment.addKidLocationDetails();
                        addKidThirdFragment.updateFields();
                        nonSwappableViewPager.setCurrentItem(2);
                        txtSubHeading.setText("Confirm Details");
                        doneAnimation.setVisibility(View.VISIBLE);
                        locationAnimation.setVisibility(View.GONE);
                        kidAnimation.setVisibility(View.GONE);
                        currentPage = 3;
                        btnSave.setVisibility(View.VISIBLE);
                        btnNext.setVisibility(View.GONE);
                    }
                }

            }
        });
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainSaveAnimation.setVisibility(View.VISIBLE);
                mainSaveAnimation.playAnimation();
                mainSaveAnimation.setVisibility(View.GONE);

            }
        });


        mainSaveAnimation.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mainSaveAnimation.setVisibility(View.GONE);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        finish();
                    }
                }, 800);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

    }

    public void goToFirstStep() {
        nonSwappableViewPager.setCurrentItem(1);
        txtSubHeading.setText("Basic Details");
        kidAnimation.setVisibility(View.VISIBLE);
        doneAnimation.setVisibility(View.GONE);
        locationAnimation.setVisibility(View.GONE);
        currentPage = 2;

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

            return 3;
        }
    }

    private void setupViewPager(ViewPager viewPager) {
        SectionsPagerAdapter sectionsPager = new SectionsPagerAdapter(getSupportFragmentManager());

        sectionsPager.addFragment(addKidFirstFragment);
        sectionsPager.addFragment(addKidSecondFragment);
        sectionsPager.addFragment(addKidThirdFragment);

        viewPager.setAdapter(sectionsPager);
    }
}