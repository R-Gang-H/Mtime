package com.mtime.provider;

import android.content.Context;
import android.os.Bundle;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.kotlin.android.router.RouterManager;
import com.kotlin.android.app.router.path.RouterActivityPath;
import com.kotlin.android.app.router.path.RouterProviderPath;
import com.kotlin.android.app.router.provider.ticket.ITicketProvider;
import com.mtime.bussiness.ticket.cinema.activity.NewCinemaShowtimeActivity;
import com.mtime.bussiness.ticket.movie.activity.MovieShowtimeActivity;
import com.mtime.bussiness.ticket.movie.details.MovieDetailsActivity;
import com.mtime.frame.App;

import org.jetbrains.annotations.NotNull;

/**
 * @author ZhouSuQiang
 * @email zhousuqiang@126.com
 * @date 2020/8/21
 *
 * 票务
 */
@Route(path = RouterProviderPath.Provider.PROVIDER_TICKET)
public class TicketProvider implements ITicketProvider {
    @Override
    public void startMovieDetailsActivity(long movieId) {
        Bundle bundle = new Bundle();
        bundle.putLong(MovieDetailsActivity.KEY_MOVIE_ID, movieId);
        RouterManager.Companion.getInstance().navigation(
                RouterActivityPath.Ticket.PAGER_MOVIE_DETAIL,
                bundle,
                null,
                -1,
                0,
                false,
                null
        );
    }

    @Override
    public void startMovieShowtimeActivity(long movieId) {
        Bundle bundle = new Bundle();
        bundle.putLong(MovieShowtimeActivity.KEY_MOVIE_ID, movieId);
        RouterManager.Companion.getInstance().navigation(
                RouterActivityPath.Ticket.PAGER_MOVIE_SHOWTIME,
                bundle,
                null,
                -1,
                0,
                false,
                null
        );
    }

    @Override
    public void init(Context context) {

    }

    @Override
    public void startCinemaShowTimeActivity(long cinemaId, @NotNull String movieId, @NotNull String showMovieDate) {
        Bundle bundle = new Bundle();
        bundle.putString(NewCinemaShowtimeActivity.KEY_CINEMA_ID,String.valueOf(cinemaId));
        bundle.putString(NewCinemaShowtimeActivity.KEY_MOVIE_ID,movieId);
        bundle.putString(NewCinemaShowtimeActivity.KEY_MOVIE_SHOWTIME_DATE,showMovieDate);
        RouterManager.Companion.getInstance().navigation(
                RouterActivityPath.Ticket.PAGE_CINEMA_SHOWTIME,
                bundle,
                null,
                -1,
                0,
                false,
                null
        );
    }

    @Override
    public void startSeatSelectFromOrderListActivity(long orderId, boolean reselectAgain) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(App.getInstance().KEY_SEATING_SELECT_AGAIN,reselectAgain);
        bundle.putString(App.getInstance().KEY_SEATING_LAST_ORDER_ID,String.valueOf(orderId));
        RouterManager.Companion.getInstance().navigation(
                RouterActivityPath.Ticket.PAGE_SEAT_SELECT,
                bundle,
                null,
                -1,
                0,
                false,
                null
        );
    }

    @Override
    public void startSeatSelectActivity(long showTimeId, @NotNull String date, long movieId, long cinemaId) {
        Bundle bundle = new Bundle();
        bundle.putString(App.getInstance().KEY_SEATING_DID, String.valueOf(showTimeId));
        bundle.putString(App.getInstance().KEY_CINEMA_ID, String.valueOf(cinemaId));
        bundle.putString(App.getInstance().KEY_MOVIE_ID, String.valueOf(movieId));
        bundle.putString(App.getInstance().KEY_SHOWTIME_DATE, date);
        RouterManager.Companion.getInstance().navigation(
                RouterActivityPath.Ticket.PAGE_SEAT_SELECT,
                bundle,
                null,
                -1,
                0,
                false,
                null
        );
    }
}
