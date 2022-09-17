package com.mtime.beans;

import com.mtime.base.bean.MBaseBean;

/**
 * Created by yinguanping on 17/2/23.
 */

public class URLData extends MBaseBean
{
    private String commandID;
    private long   expires;
    private String netType;
    private String parserBeanName;
    private String parserClassName;
    private String url;

    public String getCommandID()
    {
        return this.commandID;
    }

    public long getExpires()
    {
        return this.expires;
    }

    public String getNetType()
    {
        return this.netType;
    }

    public String getParserBeanName()
    {
        return this.parserBeanName;
    }

    public String getParserClassName()
    {
        return this.parserClassName;
    }

    public String getUrl()
    {
        return this.url;
    }

    public void setCommandID(final String commandID)
    {
        this.commandID = commandID;
    }

    public void setExpires(final long timeout)
    {
        this.expires = timeout;
    }

    public void setNetType(final String netType)
    {
        this.netType = netType;
    }

    public void setParserBeanName(final String parserBeanName)
    {
        this.parserBeanName = parserBeanName;
    }

    public void setParserClassName(final String parserClassName)
    {
        this.parserClassName = parserClassName;
    }

    public void setUrl(final String url)
    {
        this.url = url;
    }

}
