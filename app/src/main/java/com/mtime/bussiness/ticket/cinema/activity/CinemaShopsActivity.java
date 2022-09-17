package com.mtime.bussiness.ticket.cinema.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.mtime.base.location.LocationException;
import com.mtime.frame.App;
import com.mtime.frame.BaseActivity;
import com.mtime.R;
import com.mtime.base.location.LocationInfo;
import com.mtime.base.location.OnLocationCallback;
import com.mtime.bussiness.location.LocationHelper;
import com.mtime.bussiness.ticket.cinema.bean.CinemaViewFeature;
import com.mtime.bussiness.ticket.cinema.bean.BranchCinemasBean;
import com.mtime.widgets.BaseTitleView.StructType;
import com.mtime.widgets.TitleOfNormalView;
import com.mtime.util.MtimeUtils;
import com.mtime.util.ToolsUtils;

import java.util.ArrayList;
import java.util.List;

public class CinemaShopsActivity extends BaseActivity {

    private class ShopAdapter extends BaseAdapter {
        class ViewHolder {
            TextView name;
            TextView address;
            TextView like;
            ImageView ticket;
            View   parks_icon_left;
            ImageView park;
            View facility_icon_left;
            ImageView facility;
            View imax_icon_left;
            ImageView imax;
            View i3d_icon_left;
            ImageView c3d;
            View vip_icon_left;
            ImageView vip;
            View creditcard_icon_left;
            ImageView card;
            View wififree_icon_left;
            ImageView wifi;
            View gamezone_icon_left;
            ImageView game;
            View coupleseat_icon_left;
            ImageView couple;
        }
        
        
        @Override
        public int getCount() {
            return shops.size();
        }

        @Override
        public Object getItem(int arg0) {
            return arg0;
        }

        @Override
        public long getItemId(int arg0) {
            return arg0;
        }

