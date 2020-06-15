package com.hutech.libadmin.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hutech.libadmin.Models.AccessToken;
import com.hutech.libadmin.Models.Customer;
import com.hutech.libadmin.Models.Order;
import com.hutech.libadmin.R;
import com.hutech.libadmin.Utils.APIService;
import com.hutech.libadmin.Utils.CustomDialog;
import com.hutech.libadmin.Utils.RetrofitBuilder;
import com.hutech.libadmin.Utils.TokenManager;
import com.squareup.picasso.Picasso;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewOrderActivity extends AppCompatActivity {

    TokenManager tokenManager;
    APIService service;
    APIService serviceWithAuth;

    private static final String TAG = "ViewOrderActivity";

    private String hire_id;

    LinearLayout llBack;
    TextView txtCustomerName, txtCustomerEmail, txtBookName, txtDateHire, txtDateReturn;
    ImageView imgBook;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_order);

        setLightStatusBar(this);
        __autoLoad();

        llBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                __back();
            }
        });
    }

    private void __autoLoad() {
        service = RetrofitBuilder.createService(APIService.class);
        tokenManager = TokenManager.getInstance(this.getSharedPreferences("prefs", MODE_PRIVATE));
        serviceWithAuth = RetrofitBuilder.createServiceWithAuth(APIService.class, tokenManager);

        if (tokenManager.getToken().getAccessToken() == null) {
            tokenManager.deleteToken();
            __onForwardLogin();
        }

        __ref();
        __checkAuth();
        getDataActivity();
        __loadingData(hire_id);
    }

    private void __ref() {
        llBack = findViewById(R.id.llBack);
        txtCustomerName = findViewById(R.id.txtCustomerName);
        txtCustomerEmail = findViewById(R.id.txtCustomerEmail);
        txtBookName = findViewById(R.id.txtBookName);
        txtDateHire = findViewById(R.id.txtDateHire);
        txtDateReturn = findViewById(R.id.txtDateReturn);
        imgBook = findViewById(R.id.imgBook);
    }

    private void __loadingData(String code) {
        final ProgressDialog progressDialog = new ProgressDialog(this,
                R.style.ThemeOverlay_MaterialComponents_Dialog);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        progressDialog.setIndeterminate(true);
        progressDialog.show();

        service.scan(tokenManager.getToken().getAccessToken(), code).enqueue(new Callback<Order>() {
            @Override
            public void onResponse(Call<Order> call, Response<Order> response) {
                progressDialog.dismiss();
                if (response.isSuccessful()) {
                    if (response.body() != null && response.body().getUser_name() != null) {
                        txtCustomerName.setText("Tên người thuê | " + response.body().getUser_name());
                        txtCustomerEmail.setText("Email | " + response.body().getUser_email());
                        txtBookName.setText("Tên sách | " + response.body().getName_book());
                        txtDateHire.setText("Ngày thuê | " + response.body().getDate_hire());
                        txtDateReturn.setText("Ngày trả | " + response.body().getDate_return());

                        Picasso.get()
                                .load(response.body().getImage_book())
                                .placeholder(R.drawable.ic_error_404)
                                .error(R.drawable.ic_error_404)
                                .into(imgBook);
                    }
                } else {
                    CustomDialog.confirmDialog(ViewOrderActivity.this, "Có lỗi, vui lòng thử lại", 1000);
                }
            }

            @Override
            public void onFailure(Call<Order> call, Throwable t) {
                progressDialog.dismiss();
                Log.w(TAG, "onFailure: " + t.getMessage());
            }
        });
    }

    private void __checkAuth() {
        serviceWithAuth.get_data_customer(tokenManager.getToken().getAccessToken()).enqueue(new Callback<Customer>() {
            @Override
            public void onResponse(Call<Customer> call, Response<Customer> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null && response.body().customer_name != null) {
                        Log.d(TAG, "onResponse: " + response.body());
                    } else {
                        tokenManager.deleteToken();
                        __onForwardLogin();
                    }
                } else {
                    CustomDialog.confirmDialog(ViewOrderActivity.this, "Lỗi kết nối, vui lòng thử lại", 1000);
                }
            }

            @Override
            public void onFailure(Call<Customer> call, Throwable t) {
                Log.w(TAG, "onFailure: " + t.getMessage());
            }
        });
    }

    private void __onForwardLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void getDataActivity() {
        Intent intent = getIntent();
        hire_id = intent.getStringExtra("hire_id");
    }

    private void __back() {
        Intent intent = new Intent();
        setResult(Activity.RESULT_CANCELED, intent);
        finish();
    }

    private static void setLightStatusBar(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            activity.getWindow().setStatusBarColor(activity.getResources().getColor(R.color.colorWhite));
        }
    }
}
