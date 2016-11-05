package util;

import android.content.Context;

import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

/**
 * 다음 맵뷰는 두 개 이상 존재하면 오류가 발생하므로, 싱글턴 패턴을 사용한다.
 */
public class DaumMapView
{
	private static DaumMapView ourInstance;
	private MapView mapView;

	public static MapView get(Context context)
	{
		if (ourInstance == null)
			ourInstance = new DaumMapView(context);
		return ourInstance.mapView;
	}

	private DaumMapView(Context context)
	{
		// 맵뷰를 초기화한다.
		mapView = new MapView(context);
		mapView.setDaumMapApiKey("d7430f85cbcf60aced1e2c584ae6361f"); // 다음 API 키 적용
		mapView.setMapCenterPointAndZoomLevel(MapPoint.mapPointWithGeoCoord(37.494632, 126.959854), -2, true); // 정보대 위치로 이동
		mapView.setMapType(MapView.MapType.Hybrid); // 지도는 스카이뷰 모드로
	}
}