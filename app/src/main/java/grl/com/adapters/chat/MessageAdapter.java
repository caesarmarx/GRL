package grl.com.adapters.chat;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.AnimationDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.text.Spannable;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.BufferType;
import android.widget.Toast;

import com.easemob.EMCallBack;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.easemob.chat.EMMessage.ChatType;
import com.easemob.chat.EMMessage.Type;
import com.easemob.chat.FileMessageBody;
import com.easemob.chat.ImageMessageBody;
import com.easemob.chat.LocationMessageBody;
import com.easemob.chat.TextMessageBody;
import com.easemob.chat.VideoMessageBody;
import com.easemob.chat.VoiceMessageBody;
import com.easemob.util.DateUtils;
import com.easemob.util.EMLog;
import com.easemob.util.LatLng;
import com.easemob.util.TextFormater;
import com.koushikdutta.async.http.BasicNameValuePair;

import javax.microedition.khronos.opengles.GL;

import grl.com.activities.consult.ChatActivity;
import grl.com.activities.discovery.profile.VideoPlayActivity;
import grl.com.activities.imageManage.ImagePreviewActivity;
import grl.com.activities.map.MapDetailActivity;
import grl.com.configuratoin.Constant;
import grl.com.configuratoin.GlobalVars;
import grl.com.configuratoin.ImageUtils;
import grl.com.configuratoin.SmileUtils;

import grl.com.configuratoin.Utils;
import grl.com.dataModels.EmoticonModel;
import grl.com.dataModels.MessageModel;
import grl.wangu.com.grl.R;

public class MessageAdapter extends BaseAdapter {

	private final static String TAG = "msg";

	MessageModel message;

	private static final int MESSAGE_TYPE_RECV_TXT = 0;
	private static final int MESSAGE_TYPE_SENT_TXT = 1;
	private static final int MESSAGE_TYPE_SENT_IMAGE = 2;
	private static final int MESSAGE_TYPE_SENT_LOCATION = 3;
	private static final int MESSAGE_TYPE_RECV_LOCATION = 4;
	private static final int MESSAGE_TYPE_RECV_IMAGE = 5;
	private static final int MESSAGE_TYPE_SENT_VOICE = 6;
	private static final int MESSAGE_TYPE_RECV_VOICE = 7;
	private static final int MESSAGE_TYPE_SENT_VIDEO = 8;
	private static final int MESSAGE_TYPE_RECV_VIDEO = 9;
	private static final int MESSAGE_TYPE_SENT_FILE = 10;
	private static final int MESSAGE_TYPE_RECV_FILE = 11;
	private static final int MESSAGE_TYPE_SENT_VOICE_CALL = 12;
	private static final int MESSAGE_TYPE_RECV_VOICE_CALL = 13;
	private static final int MESSAGE_TYPE_SENT_VIDEO_CALL = 14;
	private static final int MESSAGE_TYPE_RECV_VIDEO_CALL = 15;

	public static final String IMAGE_DIR = "chat/image/";
	public static final String VOICE_DIR = "chat/audio/";
	public static final String VIDEO_DIR = "chat/video";

	private String username;
	private LayoutInflater inflater;
	private Activity activity;
	private OnItemLongClickListener mItemLongClickListener;

	// reference to conversation object in chatsdk
	private EMConversation conversation;

	private Context context;

	private Map<String, Timer> timers = new Hashtable<String, Timer>();

	private ArrayList<MessageModel> msgList;
	private String chatType;

	public MessageAdapter(Context context, String username, String type) {
		this.username = username;
		this.context = context;
		chatType = type;
		inflater = LayoutInflater.from(context);
		activity = (Activity) context;
		msgList = new ArrayList<MessageModel>();
	}

	public void setDateSource(ArrayList<MessageModel> list) {
		msgList = list;
	}
	/**
	 * 获取item数
	 */
	public int getCount() {
//		return conversation.getMsgCount();
		return msgList.size();
	}

	/**
	 * 刷新页面
	 */
	public void refresh() {
		notifyDataSetChanged();
	}

	public MessageModel getItem(int position) {
		return msgList.get(position);
	}

	public void removeItem(int position) {
		msgList.remove(position);
	}
	public long getItemId(int position) {
		return position;
	}

	/**
	 * 获取item类型
	 */

