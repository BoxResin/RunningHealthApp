package data;

import android.content.Context;
import android.content.SharedPreferences;

import net.daum.mf.map.api.MapView;

/**
 * 환경설정을 관리하는 클래스
 */
public class Pref
{
	public static boolean isFirst; // 이번 실행이 첫 번째 앱 실행인지 여부
	public static String gender;
	public static int height;
	public static int weight;
	public static MapView.MapType mapType;

	public static void load(Context context)
	{
		SharedPreferences prefs = context.getSharedPreferences("Pref", Context.MODE_PRIVATE);
		isFirst = prefs.getBoolean("isFirst", true);
		gender = prefs.getString("gender", "");
		height = prefs.getInt("height", 0);
		weight = prefs.getInt("weight", 0);

		String mapType_str = prefs.getString("mapType", "Standard");
		if (mapType_str.equals("Standard"))
			mapType = MapView.MapType.Standard;
		else if (mapType_str.equals("Satellite"))
			mapType = MapView.MapType.Satellite;
		else if (mapType_str.equals("Hybrid"))
			mapType = MapView.MapType.Hybrid;
	}

	public static void save(Context context)
	{
		SharedPreferences prefs = context.getSharedPreferences("Pref", Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putBoolean("isFirst", isFirst);
		editor.putString("gender", gender);
		editor.putInt("height", height);
		editor.putInt("weight", weight);

		if (mapType == MapView.MapType.Standard)
			editor.putString("mapType", "Standard");
		else if (mapType == MapView.MapType.Satellite)
			editor.putString("mapType", "Satellite");
		else if (mapType == MapView.MapType.Hybrid)
			editor.putString("mapType", "Hybrid");
		editor.apply();
	}
}
