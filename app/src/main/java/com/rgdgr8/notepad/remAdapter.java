package com.rgdgr8.notepad;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.SearchManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;
import android.util.TypedValue;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class remAdapter extends RecyclerView.Adapter<remAdapter.remHolder> implements Filterable {

    private Context ctx;

    private static final String TAG="remAdapter";

    private Typeface typeface;
    String dead = "*(DEAD)* ";
    static public ActionMode actionMode = null;
    static public ActionMode mode2=null;

    private ArrayList<Integer> multiselect = new ArrayList<>();

    private boolean longClick = true;

    static ArrayList<Integer> clickPurposeFav = new ArrayList<>();

    public remAdapter(Context ct) {
        ctx = ct;
    }

    @NonNull
    @Override
    public remHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(ctx);
        View v = layoutInflater.inflate(R.layout.row, parent, false);
        return new remHolder(v);
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

    private void fragViews() {
        if (Reminder.emptyImgRem != null && Reminder.emptyTvRem != null) {
            if (MainActivity.reminders.isEmpty() || reminderCheck() == MainActivity.reminders.size()) {
                Reminder.emptyTvRem.setVisibility(View.VISIBLE);
                Reminder.emptyImgRem.setVisibility(View.VISIBLE);
            } else {
                Reminder.emptyTvRem.setVisibility(View.INVISIBLE);
                Reminder.emptyImgRem.setVisibility(View.INVISIBLE);
            }
        }
    }

    @Override
    public void onBindViewHolder(@NonNull remHolder holder, int position) {

        if (MainActivity.reminders.equals(MainActivity.remDb.getWholeFav())){
            Log.i(TAG, "onBindViewHolder: "+true);
        }else {
            Log.i(TAG, "onBindViewHolder: "+false);
        }
        for(int i=0;i<MainActivity.reminders.size();i++){
            Log.i(TAG, i+" "+MainActivity.reminders.get(i));
        }

        try {

            if (MainActivity.reminders.size() > position) {

                fragViews();
                String text = "";

                if (!MainActivity.reminders.get(position).equals("")) {

                    holder.layout.setVisibility(View.VISIBLE);
                    holder.layout.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

                    switch (MainActivity.font) {

                        case "0":
                            typeface = ResourcesCompat.getFont(ctx, R.font.annie_use_your_telescope);
                            holder.itembutton.setTypeface(typeface);
                            break;
                        case "1":
                            typeface = ResourcesCompat.getFont(ctx, R.font.abeezee);
                            holder.itembutton.setTypeface(typeface);
                            break;
                        case "2":
                            typeface = ResourcesCompat.getFont(ctx, R.font.amatic_sc);
                            holder.itembutton.setTypeface(typeface);
                            break;
                        case "3":
                            typeface = ResourcesCompat.getFont(ctx, R.font.amita);
                            holder.itembutton.setTypeface(typeface);
                            break;
                        case "4":
                            typeface = ResourcesCompat.getFont(ctx, R.font.bad_script);
                            holder.itembutton.setTypeface(typeface);
                            break;
                        case "5":
                            typeface = ResourcesCompat.getFont(ctx, R.font.butterfly_kids);
                            holder.itembutton.setTypeface(typeface);
                            break;
                        case "6":
                            typeface = ResourcesCompat.getFont(ctx, R.font.caesar_dressing);
                            holder.itembutton.setTypeface(typeface);
                            break;
                        case "7":
                            typeface = ResourcesCompat.getFont(ctx, R.font.alegreya_sc_italic);
                            holder.itembutton.setTypeface(typeface);
                            break;
                    }

                    switch (MainActivity.size) {
                        case "s":
                            holder.itembutton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f);
                            break;
                        case "m":
                            holder.itembutton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f);
                            break;
                        case "l":
                            holder.itembutton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f);
                            break;
                        case "x":
                            holder.itembutton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f);
                            break;

                    }

                    typeface = ResourcesCompat.getFont(ctx, R.font.press_start_2p);
                    holder.tv.setText("L");
                    holder.tv.setTypeface(typeface);

                    if (clickPurposeFav.isEmpty()) {
                        if (MainActivity.nums.get(position) == 0) {
                            text = MainActivity.reminders.get(position) + "\n\n\n⏰ " + MainActivity.timeList.get(position) + "   🔁 Once" + "\n🗓 " + MainActivity.dateList.get(position);
                        } else {
                            text = MainActivity.reminders.get(position) + "\n\n\n⏰ " + MainActivity.timeList.get(position) + "   🔁 Every " + MainActivity.nums.get(position) + " " + MainActivity.periods.get(position) + "\n🗓 " + MainActivity.dateList.get(position);
                        }
                        holder.itembutton.setTag(position);
                        holder.imageButton.setTag(position);
                        holder.layout.setTag(position);
                        holder.tv.setTag(position);

                    } else {
                        if (clickPurposeFav.size() > position) {
                            if (MainActivity.nums.get(position) == 0) {
                                text = MainActivity.reminders.get(position) + "\n\n\n⏰ " + MainActivity.timeList.get(clickPurposeFav.get(position)) + "   🔁 Once" + "\n🗓 " + MainActivity.dateList.get(clickPurposeFav.get(position));
                            } else {
                                text = MainActivity.reminders.get(position) + "\n\n\n⏰ " + MainActivity.timeList.get(clickPurposeFav.get(position)) + "   🔁 Every " + MainActivity.nums.get(clickPurposeFav.get(position)) + " " + MainActivity.periods.get(clickPurposeFav.get(position)) + "\n🗓 " + MainActivity.dateList.get(clickPurposeFav.get(position));
                            }
                            holder.itembutton.setTag(clickPurposeFav.get(position));
                            holder.imageButton.setTag(clickPurposeFav.get(position));
                            holder.layout.setTag(clickPurposeFav.get(position));
                            holder.tv.setTag(clickPurposeFav.get(position));
                        }
                    }

                    holder.itembutton.setText(text);

                    if (MainActivity.reminders.get(position).startsWith(dead)) {
                        if (MainActivity.mode.equals("o")) {
                            holder.layout.setBackgroundColor(ctx.getResources().getColor(R.color.light_grey_i));
                        } else if (MainActivity.mode.equals("f")) {
                            holder.layout.setBackgroundColor(ctx.getResources().getColor(R.color.pink));
                        }
                        holder.itembutton.setTextColor(ctx.getResources().getColor(android.R.color.darker_gray));
                        holder.tv.setTextColor(ctx.getResources().getColor(android.R.color.darker_gray));
                        holder.tv.setText("D");
                    } else {
                        if (MainActivity.mode.equals("o")) {
                            holder.layout.setBackgroundColor(ctx.getResources().getColor(R.color.light_grey_ii));
                        } else if (MainActivity.mode.equals("f")) {
                            holder.layout.setBackgroundColor(ctx.getResources().getColor(R.color.myTheme));
                        }
                        holder.itembutton.setTextColor(Color.BLACK);
                        holder.tv.setTextColor(Color.BLACK);
                        holder.tv.setText("L");
                    }

                    if (clickPurposeFav.size() <= position) {
                        if (multiselect.contains(position)) {
                            holder.layout.setBackgroundColor(ctx.getResources().getColor(R.color.myBlue));
                        } else {
                            if (MainActivity.mode.equals("o")) {
                                holder.layout.setBackgroundColor(ctx.getResources().getColor(R.color.light_grey_ii));
                            } else if (MainActivity.mode.equals("f")) {
                                holder.layout.setBackgroundColor(ctx.getResources().getColor(R.color.myTheme));
                            }
                        }
                    } else {
                        if (multiselect.contains(clickPurposeFav.get(position))) {
                            holder.layout.setBackgroundColor(ctx.getResources().getColor(R.color.myBlue));
                        } else {
                            if (MainActivity.mode.equals("o")) {
                                holder.layout.setBackgroundColor(ctx.getResources().getColor(R.color.light_grey_ii));
                            } else if (MainActivity.mode.equals("f")) {
                                holder.layout.setBackgroundColor(ctx.getResources().getColor(R.color.myTheme));
                            }
                        }
                    }

                } else {
                    holder.layout.setVisibility(View.GONE);
                    holder.layout.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
                }

                //TODO:remove empty check from here if present!

            } else {
                Toast.makeText(ctx, "RemAdapter INDEX OUT OF BOUND ON BINDER", Toast.LENGTH_LONG).show();
                throw new RuntimeException();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return MainActivity.reminders.size();
    }

    @Override
    public Filter getFilter() {
        return myFilter;
    }

    private Filter myFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            ArrayList<Integer> filterList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                for (int i = 0; i < MainActivity.remDb.getWholeFav().size(); i++) {
                    filterList.add(i);
                }
            } else {
                String toFilter = constraint.toString().toLowerCase().trim();
                for (int i = 0; i < MainActivity.remDb.getWholeFav().size(); i++) {
                    if (MainActivity.remDb.getWholeFav().get(i).toLowerCase().contains(toFilter) || MainActivity.remDb.getWholeDate().get(i).toLowerCase().contains(toFilter)
                            || MainActivity.remDb.getWholeTime().get(i).contains(toFilter)) {
                        filterList.add(i);
                    }
                }
            }

            clickPurposeFav = filterList;
            MainActivity.reminders.clear();

            for (int i = 0; i < clickPurposeFav.size(); i++) {
                MainActivity.reminders.add(MainActivity.remDb.getWholeFav().get(clickPurposeFav.get(i)));
            }

            FilterResults filterResults = new FilterResults();
            filterResults.values = filterList;

            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {

            if (Reminder.emptyTvRem != null && Reminder.emptyImgRem != null) {
                if (MainActivity.reminders.isEmpty() || reminderCheck() == MainActivity.reminders.size()) {
                    Reminder.emptyTvRem.setVisibility(View.VISIBLE);
                    Reminder.emptyImgRem.setVisibility(View.VISIBLE);
                }
            }

            notifyDataSetChanged();
        }
    };

    public class remHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        Button itembutton;
        TextView tv;
        ImageButton imageButton;
        ConstraintLayout layout;
        ImageView noClock;

        AlertDialog.Builder alertDialog;

        public remHolder(@NonNull View itemView) {
            super(itemView);

            itembutton = itemView.findViewById(R.id.button);
            tv = itemView.findViewById(R.id.textView);
            noClock = itemView.findViewById(R.id.clock);
            imageButton = itemView.findViewById(R.id.imageButton);
            layout = itemView.findViewById(R.id.layout);
            layout.setOnClickListener(this);
            tv.setOnClickListener(this);
            noClock.setVisibility(View.INVISIBLE);
            itembutton.setOnLongClickListener(this);
            itembutton.setOnClickListener(this);
            imageButton.setOnClickListener(this);
        }

        private void cancelAlarm(int _id) {
            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(ctx);
            Log.i("cancelAlarmAdapter","id: "+ _id);

            AlarmManager alarmManager = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
            Intent myIntent = new Intent(ctx, AlertReceiver.class);
            myIntent.setPackage(ctx.getPackageName());
            PendingIntent pendingIntent = PendingIntent.getBroadcast(ctx, _id, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            assert alarmManager != null;
            alarmManager.cancel(pendingIntent);
            notificationManagerCompat.cancel(_id);

            Intent myIntent2 = new Intent(ctx, AlertReceiver2.class);
            myIntent2.setPackage(ctx.getPackageName());
            int posi = _id + 90000;
            PendingIntent pendingIntent2 = PendingIntent.getBroadcast(ctx, posi, myIntent2, PendingIntent.FLAG_UPDATE_CURRENT);
            alarmManager.cancel(pendingIntent2);
            notificationManagerCompat.cancel(posi);

            int last = ctx.getSharedPreferences("rgdgr8", Context.MODE_PRIVATE).getInt("last" + posi, 0);
            ctx.getSharedPreferences("rgdgr8", Context.MODE_PRIVATE).edit().putInt("last" + posi, 0).apply();
            ctx.getSharedPreferences("rgdgr8", Context.MODE_PRIVATE).edit().putInt("r" + _id, 0).apply();
        }

        @Override
        public void onClick(View v) {
            if (longClick) {

                if (v.getId() == R.id.button) {

                    Button b = (Button) v;

                    Intent intentRemi = new Intent(ctx, ReminderActivity.class);
                    intentRemi.putExtra("id", Integer.parseInt(b.getTag().toString()) + 1);

                    ctx.startActivity(intentRemi);

                } else if (v.getId() == R.id.imageButton) {

                    final ImageButton b = (ImageButton) v;
                    PopupMenu popupMenu = new PopupMenu(ctx, v);
                    popupMenu.inflate(R.menu.rem_menu);
                    popupMenu.show();
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {

                                case R.id.copy:

                                    ClipboardManager clipboardManager = (ClipboardManager) ctx.getSystemService(Context.CLIPBOARD_SERVICE);
                                    ClipData clipData = ClipData.newPlainText("text", MainActivity.remDb.getWholeFav().get(Integer.parseInt(b.getTag().toString())));
                                    assert clipboardManager != null;
                                    clipboardManager.setPrimaryClip(clipData);
                                    Toast.makeText(ctx, "Copied to Clipboard!", Toast.LENGTH_SHORT).show();

                                    return true;

                                case R.id.remRem:

                                    if (MainActivity.mode.equals("f")) {
                                        switch (MainActivity.font) {

                                            case "0":
                                                alertDialog = new AlertDialog.Builder(ctx, R.style.myDialog0);
                                                break;
                                            case "1":
                                                alertDialog = new AlertDialog.Builder(ctx, R.style.myDialog1);
                                                break;
                                            case "2":
                                                alertDialog = new AlertDialog.Builder(ctx, R.style.myDialog2);
                                                break;
                                            case "3":
                                                alertDialog = new AlertDialog.Builder(ctx, R.style.myDialog3);
                                                break;
                                            case "4":
                                                alertDialog = new AlertDialog.Builder(ctx, R.style.myDialog4);
                                                break;
                                            case "5":
                                                alertDialog = new AlertDialog.Builder(ctx, R.style.myDialog5);
                                                break;
                                            case "6":
                                                alertDialog = new AlertDialog.Builder(ctx, R.style.myDialog6);
                                                break;
                                            case "7":
                                                alertDialog = new AlertDialog.Builder(ctx, R.style.myDialog7);
                                                break;
                                        }
                                    } else if (MainActivity.mode.equals("o")) {
                                        switch (MainActivity.font) {

                                            case "0":
                                                alertDialog = new AlertDialog.Builder(ctx, R.style.myDialog02);
                                                break;
                                            case "1":
                                                alertDialog = new AlertDialog.Builder(ctx, R.style.myDialog12);
                                                break;
                                            case "2":
                                                alertDialog = new AlertDialog.Builder(ctx, R.style.myDialog22);
                                                break;
                                            case "3":
                                                alertDialog = new AlertDialog.Builder(ctx, R.style.myDialog32);
                                                break;
                                            case "4":
                                                alertDialog = new AlertDialog.Builder(ctx, R.style.myDialog42);
                                                break;
                                            case "5":
                                                alertDialog = new AlertDialog.Builder(ctx, R.style.myDialog52);
                                                break;
                                            case "6":
                                                alertDialog = new AlertDialog.Builder(ctx, R.style.myDialog62);
                                                break;
                                            case "7":
                                                alertDialog = new AlertDialog.Builder(ctx, R.style.myDialog72);
                                                break;
                                        }
                                    }

                                    try {
                                        alertDialog.setTitle("Remove item?")
                                                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {

                                                        if (!MainActivity.reminders.equals(MainActivity.remDb.getWholeFav())) {
                                                            MainActivity.reminders.clear();
                                                            MainActivity.reminders.addAll(MainActivity.remDb.getWholeFav());
                                                        }

                                                        int i = Integer.parseInt(b.getTag().toString());

                                                        MainActivity.reminders.set(i, "");
                                                        MainActivity.dateList.set(i, "");
                                                        MainActivity.timeList.set(i, "");
                                                        MainActivity.year.set(i, 0);
                                                        MainActivity.month.set(i, 0);
                                                        MainActivity.day.set(i, 0);
                                                        MainActivity.hour.set(i, 0);
                                                        MainActivity.min.set(i, 0);
                                                        MainActivity.nums.set(i, 0);
                                                        MainActivity.periods.set(i, "");
                                                        cancelAlarm(i + 1);

                                                        MainActivity.remDb.UpdateFav();

                                                        if (MainActivity.searchView != null && !MainActivity.searchView.isIconified() && !MainActivity.searchText.equals("")) {
                                                            getFilter().filter(MainActivity.searchText);
                                                        }

                                                        if (MainActivity.pop) {
                                                            MediaPlayer mediaPlayer = MediaPlayer.create(ctx, R.raw.pop);
                                                            mediaPlayer.start();
                                                        }

                                                        Toast.makeText(ctx, " Removed!", Toast.LENGTH_SHORT).show();

                                                        fragViews();
                                                        notifyDataSetChanged();
                                                    }
                                                })
                                                .setNegativeButton("NO", null);

                                        AlertDialog dialog = alertDialog.create();
                                        dialog.show();

                                        Button button1 = dialog.findViewById(android.R.id.button1);
                                        Button button2 = dialog.findViewById(android.R.id.button2);
                                        assert button1 != null;
                                        button1.setTextColor(ctx.getResources().getColor(R.color.colorPrimary));
                                        assert button2 != null;
                                        button2.setTextColor(ctx.getResources().getColor(R.color.colorPrimary));
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        Toast.makeText(ctx, "Something went wrong!!! Please reopen app and try again.", Toast.LENGTH_SHORT).show();
                                    }
                                    return true;

                                case R.id.searchRem:

                                    Intent i = new Intent(Intent.ACTION_WEB_SEARCH);
                                    i.putExtra(SearchManager.QUERY, MainActivity.remDb.getWholeFav().get(Integer.parseInt(b.getTag().toString())));
                                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    ctx.startActivity(i);

                                    return true;

                                case R.id.callRem:

                                    Uri call = Uri.parse("tel:" + MainActivity.remDb.getWholeFav().get(Integer.parseInt(b.getTag().toString())));
                                    Intent surf = new Intent(Intent.ACTION_DIAL, call);
                                    surf.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    ctx.startActivity(surf);

                                    return true;

                                case R.id.shareRem:

                                    Intent intent1 = new Intent(Intent.ACTION_SEND);
                                    intent1.putExtra(Intent.EXTRA_TEXT, MainActivity.remDb.getWholeFav().get(Integer.parseInt(b.getTag().toString())));
                                    intent1.setType("text/*");
                                    ctx.startActivity(Intent.createChooser(intent1, "Share Via"));

                                    return true;

                                default:

                                    return false;
                            }
                        }
                    });
                }
            } else {

                if (multiselect.contains(Integer.parseInt(v.getTag().toString()))) {
                    multiselect.remove(multiselect.indexOf(Integer.parseInt(v.getTag().toString())));
                } else {
                    multiselect.add(Integer.parseInt(v.getTag().toString()));
                }

                refresh();
            }
        }

        @Override
        public boolean onLongClick(View v) {

            if (longClick) {

                multiselect.add(Integer.parseInt(v.getTag().toString()));
                longClick = false;

                if (actionMode == null) {

                    actionMode = ((AppCompatActivity) ctx).startActionMode(actionModeCallback);// startActionMode instead of startsupportActionMode!!!!!!!!!!!!!!!!!!

                } else {
                    return false;
                }

            }

            return true;
        }

        private void refresh() {             //declare longClick boolean inside Adapter class and not Holder class!!!!!!!!!
            notifyDataSetChanged();
            longClick = false;
        }

        boolean del = false;

        private ActionMode.Callback actionModeCallback = new ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {

                refresh();
                mode.getMenuInflater().inflate(R.menu.rem_menu2, menu);
                mode.setTitle("Choose an action");

                MenuItem done = menu.findItem(R.id.doneRem);

                if (MainActivity.searchView != null && !MainActivity.searchView.isIconified() && !MainActivity.searchText.equals("")) {
                    done.setVisible(false);
                } else {
                    done.setVisible(true);
                }

                MainActivity.myTab.setVisibility(View.GONE);
                mode2=mode;

                return true;

            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(final ActionMode mode, MenuItem item) {

                switch (item.getItemId()) {

                    case R.id.remRemMul:

                        if (!multiselect.isEmpty()) {

                            if (MainActivity.mode.equals("f")) {
                                switch (MainActivity.font) {

                                    case "0":
                                        alertDialog = new AlertDialog.Builder(ctx, R.style.myDialog0);
                                        break;
                                    case "1":
                                        alertDialog = new AlertDialog.Builder(ctx, R.style.myDialog1);
                                        break;
                                    case "2":
                                        alertDialog = new AlertDialog.Builder(ctx, R.style.myDialog2);
                                        break;
                                    case "3":
                                        alertDialog = new AlertDialog.Builder(ctx, R.style.myDialog3);
                                        break;
                                    case "4":
                                        alertDialog = new AlertDialog.Builder(ctx, R.style.myDialog4);
                                        break;
                                    case "5":
                                        alertDialog = new AlertDialog.Builder(ctx, R.style.myDialog5);
                                        break;
                                    case "6":
                                        alertDialog = new AlertDialog.Builder(ctx, R.style.myDialog6);
                                        break;
                                    case "7":
                                        alertDialog = new AlertDialog.Builder(ctx, R.style.myDialog7);
                                        break;
                                }
                            } else if (MainActivity.mode.equals("o")) {
                                switch (MainActivity.font) {

                                    case "0":
                                        alertDialog = new AlertDialog.Builder(ctx, R.style.myDialog02);
                                        break;
                                    case "1":
                                        alertDialog = new AlertDialog.Builder(ctx, R.style.myDialog12);
                                        break;
                                    case "2":
                                        alertDialog = new AlertDialog.Builder(ctx, R.style.myDialog22);
                                        break;
                                    case "3":
                                        alertDialog = new AlertDialog.Builder(ctx, R.style.myDialog32);
                                        break;
                                    case "4":
                                        alertDialog = new AlertDialog.Builder(ctx, R.style.myDialog42);
                                        break;
                                    case "5":
                                        alertDialog = new AlertDialog.Builder(ctx, R.style.myDialog52);
                                        break;
                                    case "6":
                                        alertDialog = new AlertDialog.Builder(ctx, R.style.myDialog62);
                                        break;
                                    case "7":
                                        alertDialog = new AlertDialog.Builder(ctx, R.style.myDialog72);
                                        break;
                                }
                            }

                            try {
                                alertDialog.setTitle("Remove item?")
                                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                                if (!MainActivity.reminders.equals(MainActivity.remDb.getWholeFav())) {
                                                    MainActivity.reminders.clear();
                                                    MainActivity.reminders.addAll(MainActivity.remDb.getWholeFav());
                                                }

                                                for (int i = 0; i < multiselect.size(); i++) {

                                                    int j = multiselect.get(i);

                                                    MainActivity.reminders.set(j, "");
                                                    MainActivity.dateList.set(j, "");
                                                    MainActivity.timeList.set(j, "");
                                                    MainActivity.year.set(j, 0);
                                                    MainActivity.month.set(j, 0);
                                                    MainActivity.day.set(j, 0);
                                                    MainActivity.hour.set(j, 0);
                                                    MainActivity.min.set(j, 0);
                                                    MainActivity.nums.set(j, 0);
                                                    MainActivity.periods.set(j, "");

                                                    cancelAlarm(j + 1);
                                                }

                                                MainActivity.remDb.UpdateFav();

                                                if (MainActivity.swoosh) {
                                                    MediaPlayer mediaPlayer = MediaPlayer.create(ctx, R.raw.swoosh);
                                                    mediaPlayer.start();
                                                }

                                                Toast.makeText(ctx, " Removed!", Toast.LENGTH_SHORT).show();

                                                if (MainActivity.searchView != null && !MainActivity.searchView.isIconified() && !MainActivity.searchText.equals("")) {
                                                    del = true;
                                                    getFilter().filter(MainActivity.searchText);
                                                }

                                                fragViews();
                                                mode.finish();
                                            }
                                        })
                                        .setNegativeButton("NO", null);

                                AlertDialog dialog = alertDialog.create();
                                dialog.show();

                                Button button1 = dialog.findViewById(android.R.id.button1);
                                Button button2 = dialog.findViewById(android.R.id.button2);
                                assert button1 != null;
                                button1.setTextColor(ctx.getResources().getColor(R.color.colorPrimary));
                                assert button2 != null;
                                button2.setTextColor(ctx.getResources().getColor(R.color.colorPrimary));

                            } catch (Exception e) {
                                e.printStackTrace();
                                Toast.makeText(ctx, "Something went wrong!!! Please reopen app and try again.", Toast.LENGTH_SHORT).show();
                            }

                        } else {
                            Toast.makeText(ctx, "Nothing Selected!", Toast.LENGTH_SHORT).show();
                        }

                        return true;

                    case R.id.doneRem:
                        multiselect.clear();
                        for (int j = 0; j < MainActivity.reminders.size(); j++) {
                            multiselect.add(j);
                        }
                        refresh();
                        return true;

                    case R.id.undoneRem:
                        multiselect.clear();
                        refresh();
                        return true;

                    case R.id.shareMulRem:

                        if (!multiselect.isEmpty()) {
                            String contentShare = MainActivity.remDb.getWholeFav().get(multiselect.get(0));

                            if (multiselect.size() > 1) {
                                for (int i = 1; i < multiselect.size(); i++) {
                                    contentShare = contentShare + "\r\n" + MainActivity.remDb.getWholeFav().get(multiselect.get(i));
                                }
                            }
                            Intent intent1 = new Intent(Intent.ACTION_SEND);
                            intent1.putExtra(Intent.EXTRA_TEXT, contentShare);
                            intent1.setType("text/*");
                            ctx.startActivity(Intent.createChooser(intent1, "Share Via"));
                            mode.finish();
                        } else {
                            Toast.makeText(ctx, "Nothing Selected!", Toast.LENGTH_SHORT).show();
                        }

                        return true;

                    case R.id.copyMulRem:

                        if (!multiselect.isEmpty()) {

                            String contentCopy = MainActivity.remDb.getWholeFav().get(multiselect.get(0));

                            if (multiselect.size() > 1) {
                                for (int i = 1; i < multiselect.size(); i++) {
                                    contentCopy = contentCopy + "\r\n" + MainActivity.remDb.getWholeFav().get(multiselect.get(i));
                                }
                            }

                            ClipboardManager clipboardManager = (ClipboardManager) ctx.getSystemService(Context.CLIPBOARD_SERVICE);
                            ClipData clipData = ClipData.newPlainText("text", contentCopy);
                            assert clipboardManager != null;
                            clipboardManager.setPrimaryClip(clipData);
                            Toast.makeText(ctx, "Copied to Clipboard!", Toast.LENGTH_SHORT).show();

                            mode.finish();

                        } else {
                            Toast.makeText(ctx, "Nothing Selected!", Toast.LENGTH_SHORT).show();
                        }
                        return true;

                    default:
                        return false;
                }
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {

                multiselect.clear();
                if (!del) {
                    refresh();
                }
                actionMode = null;
                mode2=null;
                MainActivity.myTab.setVisibility(View.VISIBLE);
                longClick = true;
                del = false;
            }
        };
    }
}
