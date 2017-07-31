package io.github.stack07142.trendingingithub.util;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class ResultCode {

    public static final String REQUEST_CODE = "requestCode";

    // TypeDef - IntDef
    // Constants
    public static final int SUCCESS = 0;
    public static final int FAIL = 1;
    public static final int NONE = 2000;
    public static final int REQUEST_GITHUB_SIGNIN = 1000;
    public static final int REQUEST_GITHUB_SIGNOUT = 1001;

    // Declare the @IntDef for these contant
    @IntDef({SUCCESS, FAIL, NONE, REQUEST_GITHUB_SIGNIN, REQUEST_GITHUB_SIGNOUT})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Result {
    }

}
