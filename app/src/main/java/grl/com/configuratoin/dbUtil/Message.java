package grl.com.configuratoin.dbUtil;


import com.orm.SugarRecord;

import grl.com.configuratoin.Constant;
import grl.com.dataModels.MessageModel;

/**
 * Created by macdev001 on 6/30/16.
 */

public class Message extends SugarRecord {
    private String msgId;
    private Integer msgFromMe;
    private String groupId;
    private String msgFromUserId;
    private String msgToUserId;
    private String msgType;
    private String groupType;
    private String msgText;
    private String thumbnailUrl;
    private String filePath;
    private String msgStatus;
    private String photo;
    private Float progress;
    private Float soundLength;
    private Long sendDate;

    private Integer bRead;

    public Message() {

    }

    public Message(MessageModel model) {
        this.msgId = model.getMsgId();
        this.msgFromMe = model.isMsgFromMe() ? 1 : 0;
        this.groupId = model.getGroupId();
        this.msgFromUserId = model.getMsgFromUserId();
        this.msgToUserId = model.getMsgToUserId();
        this.msgType = model.getMsgType();
        this.groupType = model.getGroupType();
        this.msgText = model.getMsgText();
        this.thumbnailUrl = model.getThumbnailUrl();
        this.filePath = model.getFilePath();
        this.msgStatus = model.getMsgStatus();
        this.photo = model.getPhoto();
        this.progress = Float.valueOf(model.getProgress());
        this.soundLength = Float.valueOf(model.getSoundLength());
        this.sendDate = model.getSendDate().getTime();
        this.bRead = model.getbRead() ? 1 : 0;
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public Integer getMsgFromMe() {
        return msgFromMe;
    }

    public void setMsgFromMe(Integer msgFromMe) {
        this.msgFromMe = msgFromMe;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getMsgFromUserId() {
        return msgFromUserId;
    }

    public void setMsgFromUserId(String msgFromUserId) {
        this.msgFromUserId = msgFromUserId;
    }

    public String getMsgToUserId() {
        return msgToUserId;
    }

    public void setMsgToUserId(String msgToUserId) {
        this.msgToUserId = msgToUserId;
    }

    public String getMsgType() {
        return msgType;
    }

    public void setMsgType(String msgType) {
        this.msgType = msgType;
    }

    public String getGroupType() {
        return groupType;
    }

    public void setGroupType(String groupType) {
        this.groupType = groupType;
    }

    public String getMsgText() {
        return msgText;
    }

    public void setMsgText(String msgText) {
        this.msgText = msgText;
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

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public Float getProgress() {
        return progress;
    }

    public void setProgress(Float progress) {
        this.progress = progress;
    }

    public Float getSoundLength() {
        return soundLength;
    }

    public void setSoundLength(Float soundLength) {
        this.soundLength = soundLength;
    }

    public Long getSendDate() {
        return sendDate;
    }

    public void setSendDate(Long sendDate) {
        this.sendDate = sendDate;
    }

    public Integer getbRead() {
        return bRead;
    }

    public void setbRead(Integer bRead) {
        this.bRead = bRead;
    }
}


