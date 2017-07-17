package io.github.stack07142.trendingingithub.Presenter;

import io.github.stack07142.trendingingithub.contract.DetailRepositoryContract;
import io.github.stack07142.trendingingithub.model.GitHubService;
import io.github.stack07142.trendingingithub.util.ResultCode;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class DetailRepositoryPresenter implements DetailRepositoryContract.UserActions {

    // View에 직접 접근하지 않는다. interface를 통한 접근.
    private final DetailRepositoryContract.View detailView;

    private final GitHubService gitHubService;
    private GitHubService.RepositoryItem repositoryItem;

    // Constructor
    public DetailRepositoryPresenter(DetailRepositoryContract.View detailView, GitHubService gitHubService) {

        this.detailView = detailView;
        this.gitHubService = gitHubService;
    }

    /**
     * Contract interface 구현
     */
    @Override
    public void titleClick() {

        try {

            // View에 View 변경을 통지한다
            detailView.startBrowser(repositoryItem.html_url);
        } catch (Exception e) {

            // View에 View 변경을 통지한다
            detailView.showNoti(ResultCode.FAIL);
        }
    }

    @Override
    public void prepare() {

        loadRepository();
    }

    /**
     * 한 개의 리포지토리에 대한 정보를 가져온다
     * 기본적으로 API 액세스 방법은 RepositoryListActivity#loadRepositories(String)과 같다
     */
    private void loadRepository() {

        String fullRepositoryName = detailView.getFullRepositoryName();

        // 리포지토리의 이름을 /로 분할한다
        final String[] repoData = fullRepositoryName.split("/");
        final String owner = repoData[0];
        final String repoName = repoData[1];

        gitHubService.detailRepo(owner, repoName)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<GitHubService.RepositoryItem>() {
                    @Override
                    public void call(GitHubService.RepositoryItem response) {

                        repositoryItem = response;

                        // View에 View 변경을 통지한다
                        detailView.showRepositoryInfo(response);

                        detailView.showNoti(ResultCode.SUCCESS);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {

                        // View에 View 변경을 통지한다
                        detailView.showNoti(ResultCode.FAIL);
                    }
                });
    }
}
