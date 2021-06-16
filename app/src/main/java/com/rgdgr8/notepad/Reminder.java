package com.rgdgr8.notepad;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;


/**
 * A simple {@link Fragment} subclass.
 */
public class Reminder extends Fragment implements View.OnTouchListener {

    static RecyclerView rcRem;
    private static FloatingActionButton fab;

    static ImageView emptyImgRem;
    static TextView emptyTvRem;

    private AdView mAdView;

    private float dX;
    private float dY;

    private int fixedFabClick = 0;

    static ConstraintLayout layout;

    public Reminder() {
    }

    public static Reminder newInstance() {
        return new Reminder();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_reminder, container, false);

        emptyImgRem = v.findViewById(R.id.imageViewEmptyRem);
        emptyTvRem = v.findViewById(R.id.textViewEmptyRem);
        layout = v.findViewById(R.id.frameLayoutRem);
        mAdView = v.findViewById(R.id.adRem);

        fab = v.findViewById(R.id.floatingActionButtonRem);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fixedFabClick++;

                if (fixedFabClick == 1) {
                    Intent intent1 = new Intent(getActivity(), ReminderActivity.class);
                    startActivity(intent1);
                }
            }
        });

        fab.setOnTouchListener(this);

        fab.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                View.DragShadowBuilder myShadow = new View.DragShadowBuilder(fab);
                if (Build.VERSION.SDK_INT >= 24) {
                    v.startDragAndDrop(null, myShadow, null, 0);
                } else {
                    v.startDrag(null, myShadow, null, 0);
                }
                return true;
            }
        });

        layout.setOnDragListener(dragListener);

        rcRem = v.findViewById(R.id.rcRem);
        rcRem.setAdapter(MainActivity.rA);
        rcRem.setLayoutManager(new LinearLayoutManager(getContext()));
        rcRem.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if ((dy != 0) && fab.isShown()) {
                    fab.hide();
                }
            }

            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {

                    fab.show();
                }

                super.onScrollStateChanged(recyclerView, newState);
            }
        });

        return v;
    }

    View.OnDragListener dragListener = new View.OnDragListener() {

        Boolean entered = true;
        float X = 0;
        float Y = 0;

        @Override
        public boolean onDrag(View v, DragEvent event) {
            switch (event.getAction()) {
                case DragEvent.ACTION_DRAG_STARTED:
                    return true;

                case DragEvent.ACTION_DRAG_ENTERED:
                    entered = true;
                    return true;

                case DragEvent.ACTION_DRAG_EXITED:
                    entered = false;
                    return true;

                case DragEvent.ACTION_DROP:
                    if (entered) {
                        fab.setY(event.getY());
                        fab.setX(event.getX() + dX);
                    }
                    if (fab.getX() < 0) {
                        fab.setX(5);
                    } else if ((fab.getX() + fab.getWidth()) > (layout.getWidth())) {
                        fab.setX((layout.getWidth() - fab.getWidth() - 5));
                    }
                    if (fab.getY() < 0) {
                        fab.setY(5);
                    } else if ((fab.getY() + fab.getHeight()) > (layout.getHeight())) {
                        fab.setY((layout.getHeight() - fab.getHeight() - 5));
                    }
                    return true;

                default:
                    return false;

            }
        }
    };

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                dX = v.getX() - event.getRawX();
                dY = v.getY() - event.getRawY();
                break;

            /*case MotionEvent.ACTION_MOVE:

                if ((v.getX()+v.getWidth()) < layout.getWidth() && v.getX() > 0 && v.getY() > 0 && (v.getY()+v.getHeight()) < layout.getHeight()) {
                    v.setY(event.getRawY() + dY);
                    v.setX(event.getRawX() + dX);
                } else {
                    if (v.getX() < 0) {
                        v.setX(5);
                    } else if ((v.getX()+v.getWidth()) > (layout.getWidth())) {
                        v.setX((layout.getWidth()-v.getWidth()-5));
                    }
                    if (v.getY() < 0) {
                        v.setY(5);
                    } else if ((v.getY()+v.getHeight()) > (layout.getHeight())) {
                        v.setY((layout.getHeight()-v.getHeight()-5));
                    }
                }
                break;

            case MotionEvent.ACTION_UP: //SET THE POSITION FINALLY ON ACTION UP, OTHERWISE, IT WILL NOT WORK!!!!!!
                if (v.getX() < 0) {
                    v.setX(5);
                } else if ((v.getX()+v.getWidth()) > (layout.getWidth())) {
                    v.setX((layout.getWidth()-v.getWidth()-5));
                }
                if (v.getY() < 0) {
                    v.setY(5);
                } else if ((v.getY()+v.getHeight()) > (layout.getHeight())) {
                    v.setY((layout.getHeight()-v.getHeight()-5));
                }
                break;

             */

            default:
                return false;
        }
        return false;
    }

    @Override
    public void onDestroy() {

        emptyImgRem = null;
        emptyTvRem = null;

        super.onDestroy();
    }

    private int reminderCheck() {
        int c = 0;
        for (String s : MainActivity.reminders) {
            if (s.equals("")) {
                c++;
            }
        }

        return c;
    }

    @Override
    public void onResume() {
        super.onResume();

        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        mAdView.setAdListener(new AdListener() {

            @Override
            public void onAdLoaded() {
                mAdView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAdFailedToLoad(int i) {
                mAdView.setVisibility(View.GONE);
            }
        });

        if (MainActivity.reminders.isEmpty() || reminderCheck() == MainActivity.reminders.size()) {
            emptyTvRem.setVisibility(View.VISIBLE);
            emptyImgRem.setVisibility(View.VISIBLE);
        } else {
            emptyTvRem.setVisibility(View.INVISIBLE);
            emptyImgRem.setVisibility(View.INVISIBLE);
        }

        if (MainActivity.mode.equals("f")) {
            fab.setBackgroundColor(getResources().getColor(R.color.red_for_pink));
            fab.setBackgroundTintList(getResources().getColorStateList(R.color.red_for_pink));
            fab.setImageResource(R.drawable.ic_alarm_add_yellow_24dp);
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                mAdView.setBackgroundColor(getResources().getColor(R.color.red_for_pink));
            }
        } else if (MainActivity.mode.equals("o")) {
            fab.setBackgroundColor(getResources().getColor(R.color.navy));
            fab.setBackgroundTintList(getResources().getColorStateList(R.color.navy));
            fab.setImageResource(R.drawable.ic_alarm_add_grey_24dp);
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                mAdView.setBackgroundColor(getResources().getColor(R.color.navy));
            }
        }

        fixedFabClick = 0;
    }
}
