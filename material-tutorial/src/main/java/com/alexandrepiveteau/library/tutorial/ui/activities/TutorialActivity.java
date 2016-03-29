package com.alexandrepiveteau.library.tutorial.ui.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.PendingIntent;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.alexandrepiveteau.library.tutorial.CustomAction;
import com.alexandrepiveteau.library.tutorial.R;
import com.alexandrepiveteau.library.tutorial.ui.TutorialViewPagerAdapter;
import com.alexandrepiveteau.library.tutorial.ui.fragments.AbstractTutorialValidationFragment;
import com.alexandrepiveteau.library.tutorial.ui.fragments.ITutorialValidationFragment;
import com.alexandrepiveteau.library.tutorial.ui.fragments.TutorialFragment;
import com.alexandrepiveteau.library.tutorial.ui.interfaces.ITutorialActivity;
import com.alexandrepiveteau.library.tutorial.utils.ColorMixer;
import com.alexandrepiveteau.library.tutorial.widgets.BlockableRightViewPager;
import com.alexandrepiveteau.library.tutorial.widgets.DefaultPageIndicatorEngine;
import com.alexandrepiveteau.library.tutorial.widgets.PageIndicator;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;


public abstract class TutorialActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener, ViewPager.OnPageChangeListener, ITutorialActivity {

    @StringRes
    public abstract int getIgnoreText();

    private final String getInternalIgnoreText() {
        return getString(getIgnoreText());
    }

    public PageIndicator.Engine getPageIndicatorEngine() {
        return new DefaultPageIndicatorEngine();
    }

    public abstract int getCount();

    @ColorRes
    public abstract int getBackgroundColor(int position);

    @ColorRes
    public abstract int getNavigationBarColor(int position);

    @ColorRes
    public abstract int getStatusBarColor(int position);

    public abstract AbstractTutorialValidationFragment getTutorialFragmentFor(int position);

    public abstract boolean isNavigationBarColored();

    public abstract boolean isStatusBarColored();

    public abstract ViewPager.PageTransformer getPageTransformer();

    private boolean _avoid_try_validate;

    private ReentrantLock _lock;

    //Views used
    private Button mButtonLeft;
    private ImageButton mImageButtonLeft;
    private ImageButton mImageButtonRight;
    private PageIndicator mPageIndicator;
    private RelativeLayout mRelativeLayout;
    private BlockableRightViewPager mViewPager;

    //Objects needed
    private TutorialViewPagerAdapter mAdapter;
    private ColorMixer mColorMixerBackground;
    private ColorMixer mColorMixerNavigationBar;
    private ColorMixer mColorMixerStatusBas;

    private int mPreviousPage; //Needed if we want to animate the custom actions

    private List<Fragment> mFragmentList;

    private void setupFragmentList() {
        List<Fragment> fragments = new ArrayList<>();
        for (int i = 0; i < getCount(); i++) {
            fragments.add(getTutorialFragmentFor(i));
        }
        mFragmentList = fragments;
    }

    @Override
    public final void onValidate(@NonNull AbstractTutorialValidationFragment fragment, boolean is_ok) {
        /*if (is_ok) {
            _avoid_try_validate = true;
            onClick(mImageButtonRight);
            _avoid_try_validate = false;
        }*/
        invalidateSwipable();
    }

    private void invalidateSwipable() {
        ITutorialValidationFragment fragment = (ITutorialValidationFragment) mFragmentList.get(mViewPager.getCurrentItem());

        if (fragment.isValid()) {
            setSwipableRight(true);
        } else {
            setSwipableRight(false);
        }
    }

    public void setSwipableRight(boolean state) {
        mViewPager.setSwipableRight(state);
    }

    @Override
    public void onClick(@NonNull View v) {
        if (v.getId() == R.id.tutorial_button_left || v.getId() == R.id.tutorial_button_image_left) {
            boolean hasCustomAction = false;

            boolean is_skippable = true;

            ITutorialValidationFragment current = (ITutorialValidationFragment) mFragmentList.get(mViewPager.getCurrentItem());

            if (current instanceof CustomAction) {
                if (((CustomAction) mFragmentList.get(mViewPager.getCurrentItem())).isEnabled()) {
                    hasCustomAction = true;
                }
            }

            if (current instanceof TutorialFragment) {
                is_skippable = ((TutorialFragment) current).isValid();
            }

            if (hasCustomAction) {
                PendingIntent intent = ((CustomAction) mFragmentList.get(mViewPager.getCurrentItem())).getCustomActionPendingIntent();
                try {
                    intent.send();
                } catch (PendingIntent.CanceledException exception) {
                    exception.printStackTrace();
                }
            } else if (mViewPager.getCurrentItem() > 0) {
                mViewPager.setCurrentItem(mViewPager.getCurrentItem() - 1, true);
            } else if (is_skippable && mViewPager.getCurrentItem() == 0) {

                int index = mViewPager.getCurrentItem();
                getFragment(index).onTryValidate(this);

                //we linearly cover every fragment until we found one with the proper data

                while (index < mFragmentList.size() && getFragment(index).isValid()) {
                    index++;

                    AbstractTutorialValidationFragment fragment = getFragment(index);
                    if (fragment != null && index + 1 < getCount()) {
                        fragment.onTryValidate(this);
                    }
                }

                while (index > mFragmentList.size()) index--;

                mViewPager.setCurrentItem(index, true);
            }
        } else if (v.getId() == R.id.tutorial_button_image_right) {
            boolean is_valid = true;
            AbstractTutorialValidationFragment fragment = getFragment(mViewPager.getCurrentItem());

            if (!_avoid_try_validate) {
                ((AbstractTutorialValidationFragment) fragment).onTryValidate(this);
            }

            is_valid = ((AbstractTutorialValidationFragment) fragment).isValid();

            //we are at the end and we have a simple fragment blocking
            if (fragment instanceof TutorialFragment
                    && mViewPager.getCurrentItem() == getCount() - 1) {
                is_valid = true;
            }

            if (is_valid) {
                if (mViewPager.getCurrentItem() == getCount() - 1) {
                    finish();
                } else {
                    mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1, true);
                }
            }
        }
//        invalidateSwipable();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        _avoid_try_validate = false;

