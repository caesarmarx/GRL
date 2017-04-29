package grl.com.dataModels;

import android.graphics.Bitmap;

import java.util.Date;

public class MessageModel {
    private long sqlId;
    private String msgId;
    private boolean msgFromMe;
    private String msgFromUserId;
    private String msgToUserId;
    private String groupId;
    private String msgType;
    private String groupType;
    private String msgText;
    private Bitmap thumbnail;
    private String thumbnailUrl;
    private String filePath;
    private String msgStatus;
    private String userName;         // User Name
    private String photo;           // User Photo
    private int progress;
    private float soundLength;
    private Date sendDate;

    private Boolean bRead;

    public MessageModel() {
        sqlId = 0;
        msgId = "";
        msgFromMe = true;
        msgFromUserId = "";
        msgToUserId = "";
        groupId = "";
        msgType = "";
        groupType = "";
        msgText = "";
        thumbnail = null;
        thumbnailUrl = "";
        filePath = "";
        msgStatus = "";
        photo = "";
        progress = 0;
    }

    public long getSqlId() {
        return sqlId;
    }

    public void setSqlId(long sqlId) {
        this.sqlId = sqlId;
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public boolean isMsgFromMe() {
        return msgFromMe;
    }

    public void setMsgFromMe(boolean msgFromMe) {
        this.msgFromMe = msgFromMe;
    }

    public String getMsgType() {
        return msgType;
    }
    public void setMsgType(String msgType) {
        this.msgType = msgType;
    }

    public String getMsgText() {
        return msgText;
    }
    public void setMsgText(String msgText) {
        this.msgText = msgText;
    }


    public Bitmap getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(Bitmap thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getMsgStatus() {
        return msgStatus;
    }

    public void setMsgStatus(String msgStatus) {
        this.msgStatus = msgStatus;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public String getMsgToUserId() {
        return msgToUserId;
    }

    public void setMsgToUserId(String msgToUserId) {
        this.msgToUserId = msgToUserId;
    }

    public String getGroupType() {
        return groupType;
    }

    public void setGroupType(String groupType) {
        this.groupType = groupType;
    }

    public float getSoundLength() {
        return soundLength;
    }

    public void setSoundLength(float soundLength) {
        this.soundLength = soundLength;
    }

    public Date getSendDate() {
        return sendDate;
    }

    public void setSendDate(Date sendDate) {
        this.sendDate = sendDate;
    }

    public Boolean getbRead() {
        return bRead;
    }

    public void setbRead(Boolean bRead) {
        this.bRead = bRead;
    }

    public String getMsgFromUserId() {
        return msgFromUserId;

    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public void setMsgFromUserId(String msgFromUserId) {
        this.msgFromUserId = msgFromUserId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }
}