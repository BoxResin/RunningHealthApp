package data;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * 환경설정을 관리하는 클래스
 */
public class Pref
{
	public static boolean isFirst; // 이번 실행이 첫 번째 앱 실행인지 여부

	public static void load(Context context)
	{
		SharedPreferences prefs = context.getSharedPreferences("Pref", Context.MODE_PRIVATE);
		isFirst = prefs.getBoolean("isFirst", false);
	}

	public static void save(Context context)
	{
		SharedPreferences prefs = context.getSharedPreferences("Pref", Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putBoolean("isFirst", isFirst);
		editor.commit();
	}
}