	private View createViewByMessage(MessageModel message, int position) {
		if (message.getMsgType().compareTo(Constant.MSG_LOCATION_TYPE) == 0) {
			return message.isMsgFromMe() ? inflater
					.inflate(R.layout.row_sent_location, null) : inflater
					.inflate(R.layout.row_received_location, null);
		}
		if (message.getMsgType().compareTo(Constant.MSG_IMAGE_TYPE) == 0) {
			return message.isMsgFromMe() ? inflater
					.inflate(R.layout.row_sent_picture, null) : inflater
					.inflate(R.layout.row_received_picture, null);
		}
		if (message.getMsgType().compareTo(Constant.MSG_VOICE_TYPE) == 0) {
			return message.isMsgFromMe() ? inflater
					.inflate(R.layout.row_sent_voice, null) : inflater
					.inflate(R.layout.row_received_voice, null);
		}
		if (message.getMsgType().compareTo(Constant.MSG_EMOTICON_TYPE) == 0) {
			return message.isMsgFromMe() ? inflater
					.inflate(R.layout.row_sent_emoticon, null) : inflater
					.inflate(R.layout.row_received_emoticon, null);
		}
		if (message.getMsgType().compareTo(Constant.MSG_VIDEO_TYPE) == 0) {
			return message.isMsgFromMe() ? inflater
					.inflate(R.layout.row_sent_video, null) : inflater
					.inflate(R.layout.row_received_video, null);
		}
		if (message.getMsgType().compareTo(Constant.MSG_FILE_TYPE) == 0) {
			return message.isMsgFromMe() ? inflater
					.inflate(R.layout.row_sent_file, null) : inflater
					.inflate(R.layout.row_received_file, null);
		}
		return message.isMsgFromMe()? inflater
				.inflate(R.layout.row_sent_message, null) : inflater
				.inflate(R.layout.row_received_message, null);
				// 语音通话
//				if (message.getBooleanAttribute(
//						Constant.MESSAGE_ATTR_IS_VOICE_CALL, false))
//					return message.direct == EMMessage.Direct.RECEIVE ? inflater
//							.inflate(R.layout.row_received_voice_call, null)
//							: inflater.inflate(R.layout.row_sent_voice_call, null);
//					// 视频通话
//				else if (message.getBooleanAttribute(
//						Constant.MESSAGE_ATTR_IS_VIDEO_CALL, false))
//					return message.direct == EMMessage.Direct.RECEIVE ? inflater
//							.inflate(R.layout.row_received_video_call, null)
//							: inflater.inflate(R.layout.row_sent_video_call, null);
//				return message.direct == EMMessage.Direct.RECEIVE ? inflater
//						.inflate(R.layout.row_received_message, null) : inflater
//						.inflate(R.layout.row_sent_message, null);
	}

//	public int getViewTypeCount() {
//		return 16;
//	}

