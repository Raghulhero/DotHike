package me.dotteam.dotprod;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.support.v4.app.NavUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;
import java.util.Formatter;
import java.util.List;
import java.util.Locale;

import me.dotteam.dotprod.data.HikeDataDirector;


/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p/>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class HikeSettingsActivity extends PreferenceActivity {

    public static final String TAG="HikeSettigns";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void setupActionBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            // Show the Up button in the action bar.
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. Use NavUtils to allow users
            // to navigate up one level in the application structure. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
            // TODO: If Settings has multiple levels, Up should navigate up
            // that hierarchy.
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected boolean isValidFragment(String fragmentName) {
        Log.e(TAG, "isValidFragment "+fragmentName);
        //TODO: Probably change this so we can confirm the fragment works.
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onIsMultiPane() {
        return isXLargeTablet(this);
    }

    /**
     * Helper method to determine if the device has an extra-large screen. For
     * example, 10" tablets are extra-large.
     */
    private static boolean isXLargeTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }


    /**
     * {@inheritDoc}
     */
    @Override
//    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.pref_headers, target);
    }

    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */
    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();

            if (preference instanceof ListPreference) {
                // For list preferences, look up the correct display value in
                // the preference's 'entries' list.
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);

                // Set the summary to reflect the new value.
                preference.setSummary(
                        index >= 0
                                ? listPreference.getEntries()[index]
                                : null);

            } else if (preference instanceof RingtonePreference) {
                // For ringtone preferences, look up the correct display value
                // using RingtoneManager.
                if (TextUtils.isEmpty(stringValue)) {
                    // Empty values correspond to 'silent' (no ringtone).

                } else {
                    Ringtone ringtone = RingtoneManager.getRingtone(
                            preference.getContext(), Uri.parse(stringValue));

                    if (ringtone == null) {
                        // Clear the summary if there was a lookup error.
                        preference.setSummary(null);
                    } else {
                        // Set the summary to reflect the new ringtone display
                        // name.
                        String name = ringtone.getTitle(preference.getContext());
                        preference.setSummary(name);
                    }
                }

            } else {
                // For all other preferences, set the summary to the value's
                // simple string representation.
                preference.setSummary(stringValue);
            }
            return true;
        }
    };

    /**
     * Binds a preference's summary to its value. More specifically, when the
     * preference's value is changed, its summary (line of text below the
     * preference title) is updated to reflect the value. The summary is also
     * immediately updated upon calling this method. The exact display format is
     * dependent on the type of preference.
     *
     * @see #sBindPreferenceSummaryToValueListener
     */
    private static void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        // Trigger the listener immediately with the preference's
        // current value.
        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
        Log.d(TAG, "bindPreferenceSummaryToValue "+
                PreferenceManager.getDefaultSharedPreferences(preference.getContext()).
                        getString(preference.getKey(),""));
    }

    public static class DisplayPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_display);

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            bindPreferenceSummaryToValue(findPreference("display_units"));
            bindPreferenceSummaryToValue(findPreference("display_maptype"));
        }
    }

    public static class SensorPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_sensors);

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            bindPreferenceSummaryToValue(findPreference("extsensor"));
            bindPreferenceSummaryToValue(findPreference("sensor_refresh"));

            Preference myPref = findPreference("extsensor_period");
            myPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(final Preference preference) {
                    SeekBar inputVal = new SeekBar(getActivity());
                    final TextView valueText = new TextView(getActivity());
                    LinearLayout root = new LinearLayout(getActivity());
                    SharedPreferences appPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

                    inputVal.setMax(2550);
                    inputVal.setProgress(appPrefs.getInt(preference.getKey(), 500));
                    valueText.setText(Integer.toString(inputVal.getProgress()));


                    root.setOrientation(LinearLayout.VERTICAL);
                    root.setGravity(Gravity.CENTER);

                    inputVal.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT));
                    valueText.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT));
                    valueText.setGravity(Gravity.CENTER);
                    valueText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
                    valueText.setText(String.format("%s ms",
                            PreferenceManager.getDefaultSharedPreferences(getActivity()).getInt(preference.getKey(), 500)));
                    root.addView(inputVal);
                    root.addView(valueText);

                    inputVal.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                        @Override
                        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                            if (progress < 50) {
                                progress = 50;
                            }
                            valueText.setText(String.format("%s ms", progress));
                        }

                        @Override
                        public void onStartTrackingTouch(SeekBar seekBar) {

                        }

                        @Override
                        public void onStopTrackingTouch(SeekBar seekBar) {
                            SharedPreferences appPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
                            SharedPreferences.Editor edit = appPrefs.edit();
                            edit.putInt(preference.getKey(), seekBar.getProgress());
                            edit.apply();
                        }
                    });


                    AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                    dialog.setTitle("Change Sensor Update Frequency");
                    dialog.setMessage("Higher values imply Lower Frequency and Less Battery Drain");
                    dialog.setView(root);
                    dialog.show();
                    return false;
                }
            });
        }
    }

    public static class NotificationPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_notification);

        }
    }

    public static class LocationPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_location);

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            bindPreferenceSummaryToValue(findPreference("location_permission"));

            Preference myPref = findPreference("location_filter_accuracy");
            myPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(final Preference preference) {
                    SeekBar inputVal = new SeekBar(getActivity());
                    final TextView valueText = new TextView(getActivity());
                    LinearLayout root = new LinearLayout(getActivity());
                    SharedPreferences appPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

                    inputVal.setMax(100);
                    inputVal.setProgress(appPrefs.getInt(preference.getKey(), 40));
                    valueText.setText(Integer.toString(inputVal.getProgress()));


                    root.setOrientation(LinearLayout.VERTICAL);
                    root.setGravity(Gravity.CENTER);

                    inputVal.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT));
                    valueText.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT));
                    valueText.setGravity(Gravity.CENTER);
                    valueText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
                    root.addView(inputVal);
                    root.addView(valueText);

                    inputVal.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                        @Override
                        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                            valueText.setText(Integer.toString(progress));
                        }

                        @Override
                        public void onStartTrackingTouch(SeekBar seekBar) {

                        }

                        @Override
                        public void onStopTrackingTouch(SeekBar seekBar) {
                            SharedPreferences appPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
                            SharedPreferences.Editor edit = appPrefs.edit();
                            edit.putInt(preference.getKey(), seekBar.getProgress());
                            edit.apply();
                        }
                    });


                    AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                    dialog.setTitle("Change Location Filter Accuracy");
                    dialog.setMessage("Lower values -> strict filtering\nHigher Values -> less filtering");
                    dialog.setView(root);
                    dialog.show();
                    return false;
                }
            });
        }
    }

    public static class AboutPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_about);

            //Set all the OnClick Listeners here
            Preference myPref =  findPreference("about_dothike");
            myPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {

                    //TODO: Make a nicer DialogFragment with some text about .Hike
                    AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                    dialog.setTitle("About .Hike");
                    dialog.setMessage(getResources().getString(R.string.app_summary));
                    if (BuildConfig.DEBUG) {
                        LinearLayout root = new LinearLayout(getActivity());
                        TextView debugInfo = new TextView(getActivity());


                        Date buildDate = new Date(BuildConfig.TIMESTAMP);
                        Formatter formatter = new Formatter(new StringBuilder(), Locale.US);
                        formatter.format("[ %1$s build ] \nBuild: %2$s\nCommitt: %3$s \n[from %4$s]",
                                BuildConfig.BUILD_TYPE,
                                buildDate.toString(),
                                BuildConfig.GIT_COMMIT_INFO,
                                BuildConfig.GIT_BRANCH);

                        debugInfo.setText(formatter.toString());
                        debugInfo.setLayoutParams(new LinearLayout.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.MATCH_PARENT));
                        root.addView(debugInfo);
                        root.setPadding(20, 20, 20, 20);
                        dialog.setView(root);
                    }
                    dialog.show();
                    return false;
                }
            });

            myPref = findPreference("about_devs");
            myPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    //TODO: Make an EVEN NICER dialog fragment with all our info
                    AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                    dialog.setTitle("About Team Dot");
