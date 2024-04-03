package com.capcorp.utils.facebook;

import com.facebook.FacebookException;
import com.facebook.GraphResponse;

import org.json.JSONObject;

/**
 * Created by ABC on 07-Dec-15.
 */
public interface FacebookLoginListener {

    void onFbLoginSuccess();

    void onFbLoginCancel();

    void onFbLoginError(FacebookException exception);

    void onGetprofileSuccess(JSONObject object, GraphResponse response);
}
