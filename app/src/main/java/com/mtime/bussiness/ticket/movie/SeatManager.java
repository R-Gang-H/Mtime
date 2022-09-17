package com.mtime.bussiness.ticket.movie;

import com.mtime.bussiness.ticket.movie.bean.Seat;
import com.mtime.bussiness.ticket.movie.bean.SeatInfo;
import com.mtime.common.utils.LogWriter;
import com.mtime.mtmovie.widgets.ISeatSelectInterface;
import com.mtime.bussiness.ticket.movie.bean.ImagePiece;

import java.util.ArrayList;
import java.util.List;

import android.text.TextUtils;

public class SeatManager {

    // 选座 返回值的枚举
    public static final int RESULT_NONE = 0; // 没选择
    public static final int RESULT_OK = 1; // 正常
    public static final int RESULT_NOTSEAT = -2; // 该座位不存在
    public static final int RESULT_SELECTED_BY_OTHERS = -3; // 此座位已被他人锁定，如果20分钟内没有完成支付将自动解锁
    public static final int RESULT_MORE_THAN_LIMIT = -4; // 超过买票上限
    public static final int RESULT_ONE_LEFT_BUT_CHOOSE_LOVERSEAT = -5; // 该座位为情侣座位，不单独销售，您还可以选择一个普通座位
    public static final int RESULT_LEFT_SINGLE_SEAT = -6; // 请连续选座位，不可留下单独座位
    public static final int RESULT_ONE_LEFT = -7; // 您只能选择一张票

    // 座位状态Status的枚举
    public static final int STATUS_CAN_SELECT = 1; // 可选
    public static final int STATUS_SELECTED_BY_SELF = 2; // 已选（自己选中）
    public static final int STATUS_SELECTED_BY_OTHERS = 3; // 已选（别人选中）
    public static final int STATUS_NOT_SEAT = 4; // 不是座位

    // 座位类型的枚举
    public static final int TYPE_NORMAL = 0; // 普通座位
    public static final int TYPE_DISABLED = 1; // 残疾人座位
    public static final int TYPE_LOVERLEFT = 2; // 情侣座-左
    public static final int TYPE_LOVERRIGHT = 3; // 情侣座-右

    private SeatInfo[] arrAllSeats;
    public ArrayList<SeatInfo> selectedSeatList = new ArrayList<SeatInfo>();
    private int limitCount;

    private int rowCount;
    private int columnCount;

    public SeatManager() {
    }


    /**
     * @param seatlist    座位列表
     * @param limitCount  最多能选座位数量
     * @param rowCount    有几排
     * @param columnCount 每排几个座位
     */
    public void initSeats(final List<Seat> seatlist, final int limitCount, final int rowCount, final int columnCount) {
        this.arrAllSeats = new SeatInfo[seatlist.size()];
        this.selectedSeatList = new ArrayList<SeatInfo>();
        this.limitCount = limitCount;

        this.columnCount = columnCount;
        this.rowCount = rowCount;

        // 向数组填充座位
        for (int i = 0; i < seatlist.size(); i++) {
            final Seat item = seatlist.get(i);
            final SeatInfo seatInfo = new SeatInfo();

            seatInfo.setSeatNum(item.getSeatNumber());
            seatInfo.setSeatId(item.getId());
            seatInfo.setType(item.getType());
            seatInfo.setX(item.getX());
            seatInfo.setY(item.getY());
            seatInfo.setSeatName(item.getName());
            if (TextUtils.isEmpty(item.getId())) {
                seatInfo.setStatus(STATUS_NOT_SEAT);
            } else if (item.isStatus() == true) {
                seatInfo.setStatus(STATUS_CAN_SELECT);
            } else {
                seatInfo.setStatus(STATUS_SELECTED_BY_OTHERS);
            }

            this.arrAllSeats[columnCount * item.getY() + item.getX()] = seatInfo;
        }
    }

    public SeatInfo[] getAllSeats() {
        return this.arrAllSeats;
    }

    public ArrayList<SeatInfo> getSelectedSeatList() {
        if (this.selectedSeatList == null) {
            selectedSeatList = new ArrayList<SeatInfo>();
        }
        return this.selectedSeatList;
    }

    public void setSelectedSeatList(ArrayList<SeatInfo> selectedSeatList) {
        this.selectedSeatList = selectedSeatList;
    }


