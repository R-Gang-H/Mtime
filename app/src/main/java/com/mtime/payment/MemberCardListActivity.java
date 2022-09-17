package com.mtime.payment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.mtime.frame.BaseActivity;
import com.mtime.R;
import com.mtime.base.utils.MToastUtils;
import com.mtime.bussiness.mine.adapter.MemberCardAdapter;
import com.mtime.bussiness.mine.bean.MemberCardBean;
import com.mtime.bussiness.mine.bean.MemberList;
import com.mtime.network.ConstantUrl;
import com.mtime.network.RequestCallback;
import com.mtime.util.HttpUtil;
import com.mtime.util.TipsDlg;
import com.mtime.widgets.BaseTitleView.StructType;
import com.mtime.widgets.TitleOfNormalView;

import java.util.List;

public class MemberCardListActivity extends BaseActivity {
	private TipsDlg dialog;
	List<MemberList> memberCardlist;
	private ListView cardListView;
	private TextView nodataView;

	protected void onInitEvent() {
	}

	protected void onInitVariable() {
		setPageLabel("memberCardList");
	}

	private void showDlg() {
		dismissDlg();
		dialog = new TipsDlg(this);
		dialog.show();
		dialog.setText("正在加载，请稍后...");
		dialog.getImg().setVisibility(View.GONE);
	}

	private void dismissDlg() {
		if (dialog != null && dialog.isShowing()) {
			dialog.dismiss();
		}
		dialog = null;
	}

	protected void onInitView(Bundle savedInstanceState) {
		setContentView(R.layout.act_member_cardlist);
		cardListView = findViewById(R.id.member_list);
		nodataView = findViewById(R.id.no_data);

		View navBar = findViewById(R.id.navigationbar);
		new TitleOfNormalView(this, navBar,
				StructType.TYPE_NORMAL_SHOW_BACK_TITLE, "", null);
	}

	protected void onLoadData() {
	}

	protected void onRequestData() {
		showDlg();
		HttpUtil.get(ConstantUrl.GET_MEMBER_CARD_LIST, MemberCardBean.class, new RequestCallback() {
			public void onSuccess(Object o) {
				MemberCardBean data = (MemberCardBean) o;
				if (data.getMemberList() != null && data.getMemberList().size() > 0) {
					memberCardlist = data.getMemberList();
					MemberCardAdapter adapter = new MemberCardAdapter(MemberCardListActivity.this, memberCardlist);
					cardListView.setAdapter(adapter);
					nodataView.setVisibility(View.GONE);
					cardListView.setVisibility(View.VISIBLE);
					dismissDlg();
				} else {
					nodataView.setVisibility(View.VISIBLE);
					cardListView.setVisibility(View.GONE);
					dismissDlg();
					MToastUtils.showShortToast("未获取到卡列表数据！");
				}
			}

			public void onFail(Exception e) {
				dismissDlg();
			}
		});
	}

	protected void onUnloadData() {
	}

	public static void launch(Context context){
		Intent launcher = new Intent(context,MemberCardListActivity.class);
		context.startActivity(launcher);
	}

}
