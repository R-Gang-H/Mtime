package com.mtime.bussiness.mine.bean;


import com.mtime.base.bean.MBaseBean;

public class VoucherList extends MBaseBean {
	private int id;
	private String name;
	private String description;
	private long startTime;
	private long endTime;
	private int movieId;
	private String status;
	private int orderId;
	private long useTime;
	private String movieImg;
	private boolean outDate;
	public boolean isOutDate()
    {
        return outDate;
    }

    public void setOutDate(boolean outDate)
    {
        this.outDate = outDate;
    }

    public String getMovieImg()
    {
        return movieImg;
    }

    public void setMovieImg(String movieImg)
    {
        this.movieImg = movieImg;
    }

    public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
    if (name==null)
    {
        return "";
    }
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
        if (description == null)
        {
            return "";
        }
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public long getStartTime() {
		return startTime;
	}

	public void setStartTime(int startTime) {
		this.startTime = startTime;
	}

	public long getEndTime() {
		return endTime;
	}
    public long getEndTimeMillins() {
        return endTime;
    }
	public void setEndTime(int endTime) {
		this.endTime = endTime;
	}

	public int getMovieId() {
		return movieId;
	}

	public void setMovieId(int movieId) {
		this.movieId = movieId;
	}

	public String getStatus() {
	    if (status==null)
	    {
	        return "";
	    }
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public int getOrderId() {
		return orderId;
	}

	public void setOrderId(int orderId) {
		this.orderId = orderId;
	}

	public long getUseTime() {
		return useTime;
	}

	public void setUseTime(long useTime) {
		this.useTime = useTime;
	}
}
