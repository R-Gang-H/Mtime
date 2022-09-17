package com.mtime.mtmovie.widgets;

import com.mtime.bussiness.ticket.movie.bean.SeatInfo;

public interface ISeatSelectInterface
{
    void onSelect(SeatInfo seat, int selectStatus);
    
    void onMaxOrMin(boolean isMax);

    void onBind();

    void onScaleChenged(float scale);
}
