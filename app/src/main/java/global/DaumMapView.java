package global;

import android.content.Context;
import android.view.ViewGroup;

import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

/**
 * 다음 맵 뷰가 하나만 생성되도록 하기 위한 싱글톤
 *
 */
public class DaumMapView
{
	private static DaumMapView inst;

	private MapView origin;
	private MapView mapView;
	private ViewGroup lastParent; // 마지막으로 mapView와 연결된 부모뷰

	public static MapView get(Context context)
	{
		if (inst == null)
			inst = new DaumMapView(context);
		return inst.origin;
	}

	private DaumMapView(Context context)
	{
		initMapView(context);
	}

	private void initMapView(Context context)
	{
		// 맵뷰를 초기화한다.
		origin = new MapView(context);
		origin.setDaumMapApiKey("d7430f85cbcf60aced1e2c584ae6361f"); // 다음 API 키 적용
		origin.setMapCenterPointAndZoomLevel(MapPoint.mapPointWithGeoCoord(37.494632, 126.959854), -2, true); // 정보대 위치로 이동
		origin.setMapType(Settings.get().getMapType()); // 지도 표시 형식(약도, 위성 사진 등)은 설정값대로
	}

	/**
	 * 맵뷰의 부모 뷰를 바꾸는 메서드
	 */
	public static void changeParent(Context context, ViewGroup parent)
	{
		get(context);

		// 이전의 맵뷰는 삭제하고 새로 만든다.
		resetParent(context, inst.lastParent);

		parent.addView(get(context));
		inst.lastParent = parent;
	}

	/**
	 * 맵뷰의 부모 뷰를 초기화하는 메서드
	 */
	public static void resetParent(Context context, ViewGroup parent)
	{
		// 맵뷰를 부모 뷰로부터 분리하고 새로 만든다.
		if (parent != null)
		{
			get(context);
			parent.removeView(get(context));
			inst.lastParent = null;
			inst.initMapView(context);
		}
	}
}