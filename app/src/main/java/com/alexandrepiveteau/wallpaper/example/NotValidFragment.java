package com.alexandrepiveteau.wallpaper.example;

import com.alexandrepiveteau.library.tutorial.ui.fragments.AbstractTutorialValidationFragment;

/**
 * Created by kevinleperf on 09/02/16.
 */
public class NotValidFragment extends AbstractTutorialValidationFragment {
    @Override
    public boolean isValid() {
        return false;
    }

    @Override
    public boolean onTryValidate() {
        return false;
    }
}
