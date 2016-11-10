package util;

import android.content.Context;
import android.view.ViewGroup;

import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

import global.Settings;

/**
 * 다음 맵 뷰가 하나만 생성되도록 하기 위한 싱글톤
 *
 */
public class DaumMapView
{
	private static DaumMapView inst;

	private MapView mapView;
	private ViewGroup lastParent; // 마지막으로 mapView와 연결된 부모뷰

	public static MapView get(Context context)
	{
		if (inst == null)
			inst = new DaumMapView(context);
		return inst.mapView;
	}

	private DaumMapView(Context context)
	{
		// 맵뷰를 초기화한다.
		mapView = new MapView(context);
		mapView.setDaumMapApiKey("d7430f85cbcf60aced1e2c584ae6361f"); // 다음 API 키 적용
		mapView.setMapCenterPointAndZoomLevel(MapPoint.mapPointWithGeoCoord(37.494632, 126.959854), -2, true); // 정보대 위치로 이동
		mapView.setMapType(Settings.get().getMapType()); // 지도 표시 형식(약도, 위성 사진 등)은 설정값대로
	}

	/**
	 * 맵뷰의 부모 뷰를 바꾸는 메서드
	 */
	public static void changeParent(Context context, ViewGroup parent)
	{
		MapView mapView = get(context);

		if (inst.lastParent != null)
			inst.lastParent.removeView(mapView);
		parent.addView(mapView);
		inst.lastParent = parent;
	}
}