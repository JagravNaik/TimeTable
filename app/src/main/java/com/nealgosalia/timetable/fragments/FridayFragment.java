package com.nealgosalia.timetable.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nealgosalia.timetable.R;
import com.nealgosalia.timetable.adapters.LecturesAdapter;
import com.nealgosalia.timetable.utils.DividerItemDecoration;
import com.nealgosalia.timetable.database.FragmentDatabase;
import com.nealgosalia.timetable.utils.Lecture;

import java.util.ArrayList;
import java.util.List;

public class FridayFragment extends Fragment {

    private List<Lecture> lecturesList = new ArrayList<>();
    private RecyclerView recyclerLectures;
    private LecturesAdapter mLectureAdapter;
    private TextView placeholderText;
    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_friday, container, false);
        placeholderText = (TextView) view.findViewById(R.id.fridayPlaceholderText);
        FragmentDatabase db = new FragmentDatabase(getActivity());
        lecturesList = new ArrayList<>(db.getLectureList(4));
        if(lecturesList.size()!=0){
            placeholderText.setVisibility(View.GONE);
        }
        recyclerLectures = (RecyclerView) view.findViewById(R.id.listFriday);
        mLectureAdapter = new LecturesAdapter(lecturesList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerLectures.setLayoutManager(mLayoutManager);
        recyclerLectures.setItemAnimator(new DefaultItemAnimator());
        recyclerLectures.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
        recyclerLectures.setAdapter(mLectureAdapter);
        return view;
    }
}