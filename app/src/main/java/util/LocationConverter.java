package util;

import android.os.Parcelable;

import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapPolyline;

/**
 * GPS 좌표 <-> 다음 맵 포인트를 변환할 수 있는 유틸리티 클래스
 */

public class LocationConverter
{
	public static MyLocation[] toMyLocations(MapPolyline polyline)
	{
		int pointCount = polyline.getPointCount();
		MapPoint[] mapPoints = polyline.getMapPoints();

		MyLocation[] locations = new MyLocation[pointCount];
		for (int i = 0; i < pointCount; i++)
		{
			MapPoint.GeoCoordinate geoCoord = polyline.getPoint(i).getMapPointGeoCoord();
			locations[i] = new MyLocation(geoCoord.latitude, geoCoord.longitude);
		}

		return locations;
	}

	public static MapPolyline toPolyline(Parcelable[] locations)
	{
		MapPolyline polyline = new MapPolyline();
		for (Parcelable i :	locations)
			polyline.addPoint(MapPoint.mapPointWithGeoCoord(((MyLocation) i).latitude, ((MyLocation) i).longitude));
		return polyline;
	}
}