        setContentView(R.layout.activity_tutorial);

        mButtonLeft = (Button) findViewById(R.id.tutorial_button_left);
        mImageButtonLeft = (ImageButton) findViewById(R.id.tutorial_button_image_left);
        mImageButtonRight = (ImageButton) findViewById(R.id.tutorial_button_image_right);
        mPageIndicator = (PageIndicator) findViewById(R.id.tutorial_page_indicator);
        mRelativeLayout = (RelativeLayout) findViewById(R.id.relative_layout);
        mViewPager = (BlockableRightViewPager) findViewById(R.id.view_pager);

        mButtonLeft.setOnClickListener(this);
        mImageButtonLeft.setOnClickListener(this);
        mImageButtonRight.setOnClickListener(this);
        mImageButtonLeft.setOnLongClickListener(this);
        mImageButtonRight.setOnLongClickListener(this);

        setupFragmentList();

        mAdapter = new TutorialViewPagerAdapter(getSupportFragmentManager());

        mAdapter.setFragments(mFragmentList);

        mRelativeLayout.setBackgroundColor(Color.BLUE);
        mViewPager.setAdapter(mAdapter);
        mViewPager.addOnPageChangeListener(this);
        mViewPager.setPageTransformer(false, getPageTransformer());

        mPageIndicator.setEngine(getPageIndicatorEngine());
        mPageIndicator.setViewPager(mViewPager);

        //We use this to actualize the Strings
        mPreviousPage = 0;
        onPageSelected(0);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        mColorMixerBackground = new ColorMixer();
        mColorMixerNavigationBar = new ColorMixer();
        mColorMixerStatusBas = new ColorMixer();

        Resources resources = getResources();

        int first_color_mixer = resources.getColor(getBackgroundColor(position));
        int second_color_mixer;
        try {
            second_color_mixer = resources.getColor(getBackgroundColor(position + 1));
        } catch (Exception exception) {
            second_color_mixer = resources.getColor(getBackgroundColor(position));
        }

        int first_color_navigation = resources.getColor(getNavigationBarColor(position));
        int second_color_navigation;
        try {
            second_color_navigation = resources.getColor(getNavigationBarColor(position + 1));
        } catch (Exception exception) {
            second_color_navigation = resources.getColor(getNavigationBarColor(position));
        }

        int first_color_status = resources.getColor(getStatusBarColor(position));
        int second_color_status;

        try {
            second_color_status = resources.getColor(getStatusBarColor(position + 1));
        } catch (Exception exception) {
            second_color_status = resources.getColor(getStatusBarColor(position));
        }


        mColorMixerBackground.setFirstColor(first_color_mixer);
        mColorMixerBackground.setSecondColor(second_color_mixer);

        mColorMixerNavigationBar.setFirstColor(first_color_navigation);
        mColorMixerNavigationBar.setSecondColor(second_color_navigation);


        mColorMixerStatusBas.setFirstColor(first_color_status);
        mColorMixerStatusBas.setSecondColor(second_color_status);

