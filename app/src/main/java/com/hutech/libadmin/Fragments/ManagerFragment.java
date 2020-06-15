package com.hutech.libadmin.Fragments;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hutech.libadmin.Activities.CreateAuthorActivity;
import com.hutech.libadmin.Activities.CreateBookActivity;
import com.hutech.libadmin.Activities.LoginActivity;
import com.hutech.libadmin.Models.Customer;
import com.hutech.libadmin.Models.Statistic;
import com.hutech.libadmin.R;
import com.hutech.libadmin.Utils.APIService;
import com.hutech.libadmin.Utils.CustomDialog;
import com.hutech.libadmin.Utils.RetrofitBuilder;
import com.hutech.libadmin.Utils.TokenManager;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 */
public class ManagerFragment extends Fragment {
    private static final String TAG = "ManagerFragment";
    Context context;
    APIService service, serviceWithAuth;
    TokenManager tokenManager;

    TextView txtCustomerName, txtNameCustomer, txtCustomerEmail;

    LinearLayout llLogout, llAddBook, llAddAuthor;

    public ManagerFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_manager, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setLightStatusBar(getActivity());
        __ref(view);
        __init();

        llLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tokenManager.deleteToken();
                __onForwardLogin();
            }
        });

        llAddBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, CreateBookActivity.class);
                startActivity(intent);
            }
        });

        llAddAuthor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, CreateAuthorActivity.class);
                startActivity(intent);
            }
        });
    }

    private void __ref(View view) {
        txtCustomerName = view.findViewById(R.id.txtCustomerName);
        llLogout = view.findViewById(R.id.llLogout);
        llAddBook = view.findViewById(R.id.llAddBook);
        llAddAuthor = view.findViewById(R.id.llAddAuthor);
        txtNameCustomer = view.findViewById(R.id.txtNameCustomer);
        txtCustomerEmail = view.findViewById(R.id.txtCustomerEmail);
    }

    private void __init() {
        context = getContext();

        service = RetrofitBuilder.createService(APIService.class);
        tokenManager = TokenManager.getInstance(context.getSharedPreferences("prefs", MODE_PRIVATE));

        serviceWithAuth = RetrofitBuilder.createServiceWithAuth(APIService.class, tokenManager);

        if (tokenManager.getToken().getAccessToken() == null) {
            tokenManager.deleteToken();
            __onForwardLogin();
        } else {
            __checkAuth();
            __getUser();
        }
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
                    CustomDialog.confirmDialog(context, "Lỗi kết nối, vui lòng thử lại", 1000);
                }
            }

            @Override
            public void onFailure(Call<Customer> call, Throwable t) {
                Log.w(TAG, "onFailure: " + t.getMessage());
            }
        });
    }

    private void __getUser() {
        final ProgressDialog progressDialog = new ProgressDialog(getActivity(),
                R.style.ThemeOverlay_MaterialComponents_Dialog);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        progressDialog.setIndeterminate(true);
        progressDialog.show();

        service.get_data_customer(tokenManager.getToken().getAccessToken()).enqueue(new Callback<Customer>() {
            @Override
            public void onResponse(Call<Customer> call, Response<Customer> response) {

                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        txtCustomerName.setText(response.body().getCustomer_name());
                        txtNameCustomer.setText(response.body().getCustomer_name());
                        txtCustomerEmail.setText(response.body().getCustomer_email());
                    }
                } else {
                    CustomDialog.confirmDialog(context, "Có lỗi, vui lòng thử lại", 1000);
                }
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<Customer> call, Throwable t) {
                progressDialog.dismiss();
                Log.w(TAG, "onFailure: " + t.getMessage());
            }
        });
    }

    private static void setLightStatusBar(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            activity.getWindow().setStatusBarColor(activity.getResources().getColor(R.color.start_2));
        }
    }

    private void __onForwardLogin() {
        Intent intent = new Intent(context, LoginActivity.class);
        startActivity(intent);
        getActivity().finish();
    }

    private void onRefreshFragment() {
        Fragment currentFragment = getActivity().getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.detach(currentFragment);
        fragmentTransaction.attach(currentFragment);
        fragmentTransaction.commit();
    }
}
