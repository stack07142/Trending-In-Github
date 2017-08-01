package io.github.stack07142.trendingingithub.model;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.google.firebase.auth.FirebaseAuth;

import java.math.BigInteger;
import java.util.Random;

import io.github.stack07142.trendingingithub.R;
import io.github.stack07142.trendingingithub.util.DebugLog;
import io.github.stack07142.trendingingithub.util.ResultCode;
import io.github.stack07142.trendingingithub.view.SignInOutActivity;
import okhttp3.HttpUrl;

public class GitHubSignInOutService {

    private final String TAG = GitHubSignInOutService.class.getSimpleName();

    private Context context;

    private FirebaseAuth mAuth;

    public GitHubSignInOutService(Context context) {

        this.context = context;

        // Firebase Auth
        mAuth = FirebaseAuth.getInstance();
    }

    public void signIn() {

        /**
         * Github - Web Application Flow
         *
         * The flow to authorize users for your app is:
         * 1. Users are redirected to request their GitHub identity
         * 2. Users are redirected back to your site by GitHub
         * 3. Your GitHub App accesses the API with the user's access token
         */

        // 1. Users are redirected to request their GitHub identity
        // GET http://github.com/login/oauth/authorize
        HttpUrl httpUrl = new HttpUrl.Builder()
                .scheme("http")
                .host("github.com")
                .addPathSegment("login")
                .addPathSegment("oauth")
                .addPathSegment("authorize")
                .addQueryParameter("client_id", context.getString(R.string.github_client_id))
                .addQueryParameter("redirect_uri", context.getString(R.string.github_redirect_url))
                .addQueryParameter("state", getRandomString())
                .addQueryParameter("scope", "user:email")
                .build();

        DebugLog.logD(TAG, "httpUrl = " + httpUrl.toString());

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(httpUrl.toString()));

        ((SignInOutActivity) context).startActivityForResult(intent, ResultCode.REQUEST_GITHUB_REDIRECT);
    }

    public boolean signOut() {

        mAuth.signOut();

        if (mAuth.getCurrentUser() == null) {

            return true;
        } else {

            return false;
        }
    }

    /**
     * An unguessable random string.
     * It is used to protect against cross-site request forgery attacks.
     */
    private String getRandomString() {

        return new BigInteger(130, new Random()).toString(32);
    }
}
