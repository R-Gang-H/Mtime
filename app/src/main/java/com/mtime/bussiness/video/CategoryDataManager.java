package com.mtime.bussiness.video;

import androidx.collection.SparseArrayCompat;

import com.mtime.base.network.NetworkException;
import com.mtime.bussiness.video.bean.CategoryVideosBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JiaJunHui on 2018/6/25.
 */
public class CategoryDataManager {

    private final String TAG = "CategoryDataManager";

    private final int PAGE_SIZE = 10;
    private static CategoryDataManager i;
    private final SparseArrayCompat<CategoryData> mCategoryDataArray;

    private Params mParams;

    private int mCurrentCategoryType = Integer.MAX_VALUE;

    private CategoryDataManager() {
        mCategoryDataArray = new SparseArrayCompat<>();
    }

    public static CategoryDataManager get() {
        if (null == i) {
            synchronized (CategoryDataManager.class) {
                if (null == i) {
                    i = new CategoryDataManager();
                }
            }
        }
        return i;
    }

    public void init(int movieId, List<CategoryVideosBean.Category> categories) {
        mParams = new Params();
        mParams.movieId = movieId;
        mParams.categories = categories;
    }

    private void updateCategoryData(int categoryType, CategoryData categoryData) {
        mCategoryDataArray.put(categoryType, categoryData);
    }

    public List<CategoryVideosBean.Category> getCategoryList() {
        if (mParams != null) {
            return mParams.categories;
        }
        return null;
    }

    public int getMovieId() {
        if (mParams != null) {
            return mParams.movieId;
        }
        return -1;
    }

    public boolean isListComplete() {
        if (mParams == null || mCategoryDataArray.size() <= 0)
            return true;
        if (mParams.categories == null)
            return true;
        List<CategoryVideosBean.Category> categories = mParams.categories;
        int size = categories.size();
        if (size <= 0)
            return true;
        int currentCategoryType = getCurrentCategoryType();
        int categoryTypeIndex = getCategoryTypeIndex(currentCategoryType);
        if (categoryTypeIndex != size - 1)
            return false;
        int listIndex = getListIndex(currentCategoryType);
        CategoryData cacheCategoryData = getCacheCategoryData(currentCategoryType);
        if (cacheCategoryData == null || !cacheCategoryData.isListAvailable()) {
            return true;
        }
        return listIndex == cacheCategoryData.getAllItems().size() - 1;
    }

    public int getCategoryTypeIndex(int categoryType) {
        int index = -1;
        if (mParams != null && mParams.categories != null) {
            List<CategoryVideosBean.Category> categories = mParams.categories;
            int size = categories.size();
            for (int i = 0; i < size; i++) {
                if (categoryType == categories.get(i).getType()) {
                    index = i;
                    break;
                }
            }
        }
        return index;
    }

    public int getCurrentCategoryIndex() {
        return getCategoryTypeIndex(mCurrentCategoryType);
    }

    public int getCategoryTypePageIndex(int categoryType) {
        CategoryData cacheCategoryData = getCacheCategoryData(categoryType);
        if (cacheCategoryData != null) {
            return cacheCategoryData.getPageIndex();
        }
        return 1;
    }

    private int getCategoryTypeListSize() {
        if (mParams != null && mParams.categories != null) {
            return mParams.categories.size();
        }
        return 0;
    }

    private int getNextCategoryType() {
        if (mParams != null && mParams.categories != null) {
            List<CategoryVideosBean.Category> categories = mParams.categories;
            int categoryTypeIndex = getCategoryTypeIndex(mCurrentCategoryType);
            if (categoryTypeIndex != -1) {
                int size = getCategoryTypeListSize();
                if (categoryTypeIndex >= size - 1) {
                    return Integer.MAX_VALUE;
                } else {
                    categoryTypeIndex++;
                    return categories.get(categoryTypeIndex).getType();
                }
            }
        }
        return Integer.MAX_VALUE;
    }

    public void updateCurrentCategoryType(int categoryType) {
        mCurrentCategoryType = categoryType;
    }

    public int getCurrentCategoryType() {
        return mCurrentCategoryType;
    }

    public void updateListIndex(int categoryType, int listIndex) {
        CategoryData categoryData = mCategoryDataArray.get(categoryType);
        if (categoryData != null) {
            categoryData.setListIndex(listIndex);
        }
    }

    public CategoryVideosBean.RecommendVideoItem getItem(int categoryType, int position) {
        CategoryData data = getCacheCategoryData(categoryType);
        if (data != null && data.isListAvailable()) {
            List<CategoryVideosBean.RecommendVideoItem> items = data.getAllItems();
            int size = items.size();
            return position <= size - 1 ? items.get(position) : null;
        }
        return null;
    }

    public CategoryVideosBean.RecommendVideoItem getCurrentItem() {
        return getItem(getCurrentCategoryType(), getListIndex(getCurrentCategoryType()));
    }

