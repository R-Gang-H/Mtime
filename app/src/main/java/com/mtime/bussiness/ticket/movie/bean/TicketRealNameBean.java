package com.mtime.bussiness.ticket.movie.bean;

import com.helen.obfuscator.IObfuscateKeepAll;

/**
 * @author vivian.wei
 * @date 2020/7/28
 * @desc 实名预约购票用户信息Bean
 */
public class TicketRealNameBean implements IObfuscateKeepAll {

    private String idType;      // 证件类型：1 身份证、2 台胞证、3 护照、4 其他有效证件（默认显示身份证）
    private String idTypeName;  // 身份证
    private String idNum;       // 证件号
    private String realName;    // 姓名

    public String getIdType() {
        return idType;
    }

    public void setIdType(String idType) {
        this.idType = idType;
    }

    public String getIdTypeName() {
        return idTypeName;
    }

    public void setIdTypeName(String idTypeName) {
        this.idTypeName = idTypeName;
    }

    public String getIdNum() {
        return idNum;
    }

    public void setIdNum(String idNum) {
        this.idNum = idNum;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }
}
