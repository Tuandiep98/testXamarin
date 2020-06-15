package com.hutech.libadmin.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.hutech.libadmin.Models.AccessToken;
import com.hutech.libadmin.R;
import com.hutech.libadmin.Utils.APIError;
import com.hutech.libadmin.Utils.APIService;
import com.hutech.libadmin.Utils.CustomDialog;
import com.hutech.libadmin.Utils.RetrofitBuilder;
import com.hutech.libadmin.Utils.TokenManager;
import com.hutech.libadmin.Utils.ValidateError;

import org.json.JSONObject;

import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {


    private static final String TAG = "LoginActivity";

    Call<AccessToken> call;
    APIService service;
    TokenManager tokenManager;

    ConstraintLayout container;
    ProgressBar progressBar;
    Button btnLogin;

    EditText edtEmail, edtPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            this.getWindow().setStatusBarColor(getResources().getColor(R.color.colorLoginActivity));
        }

        tokenManager = TokenManager.getInstance(getSharedPreferences("prefs", MODE_PRIVATE));
        service = RetrofitBuilder.createService(APIService.class);

        if (tokenManager.getToken().getAccessToken() != null) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }

        container = findViewById(R.id.container);
        btnLogin = findViewById(R.id.btn_login);
        progressBar = findViewById(R.id.progress_bar);
        edtEmail = findViewById(R.id.edt_email);
        edtPassword = findViewById(R.id.edt_password);
        showForm();

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                __login();
            }
        });
    }

    private void __login() {
        showLoading();

        if (!validate()) {
            showForm();
            return;
        }

        String email = edtEmail.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();

        edtEmail.setError(null);
        edtPassword.setError(null);

        call = service.login(email, password);
        call.enqueue(new Callback<AccessToken>() {
            @Override
            public void onResponse(Call<AccessToken> call, Response<AccessToken> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        tokenManager.saveToken(response.body());
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();
                    }
                } else {
                    if (response.code() == 422) {
                        handleErrors(response.errorBody());
                    }
                    if (response.code() == 401) {
                        try {
                            JSONObject jObjError = new JSONObject(response.errorBody().string());
                            CustomDialog.confirmDialog(LoginActivity.this, jObjError.getString("auth"), 2000);

                        } catch (Exception e) {
                            Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                    showForm();
                }
            }

            @Override
            public void onFailure(Call<AccessToken> call, Throwable t) {
                Log.w(TAG, "onFailure: " + t.getMessage());
                CustomDialog.confirmDialog(LoginActivity.this, "Quá thời gian đăng nhập, vui lòng thử lại", 2000);
                showForm();
            }
        });
    }

    private void handleErrors(ResponseBody response) {
        APIError apiError = ValidateError.convertErrors(response);

        for (Map.Entry<String, List<String>> error : apiError.getErrors().entrySet()) {
            if (error.getKey().equals("email")) {
                edtEmail.setError(error.getValue().get(0));
            }
            if (error.getKey().equals("password")) {
                edtPassword.setError(error.getValue().get(0));
            }
        }
    }

    public boolean validate() {
        boolean valid = true;

        String email = edtEmail.getText().toString();
        String password = edtPassword.getText().toString();

        if (email.isEmpty()) {
            edtEmail.setError("Nhập vào email");
            valid = false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            edtEmail.setError("Email không đúng định dạng");
            valid = false;
        } else {
            edtEmail.setError(null);
        }

        if (password.isEmpty() || password.length() < 8) {
            edtPassword.setError("Mật khẩu không được nhỏ hơn 8 ký tự");
            valid = false;
        } else {
            edtPassword.setError(null);
        }

        return valid;
    }

    private void showLoading() {
        TransitionManager.beginDelayedTransition(container);
        btnLogin.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
    }

    private void showForm() {
        TransitionManager.beginDelayedTransition(container);
        btnLogin.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (call != null) {
            call.cancel();
            call = null;
        }
    }
}
