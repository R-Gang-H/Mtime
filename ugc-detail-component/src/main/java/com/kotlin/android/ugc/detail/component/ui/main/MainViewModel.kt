package com.kotlin.android.ugc.detail.component.ui.main

import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.kotlin.android.comment.component.bean.CommentTitleViewBean
import com.kotlin.android.comment.component.bean.CommentViewBean
import com.kotlin.android.comment.component.bean.ReplyViewBean
import com.kotlin.android.comment.component.binder.CommentListBinder
import com.kotlin.android.comment.component.binder.CommentListTitleBinder
import com.kotlin.android.core.BaseViewModel
import com.kotlin.android.ugc.detail.component.bean.*
import com.kotlin.android.ugc.detail.component.binder.*
import com.kotlin.android.ugc.web.component.html.TYPE_ARTICLE
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder

/**
 * Created by lushan on 2020/8/5
 */
class MainViewModel : BaseViewModel() {
    val content = "<div> \n" +
            "   <div> \n" +
            "    <img src=\"https://img5.mtime.cn/mg/2020/08/04/114800.52890329.jpg\" data-src=\"https://img5.mtime.cn/mg/2020/08/04/114800.52890329.jpg\" data-original=\"https://img5.mtime.cn/mg/2020/08/04/114800.52890329.jpg\" /> \n" +
            "   </div> \n" +
            "   <div>\n" +
            "     韦伯和《猫》女主角泰勒·斯威夫特 \n" +
            "   </div> \n" +
            "  </div> \n" +
            "  <div> \n" +
            "   <br /> \n" +
            "  </div> \n" +
            "  <div>\n" +
            "    　　 \n" +
            "   <b>时光网讯</b>曾凭 \n" +
            "   <a href=\"https://movie.mtime.com/118926/\" target=\"_blank\">《国王的演讲》</a>拿下奥斯卡最佳影片和最佳导演的 \n" +
            "   <a href=\"https://people.mtime.com/1181585/\" target=\"_blank\">汤姆·霍伯</a>，去年底遭遇职业滑铁卢——由他导演的 \n" +
            "   <a href=\"https://movie.mtime.com/258990/\" target=\"_blank\">《猫》</a>被观众、媒体各种批评。今年3月金酸莓奖上拿下最差影片等6项“大奖”，最近又被音乐剧原作者 \n" +
            "   <a href=\"https://people.mtime.com/906190/\" target=\"_blank\">安德鲁·洛伊德·韦伯</a>痛批“很荒唐”。 \n" +
            "  </div> \n" +
            "  <div> \n" +
            "   <br /> \n" +
            "  </div> \n" +
            "  <div>\n" +
            "    　　音乐剧的创作者韦伯近日在与《星期日泰晤士报》的采访中，谈到了票房失败并引起大众吐槽的电影版《猫》，称其“荒唐”。韦伯透露：“这部电影的问题在于，汤姆·霍伯(导演)不想让任何原音乐剧的人参与其中，这整个东西都很荒唐。” \u200B\u200B\u200B\u200B \n" +
            "   <br /> \n" +
            "  </div> \n" +
            "  <div> \n" +
            "   <div> \n" +
            "    <img src=\"https://img5.mtime.cn/mg/2020/08/04/114801.71397640.jpg\" data-src=\"https://img5.mtime.cn/mg/2020/08/04/114801.71397640.jpg\" data-original=\"https://img5.mtime.cn/mg/2020/08/04/114801.71397640.jpg\" /> \n" +
            "   </div> \n" +
            "   <div>\n" +
            "     韦伯和音乐剧《猫》的艺人 \n" +
            "   </div> \n" +
            "  </div> \n" +
            "  <div> \n" +
            "   <br /> \n" +
            "  </div> \n" +
            "  <div>\n" +
            "    　　按道理，汤姆·霍伯并不是个经常拍烂片的人，除了《国王的演讲》，他2012年的 \n" +
            "   <a href=\"https://movie.mtime.com/155497/\" target=\"_blank\">《悲惨世界》</a>也是改编自同名音乐剧，入围了奥斯卡最佳影片。2015年的 \n" +
            "   <a href=\"https://movie.mtime.com/91850/\" target=\"_blank\">《丹麦女孩》</a>也提名了4项奥斯卡，怎么就栽在《猫》这部电影上了呢？ \n" +
            "  </div> \n" +
            "  <div> \n" +
            "   <img src=\"https://img5.mtime.cn/mg/2020/08/04/114801.48087636.jpg\" data-src=\"https://img5.mtime.cn/mg/2020/08/04/114801.48087636.jpg\" data-original=\"https://img5.mtime.cn/mg/2020/08/04/114801.48087636.jpg\" /> \n" +
            "   <br /> \n" +
            "   <img src=\"https://img5.mtime.cn/mg/2020/08/04/114801.34098114.jpg\" data-src=\"https://img5.mtime.cn/mg/2020/08/04/114801.34098114.jpg\" data-original=\"https://img5.mtime.cn/mg/2020/08/04/114801.34098114.jpg\" /> \n" +
            "   <br /> \n" +
            "   <br /> \n" +
            "  </div>"

