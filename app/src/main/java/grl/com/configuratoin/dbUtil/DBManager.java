package grl.com.configuratoin.dbUtil;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import grl.com.configuratoin.Constant;
import grl.com.configuratoin.SelfInfoModel;
import grl.com.dataModels.MessageModel;

public class DBManager {

    public static MessageModel insertMessage(MessageModel model) {
        model.setSendDate(new Date());
        Message message = new Message(model);
        message.save();
        model.setSqlId(message.getId());

        String toUserId = "";
        if (model.isMsgFromMe())
            toUserId = model.getMsgToUserId();
        else
            toUserId = model.getMsgFromUserId();

        if (model.getGroupType().compareTo(Constant.CHAT_CONSULT) == 0) {
            if (model.getbRead())
                inserUser(SelfInfoModel.userID, toUserId, 0);
            else
                inserUser(SelfInfoModel.userID, toUserId, 1);
        }

        return model;
    }

    public static ArrayList<MessageModel> getMessageList(String query,  String... whereArgs) {
        List<Message> list = Message.find(Message.class, query, whereArgs);
        ArrayList<MessageModel> result = new ArrayList<MessageModel>();
        for (int i = 0; i < list.size(); i++) {
            Message message = list.get(i);
            MessageModel model = parseFromDb(message);
            result.add(model);
        }
        return result;
    }

    public static MessageModel getLastMessage(String fromUserId, String toUserId) {
        String[] args = {Constant.CHAT_CONSULT, fromUserId, toUserId, toUserId, fromUserId};
        List<Message> list = Message.find(Message.class, "GROUP_TYPE = ? and ((MSG_FROM_USER_ID = ? and MSG_TO_USER_ID = ?) or (MSG_FROM_USER_ID = ? and MSG_TO_USER_ID = ?))",
                args, "", "SEND_DATE DESC", "");
        if (list.size() == 0) return null;
        Message message = list.get(0);
        MessageModel model = parseFromDb(message);
        return model;
    }

    public static MessageModel getMessage(long sqlId) {
        Message message = Message.findById(Message.class, sqlId);
        if (message == null)
            return null;
        return parseFromDb(message);
    }

    public static void updateMessageState(String messageStatus, long sqlId) {
        Message message = Message.findById(Message.class, sqlId);
        message.setMsgStatus(messageStatus);
        message.save();
    }
    public static void updateMessageFilePath(String filePath, long sqlId) {
        Message message = Message.findById(Message.class, sqlId);
        message.setFilePath(filePath);
        message.save();
    }
    public static void updateMessageThumbPath(String filePath, long sqlId) {
        Message message = Message.findById(Message.class, sqlId);
        message.setThumbnailUrl(filePath);
        message.save();
    }

    public static void updateMessageProgress(float progress, long sqlId) {
        Message message = Message.findById(Message.class, sqlId);
        message.setProgress(Float.valueOf(progress));
        message.save();
    }

    public static void removeMessageById(long sqlId) {
        Message message = Message.findById(Message.class, sqlId);
        message.delete();
    }

    public static void removeMessageByGroupId(String groupType, String groupId) {
        Message.deleteAll(Message.class, "GROUP_TYPE = ? AND GROUP_ID = ?", groupType, groupId);
    }

    public static void removeMessageByMsgId(String msgId) {
        Message.deleteAll(Message.class, "MSG_ID = ?", msgId);
    }

    public static void markAsRead(String userId) {
        String[] args = {Constant.CHAT_CONSULT, SelfInfoModel.userID, userId, userId, SelfInfoModel.userID};
        List<Message> list = Message.find(Message.class, "GROUP_TYPE = ? and ((MSG_FROM_USER_ID = ? and MSG_TO_USER_ID = ?) or (MSG_FROM_USER_ID = ? and MSG_TO_USER_ID = ?))",
                args, "", "SEND_DATE DESC", "");
        if (list.size() == 0) return;
        for (int i = 0; i < list.size(); i ++) {
            Message message = list.get(i);
            message.setbRead(1);
            message.save();
        }
        MsgUser user = getUser(SelfInfoModel.userID, userId);
        if (user == null)
            return;
        user.setUnread(0);
        user.save();
    }