    public void next(OnNextVideoListener onNextVideoListener) {
        int listIndex = getListIndex(mCurrentCategoryType);
        listIndex++;
        CategoryData data = getCacheCategoryData(mCurrentCategoryType);
        if (data != null) {
            List<CategoryVideosBean.RecommendVideoItem> allItems = data.getAllItems();
            int size = allItems.size();
            //from current cache list
            if (listIndex <= size - 1) {
                CategoryVideosBean.RecommendVideoItem videoItem = allItems.get(listIndex);
                //update category and list index
                updateListIndex(mCurrentCategoryType, listIndex);
                onCallbackNextVideo(videoItem, mCurrentCategoryType, listIndex, onNextVideoListener);
            } else {
                if (!data.isLoadFinish()) {
                    //current next page
                    int pageIndex = data.getPageIndex();
                    pageIndex++;
                    int _ListIndex = data.getAllItems().size();
                    loadCategoryListData(mCurrentCategoryType, pageIndex, new OnCategoryListLoadListener() {
                        @Override
                        public void onDataReady(List<CategoryVideosBean.RecommendVideoItem> videoItems, boolean loadFinish) {
                            if (videoItems != null && videoItems.size() > 0) {
                                //update category and list index
                                updateListIndex(mCurrentCategoryType, _ListIndex);
                                onCallbackNextVideo(videoItems.get(0), mCurrentCategoryType, _ListIndex, onNextVideoListener);
                            }
                        }

                        @Override
                        public void onFailure() {
                            if (onNextVideoListener != null)
                                onNextVideoListener.onFailure();
                        }
                    });
                } else {
                    //next category
                    int nextCategoryType = getNextCategoryType();
                    if (nextCategoryType == Integer.MAX_VALUE) {
                        onCallbackNextVideo(null, nextCategoryType, -1, onNextVideoListener);
                        return;
                    }
                    //update current category type
                    updateCurrentCategoryType(nextCategoryType);
                    CategoryData categoryData = getCacheCategoryData(nextCategoryType);
                    if (categoryData != null && categoryData.isListAvailable()) {
                        //get from cache
                        List<CategoryVideosBean.RecommendVideoItem> items = categoryData.getAllItems();
                        //update category and list index
                        updateListIndex(nextCategoryType, 0);
                        onCallbackNextVideo(items.get(0), nextCategoryType, 0, onNextVideoListener);
                    } else {
                        //api load
                        int pageIndex = 1;
                        int listIndex1 = 0;
                        if (categoryData != null) {
                            pageIndex = categoryData.getPageIndex();
                            listIndex1 = categoryData.getListIndex();
                        }
                        if (pageIndex > 1) {
                            listIndex1 += 1;
                        }
                        final int assertIndex = listIndex1;
                        loadCategoryListData(nextCategoryType, pageIndex, new OnCategoryListLoadListener() {
                            @Override
                            public void onDataReady(List<CategoryVideosBean.RecommendVideoItem> videoItems, boolean loadFinish) {
                                if (videoItems != null && videoItems.size() > 0) {
                                    //update category and list index
                                    updateListIndex(nextCategoryType, assertIndex);
                                    onCallbackNextVideo(videoItems.get(0), nextCategoryType, assertIndex, onNextVideoListener);
                                }
                            }

                            @Override
                            public void onFailure() {
                                if (onNextVideoListener != null)
                                    onNextVideoListener.onFailure();
                            }
                        });
                    }
                }
            }
        }
    }

    private void onCallbackNextVideo(CategoryVideosBean.RecommendVideoItem item, int type, int index, OnNextVideoListener onNextVideoListener) {
        if (onNextVideoListener != null)
            onNextVideoListener.onNextReady(item, type, index);
    }

    public int getListIndex(int categoryType) {
        CategoryData categoryData = mCategoryDataArray.get(categoryType);
        if (categoryData != null) {
            return categoryData.getListIndex();
        }
        return -1;
    }

    private boolean isCategoryTypeLoadFinish(int categoryType) {
        CategoryData categoryData = mCategoryDataArray.get(categoryType);
        return categoryData != null && categoryData.isLoadFinish();
    }