        @Override
        public View getView(final int arg0, View arg1, ViewGroup arg2) {
            final ViewHolder holder;
            if (null == arg1) {
                arg1 = getLayoutInflater().inflate(R.layout.cinema_subshop_listitem, null);
                holder = new ViewHolder();
                
                holder.name = arg1.findViewById(R.id.name);
                holder.address = arg1.findViewById(R.id.address);
                holder.like = arg1.findViewById(R.id.like);
                holder.ticket = arg1.findViewById(R.id.ticket_machine_icon);
                holder.parks_icon_left = arg1.findViewById(R.id.parks_icon_left);
                holder.park = arg1.findViewById(R.id.parks_icon);
                holder.facility_icon_left = arg1.findViewById(R.id.facility_icon_left);
                holder.facility = arg1.findViewById(R.id.facility_icon);
                holder.imax_icon_left = arg1.findViewById(R.id.imax_icon_left);
                holder.imax = arg1.findViewById(R.id.icon_imax);
                holder.i3d_icon_left = arg1.findViewById(R.id.i3d_icon_left);
                holder.c3d = arg1.findViewById(R.id.icon_3d);
                holder.vip_icon_left = arg1.findViewById(R.id.vip_icon_left);
                holder.vip = arg1.findViewById(R.id.icon_vip);
                holder.creditcard_icon_left = arg1.findViewById(R.id.creditcard_icon_left);
                holder.card = arg1.findViewById(R.id.icon_creditcard);
                holder.wififree_icon_left = arg1.findViewById(R.id.wififree_icon_left);
                holder.wifi = arg1.findViewById(R.id.icon_wififree);
                holder.gamezone_icon_left = arg1.findViewById(R.id.gamezone_icon_left);
                holder.game = arg1.findViewById(R.id.icon_gamezone);
                holder.coupleseat_icon_left = arg1.findViewById(R.id.coupleseat_icon_left);
                holder.couple = arg1.findViewById(R.id.icon_coupleseat);
                
                arg1.setTag(holder);
                
            } else {
                holder = (ViewHolder) arg1.getTag();
            }
            
            holder.name.setText(shops.get(arg0).getName());
            
            //calculate distance with latitude/longitude
//            double latitude = (null == App.getInstance().locationCity) ? 0 : App.getInstance().locationCity.getLatitude();
//            double longitude = (null == App.getInstance().locationCity) ? 0 : App.getInstance().locationCity.getLongitude();
    
            holder.address.setText("");
            LocationHelper.location(getApplicationContext(), false, new OnLocationCallback() {
                @Override
                public void onLocationSuccess(LocationInfo locationInfo) {
                    if(null != locationInfo) {
                        double distance = MtimeUtils.gps2m(locationInfo.getLatitude(), locationInfo.getLongitude(), shops.get(arg0).getBaiduLatitude(), shops.get(arg0).getBaiduLongitude());
                        String value = ToolsUtils.getDistance(distance);
    
                        String address = shops.get(arg0).getAddress();
                        if (0 != locationInfo.getLatitude() && 0 != locationInfo.getLongitude()) {
                            address += " " + value;
                        }
                        holder.address.setText(address);
                    }
                }

                @Override
                public void onLocationFailure(LocationException e) {
                    onLocationSuccess(LocationHelper.getDefaultLocationInfo());
                }
            });
            
            //
            StringBuffer sb = new StringBuffer();
            sb.append((int) (shops.get(arg0).getRatingFinal() * 10));
            sb.append("%");
            holder.like.setText(sb.toString());
            
            CinemaViewFeature feature = shops.get(arg0).getFeature();
            if (null == feature) {
                feature = new CinemaViewFeature();
            }
            
            if (1 == feature.getHasServiceTicket()) {
                holder.ticket.setVisibility(View.VISIBLE);
                holder.parks_icon_left.setVisibility(View.VISIBLE);
            } else {
                holder.ticket.setVisibility(View.GONE);
                holder.parks_icon_left.setVisibility(View.GONE);
            }
            
            if (1 == feature.getHasPark()) {
                holder.park.setVisibility(View.VISIBLE);
                holder.facility_icon_left.setVisibility(View.VISIBLE);
            } else {
                holder.park.setVisibility(View.GONE);
                holder.facility_icon_left.setVisibility(View.GONE);
            }
            
            if (1 == feature.getHasFood() || 1 == feature.getHasLeisure()) {
                holder.facility.setVisibility(View.VISIBLE);
                holder.imax_icon_left.setVisibility(View.VISIBLE);
            } else {
                holder.facility.setVisibility(View.GONE);
                holder.imax_icon_left.setVisibility(View.GONE);
            }
            
            if (1 == feature.getHasIMAX()) {
                holder.imax.setVisibility(View.VISIBLE);
                holder.i3d_icon_left.setVisibility(View.VISIBLE);
            } else {
                holder.imax.setVisibility(View.GONE);
                holder.i3d_icon_left.setVisibility(View.GONE);
            }
            
            if (1 == feature.getHas3D()) {
                holder.c3d.setVisibility(View.VISIBLE);
                holder.vip_icon_left.setVisibility(View.VISIBLE);
            } else {
                holder.c3d.setVisibility(View.GONE);
                holder.vip_icon_left.setVisibility(View.GONE);
            }
            
            if (1 == feature.getHasVIP()) {
                holder.vip.setVisibility(View.VISIBLE);
                holder.creditcard_icon_left.setVisibility(View.VISIBLE);
            } else {
                holder.vip.setVisibility(View.GONE);
                holder.creditcard_icon_left.setVisibility(View.GONE);
            }
            
            if (1 == feature.getHasPark()) {
                holder.card.setVisibility(View.VISIBLE);
                holder.wififree_icon_left.setVisibility(View.VISIBLE);
            } else {
                holder.card.setVisibility(View.GONE);
                holder.wififree_icon_left.setVisibility(View.GONE);
            }
            
            if (1 == feature.getHasWifi()) {
                holder.wifi.setVisibility(View.VISIBLE);
                holder.gamezone_icon_left.setVisibility(View.VISIBLE);
            } else {
                holder.wifi.setVisibility(View.GONE);
                holder.gamezone_icon_left.setVisibility(View.GONE);
            }

            if (1 == feature.getHasGame()) {
                holder.game.setVisibility(View.VISIBLE);
                holder.coupleseat_icon_left.setVisibility(View.VISIBLE);
            } else {
                holder.game.setVisibility(View.GONE);
                holder.coupleseat_icon_left.setVisibility(View.GONE);
            }
            
            if (1 == feature.getHasLoveseat()) {
                holder.couple.setVisibility(View.VISIBLE);
            } else {
                holder.couple.setVisibility(View.GONE);
            }
            
            return arg1;
        }
        
    }
    
    public static List<BranchCinemasBean> shops = new ArrayList<BranchCinemasBean>();
    
    @Override
    protected void onInitEvent() {
    }

    @Override
    protected void onInitVariable() {
        setPageLabel("cinemaSubs");
    }

    @Override
    protected void onInitView(Bundle savedInstanceState) {
        this.setContentView(R.layout.cinema_subshop);
        
        View root = this.findViewById(R.id.experience_title);
        new TitleOfNormalView(this, root, StructType.TYPE_NORMAL_SHOW_BACK_TITLE, "分店", null);
        
        ListView list = this.findViewById(R.id.list);
        
        // create adapter here.
        ShopAdapter adapter = new ShopAdapter();
        
        list.setAdapter(adapter);
        list.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                final Intent intent = new Intent();
                intent.putExtra(App.getInstance().KEY_CINEMA_ID, String.valueOf(shops.get(arg2).getCinemaId()));
               startActivity(NewCinemaShowtimeActivity.class, intent);

            }});
        
    }

    @Override
    protected void onLoadData() {
    }

    @Override
    protected void onRequestData() {
    }

    @Override
    protected void onUnloadData() {
    }

}