    public static void updateUnread(boolean read, long sqlId) {
        Message message = Message.findById(Message.class, sqlId);
        if (read)
            message.setbRead(1);
        else
            message.setbRead(0);
        message.save();
    }

    //  User DB
    public static void inserUser(String fromUserId, String toUserId, int unread) {
        MsgUser oldUser = checkUserLog(fromUserId, toUserId);
        if (oldUser != null) {
            Integer oldUnread = oldUser.getUnread();
            oldUser.setUnread(oldUnread + unread);
            oldUser.save();
            return;
        }
        MsgUser user = new MsgUser();
        user.setFromUserId(fromUserId);
        user.setToUserId(toUserId);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String strDate = formatter.format(new Date(new Date().getTime()));
        user.setDate(strDate);
        user.setJudge(-1);
        user.setUnread(unread);
        user.save();
    }

    public static List<MsgUser> getUserList(String fromUserId, String date) {
        String[] args = {fromUserId, date};
        List<MsgUser> userList = MsgUser.find(MsgUser.class, "FROM_USER_ID = ? and DATE like ?", args, "", "DATE DESC", "");
        return userList;
    }

    public static MsgUser getUser(String fromUserId, String toUserId) {
        List<MsgUser> userList = MsgUser.find(MsgUser.class, "FROM_USER_ID = ? and TO_USER_ID = ?", fromUserId, toUserId);
        if (userList.size() == 0) return null;
        return userList.get(0);
    }

    public static List<MsgUser> getAllUsers(String fromUserId) {
        List<MsgUser> userList = MsgUser.find(MsgUser.class, "FROM_USER_ID = ?", fromUserId);
        return userList;
    }

