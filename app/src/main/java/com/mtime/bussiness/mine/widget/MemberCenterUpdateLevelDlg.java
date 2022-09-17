package com.mtime.bussiness.mine.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.mtime.frame.App;
import com.mtime.R;
import com.mtime.bussiness.mine.bean.MemberCenterPopupBean;
import com.mtime.bussiness.mine.bean.MemberRightBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vivian.wei on 2017/6/30.
 * 会员中心首页弹窗_升级提示弹窗
 */

public class MemberCenterUpdateLevelDlg extends Dialog {

    private View rlTitle;
    private TextView tvLevelName;
    private TextView tvLevelDesc;
    private TextView tvBtn;
    private ImageView ivClose;

    private final MemberCenterPopupBean popupBean;
    private View.OnClickListener btnListener = null;
    private View.OnClickListener closeListener = null;

    // 特权列表
    private final List<ImageView> ivIcons = new ArrayList<>();
    private final List<TextView> tvNames = new ArrayList<>();
    // 图标id
    private final int[] iconIds = {R.id.iv_icon_1, R.id.iv_icon_2, R.id.iv_icon_3, R.id.iv_icon_4, R.id.iv_icon_5, R.id.iv_icon_6};
    // 特权名id
    private final int[] nameIds = {R.id.tv_name_1, R.id.tv_name_2, R.id.tv_name_3, R.id.tv_name_4, R.id.tv_name_5, R.id.tv_name_6};
    // 特权图标：与会员特权页各等级图标顺序一致
    private final int[] silverDrawables = {R.drawable.dialog_silver_level,
            R.drawable.dialog_silver_birth,
            R.drawable.dialog_silver_free_fee,
            R.drawable.dialog_silver_return_mcoin};
    private final int[] goldDrawables = {R.drawable.dialog_gold_level,
            R.drawable.dialog_gold_birth,
            R.drawable.dialog_gold_discount,
            R.drawable.dialog_gold_free_fee,
            R.drawable.dialog_gold_return_mcoin};
    private final int[] platinumDrawables = {R.drawable.dialog_platinum_level,
            R.drawable.dialog_platinum_birth,
            R.drawable.dialog_platinum_discount,
            R.drawable.dialog_platinum_free_fee,
            R.drawable.dialog_platinum_return_mcoin,
            R.drawable.dialog_platinum_activity};
    private final int[] diamondDrawables = {R.drawable.dialog_diamond_level,
            R.drawable.dialog_diamond_birth,
            R.drawable.dialog_diamond_discount,
            R.drawable.dialog_diamond_free_fee,
            R.drawable.dialog_diamond_return_mcoin,
            R.drawable.dialog_diamond_activity};

    public MemberCenterUpdateLevelDlg(final Context context, final MemberCenterPopupBean popupBean) {
        super(context, R.style.upomp_bypay_MyDialog);
        this.popupBean = popupBean;
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        this.setContentView(R.layout.dialog_member_center_update_level);
        rlTitle = findViewById(R.id.rl_title);
        tvLevelName = findViewById(R.id.tv_level_name);
        tvLevelDesc = findViewById(R.id.tv_level_desc);
        tvBtn = findViewById(R.id.tv_btn);
        ivClose = findViewById(R.id.iv_close);
        for(int i = 0, length = iconIds.length; i < length; i++) {
            ivIcons.add(findViewById(iconIds[i]));
            tvNames.add(findViewById(nameIds[i]));
        }

        tvLevelName.setText(popupBean.getLevelName());
        tvLevelDesc.setText(popupBean.getLevelDesc());
        // 特权列表
        int level = popupBean.getLevel();
        List<MemberRightBean> rightList = popupBean.getRightList();
        switch (level) {
            case App.USER_LEVER_SILVEL:
                showSilver(rightList);
                break;
            case App.USER_LEVER_GOLDEN:
                showGold(rightList);
                break;
            case App.USER_LEVER_PLATINUM:
            case App.USER_LEVER_DIAMOND:
                showPlatinumOrDiamond(level, rightList);
                break;
            default:
                break;
        }

        if(null != btnListener) {
            tvBtn.setOnClickListener(btnListener);
        }

        if(null != closeListener) {
            ivClose.setOnClickListener(closeListener);
        }
    }

    /**
     * 设置底部按钮的事件
     */
    public void setBtnListener(final View.OnClickListener click) {
        btnListener = click;
    }

    /**
     * 设置关闭按钮的事件
     */
    public void setBtnCloseListener(final View.OnClickListener click) {
        closeListener = click;
    }

