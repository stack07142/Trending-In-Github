package io.github.stack07142.trendingingithub.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import io.github.stack07142.trendingingithub.Presenter.SignInOutPresenter;
import io.github.stack07142.trendingingithub.contract.SignInOutContract;
import io.github.stack07142.trendingingithub.model.GitHubSignInOutService;
import io.github.stack07142.trendingingithub.util.ResultCode;

public class SignInOutActivity extends Activity implements SignInOutContract.View {

    private final String TAG = SignInOutActivity.class.getSimpleName();

    // View는 Presenter로 직접 접근하지 않는다. Interface를 통해 접근한다.
    private SignInOutContract.UserAction signInOutPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // GitHubSignInOutService Instance 생성
        final GitHubSignInOutService gitHubSignInOutService = new GitHubSignInOutService(this);

        // Presenter Instance 생성
        signInOutPresenter = new SignInOutPresenter(this, gitHubSignInOutService);

        Bundle bundle = getIntent().getExtras();
        int requestCode = bundle.getInt(ResultCode.REQUEST_CODE, ResultCode.NONE);

        // Sign In
        if (requestCode == ResultCode.REQUEST_GITHUB_SIGNIN) {

            signInOutPresenter.signIn();

        }
        // Sign Out
        else if (requestCode == ResultCode.REQUEST_GITHUB_SIGNOUT) {

            signInOutPresenter.signOut();
        }


    } // ~onCreate

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        if (intent != null) {

            String code = intent.getStringExtra("code");
            String state = intent.getStringExtra("state");

            signInOutPresenter.githubRedirect(code, state);
        }
    }

    @Override
    public void finishView() {

        finish();
    }
}