	@SuppressLint("NewApi")
	public View getView(final int position, View convertView, ViewGroup parent) {
		MessageModel message = msgList.get(position);
		ViewHolder holder;
		View view = null;
		if (convertView != null && ((ViewHolder) convertView.getTag()).position == position) {
			view = convertView;
			holder = (ViewHolder)convertView.getTag();
		} else {
			holder = new ViewHolder();
			holder.position = position;
			view = createViewByMessage(message, position);
			if (message.getMsgType().compareTo(Constant.MSG_TEXT_TYPE) == 0) {
				try {
					holder.pb = (ProgressBar) view
							.findViewById(R.id.pb_sending);
					holder.staus_iv = (ImageView) view
							.findViewById(R.id.msg_status);
					holder.head_iv = (ImageView) view
							.findViewById(R.id.iv_userhead);
					// 这里是文字内容
					holder.tv = (TextView) view
							.findViewById(R.id.tv_chatcontent);
					holder.tv_userId = (TextView) view
							.findViewById(R.id.tv_userid);
				} catch (Exception e) {
				}
			} else if (message.getMsgType().compareTo(Constant.MSG_IMAGE_TYPE) == 0) {
				try {
					holder.iv = ((ImageView) view
							.findViewById(R.id.iv_sendPicture));
					holder.head_iv = (ImageView) view
							.findViewById(R.id.iv_userhead);
					holder.tv = (TextView) view
							.findViewById(R.id.percentage);
					holder.pb = (ProgressBar) view
							.findViewById(R.id.progressBar);
					holder.staus_iv = (ImageView) view
							.findViewById(R.id.msg_status);
					holder.tv_userId = (TextView) view
							.findViewById(R.id.tv_userid);
				} catch (Exception e) {
				}

			} else if (message.getMsgType().compareTo(Constant.MSG_VOICE_TYPE) == 0) {
				try {
					holder.iv = ((ImageView) view
							.findViewById(R.id.iv_voice));
					holder.head_iv = (ImageView) view
							.findViewById(R.id.iv_userhead);
					holder.lv = (FrameLayout) view
							.findViewById(R.id.layout_voice);
					holder.tv = (TextView) view
							.findViewById(R.id.tv_length);
					holder.pb = (ProgressBar) view
							.findViewById(R.id.pb_sending);
					holder.staus_iv = (ImageView) view
							.findViewById(R.id.msg_status);
					holder.tv_userId = (TextView) view
							.findViewById(R.id.tv_userid);
					holder.iv_read_status = (ImageView) view
							.findViewById(R.id.iv_unread_voice);
				} catch (Exception e) {
				}
			} else if (message.getMsgType().compareTo(Constant.MSG_EMOTICON_TYPE) == 0) {
				try {
					holder.iv = ((ImageView) view
							.findViewById(R.id.iv_sendPicture));
					holder.head_iv = (ImageView) view
							.findViewById(R.id.iv_userhead);
					holder.tv = (TextView) view
							.findViewById(R.id.percentage);
					holder.pb = (ProgressBar) view
							.findViewById(R.id.progressBar);
					holder.staus_iv = (ImageView) view
							.findViewById(R.id.msg_status);
					holder.tv_userId = (TextView) view
							.findViewById(R.id.tv_userid);
				} catch (Exception e) {
				}
			} else if (message.getMsgType().compareTo(Constant.MSG_LOCATION_TYPE) == 0) {
				try {
					holder.head_iv = (ImageView) view
							.findViewById(R.id.iv_userhead);
					holder.tv = (TextView) view
							.findViewById(R.id.tv_location);
					holder.iv = (ImageView) view
							.findViewById(R.id.iv_location);
					holder.pb = (ProgressBar) view
							.findViewById(R.id.pb_sending);
					holder.staus_iv = (ImageView) view
							.findViewById(R.id.msg_status);
					holder.tv_userId = (TextView) view
							.findViewById(R.id.tv_userid);
				} catch (Exception e) {
				}
			} else if (message.getMsgType().compareTo(Constant.MSG_VIDEO_TYPE) == 0) {
				try {
					holder.iv = ((ImageView) view
							.findViewById(R.id.chatting_content_iv));
					holder.head_iv = (ImageView) view
							.findViewById(R.id.iv_userhead);
					holder.tv = (TextView) view
							.findViewById(R.id.percentage);
					holder.pb = (ProgressBar) view
							.findViewById(R.id.progressBar);
					holder.staus_iv = (ImageView) view
							.findViewById(R.id.msg_status);
					holder.size = (TextView) view
							.findViewById(R.id.chatting_size_iv);
					holder.timeLength = (TextView) view
							.findViewById(R.id.chatting_length_iv);
					holder.playBtn = (ImageView) view
							.findViewById(R.id.chatting_status_btn);
					holder.container_status_btn = (LinearLayout) view
							.findViewById(R.id.container_status_btn);
					holder.tv_userId = (TextView) view
							.findViewById(R.id.tv_userid);

				} catch (Exception e) {
				}
			} else if (message.getMsgType().compareTo(Constant.MSG_FILE_TYPE) == 0) {
				try {
					holder.head_iv = (ImageView) view
							.findViewById(R.id.iv_userhead);
					holder.tv_file_name = (TextView) view
							.findViewById(R.id.tv_file_name);
					holder.tv_file_size = (TextView) view
							.findViewById(R.id.tv_file_size);
					holder.pb = (ProgressBar) view
							.findViewById(R.id.pb_sending);
					holder.staus_iv = (ImageView) view
							.findViewById(R.id.msg_status);
					holder.tv_file_download_state = (TextView) view
							.findViewById(R.id.tv_file_state);
					holder.ll_container = (LinearLayout) view
							.findViewById(R.id.ll_file_container);
					// 这里是进度值
					holder.tv = (TextView) view
							.findViewById(R.id.percentage);
				} catch (Exception e) {
				}
				try {
					holder.tv_userId = (TextView) view
							.findViewById(R.id.tv_userid);
				} catch (Exception e) {
				}

			}
			view.setTag(holder);
		}

		GlobalVars.loadImage(holder.head_iv, message.getPhoto());
		holder.head_iv.setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {

				return true;
			}
		});
