package global;

import net.daum.mf.map.api.MapView;

/**
 * 전역적인 환경설정 값을 저장하는 클래스
 */
public class Settings
{
	private static Settings ourInstance = new Settings();

	public static Settings get()
	{
		return ourInstance;
	}

	private Settings()
	{
	}

	private MapView.MapType mapType = MapView.MapType.Standard;

	public MapView.MapType getMapType()
	{
		return mapType;
	}

	public void setMapType(MapView.MapType mapType)
	{
		this.mapType = mapType;
	}
}