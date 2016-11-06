package app.boxresin.runninghealthapp;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

import app.boxresin.runninghealthapp.databinding.FragmentMapBinding;
import global.Settings;


/**
 * 지도 화면 프래그먼트
 */
public class MapFragment extends Fragment implements Toolbar.OnMenuItemClickListener, View.OnClickListener
{
	private FragmentMapBinding binding;
	private MapView mapView;

	private boolean bStarted; // 위치 기록 시작 여부
	private boolean bChase; // 현재 위치 추적 여부

	private static int imgPadding;

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
	{
		binding = DataBindingUtil.inflate(inflater, R.layout.fragment_map, container, false);

		// 맵뷰를 초기화한다.
		mapView = new MapView(getActivity());
		mapView.setDaumMapApiKey("d7430f85cbcf60aced1e2c584ae6361f"); // 다음 API 키 적용
		mapView.setMapCenterPointAndZoomLevel(MapPoint.mapPointWithGeoCoord(37.494632, 126.959854), -2, true); // 정보대 위치로 이동
		mapView.setMapType(Settings.get().getMapType()); // 지도 표시 형식(약도, 위성 사진 등)은 설정값대로

		// 화면에 맵뷰를 추가한다.
		binding.mapViewParent.addView(mapView);

		// 버튼을 초기화한다.
		binding.btnLocationChase.setOnClickListener(this);
		binding.btnZoomIn.setOnClickListener(this);
		binding.btnZoomOut.setOnClickListener(this);

		return binding.getRoot();
	}

	/**
	 * 지도 프래그먼트가 사라지거나 나타날 때 호출되는 메서드
	 */
	@Override
	public void onHiddenChanged(boolean hidden)
	{
		if (!hidden)
		{
			// 환경설정대로 지도 표시 형태를 갱신한다.
			mapView.setMapType(Settings.get().getMapType());
		}
	}

	/**
	 * 메뉴의 상태(ex. 시작·정지 상태인지)를 동기화하는 메서드
	 */
	public void syncMenuStatus(Menu menu)
	{
		if (bStarted)
			menu.findItem(R.id.action_start).setIcon(R.drawable.action_pause_white);
		else
			menu.findItem(R.id.action_start).setIcon(R.drawable.action_start_white);
	}

	/**
	 * 지도 프래그먼트에서 메뉴 아이템을 클릭했을 때 호출되는 메서드
	 */
	@Override
	public boolean onMenuItemClick(MenuItem item)
	{
		if (item.getItemId() == R.id.action_start)
		{
			if (!bStarted)
			{
				item.setIcon(R.drawable.action_pause_white);
				bStarted = true;
			}
			else
			{
				item.setIcon(R.drawable.action_start_white);
				bStarted = false;
			}
		}

		return false;
	}

	/**
	 * 지도 위의 버튼을 눌렀을 때 호출되는 메서드
	 */
	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
		case R.id.btn_location_chase: // 위치 추적 버튼
			imgPadding = binding.btnLocationChase.getPaddingLeft();

			// 현재 위치 추적 끄기
			if (bChase)
			{
				bChase = false;
				binding.btnLocationChase.setImageDrawable(getResources().getDrawable(R.drawable.action_my_location));
				binding.btnLocationChase.setBackgroundResource(R.drawable.btn_square_flat);
			}

			// 현재 위치 추적 켜기
			else
			{
				bChase = true;
				binding.btnLocationChase.setImageDrawable(getResources().getDrawable(R.drawable.action_my_location_white));
				binding.btnLocationChase.setBackgroundResource(R.drawable.btn_square_theme_flat_normal);
			}

			binding.btnLocationChase.setPadding(imgPadding, imgPadding, imgPadding, imgPadding);
			break;

		case R.id.btn_zoom_in: // 지도 확대 버튼
			mapView.zoomIn(true);
			break;

		case R.id.btn_zoom_out: // 지도 축소 버튼
			mapView.zoomOut(true);
			break;
		}
	}
}