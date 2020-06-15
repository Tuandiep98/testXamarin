package com.hutech.libadmin.Fragments;


import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Vibrator;
import android.util.Log;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.hutech.libadmin.Activities.LoginActivity;
import com.hutech.libadmin.Activities.ViewOrderActivity;
import com.hutech.libadmin.Models.Customer;
import com.hutech.libadmin.Models.Order;
import com.hutech.libadmin.R;
import com.hutech.libadmin.Utils.APIService;
import com.hutech.libadmin.Utils.CustomDialog;
import com.hutech.libadmin.Utils.RetrofitBuilder;
import com.hutech.libadmin.Utils.TokenManager;

import java.io.IOException;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.Manifest.permission.CAMERA;
import static android.content.Context.MODE_PRIVATE;

public class HomeFragment extends Fragment {
    private static final int REQUEST_CODE = 100;
    private String[] neededPermissions = new String[]{CAMERA};

    private static final String TAG = "HomeFragment";

    SurfaceView surfaceView;
    SurfaceHolder surfaceHolder;
    CameraSource cameraSource;
    BarcodeDetector barcodeDetector;
    TextView txtTitle;

    LinearLayout btnPutHand;

    APIService service, serviceWithAuth;
    TokenManager tokenManager;

    Context context;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            setWindowFlag(getActivity(), WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false);
            getActivity().getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        __init();
        __ref(view);
        __click();
    }

    private void __ref(View view) {
        txtTitle = view.findViewById(R.id.txtTitle);
        surfaceView = view.findViewById(R.id.cameraPreview);
        btnPutHand = view.findViewById(R.id.btnPutHand);
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

    private void __click() {
        if (surfaceView != null) {
            boolean result = checkPermission();
            if (result == false) {
                onRefreshFragment();
            }
            setupSurfaceHolder();
        }

        btnPutHand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                putHand();
            }
        });
    }

    private boolean checkPermission() {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (currentAPIVersion >= android.os.Build.VERSION_CODES.M) {
            ArrayList<String> permissionsNotGranted = new ArrayList<>();
            for (String permission : neededPermissions) {
                if (ContextCompat.checkSelfPermission(getActivity(), permission) != PackageManager.PERMISSION_GRANTED) {
                    permissionsNotGranted.add(permission);
                }
            }
            if (permissionsNotGranted.size() > 0) {
                requestPermissions(permissionsNotGranted.toArray(new String[permissionsNotGranted.size()]));
                return false;
            }
        }
        return true;
    }

    private void setViewVisibility(int id, int visibility) {
        View view = getActivity().findViewById(id);
        if (view != null) {
            view.setVisibility(visibility);
        }
    }

    private void setupSurfaceHolder() {
        setViewVisibility(R.id.cameraPreview, View.VISIBLE);
        surfaceHolder = surfaceView.getHolder();

        barcodeDetector = new BarcodeDetector.Builder(getActivity()).setBarcodeFormats(Barcode.QR_CODE).build();

        cameraSource = new CameraSource.Builder(getActivity(), barcodeDetector).setRequestedPreviewSize(1920, 1080).build();

        surfaceHolder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder surfaceHolder) {
                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    try {
                        cameraSource.start(surfaceHolder);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
                cameraSource.stop();
            }
        });

        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {

            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> qrCode = detections.getDetectedItems();

                if (qrCode.size() != 0) {
                    txtTitle.post(new Runnable() {
                        @Override
                        public void run() {
                            Vibrator vibrator = (Vibrator) getActivity().getBaseContext().getSystemService(Context.VIBRATOR_SERVICE);
                            vibrator.vibrate(100);
                            String code = qrCode.valueAt(0).displayValue;
                            cameraSource.stop();
                            openDialog(code);
                        }
                    });
                }
            }
        });
    }

    private void putHand() {
        cameraSource.stop();
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.put_hand, null);

        final EditText edtCode = alertLayout.findViewById(R.id.edtCode);
        final Button btnProcess = alertLayout.findViewById(R.id.btnProcess);

        btnProcess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code = edtCode.getText().toString();

                if (edtCode.length() < 2) {
                    edtCode.setError("Vui lòng nhập từ 2 ký tự trở lên");
                } else {
                    openDialog(code);
                }
            }
        });

        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
        alert.setView(alertLayout);
        alert.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                onRefreshFragment();
            }
        });
        AlertDialog dialog = alert.create();

        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();

        wlp.gravity = Gravity.BOTTOM;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(wlp);

        dialog.show();
    }

    private void openDialog(final String code) {
        final ProgressDialog progressDialog = new ProgressDialog(getActivity(),
                R.style.ThemeOverlay_MaterialComponents_Dialog);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        progressDialog.setIndeterminate(true);
        progressDialog.show();

        service.scan(tokenManager.getToken().getAccessToken(), code).enqueue(new Callback<Order>() {
            @Override
            public void onResponse(Call<Order> call, Response<Order> response) {
                progressDialog.dismiss();
                if (response.isSuccessful()) {
                    if (response.body() != null && response.body().getName_book() != null) {
                        Intent intent = new Intent(context, ViewOrderActivity.class);
                        intent.putExtra("hire_id", code);
                        startActivity(intent);
                    }
                } else {
                    if (response.code() == 422) {
                        CustomDialog.confirmDialog(context, "Mã đơn thuê không chính xác", 1000);
                    } else {
                        CustomDialog.confirmDialog(context, "Có lỗi, vui lòng thử lại", 1000);
                    }
                }
            }

            @Override
            public void onFailure(Call<Order> call, Throwable t) {
                progressDialog.dismiss();
                Log.w(TAG, "onFailure: " + t.getMessage());
            }
        });
    }

    private void requestPermissions(String[] permissions) {
        ActivityCompat.requestPermissions(getActivity(), permissions, REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CODE:
                for (int result : grantResults) {
                    if (result == PackageManager.PERMISSION_DENIED) {
                        Toast.makeText(getActivity(), "LibAdmin cần quyền truy cập CAMERA của bạn", Toast.LENGTH_LONG).show();
                        return;
                    }
                }
                setupSurfaceHolder();
                break;
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

    private void onRefreshFragment() {
        Fragment currentFragment = getActivity().getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.detach(currentFragment);
        fragmentTransaction.attach(currentFragment);
        fragmentTransaction.commit();
    }

    private static void setWindowFlag(Activity activity, final int bits, boolean on) {
        Window win = activity.getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    private void __onForwardLogin() {
        Intent intent = new Intent(context, LoginActivity.class);
        startActivity(intent);
        getActivity().finish();
    }
}
