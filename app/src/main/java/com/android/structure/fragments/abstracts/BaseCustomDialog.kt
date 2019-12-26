package com.android.structure.fragments.abstracts

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.util.Pair
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.DialogFragment
import com.android.structure.BaseApplication
import com.android.structure.R
import com.android.structure.activities.BaseActivity
import com.android.structure.activities.HomeActivity
import com.android.structure.activities.MainActivity
import com.android.structure.callbacks.OnNewPacketReceivedListener
import com.android.structure.constatnts.AppConstants
import com.android.structure.helperclasses.ui.helper.KeyboardHelper
import com.android.structure.helperclasses.ui.helper.UIHelper
import com.android.structure.libraries.residemenu.ResideMenu
import com.android.structure.managers.DateManager
import com.android.structure.managers.FileManager
import com.android.structure.managers.SharedPreferenceManager
import com.android.structure.models.receiving_model.UserModel
import com.android.structure.models.wrappers.WebResponse
import com.android.structure.widget.TitleBar
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import java.io.File

abstract class BaseCustomDialog : DialogFragment(), OnNewPacketReceivedListener {

    var sharedPreferenceManager: SharedPreferenceManager? = null
    var TAG = "Logging Tag"
    var onCreated = false
    var subscription: Disposable? = null

    protected abstract val fragmentLayout: Int
    protected abstract val fragmentSizeMatchParent: Boolean

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view = inflater.inflate(fragmentLayout, container, false)
        return view
    }

    open fun getCurrentUser(): UserModel? {
        return sharedPreferenceManager!!.currentUser
    }

    open fun getToken(): String? {
        return sharedPreferenceManager!!.getString(AppConstants.KEY_TOKEN)
    }

    open fun getOneTimeToken(): String? {
        return sharedPreferenceManager!!.getString(AppConstants.KEY_ONE_TIME_TOKEN)
    }

    open fun putOneTimeToken(token: String?) {
        sharedPreferenceManager!!.putValue(AppConstants.KEY_ONE_TIME_TOKEN, token)
    }


    // Use  UIHelper.showSpinnerDialog
    @Deprecated("")
    open fun setSpinner(adaptSpinner: ArrayAdapter<*>?, textView: TextView?, spinner: Spinner?) {
        if (adaptSpinner == null || spinner == null) return
        //selected item will look like a spinner set from XML
//        simple_list_item_single_choice
        adaptSpinner.setDropDownViewResource(android.R.layout.simple_list_item_single_choice)
        spinner.adapter = adaptSpinner
        spinner.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View, position: Int, id: Long) {
                val str = spinner.getItemAtPosition(position).toString()
                if (textView != null) textView.text = str
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    open fun getBaseActivity(): BaseActivity? {
        return activity as BaseActivity?
    }

    open fun getHomeActivity(): HomeActivity? {
        return activity as HomeActivity?
    }

    abstract fun setTitlebar(titleBar: TitleBar?)

    abstract fun getDrawerLockMode(): Int

    abstract fun setListeners()

    override fun onResume() {
        super.onResume()
        onCreated = true
        setListeners()
        if (getBaseActivity() != null) {
            setTitlebar(getBaseActivity()!!.titleBar)
        }
        if (getBaseActivity() != null && getBaseActivity()!!.window.decorView != null) {
            KeyboardHelper.hideSoftKeyboard(getBaseActivity(), getBaseActivity()!!.window.decorView)
        }
    }

    override fun onPause() {
        if (getBaseActivity() != null && getBaseActivity()!!.window.decorView != null) {
            KeyboardHelper.hideSoftKeyboard(getBaseActivity(), getBaseActivity()!!.window.decorView)
        }
        super.onPause()
    }


    open fun notifyToAll(event: Int, data: Any) {
        BaseApplication.getPublishSubject().onNext(Pair(event, data))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.e("abc", "onDestroyView")
        if (subscription != null) subscription!!.dispose()
    }


    override fun onStart() {
        super.onStart()

        if (fragmentSizeMatchParent) {
            dialog.window
                    .setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getBaseActivity()!!.titleBar.resetViews()
        getBaseActivity()!!.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED) // Default Locked in this project

        getBaseActivity()!!.drawerLayout.closeDrawer(GravityCompat.START)

        subscribeToNewPacket(this)
    }

    protected open fun subscribeToNewPacket(newPacketReceivedListener: OnNewPacketReceivedListener) {
        subscription = BaseApplication.getPublishSubject()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { pair ->
                    Log.e("abc", "on accept")
                    newPacketReceivedListener.onNewPacket(pair.first as Int, pair.second)
                }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.DialogTheme)
        sharedPreferenceManager = SharedPreferenceManager.getInstance(context)
        onCreated = false
    }

    open fun BaseCustomDialog() {}


    open fun showNextBuildToast() {
        UIHelper.showToast(context, "This feature is in progress")
    }


    open fun saveAndOpenFile(webResponse: WebResponse<String?>) {
        val fileName = AppConstants.FILE_NAME + DateManager.getTime(DateManager.getCurrentMillis()) + ".pdf"
        val path = FileManager.writeResponseBodyToDisk(context, webResponse.result, fileName, AppConstants.getUserFolderPath(context), true, true)
        //                                final File file = new File(AppConstants.getUserFolderPath(getContext())
//                                        + "/" + fileName + ".pdf");
        val file = File(path)
        Handler().postDelayed({ FileManager.openFile(context, file) }, 100)
    }

    override fun onNewPacket(event: Int, data: Any?) {
        when (event) {
        }
    }


    open fun getResideMenu(): ResideMenu? {
        return getHomeActivity()!!.resideMenu
    }


    // FOR RESIDE MENU
    open fun closeMenu() {
        getHomeActivity()!!.resideMenu.closeMenu()
    }


    open fun logoutClick(baseFragment: BaseFragment) {
        val context = baseFragment.context
        val genericDialogFragment = GenericDialogFragment.newInstance()
        genericDialogFragment.setTitle("Logout")
        genericDialogFragment.setMessage(context!!.getString(R.string.areYouSureToLogout))
        genericDialogFragment.setButton1("Yes") {
            genericDialogFragment.dismiss()
            baseFragment.sharedPreferenceManager.clearDB()
            baseFragment.baseActivity.clearAllActivitiesExceptThis(MainActivity::class.java)
        }
        genericDialogFragment.setButton2("No") { genericDialogFragment.dialog.dismiss() }
        genericDialogFragment.show(baseFragment.baseActivity.supportFragmentManager, null)
    }

}