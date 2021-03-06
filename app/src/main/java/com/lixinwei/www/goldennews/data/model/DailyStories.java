
package com.lixinwei.www.goldennews.data.model;

import java.util.List;
import javax.annotation.Generated;
import com.google.gson.annotations.SerializedName;

@Generated("net.hexar.json2pojo")
@SuppressWarnings("unused")
public class DailyStories {

    @SerializedName("date")
    private String mDate;
    @SerializedName("stories")
    private List<Story> mStories;
    @SerializedName("top_stories")
    private List<TopStory> mTopStories;

    public String getDate() {
        return mDate;
    }

    public void setDate(String date) {
        mDate = date;
    }

    public List<Story> getStories() {
        return mStories;
    }

    public void setStories(List<Story> stories) {
        mStories = stories;
    }

    public List<TopStory> getTopStories() {
        return mTopStories;
    }

    public void setTopStories(List<TopStory> topStories) {
        mTopStories = topStories;
    }

}
