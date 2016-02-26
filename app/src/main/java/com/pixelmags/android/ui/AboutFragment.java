package com.pixelmags.android.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pixelmags.android.pixelmagsapp.R;

/**
 * Created by Annie on 29/09/15.
 */
public class AboutFragment extends Fragment
{
    private TextView Version;
    private TextView Copyright;
    private TextView Url;
    private TextView Url1;




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // set the AboutFragment Listener
        View rootView = inflater.inflate(R.layout.fragment_about, container, false);

        Version = (TextView) rootView.findViewById(R.id.Version);
        Copyright = (TextView) rootView.findViewById(R.id.copyright);
        Url = (TextView) rootView.findViewById(R.id.ourwebsite);
        Url1 = (TextView) rootView.findViewById(R.id.ourconditions);
        return rootView;

    }
}

