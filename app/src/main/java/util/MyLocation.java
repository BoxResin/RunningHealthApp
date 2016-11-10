package util;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 직렬화를 할 수 있는 좌표 클래스
 */
public class MyLocation implements Parcelable
{
	public final double latitude;
	public final double longitude;

	public MyLocation(double latitude, double longitude)
	{
		this.latitude = latitude;
		this.longitude = longitude;
	}

	protected MyLocation(Parcel in)
	{
		latitude = in.readDouble();
		longitude = in.readDouble();
	}

	public static final Creator<MyLocation> CREATOR = new Creator<MyLocation>()
	{
		@Override
		public MyLocation createFromParcel(Parcel in)
		{
			return new MyLocation(in);
		}

		@Override
		public MyLocation[] newArray(int size)
		{
			return new MyLocation[size];
		}
	};

	@Override
	public int describeContents()
	{
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags)
	{

		dest.writeDouble(latitude);
		dest.writeDouble(longitude);
	}
}