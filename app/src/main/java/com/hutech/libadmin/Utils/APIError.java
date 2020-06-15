package com.hutech.libadmin.Utils;

import java.util.List;
import java.util.Map;

public class APIError {
    String message;
    Map<String, List<String>> errors;

    public String getMessage() {
        return message;
    }

    public Map<String, List<String>> getErrors() {
        return errors;
    }
}