    // / <summary>
    // / 已过时，仅用于单元测试
    // / </summary>
    // / <param name="x"></param>
    // / <param name="y"></param>
    // / <returns></returns>
    public int chooseSeat(final int x, final int y) {
        // 不是座位
        if (x >= this.columnCount || y >= this.rowCount) {
            return RESULT_NOTSEAT;
        }

        final int curPos = this.getPositionInSeatArray(x, y);
        final SeatInfo findSeat = this.arrAllSeats[curPos];

        // 不是座位
        if (findSeat.getStatus() == STATUS_NOT_SEAT) {
            return RESULT_NOTSEAT;
        }

        // 被别人选了
        if (findSeat.getStatus() == STATUS_SELECTED_BY_OTHERS) {
            return RESULT_SELECTED_BY_OTHERS;
        }

        // 判断当前座位是选中状态(true)还是取消选中状态(true)
        if (findSeat.getStatus() == STATUS_CAN_SELECT) // 选中的逻辑
        {
            return this.performChooseSeat(findSeat);
        } else
        // 取消选中的逻辑 即findSeat.Status == SeatStatus_SelectedByMyself
        {
            return this.performCancelSeat(findSeat);
        }
    }

    // / <summary>
    // / 选座
    // / </summary>
    // / <param name="findSeat"></param>
    int performChooseSeat(final SeatInfo findSeat) {
        if (this.selectedSeatList.size() >= this.limitCount) {
            return RESULT_MORE_THAN_LIMIT;
        }

        // 如果是情侣座
        int result = this.handleLoverSeatWhenSelectSeat(findSeat);
        if (result != RESULT_NONE) {
            return result;
        }

        // 接下来是普通座的连选逻辑
        // 这里不可选的定义是，墙，情侣座，被其他人选，不是座位
        // 处理孤岛1个座，能处理就直接返回，否则继续下面的逻辑
        result = this.handle1IslandWhenSelectSeat(findSeat);
        if (result != RESULT_NONE) {
            return result;
        }

        // 处理孤岛2个座，能处理就直接返回，否则继续下面的逻辑
        result = this.handle2IslandWhenSelectSeat(findSeat);
        if (result != RESULT_NONE) {
            return result;
        }

        // 处理孤岛3个座，能处理就直接返回，否则继续下面的逻辑
        result = this.handle3IslandWhenSelectSeat(findSeat);
        if (result != RESULT_NONE) {
            return result;
        }

        // 处理孤岛>=4个座，能处理就直接返回，否则继续下面的逻辑
        result = this.handle4IslandWhenSelectSeat(findSeat);
        if (result != RESULT_NONE) {
            return result;
        }

        // 处理上述所有情况的漏网之鱼，都是单选，而且视为成功
        this.selectSeat(findSeat); // 其他座位都是单选
        return RESULT_OK;
    }

    // / <summary>
    // / 取消选座
    // / </summary>
    // / <param name="findSeat"></param>
    int performCancelSeat(final SeatInfo findSeat) {
        // 先找"同生共死连坐对"
        int result = this.handleDoubleSelectWhenCancelSeat(findSeat);
        if (result != RESULT_NONE) {
            return result;
        }

        // 如果没有"同生共死连坐对",也就是说，都是一个一个选上的，分2种情况
        // 情况1：从墙开始，按顺序一个一个选
        // 情况2：//从中间的某个位置开始，按顺序一个一个选，这个中间位置，距离最近的不可选座位，至少要有2个座位，不然会导致连选

        // 先考虑一种特例，孤岛2个座，两边都是不可选的.那么仅仅是单删
        result = this.handle2IslandSpecialCaseWhenCancelSeat(findSeat);
        if (result != RESULT_NONE) {
            return result;
        }

        // 取消选座，处理从墙（左边）的一边，一个一个选起
        result = this.handleSelectFromLeftWallOneByOneWhenCancelSeat(findSeat);
        if (result != RESULT_NONE) {
            return result;
        }

        // 取消选座，处理从墙（右边）的一边，一个一个选起
        result = this.handleSelectFromRightWallOneByOneWhenCancelSeat(findSeat);
        if (result != RESULT_NONE) {
            return result;
        }

        // 从中间的某个位置开始，按顺序一个一个选
        result = this.handleSelectFromMiddleOneByOneWhenCancelSeat(findSeat);
        if (result != RESULT_NONE) {
            return result;
        }

        // 处理所有其他情况，漏网之鱼，全都视为单删
        this.deselectSeat(findSeat);
        return RESULT_OK;
    }

