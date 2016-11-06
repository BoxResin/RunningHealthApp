package app.boxresin.runninghealthapp;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import net.daum.mf.map.api.MapView;

import app.boxresin.runninghealthapp.databinding.FragmentSettingBinding;
import global.Settings;

/**
 * 설정 프래그먼트
 */

public class SettingFragment extends Fragment implements Toolbar.OnMenuItemClickListener, RadioGroup.OnCheckedChangeListener
{
	private FragmentSettingBinding binding;

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
	{
		binding = DataBindingUtil.inflate(inflater, R.layout.fragment_setting, container, false);

		// 지도 타입 라디오 버튼을 초기화한다.
		binding.radioMapType.setOnCheckedChangeListener(this);

		return binding.getRoot();
	}

	@Override
	public boolean onMenuItemClick(MenuItem item)
	{
		return false;
	}

	/**
	 * 사용자가 라디오 버튼을 눌렀을 때 호출되는 메서드
	 */
	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId)
	{
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