    public static List<MsgUser> getTodayUsers(String fromUserId) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String strDate = formatter.format(new Date(new Date().getTime()));
        return getUserList(fromUserId, strDate);
    }

    public static int getUnreadMsgTotalCount(String userId) {
        List<MsgUser> userList = getTodayUsers(userId);
        int total = 0;
        for (int i = 0; i < userList.size(); i++) {
            MsgUser user = userList.get(i);
            if (user.getUnread() != null) {
                total += user.getUnread();
            }
        }
        return total;
    }

    public static void updateUser(String fromUserId, String toUserId, int unread) {
        List<MsgUser> userList = MsgUser.find(MsgUser.class, "FROM_USER_ID = ? and TO_USER_ID", fromUserId, toUserId);

        for (int i = 0; i < userList.size(); i++) {
            MsgUser user = userList.get(i);
            user.setUnread(unread);
            user.save();
        }
    }

    public static int getTimeRecord(String fromUserId, String toUserId) {
        MsgUser user = checkUserLog(fromUserId, toUserId);
        if (user == null) return 0;
        return user.getTime();
    }

    public static void updateTimeRecord(String fromUserId, String toUserId, int time) {
        if (time < 0) time = 0;
        MsgUser user = checkUserLog(fromUserId, toUserId);
        if (user == null) return;
        user.setTime(user.getTime() + time);
        user.save();
    }

    public static void clearUserHistory() {
        MsgUser.deleteAll(MsgUser.class);
    }

    public static void removeUserHistory(String fromUserId, String toUserId) {

        List<MsgUser> list = MsgUser.find(MsgUser.class, "FROM_USER_ID = ? and TO_USER_ID = ?", fromUserId, toUserId);
        for (int i = 0; i < list.size(); i ++) {
            MsgUser user = list.get(i);
            user.delete();
        }

        Message.deleteAll(Message.class, "GROUP_TYPE = ? and ((MSG_FROM_USER_ID = ? and MSG_TO_USER_ID = ?) or (MSG_FROM_USER_ID = ? and MSG_TO_USER_ID = ?))",
                Constant.CHAT_CONSULT, fromUserId, toUserId, toUserId, fromUserId);
    }

    public static MsgUser checkUserLog(String fromUserId, String toUserId) {
        String []args = {fromUserId, toUserId};
        List<MsgUser> list = MsgUser.find(MsgUser.class, "FROM_USER_ID = ? and TO_USER_ID = ?", args);
        if (list.size() == 0)
            return null;
        return list.get(0);
    }


    /**
     *  Favourite Message
     */
    public static MessageModel insertFavouriteMsg(MessageModel model) {
        MsgFavourite message = new MsgFavourite(model);
        message.save();
        model.setSqlId(message.getId());

        String toUserId = "";
        if (model.isMsgFromMe())
            toUserId = model.getMsgToUserId();
        else
            toUserId = model.getMsgFromUserId();

        if (model.getbRead())
            inserUser(SelfInfoModel.userID, toUserId, 0);
        else
            inserUser(SelfInfoModel.userID, toUserId, 1);

        return model;
    }

    public static ArrayList<MessageModel> getFavouriteMsgList() {
        List<MsgFavourite> list = MsgFavourite.listAll(MsgFavourite.class);
        ArrayList<MessageModel> result = new ArrayList<MessageModel>();
        for (int i = 0; i < list.size(); i++) {
            MsgFavourite message = list.get(i);
            MessageModel model = parseFromDb(message);
            result.add(model);
        }
        return result;
    }

    public static void removeAll() {
        MsgFavourite.deleteAll(MsgFavourite.class);
    }

    public static void removeFavouriteMsg(long id) {
        MsgFavourite message = MsgFavourite.findById(MsgFavourite.class, id);
        message.delete();
    }
    /**
     *  Message Parse
     */
    public static MessageModel parseFromDb(Message message) {
        MessageModel model = new MessageModel();
        model.setSqlId(message.getId());
        model.setMsgId(message.getMsgId());
        if (message.getMsgFromMe() > 0)
            model.setMsgFromMe(true);
        else
            model.setMsgFromMe(false);
        model.setGroupId(message.getGroupId());
        model.setMsgFromUserId(message.getMsgFromUserId());
        model.setMsgToUserId(message.getMsgToUserId());
        model.setMsgType(message.getMsgType());
        model.setGroupType(message.getGroupType());
        model.setMsgText(message.getMsgText());
        model.setThumbnailUrl(message.getThumbnailUrl());
        model.setFilePath(message.getFilePath());
        model.setMsgStatus(message.getMsgStatus());
        model.setPhoto(message.getPhoto());
        model.setProgress(message.getProgress().intValue());
        model.setSoundLength(message.getSoundLength().floatValue());
        model.setSendDate(new Date(message.getSendDate()));
        if (message.getbRead() > 0)
            model.setbRead(true);
        else
            model.setbRead(false);
        return model;
    }
    public static MessageModel parseFromDb(MsgFavourite message) {
        MessageModel model = new MessageModel();
        model.setSqlId(message.getId());
        model.setMsgId(message.getMsgId());
        if (message.getMsgFromMe() > 0)
            model.setMsgFromMe(true);
        else
            model.setMsgFromMe(false);
        model.setMsgFromUserId(message.getMsgFromUserId());
        model.setMsgToUserId(message.getMsgToUserId());
        model.setMsgType(message.getMsgType());
        model.setGroupType(message.getGroupType());
        model.setMsgText(message.getMsgText());
        model.setThumbnailUrl(message.getThumbnailUrl());
        model.setFilePath(message.getFilePath());
        model.setMsgStatus(message.getMsgStatus());
        model.setPhoto(message.getPhoto());
        model.setSoundLength(message.getSoundLength().floatValue());
        model.setSendDate(new Date(message.getSendDate()));
        return model;
    }


    public static AliasDes getAliaDes(String userId) {
        String []args = {userId};
        List<AliasDes> lst = AliasDes.find(AliasDes.class, "USER_ID = ?", args);
        if (lst.size() == 0)
            return null;

        return lst.get(0);
    }
}