    // 取消选座，处理"同生共死连坐对"
    int handleDoubleSelectWhenCancelSeat(final SeatInfo findSeat) {
        for (final int weight : this.arrWeight) {
            if (weight == 0) {
                continue;
            }

            if (weight == (findSeat.getX() * 2 + 1) + findSeat.getY() * 1000) // 找到"同生共死连坐对"，findSeat在左，另一个是R1
            {
                final SeatInfo seatR1 = this.getRightSeat(findSeat, 1);
                this.unbindSeats(findSeat, seatR1);
                return RESULT_OK;
            } else if (weight == (findSeat.getX() * 2 - 1) + findSeat.getY() * 1000) // 找到"同生共死连坐对"，findSeat在右，另一个是L1
            {
                final SeatInfo seatL1 = this.getLeftSeat(findSeat, 1);
                this.unbindSeats(findSeat, seatL1);
                return RESULT_OK;
            }
        }

        return RESULT_NONE;
    }

    // 取消选座，处理特例，孤岛2个座，两边都是不可选的.那么仅仅是单删
    int handle2IslandSpecialCaseWhenCancelSeat(final SeatInfo findSeat) {
        final SeatInfo seatL1 = this.getLeftSeat(findSeat, 1);
        final SeatInfo seatR1 = this.getRightSeat(findSeat, 1);
        final SeatInfo seatL2 = this.getLeftSeat(findSeat, 2);
        final SeatInfo seatR2 = this.getRightSeat(findSeat, 2);

        if ((!this.canSelect(seatL2) && !this.canSelect(seatR1)) || (!this.canSelect(seatL1) && !this.canSelect(seatR2))) {
            this.deselectSeat(findSeat);
            return RESULT_OK;
        }

        return RESULT_NONE;
    }

    // 取消选座，处理从墙（左边）的一边，一个一个选起
    int handleSelectFromLeftWallOneByOneWhenCancelSeat(final SeatInfo findSeat) {
        final SeatInfo seatL1 = this.getLeftSeat(findSeat, 1);
        final SeatInfo seatR1 = this.getRightSeat(findSeat, 1);
        final SeatInfo seatL2 = this.getLeftSeat(findSeat, 2);
        final SeatInfo seatR2 = this.getRightSeat(findSeat, 2);
        final SeatInfo seatL3 = this.getLeftSeat(findSeat, 3);
        final SeatInfo seatR3 = this.getRightSeat(findSeat, 3);

        // 从不可选开始，按顺序一个一个选上的：墙，1，2，3，4
        if (!this.canSelect(seatL1) && this.isSelectByMyselfInNormalMode(seatR1) && this.isSelectByMyselfInNormalMode(seatR2)
                && this.isSelectByMyselfInNormalMode(seatR3)) {
            // 4个连选，取消1，连删2
            this.deselectSeat(findSeat);
            this.deselectSeat(seatR1);
            return RESULT_OK;
        } else if (!this.canSelect(seatL2) && this.isSelectByMyselfInNormalMode(seatL1) && this.isSelectByMyselfInNormalMode(seatR1)
                && this.isSelectByMyselfInNormalMode(seatR2)) {
            // 4个连选，取消2，连删1
            this.deselectSeat(findSeat);
            this.deselectSeat(seatL1);
            return RESULT_OK;
        } else if (!this.canSelect(seatL3) && this.isSelectByMyselfInNormalMode(seatL2) && this.isSelectByMyselfInNormalMode(seatL1)
                && this.isSelectByMyselfInNormalMode(seatR1)) {
            // 4个连选，取消3，连删4
            this.deselectSeat(findSeat);
            this.deselectSeat(seatR1);
            return RESULT_OK;
        } else if (!this.canSelect(seatL1) && this.isSelectByMyselfInNormalMode(seatR1) && this.isSelectByMyselfInNormalMode(seatR2)) {
            // 3个连选，取消1，连删2
            this.deselectSeat(findSeat);
            this.deselectSeat(seatR1);
            return RESULT_OK;
        } else if (!this.canSelect(seatL2) && this.isSelectByMyselfInNormalMode(seatL1) && this.isSelectByMyselfInNormalMode(seatR1)) {
            // 3个连选，取消2，连删3
            this.deselectSeat(findSeat);
            this.deselectSeat(seatR1);
            return RESULT_OK;
        } else if (!this.canSelect(seatL1) && this.isSelectByMyselfInNormalMode(seatR1)) {
            // 2个连选，取消1，连删2
            this.deselectSeat(findSeat);
            this.deselectSeat(seatR1);
            return RESULT_OK;
        }

        return RESULT_NONE;
    }

