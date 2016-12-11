package app.boxresin.runninghealthapp;

import android.app.Application;

import data.Pref;
import data.RecordDatabase;

/**
 * 커스텀 Application 클래스
 */
public class MyApplication extends Application
{
	/**
	 * 앱이 실행될 때 호출되는 메서드
	 */
	@Override
	public void onCreate()
	{
		super.onCreate();

		// 환경설정을 로드한다.
		Pref.load(this);

		// 데이터베이스를 초기화한다.
		RecordDatabase.init(this);
	}
}