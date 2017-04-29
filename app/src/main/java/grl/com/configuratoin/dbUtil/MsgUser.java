package grl.com.configuratoin.dbUtil;


import com.orm.SugarRecord;

import grl.com.dataModels.MessageModel;

/**
 * Created by macdev001 on 6/30/16.
 */

public class MsgUser extends SugarRecord {
    private String msgId;
    private String fromUserId;
    private String toUserId;
    private String  date;
    private Integer time;
    private Integer judge;
    private Integer unread;

    public MsgUser() {
        msgId = "";
        fromUserId = "";
        toUserId = "";
        date = "";
        time = Integer.valueOf(0);
        judge = Integer.valueOf(0);
        unread = Integer.valueOf(0);
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public String getFromUserId() {
        return fromUserId;
    }

    public void setFromUserId(String fromUserId) {
        this.fromUserId = fromUserId;
    }

    public String getToUserId() {
        return toUserId;
    }

    public void setToUserId(String toUserId) {
        this.toUserId = toUserId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Integer getTime() {
        return time;
    }

    public void setTime(Integer time) {
        this.time = time;
    }

    public Integer getJudge() {
        return judge;
    }

    public void setJudge(Integer judge) {
        this.judge = judge;
    }

    public Integer getUnread() {
        return unread;
    }

    public void setUnread(Integer unread) {
        this.unread = unread;
    }
}


