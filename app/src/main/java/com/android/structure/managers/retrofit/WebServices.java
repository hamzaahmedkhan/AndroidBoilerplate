package com.android.structure.managers.retrofit;

import android.app.Activity;
import android.content.Intent;


import com.android.structure.activities.BaseActivity;
import com.android.structure.activities.HomeActivity;
import com.android.structure.activities.MainActivity;
import com.android.structure.constatnts.AppConstants;
import com.android.structure.constatnts.WebServiceConstants;
import com.android.structure.enums.BaseURLTypes;
import com.android.structure.enums.FileType;
import com.android.structure.helperclasses.Helper;
import com.android.structure.helperclasses.ui.helper.UIHelper;
import com.android.structure.managers.FileManager;
import com.android.structure.managers.SharedPreferenceManager;
import com.android.structure.managers.retrofit.entities.MultiFileModel;
import com.android.structure.models.wrappers.UserModelWrapper;
import com.android.structure.models.wrappers.WebResponse;
import com.google.android.gms.common.ErrorDialogFragment;
import com.kaopiz.kprogresshud.KProgressHUD;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Converter;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.android.structure.constatnts.WebServiceConstants.BASE_URL;

/**
 * Created by hamzakhan on 6/30/2017.
 */

public class WebServices {
    private WebServiceProxy apiService;
    private KProgressHUD mDialog;
    private Activity activity;

    public WebServices(Activity activity, String token, BaseURLTypes baseURLTypes) {
        switch (baseURLTypes) {

            case BASE_URL:
                apiService = WebServiceFactory.getInstanceBaseURL(token);
                break;
            case XML_URL:
                apiService = WebServiceFactory.getInstanceXML();
        }


        this.activity = activity;
        mDialog = UIHelper.getProgressHUD(this.activity);
        if (!((Activity) this.activity).isFinishing())
            mDialog.show();
    }

    public WebServices(Activity activity, String token, BaseURLTypes baseURLTypes, boolean isShowLoader) {
        switch (baseURLTypes) {
            case BASE_URL:
                apiService = WebServiceFactory.getInstanceBaseURL(token);
                break;
            case XML_URL:
                apiService = WebServiceFactory.getInstanceXML();
        }

        this.activity = activity;

        if (isShowLoader) {
            mDialog = UIHelper.getProgressHUD(this.activity);

            if (!((Activity) this.activity).isFinishing())
                mDialog.show();
        }

    }


    private static boolean IsResponseError(Response<WebResponse<Object>> response) {
        return !(response != null && !response.isSuccessful() && response.errorBody() != null);
    }


    private boolean hasValidStatus(Response<WebResponse<Object>> response) {
        if (response != null && response.body() != null) {
            return response.body().isSuccess();
        } else {
            return false;
        }
    }


    /**
     * TO UPLOAD FILE
     *
     * @param multiFileModelArrayList
     * @param jsonStringBody
     * @param callBack
     */

