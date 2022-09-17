package com.kotlin.android.user

import android.text.SpannableStringBuilder
import android.view.View
import com.kotlin.android.ktx.ext.span.toBold
import com.kotlin.android.ktx.ext.span.toColor
import com.kotlin.android.ktx.ext.span.toLinkNotUnderLine
import com.kotlin.android.ktx.ext.span.toSpan
import com.kotlin.android.mtime.ktx.getColor

/**
 *
 * Created on 2021/12/23.
 *
 * @author o.s
 */

/**
 * 隐私条款头
 */
val privacyTitle: CharSequence
    get() = "温馨提示"

/**
 * 隐私条款，格式注意保持"""   """
 */
fun privacyContent(action: ((view: View, key: String) -> Unit)? = null): CharSequence {
    return """        亲爱的用户，感谢您信任并使用万达电影！为保护您的权益，我们依据最新法律法规、监管政策要求及业务实际情况，更新了"""
            .toSpan()
            .append(privacyKey(action))
            .append(
                """，特向您说明如下：
1、为向您提供服务基本功能，我们会遵循合法且最小必要的原则收集和使用必要的信息。
2、为向您提供个性化服务，我们会根据您的授权收集和使用必要的信息。如您未授权不会影响您使用搜索、浏览等基本功能。
3、如您使用第三方账号授权登录，因依据法律法规规定，第三方不会将您的手机号信息共享给我们，"""
            )
            .append("故需要您在第三方账号授权后，仍需绑定您的真实手机号。".toSpan().toBold())
            .append(
                """
4、我们将按照法律法规要求，采取相应安全保护措施，尽力保护您的个人信息安全可控。
                        
        请您务必仔细阅读并理解最新版"""
            )
            .append(privacyKey(action))
            .append("，在确认充分理解并同意后使用万达电影相关产品或服务。点击同意或勾选即代表您已阅读并同意全部内容。")
}

private fun privacyKey(action: ((view: View, key: String) -> Unit)? = null): SpannableStringBuilder {
    return "".toSpan()
        .append(
            "《Mtime时光网服务条款》"
                .toSpan()
                .toLinkNotUnderLine(action = action)
                .toColor(color = getColor(R.color.color_20a0da))
        )
        .append("与")
        .append(
            "《隐私政策》"
                .toSpan()
                .toLinkNotUnderLine(action = action)
                .toColor(color = getColor(R.color.color_20a0da))
        )
}

fun mTimePrivacyContent(action: ((view: View, key: String) -> Unit)? = null): CharSequence {
    return "欢迎使用时光网产品/服务！在我们正式为您提供服务前，为了更好地保障您的个人权益，请务必审慎阅读".toSpan()
        .append(privacyKey(action))
        .append(
    """内的所有条款，本服务条款及隐私政策将向您说明：
只有当您阅读并同意后，我们才会为您提供时光网的全部产品/服务内容，为更好的向您提供个性化推荐、购票购物、交流互动等服务，在使用过程中，可能会收集您的位置、设备信息、联系方式等必要信息，以便为您提供更好的服务。您可在使用过程中随时取消授权，但取消授权后相关服务可能会受影响。
我们在您使用服务过程中收集的信息：
1.第三方服务提供商与业务合作伙伴指定的与您相关的信息：我们可能收集并使用如第三方服务提供商与业务合作伙伴分配的ID。
2.与您的应用使用相关的信息： 包括应用基础信息，例如应用ID信息、SDK版本、应用设置（地区、语言、时区、字体），以及应用状态记录（例如下载、安装、更新、删除）。
3.您在使用服务时生成的信息： 例如社区/平台中您的用户等级、签到信息、浏览记录；您在使用社区服务时的站内信（仅发送和接收的双方可见）；您在使用推送服务时的推送文本；您在使用广告服务时的行为（如点击、曝光）。
4.位置信息（仅适用于特定服务/功能）： 若您使用和位置相关的服务（如使用影院定位，附近的影院，刷新位置），我们可能采集与您设备的精确或模糊位置相关的各类信息。例如地区、国家代码、城市代码、移动网络代码、移动国家代码、经纬度信息、时区设置和语言设置。您可以随时在手机设置（设置-权限）中关闭位置服务。
5.日志信息：与您使用某些功能、应用和网站相关的信息。例如Cookie和其他匿名标识符技术、互联网协议（IP）地址、网络请求信息、临时消息历史、标准系统日志、错误崩溃信息、使用服务产生的日志信息（如注册时间、访问时间、活动时间等），可能应用于个性化推荐算法中，以推荐用户感兴趣的内容。
6.存储权限：需要时申请设备存储权限，用于缓存浏览内容，在无网络环境下保证用户体验，更换头像、发布图片、保存电影海报/剧照以及分享影评等。
7.其他信息：IMEI，IMSI，Mac地址，环境特征值 (ECV)，即从时光账号、设备标识、连接的Wi-Fi产生的信息和地理位置信息。
        
您点击""".trimIndent()
        )
        .append("“同意”".toSpan().toBold())
        .append("的行为即表示您已阅读完毕并同意以上协议的全部内容，包括您同意我们对您的个人信息的收集/保存/使用/对外提供/保护等规则条款，您的用户权利等条款，约定我们的限制责任、免责条款，以及协议内的其他所有条款。如您对以上协议有任何疑问，可通过客服电话4006-059-500与我们联系。")

//    return "感谢您对时光网产品/服务的信任，您已经进入时光网用户的注册流程，当您完成所有注册程序后，您即可以享受我们专门为注册用户提供的产品/服务内容，在此使用过程中，可能会对您应履行的义务有所要求或对您的权利有所限制。因此，为了更好地保障您的个人权益，请务必审慎阅读《Mtime时光网服务条款》与《隐私政策》内的所有条款，并在您同意以上协议全部内容后点击“同意”并进入下一步注册流程。 如果您不同意以上协议（包括不同意协议的任一内容），请您停止注册，您停止注册的行为不影响您可以使用/继续使用我们提供的部分产品/服务，但您将无法享受我们专门为我们的注册用户提供的完整的产品/服务。您点击“同意”的行为即表示您已阅读完毕并同意以上协议的全部内容，包括您同意我们对您的个人信息的收集/保存/使用/对外提供/保护等规则条款，以及您的用户权利等条款；约定我们的限制责任、免责条款；其他以颜色或加粗进行标识的重要条款；以及协议内的其他所有条款。 如您对以上协议有任何疑问，可通过客服电话4006059500与我们联系。"
}