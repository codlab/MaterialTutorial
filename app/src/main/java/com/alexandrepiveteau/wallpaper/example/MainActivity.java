package com.alexandrepiveteau.wallpaper.example;

import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;

import com.alexandrepiveteau.library.tutorial.ui.activities.TutorialActivity;
import com.alexandrepiveteau.library.tutorial.ui.fragments.TutorialFragment;
import com.alexandrepiveteau.library.tutorial.widgets.LinePageIndicatorEngine;
import com.alexandrepiveteau.library.tutorial.widgets.PageIndicator;

/**
 * Created by Tanmay Parikh on 7/19/2015.
 */
public class MainActivity extends TutorialActivity {

    private static final int[] BACKGROUND_COLORS = {
            Color.parseColor("#F44336"),
            Color.parseColor("#e9a83b"),
            Color.parseColor("#5b9899"),
            Color.parseColor("#265963"),
            Color.parseColor("#cbb6c5"),
            Color.parseColor("#4FC3F7"),
            Color.parseColor("#3F51B5"),
            Color.parseColor("#F44336")};

    @Override
    public String getIgnoreText() {
        return "SKIP";
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

    @Override
    public int getBackgroundColor(int position) {
        return BACKGROUND_COLORS[position];
    }

    @Override
    public int getNavigationBarColor(int position) {
        return BACKGROUND_COLORS[position];
    }

    @Override
    public int getStatusBarColor(int position) {
        return BACKGROUND_COLORS[position];
    }

    @Override
    public Fragment getTutorialFragmentFor(int position) {
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
