package com.app.iostudio.activity;

import android.app.ProgressDialog;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.app.iostudio.R;

import java.text.DecimalFormat;


/**
 * Created by AnantShah on 20-11-2016.
 */

abstract public class BaseActivity extends AppCompatActivity {
    private static final String TAG = "BaseActivity";
    public ProgressDialog progressDialog;
    Toolbar toolbar;
    EditText edit_text_search_toolbar;
    ImageView image_view_cross_search_toolbar;
    ProgressBar progress_bar_toolbar;
    boolean isLocationEnabled;
    String className;
    int isFirstTime = 0;
    TextView textView_toolbar_title;
    ImageView image_view_secondary;
    DecimalFormat decimalFormat;
    String orderType;

    /*public AdRequest getadRequestTestDevice() {
        return  new AdRequest.Builder()
                .addTestDevice("696B836DBA93569E8F3CEFCB273A94C5")
                .build();
    }*/
    @Override
    @SuppressWarnings("unchecked")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        className = this.getClass().getSimpleName();
        decimalFormat = new DecimalFormat("#.##");

    }


    public void initBase() {
        initView();
        initData();
        bindEvent();
    }

    public void setToolbar(boolean isShowTitle, boolean isHomeUpEnabled,
            String toolbar_title_text, boolean isShowShadow) {
        toolbar = (Toolbar) findViewById(R.id.toolbar_main);
        AppBarLayout appbar_layout = findViewById(R.id.appbar_layout);
        if (toolbar != null) {
            textView_toolbar_title = (TextView) toolbar.findViewById(R.id.textView_toolbar_title);

        }
        setSupportActionBar(toolbar);
        try {
            if (isShowShadow &&
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                appbar_layout.setElevation(2.0f);
            }

            if (isHomeUpEnabled) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setDisplayShowTitleEnabled(false);
                textView_toolbar_title.setVisibility(View.VISIBLE);
                textView_toolbar_title.setText(toolbar_title_text);
            } else {
                if (isShowTitle) {
                    //getSupportActionBar().setTitle(toolbar_title_text);
                    getSupportActionBar().setDisplayShowTitleEnabled(false);
                    textView_toolbar_title.setText(toolbar_title_text);
                    textView_toolbar_title.setVisibility(View.VISIBLE);
                } else {
                    textView_toolbar_title.setVisibility(View.GONE);
                    getSupportActionBar().setDisplayShowTitleEnabled(false);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    ;

    protected void addFragment(final int container, Fragment fragment, boolean addToBackStack) {
        if (addToBackStack) {
            getSupportFragmentManager().beginTransaction().add(container, fragment)
                    .addToBackStack(null).commit();
        } else {
            getSupportFragmentManager().beginTransaction().add(container, fragment).commit();
        }
    }

    protected void replaceFragment(final int container, Fragment fragment, boolean addToBackStack) {
        if (addToBackStack) {
            getSupportFragmentManager().beginTransaction().replace(container, fragment)
                    .addToBackStack(null).commit();
        } else {
            getSupportFragmentManager().beginTransaction().replace(container, fragment).commit();
        }
    }

    protected abstract void initView();

    protected abstract void initData();

    protected abstract void bindEvent();

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home: // default back
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    finishAfterTransition();
                } else {
                    finish();
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    public void showAlertNoInternet() {
        // Alert.alert(this, "Alert", getString(R.string.no_internet), "", "Ok", null, null);

    }


    public void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();

    }


    public void setRecyclerViewWithAdapter(RecyclerView recyclerView,
            RecyclerView.LayoutManager layoutManager,
            RecyclerView.Adapter<RecyclerView.ViewHolder> recyclerAdapter,
            boolean isFixedSize,
            boolean isAutoMeasureEnabled,
            boolean isNestedScrollingEnabled) {
        recyclerView.setHasFixedSize(isFixedSize);
        layoutManager.setAutoMeasureEnabled(isAutoMeasureEnabled);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setNestedScrollingEnabled(isNestedScrollingEnabled);
        recyclerView.setAdapter(recyclerAdapter);

    }

    public void setRecyclerViewWithAdapter(RecyclerView recyclerView,
            RecyclerView.LayoutManager layoutManager,
            RecyclerView.Adapter<RecyclerView.ViewHolder> recyclerAdapter,
            boolean isFixedSize,
            boolean isAutoMeasureEnabled,
            boolean isNestedScrollingEnabled,
            RecyclerView.ItemDecoration dividerItemDecoration) {
        recyclerView.setHasFixedSize(isFixedSize);
        layoutManager.setAutoMeasureEnabled(isAutoMeasureEnabled);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setNestedScrollingEnabled(isNestedScrollingEnabled);
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setAdapter(recyclerAdapter);

    }



    public void showProgressDialogSimple(Boolean isShow) {
        try {
            if (progressDialog != null &&
                    progressDialog.isShowing())
                progressDialog.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
        progressDialog = new ProgressDialog(this);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Please Wait...");

        if (isShow)
            progressDialog.show();
        else
            progressDialog.dismiss();

    }

    public void hideProgressDialogSimple() {
        progressDialog.dismiss();
    }



    public boolean isValidString(String value) {
        if (TextUtils.isEmpty(value)) {
            return false;
        } else {
            return true;
        }
    }

}
