package edu.ubbcluj.cs.groutes.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import edu.ubbcluj.cs.groutes.R;
import edu.ubbcluj.cs.groutes.activityhelper.HistoryAssist;
import edu.ubbcluj.cs.groutes.generalhelper.DatabaseAssist;

public class LoginTabActivity extends AppCompatActivity {

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
    private DatabaseAssist mDatabaseAssist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_tab);

   //     findViewById(R.id.loadingPanel).setVisibility(View.INVISIBLE);

        this.mDatabaseAssist = DatabaseAssist.getInstance();

//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), this);

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

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
        private LoginTabActivity context;

        public PlaceholderFragment() {
        }

        @SuppressLint("ValidFragment")
        public PlaceholderFragment(LoginTabActivity pContext) {
            context = pContext;
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber, LoginTabActivity pContext) {
            PlaceholderFragment fragment = new PlaceholderFragment(pContext);
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_login_tab, container, false);
            Button button = rootView.findViewById(R.id.email_sign_in_button);
            Button buttonReset = rootView.findViewById(R.id.btn_pw_reset);
            mEmailView = rootView.findViewById(R.id.email);
            mPasswordView = rootView.findViewById(R.id.password);

            if (getArguments().getInt(ARG_SECTION_NUMBER) == 1) {

                // todo - remove
                mEmailView.setText("l_zsombor@yahoo.com");
                mPasswordView.setText("123456");

                button.setText(getString(R.string.action_sign_in));
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        doLogin();
                    }
                });

                buttonReset.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        doPasswordReset();
                    }
                });
            } else {
                button.setText(getString(R.string.action_register));
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        doRegister();
                    }
                });

                buttonReset.setVisibility(View.INVISIBLE);
            }


            return rootView;
        }

        public void doRegister() {
            if(!isCredentialValid()){
                return;
            }

            String email = mEmailView.getText().toString();
            String password = mPasswordView.getText().toString();

            context.mDatabaseAssist.doRegister(context, email, password);
        }

        public void doLogin() {
            if(!isCredentialValid()){
                return;
            }

            String email = mEmailView.getText().toString();
            String password = mPasswordView.getText().toString();

            context.mDatabaseAssist.doLogin(context, email, password);
        }

        public void doPasswordReset(){
            String email = mEmailView.getText().toString();

            context.mDatabaseAssist.doPasswordReset(context, email);
        }

        private boolean isCredentialValid(){
            // Reset errors.
            mEmailView.setError(null);
            mPasswordView.setError(null);

            // Store values at the time of the login attempt.
            String email = mEmailView.getText().toString();
            String password = mPasswordView.getText().toString();

            boolean cancel = false;
            View focusView = null;

            // Check for a valid password.
            if (TextUtils.isEmpty(password)){
                mPasswordView.setError(getString(R.string.error_field_required));
                focusView = mPasswordView;
                cancel = true;
            }else if (!isPasswordValid(password)) {
                mPasswordView.setError(getString(R.string.error_invalid_password));
                focusView = mPasswordView;
                cancel = true;
            }

            // Check for a valid email address.
            if (TextUtils.isEmpty(email)) {
                mEmailView.setError(getString(R.string.error_field_required));
                focusView = mEmailView;
                cancel = true;
            } else if (!isEmailValid(email)) {
                mEmailView.setError(getString(R.string.error_invalid_email));
                focusView = mEmailView;
                cancel = true;
            }

            if (cancel) {
                // There was an error; don't attempt login and focus the first
                // form field with an error.
                focusView.requestFocus();
                return false;
            } else {
                // Show a progress spinner, and kick off a background task to
                // perform the user login attempt.
                return true;
            }
        }


        private boolean isEmailValid(String email) {
            //TODO: Replace this with your own logic
            if(email.isEmpty()){
                return false;
            }

            if(!email.contains("@")) {
                return false;
            }

            return true;
        }

        private boolean isPasswordValid(String password) {
            //TODO: Replace this with your own logic
             if(password.isEmpty()){
                return false;
            }

            if(password.length() < 6){
                return false;
            }

            return true;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        private LoginTabActivity context;

        public SectionsPagerAdapter(FragmentManager fm, LoginTabActivity pContext) {
            super(fm);
            context = pContext;
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).

            PlaceholderFragment placeholderFragment = PlaceholderFragment.newInstance(position + 1, context);
            return placeholderFragment;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 2;
        }
    }

    public void updateUI() {
        Intent intent = new Intent(this, LoggedInActivity.class);
        startActivity(intent);
    }

    public void showMessage(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }
}