    // 取消选座，处理从墙（右边）的一边，一个一个选起
    int handleSelectFromRightWallOneByOneWhenCancelSeat(final SeatInfo findSeat) {
        final SeatInfo seatL1 = this.getLeftSeat(findSeat, 1);
        final SeatInfo seatR1 = this.getRightSeat(findSeat, 1);
        final SeatInfo seatL2 = this.getLeftSeat(findSeat, 2);
        final SeatInfo seatR2 = this.getRightSeat(findSeat, 2);
        final SeatInfo seatL3 = this.getLeftSeat(findSeat, 3);
        final SeatInfo seatR3 = this.getRightSeat(findSeat, 3);

        // 另一个方向，从墙开始，按顺序一个一个选上的：4，3，2，1，墙
        if (!this.canSelect(seatR1) && this.isSelectByMyselfInNormalMode(seatL1) && this.isSelectByMyselfInNormalMode(seatL2)
                && this.isSelectByMyselfInNormalMode(seatL3)) {
            // 4个连选，取消1，连删2
            this.deselectSeat(findSeat);
            this.deselectSeat(seatL1);
            return RESULT_OK;
        } else if (!this.canSelect(seatR2) && this.isSelectByMyselfInNormalMode(seatR1) && this.isSelectByMyselfInNormalMode(seatL1)
                && this.isSelectByMyselfInNormalMode(seatL2)) {
            // 4个连选，取消2，连删1
            this.deselectSeat(findSeat);
            this.deselectSeat(seatR1);
            return RESULT_OK;
        } else if (!this.canSelect(seatR3) && this.isSelectByMyselfInNormalMode(seatR2) && this.isSelectByMyselfInNormalMode(seatR1)
                && this.isSelectByMyselfInNormalMode(seatL1)) {
            // 4个连选，取消3，连删4
            this.deselectSeat(findSeat);
            this.deselectSeat(seatL1);
            return RESULT_OK;
        } else if (!this.canSelect(seatR1) && this.isSelectByMyselfInNormalMode(seatL1) && this.isSelectByMyselfInNormalMode(seatL2)) {
            // 3个连选，取消1，连删2
            this.deselectSeat(findSeat);
            this.deselectSeat(seatL1);
            return RESULT_OK;
        } else if (!this.canSelect(seatR2) && this.isSelectByMyselfInNormalMode(seatR1) && this.isSelectByMyselfInNormalMode(seatL1)) {
            // 3个连选，取消2，连删3
            this.deselectSeat(findSeat);
            this.deselectSeat(seatL1);
            return RESULT_OK;
        } else if (!this.canSelect(seatR1) && this.isSelectByMyselfInNormalMode(seatL1)) {
            // 2个连选，取消1，连删2
            this.deselectSeat(findSeat);
            this.deselectSeat(seatL1);
            return RESULT_OK;
        }

        return RESULT_NONE;
    }

    // 从中间的某个位置开始，按顺序一个一个选
    int handleSelectFromMiddleOneByOneWhenCancelSeat(final SeatInfo findSeat) {
        final SeatInfo seatL1 = this.getLeftSeat(findSeat, 1);
        final SeatInfo seatR1 = this.getRightSeat(findSeat, 1);
        final SeatInfo seatL2 = this.getLeftSeat(findSeat, 2);
        final SeatInfo seatR2 = this.getRightSeat(findSeat, 2);

        // 4个连续，1，2，3，4。取消2，连删1
        if (this.isSelectByMyselfInNormalMode(seatL1) && this.isSelectByMyselfInNormalMode(seatR1)
                && this.isSelectByMyselfInNormalMode(seatR2)) {
            this.deselectSeat(findSeat);
            this.deselectSeat(seatL1);
            return RESULT_OK;
        }
        // 4个连续，1，2，3，4。取消3，连删4
        else if (this.isSelectByMyselfInNormalMode(seatL2) && this.isSelectByMyselfInNormalMode(seatL1)
                && this.isSelectByMyselfInNormalMode(seatR1)) {
            this.deselectSeat(findSeat);
            this.deselectSeat(seatR1);
            return RESULT_OK;
        }
        // 3个连选，取消中间的，连删，从已选中座位数组中，从后往前找L1或R1，先找到哪个就删除哪个
        else if (this.isSelectByMyselfInNormalMode(seatL1) && this.isSelectByMyselfInNormalMode(seatR1)) {
            for (int i = this.selectedSeatList.size() - 1; i >= 0; i--) {
                final SeatInfo seatInfo = this.selectedSeatList.get(i);
                if (seatInfo.getX() == seatL1.getX() && seatInfo.getY() == seatL1.getY()) {
                    this.deselectSeat(findSeat);
                    this.deselectSeat(seatL1);
                    return RESULT_OK;
                } else if (seatInfo.getX() == seatR1.getX() && seatInfo.getY() == seatR1.getY()) {
                    this.deselectSeat(findSeat);
                    this.deselectSeat(seatR1);
                    return RESULT_OK;
                }
            }
        }

        return RESULT_NONE;
    }

