package io.github.stack07142.trendingingithub.util;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class ResultCode {

    // TypeDef - IntDef
    // Constants
    public static final int SUCCESS = 0;
    public static final int FAIL = 1;

    // Declare the @IntDef for these contants
    @IntDef({SUCCESS, FAIL})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Result {
    }

}
