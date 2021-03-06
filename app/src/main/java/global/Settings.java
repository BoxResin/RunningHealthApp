package global;

import android.support.design.widget.NavigationView;
import android.widget.ArrayAdapter;

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

	private ArrayAdapter<Record> recordAdapter;
	private NavigationView navView;
	public Record currentRecord;

	public ArrayAdapter<Record> getRecordAdapter()
	{
		return recordAdapter;
	}

	public void setRecordAdapter(ArrayAdapter<Record> recordAdapter)
	{
		this.recordAdapter = recordAdapter;
	}

	public NavigationView getNavView()
	{
		return navView;
	}

	public void setNavView(NavigationView navView)
	{
		this.navView = navView;
	}
}