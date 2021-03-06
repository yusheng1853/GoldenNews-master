package com.lixinwei.www.goldennews.newslist;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.lixinwei.www.goldennews.app.GoldenNewsApplication;
import com.lixinwei.www.goldennews.R;
import com.lixinwei.www.goldennews.base.BaseFragment;
import com.lixinwei.www.goldennews.commentslist.CommentsActivity;
import com.lixinwei.www.goldennews.data.model.StoryForNewsList;
import com.lixinwei.www.goldennews.data.sharedPreferences.PreferencesServiceImpl;
import com.lixinwei.www.goldennews.likedlist.LikedListActivity;
import com.lixinwei.www.goldennews.newsDetail.NewsDetailActivity;
import com.lixinwei.www.goldennews.services.PollService;
import com.lixinwei.www.goldennews.util.Utils;

import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by welding on 2017/6/29.
 */

public class NewsListFragment extends BaseFragment implements NewsListContract.View {

    @BindView(R.id.recycler_view_news_list)
    RecyclerView mRecyclerView;
    @BindView(R.id.refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;


    @Inject
    NewsListAdapter mNewsListAdapter;

    @Inject
    NewsListContract.Presenter mNewsListPresenter;

    //TODO the differences between the getActivity() as context and the application context??
    @Inject
    Context mContext;

    public static NewsListFragment newInstance() {
        return new NewsListFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_news_list, container, false);

        GoldenNewsApplication.getGoldenNewsApplication(getActivity()).getNewsListSubComponent()
                .inject(this);

        ButterKnife.bind(this, view);

        //customize the progress indicator color
        mSwipeRefreshLayout.setColorSchemeColors(
                ContextCompat.getColor(getActivity(), R.color.colorPrimary),
                ContextCompat.getColor(getActivity(), R.color.colorAccent),
                ContextCompat.getColor(getActivity(), R.color.colorPrimaryDark)
        );

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(!Utils.isConnected(mContext)) {
                    showNetworkErrorSnackbar();
                    setLoadingIndicator(false);
                } else {
                    mNewsListPresenter.loadDailyStories(true);
                }
            }
        });



        initRecyclerView();
        mNewsListPresenter.bindView(this);

        mNewsListPresenter.loadDailyStories(false);

        PollService.setServiceAlarm(getActivity(), PreferencesServiceImpl.isAlarmOn(getActivity()));

        setHasOptionsMenu(true);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        //TODO 为啥要在onResume中重新加载数据删除liked的效果才生效呢？ 通知recyclerView刷新！！
        // 不是backButton自动会从onCreate执行吗，而onCreate中已经加载过数据了啊，还是说现在backButton的行为已经改变？待解决
        mNewsListPresenter.loadDailyStories(false);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_news_list_menu, menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_liked:
                Intent intent = LikedListActivity.newIntent(getActivity());
                startActivity(intent);
                break;
        }
        return true;
    }

    @Override
    public void setLoadingIndicator(final boolean active) {
        if (getView() == null) return;

        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(active);
            }
        });
    }

    @Override
    public void showLikedSnackbar() {
        Snackbar.make(mRecyclerView, "Saved!", Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void showLoadErrorSnackbar() {
        Snackbar.make(mRecyclerView, "Load Error", Snackbar.LENGTH_SHORT).show();
    }

    public void showNetworkErrorSnackbar() {
        Snackbar.make(getActivity().findViewById(android.R.id.content), "Network Error", Snackbar.LENGTH_LONG).show();
        //Snackbar.make(mRecyclerView, "Network Error", Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void startCommentsActivity(long id) {
        Intent intent = CommentsActivity.newIntent(getActivity(), id);
        startActivity(intent);
    }

    @Override
    public void shareNews(StoryForNewsList story) {
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("text/plain");
        i.putExtra(Intent.EXTRA_SUBJECT, story.getTitle());
        i.putExtra(Intent.EXTRA_TEXT, "https://news-at.zhihu.com/api/4/news/" + story.getId());
        startActivity(Intent.createChooser(i, getResources().getString(R.string.chooser_title)));
    }

    @Override
    public void startDetailActivity(long id) {
        Intent intent = NewsDetailActivity.newIntent(getActivity(), id);
        startActivity(intent);
    }

    private void initRecyclerView() {
        //TODO 面试点 由于recyclerView中单个item比较大，一次只加载两个item，而又由于图片的加载过程较慢，这样在正常的滑动
        //过程中就会看到下方条目的图片是空白的，覆盖LinearLayoutManager中getExtraSpace方法来增加recyclerView的加载条目
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity()){
            @Override
            protected int getExtraLayoutSpace(RecyclerView.State state) {
                return 300;
            }
        };

        mRecyclerView.setLayoutManager(linearLayoutManager);
        mNewsListAdapter.bindFragment(this);
        mRecyclerView.setAdapter(mNewsListAdapter);
        mRecyclerView.setItemAnimator(new NewsItemAnimator());
    }

    @Override
    public void showTopStories(List<StoryForNewsList> storyList) {
        mNewsListAdapter.updateStoriesList(storyList);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mNewsListPresenter.unbindView();
        GoldenNewsApplication.getGoldenNewsApplication(getActivity()).releaseNewsListSubComponent();
    }




}
