package com.niucong.punchcardclient;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.niucong.punchcardclient.app.App;
import com.niucong.punchcardclient.net.ApiCallback;
import com.niucong.punchcardclient.net.bean.BasicBean;

import java.util.HashMap;
import java.util.Map;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends BasicActivity {

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;

    private EditText etIp, etPort;
    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

        etIp = (EditText) findViewById(R.id.ip);
        etPort = (EditText) findViewById(R.id.port);

        Log.d("LoginActivity", "userId=" + App.sp.getInt("userId", 0));
        if (App.sp.getInt("userId", 0) > 0) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        String ip = etIp.getText().toString();
        String port = etPort.getText().toString();
        if (TextUtils.isEmpty(ip)) {
            App.showToast("服务器IP地址不能为空");
            return;
        }
        if (TextUtils.isEmpty(port)) {
            App.showToast("服务器端口号不能为空");
            return;
        }
        url = "http://" + ip + ":" + port + "/";
        App.sp.putString("url", url);
        App.sp.commit();

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            return;
        }

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            return;
        }

        // Show a progress spinner, and kick off a background task to
        // perform the user login attempt.
        showProgress(true);
        Map<String, String> fields = new HashMap<>();
//        fields.put("username", email);
//        fields.put("password", password);
//        fields.put("bmobID", App.sp.getString("bmobID", ""));
//        addSubscription(getApi().login(fields), new ApiCallback<LoginBean>() {
//            @Override
//            public void onSuccess(LoginBean model) {
//                showProgress(false);
//                if (model != null) {
//                    App.showToast("" + model.getMsg());
//                    if (model.getCode() == 1) {
//                        Log.d("LoginActivity", "userId=" + model.getMemberId());
//                        App.sp.putInt("userId", model.getMemberId());
//                        App.sp.putInt("type", model.getType());
//                        App.sp.commit();
//                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
//                        finish();
//                    }
//                } else {
//                    App.showToast("接口错误" + (model == null));
//                }
//            }
//
//            @Override
//            public void onFailure(String msg) {
//                Log.d("LoginActivity", "失败了,如下 : " + msg);
//            }
//
//            @Override
//            public void onFinish() {
//                showProgress(false);
//            }
//        });

        fields.put("primary_barcode", "2000300000012");
        addSubscription(getApi().sku(fields), new ApiCallback<BasicBean>() {
            @Override
            public void onSuccess(BasicBean model) {
                showProgress(false);
                if (model != null) {
                    App.showToast("" + model.getMsg());
//                    if (model.getCode() == 1) {
//                        Log.d("LoginActivity", "userId=" + model.getMemberId());
//                        App.sp.putInt("userId", model.getMemberId());
//                        App.sp.putInt("type", model.getType());
//                        App.sp.commit();
//                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
//                        finish();
//                    }
                } else {
                    App.showToast("接口错误" + (model == null));
                }
            }

            @Override
            public void onFailure(String msg) {
                Log.d("LoginActivity", "失败了,如下 : " + msg);
            }

            @Override
            public void onFinish() {
                showProgress(false);
            }
        });
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
}