//		if (message.isMsgFromMe() && chatType.compareTo(Constant.CHAT_CONSULT) == 0) {
//			holder.tv_ack = (TextView) convertView.findViewById(R.id.tv_ack);
//			holder.tv_delivered = (TextView) convertView
//					.findViewById(R.id.tv_delivered);
//			if (holder.tv_ack != null) {
//				if (message.isAcked) {
//					if (holder.tv_delivered != null) {
//						holder.tv_delivered.setVisibility(View.INVISIBLE);
//					}
//					holder.tv_ack.setVisibility(View.VISIBLE);
//				} else {
//					holder.tv_ack.setVisibility(View.INVISIBLE);
//
//					// check and display msg delivered ack status
//					if (holder.tv_delivered != null) {
//						if (message.isDelivered) {
//							holder.tv_delivered.setVisibility(View.VISIBLE);
//						} else {
//							holder.tv_delivered.setVisibility(View.INVISIBLE);
//						}
//					}
//				}
//			}
//		} else {
//			// 如果是文本或者地图消息并且不是group messgae，显示的时候给对方发送已读回执
//			if ((message.getType() == Type.TXT || message.getType() == Type.LOCATION)
//					&& !message.isAcked && chatType != ChatType.GroupChat) {
//				// 不是语音通话记录
//				if (!message.getBooleanAttribute(
//						Constant.MESSAGE_ATTR_IS_VOICE_CALL, false)) {
//					try {
//						EMChatManager.getInstance().ackMessageRead(
//								message.getFrom(), message.getMsgId());
//						// 发送已读回执
//						message.isAcked = true;
//					} catch (Exception e) {
//						e.printStackTrace();
//					}
//				}
//			}
//		}

		switch (message.getMsgType()) {
			// 根据消息type显示item
			case Constant.MSG_IMAGE_TYPE:
				handleImageMessage(message, holder, position, convertView);
				break;
			case Constant.MSG_TEXT_TYPE:
				handleTextMessage(message, holder, position);
				break;
			case Constant.MSG_LOCATION_TYPE: // 位置
				handleLocationMessage(message, holder, position, convertView);
				break;
			case Constant.MSG_EMOTICON_TYPE: // 位置
				handleEmoticonMessage(message, holder, position, convertView);
				break;
			case Constant.MSG_VOICE_TYPE: // 语音
				handleVoiceMessage(message, holder, position, convertView);
				break;
			case Constant.MSG_VIDEO_TYPE: // 视频
				handleVideoMessage(message, holder, position, convertView);
				break;
			case Constant.MSG_FILE_TYPE: // 一般文件
//				handleFileMessage(message, holder, position, convertView);
				break;
			default:
				// not supported
		}

		return view;
	}

	private void handleTextMessage(MessageModel message, ViewHolder holder,
								   final int position) {

		holder.tv.setText(message.getMsgText());
		// 设置长按事件监听
		holder.tv.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				onItemLongClick(position);
				return true;
			}
		});

		if (message.isMsgFromMe()) {
			holder.pb.setVisibility(View.GONE);
			holder.staus_iv.setVisibility(View.GONE);

			if (message.getMsgStatus().compareTo(Constant.MSG_SEND_SUCCESS) == 0) {
				holder.pb.setVisibility(View.GONE);
				holder.staus_iv.setVisibility(View.GONE);
			}
			if (message.getMsgStatus().compareTo(Constant.MSG_SEND_FAIL) == 0) {
				holder.pb.setVisibility(View.GONE);
				holder.staus_iv.setVisibility(View.GONE);
			}
			if (message.getMsgStatus().compareTo(Constant.MSG_SEND_SUCCESS) == 0) {
//				holder.pb.setVisibility(View.VISIBLE);
//				holder.staus_iv.setVisibility(View.GONE);
			}
		}
	}

	private void handleImageMessage(final MessageModel message,
									final ViewHolder holder, final int position, View convertView) {
//		holder.pb.setTag(position);

		holder.iv.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Utils.start_Activity(activity, ImagePreviewActivity.class,
						new BasicNameValuePair(Constant.PHOTO_PATH, message.getFilePath()),
						new BasicNameValuePair("local", String.valueOf(true)));
			}
		});
		holder.iv.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				onItemLongClick(position);
				return true;
			}
		});

		// 接收方向的消息
		holder.pb.setVisibility(View.GONE);
		holder.tv.setVisibility(View.GONE);
		if (message.getThumbnail() == null)
			holder.iv.setImageResource(R.drawable.default_image);
		else
			holder.iv.setImageBitmap(message.getThumbnail());
	}

	private void handleVoiceMessage(final MessageModel message,
									final ViewHolder holder, final int position, View convertView) {

		int soundLength = (int)message.getSoundLength();

		if (!message.isMsgFromMe()) {
//			holder.lv.setPadding(12, 0, 12 + 20 * (soundLength - 1), 0);
//			holder.lv.setPadding(12, 12, 12, 12);
			holder.iv.setPadding(0, 0, 12 + 20 * (soundLength - 1), 0);
		} else {
			holder.iv.setPadding(12 + 20 * (soundLength - 1), 0, 0, 0);
//			holder.iv.setPadding(0, 0, 0, 0);
		}

		soundLength = Math.max(1, soundLength);
		holder.tv.setText(String.format("%d\"", soundLength));
		holder.iv.setOnClickListener(new VoicePlayClickListener(message,
				holder.iv, holder.iv_read_status, this, activity, username));
		holder.iv.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				onItemLongClick(position);
				return true;
			}
		});

		if (((ChatActivity) activity).playMsgId != null
				&& ((ChatActivity) activity).playMsgId.equals(message
				.getMsgId()) && VoicePlayClickListener.isPlaying) {
			AnimationDrawable voiceAnimation;
			if (!message.isMsgFromMe()) {
//				holder.iv.setImageResource(R.anim.voice_from_icon);
			} else {
//				holder.iv.setImageResource(R.anim.voice_to_icon);
			}
			voiceAnimation = (AnimationDrawable) holder.iv.getDrawable();
			voiceAnimation.start();
		} else {
			if (!message.isMsgFromMe()) {
				holder.iv.setImageResource(R.drawable.chatfrom_voice_playing);
			} else {
				holder.iv.setImageResource(R.drawable.chatto_voice_playing);
			}
		}

		if (!message.isMsgFromMe()) {
			System.err.println("it is receive msg");
			return;
		}
		holder.pb.setVisibility(View.GONE);
		holder.staus_iv.setVisibility(View.GONE);
		if (message.getMsgStatus().compareTo(Constant.MSG_SEND_SUCCESS) == 0) {
			holder.pb.setVisibility(View.GONE);
			holder.staus_iv.setVisibility(View.GONE);
		}
		if (message.getMsgStatus().compareTo(Constant.MSG_SEND_FAIL) == 0) {
			holder.pb.setVisibility(View.GONE);
			holder.staus_iv.setVisibility(View.GONE);
		}
		if (message.getMsgStatus().compareTo(Constant.MSG_SEND_SUCCESS) == 0) {
//			holder.pb.setVisibility(View.VISIBLE);
//			holder.staus_iv.setVisibility(View.GONE);
		}
	}

	private void handleLocationMessage(final MessageModel message,
									   final ViewHolder holder, final int position, View convertView) {

		if (message.getThumbnail() != null)
			holder.iv.setImageBitmap(message.getThumbnail());
		holder.iv.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				onItemLongClick(position);
				return true;
			}
		});
		holder.iv.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String[] strArr = message.getMsgText().split("-");
				double latitude = Double.valueOf(strArr[0]);
				double longitude = Double.valueOf(strArr[1]);
				float zoom = Float.valueOf(strArr[2]);

				Intent intent = new Intent(context, MapDetailActivity.class);
				intent.putExtra(Constant.SAVED_LATITUDE, latitude);
				intent.putExtra(Constant.SAVED_LONGITUDE, longitude);
				intent.putExtra(Constant.SAVED_ZOOM, zoom);
				activity.startActivity(intent);
			}
		});

