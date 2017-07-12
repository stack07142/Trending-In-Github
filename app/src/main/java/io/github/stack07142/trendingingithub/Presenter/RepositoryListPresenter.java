package io.github.stack07142.trendingingithub.Presenter;

import android.text.format.DateFormat;

import java.util.Calendar;

import io.github.stack07142.trendingingithub.model.GitHubService;
import io.github.stack07142.trendingingithub.contract.RepositoryListContract;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * interface-UserAction: View -> Presenter (event)
 */
public class RepositoryListPresenter implements RepositoryListContract.UserAction {

    // Presenter는 View로 직접 접근하지 않는다. Interface를 통해 접근한다.
    private final RepositoryListContract.View repositoryListView;
    private final GitHubService gitHubService;

    // Constructor
    public RepositoryListPresenter(RepositoryListContract.View repositoryListView, GitHubService gitHubService) {

        this.repositoryListView = repositoryListView;
        this.gitHubService = gitHubService;
    }

    /**
     * Contract interface 구현
     */

    // TODO : Refactoring this
    @Override
    public void selectLanguage(String language) {

        loadRepositories();
    }

    @Override
    public void selectRepositoryItem(GitHubService.RepositoryItem item) {

        repositoryListView.startDetailActivity(item.full_name);
    }

    // TODO : Refactoring this
    private void loadRepositories() {

        // 로딩 시작. ProgressBar 표시
        repositoryListView.showProgress();

        // 일주일 전 날짜 문자열 지금이 2016-10-27이면 2016-10-20 이라는 문자열을 얻는다
        final Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -7);
        String text = DateFormat.format("yyyy-MM-dd", calendar).toString();

        // Retrofit을 이용해 서버에 액세스한다

        // 지난 일주일간 만들어지고 언어가 language인 것을 쿼리로 전달한다
        Observable<GitHubService.Repositories> observable = gitHubService.listRepos("language:" + repositoryListView.getSelectedLanguage() + " " + "created:>" + text);

        // 입출력(IO)용 스레드로 통신해 메인스레드로 결과를 받아오게 한다
        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<GitHubService.Repositories>() {

            @Override
            public void onNext(GitHubService.Repositories repositories) {

                repositoryListView.hideProgress();

                // GET Repositories -> Recycler View에 표시한다
                repositoryListView.showRepositories(repositories);
            }

            @Override
            public void onError(Throwable e) {

                // 통신 실패 에러 표시. SnackBar
                repositoryListView.showError();
            }

            @Override
            public void onCompleted() {

                // Do Nothing.
            }
        });
    }
}
