package com.mtime.bussiness.mine.bean;


import com.mtime.base.bean.MBaseBean;
import com.mtime.beans.ArticleInfoBean;

import java.util.List;

public class OfficialAccountListBean extends MBaseBean {

    private int count;
    private List<ListBeanX> list;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<ListBeanX> getList() {
        return list;
    }

    public void setList(List<ListBeanX> list) {
        this.list = list;
    }

    public static class ListBeanX extends MBaseBean {

        public static final int ARTICLE_TYPE_ARTICLE = 1;
        public static final int ARTICLE_TYPE_RANK = 2;
        public static final int ARTICLE_TYPE_VIDEO = 3;

        private long articleId;
        private int articleType;//文章类型 1图文新闻，2榜单，3视频
        private int type;
        private String title;
        private String image;
        private String videoUrl;
        private String introduction;
        private TopListBean topList;
        private ArticleInfoBean.ListBean listBean;

        public long getArticleId() {
            return articleId;
        }

        public void setArticleId(long articleId) {
            this.articleId = articleId;
        }

        public int getArticleType() {
            return articleType;
        }

        public void setArticleType(int articleType) {
            this.articleType = articleType;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public String getVideoUrl() {
            return videoUrl;
        }

        public void setVideoUrl(String videoUrl) {
            this.videoUrl = videoUrl;
        }

        public String getIntroduction() {
            return introduction;
        }

        public void setIntroduction(String introduction) {
            this.introduction = introduction;
        }

        public TopListBean getTopList() {
            return topList;
        }

        public void setTopList(TopListBean topList) {
            this.topList = topList;
        }

        public ArticleInfoBean.ListBean getListBean() {
            return listBean;
        }

        public void setListBean(ArticleInfoBean.ListBean listBean) {
            this.listBean = listBean;
        }

        public static class TopListBean extends MBaseBean {
            /**
             * relatedType : 1
             * total : 10
             * list : [{"relatedId ":58244,"name ":"地球脉动","nameEn":"En","posterUrl":"http: //img31.mtime.cn/mt/244/58244/58244_96X128.jpg"}]
             */

            private int relatedType;
            private int total;
            private List<ListBean> list;

            public int getRelatedType() {
                return relatedType;
            }

            public void setRelatedType(int relatedType) {
                this.relatedType = relatedType;
            }

            public int getTotal() {
                return total;
            }

            public void setTotal(int total) {
                this.total = total;
            }

            public List<ListBean> getList() {
                return list;
            }

            public void setList(List<ListBean> list) {
                this.list = list;
            }

            public static class ListBean extends MBaseBean {
                /**
                 * relatedId  : 58244
                 * name  : 地球脉动
                 * nameEn : En
                 * posterUrl : http: //img31.mtime.cn/mt/244/58244/58244_96X128.jpg
                 */

                private int relatedId;
                private String name;
                private String nameEn;
                private String posterUrl;

                public int getRelatedId() {
                    return relatedId;
                }

                public void setRelatedId(int relatedId) {
                    this.relatedId = relatedId;
                }

                public String getName() {
                    return name;
                }

                public void setName(String name) {
                    this.name = name;
                }

                public String getNameEn() {
                    return nameEn;
                }

                public void setNameEn(String nameEn) {
                    this.nameEn = nameEn;
                }

                public String getPosterUrl() {
                    return posterUrl;
                }

                public void setPosterUrl(String posterUrl) {
                    this.posterUrl = posterUrl;
                }
            }
        }
    }
}
