package com.hutech.libadmin.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.DatePickerDialog;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
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
import java.util.Calendar;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateBookActivity extends AppCompatActivity {

    TokenManager tokenManager;
    APIService service;
    APIService serviceWithAuth;

    ArrayList<Author> authors;
    ArrayList<String> name = new ArrayList<>();

    Spinner spinnerAuthor;
    EditText edtNameBook, edtDescription, edtPrice, edtPublishDate;
    Button btnProcess;
    ImageView imgPickImage, imgPickDate;

    private static int RESULT_LOAD_IMG = 1;
    String encodedString;
    String imgPath, fileName;
    Bitmap bitmap;

    private static final String TAG = "CreateBookActivity";
    private int author_id;
    private String date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_book);

        setLightStatusBar(this);
        __autoLoad();
        __getAuthor();

        btnProcess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                __createBook();
            }
        });

        imgPickImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadImagefromGallery();
            }
        });

        imgPickDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR);
                int mMonth = c.get(Calendar.MONTH);
                int mDay = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(CreateBookActivity.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                int month = monthOfYear + 1;
                                date = year + "-" + month + "-" + dayOfMonth;
                                edtPublishDate.setText(date);
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });
    }

    private void __ref() {
        spinnerAuthor = findViewById(R.id.spinnerAuthor);
        edtNameBook = findViewById(R.id.edtBookName);
        spinnerAuthor = findViewById(R.id.spinnerAuthor);
        edtDescription = findViewById(R.id.edtDescriptionBook);
        edtPrice = findViewById(R.id.edtPrice);
        edtPublishDate = findViewById(R.id.edt_publish_date);
        btnProcess = findViewById(R.id.btnProcess);
        imgPickImage = findViewById(R.id.imgPickImage);
        imgPickDate = findViewById(R.id.imgPickDate);
    }

    private void __getAuthor() {
        service.list_all_author().enqueue(new Callback<ArrayList<Author>>() {
            @Override
            public void onResponse(Call<ArrayList<Author>> call, Response<ArrayList<Author>> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        authors = new ArrayList<>();
                        authors.addAll(response.body());

                        for (int i = 0; i < authors.size(); i++) {
                            name.add(authors.get(i).getName());
                        }

                        ArrayAdapter<String> authorArrayAdapter = new ArrayAdapter<>(CreateBookActivity.this, android.R.layout.simple_spinner_item, name);
                        authorArrayAdapter.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
                        spinnerAuthor.setAdapter(authorArrayAdapter);
                    } else {
                        tokenManager.deleteToken();
                        __onForwardLogin();
                    }
                } else {
                    CustomDialog.confirmDialog(CreateBookActivity.this, "Lỗi kết nối, vui lòng thử lại", 1000);
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Author>> call, Throwable t) {
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

    private void __createBook() {
        if (!validate()) {
            return;
        }

        spinnerAuthor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                author_id = spinnerAuthor.getSelectedItemPosition();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        String nameBook = edtNameBook.getText().toString().trim();
        String descriptionBook = edtDescription.getText().toString().trim();
        String publish_date = edtPublishDate.getText().toString().trim();
        float priceBook = Float.parseFloat(edtPrice.getText().toString().trim());
//
//        RequestBody fileReqBody = RequestBody.create(MediaType.parse("image/*"), fileName);
//        MultipartBody.Part part = MultipartBody.Part.createFormData("upload", fileName, fileReqBody);

        service.create_book(tokenManager.getToken().getAccessToken(), nameBook, author_id + 1, "https://i-giaitri.vnecdn.net/2018/03/14/luoc-su-thoi-gian-8675-1521009360.jpg", descriptionBook, publish_date, priceBook).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        Toast.makeText(CreateBookActivity.this, "Thêm sách thành công", Toast.LENGTH_LONG).show();
                        finish();
                    }
                } else {
                    if (response.code() == 400) {

                    } else {
                        CustomDialog.confirmDialog(CreateBookActivity.this, "Lỗi kết nối, vui lòng thử lại", 1000);
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.w(TAG, "onFailure: " + t.getMessage());
            }
        });
    }

    private boolean validate() {
        boolean valid = true;

        String nameBook = edtNameBook.getText().toString().trim();
        String descriptionBook = edtDescription.getText().toString().trim();
        float priceBook = Float.parseFloat(edtPrice.getText().toString().trim());

        if (nameBook.isEmpty()) {
            edtNameBook.setError("Tên sách không được để trống");
            valid = false;
        } else {
            edtNameBook.setError(null);
        }

        if (descriptionBook.isEmpty()) {
            edtDescription.setError("Mô tả sách không được để trống");
            valid = false;
        } else {
            edtDescription.setError(null);
        }

        if (priceBook == 0) {
            edtPrice.setError("Giá sách không được để trống");
            valid = false;
        } else {
            edtPrice.setError(null);
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
                    CustomDialog.confirmDialog(CreateBookActivity.this, "Lỗi kết nối, vui lòng thử lại", 1000);
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