    public void loadCategoryListData(int categoryType, int pageIndex, OnCategoryListLoadListener onCategoryListLoadListener) {
        if (mParams == null)
            return;
        List<CategoryVideosBean.RecommendVideoItem> cacheListItems = getCacheListByPageIndex(categoryType, pageIndex);
        if (cacheListItems != null) {
            onCallbackCategoryListData(isCategoryTypeLoadFinish(categoryType), cacheListItems, onCategoryListLoadListener);
            return;
        }
        apiLoadData(categoryType, pageIndex, new OnApiLoadListener() {
            @Override
            public void onLoadSuccess(CategoryVideosBean result, String showMsg) {
                CategoryData cacheCategoryData = getCacheCategoryData(categoryType);
                if (cacheCategoryData == null) {
                    cacheCategoryData = new CategoryData();
                }
                cacheCategoryData.setCategoryType(categoryType);
                cacheCategoryData.setPageIndex(pageIndex);
                if (result != null && result.hasListData()) {
                    List<CategoryVideosBean.RecommendVideoItem> videoList = result.getVideoList();
                    cacheCategoryData.setLoadFinish(videoList.size() < PAGE_SIZE);
                    cacheCategoryData.putVideoItems(pageIndex, videoList);
                    onCallbackCategoryListData(cacheCategoryData.isLoadFinish(), videoList, onCategoryListLoadListener);
                } else {
                    cacheCategoryData.setLoadFinish(true);
                    onCallbackCategoryListData(cacheCategoryData.isLoadFinish(), null, onCategoryListLoadListener);
                }
                updateCategoryData(categoryType, cacheCategoryData);
            }

            @Override
            public void onLoadFailure() {
                if (onCategoryListLoadListener != null)
                    onCategoryListLoadListener.onFailure();
            }
        });
    }

    private void onCallbackCategoryListData(boolean loadFinish, List<CategoryVideosBean.RecommendVideoItem> items, OnCategoryListLoadListener onCategoryListLoadListener) {
        if (onCategoryListLoadListener != null) {
            onCategoryListLoadListener.onDataReady(items, loadFinish);
        }
    }

    public void apiLoadData(int categoryType, int pageIndex, OnApiLoadListener onApiLoadListener) {
        VideoListApiHelper.get().loadVideos(TAG, mParams.movieId, pageIndex, categoryType, new VideoListApiHelper.OnApiLoadListener() {
            @Override
            public void onSuccess(CategoryVideosBean result, String showMsg) {
                if (onApiLoadListener != null) {
                    onApiLoadListener.onLoadSuccess(result, showMsg);
                }
            }

            @Override
            public void onFailure(NetworkException<CategoryVideosBean> exception, String showMsg) {
                if (onApiLoadListener != null) {
                    onApiLoadListener.onLoadFailure();
                }
            }
        });
    }

    public CategoryData getCacheCategoryData(int categoryType) {
        return mCategoryDataArray.get(categoryType);
    }

    private List<CategoryVideosBean.RecommendVideoItem> getCacheListByPageIndex(int categoryType, int pageIndex) {
        CategoryData categoryData = mCategoryDataArray.get(categoryType);
        if (categoryData != null) {
            return categoryData.getVideoItems(pageIndex);
        }
        return null;
    }

    public void destroy() {
        mCategoryDataArray.clear();
        mParams = null;
        mCurrentCategoryType = Integer.MAX_VALUE;
        i = null;
    }

    interface OnApiLoadListener {
        void onLoadSuccess(CategoryVideosBean result, String showMsg);

        void onLoadFailure();
    }

    public interface OnCategoryListLoadListener {
        void onDataReady(List<CategoryVideosBean.RecommendVideoItem> videoItems, boolean loadFinish);

        void onFailure();
    }

    public interface OnNextVideoListener {
        void onNextReady(CategoryVideosBean.RecommendVideoItem item, int type, int index);

        void onFailure();
    }

    public static class Params {
        public int movieId;
        public List<CategoryVideosBean.Category> categories;
    }

    public static class CategoryData {
        private int categoryType;
        private int listIndex;
        private int pageIndex;
        private boolean loadFinish;
        private final SparseArrayCompat<List<CategoryVideosBean.RecommendVideoItem>> itemArray;

        public CategoryData() {
            itemArray = new SparseArrayCompat<>();
        }

        public int getCategoryType() {
            return categoryType;
        }

        public void setCategoryType(int categoryType) {
            this.categoryType = categoryType;
        }

        public int getListIndex() {
            return listIndex;
        }

        public void setListIndex(int listIndex) {
            this.listIndex = listIndex;
        }

        public int getPageIndex() {
            return pageIndex;
        }

        public void setPageIndex(int pageIndex) {
            this.pageIndex = pageIndex;
        }

        public boolean isLoadFinish() {
            return loadFinish;
        }

        public void setLoadFinish(boolean loadFinish) {
            this.loadFinish = loadFinish;
        }

        public void putVideoItems(int pageIndex, List<CategoryVideosBean.RecommendVideoItem> items) {
            itemArray.put(pageIndex, items);
        }

        public List<CategoryVideosBean.RecommendVideoItem> getVideoItems(int pageIndex) {
            return itemArray.get(pageIndex);
        }

        public boolean isListAvailable() {
            return getAllItems().size() > 0;
        }

        public List<CategoryVideosBean.RecommendVideoItem> getAllItems() {
            int size = itemArray.size();
            List<CategoryVideosBean.RecommendVideoItem> items = new ArrayList<>();
            for (int i = 0; i < size; i++) {
                items.addAll(itemArray.valueAt(i));
            }
            return items;
        }

    }

}
