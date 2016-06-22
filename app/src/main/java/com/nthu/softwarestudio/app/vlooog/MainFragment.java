package com.nthu.softwarestudio.app.vlooog;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.ImageButton;
import android.content.Intent;
import com.nthu.softwarestudio.app.vlooog.data.FriendListActivity;

/**
 * Created by Ywuan on 19/06/2016.
 */
public class MainFragment extends Fragment {
    private AlphaAnimation buttonClick = new AlphaAnimation(1F, 0.3F);

    private ImageButton profile_button = null;
    private ImageButton camera_button = null;
    private ImageButton friend_button = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        profile_button = (ImageButton) rootView.findViewById(R.id.main_profile_button);
        camera_button = (ImageButton) rootView.findViewById(R.id.main_camera_button);
        friend_button = (ImageButton) rootView.findViewById(R.id.main_friend_button);

        profile_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(buttonClick);
            }
        });

        camera_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(buttonClick);
            }
        });

        friend_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(buttonClick);
                Intent intent = new Intent(getContext(), FriendListActivity.class);
                startActivity(intent);
            }
        });

        return  rootView;
    }
}