        setBackgroundColor(mColorMixerBackground.getMixedColor(positionOffset));
        setSystemBarsColors(mColorMixerNavigationBar.getMixedColor(positionOffset), mColorMixerStatusBas.getMixedColor(positionOffset));


    }

    @Override
    public void onPageSelected(int position) {

        closeKeyboard();
        //TODO FIX ISSUE WITH
        if (mViewPager.getCurrentItem() == 0) {
            mButtonLeft.setText(getInternalIgnoreText());

            //boolean skippable = false;
            //Fragment fragment = mFragmentList.get(0);

            //if (fragment instanceof TutorialFragment) {
            //    skippable = ((TutorialFragment) fragment).isSkippable();
            //}
            //if (skippable) {
            animateViewFadeIn(mButtonLeft);
            //} else {
            //    mButtonLeft.setVisibility(View.INVISIBLE);
            //}
            animateViewScaleOut(mImageButtonLeft);

        } else if (mViewPager.getCurrentItem() == getCount() - 1) {
            animateViewFadeOut(mButtonLeft);
            animateViewScaleIn(mImageButtonLeft);
        } else {
            animateViewFadeOut(mButtonLeft);
            animateViewScaleIn(mImageButtonLeft);
        }

        if (mViewPager.getCurrentItem() == getCount() - 1 && mViewPager.getCurrentItem() != mPreviousPage) {
            mImageButtonRight.setImageResource(R.drawable.animated_next_to_ok);
            AnimationDrawable animationDrawable = (AnimationDrawable) mImageButtonRight.getDrawable();
            animationDrawable.start();
        } else if (mViewPager.getCurrentItem() != mPreviousPage && mPreviousPage == getCount() - 1) {
            mImageButtonRight.setImageResource(R.drawable.animated_ok_to_next);
            AnimationDrawable animationDrawable = (AnimationDrawable) mImageButtonRight.getDrawable();
            animationDrawable.start();
        }

        handleCustomIcons(position);
        invalidateSwipable();
    }

    private void closeKeyboard() {
        View view = mViewPager;
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void animateViewScaleIn(final @NonNull View view) {
        view.animate()
                .scaleX(1)
                .scaleY(1)
                .setDuration(getResources().getInteger(android.R.integer.config_shortAnimTime))
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        super.onAnimationStart(animation);
                        view.setVisibility(View.VISIBLE);
                    }
                })
                .start();
    }

    private void animateViewScaleOut(final @NonNull View view) {
        view.animate()
                .scaleX(0)
                .scaleY(0)
                .setDuration(getResources().getInteger(android.R.integer.config_shortAnimTime))
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        view.setVisibility(View.GONE);
                    }
                })
                .start();
    }

    private void animateViewFadeIn(final @NonNull View view) {
        view.animate()
                .alpha(1f)
                .setDuration(getResources().getInteger(android.R.integer.config_shortAnimTime))
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        super.onAnimationStart(animation);
                        view.setVisibility(View.VISIBLE);
                    }
                })
                .start();
    }

    private void animateViewFadeOut(final @NonNull View view) {
        view.animate()
                .alpha(0f)
                .setDuration(getResources().getInteger(android.R.integer.config_shortAnimTime))
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        view.setVisibility(View.GONE);
                    }
                })
                .start();
    }

    private void handleCustomIcons(int position) {
        boolean hadPreviousPageCustomIcon = false;
        boolean hasCustomIcon = false;

        int previousPageIcon;
        final int currentPageIcon;

        if (mFragmentList.get(mPreviousPage) instanceof CustomAction) {
            hadPreviousPageCustomIcon = ((CustomAction) mFragmentList.get(mPreviousPage)).isEnabled();
        }

        if (hadPreviousPageCustomIcon) {
            previousPageIcon = ((CustomAction) mFragmentList.get(mPreviousPage)).getCustomActionIcon();
        } else {
            previousPageIcon = R.drawable.static_previous;
        }

        if (mFragmentList.get(position) instanceof CustomAction) {
            hasCustomIcon = ((CustomAction) mFragmentList.get(position)).isEnabled();
        }

        if (hasCustomIcon) {
            currentPageIcon = ((CustomAction) mFragmentList.get(position)).getCustomActionIcon();
        } else {
            currentPageIcon = R.drawable.static_previous;
        }

        if (currentPageIcon != previousPageIcon) {
            mImageButtonLeft.animate()
                    .alpha(0)
                    .setDuration(getResources().getInteger(android.R.integer.config_shortAnimTime))
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            mImageButtonLeft.setImageResource(currentPageIcon);
                            mImageButtonLeft.animate()
                                    .alpha(1f)
                                    .setDuration(getResources().getInteger(android.R.integer.config_shortAnimTime))
                                    .setListener(null)//We clear all listeners
                                    .start();
                        }
                    })
                    .start();
        }

        mPreviousPage = position;
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    private void setBackgroundColor(int backgroundColor) {
        mRelativeLayout.setBackgroundColor(backgroundColor);
    }

    private void setSystemBarsColors(int colorNavigationBar, int colorStatusBar) {
        // Tinted status bar and navigation bars are available only on Lollipop, sadly :(
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (isNavigationBarColored()) {
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                getWindow().setNavigationBarColor(colorNavigationBar);
            }
            if (isStatusBarColored()) {
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                getWindow().setStatusBarColor(colorStatusBar);
            }
        }
    }

    @Override
    public boolean onLongClick(@NonNull View v) {
        if (v.getId() == R.id.tutorial_button_image_left) {
            //Toast.makeText(this, getPreviousText(), Toast.LENGTH_SHORT).show();
        } else if (v.getId() == R.id.tutorial_button_image_right) {
            //Toast.makeText(this, getNextText(), Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    @Nullable
    private AbstractTutorialValidationFragment getFragment(int index) {
        if (index < mFragmentList.size())
            return ((AbstractTutorialValidationFragment) mFragmentList.get(index));
        return null;
    }
}
