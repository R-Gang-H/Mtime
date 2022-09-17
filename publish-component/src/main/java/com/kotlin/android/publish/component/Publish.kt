package com.kotlin.android.publish.component

/**
 *
 * Created on 2020/10/15.
 *
 * @author o.s
 */
object Publish {

    const val FAMILY_LIST_REQUEST_CODE = 10066
    const val FAMILY_LIST_RESULT_CODE = 10066

    const val KEY_TYPE = "publish_key_type"
    const val KEY_MOVIE_ID = "publish_key_movie_id"
    const val KEY_MOVIE_NAME = "publish_key_movie_name"
    const val KEY_FAMILY_ID = "publish_key_family_id"
    const val KEY_FAMILY_NAME = "publish_key_family_name"
    const val KEY_CONTENT_ID = "publish_key_content_id"
    const val KEY_IS_FOOTMARKS = "publish_key_is_footmarks"
    const val KEY_IS_LONG_COMMENT = "publish_key_is_long_comment"
    const val KEY_VIDEO_PATH = "publish_key_video_path"
    const val KEY_RECORD_ID = "publish_key_rec_id"

    const val PUBLISH_SCORE_LEVEL_0 = 0

    const val MAX_LABEL_LIMIT = 10
    /**
     * 图集最大数限制
     */
    const val MAX_IMAGES_LIMIT = 100

    /**
     * 上传图片最大数限制
     */
    const val MAX_UPLOAD_IMAGES_LIMIT = 9

    /**
     * 添加图片最大限制
     */
    const val MAX_ADD_IMAGE_LIMIT = 20

    /**
     * 添加视频最大限制
     */
    const val MAX_ADD_VIDEO_LIMIT = 5

    /**
     * 添加电影最大限制
     */
    const val MAX_ADD_MOVIE_LIMIT = 20

    /**
     * 文章字数最大限制
     */
    const val MAX_ARTICLE_TEXT_COUNT_LIMIT = 10000

    /**
     * 影评字数最大限制
     */
    const val MAX_FILM_COMMENT_TEXT_COUNT_LIMIT = 210

    /**
     * 长影评字数最大限制
     */
    const val MAX_LONG_COMMENT_TEXT_COUNT_LIMIT = 100000

    /**
     * 标题字数最大限制
     */
    const val MAX_TITLE_TEXT_COUNT_LIMIT = 50

    /**
     * 家族数量最大限制
     */
    const val MAX_GROUP_COUNT_LIMIT = 5

    const val EDITOR_CUSTOM = "自定义"
    const val EDITOR_EDITOR_NONE = "无署名"
    const val EDITOR_SOURCE_NONE = "无来源"
}