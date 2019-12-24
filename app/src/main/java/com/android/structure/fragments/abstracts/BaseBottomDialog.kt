package com.android.structure.fragments.abstracts

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.res.Resources
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.drawerlayout.widget.DrawerLayout
import com.android.structure.BaseApplication
import com.android.structure.R
import com.android.structure.activities.MainActivity
import com.android.structure.callbacks.OnNewPacketReceivedListener
import com.android.structure.constatnts.AppConstants
import com.android.structure.helperclasses.ui.helper.UIHelper
import com.android.structure.libraries.residemenu.ResideMenu
import com.android.structure.managers.DateManager
import com.android.structure.managers.FileManager
import com.android.structure.managers.SharedPreferenceManager
import com.android.structure.models.wrappers.WebResponse
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import java.io.File


abstract class BaseBottomDialog : AppCompatDialogFragment(), AdapterView.OnItemClickListener, OnNewPacketReceivedListener {

    var sharedPreferenceManager: SharedPreferenceManager? = null
    var TAG = "Logging Tag"
    var onCreated = false
    var subscription: Disposable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferenceManager = SharedPreferenceManager.getInstance(context)
        onCreated = false
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view = inflater.inflate(fragmentLayout, container, false)
        return view
    }
    override fun onDestroyView() {
        super.onDestroyView()
        Log.e("abc", "onDestroyView")
        if (subscription != null) subscription!!.dispose()
    }

    open fun saveAndOpenFile(webResponse: WebResponse<String?>) {
        val fileName = AppConstants.FILE_NAME + DateManager.getTime(DateManager.getCurrentMillis()) + ".pdf"
        val path = FileManager.writeResponseBodyToDisk(context, webResponse.result, fileName, AppConstants.getUserFolderPath(context), true, true)
        //                                final File file = new File(AppConstants.getUserFolderPath(getContext())
//                                        + "/" + fileName + ".pdf");
        val file = File(path)
        Handler().postDelayed({ FileManager.openFile(context, file) }, 100)
    }


    open fun showNextBuildToast() {
        UIHelper.showToast(context, "This feature is in progress")
    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return BottomSheetDialog(this.context!!, this.theme)
    }

    protected abstract val fragmentLayout: Int
    private val height: Int
        private get() = Resources.getSystem().displayMetrics.heightPixels

    override fun setupDialog(dialog: Dialog, style: Int) { //Set the custom view
        val view = LayoutInflater.from(context).inflate(fragmentLayout, null)
        dialog.setContentView(view)
        val v = view.parent as View
        val params = v.layoutParams as CoordinatorLayout.LayoutParams
        params.height = height

        val behavior = params.behavior


        if (behavior is BottomSheetBehavior<*>) {
            behavior.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                @SuppressLint("SwitchIntDef")
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    var state = ""
                    when (newState) {
                        BottomSheetBehavior.STATE_DRAGGING -> {
                            state = "DRAGGING"
                        }
                        BottomSheetBehavior.STATE_SETTLING -> {
                            state = "SETTLING"
                        }
                        BottomSheetBehavior.STATE_EXPANDED -> {
                            state = "EXPANDED"
                        }
                        BottomSheetBehavior.STATE_COLLAPSED -> {
                            state = "COLLAPSED"
                        }
                        BottomSheetBehavior.STATE_HIDDEN -> {
                            dismiss()
                            behavior.state = BottomSheetBehavior.STATE_COLLAPSED
                            state = "HIDDEN"
                        }
                    }
                    //                    Toast.makeText(getContext(), "Bottom Sheet State Changed to: " + state, Toast.LENGTH_SHORT).show();
                }

                override fun onSlide(bottomSheet: View, slideOffset: Float) {}
            })
        }


    }
    override fun onNewPacket(event: Int, data: Any?) {
        when (event) {
        }
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