    // 显示白银会员
    private void showSilver(List<MemberRightBean> rightList) {
        rlTitle.setBackgroundResource(R.drawable.dialog_member_center_update_level_title_silver_bg);
        tvBtn.setBackgroundResource(R.drawable.dialog_member_center_update_level_silver_btn);
        ImageView ivIcon;
        TextView tvName;
        for(int i = 0, length = iconIds.length; i < length; i++) {
            ivIcon = ivIcons.get(i);
            tvName = tvNames.get(i);
            if(i < silverDrawables.length) {
                ivIcon.setBackgroundResource(silverDrawables[i]);
                if(null != rightList && rightList.size() > 0) {
                    // 与会员特权页各等级特权顺序一致
                    // 1 等级礼包、2 生日礼包、3 免运费 5 消费返币
                    if(i < 3) {
                        tvName.setText(getRightNameByType(i + 1, rightList));
                    } else {
                        tvName.setText(getRightNameByType(5, rightList));
                    }
                }
            } else {
                ivIcon.setVisibility(View.INVISIBLE);
                tvName.setVisibility(View.INVISIBLE);
            }
        }
    }

    // 显示黄金会员
    private void showGold(List<MemberRightBean> rightList) {
        rlTitle.setBackgroundResource(R.drawable.dialog_member_center_update_level_title_gold_bg);
        tvBtn.setBackgroundResource(R.drawable.dialog_member_center_update_level_gold_btn);
        ImageView ivIcon;
        TextView tvName;
        for(int i = 0, length = iconIds.length; i < length; i++) {
            ivIcon = ivIcons.get(i);
            tvName = tvNames.get(i);
            if(i < goldDrawables.length) {
                ivIcon.setBackgroundResource(goldDrawables[i]);
                if(null != rightList && rightList.size() > 0) {
                    // 与会员特权页各等级特权顺序一致
                    // 1 等级礼包、2 生日礼包、4 购物折扣、3 免运费、5 消费返币
                    if(i < 2 || 4 == i) {
                        tvName.setText(getRightNameByType(i + 1, rightList));
                    } else if(2 == i) {
                        tvName.setText(getRightNameByType(4, rightList));
                    } else if(3 == i) {
                        tvName.setText(getRightNameByType(3, rightList));
                    }
                }
            } else {
                ivIcon.setVisibility(View.INVISIBLE);
                tvName.setVisibility(View.INVISIBLE);
            }
        }
    }

    // 显示铂金/钻石会员
    private void showPlatinumOrDiamond(int level, List<MemberRightBean> rightList) {
        if(App.USER_LEVER_PLATINUM == level) {
            rlTitle.setBackgroundResource(R.drawable.dialog_member_center_update_level_title_platinum_bg);
            tvBtn.setBackgroundResource(R.drawable.dialog_member_center_update_level_platinum_btn);
        } else {
            rlTitle.setBackgroundResource(R.drawable.dialog_member_center_update_level_title_diamond_bg);
            tvBtn.setBackgroundResource(R.drawable.dialog_member_center_update_level_diamond_btn);
        }
        ImageView ivIcon;
        TextView tvName;
        for(int i = 0, length = iconIds.length; i < length; i++) {
            ivIcon = ivIcons.get(i);
            tvName = tvNames.get(i);
            if(App.USER_LEVER_PLATINUM == level) {
                ivIcon.setBackgroundResource(platinumDrawables[i]);
            } else {
                ivIcon.setBackgroundResource(diamondDrawables[i]);
            }
            if(null != rightList && rightList.size() > 0) {
                // 与会员特权页各等级特权顺序一致
                // 1 等级礼包、2 生日礼包、4 购物折扣、3 免运费、5 消费返币、 6 线下活动优先
                if(2 == i) {
                    tvName.setText(getRightNameByType(4, rightList));
                } else if(3 == i) {
                    tvName.setText(getRightNameByType(3, rightList));
                } else {
                    tvName.setText(getRightNameByType(i + 1, rightList));
                }
            }
        }
    }

    // 获取指定特权类型的特权名称
    private String getRightNameByType(int type, List<MemberRightBean> rightList) {
        String rightName = "";
        MemberRightBean bean;
        for(int i = 0, length = iconIds.length; i < length; i++) {
            bean = rightList.get(i);
            if(bean.getType() == type) {
                rightName = bean.getName();
                break;
            }
        }
        return rightName;
    }
}
