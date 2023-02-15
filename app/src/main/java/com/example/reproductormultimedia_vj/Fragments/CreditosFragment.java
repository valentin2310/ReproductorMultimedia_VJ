package com.example.reproductormultimedia_vj.Fragments;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.MediaController;
import android.widget.VideoView;

import com.example.reproductormultimedia_vj.R;

public class CreditosFragment extends Fragment {

    public CreditosFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_creditos, container, false);

        VideoView vid = view.findViewById(R.id.creditos_video);

        vid.setVideoURI(Uri.parse("android.resource://" + getActivity().getPackageName() + "/" + R.raw.sake_binks));

        MediaController mediaController = new MediaController(getContext());
        vid.setMediaController(mediaController);

        vid.start();

        return view;
    }
}