    // 选情侣座
    int handleLoverSeatWhenSelectSeat(final SeatInfo findSeat) {
        if (findSeat.getType() == TYPE_LOVERLEFT || findSeat.getType() == TYPE_LOVERRIGHT) {
            if (this.selectedSeatList.size() + 2 > this.limitCount) {
                return RESULT_ONE_LEFT_BUT_CHOOSE_LOVERSEAT;
            }

            // 情侣座 2为左边
            if (findSeat.getType() == TYPE_LOVERLEFT) {
                // 顺便把右边也选了
                final SeatInfo seatR1 = this.getRightSeat(findSeat, 1);
                if (seatR1 != null) {
                    this.bindSeats(findSeat, seatR1);
                    return RESULT_OK;
                }
            } else if (findSeat.getType() == TYPE_LOVERRIGHT) {
                // 3为右边，顺便把左边也选了
                final SeatInfo seatL1 = this.getLeftSeat(findSeat, 1);
                if (seatL1 != null) {
                    this.bindSeats(findSeat, seatL1);
                    return RESULT_OK;
                }
            }
        }

        return RESULT_NONE;
    }

    // 选座，处理孤岛>=4个座位
    int handle4IslandWhenSelectSeat(final SeatInfo findSeat) {
        final SeatInfo seatL1 = this.getLeftSeat(findSeat, 1);
        final SeatInfo seatR1 = this.getRightSeat(findSeat, 1);
        final SeatInfo seatL2 = this.getLeftSeat(findSeat, 2);
        final SeatInfo seatR2 = this.getRightSeat(findSeat, 2);

        // 对于孤岛>=4
        // 1)从任意一边算起，如果当前选中了第二个座位，那么会把第一个座位也选中
        // 2)其他座位都是单选

        //如果L2不可选或已经被我选了，且L1不可选，那么连选
        if ((!this.canSelect(seatL2) || this.isSelectByMyselfInNormalMode(seatL2)) && this.canSelect(seatL1) && seatL1.getStatus() != STATUS_SELECTED_BY_SELF) // 从左边算起
        {
            if (this.selectedSeatList.size() + 2 > this.limitCount) {
                return RESULT_ONE_LEFT;
            }

            this.bindSeats(findSeat, seatL1);
            return RESULT_OK;
        }
        //如果R2不可选或已经被我选了，且R1不可选，那么连选
        else if ((!this.canSelect(seatR2) || this.isSelectByMyselfInNormalMode(seatR2)) && this.canSelect(seatR1) && seatR1.getStatus() != STATUS_SELECTED_BY_SELF) // 从右边算起
        {
            if (this.selectedSeatList.size() + 2 > this.limitCount) {
                return RESULT_ONE_LEFT;
            }

            this.bindSeats(findSeat, seatR1);
            return RESULT_OK;
        }

        return RESULT_NONE;
    }

