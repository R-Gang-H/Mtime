// JSON Java Class Generator
// Written by Bruce Bao
// Used for API: 
package com.mtime.bussiness.mine.bean;

import com.mtime.base.bean.MBaseBean;

public class MemberList  extends MBaseBean
{
    private int    membershipCardId;
    private String cardNumber;
    private String memberName;
    private int    validDate;
    private String typeName;
    private int    status;
    private String memberDetailUrl;
    private String topImage;
    private String bottomImage;
    private int    cinemaId;
    private String cinemaImage;
    private String cinemaName;
    private String Address;
    private String typeImage;

    public String getTypeImage()
    {
        return typeImage;
    }

    public void setTypeImage(String typeImage)
    {
        this.typeImage = typeImage;
    }

    public int getMembershipCardId()
    {
        return membershipCardId;
    }

    public void setMembershipCardId(int membershipCardId)
    {
        this.membershipCardId = membershipCardId;
    }

    public String getCardNumber()
    {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber)
    {
        this.cardNumber = cardNumber;
    }

    public String getMemberName()
    {
        return memberName;
    }

    public void setMemberName(String memberName)
    {
        this.memberName = memberName;
    }

    public int getValidDate()
    {
        return validDate;
    }

    public void setValidDate(int validDate)
    {
        this.validDate = validDate;
    }

    public String getTypeName()
    {
        return typeName;
    }

    public void setTypeName(String typeName)
    {
        this.typeName = typeName;
    }

    public int getStatus()
    {
        return status;
    }

    public void setStatus(int status)
    {
        this.status = status;
    }

    public String getMemberDetailUrl()
    {
        return memberDetailUrl;
    }

    public void setMemberDetailUrl(String memberDetailUrl)
    {
        this.memberDetailUrl = memberDetailUrl;
    }

    public String getTopImage()
    {
        return topImage;
    }

    public void setTopImage(String topImage)
    {
        this.topImage = topImage;
    }

    public String getBottomImage()
    {
        return bottomImage;
    }

    public void setBottomImage(String bottomImage)
    {
        this.bottomImage = bottomImage;
    }

    public int getCinemaId()
    {
        return cinemaId;
    }

    public void setCinemaId(int cinemaId)
    {
        this.cinemaId = cinemaId;
    }

    public String getCinemaImage()
    {
        return cinemaImage;
    }

    public void setCinemaImage(String cinemaImage)
    {
        this.cinemaImage = cinemaImage;
    }

    public String getCinemaName()
    {
        return cinemaName;
    }

    public void setCinemaName(String cinemaName)
    {
        this.cinemaName = cinemaName;
    }

    public String getAddress()
    {
        return Address;
    }

    public void setAddress(String Address)
    {
        this.Address = Address;
    }
}
