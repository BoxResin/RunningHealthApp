package util;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.view.MenuItem;

import app.boxresin.runninghealthapp.MainActivity;
import app.boxresin.runninghealthapp.R;
import app.boxresin.runninghealthapp.SettingActivity;

/**
 * Created by eomin on 2016-11-05.
 */

public class Initializer
{
	/**
	 * 내비게이션 목록 뷰를 초기화하는 메서드
	 */
	public static void initNavView(final Context context, NavigationView navView)
	{
		navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener()
		{
			@Override
			public boolean onNavigationItemSelected(MenuItem item)
			{
				switch (item.getItemId())
				{
				case R.id.nav_main: // 지도 화면을 눌렀을 때
					context.startActivity(new Intent(context, MainActivity.class));
					break;

				case R.id.nav_record:
//					context.startActivity(new Intent(context, RecordActivity.class));
					break;

				case R.id.nav_setting: // 세팅 화면을 눌렀을 때
					context.startActivity(new Intent(context, SettingActivity.class));
					break;
				}
				return false;
			}
		});
	}
}