    // 选座，处理孤岛3个座位
    int handle3IslandWhenSelectSeat(final SeatInfo findSeat) {
        final SeatInfo seatL1 = this.getLeftSeat(findSeat, 1);
        final SeatInfo seatR1 = this.getRightSeat(findSeat, 1);
        final SeatInfo seatL2 = this.getLeftSeat(findSeat, 2);
        final SeatInfo seatR2 = this.getRightSeat(findSeat, 2);
        final SeatInfo seatL3 = this.getLeftSeat(findSeat, 3);
        final SeatInfo seatR3 = this.getRightSeat(findSeat, 3);

        // 孤岛3个座位，最左边和最右边的位置都是单选
        // 先考虑孤岛中间的那个位置
        if (this.isIsland(seatL2, seatR2) && this.canSelect(seatL1) && this.canSelect(seatR1)) // 选中间座位,
        // L2,(L1,cur,R1),R2
        {
            // 1）如果孤岛的左1和右1（即L2和R2）都是不可选的，那么中间位置不可选
            if (!this.canSelect(seatL2) && !this.canSelect(seatR2)) {
                return RESULT_LEFT_SINGLE_SEAT;
            } else if (this.isSelectByMyselfInNormalMode(seatL2) && this.isSelectByMyselfInNormalMode(seatR2)) {
                return RESULT_LEFT_SINGLE_SEAT;
            } else if (!this.canSelect(seatL2) && this.isSelectByMyselfInNormalMode(seatR2)) {
                if (this.selectedSeatList.size() + 2 > this.limitCount) {
                    return RESULT_ONE_LEFT;
                }

                this.bindSeats(findSeat, seatR1);
                return RESULT_OK;
            }
            // 4）如果孤岛的左1和右1（即L2和R2），L2是自己选的，R2不可选，那么选中中间位值的同时，也会勾选L1
            else if (!this.canSelect(seatR2) && this.isSelectByMyselfInNormalMode(seatL2)) {
                if (this.selectedSeatList.size() + 2 > this.limitCount) {
                    return RESULT_ONE_LEFT;
                }

                this.bindSeats(findSeat, seatL1);
                return RESULT_OK;
            }
        } else if (this.isIsland(seatL1, seatR3) && this.canSelect(seatR1) && this.canSelect(seatR2)) // 选最左边，单选,
        // L1,(cur,R1,R2),R3
        {
            this.selectSeat(findSeat);
            return RESULT_OK;
        } else if (this.isIsland(seatL3, seatR1) && this.canSelect(seatL2) && this.canSelect(seatL1)) // 选最右边，单选,
        // L3,(L2,L1,cur),R1
        {
            this.selectSeat(findSeat);
            return RESULT_OK;
        }

        return RESULT_NONE;
    }

    // 选座，处理孤岛2个座位
    int handle2IslandWhenSelectSeat(final SeatInfo findSeat) {
        final SeatInfo seatL1 = this.getLeftSeat(findSeat, 1);
        final SeatInfo seatR1 = this.getRightSeat(findSeat, 1);
        final SeatInfo seatL2 = this.getLeftSeat(findSeat, 2);
        final SeatInfo seatR2 = this.getRightSeat(findSeat, 2);
        // seatL3 seatR3用于处理, 影院方要求不能留下单个空座
        final SeatInfo seatL3 = this.getLeftSeat(findSeat, 3);
        final SeatInfo seatR3 = this.getRightSeat(findSeat, 3);

        if (this.isIsland(seatL1, seatR2) && this.canSelect(seatR1)) // 孤岛2个座位，选中左边的座位，即findSeat和R1
        {
            // 如果孤岛的两边(即L1和R2)都已经被自己选，那么会把另一个也选上
            if (this.isSelectByMyselfInNormalMode(seatL1) && this.isSelectByMyselfInNormalMode(seatR2)) {
                if (this.selectedSeatList.size() + 2 > this.limitCount) {
                    return RESULT_ONE_LEFT;
                }

                this.bindSeats(findSeat, seatR1);
                return RESULT_OK;
            }
            // 如果孤岛的两边(即L1和R2)，R2被自己选，L1不可选，那么会把另一个也选上
            else if (!this.canSelect(seatL1) && this.isSelectByMyselfInNormalMode(seatR2)) {
                if (this.selectedSeatList.size() + 2 > this.limitCount) {
                    return RESULT_ONE_LEFT;
                }

                this.bindSeats(findSeat, seatR1);
                return RESULT_OK;
            }
            // 如果孤岛的两边(即L1和R2)，L1被自己选，R2不可选，L3可选 那么会把另一个也选上
            else if (!this.canSelect(seatR2) && this.isSelectByMyselfInNormalMode(seatL1) && this.canSelect(seatL3)) {
                if (this.selectedSeatList.size() + 2 > this.limitCount) {
                    return RESULT_ONE_LEFT;
                }

                this.bindSeats(findSeat, seatR1);
                return RESULT_OK;
            }
            // 孤岛2个座位，其他情形，那么仅仅是单选
            else {
                this.selectSeat(findSeat);
                return RESULT_OK;
            }
        } else if (this.isIsland(seatL2, seatR1) && this.canSelect(seatL1)) // 孤岛2个座位，选中右边的座位，即L1和findSeat
        {
            // 如果孤岛的两边(即L2和R1)都已经被自己选，那么会把另一个也选上
            if (this.isSelectByMyselfInNormalMode(seatL2) && this.isSelectByMyselfInNormalMode(seatR1)) {
                if (this.selectedSeatList.size() + 2 > this.limitCount) {
                    return RESULT_ONE_LEFT;
                }

                this.bindSeats(findSeat, seatL1);
                return RESULT_OK;
            }
            // 如果孤岛的两边(即L2和R1)，L2被自己选，R1不可选，那么会把另一个也选上
            else if (this.isSelectByMyselfInNormalMode(seatL2) && !this.canSelect(seatR1)) {
                if (this.selectedSeatList.size() + 2 > this.limitCount) {
                    return RESULT_ONE_LEFT;
                }

                this.bindSeats(findSeat, seatL1);
                return RESULT_OK;
            }
            // 如果孤岛的两边(即L2和R1)，L2被自己选，R1不可选，R3可选 那么会把另一个也选上
            else if (this.isSelectByMyselfInNormalMode(seatR1) && !this.canSelect(seatL2) && this.canSelect(seatR3)) {
                if (this.selectedSeatList.size() + 2 > this.limitCount) {
                    return RESULT_ONE_LEFT;
                }

                this.bindSeats(findSeat, seatL1);
                return RESULT_OK;
            }
            // 孤岛2个座位，其他情形，那么仅仅是单选
            else {
                this.selectSeat(findSeat);
                return RESULT_OK;
            }
        }

        return RESULT_NONE;
    }

