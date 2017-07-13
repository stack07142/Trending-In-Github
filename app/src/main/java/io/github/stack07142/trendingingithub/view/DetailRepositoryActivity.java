package io.github.stack07142.trendingingithub.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;

import io.github.stack07142.trendingingithub.Presenter.DetailRepositoryPresenter;
import io.github.stack07142.trendingingithub.R;
import io.github.stack07142.trendingingithub.contract.DetailRepositoryContract;
import io.github.stack07142.trendingingithub.model.GitHubService;
import io.github.stack07142.trendingingithub.model.NewGitHubRepoApplication;
import io.github.stack07142.trendingingithub.util.BaseActivityUtil;

public class DetailRepositoryActivity extends BaseActivityUtil implements DetailRepositoryContract.View {

    private static final String TAG = DetailRepositoryActivity.class.getSimpleName();

    private static final String EXTRA_FULL_REPOSITORY_NAME = "EXTRA_FULL_REPOSITORY_NAME";

    // Presenter에 직접 접근하지 않는다. Interface를 통한 접근
    private DetailRepositoryContract.UserActions detailPresenter;

    private TextView fullNameTextView;
    private TextView detailTextView;
    private TextView repoStarTextView;
    private TextView repoForkTextView;
    private ImageView ownerImage;

    private String fullRepositoryName;

    /**
     * DetailActivity를 시작하는 메소드
     *
     * @param fullRepositoryName 표시하고 싶은 리포지토리 이름(google/iosched 등)
     */
    public static void start(Context context, String fullRepositoryName) {

        final Intent intent = new Intent(context, DetailRepositoryActivity.class);
        intent.putExtra(EXTRA_FULL_REPOSITORY_NAME, fullRepositoryName);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_detail_repo);

        fullNameTextView = (TextView) DetailRepositoryActivity.this.findViewById(R.id.fullname);
        detailTextView = (TextView) findViewById(R.id.detail);
        repoStarTextView = (TextView) findViewById(R.id.repo_star);
        repoForkTextView = (TextView) findViewById(R.id.repo_fork);
        ownerImage = (ImageView) findViewById(R.id.owner_image);

        final Intent intent = getIntent();
        fullRepositoryName = intent.getStringExtra(EXTRA_FULL_REPOSITORY_NAME);

        final GitHubService gitHubService = ((NewGitHubRepoApplication) getApplication()).getGitHubService();
        detailPresenter = new DetailRepositoryPresenter((DetailRepositoryContract.View) this, gitHubService);
        detailPresenter.prepare();
    }

    /**
     * Contract interface 구현
     */

    @Override
    public String getFullRepositoryName() {

        return fullRepositoryName;
    }

    @Override
    public void showRepositoryInfo(GitHubService.RepositoryItem response) {

        fullNameTextView.setText(response.full_name);
        detailTextView.setText(response.description);
        repoStarTextView.setText(response.stargazers_count);
        repoForkTextView.setText(response.forks_count);

        // 서버로부터 이미지를 가져와 imageView에 넣는다
        Glide.with(DetailRepositoryActivity.this)
                .load(response.owner.avatar_url)
                .asBitmap().centerCrop().into(new BitmapImageViewTarget(ownerImage) {
            @Override
            protected void setResource(Bitmap resource) {
                RoundedBitmapDrawable circularBitmapDrawable =
                        RoundedBitmapDrawableFactory.create(getResources(), resource);
                circularBitmapDrawable.setCircular(true);
                ownerImage.setImageDrawable(circularBitmapDrawable);
            }
        });

        // 로고와 리포지토리 이름을 탭하면 작자의 GitHub 페이지를 브라우저로 연다
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Presenter에 Event 발생을 통지한다
                detailPresenter.titleClick();
            }
        };

        fullNameTextView.setOnClickListener(listener);
        ownerImage.setOnClickListener(listener);
    }

    @Override
    public void startBrowser(String url) {

        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
    }

    @Override
    public void showError(String message) {

        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG)
                .show();
    }
}
