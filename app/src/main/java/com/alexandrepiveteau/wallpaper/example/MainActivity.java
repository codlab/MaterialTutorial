package com.alexandrepiveteau.wallpaper.example;

import android.support.annotation.ColorRes;
import android.support.annotation.StringRes;
import android.support.v4.view.ViewPager;

import com.alexandrepiveteau.library.tutorial.ui.activities.TutorialActivity;
import com.alexandrepiveteau.library.tutorial.ui.fragments.AbstractTutorialValidationFragment;
import com.alexandrepiveteau.library.tutorial.ui.fragments.TutorialFragment;
import com.alexandrepiveteau.library.tutorial.widgets.LinePageIndicatorEngine;
import com.alexandrepiveteau.library.tutorial.widgets.PageIndicator;

/**
 * Created by Tanmay Parikh on 7/19/2015.
 */
public class MainActivity extends TutorialActivity {

    private static final
    @ColorRes
    int[] BACKGROUND_COLORS = {
            R.color.first,
            R.color.second,
            R.color.third,
            R.color.fourth,
            R.color.fifth,
            R.color.sixth,
            R.color.seventh,
            R.color.first};

    @StringRes
    @Override
    public int getIgnoreText() {
        return R.string.skip;
    }

    /**
     * Requesting an IndicatorEngine to draw dots
     * Lib comes with 6 Engines built in:
     * DefaultPageIndicatorEngine (Material Design)
     * GoogleNowLauncherIndicatorEngine (Similar to the indicators in Google Now Launcher)
     * SimplePageIndicatorEngine (Simple dot color change on page change)
     * TyzenPageIndicatorEngine (Lines that rotate on page select)
     * RingPageIndicatorEngine (Rings that color up, two animations available)
     * LinePageIndicatorEngine (Line that stretches like progress or line can be constant size and move around)
     *
     * @return PageIndicatorEngine class which draws the indicator dots
     */
    @Override
    public PageIndicator.Engine getPageIndicatorEngine() {
        return new LinePageIndicatorEngine();
    }

    @Override
    public int getCount() {
        return BACKGROUND_COLORS.length;
    }

    @ColorRes
    @Override
    public int getBackgroundColor(int position) {
        return BACKGROUND_COLORS[position];
    }

    @ColorRes
    @Override
    public int getNavigationBarColor(int position) {
        return BACKGROUND_COLORS[position];
    }

    @ColorRes
    @Override
    public int getStatusBarColor(int position) {
        return BACKGROUND_COLORS[position];
    }

    @Override
    public AbstractTutorialValidationFragment getTutorialFragmentFor(int position) {
        switch (position) {
            case 3:
                return new TutorialFragment.Builder()
                        .setSkippable(false)
                        .setTitle("Title")
                        .setDescription("Desc")
                        .setImageResourceBackground(R.drawable.device)
                        .setImageResourceForeground(R.mipmap.ic_launcher)
                        .build();
            case 4:
                return new TutorialFragment.Builder()
                        .setTitle("Title")
                        .setSkippable(true)
                        .setDescription("Desc")
                        .setImageResourceBackground(R.drawable.device)
                        .setImageResourceForeground(R.mipmap.ic_launcher)
                        .build();
            case 5:
            case 6:
                return new NotValidFragment();
            default:
                return new TutorialFragment.Builder()
                        .setTitle("Title")
                        .setDescription("Desc")
                        .setImageResourceBackground(R.drawable.device)
                        .setImageResourceForeground(R.mipmap.ic_launcher)
                        .setSkippable(true)
                        .build();
        }
    }

    @Override
    public boolean isNavigationBarColored() {
        return true;
    }

    @Override
    public boolean isStatusBarColored() {
        return true;
    }

    @Override
    public ViewPager.PageTransformer getPageTransformer() {
        return TutorialFragment.getParallaxPageTransformer(1.25f);
    }
}
