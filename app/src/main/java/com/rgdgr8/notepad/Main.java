package com.rgdgr8.notepad;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Collections;


/**
 * A simple {@link Fragment} subclass.
 */
public class Main extends Fragment implements View.OnTouchListener {

    static RecyclerView rc;
    private static FloatingActionButton fab;
    private static FloatingActionButton transFab;

    private AdView mAdView;

    static ImageView emptyImg;
    static TextView emptyTv;

    private float dX;
    private float dY;
    private int click = 0;

    private int fixedFabClick=0;

    boolean fixed;

    static ConstraintLayout layout;

    public Main() {
        // Required empty public constructor
    }

    public static Main newInstance() {
        return new Main();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_main, container, false);

        emptyImg = v.findViewById(R.id.imageViewEmpty);
        emptyTv = v.findViewById(R.id.textViewEmpty);
        layout = v.findViewById(R.id.frameLayoutMain);

        fab = v.findViewById(R.id.floatingActionButton);
        transFab = v.findViewById(R.id.floatingActionButtonTrans);
        rc = v.findViewById(R.id.rcMain);
        mAdView = v.findViewById(R.id.ad);

        //Toast toast = Toast.makeText(getActivity(), "Double click to use floating button and drag to change position", Toast.LENGTH_SHORT);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fixedFabClick++;

                /*Handler clickHandler = new Handler();
                Runnable r = new Runnable() {
                    @Override
                    public void run() {
                        Log.i("chandle", String.valueOf(fixedFabClick));
                        fixedFabClick = 0;
                    }
                };

                 */

                /*if (fixedFabClick == 1) {
                    Log.i("c1", String.valueOf(fixedFabClick));
                    clickHandler.postDelayed(r, 1000);
                    toast.show();
                } else if (fixedFabClick == 2) {

                 */