    public void postMultipartAPIWithSameKeyAttachments(String path, ArrayList<MultiFileModel> multiFileModelArrayList, String jsonStringBody,
                                                       final IRequestWebResponseAnyObjectCallBack callBack) {

        ArrayList<MultipartBody.Part> partArrayList = new ArrayList<>();

        MultipartBody.Part[] attachments = new MultipartBody.Part[multiFileModelArrayList.size()];


        if (jsonStringBody != null && !jsonStringBody.isEmpty()) {
            try {
                JSONObject jsonObject = new JSONObject(jsonStringBody);
                Iterator<?> keys = jsonObject.keys();
                while (keys.hasNext()) {
                    String key = (String) keys.next();
                    String value = jsonObject.getString(key);
                    partArrayList.add(MultipartBody.Part.createFormData(key, value));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        if (!multiFileModelArrayList.isEmpty()) {
            for (int i = 0; i < multiFileModelArrayList.size(); i++) {
                if (multiFileModelArrayList.get(i).getFile() == null || !multiFileModelArrayList.get(i).getFile().exists()) {
                    dismissDialog();
                    UIHelper.showShortToastInCenter(activity, "File is empty.");
                    return;
                }

                MultipartBody.Part multipart = getMultipart(multiFileModelArrayList.get(i).getFileType(), multiFileModelArrayList.get(i).getFile(), multiFileModelArrayList.get(i).getKeyName());
                attachments[i] = multipart;
            }
        }


        try {
            if (Helper.isNetworkConnected(activity, true)) {
                apiService.postMultipartWithSameKeyAPI(path, partArrayList, attachments).enqueue(
                        new Callback<WebResponse<Object>>() {
                            @Override
                            public void onResponse(Call<WebResponse<Object>> call, Response<WebResponse<Object>> response) {
                                validateIfWebResponse(response, callBack);
                            }

                            @Override
                            public void onFailure(Call<WebResponse<Object>> call, Throwable t) {
                                UIHelper.showShortToastInCenter(activity, "Something went wrong, Please check your internet connection.");
                                dismissDialog();
                                callBack.onError("");
                            }
                        });
            } else {
                dismissDialog();
                callBack.onError("Internet Error");
            }

        } catch (
                Exception e) {
            dismissDialog();
            e.printStackTrace();

        }
    }


    /**
     * TO UPLOAD FILE
     *
     * @param multiFileModelArrayList
     * @param jsonStringBody
     * @param callBack
     */

    public void putMultipartAPI(String path, ArrayList<MultiFileModel> multiFileModelArrayList, String jsonStringBody,
                                final IRequestWebResponseAnyObjectCallBack callBack) {

        ArrayList<MultipartBody.Part> partArrayList = new ArrayList<>();


        if (jsonStringBody != null && !jsonStringBody.isEmpty()) {
            try {
                JSONObject jsonObject = new JSONObject(jsonStringBody);
                Iterator<?> keys = jsonObject.keys();
                while (keys.hasNext()) {
                    String key = (String) keys.next();
                    String value = jsonObject.getString(key);
                    partArrayList.add(MultipartBody.Part.createFormData(key, value));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        if (multiFileModelArrayList != null && !multiFileModelArrayList.isEmpty()) {
            for (MultiFileModel multiFileModel : multiFileModelArrayList) {
                if (multiFileModel.getFile() == null || !multiFileModel.getFile().exists()) {
                    dismissDialog();
                    UIHelper.showShortToastInCenter(activity, "File is empty.");
                    return;
                }

                MultipartBody.Part multipart = getMultipart(multiFileModel.getFileType(), multiFileModel.getFile(), multiFileModel.getKeyName());
                partArrayList.add(multipart);
            }
        }

        // Method Spoofing
        partArrayList.add(MultipartBody.Part.createFormData("_method", "PUT"));


        try {
            if (Helper.isNetworkConnected(activity, true)) {
                apiService.postMultipartAPI(path, partArrayList).enqueue(
                        new Callback<WebResponse<Object>>() {
                            @Override
                            public void onResponse(Call<WebResponse<Object>> call, Response<WebResponse<Object>> response) {
                                validateIfWebResponse(response, callBack);
                            }

                            @Override
                            public void onFailure(Call<WebResponse<Object>> call, Throwable t) {
                                UIHelper.showShortToastInCenter(activity, "Something went wrong, Please check your internet connection.");
                                dismissDialog();
                                callBack.onError("");
                            }
                        });
            } else {
                dismissDialog();
                callBack.onError("Internet Error");
            }

        } catch (
                Exception e) {
            dismissDialog();
            e.printStackTrace();

        }
    }

    /**
     * TO UPLOAD FILE
     *
     * @param multiFileModelArrayList
     * @param jsonStringBody
     * @param callBack
     */

    public void postMultipartAPI(String path, ArrayList<MultiFileModel> multiFileModelArrayList, String jsonStringBody,
                                 final IRequestWebResponseAnyObjectCallBack callBack) {

        ArrayList<MultipartBody.Part> partArrayList = new ArrayList<>();


        if (jsonStringBody != null && !jsonStringBody.isEmpty()) {
            try {
                JSONObject jsonObject = new JSONObject(jsonStringBody);
                Iterator<?> keys = jsonObject.keys();
                while (keys.hasNext()) {
                    String key = (String) keys.next();
                    String value = jsonObject.getString(key);
                    partArrayList.add(MultipartBody.Part.createFormData(key, value));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        if (multiFileModelArrayList != null && !multiFileModelArrayList.isEmpty()) {
            for (MultiFileModel multiFileModel : multiFileModelArrayList) {
                if (multiFileModel.getFile() == null || !multiFileModel.getFile().exists()) {
                    dismissDialog();
                    UIHelper.showShortToastInCenter(activity, "File is empty.");
                    return;
                }

                MultipartBody.Part multipart = getMultipart(multiFileModel.getFileType(), multiFileModel.getFile(), multiFileModel.getKeyName());
                partArrayList.add(multipart);
            }
        }


        try {
            if (Helper.isNetworkConnected(activity, true)) {
                apiService.postMultipartAPI(path, partArrayList).enqueue(
                        new Callback<WebResponse<Object>>() {
                            @Override
                            public void onResponse(Call<WebResponse<Object>> call, Response<WebResponse<Object>> response) {
                                validateIfWebResponse(response, callBack);
                            }

                            @Override
                            public void onFailure(Call<WebResponse<Object>> call, Throwable t) {
                                UIHelper.showShortToastInCenter(activity, "Something went wrong, Please check your internet connection.");
                                dismissDialog();
                                callBack.onError("");
                            }
                        });
            } else {
                dismissDialog();
                callBack.onError("Internet Error");
            }

        } catch (
                Exception e) {
            dismissDialog();
            e.printStackTrace();

        }
    }


    /**
     * WEB CALL DELETE
     *
     * @param path
     * @param requestData can give null or empty
     * @param callBack
     * @return
     */

    public Call<WebResponse<Object>> deleteAPIAnyObject(String path, String requestData, final IRequestWebResponseAnyObjectCallBack callBack) {
//        RequestBody bodyRequestData = getRequestBody(okhttp3.MediaType.parse("application/json; charset=utf-8"), requestData);

//        MultipartBody.Part multipartBody = MultipartBody.Part.create(bodyRequestData);

        Call<WebResponse<Object>> webResponseCall = apiService.deleteAPIWebResponseAnyObject(path);

        try {
            if (Helper.isNetworkConnected(activity, true)) {
                webResponseCall.enqueue(new Callback<WebResponse<Object>>() {
                    @Override
                    public void onResponse(Call<WebResponse<Object>> call, Response<WebResponse<Object>> response) {
                        validateIfWebResponse(response, callBack);
                    }

                    @Override
                    public void onFailure(Call<WebResponse<Object>> call, Throwable t) {
                        UIHelper.showShortToastInCenter(activity, "Something went wrong, Please check your internet connection.");
                        dismissDialog();
                        callBack.onError("");
                    }
                });
            } else {
                dismissDialog();
            }

        } catch (Exception e) {
            dismissDialog();
            e.printStackTrace();

        }

        return webResponseCall;
    }


    /**
     * WEB CALL POST
     *
     * @param path
     * @param requestData
     * @param callBack
     * @return
     */

    public Call<WebResponse<Object>> postAPIAnyObject(String path, String requestData, final IRequestWebResponseAnyObjectCallBack callBack) {
        RequestBody bodyRequestData = getRequestBody(okhttp3.MediaType.parse("application/json; charset=utf-8"), requestData);

//        MultipartBody.Part multipartBody = MultipartBody.Part.create(bodyRequestData);

        Call<WebResponse<Object>> webResponseCall = apiService.postAPIWebResponseAnyObject(path, bodyRequestData);

        try {
            if (Helper.isNetworkConnected(activity, true)) {
                webResponseCall.enqueue(new Callback<WebResponse<Object>>() {
                    @Override
                    public void onResponse(Call<WebResponse<Object>> call, Response<WebResponse<Object>> response) {
                        validateIfWebResponse(response, callBack);
                    }

                    @Override
                    public void onFailure(Call<WebResponse<Object>> call, Throwable t) {
                        UIHelper.showShortToastInCenter(activity, "Something went wrong, Please check your internet connection.");
                        dismissDialog();
                        callBack.onError("");
                    }
                });
            } else {
                dismissDialog();
            }

        } catch (Exception e) {
            dismissDialog();
            e.printStackTrace();

        }

        return webResponseCall;
    }


    /**
     * WEB CALL GET
     *
     * @param path
     * @param queryMap
     * @param callBack
     * @return
     */
    public Call<WebResponse<Object>> getAPIAnyObject(String path, Map<String, Object> queryMap, final IRequestWebResponseAnyObjectCallBack callBack) {

        Call<WebResponse<Object>> webResponseCall = apiService.getAPIForWebresponseAnyObject(path, queryMap);


        try {
            if (Helper.isNetworkConnected(activity, true)) {
                webResponseCall.enqueue(new Callback<WebResponse<Object>>() {
                    @Override
                    public void onResponse(Call<WebResponse<Object>> call, Response<WebResponse<Object>> response) {
                        validateIfWebResponse(response, callBack);
                    }

                    @Override
                    public void onFailure(Call<WebResponse<Object>> call, Throwable t) {
                        UIHelper.showShortToastInCenter(activity, "Something went wrong, Please check your internet connection.");
                        dismissDialog();
                        callBack.onError("");
                    }
                });
            } else {
                dismissDialog();
            }

        } catch (Exception e) {
            dismissDialog();
            e.printStackTrace();

        }

        return webResponseCall;
    }


    public void validateIfWebResponse(Response<WebResponse<Object>> response, IRequestWebResponseAnyObjectCallBack callBack) {

        dismissDialog();
        if (response.body() == null) {

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(GsonFactory.getSimpleGson()))
                    .build();

            Converter<ResponseBody, WebResponse<Object>> errorConverter =
                    retrofit.responseBodyConverter(WebResponse.class, new Annotation[0]);
            WebResponse<Object> error = null;

            try {
                error = errorConverter.convert(response.errorBody());
            } catch (IOException e) {
                e.printStackTrace();
            }


            if (response.code() == WebServiceConstants.PARAMS_TOKEN_EXPIRE) {
//                UIHelper.showToast(activity, "TOKEN ERROR " + PARAMS_TOKEN_EXPIRE);
                tokenRefresh();


            } else if (response.code() == WebServiceConstants.PARAMS_TOKEN_BLACKLIST) {
                UIHelper.showAlertDialog(activity, "Token Authentication Failed, Kindly login again");
                SharedPreferenceManager.getInstance(activity).clearDB();
                clearAllActivitiesExceptThis(MainActivity.class);
            } else {
                errorToastForObject(error, true);
            }


            callBack.onError(error);

            return;
        }

        if (response.isSuccessful() && response.body().isSuccess()) {
            if (callBack != null)
                callBack.requestDataResponse(response.body());
        } else {
            callBack.onError(errorToastForObject(response));
        }
    }

    public void tokenRefresh() {
        RequestBody bodyRequestData = getRequestBody(okhttp3.MediaType.parse("application/json; charset=utf-8"), "");

        Call<WebResponse<Object>> webResponseCall = apiService.postAPIWebResponseAnyObject(WebServiceConstants.PATH_GET_REFRESH, bodyRequestData);

        try {
            if (Helper.isNetworkConnected(activity, true)) {
                webResponseCall.enqueue(new Callback<WebResponse<Object>>() {
                    @Override
                    public void onResponse(Call<WebResponse<Object>> call, Response<WebResponse<Object>> response) {

                        if (response.body() == null) {
                            UIHelper.showAlertDialog(activity, "Token Authentication Failed, Kindly login again");
                            SharedPreferenceManager.getInstance(activity).clearDB();
                            clearAllActivitiesExceptThis(MainActivity.class);
                            return;
                        }

                        if (response.isSuccessful() && response.body().isSuccess()) {
                            SharedPreferenceManager sharedPreferenceManager = SharedPreferenceManager.getInstance(activity);
                            UserModelWrapper userModelWrapper = GsonFactory.getSimpleGson().fromJson(GsonFactory.getSimpleGson().toJson(response.body().result), UserModelWrapper.class);

                            sharedPreferenceManager.putObject(AppConstants.KEY_CURRENT_USER_MODEL, userModelWrapper.getUser());
                            sharedPreferenceManager.putValue(AppConstants.KEY_TOKEN, userModelWrapper.getUser().getAccessToken());
//
                            UIHelper.showAlertDialog(activity, "Token refreshed successfully");
                            if (activity instanceof HomeActivity) {
                                reload();
                            } else {
                                clearAllActivitiesExceptThis(HomeActivity.class);
                            }
                        } else {
                            UIHelper.showAlertDialog(activity, "Token Authentication Failed, Kindly login again");
                            SharedPreferenceManager.getInstance(activity).clearDB();
                            clearAllActivitiesExceptThis(MainActivity.class);
                        }

                    }

                    @Override
                    public void onFailure(Call<WebResponse<Object>> call, Throwable t) {
                        dismissDialog();
                        UIHelper.showAlertDialog(activity, "Token Authentication Failed, Kindly login again");
                        SharedPreferenceManager.getInstance(activity).clearDB();
                        clearAllActivitiesExceptThis(MainActivity.class);
                    }
                });
            } else {
                dismissDialog();
            }

        } catch (Exception e) {
            dismissDialog();
            e.printStackTrace();

        }
    }

    public static MultipartBody.Part getMultipart(FileType fileType, File file, String keyName) {
        return MultipartBody.Part.createFormData(keyName, file.getName(),
                RequestBody.create(MediaType.parse(fileType.canonicalForm() + "/" + FileManager.getExtension(file.getName())), file)
        );
    }


    private RequestBody getRequestBody(MediaType form, String trim) {
        return RequestBody.create(
                form, trim);
    }


    private void dismissDialog() {
        if (mDialog != null) {
            mDialog.dismiss();
        }
    }


    private String errorToastForObject(WebResponse<Object> response, boolean showDialogIfTrueToastIfFalse) {
        String responseMessage = "";

        if (response != null) {
            responseMessage = response.message;
        }

        if (responseMessage.isEmpty()) {
            UIHelper.showShortToastInCenter(activity, "API Response Error");
        } else {

            UIHelper.showShortToastInCenter(activity, responseMessage);
//            if (showDialogIfTrueToastIfFalse && activity instanceof BaseActivity && response.errorList != null && !response.errorList.isEmpty()) {
//                ErrorDialogFragment.newInstance(response.errorList, null).show(((BaseActivity) activity).getSupportFragmentManager(), "ErrorDialogFragment");
//            } else {
//
//            }
        }
        return responseMessage;
    }


    private String errorToastForObject(Response<WebResponse<Object>> response) {
        String responseMessage = "";

        if (response.body() != null) {
            responseMessage = response.body().message != null ? response.body().message : response.errorBody().toString();
        }

        if (responseMessage.isEmpty()) {
            UIHelper.showShortToastInCenter(activity, "API Response Error ");
        } else {
            UIHelper.showShortToastInCenter(activity, responseMessage);
        }
        return responseMessage;
    }


    public interface IRequestWebResponseAnyObjectCallBack {
        void requestDataResponse(WebResponse<Object> webResponse);

        void onError(Object object);
    }

    public interface IRequestWebResponseJustObjectCallBack {
        void requestDataResponse(Object webResponse);

        void onError(Object object);
    }


    public void reload() {
        Intent intent = activity.getIntent();
        activity.overridePendingTransition(0, 0);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        activity.finish();
        activity.overridePendingTransition(0, 0);
        activity.startActivity(intent);
    }


    public void clearAllActivitiesExceptThis(Class<?> cls) {
        Intent intents = new Intent(activity, cls);
        intents.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        activity.startActivity(intents);
        activity.finish();
    }


}
