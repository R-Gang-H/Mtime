package com.mtime.bussiness.ticket.movie.details.bean;

import com.helen.obfuscator.IObfuscateKeepAll;
import com.mtime.base.utils.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ZhouSuQiang
 * @email zhousuqiang@126.com
 * @date 2019-05-30
 */
public class MovieDetailsActors implements IObfuscateKeepAll {
    public long movieId;

    public MovieDetailsBasic.Director director;
    //演员，最多给20个
    public List<MovieDetailsBasic.Actor> actors;

    private boolean hasDirector() {
        return null != director && director.directorId > 0;
    }

    private boolean hasActors() {
        return !CollectionUtils.isEmpty(actors);
    }

    public boolean hasDatas() {
        return hasDirector() || hasActors();
    }

    public int getCount() {
        int count = 0;
        if (hasDirector()) {
            count += 1;
        }
        if (hasActors()) {
            count += actors.size();
        }
        return count;
    }

    private final List<MovieDetailsBasic.Actor> datas = new ArrayList<>();
    public List<MovieDetailsBasic.Actor> getDatas() {
        if (datas.isEmpty()) {
            if (hasDirector()) {
                MovieDetailsBasic.Actor dire = new MovieDetailsBasic.Actor();
                dire.actorId = director.directorId;
                dire.name = director.name;
                dire.nameEn = director.nameEn;
                dire.img = director.img;
                dire.roleName = "导演";
                dire.isDirector = true;
                datas.add(dire);
            }
            if (hasActors()) {
                datas.addAll(actors);
            }
        }
        return datas;
    }
}
