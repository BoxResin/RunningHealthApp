package util;

import android.app.Activity;
import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import app.boxresin.runninghealthapp.MainActivity;
import app.boxresin.runninghealthapp.R;
import app.boxresin.runninghealthapp.SettingActivity;

import static android.content.Intent.FLAG_ACTIVITY_NO_ANIMATION;

/**
 * Created by eomin on 2016-11-05.
 */

public class Initializer
{
	/**
	 * 내비게이션 목록 뷰를 초기화하는 메서드
	 */
	public static void initNavDrawer(final Activity activity, Toolbar toolbar, final DrawerLayout drawer, NavigationView navView)
	{
		// 툴바에 햄버거 메뉴를 추가하고 내비게이션 드로어와 동기화한다.
		ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(activity, drawer, toolbar, R.string.app_name, R.string.app_name);
		drawer.setDrawerListener(toggle);
		toggle.syncState();

		// 내비게이션 목록을 누르면 해당 액티비티로 이동하게 한다.
		navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener()
		{
			@Override
			public boolean onNavigationItemSelected(MenuItem item)
			{
				drawer.closeDrawers();
				activity.finish();
				activity.overridePendingTransition(0, 0);

				Intent intent = null;

				switch (item.getItemId())
				{
				case R.id.nav_main: // 지도 화면을 눌렀을 때
					intent = new Intent(activity, MainActivity.class);
					break;

				case R.id.nav_record: // 기록을 눌렀을 때
					break;

				case R.id.nav_setting: // 설정을 눌렀을 때
					intent = new Intent(activity, SettingActivity.class);
					break;
				}

				if (intent != null)
				{
					intent.addFlags(FLAG_ACTIVITY_NO_ANIMATION);
					activity.startActivity(intent);
				}
				return true;
			}
		});
	}
}
