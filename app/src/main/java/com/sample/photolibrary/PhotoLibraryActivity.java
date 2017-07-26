package com.sample.photolibrary;

import android.support.v4.app.Fragment;

public class PhotoLibraryActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return PhotoLibraryFragment.newInstance();
    }
}
