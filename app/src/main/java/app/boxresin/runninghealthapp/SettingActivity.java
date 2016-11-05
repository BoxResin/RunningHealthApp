package app.boxresin.runninghealthapp;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import app.boxresin.runninghealthapp.databinding.ActivitySettingBinding;

/**
 * Created by eomin on 2016-11-04.
 */

public class SettingActivity extends AppCompatActivity
{
	private ActivitySettingBinding binding;
	private Toolbar toolbar;
	private NavigationView navView;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		binding = DataBindingUtil.setContentView(this, R.layout.activity_setting);

		// 툴바를 초기화한다.
		toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		// 내비게이션 드로어를 초기화한다.
		navView = (NavigationView) findViewById(R.id.nav_view);
//		Initializer.initNavDrawer(this, toolbar, binding.drawer, navView);

		// 내비게이션 드로어에서, 설정 (설정 액티비티)에 체크한다.
		navView.setCheckedItem(R.id.nav_setting);
	}
}