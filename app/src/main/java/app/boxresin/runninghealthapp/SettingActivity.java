package app.boxresin.runninghealthapp;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.RadioGroup;

import net.daum.mf.map.api.MapView;

import app.boxresin.runninghealthapp.databinding.ActivitySettingBinding;
import global.Settings;

/**
 * Created by eomin on 2016-11-06.
 */

public class SettingActivity extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener
{
	private ActivitySettingBinding binding;
	private Toolbar toolbar;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		binding = DataBindingUtil.setContentView(this, R.layout.activity_setting);

		// 툴바를 초기화한다.
		toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		// 환경설정 대로 초기값을 설정한다.
		switch (Settings.get().getMapType())
		{
		case Standard:
			binding.btnStandardMap.setChecked(true);
			break;

		case Satellite:
			binding.btnSatelliteMap.setChecked(true);
			break;

		case Hybrid:
			binding.btnHybridMap.setChecked(true);
			break;
		}

		// 지도 타입 라디오 버튼을 초기화한다.
		binding.radioMapType.setOnCheckedChangeListener(this);
	}

	/**
	 * 사용자가 라디오 버튼을 눌렀을 때 호출되는 메서드
	 */
	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId)
	{
		// 지도 타입 라디오 버튼
		if (group == binding.radioMapType)
		{
			switch (checkedId)
			{
			case R.id.btn_standard_map: // 약도 형식 맵
				Settings.get().setMapType(MapView.MapType.Standard);
				break;

			case R.id.btn_satellite_map: // 위성 형식 맵
				Settings.get().setMapType(MapView.MapType.Satellite);
				break;

			case R.id.btn_hybrid_map: // 혼항형 맵
				Settings.get().setMapType(MapView.MapType.Hybrid);
				break;
			}
		}
	}
}