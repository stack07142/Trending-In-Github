package io.github.stack07142.trendingingithub.util;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.tsengvn.typekit.Typekit;
import com.tsengvn.typekit.TypekitContextWrapper;

public class BaseActivityUtil extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Typekit.getInstance()
                .addNormal(Typekit.createFromAsset(this, "font/cooperhewitt-light.otf"))
                .addItalic(Typekit.createFromAsset(this, "font/cooperhewitt-lightitalic.otf"))
                .addBold(Typekit.createFromAsset(this, "font/cooperhewitt-medium.otf"))
                .addBoldItalic(Typekit.createFromAsset(this, "font/cooperhewitt-mediumitalic.otf"));
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(TypekitContextWrapper.wrap(newBase));
    }
}
