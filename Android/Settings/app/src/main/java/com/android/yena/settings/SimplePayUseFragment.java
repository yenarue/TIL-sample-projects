package com.android.yena.settings;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Yena on 2016-11-01.
 */
public class SimplePayUseFragment extends Fragment {
    private static final String TAG = "SimplePayUseFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_simplepay_use, container, false);
    }
}
