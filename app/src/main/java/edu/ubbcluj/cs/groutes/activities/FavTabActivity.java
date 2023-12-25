package edu.ubbcluj.cs.groutes.activities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import edu.ubbcluj.cs.groutes.R;
import edu.ubbcluj.cs.groutes.activityhelper.FavAssist;
import edu.ubbcluj.cs.groutes.generalhelper.DatabaseAssist;

public class FavTabActivity extends AppCompatActivity {

    private static final String TAG = "grm.FavTabActivity";

    private static FavAssist mAssist;
    private static LinearLayout linRoutes;
    private int mTab = 0;

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
        setContentView(R.layout.activity_fav_tab);

        this.mAssist = FavAssist.getInstance();
        mAssist.setActivity(this);
        //this.addHistoryFields();

  //      findViewById(R.id.loadingPanel).setVisibility(View.INVISIBLE);

//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new FavTabActivity.SectionsPagerAdapter(getSupportFragmentManager(), this);

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mAssist.setTab(tab.getPosition());
                mTab = tab.getPosition();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });




    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login_tab, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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

        private AutoCompleteTextView mEmailView;
        private EditText mPasswordView;
        private FavTabActivity context;

        public PlaceholderFragment() {
        }

        @SuppressLint("ValidFragment")
        public PlaceholderFragment(FavTabActivity pContext) {
            context = pContext;
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static FavTabActivity.PlaceholderFragment newInstance(int sectionNumber, FavTabActivity pContext) {
            FavTabActivity.PlaceholderFragment fragment = new FavTabActivity.PlaceholderFragment(pContext);
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_fav_tab, container, false);

            linRoutes = rootView.findViewById(R.id.history_root2);
            linRoutes.removeAllViews();

            if (getArguments().getInt(ARG_SECTION_NUMBER) == 1) {
                //mAssist.setTab(1);
                // todo - remove
                Log.d(TAG, "1");
                mAssist.getFavData(linRoutes);


            } else {
                //mAssist.setTab(2);
                Log.d(TAG, "2");
                linRoutes = rootView.findViewById(R.id.history_root2);
                mAssist.getFavPlaces(linRoutes);
            }


            return rootView;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        private FavTabActivity context;

        public SectionsPagerAdapter(FragmentManager fm, FavTabActivity pContext) {
            super(fm);
            context = pContext;
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).

            FavTabActivity.PlaceholderFragment placeholderFragment = FavTabActivity.PlaceholderFragment.newInstance(position + 1, context);
            return placeholderFragment;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 2;
        }
    }

    public void onBtnShowMap(View view) {
        Intent intent;

        if(mTab == 0){
            mAssist.setSingleRoute(view, false);
            intent = new Intent(this, MultiRouteActivity.class);
        }else{
            mAssist.setSingleLocation(view, false);
            intent = new Intent(this, ShowMapActivity.class);
        }

        startActivity(intent);
    }

    public void onDelete(View view) {
        mAssist.deleteRoute(view);
    }

    public void onBtnSearch(View view){
        Intent intent;

        mAssist.setSingleRoute(view, true);

        intent = new Intent(this, SearchActivity.class);

        if(mTab == 0){
            mAssist.setSingleRoute(view, true);

        }else{
            mAssist.setSingleLocation(view, true);
        }

        intent = new Intent(this, SearchActivity.class);
        startActivity(intent);
    }

    public void onRename(final View view){
        mAssist.renameFav(view);
    }

}
