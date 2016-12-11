package app.boxresin.runninghealthapp;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioGroup;

import app.boxresin.runninghealthapp.databinding.ActivitySettingBinding;
import data.Pref;

import static net.daum.mf.map.api.MapView.MapType.Hybrid;
import static net.daum.mf.map.api.MapView.MapType.Satellite;
import static net.daum.mf.map.api.MapView.MapType.Standard;

/**
 * 설정 액티비티
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
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		// 환경설정 대로 초기값을 설정한다.
		if (Pref.gender.equals("man"))
			binding.btnMan.setChecked(true);
		else if (Pref.gender.equals("woman"))
			binding.btnWoman.setChecked(true);

		binding.editHeight.setText("" + Pref.height);
		binding.editWeight.setText("" + Pref.weight);

		switch (Pref.mapType)
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

		// 라디오 버튼을 초기화한다.
		binding.genderChoice.setOnCheckedChangeListener(this);
		binding.radioMapType.setOnCheckedChangeListener(this);

		// 신체정보 관련 에디트를 초기화한다.
		binding.editHeight.setOnKeyListener(new View.OnKeyListener()
		{
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event)
			{
				if (v.getId() == R.id.edit_height)
				{
					if (!binding.editHeight.getText().toString().equals(""))
						Pref.height = Integer.parseInt(binding.editHeight.getText().toString());
				}
				else if (v.getId() == R.id.edit_weight)
				{
					if (!binding.editWeight.getText().toString().equals(""))
						Pref.weight = Integer.parseInt(binding.editWeight.getText().toString());
				}
				return false;
			}
		});
	}

	/**
	 * 사용자가 라디오 버튼을 눌렀을 때 호출되는 메서드
	 */
	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId)
	{
		// 성별 선택 라디오 버튼
		if (group == binding.genderChoice)
		{
			switch (checkedId)
			{
			case R.id.btn_man:
				Pref.gender = "man";
				break;

			case R.id.btn_woman:
				Pref.gender = "woman";
				break;
			}
		}

		// 지도 타입 라디오 버튼
		else if (group == binding.radioMapType)
		{
			switch (checkedId)
			{
			case R.id.btn_standard_map: // 약도 형식 맵
				Pref.mapType = Standard;
				break;

			case R.id.btn_satellite_map: // 위성 형식 맵
				Pref.mapType = Satellite;
				break;

			case R.id.btn_hybrid_map: // 혼항형 맵
				Pref.mapType = Hybrid;
				break;
			}
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
		// 사용자가 뒤로가기 버튼을 누르면 액티비티를 종료한다.
		case android.R.id.home:
			onBackPressed();
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}
}