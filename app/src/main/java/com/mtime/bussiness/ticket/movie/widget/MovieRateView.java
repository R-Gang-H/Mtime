package com.mtime.bussiness.ticket.movie.widget;

import android.Manifest;
import android.content.Context;
import android.graphics.Point;
import androidx.core.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.mtime.frame.BaseActivity;
import com.mtime.R;
import com.mtime.base.utils.MToastUtils;
import com.mtime.beans.ImageBean;
import com.mtime.common.utils.Utils;
import com.mtime.mtmovie.widgets.ListenerScrollView;
import com.mtime.bussiness.ticket.widget.RateView;
import com.mtime.bussiness.ticket.widget.RateView.IRateViewListener;
import com.mtime.mtmovie.widgets.ScrollGridview;
import com.mtime.statistic.baidu.BaiduConstants;
import com.mtime.util.JumpUtil;
import com.mtime.util.MtimeUtils;
import com.mylhyl.acp.Acp;
import com.mylhyl.acp.AcpListener;
import com.mylhyl.acp.AcpOptions;

import java.io.File;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class MovieRateView implements OnClickListener {
    public enum MovieRateViewEventType {
        TYPE_CANCLE, TYPE_OK, TYPE_CLOSE
    }

    public interface IMovieRateViewListener {
        void onEvent(MovieRateViewEventType type, final List<ImageBean> images, final double rate, final int rateChangedMusic,
                     final int rateChangedGeneral, final int rateChangedDirector, final int rateChangedStory,
                     final int rateChangedPerform, final int rateChangedImpressions, final String content, final boolean isDeploySubitem, final boolean share);
    }

    /**
     * must set the rate value when show the rate view each time.
     */

    private final IMovieRateViewListener listener;
    private List<ImageBean> images;
    private final BaseActivity context;
    private ImgsAdapter adapter;
    private final View root;
    private final Point mPoint = new Point(0, 0);
    private List<ImageBean> bmps = new ArrayList<ImageBean>();//已经选择的图片
    private TextView rateDeploy;
    private TextView rateDeployClose;
    private RelativeLayout rateSubitem;
    private EditText rate_edit;
    private TextView rateResult;
    private TextView raterScore;
    private TextView musicResult;
    private TextView generalResult;
    private TextView directorResult;
    private TextView storyResult;
    private TextView performResult;
    private TextView impressionsResult;
    private ScrollGridview rateGrid;

    private RateView musicView;
    private RateView generalView;
    private RateView directorView;
    private RateView storyView;
    private RateView performView;
    private RateView impressionsView;
    private RateView allRateView;
    private ListenerScrollView scrollView;
    private ImageView scrollShadowLine;

    private int rateMusicQuality;
    private int rateGeneralQuality;
    private int rateDirectorQuality;
    private int rateStoryQuality;
    private int ratePerformQuality;
    private int rateImpressionsQuality;
    // private int rateAllQuality;

    private int rateChangedMusic;
    private int rateChangedGeneral;
    private int rateChangedDirector;
    private int rateChangedStory;
    private int rateChangedPerform;
    private int rateChangedImpressions;
    @SuppressWarnings("unused")
    private double rateChangedAll;
    public boolean deploySubitem;
    private double total;
    private final String pageLabel = "movieScore";
    private static final int PHOTO_WIDTH = 50;

    public MovieRateView(final BaseActivity context, final View root, final String movieId,
                         final IMovieRateViewListener listener) {
        this.context = context;
        this.root = root;
        this.listener = listener;

        this.init();
        deploySubitem(deploySubitem);
    }

    public RateView getAllRateView() {
        return allRateView;
    }

    public void setVisibility(int visibility) {
        if (View.VISIBLE == visibility) {

            this.musicView.setValue(this.rateChangedMusic, false);
            this.generalView.setValue(this.rateChangedGeneral, false);
            this.directorView.setValue(this.rateChangedDirector, false);
            this.storyView.setValue(this.rateChangedStory, false);
            this.performView.setValue(this.rateChangedPerform, false);
            this.impressionsView.setValue(this.rateChangedImpressions, false);
            if (rate_edit != null) {
                rate_edit.setText("");
            }
        }
        else if (getVisibility() == View.VISIBLE) {//如果当前是可见的，说明已经打开了。此时只要传过来的不是可见的，说明都是关闭该对话框。
        }
        root.setVisibility(visibility);
    }

    public int getVisibility() {
        return this.root.getVisibility();
    }

    public void setValues(final int rateChangedMusic, final int rateChangedGeneral, final int rateChangedDirector,
                          final int rateChangedStory, final int rateChangedPerform, final int rateChangedImpressions) {
        this.rateChangedMusic = rateChangedMusic;
        this.rateChangedGeneral = rateChangedGeneral;
        this.rateChangedDirector = rateChangedDirector;
        this.rateChangedStory = rateChangedStory;
        this.rateChangedPerform = rateChangedPerform;
        this.rateChangedImpressions = rateChangedImpressions;

        this.rateMusicQuality = rateChangedMusic;
        this.rateGeneralQuality = rateChangedGeneral;
        this.rateDirectorQuality = rateChangedDirector;
        this.rateStoryQuality = rateChangedStory;
        this.ratePerformQuality = rateChangedPerform;
        this.rateImpressionsQuality = rateChangedImpressions;

        this.musicView.setValue(rateChangedMusic);
        this.generalView.setValue(rateChangedGeneral);
        this.directorView.setValue(rateChangedDirector);
        this.storyView.setValue(rateChangedStory);
        this.performView.setValue(rateChangedPerform);
        this.impressionsView.setValue(rateChangedImpressions);
        if (rateChangedMusic != 0 || rateChangedGeneral != 0 || rateChangedDirector != 0 || rateChangedStory != 0
                || rateChangedPerform != 0 || rateChangedImpressions != 0) {
            this.rateResult.setVisibility(View.VISIBLE);
            raterScore.setVisibility(View.VISIBLE);

        } else {
            this.rateResult.setText("未评分");
            raterScore.setText("0.0");
        }
    }

    public void setValues(final int rateChangedMusic, final int rateChangedGeneral, final int rateChangedDirector,
                          final int rateChangedStory, final int rateChangedPerform, final int rateChangedImpressions,
                          final double rateChangedAll) {
        this.rateChangedMusic = rateChangedMusic;
        this.rateChangedGeneral = rateChangedGeneral;
        this.rateChangedDirector = rateChangedDirector;
        this.rateChangedStory = rateChangedStory;
        this.rateChangedPerform = rateChangedPerform;
        this.rateChangedImpressions = rateChangedImpressions;
        this.rateMusicQuality = rateChangedMusic;
        this.rateGeneralQuality = rateChangedGeneral;
        this.rateDirectorQuality = rateChangedDirector;
        this.rateStoryQuality = rateChangedStory;
        this.ratePerformQuality = rateChangedPerform;
        this.rateImpressionsQuality = rateChangedImpressions;
        this.rateChangedAll = rateChangedAll;
//        this.musicView.setValue(rateChangedMusic, false);
//        this.generalView.setValue(rateChangedGeneral, false);
//        this.directorView.setValue(rateChangedDirector, false);
//        this.storyView.setValue(rateChangedStory, false);
//        this.performView.setValue(rateChangedPerform, false);
//        this.impressionsView.setValue(rateChangedImpressions, false);
//        this.allRateView.setValue(rateChangedAll, false);
        this.musicView.setValue(rateChangedMusic);
        this.generalView.setValue(rateChangedGeneral);
        this.directorView.setValue(rateChangedDirector);
        this.storyView.setValue(rateChangedStory);
        this.performView.setValue(rateChangedPerform);
        this.impressionsView.setValue(rateChangedImpressions);
        this.allRateView.setValue(rateChangedAll, true);
        if (rateChangedMusic != 0 || rateChangedGeneral != 0 || rateChangedDirector != 0 || rateChangedStory != 0
                || rateChangedPerform != 0 || rateChangedImpressions != 0 || rateChangedAll != 0) {
            this.rateResult.setVisibility(View.VISIBLE);
            raterScore.setVisibility(View.VISIBLE);
            update(rateChangedAll);

        } else {
            this.rateResult.setText("未评分");
            raterScore.setText("0.0");
            update(0.0);
        }

    }

    private void init() {
        this.rate_edit = this.root.findViewById(R.id.rate_edit);
        this.rateGrid = this.root.findViewById(R.id.rate_imgs);
        this.raterScore = this.root.findViewById(R.id.rate_score);
        this.rateResult = this.root.findViewById(R.id.rate_result);
        this.rateDeploy = this.root.findViewById(R.id.rate_deploy);
        this.rateDeployClose = this.root.findViewById(R.id.rate_deploy_top);
        adapter = new ImgsAdapter(context);
        this.rateGrid.setAdapter(adapter);
        this.rateGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                final int position = arg2;
                // 权限
                Acp.getInstance(context).request(new AcpOptions.Builder().setPermissions(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                ).build(), new AcpListener() {
                    @Override
                    public void onGranted() {
                        Acp.getInstance(context).onDestroy();
                        if (bmps != null && bmps.size() > 0) {
                            ImageBean ib = bmps.get(0);
                            if (TextUtils.isEmpty(ib.path)) {
//                                Intent intent = new Intent();
//                                context.startActivityForResult(PictureSelectActivity.class, intent);
                                JumpUtil.startPictureSelectActivityForResult(context,"",null, 0);
                            } else {
//                                Intent intent = new Intent();
//                                intent.putExtra(PictureSelectActivity.IMAGESEXTRA, (Serializable) bmps);
//                                context.startActivityForResult(PictureActivity.class, intent);
                                JumpUtil.startPictureActivityForResult(context, "", position, bmps, 0);
                            }
                        }
                    }

                    @Override
                    public void onDenied(List<String> permissions) {
                        Acp.getInstance(context).onDestroy();
                        MToastUtils.showShortToast(permissions.toString() + "权限拒绝");
                    }
                });
//                if (arg2 == bmps.size()-1) {
//                    Intent intent = new Intent();
//                    context.startActivityForResult(PictureSelectActivity.class, intent);
//                } else {
//                    Intent intent = new Intent();
//                    intent.putExtra(PictureSelectActivity.IMAGESEXTRA, (Serializable) bmps);
//                    context.startActivityForResult(PictureActivity.class, intent);
//                }
            }
        });
        rateDeploy.setOnClickListener(this);
        rateDeployClose.setOnClickListener(this);
        ImageButton close = this.root.findViewById(R.id.close);
        close.setOnClickListener(this);
        rateSubitem = this.root.findViewById(R.id.rate_subitem);
        this.musicView = this.root.findViewById(R.id.music_rate);
        this.musicView.addResources(10, R.drawable.movie_rate_off, R.drawable.movie_rate_on);
        this.musicView.setListener(new IRateViewListener() {

            @Override
            public void onEvent(int rateValue) {
                // if (rateValue < 1) {
                // rateValue = 1;
                // musicView.setValue(rateValue);
                // return;
                // }
                rateChangedMusic = rateValue;
                update(musicResult, rateValue);
            }
        });
        this.musicResult = this.root.findViewById(R.id.rate_music_result);

        this.generalView = this.root.findViewById(R.id.general_rate);
        this.generalView.addResources(10, R.drawable.movie_rate_off, R.drawable.movie_rate_on);
        this.generalView.setListener(new IRateViewListener() {

            @Override
            public void onEvent(int rateValue) {
                // if (rateValue < 1) {
                // rateValue = 1;
                // generalView.setValue(rateValue);
                // return;
                // }
                rateChangedGeneral = rateValue;
                update(generalResult, rateValue);
            }
        });
        this.generalResult = this.root.findViewById(R.id.rate_general_result);

        this.directorView = this.root.findViewById(R.id.director_rate);
        this.directorView.addResources(10, R.drawable.movie_rate_off, R.drawable.movie_rate_on);
        this.directorView.setListener(new IRateViewListener() {

            @Override
            public void onEvent(int rateValue) {
                // if (rateValue < 1) {
                // rateValue = 1;
                // directorView.setValue(rateValue);
                // return;
                // }
                rateChangedDirector = rateValue;
                update(directorResult, rateValue);
            }
        });
        this.directorResult = this.root.findViewById(R.id.rate_director_result);

        this.storyView = this.root.findViewById(R.id.story_rate);
        this.storyView.addResources(10, R.drawable.movie_rate_off, R.drawable.movie_rate_on);
        this.storyView.setListener(new IRateViewListener() {

            @Override
            public void onEvent(int rateValue) {
                // if (rateValue < 1) {
                // rateValue = 1;
                // storyView.setValue(rateValue);
                // return;
                // }
                rateChangedStory = rateValue;
                update(storyResult, rateValue);
            }
        });
        this.storyResult = this.root.findViewById(R.id.rate_story_result);

        this.performView = this.root.findViewById(R.id.perform_rate);
        this.performView.addResources(10, R.drawable.movie_rate_off, R.drawable.movie_rate_on);
        this.performView.setListener(new IRateViewListener() {

            @Override
            public void onEvent(int rateValue) {
                // if (rateValue < 1) {
                // rateValue = 1;
                // performView.setValue(rateValue);
                // return;
                // }
                rateChangedPerform = rateValue;
                update(performResult, rateValue);
            }
        });
        this.performResult = this.root.findViewById(R.id.rate_perform_result);

        this.impressionsView = this.root.findViewById(R.id.impressions_rate);
        this.impressionsView.addResources(10, R.drawable.movie_rate_off, R.drawable.movie_rate_on);
        this.impressionsView.setListener(new IRateViewListener() {

            @Override
            public void onEvent(int rateValue) {
                // if (rateValue < 1) {
                // rateValue = 1;
                // impressionsView.setValue(rateValue);
                // return;
                // }
                rateChangedImpressions = rateValue;
                update(impressionsResult, rateValue);
            }
        });
        this.impressionsResult = this.root.findViewById(R.id.rate_impressions_result);
        this.allRateView = this.root.findViewById(R.id.all_rate);
        this.allRateView.addResources(10, R.drawable.rate_off, R.drawable.rate_on);
        this.allRateView.setListener(new IRateViewListener() {

            @Override
            public void onEvent(int rateValue) {
                // 写在这里不合适，将逻辑改到RateView中：currentSelected = currentSelected == 0
                // ? 1 : currentSelected;
                // if (rateValue < 1) {
                // rateValue = 1;
                // total=rateValue;
                // allRateView.setValue(rateValue);
                // return;
                // }
                rateChangedAll = rateValue;
                update(null, rateValue);
            }
        });
        TextView ok = this.root.findViewById(R.id.rate_ok);
        ok.setOnClickListener(this);

        TextView share = this.root.findViewById(R.id.rate_share);
        share.setOnClickListener(this);
        scrollShadowLine = this.root.findViewById(R.id.scroll_shadow_line);
        scrollView = this.root.findViewById(R.id.scrollView);
        scrollView.setScrollListener(new ListenerScrollView.ScrollListener() {
            @Override
            public void scrollChanged(int t) {
                if (t > 0) {
                    if (scrollShadowLine.getVisibility() != View.VISIBLE) {
                        scrollShadowLine.setVisibility(View.VISIBLE);
                    }
                } else {
                    if (scrollShadowLine.getVisibility() == View.VISIBLE) {
                        scrollShadowLine.setVisibility(View.INVISIBLE);
                    }
                }
            }
        });
        bmps.add(new ImageBean(""));
    }

    @Override
    public void onClick(View arg0) {
        switch (arg0.getId()) {
            case R.id.rate_share:
                if (total == 0) {
                    MToastUtils.showShortToast("您还没有任何评分");
                    return;
                }
                int changedNum = 0;
//                if (this.rateMusicQuality != this.rateChangedMusic) {
//                    changedNum++;
//                }
//                if (this.rateGeneralQuality != this.rateChangedGeneral) {
//                    changedNum++;
//                }
//                if (this.rateDirectorQuality != this.rateChangedDirector) {
//                    changedNum++;
//                }
//                if (this.rateStoryQuality != this.rateChangedStory) {
//                    changedNum++;
//                }
//                if (this.ratePerformQuality != this.rateChangedPerform) {
//                    changedNum++;
//                }
//                if (this.rateImpressionsQuality != this.rateChangedImpressions) {
//                    changedNum++;
//                }
                if (this.rateChangedMusic != 0) {
                    changedNum++;
                }
                if (this.rateChangedGeneral != 0) {
                    changedNum++;
                }
                if (this.rateChangedDirector != 0) {
                    changedNum++;
                }
                if (this.rateChangedStory != 0) {
                    changedNum++;
                }
                if (this.rateChangedPerform != 0) {
                    changedNum++;
                }
                if (this.rateChangedImpressions != 0) {
                    changedNum++;
                }
                if (changedNum < 2 && deploySubitem) {
                    MToastUtils.showShortToast("请至少对两个分项进行评分");
                    return;
                }

                this.rateMusicQuality = this.rateChangedMusic;
                this.rateGeneralQuality = this.rateChangedGeneral;
                this.rateDirectorQuality = this.rateChangedDirector;
                this.rateStoryQuality = this.rateChangedStory;
                this.ratePerformQuality = this.rateChangedPerform;
                this.rateImpressionsQuality = this.rateChangedImpressions;
                this.setVisibility(View.INVISIBLE);
                if (null != listener) {
                    listener.onEvent(MovieRateViewEventType.TYPE_OK, images, total, this.rateMusicQuality,
                            this.rateGeneralQuality, this.rateDirectorQuality, this.rateStoryQuality,
                            this.ratePerformQuality, this.rateImpressionsQuality, rate_edit.getText().toString(), deploySubitem, true);
                }

                InputMethodManager imm2 = (InputMethodManager) context.getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm2.hideSoftInputFromWindow(rate_edit.getWindowToken(), 0);

                break;
            case R.id.rate_deploy:
                deploySubitem = true;
                deploySubitem(deploySubitem);
                // this.setVisibility(View.VISIBLE);
                break;
            case R.id.rate_deploy_top:
                deploySubitem = false;
                deploySubitem(deploySubitem);
                break;
            case R.id.close:
                this.setVisibility(View.INVISIBLE);
                if (null != listener) {
                    // listener.onEvent(MovieRateViewEventType.TYPE_OK, 0d, 0,
                    // 0, 0, 0, 0, 0);
                    listener.onEvent(MovieRateViewEventType.TYPE_CLOSE, images, 0d, 0, 0, 0, 0, 0, 0, rate_edit.getText().toString(), deploySubitem, false);

                }

                // 这里隐藏输入法
                InputMethodManager imm = (InputMethodManager) context.getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(rate_edit.getWindowToken(), 0);
                // this.rateChangedMusic = 0;
                // this.rateChangedGeneral = 0;
                // this.rateChangedDirector = 0;
                // this.rateChangedStory = 0;
                // this.rateChangedPerform = 0;
                // this.rateChangedImpressions = 0;
                break;
            case R.id.rate_ok:
                if (total == 0 && TextUtils.isEmpty(rate_edit.getText().toString().trim())) {
                    MToastUtils.showShortToast("您还没有任何评分");
                    return;
                }
                this.setVisibility(View.INVISIBLE);
                this.rateMusicQuality = this.rateChangedMusic;
                this.rateGeneralQuality = this.rateChangedGeneral;
                this.rateDirectorQuality = this.rateChangedDirector;
                this.rateStoryQuality = this.rateChangedStory;
                this.ratePerformQuality = this.rateChangedPerform;
                this.rateImpressionsQuality = this.rateChangedImpressions;
                if (null != listener) {
                    listener.onEvent(MovieRateViewEventType.TYPE_OK, images, total, this.rateMusicQuality,
                            this.rateGeneralQuality, this.rateDirectorQuality, this.rateStoryQuality,
                            this.ratePerformQuality, this.rateImpressionsQuality, rate_edit.getText().toString(), deploySubitem, false);
                    // listener.onEvent(MovieRateViewEventType.TYPE_OK, total);
                }

                InputMethodManager imm1 = (InputMethodManager) context.getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm1.hideSoftInputFromWindow(rate_edit.getWindowToken(), 0);
                break;
            default:
                break;
        }

    }

    public void update(double value) {
        total = value;
        if (total >= 10) {
            raterScore.setText("10");
        } else {
            DecimalFormat format = new DecimalFormat("0.0");
            raterScore.setText(format.format(new BigDecimal(String.valueOf(total))));
        }
        if (total == 0) {
            this.rateResult.setText("未评分");
            raterScore.setText("0.0");
        } else {
            this.rateResult.setText(MtimeUtils.getDefaultScoreContent(total));
        }
        this.rateResult.setVisibility(View.VISIBLE);
        raterScore.setVisibility(View.VISIBLE);
    }

    private void update(TextView view, int value) {
        int selectedColor = ContextCompat.getColor(context,R.color.color_659d0e);

        total = value;
        if (view != null) {
            switch (value) {
                case 0:
                    // update(view,1);
                    selectedColor = ContextCompat.getColor(context,R.color.color_777777);
                    view.setText("");
                    view.setTextColor(selectedColor);
                    break;
                default:
                    view.setText(String.valueOf(value));
                    view.setTextColor(selectedColor);
                    break;
            }
            total = this.rateChangedMusic * 0.0495 + this.rateChangedGeneral * 0.1182 + this.rateChangedDirector
                    * 0.2364 + this.rateChangedStory * 0.1912 + this.rateChangedPerform * 0.174
                    + this.rateChangedImpressions * 0.2307;
        }
        if (total >= 10) {
            raterScore.setText("10");
        } else {
            DecimalFormat format = new DecimalFormat("0.0");
            raterScore.setText(format.format(new BigDecimal(String.valueOf(total))));
        }

        this.rateResult.setVisibility(View.VISIBLE);
        raterScore.setVisibility(View.VISIBLE);
        if (total == 0) {
            this.rateResult.setText("未评分");
            raterScore.setText("0.0");
        } else {
            this.rateResult.setText(MtimeUtils.getDefaultScoreContent(total));
        }

    }

    public String getDefContent() {
        return this.rateResult.getText().toString();
    }


    public void deploySubitem(boolean deploy) {

        if (deploy) {
            // setValues((int) total, (int) total, (int) total, (int) total,
            // (int) total, (int) total);
            this.allRateView.setVisibility(View.GONE);
            rateSubitem.setVisibility(View.VISIBLE);
            rateDeployClose.setVisibility(View.VISIBLE);
            rateDeploy.setVisibility(View.GONE);
            // setValues(this.rateChangedMusic, this.rateChangedGeneral,
            // this.rateChangedDirector, this.rateChangedStory,
            // this.rateChangedPerform, this.rateChangedImpressions,total);
            // setVisibility(View.VISIBLE);
        } else {
            this.allRateView.setValue(total, false);
            update(total);
            this.allRateView.setVisibility(View.VISIBLE);
            rateSubitem.setVisibility(View.GONE);
            rateDeployClose.setVisibility(View.GONE);
            rateDeploy.setVisibility(View.VISIBLE);
        }
    }

    public class ImgsAdapter extends BaseAdapter {
        private final LayoutInflater inflater;
        private final int selectedPosition = -1;
        private boolean shape;


        public ImgsAdapter(Context context) {
            inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return bmps == null ? 0 : bmps.size();
        }

        @Override
        public Object getItem(int i) {
            return bmps == null ? null : bmps.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View convertView, ViewGroup viewGroup) {
            final ImageBean ib = (ImageBean) getItem(i);
            final ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = inflater.inflate(R.layout.item_movie_rateview_grid, viewGroup, false);
                holder.image = convertView.findViewById(R.id.item_image);
//                holder.image.setOnMeasureListener(new MyImageView.OnMeasureListener() {
//                    @Override
//                    public void onMeasureSize(int width, int height) {
//                        mPoint.set(width, height);
//                    }
//                });
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
//            holder.image.setTag(ib.path);
            if (TextUtils.isEmpty(ib.path)) {
                holder.image.setImageResource(R.drawable.add_pic);
            } else {
                File file = new File(ib.path);
                if (null == file) {
                    holder.image.setImageResource(R.drawable.add_pic);
                } else {
                    context.volleyImageLoader.displayFile(file, holder.image, R.drawable.add_pic, R.drawable.add_pic,
                            Utils.dip2px(context, PHOTO_WIDTH), Utils.dip2px(context, PHOTO_WIDTH), null);
                }

//                Bitmap bitmap = NativeImageLoader.getInstance().loadNativeImage(
//                        ib.path, mPoint, new NativeImageLoader.ImageLoaderCallBack() {
//
//                            @Override
//                            public void onImageLoader(Bitmap bitmap, String path) {
//                                if (rateGrid != null) {
//                                    ImageView mImageView = (ImageView) rateGrid.findViewWithTag(ib.path);
//                                    if (bitmap != null && mImageView != null) {
//                                        mImageView.setImageBitmap(bitmap);
//                                    }
//                                }
//
//                                if (null != bitmap) {
//                                    holder.image.setImageBitmap(bitmap);
//                                }
//                            }
//                        });
//                if (bitmap != null) {
//                    holder.image.setImageBitmap(bitmap);
//                }
            }
            return convertView;
        }

        public class ViewHolder {
            public ImageView image;
        }
    }

    public void setRateGridImgs(List<ImageBean> images) {
        this.images = images;
        if (bmps == null) {
            bmps = new ArrayList<ImageBean>();
        }
        bmps.clear();
        if (images != null) {
            bmps.addAll(this.images);
        }
        if (bmps.size() == 0) {
            bmps.add(new ImageBean(""));
        }
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    public List<ImageBean> getImgs() {
        return images;

    }

}