//		locationView.setText(locBody.getAddress());
		double latitude = 0;
		double longitude = 0;
		LatLng loc = new LatLng(latitude, longitude);

		if (!message.isMsgFromMe()) {
			System.err.println("it is receive msg");
			return;
		}
		holder.pb.setVisibility(View.GONE);
		holder.staus_iv.setVisibility(View.GONE);
		if (message.getMsgStatus().compareTo(Constant.MSG_SEND_SUCCESS) == 0) {
			holder.pb.setVisibility(View.GONE);
			holder.staus_iv.setVisibility(View.GONE);
		}
		if (message.getMsgStatus().compareTo(Constant.MSG_SEND_FAIL) == 0) {
			holder.pb.setVisibility(View.GONE);
			holder.staus_iv.setVisibility(View.GONE);
		}
		if (message.getMsgStatus().compareTo(Constant.MSG_SEND_SUCCESS) == 0) {
//			holder.pb.setVisibility(View.VISIBLE);
//			holder.staus_iv.setVisibility(View.GONE);
		}
	}

	private void handleVideoMessage(final MessageModel message,
									final ViewHolder holder, final int position, View convertView) {

		// final File image=new File(PathUtil.getInstance().getVideoPath(),
		// videoBody.getFileName());
		if (message.getThumbnail() != null)
			holder.iv.setImageBitmap(message.getThumbnail());
		holder.iv.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				onItemLongClick(position);
				return true;
			}
		});


		holder.playBtn.setImageResource(R.drawable.video_download_btn_nor);
		holder.playBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Utils.start_Activity(activity, VideoPlayActivity.class,
						new BasicNameValuePair(Constant.VIDEO_PATH, message.getFilePath()));
			}
		});

