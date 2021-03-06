package com.nealgosalia.timetable.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import com.nealgosalia.timetable.database.FragmentDatabase;
import com.nealgosalia.timetable.database.FragmentDetails;
import com.nealgosalia.timetable.utils.DividerItemDecoration;
import com.nealgosalia.timetable.utils.Lecture;
import com.nealgosalia.timetable.utils.RecyclerItemClickListener;

import java.util.ArrayList;
import java.util.List;

public class FridayFragment extends Fragment {

    private FragmentDatabase db;
    private List<Lecture> lecturesList = new ArrayList<>();
    private RecyclerView recyclerLectures;
    private LecturesAdapter mLectureAdapter;
    private TextView placeholderText;
    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_friday, container, false);
        placeholderText = view.findViewById(R.id.fridayPlaceholderText);
        db = new FragmentDatabase(getActivity());
        lecturesList = new ArrayList<>(db.getLectureList(4));
        if (lecturesList.size() != 0) {
            placeholderText.setVisibility(View.GONE);
        }
        recyclerLectures = view.findViewById(R.id.listFriday);
        mLectureAdapter = new LecturesAdapter(lecturesList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerLectures.setLayoutManager(mLayoutManager);
        recyclerLectures.setItemAnimator(new DefaultItemAnimator());
        recyclerLectures.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
        recyclerLectures.setAdapter(mLectureAdapter);
        recyclerLectures.addOnItemTouchListener(new RecyclerItemClickListener(getContext(), new RecyclerItemClickListener.OnItemClickListener() {
            @Override public void onItemClick(View view, int position) {
                Lecture lecture = lecturesList.get(position);
                showDeleteDialog(lecture, position);
            }
        }));
        return view;
    }

    public void showDeleteDialog(final Lecture lecture, final int position) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        dialogBuilder.setTitle(getResources().getString(R.string.delete));
        dialogBuilder.setMessage(getResources().getString(R.string.delete_lecture));
        dialogBuilder.setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                FragmentDetails fd = new FragmentDetails(
                        lecture.getDay(),
                        lecture.getSubjectName(),
                        Integer.parseInt(lecture.getStartTime().substring(0,2)),
                        Integer.parseInt(lecture.getStartTime().substring(3,5)),
                        Integer.parseInt(lecture.getEndTime().substring(0,2)),
                        Integer.parseInt(lecture.getEndTime().substring(3,5)),
                        lecture.getRoomNo()
                );
                db.remove(fd);
                lecturesList.remove(position);
                mLectureAdapter.notifyDataSetChanged();
            }
        });
        dialogBuilder.setNegativeButton(getResources().getString(R.string.no), null);
        AlertDialog b = dialogBuilder.create();
        b.show();
    }
}