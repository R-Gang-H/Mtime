package com.kotlin.android.publish.component.widget.selector

import android.content.pm.ActivityInfo
import android.os.Parcelable
import com.kotlin.android.app.data.ProguardRule
import com.kotlin.android.publish.component.widget.selector.PictureSelectionConfig
import kotlinx.android.parcel.Parcelize

@Parcelize
class PictureSelectionConfig : Parcelable ,ProguardRule{
    @JvmField
    var chooseMode = 0
    var isOnlyCamera = false
    var isDirectReturnSingle = false
    var cameraImageFormat: String? = null
    var cameraVideoFormat: String? = null
    var cameraImageFormatForQ: String? = null
    var cameraVideoFormatForQ: String? = null
    var requestedOrientation = 0
    var isCameraAroundState = false
    var selectionMode = 0
    var maxSelectNum = 0
    var minSelectNum = 0
    var maxVideoSelectNum = 0
    var minVideoSelectNum = 0
    var minAudioSelectNum = 0
    var videoQuality = 0
    @JvmField
    var filterVideoMaxSecond = 0
    @JvmField
    var filterVideoMinSecond = 0
    var selectMaxDurationSecond = 0
    var selectMinDurationSecond = 0
    var recordVideoMaxSecond = 0
    var recordVideoMinSecond = 0
    var imageSpanCount = 0
    var filterMaxFileSize: Long = 0
    var filterMinFileSize: Long = 0
    var selectMaxFileSize: Long = 0
    var selectMinFileSize: Long = 0
    var language = 0
    var isDisplayCamera = false
    @JvmField
    var isGif = false
    var isWebp = false
    var isBmp = false
    var isEnablePreviewImage = false
    var isEnablePreviewVideo = false
    var isEnablePreviewAudio = false
    var isPreviewFullScreenMode = false
    var isPreviewZoomEffect = false
    var isOpenClickSound = false
    var isEmptyResultReturn = false
    var isHidePreviewDownload = false
    var isWithVideoImage = false
    @JvmField
    var queryOnlyList: List<String>? = null
    var skipCropList: List<String>? = null
    var isCheckOriginalImage = false
    var outPutCameraImageFileName: String? = null
    var outPutCameraVideoFileName: String? = null
    var outPutAudioFileName: String? = null
    var outPutCameraDir: String? = null
    var outPutAudioDir: String? = null
    var sandboxDir: String? = null
    var originalPath: String? = null
    var cameraPath: String? = null
    var sortOrder: String? = null
    var pageSize = 0
    var isPageStrategy = false
    var isFilterInvalidFile = false
    var isMaxSelectEnabledMask = false
    var animationMode = 0
    var isAutomaticTitleRecyclerTop = false
    var isQuickCapture = false
    var isCameraRotateImage = false
    var isAutoRotating = false
    var isSyncCover = false
    var ofAllCameraType = 0
    var isOnlySandboxDir = false
    var isCameraForegroundService = false
    var isResultListenerBack = false
    var isInjectLayoutResource = false
    var isActivityResultBack = false
    var isCompressEngine = false
    var isLoaderDataEngine = false
    var isSandboxFileEngine = false
    var isOriginalControl = false
    var isDisplayTimeAxis = false
    var isFastSlidingSelect = false
    var isSelectZoomAnim = false
    var isMp4 = false


    protected fun initDefaultValue() {
        isOnlyCamera = false
        maxSelectNum = 9
        minSelectNum = 0
        maxVideoSelectNum = 1
        minVideoSelectNum = 0
        minAudioSelectNum = 0
        filterVideoMaxSecond = 0
        filterVideoMinSecond = 1000
        selectMaxDurationSecond = 0
        selectMinDurationSecond = 0
        filterMaxFileSize = 0
        filterMinFileSize = 1024
        selectMaxFileSize = 0
        selectMinFileSize = 0
        recordVideoMaxSecond = 60
        recordVideoMinSecond = 0
        isCameraAroundState = false
        isWithVideoImage = false
        isDisplayCamera = true
        isGif = false
        isWebp = true
        isBmp = true
        isCheckOriginalImage = false
        isDirectReturnSingle = false
        isEnablePreviewImage = true
        isEnablePreviewVideo = true
        isEnablePreviewAudio = true
        isHidePreviewDownload = false
        isOpenClickSound = false
        isEmptyResultReturn = false
        outPutCameraImageFileName = ""
        outPutCameraVideoFileName = ""
        outPutAudioFileName = ""
        queryOnlyList = ArrayList()
        outPutCameraDir = ""
        outPutAudioDir = ""
        sandboxDir = ""
        originalPath = ""
        cameraPath = ""
        isPageStrategy = true
        isFilterInvalidFile = false
        isMaxSelectEnabledMask = false
        animationMode = -1
        isAutomaticTitleRecyclerTop = true
        isQuickCapture = true
        isCameraRotateImage = true
        isAutoRotating = true
        isOnlySandboxDir = false
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        isCameraForegroundService = true
        isResultListenerBack = true
        isActivityResultBack = false
        isCompressEngine = false
        isLoaderDataEngine = false
        isSandboxFileEngine = false
        isPreviewFullScreenMode = true
        isOriginalControl = false
        isInjectLayoutResource = false
        isDisplayTimeAxis = true
        isFastSlidingSelect = false
        skipCropList = ArrayList()
        sortOrder = ""
        isSelectZoomAnim = true
        isMp4 = false
    }


    companion object {
        val cleanInstance: PictureSelectionConfig?
            get() {
                val selectionSpec = instance
                selectionSpec?.initDefaultValue()
                return selectionSpec
            }
        private var mInstance: PictureSelectionConfig? = null
        @JvmStatic
        val instance: PictureSelectionConfig?
            get() {
                if (mInstance == null) {
                    synchronized(PictureSelectionConfig::class.java) {
                        if (mInstance == null) {
                            mInstance = PictureSelectionConfig()
                            mInstance?.initDefaultValue()
                        }
                    }
                }
                return mInstance
            }

    }
}