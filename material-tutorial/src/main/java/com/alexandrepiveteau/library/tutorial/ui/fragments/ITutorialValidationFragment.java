package com.alexandrepiveteau.library.tutorial.ui.fragments;

import android.support.annotation.NonNull;

import com.alexandrepiveteau.library.tutorial.ui.activities.TutorialActivity;

/**
 * Created by kevinleperf on 25/01/16.
 */
public interface ITutorialValidationFragment {
    boolean isValid();

    boolean onTryValidate(@NonNull TutorialActivity parent);
}
