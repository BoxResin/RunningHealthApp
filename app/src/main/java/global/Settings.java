package global;

import android.widget.ArrayAdapter;

import net.daum.mf.map.api.MapView;

import data.Record;

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
	private ArrayAdapter<Record> recordAdapter;

	public MapView.MapType getMapType()
	{
		return mapType;
	}

	public void setMapType(MapView.MapType mapType)
	{
		this.mapType = mapType;
	}

	public ArrayAdapter<Record> getRecordAdapter()
	{
		return recordAdapter;
	}

	public void setRecordAdapter(ArrayAdapter<Record> recordAdapter)
	{
		this.recordAdapter = recordAdapter;
	}
}