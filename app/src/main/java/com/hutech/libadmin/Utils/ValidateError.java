package com.hutech.libadmin.Utils;

import java.io.IOException;
import java.lang.annotation.Annotation;

import okhttp3.ResponseBody;
import retrofit2.Converter;

public class ValidateError {

    public static APIError convertErrors(ResponseBody response) {
        Converter<ResponseBody, APIError> converter = RetrofitBuilder.getRetrofit().responseBodyConverter(APIError.class, new Annotation[0]);

        APIError apiError = null;

        try {
            apiError = converter.convert(response);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return apiError;
    }
}
