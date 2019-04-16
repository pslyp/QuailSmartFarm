package com.pslyp.dev.quailsmartfarm;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import static android.content.Context.MODE_PRIVATE;


/**
 * A simple {@link Fragment} subclass.
 */
public class MeFragment extends Fragment {

    private SharedPreferences sp;
    private final String PREF_NAME = "LoginPreferences";

    private ImageView mImage;
    private TextView mName, mEmail;

    public MeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_me, container, false);

        initInstance(view);

        return view;
    }

    private void initInstance(View view) {

        mImage = view.findViewById(R.id.image_view_account);
        mName = view.findViewById(R.id.text_view_name);
        mEmail = view.findViewById(R.id.text_view_email);

        sp = getContext().getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        String id = sp.getString("ID", "");
        String firstName = sp.getString("FIRST_NAME", "");
        String lastName = sp.getString("LAST_NAME", "");
        String email = sp.getString("EMAIL", "");
        String photo_url = sp.getString("URL_PHOTO", "");

        if(photo_url != null) {
            Glide.with(getContext()).load(photo_url).into(mImage);
        }

        mName.setText(firstName + lastName);
        mEmail.setText(email);
    }

}
