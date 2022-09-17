package com.mtime.bussiness.mine.bean;

import com.mtime.base.bean.MBaseBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vivian.wei on 2017/6/23.
 * 会员中心首页弹窗Bean
 */

public class MemberCenterPopupBean  extends MBaseBean {

    private int type;                   // 0无弹窗、1等级、2生日、3升级提升
    private String birthdayGiftTitle;   // 生日提示标题
    private String birthdayGiftTips;    // 生日提示
    private String levelGiftTitle;      // 等级弹窗提示标题
    private String levelGiftTips;       // 等级弹窗提示
    private String levelName;           // 会员等级名称，如：黄金会员
    private String levelDesc;           // 会员等级描述，如：恭喜您！您已经升级到黄金会员
    private int level;                  // 会员等级，从1到5开始，对应M0到M4会员
    private List<MemberRightBean> rightList = new ArrayList<>();  // 会员权益

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getBirthdayGiftTitle() {
        return birthdayGiftTitle;
    }

    public void setBirthdayGiftTitle(String birthdayGiftTitle) {
        this.birthdayGiftTitle = birthdayGiftTitle;
    }

    public String getBirthdayGiftTips() {
        return birthdayGiftTips;
    }

    public void setBirthdayGiftTips(String birthdayGiftTips) {
        this.birthdayGiftTips = birthdayGiftTips;
    }

    public String getLevelGiftTitle() {
        return levelGiftTitle;
    }

    public void setLevelGiftTitle(String levelGiftTitle) {
        this.levelGiftTitle = levelGiftTitle;
    }

    public String getLevelGiftTips() {
        return levelGiftTips;
    }

    public void setLevelGiftTips(String levelGiftTips) {
        this.levelGiftTips = levelGiftTips;
    }

    public String getLevelName() {
        return levelName;
    }

    public void setLevelName(String levelName) {
        this.levelName = levelName;
    }

    public String getLevelDesc() {
        return levelDesc;
    }

    public void setLevelDesc(String levelDesc) {
        this.levelDesc = levelDesc;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public List<MemberRightBean> getRightList() {
        return rightList;
    }

    public void setRightList(List<MemberRightBean> rightList) {
        this.rightList = rightList;
    }
}