//		holder.pb.setTag(position);

		if (!message.isMsgFromMe()) {
			System.err.println("it is receive msg");
			return;
		}
		if (message.getMsgStatus().compareTo(Constant.MSG_SEND_SUCCESS) == 0) {
			holder.pb.setVisibility(View.GONE);
			holder.staus_iv.setVisibility(View.GONE);
		}
		if (message.getMsgStatus().compareTo(Constant.MSG_SEND_FAIL) == 0) {
			holder.pb.setVisibility(View.GONE);
			holder.staus_iv.setVisibility(View.GONE);
		}
		if (message.getMsgStatus().compareTo(Constant.MSG_SEND_SUCCESS) == 0) {
//			holder.pb.setVisibility(View.VISIBLE);
//			holder.staus_iv.setVisibility(View.GONE);
		}

	}

	private void handleEmoticonMessage(final MessageModel message,
									final ViewHolder holder, final int position, View convertView) {
//		holder.pb.setTag(position);
		holder.iv.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				onItemLongClick(position);
				return true;
			}
		});
		// 接收方向的消息
		holder.pb.setVisibility(View.GONE);
		holder.tv.setVisibility(View.GONE);

		Display display = activity.getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);

		holder.iv.setLayoutParams(new RelativeLayout.LayoutParams(size.x / 4, size.x / 4));

		EmoticonModel emoticon = GlobalVars.getEmoticon(message.getMsgText());
		if (emoticon != null)
			holder.iv.setImageResource(emoticon.resId);
	}

	public static class ViewHolder {
		ImageView iv;
		TextView tv;
		ProgressBar pb;
		ImageView staus_iv;
		ImageView head_iv;
		TextView tv_userId;
		ImageView playBtn;
		TextView timeLength;
		TextView size;
		LinearLayout container_status_btn;
		LinearLayout ll_container;
		ImageView iv_read_status;
		// 显示已读回执状态
		TextView tv_ack;
		// 显示送达回执状态
		TextView tv_delivered;

		TextView tv_file_name;
		TextView tv_file_size;
		TextView tv_file_download_state;
		FrameLayout lv; //voice background

		int position;
	}

	public void onItemLongClick(int position) {
		if (mItemLongClickListener == null)
			return;
		mItemLongClickListener.onChatLongClick(position);
	}

	public void setItemLongClickListener(
			OnItemLongClickListener onItemLongClickListener) {
		this.mItemLongClickListener = onItemLongClickListener;
	}

	public static interface OnItemLongClickListener {
		public void onChatLongClick(int position);
	}



}