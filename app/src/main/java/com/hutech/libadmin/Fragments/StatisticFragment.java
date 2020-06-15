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
import android.widget.TextView;

import com.hutech.libadmin.Activities.LoginActivity;
import com.hutech.libadmin.Activities.ViewOrderActivity;
import com.hutech.libadmin.Models.Customer;
import com.hutech.libadmin.Models.Order;
import com.hutech.libadmin.Models.Statistic;
import com.hutech.libadmin.R;
import com.hutech.libadmin.Utils.APIService;
import com.hutech.libadmin.Utils.CustomDialog;
import com.hutech.libadmin.Utils.RetrofitBuilder;
import com.hutech.libadmin.Utils.TokenManager;

import org.w3c.dom.Text;

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
public class StatisticFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private static final String TAG = "StatisticFragment";
    Context context;
    APIService service, serviceWithAuth;
    TokenManager tokenManager;

    TextView txtAllBook, txtAllAuthor, txtAllHring, txtAllOrder, txtAllUser, txtAllProfit, txtAllLibrary;

    SwipeRefreshLayout swipeLayout;

    public StatisticFragment() {
        // Required empty public constructor
    }

    @Override
    public void onRefresh() {
        onRefreshFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_statistic, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setLightStatusBar(getActivity());
        __init();
        __ref(view);
        __getStatistic();
        swipeLayout.setOnRefreshListener(this);
    }

    private void __ref(View view) {
        swipeLayout = view.findViewById(R.id.swipeLayout);
        txtAllBook = view.findViewById(R.id.txtAllBook);
        txtAllAuthor = view.findViewById(R.id.txtAllAuthor);
        txtAllHring = view.findViewById(R.id.txtHiring);
        txtAllOrder = view.findViewById(R.id.txtOrder);
        txtAllUser = view.findViewById(R.id.txtUser);
        txtAllProfit = view.findViewById(R.id.txtProfit);
        txtAllLibrary = view.findViewById(R.id.txtLibrary);
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

    private void __getStatistic() {
        final ProgressDialog progressDialog = new ProgressDialog(getActivity(),
                R.style.ThemeOverlay_MaterialComponents_Dialog);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        progressDialog.setIndeterminate(true);
        progressDialog.show();

        service.get_statistic(tokenManager.getToken().getAccessToken()).enqueue(new Callback<Statistic>() {
            @Override
            public void onResponse(Call<Statistic> call, Response<Statistic> response) {

                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        txtAllBook.setText(response.body().getBook() + " đầu sách");
                        txtAllAuthor.setText(response.body().getAuthor() + " tác giả");
                        txtAllHring.setText(response.body().getHiring() + " sách đang thuê");
                        txtAllOrder.setText(response.body().getOrder() + " lượt thuê");
                        txtAllUser.setText(response.body().getUser() + " thành viên");
                        txtAllLibrary.setText(response.body().getLibrary() + " lượt thích");

                        DecimalFormat df = new DecimalFormat("#,##0");
                        df.setDecimalFormatSymbols(new DecimalFormatSymbols(Locale.US));
                        txtAllProfit.setText(df.format(response.body().getProfit()) + " VNĐ");
                    }
                } else {
                    CustomDialog.confirmDialog(context, "Có lỗi, vui lòng thử lại", 1000);
                }
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<Statistic> call, Throwable t) {
                progressDialog.dismiss();
                Log.w(TAG, "onFailure: " + t.getMessage());
            }
        });
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

    private static void setLightStatusBar(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            activity.getWindow().setStatusBarColor(activity.getResources().getColor(R.color.start));
        }
    }
}
