package com.mtime.bussiness.video.view;

import android.content.Context;
import android.graphics.Color;
import androidx.appcompat.widget.AppCompatTextView;

import com.mtime.R;

/**
 * Created by mtime on 2017/10/19.
 */

public class DefinitionItemView extends AppCompatTextView {

    private String mKey = "XX";

    public DefinitionItemView(Context context) {
        super(context);
    }

    public void setCurrentItemKey(String currentItemKey){
        if(mKey.equals(currentItemKey)){
            setTextColor(Color.parseColor("#FF8600"));
            setBackgroundResource(R.drawable.player_sdk_shape_definition_orange);
        }else{
            setTextColor(Color.parseColor("#FFFFFF"));
            setBackgroundResource(R.drawable.player_sdk_shape_definition_transparent);
        }
    }

    public String getKey() {
        return mKey;
    }

    public void setKey(String mKey) {
        this.mKey = mKey;
    }
}
