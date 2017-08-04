package io.github.stack07142.trendingingithub.view;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.util.ArrayMap;
import android.support.v7.widget.LinearLayoutManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.allattentionhere.fabulousfilter.AAH_FabulousFragment;
import com.mikepenz.aboutlibraries.Libs;
import com.mikepenz.aboutlibraries.LibsBuilder;

import java.util.ArrayList;

import io.github.stack07142.trendingingithub.Presenter.RepositoryListPresenter;
import io.github.stack07142.trendingingithub.R;
import io.github.stack07142.trendingingithub.contract.RepositoryListContract;
import io.github.stack07142.trendingingithub.databinding.ActivityRepoListBinding;
import io.github.stack07142.trendingingithub.model.FilterData;
import io.github.stack07142.trendingingithub.model.FilterPreferenceData;
import io.github.stack07142.trendingingithub.model.GitHubRepoService;
import io.github.stack07142.trendingingithub.model.NewGitHubRepoApplication;
import io.github.stack07142.trendingingithub.util.BaseActivityUtil;
import io.github.stack07142.trendingingithub.util.DebugLog;
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

        final Typeface tf = Typeface.createFromAsset(getApplicationContext().getAssets(), "font/dosis_medium.otf");
        mBinding.toolbarLayout.setCollapsedTitleTypeface(tf);
        mBinding.toolbarLayout.setExpandedTitleTypeface(tf);

        setupViews();

        // GitHubRepoService 인스턴스 생성
        final GitHubRepoService gitHubRepoService = ((NewGitHubRepoApplication) getApplication()).getGitHubRepoService();

        // Presenter의 인스턴스 생성
        repositoryListPresenter = new RepositoryListPresenter((RepositoryListContract.View) this, gitHubRepoService);

        // FAB Listener
        mBinding.fab.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                MyFabFragment dialogFrag = MyFabFragment.newInstance();
                dialogFrag.setParentFab(mBinding.fab);
                dialogFrag.show(getSupportFragmentManager(), dialogFrag.getTag());
            }
        });

        // FAB - FilterData
        filterData = new FilterData();

        // FAB - Applied Languages
        applied_filters.put(FilterPreferenceData.LANGUAGE, FilterPreferenceData.getStringArrayPref(getApplicationContext(), FilterPreferenceData.LANGUAGE));
        applied_filters.put(FilterPreferenceData.CREATED, FilterPreferenceData.getStringArrayPref(getApplicationContext(), FilterPreferenceData.CREATED));

        // 최초 Repo Query
        repositoryListPresenter.selectLanguage(applied_filters.get(FilterPreferenceData.LANGUAGE), applied_filters.get(FilterPreferenceData.CREATED).get(0));
    }

    /**
     * Toolbar - Option Menu
     */

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.oss_license:
                new LibsBuilder()
                        .withActivityStyle(Libs.ActivityStyle.LIGHT_DARK_TOOLBAR)
                        .withActivityTitle(getString(R.string.oss_license))
                        .withAboutIconShown(true)
                        .withAboutDescription(getString(R.string.opensource_license_notice))
                        .withAutoDetect(true)
                        .withLicenseShown(true)
                        .withVersionShown(true)
                        .start(this);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
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
    }

    /**
     * Repo Item이 클릭되면 상세 화면으로 이동한다
     */
    @Override
    public void onRepositoryItemClick(GitHubRepoService.RepositoryItem item) {

        // Presenter에 Event 발생을 통지한다
        repositoryListPresenter.selectRepositoryItem(item);
    }

    /**
     * Contract View interface 구현
     * Presenter로부터 지시를 받아 View 변경을 한다
     */

    @Override
    public void showProgress() {

        mBinding.progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgress() {

        mBinding.progressBar.setVisibility(View.GONE);
    }

    @Override
    public void showRepositories(ArrayList<GitHubRepoService.RepositoryItem> repositories) {

        mBinding.contentLayout.recyclerRepos.setVisibility(View.VISIBLE);
        mBinding.contentLayout.emptyLayout.setVisibility(View.GONE);

        repositoryAdapter.setItemsAndRefresh(repositories);
    }

    @Override
    public void showEmptyScreen() {

        DebugLog.logD(TAG, "showEmptyScreen()");

        mBinding.contentLayout.recyclerRepos.setVisibility(View.GONE);
        mBinding.contentLayout.emptyLayout.setVisibility(View.VISIBLE);
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
            repositoryListPresenter.selectLanguage(applied_filters.get(FilterPreferenceData.LANGUAGE), applied_filters.get(FilterPreferenceData.CREATED).get(0));

            // Edited 이후 다시 열기
            if (FilterPreferenceData.editedFlag) {

                FilterPreferenceData.editedFlag = false;

                MyFabFragment dialogFrag = MyFabFragment.newInstance();
                dialogFrag.setParentFab(mBinding.fab);
                dialogFrag.show(getSupportFragmentManager(), dialogFrag.getTag());

                Toast.makeText(this, getString(R.string.noti_edited), Toast.LENGTH_SHORT).show();
            }
        }
    }
}