    fun getMovieBinder(context: FragmentActivity,recyclerView: RecyclerView?): MutableList<MultiTypeBinder<*>> {


        val binderList = mutableListOf(
                getImageListBinder(),
                FamilyBinder(TopicFamilyViewBean(1L,"动漫","http://img5.mtime.cn/mt/2020/07/20/083247.82029902_1280X720X2.jpg",68000L,"没有家规的家族",1)),
                UgcMovieSpoilerBinder("此影评包含剧透"),
                getAlbumBinder(),
                UgcWebViewBinder(UgcWebViewBean(content, TYPE_ARTICLE), recyclerView),
                UgcTitleBinder(UgcTitleViewBean("欠你一场迈过盛夏的同")),
                MovieBinder(MovieViewBean(
                        1L, "少年的你", "少年的你",
                        "http://img5.mtime.cn/mt/2020/07/20/083247.82029902_1280X720X2.jpg",
                        "8.8", "108分钟", "动作/冒险/奇幻/家庭", "2019年10月5日中国上映", 3, false, 1
                )), MovieBinder(MovieViewBean(
                1L, "少年的你", "少年的你",
                "http://img5.mtime.cn/mt/2020/07/20/083247.82029902_1280X720X2.jpg",
                "8.8", "108分钟", "动作/冒险/奇幻/家庭", "2019年10月5日中国上映", 3, true, 2
        ))
                ,CommentListTitleBinder(CommentTitleViewBean(100L,false))
        )
        binderList.addAll(getCommentListBinder(context))
        return binderList
    }

    private fun getAlbumBinder(): UgcAlbumBinder {
        return UgcAlbumBinder(UgcAlbumViewBean(100L, 20, (0 until 20).map { UgcAlbumItemBinder(UgcAlbumItemViewBean(it.toLong(), "https://img5.mtime.cn/mg/2020/08/04/114801.34098114.jpg")) }.toMutableList()),0L)
    }

    private fun getCommentListBinder(context:FragmentActivity): MutableList<MultiTypeBinder<*>> {
        return (0 until 10).map {
            CommentListBinder(context,CommentViewBean(
                    commentId = it.toLong(),
                    userId = it.toLong(),
                    userName = "离人心上秋",
                    userPic = "http://img5.mtime.cn/mt/2020/07/20/083247.82029902_1280X720X2.jpg",
                    publishDate = "* 2020-08-07",
                    commentContent = "片尾旁白的时候，我不禁想到国内有个叫张小北的影评人很久前在影评节目里说，“只有中国人拍的电影才能触动身为中国人最深的内心”，我认为流浪…",
                    likeCount = 10 * it.toLong(),
                    userPraised = 1L,
                    commentPic = "http://img5.mtime.cn/mt/2020/07/20/083247.82029902_1280X720X2.jpg",
                    replyList = (0..it).map { ReplyViewBean(userId = it.toLong(), userName = "白首不相离:", userPic = "http://img5.mtime.cn/mt/2020/07/20/083247.82029902_1280X720X2.jpg", replyContent = "老铁，没毛病！！我不禁想到国内有个叫张小北的影评人很久前在影评节目里说，“只有中国人拍的电影才能触动身为中国人最深的内心”，我认为流浪…") }.toMutableList()
            )

            )
        }.toMutableList()
    }

    fun getImageListBinder() = UgcBannerImageBinder((0 until 10).map { UgcImageViewBean(imageId = it.toString(),ugcContent = "离人心上秋${it}",ugcPic =  "http://img5.mtime.cn/mt/2020/07/20/083247.82029902_1280X720X2.jpg" ) }.toMutableList() )

}