//                    dialog.setMessage("Thanks devs");
                    LayoutInflater inflater = getActivity().getLayoutInflater();
                    View devView = inflater.inflate(R.layout.about_devs,null);
                    dialog.setView(devView);
                    dialog.show();
                    return false;
                }
            });

            myPref = findPreference("about_help");
            myPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    //Help isn't coming.... Unless we get funded! :D
                    Toast.makeText(getActivity(),"Coming Soon, Donate Now!",Toast.LENGTH_SHORT).show();
                    return false;
                }
            });

            myPref = findPreference("donate");
            myPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    //TODO: make a binding to paypal here.
                    return false;
                }
            });

            myPref = findPreference("thanks");
            myPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                    dialog.setTitle("Special Thanks from Team Dot");
                    dialog.setMessage(getString(R.string.special_thanks));
                    dialog.show();
                    return false;
                }
            });
        }
    }

    public static class StoragePreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_storage);

            Preference myPref =  findPreference("storage_reset");
            myPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setPositiveButton("Reset Everything", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //Delete EVERYTHING!
                            HikeDataDirector.getInstance(getActivity()).deleteAllData();
                        }
                    });

                    builder.setNegativeButton("Keep", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //Keep my data.
                        }
                    });
                    builder.setMessage("This will delete ALL hikes.\nThere's no going back after this. Are you sure you want to continue?");
                    builder.setTitle("Delete ALL Stored Data");
                    AlertDialog deleteAlert = builder.create();
                    deleteAlert.setCancelable(true);
                    deleteAlert.show();
                    return false;
                }
            });
        }
    }

    public static class DemoDayFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_demo);

            Preference myPref =  findPreference("demo");
            myPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    if(!DemoShow.isRunning){
                        DemoShow demo = new DemoShow(getActivity().getApplicationContext());
                        demo.start();
                        Toast.makeText(getActivity(),"Loading Demo Data...",Toast.LENGTH_LONG).show();
                        startActivity(new Intent(getActivity(), HikeViewPagerActivity.class));
                    }
                    return false;
                }
            });
        }
    }
}
