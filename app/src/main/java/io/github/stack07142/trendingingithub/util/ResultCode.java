package io.github.stack07142.trendingingithub.util;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class ResultCode {

    public static final String REQUEST_CODE = "requestCode";

    // TypeDef - IntDef
    // Constants
    public static final int SUCCESS = 100;
    public static final int FAIL = 101;
    public static final int NONE = 2000;

    // Declare the @IntDef for these contant
    @IntDef({SUCCESS, FAIL, NONE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Result {
    }

}