    // 选座，处理孤岛1个座位
    int handle1IslandWhenSelectSeat(final SeatInfo findSeat) {
        final SeatInfo seatL1 = this.getLeftSeat(findSeat, 1);
        final SeatInfo seatR1 = this.getRightSeat(findSeat, 1);

        if (this.isIsland(seatL1, seatR1)) {
            // 孤岛1个座位，仅仅单选当前的，孤岛1个座位的定义：左1右1都不可选
            this.selectSeat(findSeat);
            return RESULT_OK;
        }

        return RESULT_NONE;
    }

    private final int[] arrWeight = {0, 0}; // 用权重来标记"同生共死连坐对"

    // 组装"同生共死连坐对"
    void bindSeats(final SeatInfo seat1, final SeatInfo seat2) {
        //情侣座的时候不应该提示“留下了单个空座，已帮您自动关联”
        if (seatSelectedInterface != null && seat1.getType() != TYPE_LOVERLEFT && seat1.getType() != TYPE_LOVERRIGHT) {
            seatSelectedInterface.onBind();
        }
        int count = 0;
        if (seat1.getStatus() != STATUS_SELECTED_BY_SELF) {
            this.selectSeat(seat1);
            count++;
        }
        if (seat2.getStatus() != STATUS_SELECTED_BY_SELF) {
            this.selectSeat(seat2);
            count++;
        }

        if (count == 2) {
            final int weight = (seat1.getX() + seat2.getX()) + seat1.getY() * 1000;
            if (this.arrWeight[0] == 0) {
                this.arrWeight[0] = weight;
            } else {
                this.arrWeight[1] = weight;
            }
        }
    }

    // 取消"同生共死连坐对"
    void unbindSeats(final SeatInfo seat1, final SeatInfo seat2) {
        this.deselectSeat(seat1);
        this.deselectSeat(seat2);

        final int weight = (seat1.getX() + seat2.getX()) + seat1.getY() * 1000;
        if (this.arrWeight[0] == weight) {
            this.arrWeight[0] = 0;
        } else {
            this.arrWeight[1] = 0;
        }
    }

    // 选一个座位
    //之所以改成public 是因为自动选座，尽量不用这个，因为没有孤岛算法在里面
    public void selectSeat(final SeatInfo seat) {
        LogWriter.e("mylog", "seatManager-selectSeat");
        this.selectedSeatList.add(seat);
        seat.setStatus(STATUS_SELECTED_BY_SELF);
        if (seatSelectNetNotUsedList != null && seatSelectNetNotUsedList.size() > 0) {
            LogWriter.e("mylog", "selectSeat--seatSelectNetNotUsedList.size():" + seatSelectNetNotUsedList.size());
            seat.setSelectImage(seatSelectNetNotUsedList.get(0));
            seatSelectNetUsedList.add(seatSelectNetNotUsedList.get(0));
            seatSelectNetNotUsedList.remove(0);
        }
    }

