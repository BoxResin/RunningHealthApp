package app.boxresin.runninghealthapp;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

import app.boxresin.runninghealthapp.databinding.FragmentMapBinding;


/**
 * 지도 화면 프래그먼트
 */
public class MapFragment extends Fragment
{
	private FragmentMapBinding binding;
	private MapView mapView;

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
	{
		binding = DataBindingUtil.inflate(inflater, R.layout.fragment_map, container, false);

		// 맵뷰를 초기화한다.
		mapView = new MapView(getActivity());
		mapView.setDaumMapApiKey("d7430f85cbcf60aced1e2c584ae6361f"); // 다음 API 키 적용
		mapView.setMapCenterPointAndZoomLevel(MapPoint.mapPointWithGeoCoord(37.494632, 126.959854), -2, true); // 정보대 위치로 이동
		mapView.setMapType(MapView.MapType.Hybrid); // 지도는 스카이뷰 모드로

		// 화면에 맵뷰를 추가한다.
		binding.mapViewParent.addView(mapView);

		return binding.getRoot();
	}

//	@Override
//	public void onDestroyView()
//	{
//		binding.mapViewParent.removeView(DaumMapView.get(getActivity()));
//		super.onDestroyView();
//	}
}