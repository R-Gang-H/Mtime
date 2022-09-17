package com.mtime.bussiness.ticket.movie.bean;

import com.mtime.base.bean.MBaseBean;

import java.io.Serializable;
import java.util.List;

public class PayItemBean extends MBaseBean implements Serializable {

    private static final long serialVersionUID = 516237708041254691L;

    // 状态码，0 错误、1成功、2超出支付金额
    private int statusCode;
    private String msg;
    private double balance;
    private double totalAmount;
    private int orderStatus;
    private int deductedAmount;
    private double serviceFee;
    private double needPayAmount;
    private boolean isAddVoucher;
    private long countDown;
    private boolean isUseBalance;
    private double cardNum;
    private double cardAmount;
    private double voucherAmount;
    private double couponActivityAmount;
    private List<String> subsidyMsgList;
    private List<CardListBean> cardList;
    private List<Voucher> voucherList;
    private List<CouponActivityListItem> couponActivityList;
    private List<ThirdPayListItem> thirdPayList;

    private boolean isDisplay = true;//false 显示中国银行去支付按钮
    private String activityDescription;//false 显示中国银行去支付按钮
    private boolean isGoto;//true 跳到银联或者其他第三方
    private String activitiesNote;
    private String paymentVerification;
    private String bankName;
    private int payType;
    private String paymentNumber;
    private String appVersionMsg;
    private String activityMsg;
    private boolean isPromotionCount;
    private boolean isUserLimitMAX;

    
    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getMsg() {
        if (msg == null) {
            return "";
        } else {
            return msg;
        }
    }
    
    public void setMsg(String msg) {
        this.msg = msg;
    }
    
    public double getBalance() {
        return balance;
    }
    
    public void setBalance(double balance) {
        this.balance = balance;
    }
    
    public double getTotalAmount() {
        return totalAmount;
    }
    
    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }
    
    public int getOrderStatus() {
        return orderStatus;
    }
    
    public void setOrderStatus(int orderStatus) {
        this.orderStatus = orderStatus;
    }
    
    public int getDeductedAmount() {
        return deductedAmount;
    }
    
    public void setDeductedAmount(int deductedAmount) {
        this.deductedAmount = deductedAmount;
    }
    
    public double getServiceFee() {
        return serviceFee;
    }
    
    public void setServiceFee(double serviceFee) {
        this.serviceFee = serviceFee;
    }
    
    public double getNeedPayAmount() {
        return needPayAmount;
    }
    
    public void setNeedPayAmount(double needPayAmount) {
        this.needPayAmount = needPayAmount;
    }
    
    public boolean getIsAddVoucher() {
        return isAddVoucher;
    }
    
    public void setIsAddVoucher(boolean isAddVoucher) {
        this.isAddVoucher = isAddVoucher;
    }
    
    public long getCountDown() {
        return countDown;
    }
    
    public void setCountDown(long countDown) {
        this.countDown = countDown;
    }
    
    public boolean getIsUseBalance() {
        return isUseBalance;
    }
    
    public void setIsUseBalance(boolean isUseBalance) {
        this.isUseBalance = isUseBalance;
    }
    
    public double getCardNum() {
        return cardNum;
    }
    
    public void setCardNum(double cardNum) {
        this.cardNum = cardNum;
    }
    
    public double getCardAmount() {
        return cardAmount;
    }
    
    public void setCardAmount(double cardAmount) {
        this.cardAmount = cardAmount;
    }
    
    public double getVoucherAmount() {
        return voucherAmount;
    }
    
    public void setVoucherAmount(double voucherAmount) {
        this.voucherAmount = voucherAmount;
    }
    
    public double getCouponActivityAmount() {
        return couponActivityAmount;
    }
    
    public void setCouponActivityAmount(double couponActivityAmount) {
        this.couponActivityAmount = couponActivityAmount;
    }
    
    public List<CardListBean> getCardList() {
        return cardList;
    }
    
    public void setCardList(List<CardListBean> cardList) {
        this.cardList = cardList;
    }
    
    public List<Voucher> getVoucherList() {
        return voucherList;
    }
    
    public void setVoucherList(List<Voucher> voucherList) {
        this.voucherList = voucherList;
    }
    
    public List<CouponActivityListItem> getCouponActivityList() {
        return couponActivityList;
    }
    
    public void setCouponActivityList(List<CouponActivityListItem> couponActivityList) {
        this.couponActivityList = couponActivityList;
    }
    
    public List<ThirdPayListItem> getThirdPayList() {
        return thirdPayList;
    }
    
    public void setThirdPayList(List<ThirdPayListItem> thirdPayList) {
        this.thirdPayList = thirdPayList;
    }

    public List<String> getSubsidyMsgList() {
        return subsidyMsgList;
    }

    public void setSubsidyMsgList(List<String> subsidyMsgList) {
        this.subsidyMsgList = subsidyMsgList;
    }

    public boolean isDisplay() {
        return isDisplay;
    }

    public void setIsDisplay(boolean isDisplay) {
        this.isDisplay = isDisplay;
    }

    public String getActivityDescription() {
        if (activityDescription == null){
            return "";
        }
        return activityDescription;
    }

    public void setActivityDescription(String activityDescription) {
        this.activityDescription = activityDescription;
    }

    public boolean isGoto() {
        return isGoto;
    }

    public void setIsGoto(boolean isGoto) {
        this.isGoto = isGoto;
    }

    public String getActivitiesNote() {
        if (activitiesNote == null){
            return "";
        }
        return activitiesNote;
    }

    public void setActivitiesNote(String activitiesNote) {
        this.activitiesNote = activitiesNote;
    }

    public String getPaymentVerification() {
        if (paymentVerification == null){
            return "";
        }
        return paymentVerification;
    }

    public void setPaymentVerification(String paymentVerification) {
        this.paymentVerification = paymentVerification;
    }

    public String getBankName() {
        if (bankName == null){
            return "";
        }
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public int getPayType() {
        return payType;
    }

    public void setPayType(int payType) {
        this.payType = payType;
    }

    public String getPaymentNumber() {
        if (paymentNumber == null){
            return "";
        }
        return paymentNumber;
    }

    public void setPaymentNumber(String paymentNumber) {
        this.paymentNumber = paymentNumber;
    }

    public String getAppVersionMsg() {
        return appVersionMsg;
    }

    public void setAppVersionMsg(String appVersionMsg) {
        this.appVersionMsg = appVersionMsg;
    }

    public String getActivityMsg() {
        return activityMsg;
    }

    public void setActivityMsg(String activityMsg) {
        this.activityMsg = activityMsg;
    }

    public boolean isPromotionCount() {
        return isPromotionCount;
    }

    public void setIsPromotionCount(boolean isPromotionCount) {
        this.isPromotionCount = isPromotionCount;
    }

    public boolean isUserLimitMAX() {
        return isUserLimitMAX;
    }

    public void setIsUserLimitMAX(boolean isUserLimitMAX) {
        this.isUserLimitMAX = isUserLimitMAX;
    }
}
