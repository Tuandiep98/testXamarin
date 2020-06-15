package com.hutech.libadmin.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.hutech.libadmin.Models.Author;
import com.hutech.libadmin.Models.Customer;
import com.hutech.libadmin.R;
import com.hutech.libadmin.Utils.APIService;
import com.hutech.libadmin.Utils.CustomDialog;
import com.hutech.libadmin.Utils.RetrofitBuilder;
import com.hutech.libadmin.Utils.TokenManager;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateAuthorActivity extends AppCompatActivity {
    TokenManager tokenManager;
    APIService service;
    APIService serviceWithAuth;

    EditText edtAuthorName, edtAuthorDescription;
    Button btnProcess;
    ImageView imgPickImage;

    private static int RESULT_LOAD_IMG = 1;
    String encodedString;
    String imgPath, fileName;
    Bitmap bitmap;

    private static final String TAG = "CreateAuthorActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_author);

        setLightStatusBar(this);
        __autoLoad();

        btnProcess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                __createAuthor();
            }
        });

        imgPickImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadImagefromGallery();
            }
        });
    }

    private void __createAuthor() {
        if (!validate()) {
            return;
        }

        String nameAuthor = edtAuthorName.getText().toString().trim();
        String description = edtAuthorDescription.getText().toString().trim();

        service.create_author(tokenManager.getToken().getAccessToken(), nameAuthor, "http://static.cand.com.vn/Uploaded_ANTG/nguyenbinh/nguyenbinh1/15_chan1283.jpg", description).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        Toast.makeText(CreateAuthorActivity.this, "" + response.body(), Toast.LENGTH_LONG).show();
                        finish();
                    }
                } else {
                    if (response.code() == 400) {

                    } else {
                        CustomDialog.confirmDialog(CreateAuthorActivity.this, "Lỗi kết nối, vui lòng thử lại", 1000);
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.w(TAG, "onFailure: " + t.getMessage());
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
    }

    private void __ref() {
        edtAuthorName = findViewById(R.id.edtAuthorName);
        edtAuthorDescription = findViewById(R.id.edtDescriptionBook);
        btnProcess = findViewById(R.id.btnProcess);
        imgPickImage = findViewById(R.id.imgPickImage);
    }

    private boolean validate() {
        boolean valid = true;

        String nameAuthor = edtAuthorName.getText().toString().trim();
        String descriptionAuthor = edtAuthorDescription.getText().toString().trim();

        if (nameAuthor.isEmpty()) {
            edtAuthorName.setError("Tên tác giả không được để trống");
            valid = false;
        } else {
            edtAuthorName.setError(null);
        }

        if (descriptionAuthor.isEmpty()) {
            edtAuthorDescription.setError("Mô tả không được để trống");
            valid = false;
        } else {
            edtAuthorDescription.setError(null);
        }

        return valid;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode == RESULT_LOAD_IMG && resultCode == RESULT_OK
                    && null != data) {
                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                Cursor cursor = getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                imgPath = cursor.getString(columnIndex);
                cursor.close();
                ImageView imgView = findViewById(R.id.imgPickImage);
                imgView.setImageBitmap(BitmapFactory
                        .decodeFile(imgPath));
                String fileNameSegments[] = imgPath.split("/");
                fileName = fileNameSegments[fileNameSegments.length - 1];

                BitmapFactory.Options options = null;
                options = new BitmapFactory.Options();
                options.inSampleSize = 3;
                bitmap = BitmapFactory.decodeFile(imgPath,
                        options);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 50, stream);
                byte[] byte_arr = stream.toByteArray();
                encodedString = Base64.encodeToString(byte_arr, 0);
            } else {
                Toast.makeText(this, "Vui lòng chọn ảnh sách", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "" + e.getMessage(), Toast.LENGTH_LONG)
                    .show();
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
                    CustomDialog.confirmDialog(CreateAuthorActivity.this, "Lỗi kết nối, vui lòng thử lại", 1000);
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

    private void __back() {
        Intent intent = new Intent();
        setResult(Activity.RESULT_CANCELED, intent);
        finish();
    }

    private void loadImagefromGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, RESULT_LOAD_IMG);
    }

    private static void setLightStatusBar(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            activity.getWindow().setStatusBarColor(activity.getResources().getColor(R.color.start_3));
        }
    }
}
