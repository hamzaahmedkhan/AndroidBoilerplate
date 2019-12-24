package com.android.structure.fragments.abstracts;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.Nullable;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.drawerlayout.widget.DrawerLayout;
import android.util.Log;
import android.util.Pair;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.structure.BaseApplication;
import com.android.structure.activities.HomeActivity;
import com.android.structure.activities.MainActivity;
import com.android.structure.callbacks.GenericClickableInterface;
import com.android.structure.constatnts.AppConstants;
import com.android.structure.helperclasses.ui.helper.KeyboardHelper;
import com.android.structure.helperclasses.ui.helper.UIHelper;
import com.android.structure.libraries.residemenu.ResideMenu;
import com.android.structure.managers.DateManager;
import com.android.structure.managers.FileManager;
import com.android.structure.managers.SharedPreferenceManager;
import com.android.structure.models.receiving_model.UserModel;
import com.android.structure.models.wrappers.WebResponse;

import java.io.File;

import com.android.structure.R;
import com.android.structure.activities.BaseActivity;
import com.android.structure.callbacks.OnNewPacketReceivedListener;
import com.android.structure.widget.TitleBar;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;


/**
 * Created by khanhamza on 10-Feb-17.
 */

public abstract class BaseFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemClickListener, OnNewPacketReceivedListener {

    protected View view;
    public SharedPreferenceManager sharedPreferenceManager;
    public String TAG = "Logging Tag";
    public boolean onCreated = false;
    Disposable subscription;


    /**
     * This is an abstract class, we should inherit our fragment from this class
     */

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferenceManager = SharedPreferenceManager.getInstance(getContext());
        onCreated = false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(getFragmentLayout(), container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getBaseActivity().getTitleBar().resetViews();
        getBaseActivity().getDrawerLayout().setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);   // Default Locked in this project
        getBaseActivity().getDrawerLayout().closeDrawer(GravityCompat.START);

        subscribeToNewPacket(this);

    }

    public UserModel getCurrentUser() {
        return sharedPreferenceManager.getCurrentUser();
    }

    public String getToken() {
        return sharedPreferenceManager.getString(AppConstants.KEY_TOKEN);
    }

    public String getOneTimeToken() {
        return sharedPreferenceManager.getString(AppConstants.KEY_ONE_TIME_TOKEN);
    }

    public void putOneTimeToken(String token) {
        sharedPreferenceManager.putValue(AppConstants.KEY_ONE_TIME_TOKEN, token);
    }

    public abstract int getDrawerLockMode();


    // Use  UIHelper.showSpinnerDialog
    @Deprecated
    public void setSpinner(ArrayAdapter adaptSpinner, final TextView textView, final Spinner spinner) {
        if (adaptSpinner == null || spinner == null)
            return;
        //selected item will look like a spinner set from XML
//        simple_list_item_single_choice
        adaptSpinner.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
        spinner.setAdapter(adaptSpinner);


        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String str = spinner.getItemAtPosition(position).toString();
                if (textView != null)
                    textView.setText(str);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }


    protected abstract int getFragmentLayout();

    public BaseActivity getBaseActivity() {
        return (BaseActivity) getActivity();
    }

    public HomeActivity getHomeActivity() {
        return (HomeActivity) getActivity();
    }

    public abstract void setTitlebar(TitleBar titleBar);


    public abstract void setListeners();

    @Override
    public void onResume() {
        super.onResume();
        onCreated = true;
        setListeners();

        if (getBaseActivity() != null) {
            setTitlebar(getBaseActivity().getTitleBar());
        }

        if (getBaseActivity() != null && getBaseActivity().getWindow().getDecorView() != null) {
            KeyboardHelper.hideSoftKeyboard(getBaseActivity(), getBaseActivity().getWindow().getDecorView());
        }

    }

    @Override
    public void onPause() {

        if (getBaseActivity() != null && getBaseActivity().getWindow().getDecorView() != null) {
            KeyboardHelper.hideSoftKeyboard(getBaseActivity(), getBaseActivity().getWindow().getDecorView());
        }

        super.onPause();

    }


    public void notifyToAll(int event, Object data) {
        BaseApplication.getPublishSubject().onNext(new Pair<>(event, data));
    }

    protected void subscribeToNewPacket(final OnNewPacketReceivedListener newPacketReceivedListener) {
        subscription = BaseApplication.getPublishSubject()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Pair>() {
                    @Override
                    public void accept(@NonNull Pair pair) throws Exception {
                        Log.e("abc", "on accept");
                        newPacketReceivedListener.onNewPacket((int) pair.first, pair.second);
                    }
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.e("abc", "onDestroyView");
        if (subscription != null)
            subscription.dispose();
    }


    public void showNextBuildToast() {
        UIHelper.showToast(getContext(), "This feature is in progress");
    }


    public void saveAndOpenFile(WebResponse<String> webResponse) {
        String fileName = AppConstants.FILE_NAME + DateManager.getTime(DateManager.getCurrentMillis()) + ".pdf";

        String path = FileManager.writeResponseBodyToDisk(getContext(), webResponse.result, fileName, AppConstants.getUserFolderPath(getContext()), true, true);

//                                final File file = new File(AppConstants.getUserFolderPath(getContext())
//                                        + "/" + fileName + ".pdf");
        final File file = new File(path);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                FileManager.openFile(getContext(), file);
            }
        }, 100);
    }

    @Override
    public void onNewPacket(int event, Object data) {
        switch (event) {

        }
    }


    public ResideMenu getResideMenu() {
        return getHomeActivity().getResideMenu();
    }


    // FOR RESIDE MENU
    public void closeMenu() {
        getHomeActivity().getResideMenu().closeMenu();
    }



    public static void logoutClick(final BaseFragment baseFragment) {
        Context context = baseFragment.getContext();

        final GenericDialogFragment genericDialogFragment = GenericDialogFragment.newInstance();

        genericDialogFragment.setTitle("Logout");
        genericDialogFragment.setMessage(context.getString(R.string.areYouSureToLogout));
        genericDialogFragment.setButton1("Yes", new GenericClickableInterface() {
            @Override
            public void click() {
                genericDialogFragment.dismiss();
                baseFragment.sharedPreferenceManager.clearDB();
                baseFragment.getBaseActivity().clearAllActivitiesExceptThis(MainActivity.class);
            }
        });

        genericDialogFragment.setButton2("No", new GenericClickableInterface() {
            @Override
            public void click() {
                genericDialogFragment.getDialog().dismiss();
            }
        });
        genericDialogFragment.show(baseFragment.getBaseActivity().getSupportFragmentManager(), null);


    }
}
