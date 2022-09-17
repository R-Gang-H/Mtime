package com.kotlin.android.community.family.component.ui.manage.constant

/**
 * @author vivian.wei
 * @date 2020/8/6
 * @desc 家族常量类
 */
class FamilyConstant {
    companion object {
        // 编辑信息页_内容类型
        const val FAMILY_EDIT_INFO_TYPE_NAME = 1
        const val FAMILY_EDIT_INFO_TYPE_DES = 2

        // key
        const val KEY_FAMILY_ID = "family_id"
        const val KEY_FAMILY_EDIT_INFO_CONTENT = "family_edit_info_content"
        const val KEY_FAMILY_EDIT_INFO_TYPE = "family_edit_info_type"
        const val KEY_FAMILY_PERMISSION = "family_permission"
        const val KEY_FAMILY_PUBLISH_PERMISSION = "family_publish_permission"
        const val KEY_FAMILY_SECTION_COUNT = "family_section_count"
        const val KEY_FAMILY_PRIMARY_CATEGORY_ID = "family_primary_category_id"
        const val KEY_FAMILY_PRIMARY_CATEGORY_NAME = "family_primary_category_name"
        const val KEY_FAMILY_MEMBER_TYPE = "family_member_type"

        // requestCode
        const val REQUEST_CODE_UPLOAD_IMG_BY_CAMERA = 101
        const val REQUEST_CODE_UPLOAD_IMG_BY_PHOTO = 102
        const val REQUEST_CODE_CHANGE_NAME_DES = 200
        const val REQUEST_CODE_CHANGE_PERMISSION: Int = 400
        const val REQUEST_CODE_CHANGE_CATEGORY = 500
        const val REQUEST_CODE_ADMINISTRATOR = 600
        const val REQUEST_CODE_MEMBER = 700
        const val REQUEST_ADD_ADMINISTRATOR_CODE = 800
        const val REQUEST_CODE_MEMBER_MANAGE = 900
        const val REQUEST_CODE_CHANGE_PUBLISH_PERMISSION = 922
        const val REQUEST_CODE_CHANGE_SECTION = 926

        // resultCode
        val RESULT_CODE_ADD_ADMINISTRATOR: Int = 10001

        //0-未加入，1-已加入成功，2-加入中（待审核），3-黑名单
        val CONSTANT_STATE_0 = 0L
        val CONSTANT_STATE_1 = 1L
        val CONSTANT_STATE_2 = 2L
        val CONSTANT_STATE_3 = 3L
        val CONSTANT_STATE_4 = 4L
    }
}