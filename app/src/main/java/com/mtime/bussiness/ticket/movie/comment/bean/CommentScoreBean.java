package com.mtime.bussiness.ticket.movie.comment.bean;

import androidx.core.util.ObjectsCompat;

/**
 * Created by <a href="mailto:wangkunlin1992@gmail.com">Wang kunlin</a>
 * <p>
 * On 2019-05-21
 */
public class CommentScoreBean extends BaseCommentBean {

    public float wholeScore = 0; // 整体评分

    public float musicScore = 0; // 音乐评分
    public float pictureScore = 0; // 画面评分
    public float directorScore = 0; // 导演评分
    public float storyScore = 0; // 故事评分
    public float actingScore = 0; // 表演评分
    public float impressionsScore = 0; // 印象评分

    public boolean expandDetailPanel = false;

    public boolean useWholeScore;

    public float getWholeScore() {
        if (useWholeScore) {
            return wholeScore;
        }
        wholeScore = musicScore * 0.0495f + pictureScore * 0.1182f + directorScore
                * 0.2364f + storyScore * 0.1912f + actingScore * 0.174f
                + impressionsScore * 0.2307f;
        return wholeScore;
    }

    @Override
    public boolean isEmpty() {
        return true;
    }

    public boolean noScore() {
        return wholeScore <= 0;
    }

    public int subScoreCount() {
        int count = 0;
        if (musicScore > 0) {
            count++;
        }
        if (pictureScore > 0) {
            count++;
        }
        if (directorScore > 0) {
            count++;
        }
        if (storyScore > 0) {
            count++;
        }
        if (actingScore > 0) {
            count++;
        }
        if (impressionsScore > 0) {
            count++;
        }
        return count;
    }

    private CommentScoreBean(Builder builder) {
        this();
        this.wholeScore = builder.wholeScore;
        this.musicScore = builder.musicScore;
        this.pictureScore = builder.pictureScore;
        this.directorScore = builder.directorScore;
        this.storyScore = builder.storyScore;
        this.actingScore = builder.actingScore;
        this.impressionsScore = builder.impressionsScore;
        this.useWholeScore = builder.useWholeScore;
    }

    public CommentScoreBean() {
        super(TYPE_SCORE);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CommentScoreBean that = (CommentScoreBean) o;
        return wholeScore == that.wholeScore &&
                musicScore == that.musicScore &&
                pictureScore == that.pictureScore &&
                directorScore == that.directorScore &&
                storyScore == that.storyScore &&
                actingScore == that.actingScore &&
                impressionsScore == that.impressionsScore &&
                mType == that.mType;
    }

    @Override
    public int hashCode() {
        return ObjectsCompat.hash(wholeScore, musicScore, pictureScore, directorScore,
                storyScore, actingScore, impressionsScore, expandDetailPanel, mType);
    }

    @Override
    public String toString() {
        return "CommentScoreBean{" +
                "wholeScore=" + wholeScore +
                ", musicScore=" + musicScore +
                ", pictureScore=" + pictureScore +
                ", directorScore=" + directorScore +
                ", storyScore=" + storyScore +
                ", actingScore=" + actingScore +
                ", impressionsScore=" + impressionsScore +
                ", useWholeScore=" + useWholeScore +
                '}';
    }

    @Override
    public BaseCommentBean copy() {
        return new Builder(wholeScore)
                .story(storyScore)
                .picture(pictureScore)
                .music(musicScore)
                .impressions(impressionsScore)
                .director(directorScore)
                .acting(actingScore)
                .build();
    }

    public static class Builder {
        private final float wholeScore; // 整体评分

        private float musicScore; // 音乐评分
        private float pictureScore; // 画面评分
        private float directorScore; // 导演评分
        private float storyScore; // 故事评分
        private float actingScore; // 表演评分
        private float impressionsScore; // 印象评分
        private boolean useWholeScore;

        public Builder(float wholeScore) {
            this.wholeScore = wholeScore;
        }

        public Builder music(float musicScore) {
            this.musicScore = musicScore;
            return this;
        }

        public Builder picture(float pictureScore) {
            this.pictureScore = pictureScore;
            return this;
        }

        public Builder director(float directorScore) {
            this.directorScore = directorScore;
            return this;
        }

        public Builder story(float storyScore) {
            this.storyScore = storyScore;
            return this;
        }

        public Builder acting(float actingScore) {
            this.actingScore = actingScore;
            return this;
        }

        public Builder impressions(float impressionsScore) {
            this.impressionsScore = impressionsScore;
            return this;
        }

        public Builder useWholeScore(boolean whole) {
            useWholeScore = whole;
            return this;
        }

        public CommentScoreBean build() {
            return new CommentScoreBean(this);
        }
    }
}
