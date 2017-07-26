package io.github.stack07142.trendingingithub.view;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.util.ArrayMap;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.allattentionhere.fabulousfilter.AAH_FabulousFragment;

import java.util.ArrayList;

import io.github.stack07142.trendingingithub.Presenter.RepositoryListPresenter;
import io.github.stack07142.trendingingithub.R;
import io.github.stack07142.trendingingithub.contract.RepositoryListContract;
import io.github.stack07142.trendingingithub.databinding.ActivityRepoListBinding;
import io.github.stack07142.trendingingithub.model.FilterData;
import io.github.stack07142.trendingingithub.model.GitHubService;
import io.github.stack07142.trendingingithub.model.NewGitHubRepoApplication;
import io.github.stack07142.trendingingithub.util.BaseActivityUtil;
import io.github.stack07142.trendingingithub.util.DebugLog;
import io.github.stack07142.trendingingithub.util.FilterPreference;
import io.github.stack07142.trendingingithub.util.ResultCode;

/**
 * interface-View: Presenter -> View 조작
 */
public class RepositoryListActivity extends BaseActivityUtil
        implements RepositoryAdapter.OnRepoItemClickListener, RepositoryListContract.View, AAH_FabulousFragment.Callbacks {

    private static final String TAG = RepositoryListActivity.class.getSimpleName();

    // FAB
    FilterData filterData;
    ArrayMap<String, ArrayList<String>> applied_filters = new ArrayMap<>();

    // Data Binding
    private ActivityRepoListBinding mBinding;

    // View는 Presenter로 직접 접근하지 않는다. Interface를 통해 접근한다.
    private RepositoryListContract.UserAction repositoryListPresenter;

    // RecyclerView Adapter
    private RepositoryAdapter repositoryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_repo_list);

        setupViews();

        // GitHubService 인스턴스 생성
        final GitHubService gitHubService = ((NewGitHubRepoApplication) getApplication()).getGitHubService();

        // Presenter의 인스턴스 생성
        repositoryListPresenter = new RepositoryListPresenter((RepositoryListContract.View) this, gitHubService);

        // FAB Listener
        mBinding.fab.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                MyFabFragment dialogFrag = MyFabFragment.newInstance();
                dialogFrag.setParentFab(mBinding.fab);
                dialogFrag.show(getSupportFragmentManager(), dialogFrag.getTag());
            }
        });

        // FAB
        filterData = new FilterData();

        applied_filters.put(FilterPreference.LANGUAGE, FilterPreference.getStringArrayPref(getApplicationContext(), FilterPreference.LANGUAGE));
        applied_filters.put(FilterPreference.CREATED, FilterPreference.getStringArrayPref(getApplicationContext(), FilterPreference.CREATED));

        // 최초 Query
        repositoryListPresenter.selectLanguage(applied_filters.get(FilterPreference.LANGUAGE), applied_filters.get(FilterPreference.CREATED).get(0));
    }

    /**
     * 목록 등 화면 요소를 만든다
     */
    private void setupViews() {

        // Toolbar
        setSupportActionBar(mBinding.toolbar);

        // Recycler View
        mBinding.contentLayout.recyclerRepos.setLayoutManager(new LinearLayoutManager(this));

        // Recycler View - set adapter, adapter 생성 시 OnRepoItemClickListener이 구현되었음을 보장
        repositoryAdapter = new RepositoryAdapter((Context) this, (RepositoryAdapter.OnRepoItemClickListener) this);
        mBinding.contentLayout.recyclerRepos.setAdapter(repositoryAdapter);

        // Spinner set adapter
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item);
        adapter.addAll("All", "java", "objective-c", "swift", "groovy", "python", "ruby", "c");
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        mBinding.languageSpinner.setAdapter(adapter);

        // Spinner Item Select Listener
        mBinding.languageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                // 선택시 뿐만 아니라 처음에도 호출된다
                String language = (String) mBinding.languageSpinner.getItemAtPosition(position);

                // Presenter에 Event 발생을 통지한다
                //repositoryListPresenter.selectLanguage(language, "this week");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

                // Do Nothing
            }
        });
    }

    /**
     * Repo Item이 클릭되면 상세 화면으로 이동한다
     */
    @Override
    public void onRepositoryItemClick(GitHubService.RepositoryItem item) {

        // Presenter에 Event 발생을 통지한다
        repositoryListPresenter.selectRepositoryItem(item);
    }

    /**
     * Contract View interface 구현
     * Presenter로부터 지시를 받아 View 변경을 한다
     */

    @Override
    public String getSelectedLanguage() {

        return (String) mBinding.languageSpinner.getSelectedItem();
    }

    @Override
    public void showProgress() {

        mBinding.progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgress() {

        mBinding.progressBar.setVisibility(View.GONE);
    }

    @Override
    public void showRepositories(GitHubService.Repositories repositories) {

        // GET Repositories -> Recycler view adapter에 설정한다
        repositoryAdapter.setItemsAndRefresh(repositories.items);
    }

    @Override
    public void showNoti(@ResultCode.Result int result) {

        String resultText = "";

        if (result == ResultCode.SUCCESS) {

            resultText = getString(R.string.result_success);
        } else if (result == ResultCode.FAIL) {

            resultText = getString(R.string.result_fail);
        }

        Snackbar.make(mBinding.coordinatorLayout, resultText, Snackbar.LENGTH_LONG).setAction("Action", null).show();
    }

    @Override
    public void startDetailActivity(String fullRepositoryName) {

        DetailRepositoryActivity.start(this, fullRepositoryName);
    }

    /**
     * AAH_FabulousFragment.Callbacks implements
     */

    @Override
    public void onResult(Object result) {

        DebugLog.logD(TAG, "filter - onResult:  " + result.toString());

        if (result.toString().equalsIgnoreCase("swiped_down")) {

            DebugLog.logD(TAG, "filter - onResult: <<swiped_down>>, DO NOTHING.");
        } else {

            DebugLog.logD(TAG, "filter - onResult: " + result.toString());

            applied_filters = (ArrayMap<String, ArrayList<String>>) result;

            // Query
            repositoryListPresenter.selectLanguage(applied_filters.get(FilterPreference.LANGUAGE), applied_filters.get(FilterPreference.CREATED).get(0));
        }
    }

}
