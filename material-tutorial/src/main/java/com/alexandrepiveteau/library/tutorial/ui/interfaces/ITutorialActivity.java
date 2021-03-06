package com.alexandrepiveteau.library.tutorial.ui.interfaces;

import android.support.annotation.NonNull;

import com.alexandrepiveteau.library.tutorial.ui.fragments.AbstractTutorialValidationFragment;

/**
 * Created by kevinleperf on 01/02/16.
 */
public interface ITutorialActivity {
    void onValidate(@NonNull AbstractTutorialValidationFragment fragment, boolean is_ok);
}