    // 取消一个座位
    //之所以改成public 是因为自动选座，尽量不用这个，因为没有孤岛算法在里面
    public void deselectSeat(final SeatInfo seat) {
        LogWriter.e("mylog", "deselectSeat begin, selectedSeatList.size():" + selectedSeatList.size());
        this.selectedSeatList.remove(seat);
        LogWriter.e("mylog", "deselectSeat, selectedSeatList.size():" + selectedSeatList.size());
        seat.setStatus(STATUS_CAN_SELECT);

        if (seat.getSelectImage() != null && seatSelectNetNotUsedList != null && seatSelectNetUsedList != null) {
            seatSelectNetNotUsedList.add(0, seat.getSelectImage());
            seatSelectNetUsedList.remove(seat.getSelectImage());
            seat.setSelectImage(null);
        }
    }

    // 判断是否为孤岛
    boolean isIsland(final SeatInfo seatL, final SeatInfo seatR) {
        return (!this.canSelect(seatL) || this.isSelectByMyselfInNormalMode(seatL))
                && (!this.canSelect(seatR) || this.isSelectByMyselfInNormalMode(seatR));
    }

    // 判断一个普通座位是否被自己选
    boolean isSelectByMyselfInNormalMode(final SeatInfo seatInfo) {
        return seatInfo != null && seatInfo.getStatus() == STATUS_SELECTED_BY_SELF && seatInfo.getType() != TYPE_LOVERLEFT
                && seatInfo.getType() != TYPE_LOVERRIGHT;
    }

    // 判断一个座位是否可选
    // 对不可选（false）的定义：墙、情侣座、不是座位、被别人选
    boolean canSelect(final SeatInfo seatInfo) {
        // 墙
        if (seatInfo == null) {
            return false;
        }

        // 情侣座
        if (seatInfo.getType() == TYPE_LOVERLEFT || seatInfo.getType() == TYPE_LOVERRIGHT) {
            return false;
        } else return seatInfo.getStatus() != STATUS_SELECTED_BY_OTHERS && seatInfo.getStatus() != STATUS_NOT_SEAT;
    }

    // / <summary>
    // / 根据坐标获取座位
    // / </summary>
    // / <param name="x"></param>
    // / <param name="y"></param>
    // / <returns></returns>
    public SeatInfo getSeat(final int x, final int y) {
        if (x >= this.columnCount || x < 0 || y >= this.rowCount || y < 0) {
            return null;
        }

        final int curPos = this.getPositionInSeatArray(x, y);
        if (curPos >= this.arrAllSeats.length || curPos < 0) {
            return null;
        }
        final SeatInfo findSeat = this.arrAllSeats[curPos];
        return findSeat;
    }

    // 根据坐标值获取在座位数组中的位置
    int getPositionInSeatArray(final int x, final int y) {
        return this.columnCount * y + x;
    }

    // / <summary>
    // / 取当前座位的左边位置
    // / </summary>
    // / <param name="seat">原点</param>
    // / <param name="step">向左走的步数，大于0</param>
    // / <returns></returns>
    SeatInfo getLeftSeat(final SeatInfo seat, final int step) {
        return this.getSeat(seat.getX() - step, seat.getY());
    }

    // / <summary>
    // / 取当前座位的右边位置
    // / </summary>
    // / <param name="seat">原点</param>
    // / <param name="step">向右走的步数，大于0</param>
    // / <returns></returns>
    SeatInfo getRightSeat(final SeatInfo seat, final int step) {
        return this.getSeat(seat.getX() + step, seat.getY());
    }


    private List<ImagePiece> seatSelectNetNotUsedList;
    private List<ImagePiece> seatSelectNetUsedList;

    public void setNetSeatIcons(List<ImagePiece> seatIconNet) {
        //将图片9等分，前4份为普通已选，5为普通已售，6+7 为 情侣已选  8+9 为情侣已售
        List<ImagePiece> seatIconSelect = new ArrayList<ImagePiece>();
        for (int i = 0; i < 4; i++) {
            seatIconSelect.add(seatIconNet.get(i));
        }
        this.seatSelectNetNotUsedList = seatIconSelect;
        this.seatSelectNetUsedList = new ArrayList<ImagePiece>();

    }

    private ISeatSelectInterface seatSelectedInterface;

    public void setSeatInterface(ISeatSelectInterface seatInterface) {
        this.seatSelectedInterface = seatInterface;
    }
}