                if (fixedFabClick==1) {
                    //toast.cancel();
                    Intent intent1 = new Intent(getActivity(), EditActivity.class);
                    intent1.putExtra("textPresent", "");
                    intent1.putExtra("mORf", 1);
                    startActivity(intent1);
                }
            }
            // }
        });

        transFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                click++;

                 if (click == 1) {
                    Intent intent1 = new Intent(getActivity(), ReminderActivity.class);
                    startActivity(intent1);
                }
            }
        });
        transFab.setOnTouchListener(this);
        fab.setOnTouchListener(this);

        fab.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                View.DragShadowBuilder myShadow = new View.DragShadowBuilder(fab);
                fixed=true;
                if (Build.VERSION.SDK_INT >= 24) {
                    v.startDragAndDrop(null, myShadow, null, 0);
                } else {
                    v.startDrag(null, myShadow, null, 0);
                }
                return true;
            }
        });

        transFab.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                View.DragShadowBuilder myShadow = new View.DragShadowBuilder(transFab);
                fixed=false;
                if (Build.VERSION.SDK_INT >= 24) {
                    v.startDragAndDrop(null, myShadow, null, 0);
                } else {
                    v.startDrag(null, myShadow, null, 0);
                }
                return true;
            }
        });

        layout.setOnDragListener(dragListener);

        rc.setAdapter(MainActivity.mA);
        rc.setLayoutManager(new LinearLayoutManager(getContext()));
        rc.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if ((dy != 0) && fab.isShown()) {
                    fab.hide();
                    transFab.hide();
                }
            }

            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if (MainActivity.add) {
                        transFab.show();
                    }
                    fab.show();
                }

                super.onScrollStateChanged(recyclerView, newState);
            }
        });

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN, 0) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {

                int from = viewHolder.getAdapterPosition();
                int to = target.getAdapterPosition();

                Collections.swap(MainActivity.copies, from, to);
                MainActivity.db.deleteOrUpdateData();

                MainActivity.mA.notifyItemMoved(from, to);

                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

            }
        });

        itemTouchHelper.attachToRecyclerView(rc);

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
                    X = fab.getX() - event.getX();//dragevent.getX gives same thing as motionevent.getX
                    Y = fab.getY() - event.getY();
                    return true;

                case DragEvent.ACTION_DRAG_ENTERED:
                    entered = true;
                    return true;

                case DragEvent.ACTION_DRAG_EXITED:
                    entered = false;
                    return true;

                case DragEvent.ACTION_DROP:
                    if (fixed) {
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
                    }else {
                        if (entered) {
                            transFab.setY(event.getY());
                            transFab.setX(event.getX() + dX);
                        }
                        if (transFab.getX() < 0) {
                            transFab.setX(5);
                        } else if ((transFab.getX() + transFab.getWidth()) > (layout.getWidth())) {
                            transFab.setX((layout.getWidth() - transFab.getWidth() - 5));
                        }
                        if (transFab.getY() < 0) {
                            transFab.setY(5);
                        } else if ((transFab.getY() + transFab.getHeight()) > (layout.getHeight())) {
                            transFab.setY((layout.getHeight() - transFab.getHeight() - 5));
                        }
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
                dX = v.getX() - event.getRawX();//constant diffeence between rawX and v.x
                dY = v.getY() - event.getRawY();
                Log.i("v", String.valueOf(v.getY()));
                Log.i("eR", String.valueOf(event.getRawY()));
                Log.i("e", String.valueOf(event.getY()));
                break;

           /* case MotionEvent.ACTION_MOVE:

                if ((v.getX() + v.getWidth()) < layout.getWidth() && v.getX() > 0 && v.getY() > 0 && (v.getY() + v.getHeight()) < layout.getHeight()) {
                    v.setY(event.getRawY() + dY);
                    v.setX(event.getRawX() + dX);
                    Log.i("eR", String.valueOf(event.getRawY()));
                } else {
                    if (v.getX() < 0) {
                        v.setX(5);
                    } else if ((v.getX() + v.getWidth()) > (layout.getWidth())) {
                        v.setX((layout.getWidth() - v.getWidth() - 5));
                    }
                    if (v.getY() < 0) {
                        v.setY(5);
                    } else if ((v.getY() + v.getHeight()) > (layout.getHeight())) {
                        v.setY((layout.getHeight() - v.getHeight() - 5));
                    }
                }
                break;

            case MotionEvent.ACTION_UP:
                //SET THE POSITION FINALLY ON ACTION UP, OTHERWISE, IT WILL NOT WORK!!!!!!

                Log.i("up",click+" "+fixedFabClick);

                if (v.getX() < 0) {
                    v.setX(5);
                } else if ((v.getX() + v.getWidth()) > (layout.getWidth())) {
                    v.setX((layout.getWidth() - v.getWidth() - 5));
                }
                if (v.getY() < 0) {
                    v.setY(5);
                } else if ((v.getY() + v.getHeight()) > (layout.getHeight())) {
                    v.setY((layout.getHeight() - v.getHeight() - 5));
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

        emptyImg = null;
        emptyTv = null;

        super.onDestroy();
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

        if (MainActivity.db.getWholeData().isEmpty()) {
            emptyTv.setVisibility(View.VISIBLE);
            emptyImg.setVisibility(View.VISIBLE);
        } else {
            emptyTv.setVisibility(View.INVISIBLE);
            emptyImg.setVisibility(View.INVISIBLE);
        }

        if (!MainActivity.add) {
            transFab.hide();
        } else {
            transFab.show();
        }

        if (MainActivity.mode.equals("f")) {
            fab.setBackgroundColor(getResources().getColor(R.color.red_for_pink));
            fab.setBackgroundTintList(getResources().getColorStateList(R.color.red_for_pink));
            fab.setImageResource(R.drawable.ic_add_black_24dp);
            transFab.setBackgroundColor(getResources().getColor(R.color.red_for_pink));
            transFab.setBackgroundTintList(getResources().getColorStateList(R.color.red_for_pink));
            transFab.setImageResource(R.drawable.ic_alarm_add_yellow_24dp);
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                mAdView.setBackgroundColor(getResources().getColor(R.color.red_for_pink));
            }
        } else if (MainActivity.mode.equals("o")) {
            fab.setBackgroundColor(getResources().getColor(R.color.navy));
            fab.setBackgroundTintList(getResources().getColorStateList(R.color.navy));
            fab.setImageResource(R.drawable.ic_add_grey_24dp);
            transFab.setBackgroundColor(getResources().getColor(R.color.navy));
            transFab.setBackgroundTintList(getResources().getColorStateList(R.color.navy));
            transFab.setImageResource(R.drawable.ic_alarm_add_grey_24dp);
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                mAdView.setBackgroundColor(getResources().getColor(R.color.navy));
            }
        }

        fixedFabClick=0;
        click=0;
    }
}
