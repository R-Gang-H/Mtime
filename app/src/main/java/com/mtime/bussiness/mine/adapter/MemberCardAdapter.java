package com.mtime.bussiness.mine.adapter;

import android.graphics.drawable.BitmapDrawable;
import androidx.collection.ArrayMap;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.kotlin.android.user.UserManager;
import com.mtime.beans.SuccessBean;
import com.mtime.frame.BaseActivity;
import com.mtime.R;
import com.mtime.base.utils.MToastUtils;
import com.mtime.bussiness.mine.bean.MemberList;
import com.mtime.network.ConstantUrl;
import com.mtime.network.RequestCallback;
import com.mtime.statistic.large.h5.StatisticH5;
import com.mtime.util.HttpUtil;
import com.mtime.util.ImageLoader;
import com.mtime.util.JumpUtil;
import com.mtime.util.VolleyError;

import java.util.List;
import java.util.Map;

public class MemberCardAdapter extends BaseAdapter
{
    List<MemberList>            memberCardlist;
    BaseActivity                context;

    public MemberCardAdapter(BaseActivity c, List<MemberList> data)
    {
        context = c;
        memberCardlist = data;
    }

    public int getCount()
    {
        if (memberCardlist != null)
        {
            return memberCardlist.size();
        }
        return 0;
    }

    public Object getItem(int arg0)
    {
        if (memberCardlist != null)
        {
            return memberCardlist.get(arg0);
        }
        return null;
    }

    public long getItemId(int arg0)
    {
        if (memberCardlist != null)
        {
            return memberCardlist.get(arg0).getCinemaId();
        }
        return 0;
    }

    public View getView(int pos, View view, ViewGroup arg2)
    {
        final ViewHolder holder;

        if (view == null)
        {
            holder = new ViewHolder();
            view = View.inflate(context, R.layout.member_card_item, null);
            holder.topView = view.findViewById(R.id.topView);
            holder.bottomView = view.findViewById(R.id.bottomView);
            holder.cardType = view.findViewById(R.id.cardType);
            holder.cinemaView = view.findViewById(R.id.cinemaView);
            holder.details = view.findViewById(R.id.details);
            holder.name = view.findViewById(R.id.name);
            holder.num = view.findViewById(R.id.num);
            holder.title = view.findViewById(R.id.title);
            view.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) view.getTag();
        }
        final MemberList data = memberCardlist.get(pos);

        holder.topView.setBackgroundResource(R.drawable.gray_top);
        holder.bottomView.setBackgroundResource(R.drawable.white_bottom);
        if (data != null)
        {
            if (data.getTopImage() != null && !data.getTopImage().equals(""))
            {
                ImageLoader.ImageListener listener = new ImageLoader.ImageListener() {
                    @Override
                    public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                        if (null != response.getBitmap()) {
                            BitmapDrawable bitmapDrawable = new BitmapDrawable(response.getBitmap());
                            holder.topView.setBackgroundDrawable(bitmapDrawable);
                        }
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        holder.topView.setBackgroundResource(R.drawable.gray_top);
                    }
                };

                context.volleyImageLoader.displayImage(data.getTopImage(), listener);

            }

            if (data.getBottomImage() != null && !data.getBottomImage().equals(""))
            {
                ImageLoader.ImageListener listener = new ImageLoader.ImageListener() {
                    @Override
                    public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                        if (null != response.getBitmap()) {
                            BitmapDrawable bitmapDrawable = new BitmapDrawable(response.getBitmap());
                            holder.bottomView.setBackgroundDrawable(bitmapDrawable);
                        }
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                };

                context.volleyImageLoader.displayImage(data.getBottomImage(), listener);
            }

            if (data.getTypeImage()!=null&&!data.getTypeImage().equals(""))
            {
                ImageLoader.ImageListener listener = new ImageLoader.ImageListener() {
                    @Override
                    public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                        if (null != response.getBitmap()) {
                            holder.cardType.setVisibility(View.VISIBLE);
                            holder.cardType.setImageBitmap(response.getBitmap());
                        }
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        holder.cardType.setVisibility(View.INVISIBLE);
                    }
                };

                context.volleyImageLoader.displayImage(data.getTypeImage(), listener);
            }
            
            if (data.getCinemaImage() != null && !data.getCinemaImage().equals(""))
            {
                ImageLoader.ImageListener listener = new ImageLoader.ImageListener() {
                    @Override
                    public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                        if (null != response.getBitmap()) {
                            BitmapDrawable bitmapDrawable = new BitmapDrawable(response.getBitmap());
                            holder.cinemaView.setBackgroundDrawable(bitmapDrawable);
                        }
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        holder.cinemaView.setVisibility(View.INVISIBLE);
                    }
                };

                context.volleyImageLoader.displayImage(data.getCinemaImage(), listener);
            }

            if (data.getCardNumber() != null && !data.getCardNumber().equals(""))
            {
                holder.num.setText(data.getCardNumber());
            }

            if (data.getMemberName() != null && !data.getMemberName().equals(""))
            {
                holder.name.setText(data.getMemberName());
            }

            if (data.getCinemaName() != null && !data.getCinemaName().equals(""))
            {
                holder.title.setText(data.getCinemaName());
            }
            if (data.getAddress() != null && !data.getAddress().equals(""))
            {
                holder.details.setText(data.getAddress());
            }
            view.setOnClickListener(new OnClickListener()
            {
                public void onClick(View arg0)
                {
                    if (data.getMemberDetailUrl()!=null&&!data.getMemberDetailUrl().equals(""))
                    {
//                        Intent intent =new Intent();
//                        intent.putExtra(App.getInstance().KEY_WEBVIEW_TITLE_NAME, context.getString(R.string.s_my_member_card));
//                        intent.putExtra("url", data.getMemberDetailUrl());
//                        intent.putExtra("needLogin", true);
//                        context.startActivity(ShareDetailActivity.class, intent);

//                        JumpUtil.startShareDetailActivity(context,context.getString(R.string.s_my_member_card),data.getMemberDetailUrl(), true);
                        
                        gotoCommonWebActivity(context, data.getMemberDetailUrl());
                    }
                    else
                    {
                        MToastUtils.showShortToast(R.string.s_error_retry_later);
                    }
                }
            });
        }

        return view;
    }
    
    public void gotoCommonWebActivity(final BaseActivity context, String url) {
        Map<String, String> parameterList = new ArrayMap<String, String>(1);
        parameterList.put("url", url);
        HttpUtil.post(ConstantUrl.GET_COUPON_URL_WITH_LOGIN, parameterList, SuccessBean.class, new RequestCallback() {
            public void onSuccess(Object o) {
                SuccessBean bean = (SuccessBean) o;
                if (bean.getSuccess() != null) {
                    if (bean.getSuccess().equalsIgnoreCase("true")) {
                        if (UserManager.Companion.getInstance().isLogin()) {
                            JumpUtil.startCommonWebActivity(context, bean.getNewUrl(), StatisticH5.PN_H5, null,
                                    true, true, true, false, null);
                        }
                        return;
                    }
                }
                MToastUtils.showShortToast("登录失败，请重新登录后重试！");
            }
            
            public void onFail(Exception e) {
                MToastUtils.showShortToast("请求数据失败，请稍后重试！");
            }
        });
    }

    static class ViewHolder
    {
        View      topView;
        View      bottomView;
        TextView  title;
        TextView  details;
        TextView  name;
        TextView  num;
        ImageView cardType;
        ImageView cinemaView;
    }

}
