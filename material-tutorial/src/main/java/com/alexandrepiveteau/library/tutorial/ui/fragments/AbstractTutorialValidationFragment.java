package com.alexandrepiveteau.library.tutorial.ui.fragments;

import android.app.Activity;
import android.support.v4.app.Fragment;

import com.alexandrepiveteau.library.tutorial.ui.interfaces.ITutorialActivity;

/**
 * Created by kevinleperf on 03/02/16.
 */
public abstract class AbstractTutorialValidationFragment extends Fragment implements ITutorialValidationFragment {

    private static final String INVALID_ACTIVITY_ERROR = "The activity does not implements " + ITutorialValidationFragment.class.getSimpleName();
    private ITutorialActivity _activity;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (!(activity instanceof ITutorialActivity)) {
            throw new IllegalStateException(INVALID_ACTIVITY_ERROR);
        } else {
            _activity = (ITutorialActivity) activity;
        }
    }

    @Override
    public void onDetach() {
        _activity = null;

        super.onDetach();
    }

    public void fireActivityValidateCurrent() {
        if (_activity != null) _activity.onValidate(this, isValid());
    }
}
