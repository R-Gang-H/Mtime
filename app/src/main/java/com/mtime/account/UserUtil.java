package com.mtime.account;

import com.kotlin.android.app.data.entity.user.ItemUser;
import com.mtime.bussiness.mine.login.bean.UserItem;

/**
 * Created on 2020/8/14.
 *
 * @author o.s
 */
public class UserUtil {

    public static ItemUser toItemUser(UserItem item){
        return new ItemUser(item.getUserId(),
                item.getNickname(),
                item.getHeadImg(),
                item.getMobile(),
                null,
                item.getGender());
    }

}
