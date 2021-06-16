package com.rgdgr8.notepad;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.preference.PreferenceManager;
import androidx.viewpager.widget.ViewPager;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Objects;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    static MyDatabase db;
    static RemDatabase remDb;
    static FavDatabase favDb;

    //String text;
    Intent intentSer;
    ClipboardManager clipboardManager;
    Toolbar toolbar;

    public static final String dead = "*(DEAD)* ";

    private DrawerLayout drawer;

    SharedPreferences sharedPreferences;

    static ArrayList<String> copies = new ArrayList<>();
    static ArrayList<String> favs = new ArrayList<>();
    static ArrayList<String> reminders = new ArrayList<>();
    static ArrayList<Integer> year = new ArrayList<>();
    static ArrayList<Integer> month = new ArrayList<>();
    static ArrayList<String> periods = new ArrayList<>();
    static ArrayList<Integer> nums = new ArrayList<>();
    static ArrayList<Integer> day = new ArrayList<>();
    static ArrayList<Integer> hour = new ArrayList<>();
    static ArrayList<Integer> min = new ArrayList<>();
    static ArrayList<String> dateList = new ArrayList<>();
    static ArrayList<String> timeList = new ArrayList<>();

    static favAdapter fA;
    static myADApter mA;
    static remAdapter rA;

    static int orderReverser = 0;
    int clickNav;

    static TabLayout myTab;
    static ViewPager myPager;

    static androidx.appcompat.widget.SearchView searchView;
    MenuItem search;
    MenuItem addTrans;
    MenuItem addnew;
    MenuItem addRem;
    MenuItem order;
    int tabKeeper = 0;
    boolean oncreate = false;
    int c = 0;

    static String font;
    static boolean add;
    static String mode;
    static boolean swoosh;
    static boolean pop;
    static String size;
    static String clickedd;
    static boolean background;

    AlertDialog.Builder alert;
    Typeface typeface;

    static String message;
    static String message2;
    String packageName;

    static String searchText = "";
    View header;

    static boolean version;

    SharedPreferences preferences;
    NavigationView navigationView;

    static InterstitialAd mInterstitialAd;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.helpmenu, menu);

        oncreate = true;

        search = menu.findItem(R.id.searcher);
        order = menu.findItem(R.id.order);
        addnew = menu.findItem(R.id.addIt);
        addTrans = menu.findItem(R.id.addMe);
        addRem = menu.findItem(R.id.addRem);

        searchView = (androidx.appcompat.widget.SearchView) search.getActionView();

        MenuItem overflow = menu.findItem(R.id.overflow);

        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);
        searchView.setQueryHint("Search Here");
        searchView.setMaxWidth(toolbar.getWidth());

        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                order.setVisible(false);
                overflow.setVisible(false);

                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);

                if (mode.equals("o")) {
                    searchView.setBackgroundColor(getResources().getColor(R.color.light_grey_ii));
                } else if (mode.equals("f")) {
                    searchView.setBackgroundColor(getResources().getColor(R.color.myTheme));
                }
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                newText = newText.replace("'", "’");
                searchText = newText;

                switch (tabKeeper) {
                    case 0:
                        mA.getFilter().filter(newText);
                        break;
                    case 1:
                        fA.getFilter().filter(newText);
                        break;
                    case 2:
                        rA.getFilter().filter(newText);
                }
                return false;
            }
        });

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {

                copies = db.getWholeData();
                favs = favDb.getWholeFav();
                myADApter.clickPurpose.clear();
                favAdapter.clickPurposeFav.clear();
                mA.notifyDataSetChanged();
                fA.notifyDataSetChanged();

                reminders = remDb.getWholeFav();
                remAdapter.clickPurposeFav.clear();
                rA.notifyDataSetChanged();

                searchText = "";
                order.setVisible(true);
                overflow.setVisible(true);

                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);

                searchView.setBackgroundColor(getResources().getColor(android.R.color.transparent));

                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.refresh) {
            switch (tabKeeper) {
                case 0:
                    mA.notifyDataSetChanged();
                    break;
                case 1:
                    fA.notifyDataSetChanged();
                    break;
                case 2:
                    rA.notifyDataSetChanged();
                    break;
            }
        } else if (item.getItemId() == R.id.order) {

            if (tabKeeper == 1 || tabKeeper == 0) {

                orderReverser++;

                Collections.reverse(copies);
                db.deleteOrUpdateData();
                mA.notifyDataSetChanged();

                Collections.reverse(favs);
                favDb.UpdateFav();
                fA.notifyDataSetChanged();

                sharedPreferences.edit().putInt("order", orderReverser).apply();

                if (orderReverser % 2 == 0) {
                    Toast.makeText(this, "Ordered by Oldest on Top", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Ordered by Latest on Top", Toast.LENGTH_SHORT).show();
                }

            }

        } else if (item.getItemId() == R.id.addMe) {
            Intent intentEdit = new Intent(this, Translator.class);
            intentEdit.putExtra("content", "");
            intentEdit.putExtra("mf", tabKeeper);
            startActivity(intentEdit);
        } else if (item.getItemId() == R.id.addIt) {
            Intent intent1 = new Intent(this, EditActivity.class);
            intent1.putExtra("textPresent", "");
            intent1.putExtra("mORf", tabKeeper + 1);
            startActivity(intent1);
        } else if (item.getItemId() == R.id.addRem) {
            Intent intentRem = new Intent(this, ReminderActivity.class);
            startActivity(intentRem);
        }

        return super.onOptionsItemSelected(item);
    }

    static public Notification Serviceotification(Context ctx, String content) {

        ClipboardManager clipManager = (ClipboardManager) ctx.getSystemService(CLIPBOARD_SERVICE);
        String clipContent = "";

        assert clipManager != null;
        if (clipManager.hasPrimaryClip()) {
            clipContent = clipManager.getPrimaryClip().getItemAt(0).coerceToText(ctx).toString();
        }

        Intent intent3 = new Intent(ctx, MyReceiver3.class);
        intent3.setPackage(ctx.getPackageName());
        PendingIntent pendingIntent3 = PendingIntent.getBroadcast(ctx, 0, intent3, 0);

        Intent intent4 = new Intent(ctx, MainActivity.class);
        intent4.setPackage(ctx.getPackageName());
        PendingIntent pendingIntent4 = PendingIntent.getActivity(ctx, 0, intent4, 0);

        Intent intent5 = new Intent(ctx, MyReceiver4.class);
        intent5.setPackage(ctx.getPackageName());
        intent5.putExtra("content", content);
        PendingIntent pendingIntent5 = PendingIntent.getBroadcast(ctx, 0, intent5, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent intentCall = new Intent(ctx, ServiceCallReceiver.class);
        intentCall.setPackage(ctx.getPackageName());
        intentCall.putExtra("content", content);
        PendingIntent pendingIntentCall = PendingIntent.getBroadcast(ctx, 0, intentCall, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder notification2 = new NotificationCompat.Builder(ctx, App.channelID2);
        notification2.setSmallIcon(R.drawable.notepadfornoti);
        int x = ctx.getSharedPreferences("rgdgr8", MODE_PRIVATE).getInt("cop", 0) + ctx.getSharedPreferences("rgdgr8", MODE_PRIVATE).getInt("rep", 0);
        String y = "";
        if (x > 0) {
            y = " (" + ctx.getSharedPreferences("rgdgr8", MODE_PRIVATE).getInt("rep", 0) + " repeating)";
        }
        notification2.setContentTitle("Today's Reminders: " + x + y);
        if (!version) {
            if (clipManager.hasPrimaryClip()) {
                if (!clipContent.equals("")) {
                    notification2.setContentText("Current Clipboard content is: " + content);
                    notification2.setStyle(new NotificationCompat.BigTextStyle().bigText("Current Clipboard content is:\n\n" + content));
                } else {
                    notification2.setContentText("Current Clipboard content is: " + "<EMPTY>");
                }
            } else {
                notification2.setContentText("Current Clipboard content is: " + "<EMPTY>");
            }
        }

        notification2.setColor(ctx.getResources().getColor(R.color.org_colorAccent));
        notification2.setPriority(NotificationCompat.PRIORITY_DEFAULT);
        notification2.setContentIntent(pendingIntent4);
        notification2.addAction(R.drawable.ic_content_copy_white_24dp, "Clear", pendingIntent3);
        if (!clipContent.equals("")) {
            notification2.addAction(R.drawable.ic_search_white_24dp, "Search", pendingIntent5);
            notification2.addAction(R.drawable.ic_call_white_24dp, "Call", pendingIntentCall);
        }

        return notification2.build();

    }

    private void reset() {
        int count = 0;

        for (int i = 0; i < remDb.getWholeFav().size(); i++) {

            if (!remDb.getWholeFav().get(i).equals("")) {

                int pos = (1 + i) + 90000;
                int idd = i + 1;
                int last = sharedPreferences.getInt("last" + pos, 0);
                int r = sharedPreferences.getInt("r" + idd, 0);

                Calendar c = Calendar.getInstance();
                c.set(Calendar.YEAR, remDb.getWholeYear().get(i));
                c.set(Calendar.MONTH, remDb.getWholeMonth().get(i));
                c.set(Calendar.DAY_OF_MONTH, remDb.getWholeDay().get(i));
                c.set(Calendar.HOUR_OF_DAY, remDb.getWholeHour().get(i));
                c.set(Calendar.MINUTE, remDb.getWholeMin().get(i));
                c.set(Calendar.SECOND, 0);

                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                Intent intenta = new Intent(this, AlertReceiver.class);
                intenta.setPackage(getPackageName());
                intenta.putExtra("content", remDb.getWholeFav().get(i));
                intenta.putExtra("id", i + 1);
                intenta.putExtra("date", remDb.getWholeDate().get(i));
                intenta.putExtra("time", remDb.getWholeTime().get(i));
                intenta.putExtra("period", remDb.getWholePeriod().get(i));
                intenta.putExtra("num", remDb.getWholeNum().get(i));
                PendingIntent pendingIntent = PendingIntent.getBroadcast(this, i + 1, intenta, PendingIntent.FLAG_UPDATE_CURRENT);

                Intent intentb = new Intent(this, AlertReceiver2.class);
                intentb.setPackage(getPackageName());
                intentb.putExtra("content", remDb.getWholeFav().get(i));
                intentb.putExtra("id", pos);
                intentb.putExtra("date", remDb.getWholeDate().get(i));
                intentb.putExtra("time", remDb.getWholeTime().get(i));
                intentb.putExtra("period", remDb.getWholePeriod().get(i));
                intentb.putExtra("num", remDb.getWholeNum().get(i));
                PendingIntent pendingIntentb = PendingIntent.getBroadcast(this, pos, intentb, PendingIntent.FLAG_UPDATE_CURRENT);
                assert alarmManager != null;

                String period = remDb.getWholePeriod().get(i);
                int num = remDb.getWholeNum().get(i);

                if (!c.before(Calendar.getInstance())) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);
                    } else {
                        alarmManager.setExact(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);
                    }
                } else if (remDb.getWholeNum().get(i) != 0 && !remDb.getWholePeriod().get(i).equals("")) {

                    switch (period) {
                        case "Minutes":
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, c.getTimeInMillis() + (1000 * 60 * num * r), pendingIntent);
                            } else {
                                alarmManager.setExact(AlarmManager.RTC_WAKEUP, c.getTimeInMillis() + (1000 * 60 * num * r), pendingIntent);
                            }
                            break;
                        case "Hours":
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, c.getTimeInMillis() + (1000 * 60 * 60 * num * r), pendingIntent);
                            } else {
                                alarmManager.setExact(AlarmManager.RTC_WAKEUP, c.getTimeInMillis() + (1000 * 60 * 60 * num * r), pendingIntent);
                            }
                            break;
                        case "Days":
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, c.getTimeInMillis() + (1000 * 60 * 60 * 24 * num * r), pendingIntent);
                            } else {
                                alarmManager.setExact(AlarmManager.RTC_WAKEUP, c.getTimeInMillis() + (1000 * 60 * 60 * 24 * num * r), pendingIntent);
                            }
                            break;
                        case "Weeks":
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, c.getTimeInMillis() + (1000 * 60 * 60 * 24 * num * 7 * r), pendingIntent);
                            } else {
                                alarmManager.setExact(AlarmManager.RTC_WAKEUP, c.getTimeInMillis() + (1000 * 60 * 60 * 24 * num * 7 * r), pendingIntent);
                            }
                            break;
                    }

                    if (last > 0) {

                        if (Calendar.getInstance().getTimeInMillis() < (c.getTimeInMillis() + last)) {

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, c.getTimeInMillis() + last, pendingIntentb);
                            } else {
                                alarmManager.setExact(AlarmManager.RTC_WAKEUP, c.getTimeInMillis() + last, pendingIntentb);
                            }
                        }
                    }

                } else {

                    if (last > 0) {

                        if (Calendar.getInstance().getTimeInMillis() < (c.getTimeInMillis() + last)) {

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, c.getTimeInMillis() + last, pendingIntentb);
                            } else {
                                alarmManager.setExact(AlarmManager.RTC_WAKEUP, c.getTimeInMillis() + last, pendingIntentb);
                            }
                        }
                    } else if (!remDb.getWholeFav().get(i).startsWith(dead)) {

                        count++;

                        Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

                        Intent intent4 = new Intent(this, NewTaskActivity.class);
                        intent4.putExtra("notiContent", remDb.getWholeFav().get(i));
                        intent4.putExtra("date", remDb.getWholeDate().get(i));
                        intent4.putExtra("time", remDb.getWholeTime().get(i));
                        intent4.putExtra("period", period);
                        intent4.putExtra("num", num);
                        intent4.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        PendingIntent pendingIntent4 = PendingIntent.getActivity(this, i + 1, intent4, PendingIntent.FLAG_UPDATE_CURRENT);

                        NotificationCompat.Builder notification2 = new NotificationCompat.Builder(this, App.channelID3);
                        notification2.setSmallIcon(R.drawable.ic_alarm_black_24dp);
                        notification2.setContentTitle("Missed Reminder " + count);
                        notification2.setContentText(remDb.getWholeFav().get(i));
                        notification2.setStyle(new NotificationCompat.BigTextStyle().bigText(remDb.getWholeFav().get(i)));
                        notification2.setColor(getResources().getColor(R.color.lighter_red_for_pink));
                        notification2.setVibrate(new long[]{0, 2000, 1000, 2000, 1000, 2000, 1000, 2000});
                        notification2.setSound(sound);
                        notification2.setPriority(NotificationCompat.PRIORITY_HIGH);
                        notification2.setContentIntent(pendingIntent4);
                        notification2.setVisibility(NotificationCompat.VISIBILITY_PUBLIC);

                        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
                        notificationManagerCompat.notify(i + 1, notification2.build());
                    }
                }
            }
        }
    }

    @SuppressLint("BatteryLife")
    private List<Intent> POWERMANAGER_INTENTS = Arrays.asList(
            new Intent().setComponent(new ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity")),
            new Intent().setComponent(new ComponentName("com.letv.android.letvsafe", "com.letv.android.letvsafe.AutobootManageActivity")),
            new Intent().setComponent(new ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.optimize.process.ProtectActivity")),
            new Intent().setComponent(new ComponentName("com.huawei.systemmanager", Build.VERSION.SDK_INT >= Build.VERSION_CODES.P ? "com.huawei.systemmanager.startupmgr.ui.StartupNormalAppListActivity" : "com.huawei.systemmanager.appcontrol.activity.StartupAppControlActivity")),
            new Intent().setComponent(new ComponentName("com.coloros.oppoguardelf", "com.coloros.powermanager.fuelgaue.PowerUsageModelActivity")),
            new Intent().setComponent(new ComponentName("com.coloros.oppoguardelf", "com.coloros.powermanager.fuelgaue.PowerSaverModeActivity")),
            new Intent().setComponent(new ComponentName("com.coloros.oppoguardelf", "com.coloros.powermanager.fuelgaue.PowerConsumptionActivity")),
            new Intent().setComponent(new ComponentName("com.coloros.safecenter", "com.coloros.safecenter.permission.startup.StartupAppListActivity")),
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.N ? new Intent().setComponent(new ComponentName("com.coloros.safecenter", "com.coloros.safecenter.startupapp.StartupAppListActivity")).setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).setData(Uri.parse("package:" + packageName)) : null,
            new Intent().setComponent(new ComponentName("com.oppo.safe", "com.oppo.safe.permission.startup.StartupAppListActivity")),
            new Intent().setComponent(new ComponentName("com.iqoo.secure", "com.iqoo.secure.ui.phoneoptimize.AddWhiteListActivity")),
            new Intent().setComponent(new ComponentName("com.iqoo.secure", "com.iqoo.secure.ui.phoneoptimize.BgStartUpManager")),
            new Intent().setComponent(new ComponentName("com.vivo.permissionmanager", "com.vivo.permissionmanager.activity.BgStartUpManagerActivity")),
            new Intent().setComponent(new ComponentName("com.asus.mobilemanager", "com.asus.mobilemanager.entry.FunctionActivity")),
            new Intent().setComponent(new ComponentName("com.asus.mobilemanager", "com.asus.mobilemanager.autostart.AutoStartActivity")),
            new Intent().setComponent(new ComponentName("com.letv.android.letvsafe", "com.letv.android.letvsafe.AutobootManageActivity")).setData(android.net.Uri.parse("mobilemanager://function/entry/AutoStart")),
            new Intent().setComponent(new ComponentName("com.meizu.safe", "com.meizu.safe.security.SHOW_APPSEC")).addCategory(Intent.CATEGORY_DEFAULT).putExtra("packageName", packageName)
    );

    private boolean isCallable(Intent intent) {
        try {
            if (intent == null) {
                return false;
            } else {
                List<ResolveInfo> list = getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
                return list.size() > 0;
            }
        } catch (Exception ignored) {
            return false;
        }
    }

    private void autoStart() {

        c = sharedPreferences.getInt("intent", 0);

        if (c != POWERMANAGER_INTENTS.size() && c != -2) {
            for (final Intent intent : POWERMANAGER_INTENTS) {
                if (isCallable(intent)) {
                    AlertDialog.Builder ab = new AlertDialog.Builder(this);
                    ab.setTitle("Allow AutoStart(Highly Required!)")
                            .setMessage("Without this permission, your Reminders will be turned off once device gets switched off")
                            .setView(R.layout.auto_perm)
                            .setPositiveButton("Allow", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    try {
                                        startActivity(intent);
                                    } catch (Exception e) {
                                        e.printStackTrace();

                                        AlertDialog ad = ab.create();
                                        ad.cancel();

                                        c = -2;
                                        sharedPreferences.edit().putInt("intent", c).apply();
                                        alerter();
                                    }
                                }
                            }).setNegativeButton("Deny", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog1, int which) {

                            new AlertDialog.Builder(MainActivity.this).setTitle("Are you sure?")
                                    .setMessage("WITHOUT THIS PERMISSION, REMINDERS WILL BE REMOVED ON DEVICE SHUTDOWN!!")
                                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog2, int which) {

                                            new AlertDialog.Builder(MainActivity.this)
                                                    .setTitle("Allow AutoStart(HIGHLY REQUIRED)!!")
                                                    .setMessage("WITHOUT THIS PERMISSION, REMINDERS WILL BE REMOVED ON DEVICE SHUTDOWN!!")
                                                    .setPositiveButton("Allow", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            try {
                                                                startActivity(intent);
                                                            } catch (Exception e) {
                                                                e.printStackTrace();

                                                                AlertDialog ad = ab.create();
                                                                ad.cancel();

                                                                c = -2;
                                                                sharedPreferences.edit().putInt("intent", c).apply();
                                                                alerter();
                                                            }
                                                        }
                                                    }).setNegativeButton("Deny", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog3, int which) {
                                                    dialog3.cancel();
                                                }
                                            }).show();
                                        }
                                    }).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            }).show();
                        }
                    }).show();
                    break;

                } else {
                    c++;
                }
            }

            sharedPreferences.edit().putInt("intent", c).apply();

            if (c >= POWERMANAGER_INTENTS.size()) {
                alerter();
            }
        } else {
            alerter();
        }
    }

    private void alerter() {
        new AlertDialog.Builder(this)
                .setView(R.layout.autostartperm2)
                .setNeutralButton("OK", null)
                .show();
    }

    private void notiAlerter() {
        AlertDialog.Builder ab = new AlertDialog.Builder(MainActivity.this)
                .setView(R.layout.noti_perm)
                .setPositiveButton("Allow", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent();
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            intent.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
                            intent.putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());
                        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
                            intent.putExtra("app_package", getPackageName());
                            intent.putExtra("app_uid", getApplicationInfo().uid);
                        } else {
                            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            intent.addCategory(Intent.CATEGORY_DEFAULT);
                            intent.setData(Uri.parse("package:" + getPackageName()));
                        }
                        startActivity(intent);
                    }
                }).setNegativeButton("Deny", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog3, int which) {
                        dialog3.cancel();
                    }
                });

        AlertDialog dialog1 = ab.create();

        Objects.requireNonNull(dialog1.getWindow()).setBackgroundDrawableResource(R.color.dialog);

        dialog1.show();

        if (Build.VERSION.SDK_INT >= 26) {
            AlertDialog.Builder alertBuild = new AlertDialog.Builder(MainActivity.this)
                    .setView(R.layout.noti_perm2)
                    .setPositiveButton("Check", new DialogInterface.OnClickListener() {
                        @RequiresApi(api = Build.VERSION_CODES.O)
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS)
                                    .putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName())
                                    .putExtra(Settings.EXTRA_CHANNEL_ID, App.channelID3);
                            startActivity(intent);
                        }
                    }).setNegativeButton("Pass", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog3, int which) {
                            dialog3.cancel();
                        }
                    });
            AlertDialog dialog = alertBuild.create();

            Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(R.color.dialog);

            dialog.show();
        }
    }

    //public static MyClipListenr mOnPrimaryClipChangedListener = new MyClipListenr();

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        packageName = this.getPackageName();
        sharedPreferences = getSharedPreferences("rgdgr8", MODE_PRIVATE);
        boolean first = sharedPreferences.getBoolean("first", true);

        if (first) {

            notiAlerter();

            if (Build.VERSION.SDK_INT >= 29) {
                new AlertDialog.Builder(this)
                        .setView(R.layout.androidten)
                        .setNeutralButton("OK", null)
                        .show();
            }

            autoStart();

            sharedPreferences.edit().putBoolean("first", false).apply();

            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, 23);
            calendar.set(Calendar.MINUTE, 59);
            calendar.set(Calendar.SECOND, 59);

            Intent intent = new Intent(this, RemChecker.class);
            intent.setPackage(getPackageName());
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 99999999, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            assert alarmManager != null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis() + 3000, pendingIntent);
            } else {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis() + 3000, pendingIntent);
            }
        }

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getResources().getString(R.string.adID_inter));

        intentSer = new Intent(this, MyService.class);

        db = new MyDatabase(this, "Notes", null, 1);
        favDb = new FavDatabase(this, "Favs", null, 1);
        remDb = new RemDatabase(this, "Rems", null, 1);

        c = sharedPreferences.getInt("intent", 0);
        if (c >= POWERMANAGER_INTENTS.size() || c == -2) {
            reset();
        }

        PowerManager pm = (PowerManager) this.getSystemService(Context.POWER_SERVICE);
        assert pm != null;
        if (!pm.isIgnoringBatteryOptimizations(packageName)) {
            try {
                @SuppressLint("BatteryLife")
                Intent intent = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                intent.setData(Uri.parse("package:" + packageName));
                startActivity(intent);
            } catch (ActivityNotFoundException e) {
                e.printStackTrace();
            }
        }

        if (Build.VERSION.SDK_INT >= 29) {
            version = true;
        } else {
            version = false;
        }

        sharedPreferences.edit().putBoolean("version", version).apply();

        message = getResources().getString(R.string.helptalk) + "\r\n\r\n" + getResources().getString(R.string.tip_2) + "\r\n" + getResources().getString(R.string.tip_3)
                + "\r\n" + getResources().getString(R.string.tip_4) + "\r\n" + getResources().getString(R.string.tip_5) + "\r\n" + getResources().getString(R.string.tip_6);

        message2 = getResources().getString(R.string.tip_ii2)
                + "\r\n" + getResources().getString(R.string.tip_ii3) + "\r\n" + getResources().getString(R.string.tip_ii4);

        orderReverser = sharedPreferences.getInt("order", 0);

        toolbar = findViewById(R.id.tb);
        setSupportActionBar(toolbar);

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        header = navigationView.getHeaderView(0);

        myTab = findViewById(R.id.myTab);
        myPager = findViewById(R.id.myPager);

        myPager.setAdapter(new myAdapter(getSupportFragmentManager()));
        myTab.setupWithViewPager(myPager);


        copies = db.getWholeData();

        favs = favDb.getWholeFav();

        reminders = remDb.getWholeFav();

        dateList = MainActivity.remDb.getWholeDate();
        timeList = MainActivity.remDb.getWholeTime();
        periods = MainActivity.remDb.getWholePeriod();

        year = MainActivity.remDb.getWholeYear();
        month = MainActivity.remDb.getWholeMonth();
        day = MainActivity.remDb.getWholeDay();
        hour = MainActivity.remDb.getWholeHour();
        min = MainActivity.remDb.getWholeMin();
        nums = MainActivity.remDb.getWholeNum();

        clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);

        assert clipboardManager != null;
        if (clipboardManager.hasPrimaryClip() && !clipboardManager.getPrimaryClip().getItemAt(0).coerceToText(this).toString().equals("")) {
            ClipData.Item data = clipboardManager.getPrimaryClip().getItemAt(0);
            String text = data.coerceToText(MainActivity.this).toString();
            text = text.replace("'", "’");
            if (!copies.contains(text)) {
                if (orderReverser % 2 == 0) {
                    copies.add(text);
                } else {
                    Collections.reverse(copies);
                    copies.add(text);
                    Collections.reverse(copies);
                }

                db.deleteOrUpdateData();

                Toast.makeText(this, "Added to Utility Pad MAIN!", Toast.LENGTH_SHORT).show();
            }
        }

        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                try {
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {

                        Window window = getWindow();

                        // clear FLAG_TRANSLUCENT_STATUS flag;
                        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

                        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
                        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

                        // finally change the color again to dark
                        if (mode.equals("o")) {
                            window.setStatusBarColor(getResources().getColor(R.color.navy));
                        } else {
                            window.setStatusBarColor(getResources().getColor(R.color.red_for_pink));
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);

                try {

                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {

                        Window window = getWindow();

                        // clear FLAG_TRANSLUCENT_STATUS flag:
                        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

                        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
                        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

                        // finally change the color to any color with transparency
                        window.setStatusBarColor(getResources().getColor(android.R.color.transparent));

                    }

                } catch (Exception e) {

                    e.printStackTrace();

                }
            }

        };

        drawer.addDrawerListener(toggle);
        toggle.syncState();

        if (savedInstanceState != null) {
            copies = savedInstanceState.getStringArrayList("copies");
            favs = savedInstanceState.getStringArrayList("favs");
            reminders = savedInstanceState.getStringArrayList("rems");

            /*

            boolean s=savedInstanceState.getBoolean("search");
            searchText=savedInstanceState.getString("searchText","");
            tabKeeper=savedInstanceState.getInt("tab",0);

            searchView.setIconified(s);

            if (!searchText.equals("")) {
                searchView.setQuery(searchText, false);

                switch (tabKeeper){
                    case 0:
                        mA.getFilter().filter(searchText);
                        break;
                    case 1:
                        fA.getFilter().filter(searchText);
                        break;
                    case 2:
                        rA.getFilter().filter(searchText);
                        break;
                }
            }

             */
        }

        mA = new myADApter(this);
        fA = new favAdapter(this);
        rA = new remAdapter(this);

        myTab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                myPager.setCurrentItem(tab.getPosition());
                tabKeeper = tab.getPosition();

                if (oncreate) {
                    if (tabKeeper == 2 || tabKeeper == 3) {
                        order.setVisible(false);
                        addTrans.setVisible(false);
                        addnew.setVisible(false);
                    } else {
                        order.setVisible(true);
                        addTrans.setVisible(true);
                        addnew.setVisible(true);
                    }
                }

                switch (tab.getPosition()) {
                    case 0:
                        if (searchView != null && !searchView.isIconified() && !searchText.equals("")) {
                            mA.getFilter().filter(searchText);

                            favAdapter.clickPurposeFav.clear();
                            favs.clear();
                            favs.addAll(favDb.getWholeFav());
                        }
                        if (favAdapter.actionMode != null) {
                            favAdapter.mode1.finish();
                        }
                        mA.notifyDataSetChanged();
                        break;
                    case 1:
                        if (searchView != null && !searchView.isIconified() && !searchText.equals("")) {
                            fA.getFilter().filter(searchText);
                        }
                        Log.i("tab", "1");
                        if (myADApter.actionMode != null) {
                            myADApter.mode0.finish();
                        } else if (remAdapter.actionMode != null) {
                            remAdapter.mode2.finish();
                        }
                        fA.notifyDataSetChanged();
                        break;
                    case 2:
                        if (searchView != null && !searchView.isIconified() && !searchText.equals("")) {
                            rA.getFilter().filter(searchText);
                        }
                        if (favAdapter.actionMode != null) {
                            favAdapter.mode1.finish();
                        }
                        rA.notifyDataSetChanged();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }

    private void reminderToday() {

        Calendar c = Calendar.getInstance();

        int cop = 0;
        int repeaters = 0;
        int dayop = c.get(Calendar.DAY_OF_MONTH);
        int monthop = c.get(Calendar.MONTH);
        int yearop = c.get(Calendar.YEAR);

        for (int i = 0; i < remDb.getWholeFav().size(); i++) {

            int id = i + 1;
            int r = sharedPreferences.getInt("r" + id, 0);

            Calendar a = Calendar.getInstance();
            a.set(Calendar.HOUR_OF_DAY, 23);
            a.set(Calendar.MINUTE, 59);
            a.set(Calendar.SECOND, 57);

            Calendar b = Calendar.getInstance();
            b.set(Calendar.YEAR, remDb.getWholeYear().get(i));
            b.set(Calendar.MONTH, remDb.getWholeMonth().get(i));
            b.set(Calendar.DAY_OF_MONTH, remDb.getWholeDay().get(i));
            b.set(Calendar.HOUR_OF_DAY, remDb.getWholeHour().get(i));
            b.set(Calendar.MINUTE, remDb.getWholeMin().get(i));
            b.set(Calendar.SECOND, 0);

            if ((remDb.getWholeDay().get(i) == dayop && remDb.getWholeMonth().get(i) == monthop && remDb.getWholeYear().get(i) == yearop && !remDb.getWholeFav().get(i).startsWith(dead))) {

                if ((!remDb.getWholePeriod().get(i).equals("") && remDb.getWholeNum().get(i) != 0)) {

                    if (Calendar.getInstance().before(b) || (remDb.getWholePeriod().get(i).equals("Minutes") && (b.getTimeInMillis() + remDb.getWholeNum().get(i) * 1000 * 60 * r) <= a.getTimeInMillis()) || (remDb.getWholePeriod().get(i).equals("Hours") && (b.getTimeInMillis() + remDb.getWholeNum().get(i) * 1000 * 60 * 60 * r) <= a.getTimeInMillis())) {
                        repeaters++;
                    }

                } else {
                    cop++;
                }
            } else if (r > 0) {
                Log.i("TAG", "" + r);
                if ((remDb.getWholePeriod().get(i).equals("Minutes") && (b.getTimeInMillis() + remDb.getWholeNum().get(i) * 1000 * 60 * r) <= a.getTimeInMillis()) || (remDb.getWholePeriod().get(i).equals("Hours") && (b.getTimeInMillis() + remDb.getWholeNum().get(i) * 1000 * 60 * 60 * r) <= a.getTimeInMillis()) || (remDb.getWholePeriod().get(i).equals("Days") && (b.getTimeInMillis() + remDb.getWholeNum().get(i) * 1000 * 60 * 60 * 24 * r) <= a.getTimeInMillis()) || (remDb.getWholePeriod().get(i).equals("Weeks") && (b.getTimeInMillis() + remDb.getWholeNum().get(i) * 1000 * 60 * 60 * 24 * 7 * r) <= a.getTimeInMillis())) {
                    repeaters++;
                }
            }
        }

        sharedPreferences.edit().putInt("cop", cop).apply();
        sharedPreferences.edit().putInt("rep", repeaters).apply();
    }

    private ClipboardManager.OnPrimaryClipChangedListener mOnPrimaryClipChangedListener =
            new ClipboardManager.OnPrimaryClipChangedListener() {
                @Override
                public void onPrimaryClipChanged() {

                    if (background) {
                        if (clipboardManager.hasPrimaryClip() && !clipboardManager.getPrimaryClip().getItemAt(0).coerceToText(MainActivity.this).toString().equals("")) {
                            ClipData.Item data = clipboardManager.getPrimaryClip().getItemAt(0);
                            String text = data.coerceToText(MainActivity.this).toString();
                            text = text.replace("'", "’");

                            if (!copies.equals(db.getWholeData())) {
                                copies.clear();
                                copies.addAll(db.getWholeData());
                            }

                            if (!copies.contains(text)) {
                                if (orderReverser % 2 == 0) {
                                    copies.add(text);
                                } else {
                                    Collections.reverse(copies);
                                    copies.add(text);
                                    Collections.reverse(copies);
                                }
                                db.deleteOrUpdateData();

                                Toast.makeText(MainActivity.this, "Saved to Utility Pad MAIN!", Toast.LENGTH_SHORT).show();

                                if (mA != null) {
                                    mA.notifyDataSetChanged();
                                }

                                if (background) {
                                    reminderToday();
                                    startService(intentSer);
                                }
                            }
                        }
                    } else {
                        Log.i("onClipChange", String.valueOf(false));
                    }
                }
            };

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putStringArrayList("copies", copies);
        outState.putStringArrayList("favs", favs);
        outState.putStringArrayList("rems", reminders);

        /*
        outState.putBoolean("search",searchView.isIconified());
        outState.putString("searchText",searchText);
        outState.putInt("tab",tabKeeper);

         */
    }

    private void shareAndBug() {
        AlertDialog.Builder builder = null;

        if (mode.equals("o")) {
            builder = new AlertDialog.Builder(this, R.style.myDialog72);
        } else if (mode.equals("f")) {
            builder = new AlertDialog.Builder(this, R.style.myDialog7);
        }

        assert builder != null;
        builder.setTitle("Found a Bug!")
                .setMessage("Do you want to report the bug by leaving a review on Google Play Store?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        try {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + packageName)));
                        } catch (android.content.ActivityNotFoundException anfe) {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + packageName)));
                        }
                    }
                }).setNegativeButton("No", null);

        AlertDialog dialog = builder.create();
        dialog.show();
        TextView alertMessage2 = dialog.findViewById(android.R.id.message);
        Button button1 = dialog.findViewById(android.R.id.button1);
        Button button2 = dialog.findViewById(android.R.id.button2);
        assert alertMessage2 != null;

        Typeface typeface1 = ResourcesCompat.getFont(this, R.font.alegreya_sc_italic);
        alertMessage2.setTypeface(typeface1);
        alertMessage2.setTextColor(Color.BLACK);
        assert button1 != null;
        assert button2 != null;
        if (mode.equals("f")) {
            button1.setTextColor(getResources().getColor(R.color.colorPrimary));
            button2.setTextColor(getResources().getColor(R.color.colorPrimary));
        } else if (mode.equals("o")) {
            button1.setTextColor(getResources().getColor(R.color.navy));
            button2.setTextColor(getResources().getColor(R.color.navy));
        }

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {

            case R.id.sound:
                if (Build.VERSION.SDK_INT >= 26) {
                    Intent intent = new Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS)
                            .putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName())
                            .putExtra(Settings.EXTRA_CHANNEL_ID, App.channelID3);
                    startActivity(intent);
                } else {
                    Toast.makeText(this, "Please go to Settings to change Reminder Sound", Toast.LENGTH_LONG).show();
                }
                return true;

            case R.id.shareApp:
                try {
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    String shareMessage = "Let me recommend you this application *UTILITY PAD*\n\n" + getResources().getString(R.string.app_desc) + "\n\n";
                    shareMessage = shareMessage + "https://play.google.com/store/apps/details?id=" + packageName;
                    shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
                    startActivity(Intent.createChooser(shareIntent, "Share Via"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return true;

            case R.id.bug:
                shareAndBug();
                return true;

            case R.id.rate:
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + packageName)));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + packageName)));
                }
                return true;

            case R.id.perm:

                PopupMenu popupMenu = new PopupMenu(this, header);
                popupMenu.inflate(R.menu.perm_menu);
                popupMenu.show();
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item1) {

                        switch (item1.getItemId()) {

                            case R.id.autos:
                                autoStart();
                                return true;

                            case R.id.notiPerm:
                                notiAlerter();
                                return true;

                            default:
                                return false;
                        }
                    }
                });
                return true;

            case R.id.setting:

                clickNav++;
                if (clickNav == 1) {
                    Intent settingsIntent = new Intent(this, SettingsActivity.class);
                    startActivity(settingsIntent);
                }
                return true;

            case R.id.translating:

                if (mode.equals("f")) {
                    switch (font) {

                        case "0":
                            alert = new AlertDialog.Builder(this, R.style.myDialog0);
                            typeface = ResourcesCompat.getFont(this, R.font.annie_use_your_telescope);
                            break;
                        case "1":
                            alert = new AlertDialog.Builder(this, R.style.myDialog1);
                            typeface = ResourcesCompat.getFont(this, R.font.abeezee);
                            break;
                        case "2":
                            alert = new AlertDialog.Builder(this, R.style.myDialog2);
                            typeface = ResourcesCompat.getFont(this, R.font.amatic_sc);
                            break;
                        case "3":
                            alert = new AlertDialog.Builder(this, R.style.myDialog3);
                            typeface = ResourcesCompat.getFont(this, R.font.amita);
                            break;
                        case "4":
                            alert = new AlertDialog.Builder(this, R.style.myDialog4);
                            typeface = ResourcesCompat.getFont(this, R.font.bad_script);
                            break;
                        case "5":
                            alert = new AlertDialog.Builder(this, R.style.myDialog5);
                            typeface = ResourcesCompat.getFont(this, R.font.butterfly_kids);
                            break;
                        case "6":
                            alert = new AlertDialog.Builder(this, R.style.myDialog6);
                            typeface = ResourcesCompat.getFont(this, R.font.caesar_dressing);
                            break;
                        case "7":
                            alert = new AlertDialog.Builder(this, R.style.myDialog7);
                            typeface = ResourcesCompat.getFont(this, R.font.alegreya_sc_italic);
                            break;
                    }
                } else if (mode.equals("o")) {
                    switch (font) {

                        case "0":
                            alert = new AlertDialog.Builder(this, R.style.myDialog02);
                            typeface = ResourcesCompat.getFont(this, R.font.annie_use_your_telescope);
                            break;
                        case "1":
                            alert = new AlertDialog.Builder(this, R.style.myDialog12);
                            typeface = ResourcesCompat.getFont(this, R.font.abeezee);
                            break;
                        case "2":
                            alert = new AlertDialog.Builder(this, R.style.myDialog22);
                            typeface = ResourcesCompat.getFont(this, R.font.amatic_sc);
                            break;
                        case "3":
                            alert = new AlertDialog.Builder(this, R.style.myDialog32);
                            typeface = ResourcesCompat.getFont(this, R.font.amita);
                            break;
                        case "4":
                            alert = new AlertDialog.Builder(this, R.style.myDialog42);
                            typeface = ResourcesCompat.getFont(this, R.font.bad_script);
                            break;
                        case "5":
                            alert = new AlertDialog.Builder(this, R.style.myDialog52);
                            typeface = ResourcesCompat.getFont(this, R.font.butterfly_kids);
                            break;
                        case "6":
                            alert = new AlertDialog.Builder(this, R.style.myDialog62);
                            typeface = ResourcesCompat.getFont(this, R.font.caesar_dressing);
                            break;
                        case "7":
                            alert = new AlertDialog.Builder(this, R.style.myDialog72);
                            typeface = ResourcesCompat.getFont(this, R.font.alegreya_sc_italic);
                            break;
                    }
                }

                alert.setTitle("Important Translator Info:");
                alert.setMessage(message2);
                alert.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.cancel();
                    }
                });
                AlertDialog dialog = alert.create();
                dialog.show();
                TextView alertMessage2 = dialog.findViewById(android.R.id.message);
                Button button1 = dialog.findViewById(android.R.id.button3);
                assert alertMessage2 != null;
                alertMessage2.setTypeface(typeface);
                alertMessage2.setTextColor(Color.BLACK);
                assert button1 != null;
                if (mode.equals("f")) {
                    button1.setTextColor(getResources().getColor(R.color.colorPrimary));
                } else if (mode.equals("o")) {
                    button1.setTextColor(getResources().getColor(R.color.navy));
                }

                return true;

            case R.id.helping:

                if (mode.equals("f")) {
                    switch (font) {

                        case "0":
                            alert = new AlertDialog.Builder(this, R.style.myDialog0);
                            typeface = ResourcesCompat.getFont(this, R.font.annie_use_your_telescope);
                            break;
                        case "1":
                            alert = new AlertDialog.Builder(this, R.style.myDialog1);
                            typeface = ResourcesCompat.getFont(this, R.font.abeezee);
                            break;
                        case "2":
                            alert = new AlertDialog.Builder(this, R.style.myDialog2);
                            typeface = ResourcesCompat.getFont(this, R.font.amatic_sc);
                            break;
                        case "3":
                            alert = new AlertDialog.Builder(this, R.style.myDialog3);
                            typeface = ResourcesCompat.getFont(this, R.font.amita);
                            break;
                        case "4":
                            alert = new AlertDialog.Builder(this, R.style.myDialog4);
                            typeface = ResourcesCompat.getFont(this, R.font.bad_script);
                            break;
                        case "5":
                            alert = new AlertDialog.Builder(this, R.style.myDialog5);
                            typeface = ResourcesCompat.getFont(this, R.font.butterfly_kids);
                            break;
                        case "6":
                            alert = new AlertDialog.Builder(this, R.style.myDialog6);
                            typeface = ResourcesCompat.getFont(this, R.font.caesar_dressing);
                            break;
                        case "7":
                            alert = new AlertDialog.Builder(this, R.style.myDialog7);
                            typeface = ResourcesCompat.getFont(this, R.font.alegreya_sc_italic);
                            break;
                    }
                } else if (mode.equals("o")) {
                    switch (font) {

                        case "0":
                            alert = new AlertDialog.Builder(this, R.style.myDialog02);
                            typeface = ResourcesCompat.getFont(this, R.font.annie_use_your_telescope);
                            break;
                        case "1":
                            alert = new AlertDialog.Builder(this, R.style.myDialog12);
                            typeface = ResourcesCompat.getFont(this, R.font.abeezee);
                            break;
                        case "2":
                            alert = new AlertDialog.Builder(this, R.style.myDialog22);
                            typeface = ResourcesCompat.getFont(this, R.font.amatic_sc);
                            break;
                        case "3":
                            alert = new AlertDialog.Builder(this, R.style.myDialog32);
                            typeface = ResourcesCompat.getFont(this, R.font.amita);
                            break;
                        case "4":
                            alert = new AlertDialog.Builder(this, R.style.myDialog42);
                            typeface = ResourcesCompat.getFont(this, R.font.bad_script);
                            break;
                        case "5":
                            alert = new AlertDialog.Builder(this, R.style.myDialog52);
                            typeface = ResourcesCompat.getFont(this, R.font.butterfly_kids);
                            break;
                        case "6":
                            alert = new AlertDialog.Builder(this, R.style.myDialog62);
                            typeface = ResourcesCompat.getFont(this, R.font.caesar_dressing);
                            break;
                        case "7":
                            alert = new AlertDialog.Builder(this, R.style.myDialog72);
                            typeface = ResourcesCompat.getFont(this, R.font.alegreya_sc_italic);
                            break;
                    }
                }

                alert.setTitle("DIRECTIONS OF USE:");
                alert.setMessage(message);
                alert.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.cancel();
                    }
                });
                AlertDialog dialogNav = alert.create();
                dialogNav.show();
                TextView alertMessage = dialogNav.findViewById(android.R.id.message);
                Button buttonOk = dialogNav.findViewById(android.R.id.button3);
                assert alertMessage != null;
                alertMessage.setTypeface(typeface);
                alertMessage.setTextColor(Color.BLACK);
                assert buttonOk != null;
                if (mode.equals("f")) {
                    buttonOk.setTextColor(getResources().getColor(R.color.colorPrimary));
                } else if (mode.equals("o")) {
                    buttonOk.setTextColor(getResources().getColor(R.color.navy));
                }

                return true;

            default:
                return false;
        }
    }

    static class myAdapter extends FragmentPagerAdapter {


        String[] tabs = {"Main", "Favourites", "Reminders"};

        public myAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabs[position];
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {

            if (position == 0) {
                return new Main();
            } else if (position == 1) {
                return new Favourites();
            } else if (position == 2) {
                return new Reminder();
            } else {
                return null;
            }
        }

        @Override
        public int getCount() {
            return tabs.length;
        }
    }

    @Override
    public void onBackPressed() {

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (searchView != null && !drawer.isDrawerOpen(GravityCompat.START) && !searchView.isIconified()) {
            searchView.setIconified(true);
        } else if (!drawer.isDrawerOpen(GravityCompat.START) && searchView != null && searchView.isIconified()) {
            super.onBackPressed();
        }
    }

    @Override
    protected void onPause() {

        if (searchView != null && !searchView.isIconified()) {
            searchView.setIconified(true);
        }
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }

        if (background) {
            reminderToday();
            startService(intentSer);
        }

        super.onPause();
    }

    private void emptyCheck() {

        Log.i("emptyCheck", String.valueOf(reminders.size()));

        for (int i = 0; i < reminders.size(); i++) {
            if (reminders.get(i).equals("")) {

                Log.i("i", String.valueOf(i));

                int k = 0;

                for (int j = i; j < reminders.size(); j++) {
                    if (!reminders.get(j).equals("")) {
                        k++;
                    }
                }

                Log.i("k", String.valueOf(k));

                if (k == 0) {
                    for (int j = remDb.getWholeFav().size() - 1; j >= i; j--) {

                        Log.i("j", String.valueOf(j));

                        reminders.remove(j);
                        MainActivity.timeList.remove(j);
                        MainActivity.dateList.remove(j);
                        MainActivity.year.remove(j);
                        MainActivity.month.remove(j);
                        MainActivity.day.remove(j);
                        MainActivity.hour.remove(j);
                        MainActivity.min.remove(j);
                        MainActivity.nums.remove(j);
                        MainActivity.periods.remove(j);
                    }

                    remDb.UpdateFav();
                    break;
                }
            }
        }

        Log.i("emptyCheckEnd", String.valueOf(reminders.size()));
        for (int z = 0; z < reminders.size(); z++) {
            Log.i("emptyCheckEnd", reminders.get(z));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (background) {
            clipboardManager.addPrimaryClipChangedListener(mOnPrimaryClipChangedListener);
        } else {
            clipboardManager.removePrimaryClipChangedListener(mOnPrimaryClipChangedListener);
        }

        stopService(intentSer);

        add = preferences.getBoolean(SettingsActivity.add, false);
        font = preferences.getString(SettingsActivity.font, getResources().getString(R.string.default_font_pref));
        mode = preferences.getString(SettingsActivity.mode, getResources().getString(R.string.default_mode));
        swoosh = preferences.getBoolean(SettingsActivity.swoosh, true);
        pop = preferences.getBoolean(SettingsActivity.pop, true);
        background = preferences.getBoolean(SettingsActivity.background, true);
        size = preferences.getString(SettingsActivity.size, getResources().getString(R.string.defaultSize));
        clickedd = preferences.getString(SettingsActivity.clicked, getResources().getString(R.string.default_click));

        if (mode.equals("o")) {
            toolbar.setBackgroundColor(getResources().getColor(R.color.navy));
            toolbar.setTitleTextColor(Color.WHITE);
            getWindow().setStatusBarColor(getResources().getColor(R.color.navy));
            myTab.setSelectedTabIndicatorColor(getResources().getColor(R.color.navy));
            myPager.setBackgroundColor(getResources().getColor(R.color.light_grey_ii));
            navigationView.setBackgroundColor(getResources().getColor(R.color.navy));
            header.setBackgroundResource(R.drawable.typewriternav);
            myTab.setBackgroundColor(getResources().getColor(android.R.color.transparent));
            myTab.setTabTextColors(Color.BLACK, getResources().getColor(R.color.navy));
        } else if (mode.equals("f")) {
            toolbar.setBackgroundColor(getResources().getColor(R.color.red_for_pink));
            toolbar.setTitleTextColor(Color.WHITE);
            getWindow().setStatusBarColor(getResources().getColor(R.color.red_for_pink));
            myTab.setSelectedTabIndicatorColor(getResources().getColor(R.color.red_for_pink));
            myPager.setBackgroundColor(getResources().getColor(R.color.myTheme));
            navigationView.setBackgroundColor(getResources().getColor(R.color.red_for_pink));
            header.setBackgroundResource(R.drawable.typewriter2nav);
            myTab.setBackgroundColor(getResources().getColor(android.R.color.transparent));
            myTab.setTabTextColors(Color.BLACK, getResources().getColor(R.color.red_for_pink));
        }

        switch (font) {

            case "0":
                toolbar.setTitleTextAppearance(this, R.style.annie_use_your_telescope);
                break;
            case "1":
                toolbar.setTitleTextAppearance(this, R.style.abeezee);
                break;
            case "2":
                toolbar.setTitleTextAppearance(this, R.style.amatic_sc);
                break;
            case "3":
                toolbar.setTitleTextAppearance(this, R.style.amita);
                break;
            case "4":
                toolbar.setTitleTextAppearance(this, R.style.bungee_shade);
                break;
            case "5":
                toolbar.setTitleTextAppearance(this, R.style.butterfly_kids);
                break;
            case "6":
                toolbar.setTitleTextAppearance(this, R.style.caesar_dressing);
                break;
            case "7":
                toolbar.setTitleTextAppearance(this, R.style.press_start_2p);
                break;
        }

        if (db.getWholeData() != null) {
            copies = db.getWholeData();
        }

        if (favDb.getWholeFav() != null) {
            favs = favDb.getWholeFav();
        }

        if (remDb.getWholeFav() != null) {
            reminders = remDb.getWholeFav();

            dateList = MainActivity.remDb.getWholeDate();
            timeList = MainActivity.remDb.getWholeTime();
            periods = MainActivity.remDb.getWholePeriod();

            year = MainActivity.remDb.getWholeYear();
            month = MainActivity.remDb.getWholeMonth();
            day = MainActivity.remDb.getWholeDay();
            hour = MainActivity.remDb.getWholeHour();
            min = MainActivity.remDb.getWholeMin();
            nums = MainActivity.remDb.getWholeNum();

            emptyCheck();
        }

        Log.i("remOnres", String.valueOf(reminders.size()));

        mA.notifyDataSetChanged();
        fA.notifyDataSetChanged();
        rA.notifyDataSetChanged();

        Menu navMenu = navigationView.getMenu();

        if (Build.VERSION.SDK_INT >= 26) {
            navMenu.findItem(R.id.sound).setVisible(true);
        } else {
            navMenu.findItem(R.id.sound).setVisible(false);
        }

        message = getResources().getString(R.string.helptalk) + "\r\n\r\n" + getResources().getString(R.string.tip_2) + "\r\n" + getResources().getString(R.string.tip_3)
                + "\r\n" + getResources().getString(R.string.tip_4) + "\r\n" + getResources().getString(R.string.tip_5) + "\r\n" + getResources().getString(R.string.tip_6);

        message2 = getResources().getString(R.string.tip_ii2)
                + "\r\n" + getResources().getString(R.string.tip_ii3) + "\r\n" + getResources().getString(R.string.tip_ii4);

        packageName = this.getPackageName();

        clickNav = 0;

        mInterstitialAd.loadAd(new AdRequest.Builder().build());

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                mInterstitialAd.loadAd(new AdRequest.Builder().build());
            }
        });

        sharedPreferences.edit().putBoolean("main", true).apply();

    }

    @Override
    protected void onDestroy() {

        mA = null;
        fA = null;
        rA = null;
        favDb.close();
        remDb.close();
        searchView = null;

        if (!background) {
            db.close();
        }

        super.onDestroy();
    }
}
