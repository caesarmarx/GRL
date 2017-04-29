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
package grl.com.configuratoin;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.ImageView;

import com.easemob.util.EMLog;
import com.easemob.util.PathUtil;

import java.io.File;

import grl.com.App;

public class ImageUtils {

	public static String getImagePath(String remoteUrl) {
		String imageName = remoteUrl.substring(remoteUrl.lastIndexOf("/") + 1,
				remoteUrl.length());
		String path = PathUtil.getInstance().getImagePath() + "/" + imageName;
		EMLog.d("msg", "image path:" + path);
		return path;

	}

	public static String getThumbnailImagePath(String thumbRemoteUrl) {
		String thumbImageName = thumbRemoteUrl.substring(
				thumbRemoteUrl.lastIndexOf("/") + 1, thumbRemoteUrl.length());
		String path = PathUtil.getInstance().getImagePath() + "/" + "th"
				+ thumbImageName;
		EMLog.d("msg", "thum image path:" + path);
		return path;
	}

	public static String getRealPathFromURI(Uri contentUri) {
		Cursor cursor = null;
		try {
			String[] proj = { MediaStore.Images.Media.DATA };
			cursor = App.getInstance().getContentResolver().query(contentUri,  proj, null, null, null);
			int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			cursor.moveToFirst();
			return cursor.getString(column_index);
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}

	public static  Boolean setImageFromPath (String path, ImageView myImage){
		File imgFile = new File(path);
		if(imgFile == null)
			return false;
		if(imgFile.exists()){
			Bitmap myBitmap = null;
			if(imgFile.length() > 2048 * 2048) {
				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inSampleSize = 8;

				myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath(), options);
			} else
			{
				myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
			}
			myImage.setImageBitmap(myBitmap);

			return  true;

		}
		return  false;
	}
}
