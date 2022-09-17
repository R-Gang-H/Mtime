package com.mtime.bussiness.ticket.movie.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.mtime.frame.App;
import com.mtime.frame.BaseActivity;
import com.mtime.R;
import com.mtime.base.utils.MToastUtils;
import com.mtime.bussiness.ticket.movie.adapter.CardListAdapter;
import com.mtime.bussiness.ticket.movie.bean.CardList;
import com.mtime.bussiness.ticket.movie.bean.CardListMainBean;
import com.mtime.network.RequestCallback;
import com.mtime.bussiness.ticket.MovieAndCinemaSwitchView.IMovieAndCinemaSwitchViewListener;
import com.mtime.widgets.TitleOfSwitchView;
import com.mtime.network.ConstantUrl;
import com.mtime.util.HttpUtil;
import com.mtime.util.ImageURLManager;
import com.mtime.util.UIUtil;

import java.util.ArrayList;

/**
 * 支付-使用银行卡支付页
 * 
 */
public class BankCardListActivity extends BaseActivity {
    private IMovieAndCinemaSwitchViewListener titleSwitchListener;
    private ListView                          creditbankListView;
    private ListView                          debitbankListView;
    private RequestCallback getbankListCallback;
    private CardListMainBean                  cardListMainBean;
    private CardListAdapter                   creditcardListAdapter, debitCardListAdapter;
    private ArrayList<CardList>               creditCardLists;                            // 信用卡
    private ArrayList<CardList>               debitCardLists;                             // 借记卡
    private int                               offencreditId, offenDebitId;
    
    @Override
    protected void onInitVariable() {
        offencreditId = App.getInstance().getPrefsManager().getInt("creditcard");
        offenDebitId = App.getInstance().getPrefsManager().getInt("debitcard");
        titleSwitchListener = new IMovieAndCinemaSwitchViewListener() {
            
            @Override
            public void onEvent(boolean leftOn) {
                if (leftOn) {
                    creditbankListView.setVisibility(View.VISIBLE);
                    debitbankListView.setVisibility(View.GONE);
                }
                else {
                    creditbankListView.setVisibility(View.GONE);
                    debitbankListView.setVisibility(View.VISIBLE);
                }
            }
            
        };

        setPageLabel("bankcardList");
    }
    
    @Override
    protected void onInitView(Bundle savedInstanceState) {
        setContentView(R.layout.act_bankcard);
        View navBar = findViewById(R.id.navigationbar);
        new TitleOfSwitchView(this, navBar, titleSwitchListener, null, getResources().getString(R.string.str_credit),
                getResources().getString(R.string.str_debit));
        creditbankListView = findViewById(R.id.creditcard_list);
        debitbankListView = findViewById(R.id.debitcard_list);
    }
    
    @Override
    protected void onInitEvent() {
        getbankListCallback = new RequestCallback() {
            
            @Override
            public void onSuccess(Object o) {
                cardListMainBean = (CardListMainBean) o;
                ArrayList<CardList> normalcreditCardLists = (ArrayList<CardList>) cardListMainBean.getCreditCardList();
                ArrayList<CardList> normaldebitCardLists = (ArrayList<CardList>) cardListMainBean.getDebitCardList();
                creditCardLists = shifOffenGo(creditbankListView, normalcreditCardLists, offencreditId);
                debitCardLists = shifOffenGo(debitbankListView, normaldebitCardLists, offenDebitId);
                creditcardListAdapter = new CardListAdapter(creditCardLists, BankCardListActivity.this);
                debitCardListAdapter = new CardListAdapter(debitCardLists, BankCardListActivity.this);
                creditbankListView.setAdapter(creditcardListAdapter);
                debitbankListView.setAdapter(debitCardListAdapter);

                UIUtil.dismissLoadingDialog();
            }
            
            @Override
            public void onFail(Exception e) {
                UIUtil.dismissLoadingDialog();
                if (canShowDlg) {
                    MToastUtils.showShortToast(e.getLocalizedMessage());
                }
            }
        };

        OnItemClickListener creditClickListener = new OnItemClickListener() {
            
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (0 == position || position > creditCardLists.size()) {
                    return;
                }

                Intent intent = new Intent();
                intent.putExtra(App.getInstance().KEY_BANK_ID, creditCardLists.get(position - 1).getId());
                setResult(2, intent);
                finish();
                App.getInstance().getPrefsManager()
                        .putInt("creditcard", creditCardLists.get(position - 1).getId());
            }
        };
        OnItemClickListener debitClickListener = new OnItemClickListener() {
            
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (0 == position || position > debitCardLists.size()) {
                    return;
                }
                Intent intent = new Intent();
                intent.putExtra(App.getInstance().KEY_BANK_ID, debitCardLists.get(position - 1).getId());
                setResult(2, intent);
                finish();
                App.getInstance().getPrefsManager()
                        .putInt("debitcard", debitCardLists.get(position - 1).getId());
            }
        };
        creditbankListView.setOnItemClickListener(creditClickListener);
        debitbankListView.setOnItemClickListener(debitClickListener);
    }
    
    private ArrayList<CardList> shifOffenGo(ListView listView, ArrayList<CardList> cardLists, int id) {
        CardList offenGoCard = null;
//        for (int i = 0; i < cardLists.size(); i++) {
//            if (cardLists.get(i).getId() == id) {
//                offenGoCard = cardLists.get(i);
//                cardLists.remove(i);
//            }
//        }
        for (int i = cardLists.size() - 1; i >= 0; i--) {
            if (cardLists.get(i).getId() == id) {
                offenGoCard = cardLists.get(i);
                cardLists.remove(i);
                break;
            }
        }
        addHeadView(listView, offenGoCard);
        return cardLists;
        
    }
    
    private void addHeadView(ListView listView, final CardList offenGoCard) {
        View headView = getLayoutInflater().inflate(R.layout.bank_headview, null);
        LinearLayout offenLin = headView.findViewById(R.id.offengo_lin);
        LinearLayout offenItem = headView.findViewById(R.id.offengo_item);
        TextView offenText = headView.findViewById(R.id.card_text);
        ImageView offenImg = headView.findViewById(R.id.card_img);
        if (offenGoCard == null) {
            offenLin.setVisibility(View.GONE);
        }
        else {
            offenLin.setVisibility(View.VISIBLE);
            offenText.setText(offenGoCard.getName());
            offenItem.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.putExtra(App.getInstance().KEY_BANK_ID, offenGoCard.getId());
                    setResult(2, intent);
                    finish();
                }
            });
            
            volleyImageLoader.displayImage(offenGoCard.getImgUrl(), offenImg, ImageURLManager.ImageStyle.THUMB, null);
        }
        listView.addHeaderView(headView);
    }
    
    @Override
    protected void onLoadData() {
        
    }
    
    @Override
    protected void onRequestData() {
        UIUtil.showLoadingDialog(this);
        HttpUtil.get(ConstantUrl.GET_BANK_CARD, CardListMainBean.class, getbankListCallback);
    }
    
    @Override
    protected void onUnloadData() {
        
    }
    
}
