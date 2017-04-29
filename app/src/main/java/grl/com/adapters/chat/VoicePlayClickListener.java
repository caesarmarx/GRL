/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package grl.com.adapters.chat;

import java.io.File;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Toast;

import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMMessage;
import com.easemob.chat.EMMessage.ChatType;
import com.easemob.chat.VoiceMessageBody;

import grl.com.configuratoin.Constant;
import grl.com.dataModels.MessageModel;
import grl.wangu.com.grl.R;
import grl.com.activities.consult.ChatActivity;

public class VoicePlayClickListener implements View.OnClickListener {

	MessageModel message;
	VoiceMessageBody voiceBody;
	ImageView voiceIconView;

	private AnimationDrawable voiceAnimation = null;
	MediaPlayer mediaPlayer = null;
	ImageView iv_read_status;
	Activity activity;
	private String chatType;
	private BaseAdapter adapter;

	public static boolean isPlaying = false;
	public static VoicePlayClickListener currentPlayListener = null;


	public VoicePlayClickListener(MessageModel message, ImageView v,
								  ImageView iv_read_status, BaseAdapter adapter, Activity activity,
								  String username) {
		this.message = message;
		this.iv_read_status = iv_read_status;
		this.adapter = adapter;
		voiceIconView = v;
		this.activity = activity;
		this.chatType = message.getGroupType();
	}

	public void stopPlayVoice() {
		voiceAnimation.stop();
		if (!message.isMsgFromMe()) {
			voiceIconView.setImageResource(R.drawable.chatfrom_voice_playing);
		} else {
			voiceIconView.setImageResource(R.drawable.chatto_voice_playing);
		}
		// stop play voice
		if (mediaPlayer != null) {
			mediaPlayer.stop();
			mediaPlayer.release();
		}
		isPlaying = false;
		((ChatActivity) activity).playMsgId = null;
		adapter.notifyDataSetChanged();
	}

	public void playVoice(String filePath) {
		if (!(new File(filePath).exists())) {
			return;
		}

		AudioManager audioManager = (AudioManager) activity
				.getSystemService(Context.AUDIO_SERVICE);

		mediaPlayer = new MediaPlayer();
		if (EMChatManager.getInstance().getChatOptions().getUseSpeaker()) {
			audioManager.setMode(AudioManager.MODE_NORMAL);
			audioManager.setSpeakerphoneOn(true);
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_RING);
		} else {
			audioManager.setSpeakerphoneOn(false);// 关闭扬声器
			// 把声音设定成Earpiece（听筒）出来，设定为正在通话中
			audioManager.setMode(AudioManager.MODE_IN_CALL);
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_VOICE_CALL);
		}
		try {
			mediaPlayer.setDataSource(filePath);
			mediaPlayer.prepare();
			mediaPlayer
					.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

						@Override
						public void onCompletion(MediaPlayer mp) {
							// TODO Auto-generated method stub
							mediaPlayer.release();
							mediaPlayer = null;
							stopPlayVoice(); // stop animation
						}

					});
			isPlaying = true;
			currentPlayListener = this;
			mediaPlayer.start();
			showAnimation();

			// 如果是接收的消息
			if (!message.isMsgFromMe()) {


			}

		} catch (Exception e) {
		}
	}

	// show the voice playing animation
	private void showAnimation() {
		// play voice, and start animation
		if (!message.isMsgFromMe()) {
			voiceIconView.setImageResource(R.anim.voice_from_icon);
		} else {
			voiceIconView.setImageResource(R.anim.voice_to_icon);
		}
		voiceAnimation = (AnimationDrawable) voiceIconView.getDrawable();
		voiceAnimation.start();
	}

	@Override
	public void onClick(View v) {
		String st = activity.getResources().getString(
				R.string.Is_download_voice_click_later);
		if (isPlaying) {
			if (((ChatActivity) activity).playMsgId != null
					&& ((ChatActivity) activity).playMsgId.equals(message
							.getMsgId())) {
				currentPlayListener.stopPlayVoice();
				return;
			}
			currentPlayListener.stopPlayVoice();
		}

		if (message.isMsgFromMe()) {
			// for sent msg, we will try to play the voice file directly
			playVoice(message.getFilePath());
		} else {
			if (message.getMsgStatus().compareTo(Constant.MSG_RECEIVE_SUCCESS) == 0) {
				playVoice(message.getFilePath());
			} else if (message.getMsgStatus().compareTo(Constant.MSG_RECEIVE_PROGRESS) == 0) {
				String s = new String();
				Toast.makeText(activity, st, Toast.LENGTH_SHORT).show();
			} else if (message.getMsgStatus().compareTo(Constant.MSG_RECEIVE_FAIL) == 0) {
				Toast.makeText(activity, st, Toast.LENGTH_SHORT).show();
			}

		}
	}
}