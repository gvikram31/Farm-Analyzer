package com.example.tsycp.myfarm;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tsycp.myfarm.model.UserData;
import com.example.tsycp.myfarm.network.LoginService;
import com.example.tsycp.myfarm.model.User;
import com.example.tsycp.myfarm.network.config.Config;
import com.example.tsycp.myfarm.util.PrefUtil;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText usernameText;
    private EditText passwordText;
    private Button btnLogin;
    private TextView registerCaption;

    private LoginService loginService;

    public static void start(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if(isSessionLogin()) {
            MainActivity.start(this);
            LoginActivity.this.finish();
        }

        usernameText = (EditText) findViewById(R.id.username);
        passwordText = (EditText) findViewById(R.id.password);
        btnLogin = (Button) findViewById(R.id.btn_login);
        registerCaption = (TextView) findViewById(R.id.register_caption);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginAct();
            }
        });

        String caption = "Dont have an account? <b>Register</b>";
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(Html.fromHtml(caption));
        spannableStringBuilder.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View view) {
                RegisterActivity.start(LoginActivity.this);
            }
        }, caption.indexOf("Register") - 3, spannableStringBuilder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableStringBuilder.setSpan(new ForegroundColorSpan(Color.WHITE), caption
                .indexOf("Register") - 3, spannableStringBuilder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        registerCaption.setText(spannableStringBuilder);
        registerCaption.setMovementMethod(LinkMovementMethod.getInstance());

    }

    void loginAct() {
        String username = usernameText.getText().toString();
        String password = passwordText.getText().toString();

        if(TextUtils.isEmpty(username)) {
            usernameText.setError("Username cannot be empty!");
            return;
        }

        if(TextUtils.isEmpty(password)) {
            passwordText.setError("Password cannot be empty");
            return;
        }

        loginService = new LoginService(this);
        loginService.doLogin(username, password, new retrofit2.Callback() {
            @Override
            public void onResponse(retrofit2.Call call, retrofit2.Response response) {
                Log.v("User Information","hahaha1");
                User user = (User) response.body();
                Log.v("User Information","hahaha2");
                Log.v("User Information",user.getData().getFirstname());
                Log.v("User Information",user.getData().getLastname());
                Log.v("User Information",user.getData().getUsername());

                if(user != null) {
                    if(!user.isError()) {
                        PrefUtil.putUser(LoginActivity.this, PrefUtil.USER_SESSION, user);
                        MainActivity.start(LoginActivity.this);
                        LoginActivity.this.finish();
                    }

                    Toast.makeText(LoginActivity.this, user.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(retrofit2.Call call, Throwable t) {
                Log.v("User Information",t.getMessage());
                Toast.makeText(LoginActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    boolean isSessionLogin() {
        return PrefUtil.getUser(this, PrefUtil.USER_SESSION) != null;
    }
}