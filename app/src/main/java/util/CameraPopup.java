package util;

import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.PopupWindow;

import app.boxresin.runninghealthapp.databinding.PopupCameraBinding;

/**
 * 카메라 팝업 뷰를 초기화·관리하는 유틸리티 클래스
 */
public class CameraPopup
{
	private static int dx, dy, xp, yp, sides, topBot;

	/**
	 * 카메라 팝업 뷰를 초기화하는 메서드
	 */
	public static void init(final PopupWindow cameraPopup, PopupCameraBinding binding)
	{
		binding.surfaceCamera.setOnTouchListener(new View.OnTouchListener()
		{
			@Override
			public boolean onTouch(View v, MotionEvent event)
			{
				switch (event.getAction())
				{
				case MotionEvent.ACTION_DOWN:
					dx = (int) event.getX();
					dy = (int) event.getY();
					return true;

				case MotionEvent.ACTION_MOVE:
					xp = (int) event.getRawX();
					yp = (int) event.getRawY();
					sides = (xp - dx);
					topBot = (yp - dy);
					cameraPopup.update(sides, topBot, -1, -1, true);
					return true;
				}

				return false;
			}
		});
	}

	/**
	 * 마지막으로 있었던 위치에 카메라 팝업을 띄우는 메서드
	 */
	public static void show(PopupWindow cameraPopup, View parent)
	{
		cameraPopup.showAtLocation(parent, Gravity.LEFT | Gravity.TOP, sides, topBot);
	}
}