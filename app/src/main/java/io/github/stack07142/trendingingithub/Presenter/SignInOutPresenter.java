package io.github.stack07142.trendingingithub.Presenter;


import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GithubAuthProvider;

import java.io.IOException;

import io.github.stack07142.trendingingithub.R;
import io.github.stack07142.trendingingithub.contract.SignInOutContract;
import io.github.stack07142.trendingingithub.model.GitHubSignInOutService;
import io.github.stack07142.trendingingithub.util.DebugLog;
import io.github.stack07142.trendingingithub.util.ResultCode;
import io.github.stack07142.trendingingithub.view.SignInOutActivity;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SignInOutPresenter implements SignInOutContract.UserAction {

    private final String TAG = SignInOutPresenter.class.getSimpleName();

    // Presenter는 View로 직접 접근하지 않는다. Interface를 통해 접근한다.
    private final SignInOutContract.View signInOutView;
    private final GitHubSignInOutService gitHubSignInOutService;

    private FirebaseAuth mAuth;

    private Context context;

    // Constuctor
    public SignInOutPresenter(SignInOutContract.View signInOutView, GitHubSignInOutService gitHubSignInOutService) {

        this.signInOutView = signInOutView;
        this.gitHubSignInOutService = gitHubSignInOutService;

        this.context = ((SignInOutActivity) signInOutView).getBaseContext();

        // Firebase Auth
        this.mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void signIn() {

        gitHubSignInOutService.signIn();
    }

    @Override
    public void signOut() {

        if (gitHubSignInOutService.signOut()) {

            signInOutView.setResult(ResultCode.SUCCESS);
            signInOutView.finishView();
        } else {

            signInOutView.setResult(ResultCode.FAIL);
            signInOutView.finishView();
        }
    }

    @Override
    public void githubRedirect(Uri uri) {

        String code = uri.getQueryParameter("code");
        String state = uri.getQueryParameter("state");

        if (code != null && state != null) {

            DebugLog.logD(TAG, "code = " + code);
            DebugLog.logD(TAG, "state = " + state);

            // 2. Users are redirected back to your site by GitHub
            // POST https://github.com/login/oauth/access_token
            sendPost(code, state);

        } else {

            DebugLog.logD(TAG, "gitHubRedirect() - Error");

            signInOutView.setResult(ResultCode.FAIL);
            signInOutView.finishView();
        }
    }


    private void sendPost(String code, String state) {

        OkHttpClient okHttpClient = new OkHttpClient();

        FormBody form = new FormBody.Builder()
                .add("client_id", context.getString(R.string.github_client_id))
                .add("client_secret", context.getString(R.string.github_client_secret))
                .add("code", code)
                .add("redirect_uri", context.getString(R.string.github_redirect_url))
                .add("state", state)
                .build();

        Request request = new Request.Builder()
                .url("https://github.com/login/oauth/access_token")
                .post(form)
                .build();

        okHttpClient.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {

                signInOutView.setResult(ResultCode.FAIL);
                signInOutView.finishView();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                // Response form : access_token=e72e16c7e42f292c6912e7710c838347ae178b4a&token_type=bearer
                String responseBody = response.body().string();
                String[] splittedBody = responseBody.split("=|&");

                DebugLog.logD(TAG, "responseBody = " + responseBody);

                if (splittedBody[0].equalsIgnoreCase("access_token")) {

                    signInWithToken(splittedBody[1]);
                } else {

                    signInOutView.setResult(ResultCode.FAIL);
                    signInOutView.finishView();
                }
            }
        });
    }

    private void signInWithToken(String token) {

        final boolean[] ret = new boolean[1];

        DebugLog.logD(TAG, "signInWithToken()");

        // credential object from the token
        AuthCredential credential = GithubAuthProvider.getCredential(token);

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(((SignInOutActivity) signInOutView), new OnCompleteListener<AuthResult>() {

                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {

                            signInOutView.setResult(ResultCode.SUCCESS);

                        } else {

                            signInOutView.setResult(ResultCode.FAIL);
                        }
                        signInOutView.finishView();
                    }
                })
                .addOnFailureListener(((SignInOutActivity) signInOutView), new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        signInOutView.setResult(ResultCode.FAIL);
                        signInOutView.finishView();
                    }
                });
    }
}
