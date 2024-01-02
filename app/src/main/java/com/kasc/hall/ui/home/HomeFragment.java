package com.kasc.hall.ui.home;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.kasc.hall.MainActivity;
import com.kasc.hall.R;
import com.kasc.hall.ui.aboutus.BranchAdapter;
import com.kasc.hall.ui.aboutus.BranchModel;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private int[] images;
    private String[] text;
    private SliderAdapter adapter;
    private SliderView sliderView;
    private ImageView map;
    private ViewPager viewPager;
    private BranchAdapter branchAdapter;
    private List<BranchModel> list;

    @Override
    @Keep
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        list = new ArrayList<>();
        list.add(new BranchModel(R.drawable.cse_icon, "Bsc Computer Technology (Bsc CT)"));
        list.add(new BranchModel(R.drawable.cse_icon, "Bsc Information Technology (Bsc IT) "));
        list.add(new BranchModel(R.drawable.cse_icon, "Bsc Computer Science (Bsc CS)"));

        // adapter
        branchAdapter = new BranchAdapter(getContext(), list);
        // viewpager initialization
        viewPager = view.findViewById(R.id.viewpager);

        // setting adapter in viewpager
        viewPager.setAdapter(branchAdapter);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sliderView = view.findViewById(R.id.sliderView);
        // setting images and text
        images = new int[]{
                R.drawable.main,
                R.drawable.e2,
                R.drawable.stu,
        };
        text = new String[]{
                "kongunadu arts and science, Coimbatore",
                "KASC Entrance",
                "Students at KASC" ,
        };

        // creating adapter
        adapter = new SliderAdapter(images, text);
        // setting adapter in slider view
        sliderView.setSliderAdapter(adapter);
        sliderView.setSliderTransformAnimation(SliderAnimations.FADETRANSFORMATION);
        sliderView.setIndicatorAnimation(IndicatorAnimationType.SLIDE);
        sliderView.startAutoCycle();

        map = view.findViewById(R.id.map);
        map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMap();
            }
        });
    }

    private void openMap() {
        Uri uri = Uri.parse("geo:0, 0?q= kongunadu arts and science,Coimbatore ");
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        intent.setPackage("com.google.android.apps.maps");
        startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) getActivity()).setActionBarTitle("Home");
    }
}
