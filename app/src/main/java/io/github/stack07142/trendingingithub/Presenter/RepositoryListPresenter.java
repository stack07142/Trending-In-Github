package io.github.stack07142.trendingingithub.Presenter;

import android.support.annotation.NonNull;
import android.text.format.DateFormat;

import java.util.ArrayList;
import java.util.Calendar;

import io.github.stack07142.trendingingithub.contract.RepositoryListContract;
import io.github.stack07142.trendingingithub.model.GitHubService;
import io.github.stack07142.trendingingithub.util.DebugLog;
import io.github.stack07142.trendingingithub.util.ResultCode;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * interface-UserAction: View -> Presenter (event)
 */
public class RepositoryListPresenter implements RepositoryListContract.UserAction {

    private final String TAG = RepositoryListPresenter.class.getSimpleName();

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

    @Override
    public void selectLanguage(ArrayList<String> languages, @NonNull String period) {

        // 로딩 시작. ProgressBar 표시
        repositoryListView.showProgress();

        // GET Query Period
        final Calendar calendar = Calendar.getInstance();

        switch (period) {

            case "today":
                calendar.add(Calendar.DAY_OF_MONTH, -1);
                break;

            // 일주일 전 날짜 문자열 지금이 2016-10-27이면 2016-10-20 이라는 문자열을 얻는다
            case "this week":
                calendar.add(Calendar.DAY_OF_MONTH, -7);
                break;

            case "this month":
                calendar.add(Calendar.DAY_OF_MONTH, -30);
                break;

            case "this year":
                calendar.add(Calendar.DAY_OF_MONTH, -365);
                break;

            default:
                calendar.add(Calendar.DAY_OF_MONTH, -7);
                break;
        }

        String text = DateFormat.format("yyyy-MM-dd", calendar).toString();

        // Retrofit을 이용해 서버에 액세스한다

        final int size = languages.size();

        final ArrayList<GitHubService.RepositoryItem> retItems = new ArrayList<>();

        for (int i = 0; i < size; i++) {

            String language = languages.get(i);

            final int finalI = i;

            // 지난 일주일간 만들어지고 언어가 language인 것을 쿼리로 전달한다
            Observable<GitHubService.Repositories> observable = gitHubService.listRepos("language:" + (language.equals("All") ? "null" : language) + " " + "created:>" + text);

            // 입출력(IO)용 스레드로 통신해 메인스레드로 결과를 받아오게 한다
            observable.subscribeOn(Schedulers.io()).

                    observeOn(AndroidSchedulers.mainThread()).

                    subscribe(new Subscriber<GitHubService.Repositories>() {

                        @Override
                        public void onNext(GitHubService.Repositories repositories) {

                            for (GitHubService.RepositoryItem item : repositories.items) {

                                retItems.add(item);
                            }

                            if (finalI == size - 1) {

                                repositoryListView.hideProgress();

                                if (retItems == null || retItems.size() == 0) {

                                    DebugLog.logD(TAG, "retItems is null");

                                    repositoryListView.showEmptyScreen();

                                } else {

                                    DebugLog.logD(TAG, "retItems size = " + retItems.size());

                                    // GET Repositories -> Recycler View에 표시한다
                                    repositoryListView.showRepositories(retItems);
                                }

                                repositoryListView.showNoti(ResultCode.SUCCESS);
                            }
                        }

                        @Override
                        public void onError(Throwable e) {

                            // 통신 실패 에러 표시. SnackBar
                            repositoryListView.showNoti(ResultCode.FAIL);
                        }

                        @Override
                        public void onCompleted() {

                            // Do Nothing.
                        }
                    });
        }

    }

    @Override
    public void selectRepositoryItem(GitHubService.RepositoryItem item) {

        repositoryListView.startDetailActivity(item.full_name);
    }
}
