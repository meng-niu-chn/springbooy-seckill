package cn.iguxue.seckill.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;

import java.util.Date;

public class SuccessKilled {

    @TableId
    private long successKilledId;

    private long userPhone;

    private short state;

    private Date createTime;

    @TableField(exist = false)
    private Seckill seckill;

    public long getSuccessKilledId() {
        return successKilledId;
    }

    public void setSuccessKilledId(long successKilledId) {
        this.successKilledId = successKilledId;
    }

    public long getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(long userPhone) {
        this.userPhone = userPhone;
    }

    public short getState() {
        return state;
    }

    public void setState(short state) {
        this.state = state;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "SuccessKilled{" +
                "successKilledId=" + successKilledId +
                ", userPhone=" + userPhone +
                ", state=" + state +
                ", createTime=" + createTime +
                ", seckill=" + seckill +
                '}';
    }
}
