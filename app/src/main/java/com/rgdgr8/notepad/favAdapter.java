package com.rgdgr8.notepad;

import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class favAdapter extends RecyclerView.Adapter<favAdapter.favHolder> implements Filterable {

    private Context ctx;

    private Typeface typeface;

    static public ActionMode mode1=null;
    static public ActionMode actionMode = null;

    private ArrayList<String> multiselect = new ArrayList<>();

    private boolean longClick = true;

    static ArrayList<String> clickPurposeFav = new ArrayList<>();

    public favAdapter(Context ct) {
        ctx = ct;
    }

    @NonNull
    @Override
    public favAdapter.favHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(ctx);
        View v = layoutInflater.inflate(R.layout.row, parent, false);
        return new favHolder(v);
    }

    private ArrayList<String> duplicateRemove() {
        ArrayList<String> dup = new ArrayList<>();

        for (String s : MainActivity.reminders) {
            if (!dup.contains(s)) {
                dup.add(s);
            }
        }

        return dup;
    }

    private void frag() {
        if (Favourites.emptyImgFav != null && Favourites.emptyTvFav != null) {
            if (MainActivity.favs.isEmpty()) {
                Favourites.emptyImgFav.setVisibility(View.VISIBLE);
                Favourites.emptyTvFav.setVisibility(View.VISIBLE);
            } else {
                Favourites.emptyImgFav.setVisibility(View.INVISIBLE);
                Favourites.emptyTvFav.setVisibility(View.INVISIBLE);
            }
        }
    }

    @Override
    public void onBindViewHolder(@NonNull favAdapter.favHolder holder, int position) {

        frag();

        if (multiselect.contains(MainActivity.favs.get(position))) {
            holder.layout.setBackgroundColor(ctx.getResources().getColor(R.color.myBlue));
        } else {
            if (MainActivity.mode.equals("o")) {
                holder.layout.setBackgroundColor(ctx.getResources().getColor(R.color.light_grey_ii));
            } else if (MainActivity.mode.equals("f")) {
                holder.layout.setBackgroundColor(ctx.getResources().getColor(R.color.myTheme));
            }
        }

        if (duplicateRemove().contains(MainActivity.favs.get(position))) {
            holder.clock.setVisibility(View.VISIBLE);
            holder.tv.setVisibility(View.INVISIBLE);
        } else {
            holder.clock.setVisibility(View.INVISIBLE);
            holder.tv.setVisibility(View.VISIBLE);
        }

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

        holder.itembutton.setText(MainActivity.favs.get(position));
        holder.imageButton.setTag(MainActivity.favs.get(position));
        holder.layout.setTag(MainActivity.favs.get(position));
        holder.clock.setTag(MainActivity.favs.get(position));
        holder.tv.setTag(MainActivity.favs.get(position));
        holder.tv.setText(String.valueOf(position + 1));
    }

    @Override
    public int getItemCount() {
        return MainActivity.favs.size();
    }

    @Override
    public Filter getFilter() {
        return myFilter;
    }

    private Filter myFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            ArrayList<String> filterList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filterList.addAll(MainActivity.favDb.getWholeFav());
            } else {
                String toFilter = constraint.toString().toLowerCase().trim();

                for (String item : MainActivity.favDb.getWholeFav()) {
                    if (item.toLowerCase().contains(toFilter)) {
                        filterList.add(item);
                    }
                }
            }

            clickPurposeFav = filterList;
            FilterResults filterResults = new FilterResults();
            filterResults.values = filterList;

            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {

            MainActivity.favs.clear();
            MainActivity.favs.addAll((List) results.values);

            if (Favourites.emptyTvFav != null && Favourites.emptyImgFav != null) {
                if (MainActivity.favs.isEmpty()) {
                    Favourites.emptyImgFav.setVisibility(View.VISIBLE);
                    Favourites.emptyTvFav.setVisibility(View.VISIBLE);
                }
            }

            notifyDataSetChanged();
        }
    };

    public class favHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        Button itembutton;
        TextView tv;
        ImageButton imageButton;
        ConstraintLayout layout;
        ImageView clock;

        AlertDialog.Builder alertDialog;

        public favHolder(@NonNull View itemView) {
            super(itemView);

            itembutton = itemView.findViewById(R.id.button);
            clock = itemView.findViewById(R.id.clock);
            tv = itemView.findViewById(R.id.textView);
            imageButton = itemView.findViewById(R.id.imageButton);
            layout = itemView.findViewById(R.id.layout);
            layout.setOnClickListener(this);
            tv.setOnClickListener(this);
            clock.setOnClickListener(this);
            itembutton.setOnLongClickListener(this);
            itembutton.setOnClickListener(this);
            imageButton.setOnClickListener(this);
        }

        @SuppressLint("RestrictedApi")

        @Override
        public void onClick(View v) {
            if (longClick) {

                final ClipboardManager clipboardManager = (ClipboardManager) ctx.getSystemService(Context.CLIPBOARD_SERVICE);

                if (v.getId() == R.id.button) {
                    Button b = (Button) v;

                    if (MainActivity.clickedd.equals("e")) {
                        Intent intent = new Intent(ctx, EditActivity.class);
                        intent.putExtra("textPresent", b.getText().toString());
                        intent.putExtra("mORf", 2);
                        ctx.startActivity(intent);
                    }else if (MainActivity.clickedd.equals("c")){
                        ClipData clipData = ClipData.newPlainText("text", b.getText().toString());
                        assert clipboardManager != null;
                        clipboardManager.setPrimaryClip(clipData);
                        Toast.makeText(ctx, "Copied to Clipboard!", Toast.LENGTH_SHORT).show();
                    }

                } else if (v.getId() == R.id.clock) {

                    Objects.requireNonNull(MainActivity.myTab.getTabAt(2)).select();
                    MainActivity.searchView.setIconified(false);
                    MainActivity.searchText=v.getTag().toString();
                    MainActivity.searchView.setQuery(MainActivity.searchText, false);
                    MainActivity.searchView.clearFocus();
                    MainActivity.rA.getFilter().filter(v.getTag().toString());

                } else if (v.getId() == R.id.imageButton) {

                    final ImageButton b = (ImageButton) v;

                    PopupMenu popupMenu = new PopupMenu(ctx, v);
                    popupMenu.inflate(R.menu.fav_menu);

                    Menu menu=popupMenu.getMenu();

                    if (MainActivity.clickedd.equals("e")){
                        menu.getItem(0).setTitle("Copy");
                    }else if (MainActivity.clickedd.equals("c")){
                        menu.getItem(0).setTitle("Edit");
                    }

                    popupMenu.show();
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {

                            switch (item.getItemId()) {

                                case R.id.remiFav:

                                    Intent intentRemi = new Intent(ctx, ReminderActivity.class);
                                    intentRemi.putExtra("content", b.getTag().toString());
                                    ctx.startActivity(intentRemi);

                                    return true;

                                case R.id.transFav:

                                    Intent transIntent = new Intent(ctx, Translator.class);
                                    transIntent.putExtra("content", b.getTag().toString());
                                    transIntent.putExtra("mf", 1);
                                    ctx.startActivity(transIntent);

                                    return true;

                                case R.id.favRem:

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

                                                        if (!MainActivity.favs.equals(MainActivity.favDb.getWholeFav())) {
                                                            MainActivity.favs.clear();
                                                            MainActivity.favs.addAll(MainActivity.favDb.getWholeFav());
                                                        }

                                                        if (MainActivity.orderReverser % 2 != 0) {
                                                            Collections.reverse(MainActivity.favs);
                                                        }

                                                        MainActivity.favs.remove(b.getTag().toString());

                                                        if (MainActivity.pop) {
                                                            MediaPlayer mediaPlayer = MediaPlayer.create(ctx, R.raw.pop);
                                                            mediaPlayer.start();
                                                        }

                                                        if (MainActivity.orderReverser % 2 != 0) {
                                                            Collections.reverse(MainActivity.favs);
                                                        }

                                                        MainActivity.favDb.UpdateFav();

                                                        notifyItemRemoved(getAdapterPosition());
                                                        notifyItemRangeChanged(getAdapterPosition(), MainActivity.favs.size());
                                                        Toast.makeText(ctx, " Removed!", Toast.LENGTH_SHORT).show();

                                                        if (MainActivity.searchView!=null && !MainActivity.searchView.isIconified() && !MainActivity.searchText.equals("")) {
                                                            clickPurposeFav.remove(b.getTag().toString());
                                                            MainActivity.favs.clear();
                                                            MainActivity.favs.addAll(clickPurposeFav);
                                                            notifyDataSetChanged();
                                                        }

                                                        frag();
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

                                case R.id.editFav: //copying

                                    if (MainActivity.clickedd.equals("e")) {
                                        ClipData clipData = ClipData.newPlainText("text", b.getTag().toString());
                                        assert clipboardManager != null;
                                        clipboardManager.setPrimaryClip(clipData);
                                        Toast.makeText(ctx, "Copied to Clipboard!", Toast.LENGTH_SHORT).show();
                                    }else if (MainActivity.clickedd.equals("c")){
                                        Intent intent = new Intent(ctx, EditActivity.class);
                                        intent.putExtra("textPresent", b.getTag().toString());
                                        intent.putExtra("mORf", 2);
                                        ctx.startActivity(intent);
                                    }

                                    return true;

                                case R.id.searchFav:

                                    Intent i = new Intent(Intent.ACTION_WEB_SEARCH);
                                    i.putExtra(SearchManager.QUERY, b.getTag().toString());
                                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    ctx.startActivity(i);

                                    return true;

                                case R.id.callFav:

                                    Uri call = Uri.parse("tel:" + b.getTag().toString());
                                    Intent surf = new Intent(Intent.ACTION_DIAL, call);
                                    surf.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    ctx.startActivity(surf);

                                    return true;

                                case R.id.shareFav:

                                    Intent intent1 = new Intent(Intent.ACTION_SEND);
                                    intent1.putExtra(Intent.EXTRA_TEXT, b.getTag().toString());
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

                if (v.getId() == R.id.button) {
                    Button a = (Button) v;
                    if (multiselect.contains(a.getText().toString())) {
                        multiselect.remove(a.getText().toString());
                    } else {
                        multiselect.add(a.getText().toString());
                    }
                } else if (v.getId() == R.id.imageButton) {
                    ImageButton c = (ImageButton) v;
                    if (multiselect.contains(c.getTag().toString())) {
                        multiselect.remove(c.getTag().toString());
                    } else {
                        multiselect.add(c.getTag().toString());
                    }
                } else if (v.getId() == R.id.layout) {
                    ConstraintLayout l = (ConstraintLayout) v;
                    if (multiselect.contains(l.getTag().toString())) {
                        multiselect.remove(l.getTag().toString());
                    } else {
                        multiselect.add(l.getTag().toString());
                    }
                } else if (v.getId() == R.id.textView) {
                    TextView t = (TextView) v;
                    if (multiselect.contains(t.getTag().toString())) {
                        multiselect.remove(t.getTag().toString());
                    } else {
                        multiselect.add(t.getTag().toString());
                    }
                } else if (v.getId() == R.id.clock) {
                    ImageView cl = (ImageView) v;
                    if (multiselect.contains(cl.getTag().toString())) {
                        multiselect.remove(cl.getTag().toString());
                    } else {
                        multiselect.add(cl.getTag().toString());
                    }
                }

                refresh();
            }
        }

        Button d;

        @Override
        public boolean onLongClick(View v) {
            d = (Button) v;

            if (longClick) {

                multiselect.add(d.getText().toString());
                longClick = false;

                if (actionMode == null) {

                    actionMode = ((AppCompatActivity) ctx).startActionMode(actionModeCallback);// startActionMode instead of startsupportActionMode!!!!!!!!!!!!!!!!!!

                } else {
                    return false;
                }

            }

            return true;
        }

        public void refresh() {             //declare longClick boolean inside Adapter class and not Holder class!!!!!!!!!
            notifyDataSetChanged();
            longClick = false;
        }

        private ActionMode.Callback actionModeCallback = new ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {

                refresh();
                mode.getMenuInflater().inflate(R.menu.fav_menu2, menu);
                mode.setTitle("Choose an action");

                MainActivity.myTab.setVisibility(View.GONE);
                mode1=mode;

                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(final ActionMode mode, MenuItem item) {

                switch (item.getItemId()) {

                    case R.id.remFav:

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

                                                if (!MainActivity.favs.equals(MainActivity.favDb.getWholeFav())) {
                                                    MainActivity.favs.clear();
                                                    MainActivity.favs.addAll(MainActivity.favDb.getWholeFav());
                                                }

                                                if (MainActivity.orderReverser % 2 != 0) {
                                                    Collections.reverse(MainActivity.favs);
                                                }

                                                for (int i = 0; i < multiselect.size(); i++) {

                                                    if (MainActivity.searchView!=null && !MainActivity.searchView.isIconified() && !MainActivity.searchText.equals("")) {
                                                        clickPurposeFav.remove(multiselect.get(i));
                                                    }

                                                    MainActivity.favs.remove(multiselect.get(i));
                                                }

                                                if (MainActivity.orderReverser % 2 != 0) {
                                                    Collections.reverse(MainActivity.favs);
                                                }

                                                MainActivity.favDb.UpdateFav();

                                                if (MainActivity.swoosh) {
                                                    MediaPlayer mediaPlayer = MediaPlayer.create(ctx, R.raw.swoosh);
                                                    mediaPlayer.start();
                                                }

                                                Toast.makeText(ctx, " Removed!", Toast.LENGTH_SHORT).show();

                                                if (MainActivity.searchView!=null && !MainActivity.searchView.isIconified() && !MainActivity.searchText.equals("")) {
                                                    MainActivity.favs.clear();
                                                    MainActivity.favs.addAll(clickPurposeFav);
                                                }

                                                frag();

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

                    case R.id.doneFav:
                        multiselect.clear();
                        multiselect.addAll(MainActivity.favs);
                        refresh();
                        return true;

                    case R.id.undoneFav:
                        multiselect.clear();
                        refresh();
                        return true;

                    case R.id.shareMulFav:

                        if (!multiselect.isEmpty()) {
                            String contentShare = multiselect.get(0);

                            if (multiselect.size() > 1) {
                                for (int i = 1; i < multiselect.size(); i++) {
                                    contentShare = contentShare + "\r\n" + multiselect.get(i);
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

                    case R.id.transFavMul:

                        if (!multiselect.isEmpty()) {

                            String contentTrans = multiselect.get(0);
                            Intent transIntent = new Intent(ctx, Translator.class);
                            transIntent.putExtra("mf", 1);

                            if (multiselect.size() > 1) {
                                for (int i = 1; i < multiselect.size(); i++) {
                                    contentTrans = contentTrans + "\r\n" + multiselect.get(i);
                                }
                            }

                            transIntent.putExtra("content", contentTrans);
                            ctx.startActivity(transIntent);
                            mode.finish();

                        } else {
                            Toast.makeText(ctx, "Nothing Selected!", Toast.LENGTH_SHORT).show();
                        }

                        return true;

                    case R.id.copyMulFav:

                        if (!multiselect.isEmpty()) {

                            String contentCopy = multiselect.get(0);

                            if (multiselect.size() > 1) {
                                for (int i = 1; i < multiselect.size(); i++) {
                                    contentCopy = contentCopy + "\r\n" + multiselect.get(i);
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
                refresh();
                actionMode = null;
                mode1=null;
                MainActivity.myTab.setVisibility(View.VISIBLE);
                longClick = true;
            }
        };
    }
}
