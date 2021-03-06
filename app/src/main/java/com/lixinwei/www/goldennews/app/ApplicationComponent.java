package com.lixinwei.www.goldennews.app;

import com.lixinwei.www.goldennews.DateNewsList.DateNewsListModule;
import com.lixinwei.www.goldennews.DateNewsList.DateNewsListSubComponent;
import com.lixinwei.www.goldennews.commentslist.CommentsModule;
import com.lixinwei.www.goldennews.commentslist.CommentsSubComponent;
import com.lixinwei.www.goldennews.data.Realm.RealmServiceModule;
import com.lixinwei.www.goldennews.data.domain.ZhihuApiModule;
import com.lixinwei.www.goldennews.likedlist.LikedListModule;
import com.lixinwei.www.goldennews.likedlist.LikedListSubComponent;
import com.lixinwei.www.goldennews.newsDetail.NewsDetailModule;
import com.lixinwei.www.goldennews.newsDetail.NewsDetailObservableManager;
import com.lixinwei.www.goldennews.newsDetail.NewsDetailSubComponent;
import com.lixinwei.www.goldennews.newslist.NewsListActivity;
import com.lixinwei.www.goldennews.newslist.NewsListSubComponent;
import com.lixinwei.www.goldennews.newslist.NewsListModule;
import com.lixinwei.www.goldennews.services.PollService;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by welding on 2017/6/29.
 */
@Singleton
@Component (
        modules = {
                ApplicationModule.class,
                ZhihuApiModule.class,
                RealmServiceModule.class
        }
)
public interface ApplicationComponent {

    void inject(PollService service);

    NewsListSubComponent plus(NewsListModule module);

    LikedListSubComponent plus(LikedListModule module);

    CommentsSubComponent plus(CommentsModule module);

    NewsDetailSubComponent plus(NewsDetailModule module);

    DateNewsListSubComponent plus(DateNewsListModule module);
}
