package com.example.andre.tabtest;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
   
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.andre.InfoList;
import com.example.andre.InfoUtils;
import com.example.andre.MtkUtil;
import com.example.andre.androidshell.ShellExecuter;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
{

    // settings
    public static final String PREF_USE_ROOT_MODE = "user_root_switch";
    public static final String PREF_APPEND_I2C_ADDRESS = "append_i2c_address";

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);


        adjustAvailabilityActions();

      FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

      fab.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
              Snackbar.make(view, R.string.action_engineer_mode, Snackbar.LENGTH_LONG)
                      .setAction("Action", null).show();

              openEngineerMode();
          }
      });
      
    }

    public void adjustAvailabilityActions()
    {
        String platform = InfoUtils.getPlatform();

        if ( ! InfoUtils.isMtkPlatform(platform))
        {
            FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

            fab.setVisibility(View.GONE);
        }
    }

    public void runApplication(String packageName, String activityName)
    {
        try {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.setComponent(new ComponentName(packageName, packageName + "." + activityName));
            startActivity(intent);
        }
        catch (Exception e)
        {
            System.err.println ("Can't run app");
        }
    }

    public void openEngineerMode()
    {
        runApplication("com.mediatek.engineermode", "EngineerMode");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_settings: {
                Intent intent = new Intent(this, com.example.andre.tabtest.SettingsActivity.class);
                startActivity(intent);
                return true;
            }
            case R.id.action_about: {
                Intent intent = new Intent(this, com.example.andre.tabtest.AboutActivity.class);
                startActivity(intent);
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    
  

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "SECTION 1";
                case 1:
                    return "SECTION 2";
                case 2:
                    return "SECTION 3";
            }
            return null;
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);

            int tab = getArguments().getInt(ARG_SECTION_NUMBER);

            TextView textView = (TextView) rootView.findViewById(R.id.section_label);

            //String resolution =  InfoUtils.getResolution();

            //textView.setText(resolution + getString(R.string.section_format, tab));

            TableLayout tableLayout = (TableLayout) rootView.findViewById(R.id.tableLayout);

            String platform = InfoUtils.getPlatform();

            ShellExecuter exec = new ShellExecuter();

            if (tab == 1)
            {
                Context context = tableLayout.getContext();

                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
                boolean isRootMode      = prefs.getBoolean(PREF_USE_ROOT_MODE, false);
                boolean isAppendAddress = prefs.getBoolean(PREF_APPEND_I2C_ADDRESS, false);

                //System.out.println("!!!!!!!!!!!" + rootMode);

                ArrayList< Pair<String, String> > objList = InfoList.buildInfoList(isRootMode, isAppendAddress);

                fillTableView(tableLayout, objList);
            }
            else if (InfoUtils.isMtkPlatform(platform))
            {
                if (tab == 2)
                {
                    ArrayList<Pair<String, String>> objList = InfoList.buildProjectConfigList();

                    if ( ! objList.isEmpty())
                        fillTableView(tableLayout, objList);
                }
                else if (tab == 3)
                {
                    String text = InfoUtils.getMtkPartitions(exec);

                    ArrayList<Pair<String, String>> objList = MtkUtil.makePartitionsList(text);

                    if ( ! objList.isEmpty())
                        fillTableView(tableLayout, objList);
                }
            }
            else if (InfoUtils.isRkPlatform(platform))
            {
                if (tab == 2)
                {
                    String partitions = InfoUtils.getRkPartitions(exec);

                    if ( ! partitions.isEmpty())
                        textView.setText(partitions);
                }
                else if (tab == 3)
                {
                    String nandInfo = InfoUtils.getRkNandInfo(exec);

                    if ( ! nandInfo.isEmpty())
                        fillTableViewSimpleText(tableLayout, nandInfo);
                }
            }

            return rootView;
        }
    }


    ///////////////////
    public static void fillTableView (TableLayout tableLayout, ArrayList< Pair<String, String> > objList)
    {
        Context context = tableLayout.getContext();

        tableLayout.removeAllViews();

        tableLayout.setStretchAllColumns(true);

        float horMargin = context.getResources().getDimension(R.dimen.activity_horizontal_margin);
        int screenWidthOffset = InfoUtils.getScreenWidth()/2 - Math.round(horMargin);

        // View

        for (int i = 0; i < objList.size(); i++)
        {
            Pair<String, String> obj = objList.get(i);

            TableRow row = new TableRow(context);
            TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
            row.setLayoutParams(lp);

            int color = ContextCompat.getColor(context, R.color.colorBackground);
            if (i % 2 == 0) row.setBackgroundColor(color);

            TextView text1 = new TextView(context);
            text1.setText(obj.first);

            TextView text2 = new TextView(context);
            text2.setText(obj.second);
            text2.setMaxWidth(screenWidthOffset);

            row.addView(text1);
            row.addView(text2);

            tableLayout.addView(row,i);
        }
    }

    public static void fillTableViewSimpleText (TableLayout tableLayout, String text)
    {
        Context context = tableLayout.getContext();

        tableLayout.removeAllViews();

        // View

        TableRow row = new TableRow(context);
        TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
        row.setLayoutParams(lp);

        TextView tv = new TextView(context);
        tv.setText(text);

        row.addView(tv);

        tableLayout.addView(row,0);
    }
}