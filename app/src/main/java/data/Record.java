package data;

import net.daum.mf.map.api.MapPoint;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.UUID;

/**
 * 기록 클래스
 */
public class Record
{
	private UUID id;   // 기록 아이디 (DB 식별용)
	private String name; // 기록 이름
	private String date; // 기록 날짜 (운동을 끝낸 시간)
	private double moved; // 총 움직인 거리 (km)
	private double consumed; // 총 소비한 칼로리
	private double elapsed; // 총 운동 시간 (분)
	private double fastestKmh = 0; // 순간 최고속도 (km/h)
	private double slowestKmh = 9999999; // 순간 최저속도 (km/h)
	private double currentSpeed; // 현재 순간 속도

	private ArrayList<Double> latitudes = new ArrayList<>(); // 모든 위도 좌표
	private ArrayList<Double> longitudes = new ArrayList<>(); // 모든 경도 좌표
	private ArrayList<Long> times = new ArrayList<>(); // 각 좌표당 시간 기록 (밀리 세컨드)

	public Record()
	{
	}

	public Record(String name, double moved, double consumed, double elapsed)
	{
		this.elapsed = elapsed;
		Calendar today = Calendar.getInstance();

		this.name = name;
		this.date = String.format(Locale.KOREAN, "%04d-%02d-%02d  %d시 %d분",
				today.get(Calendar.YEAR), today.get(Calendar.MONTH) + 1, today.get(Calendar.DAY_OF_MONTH),
				today.get(Calendar.HOUR_OF_DAY), today.get(Calendar.MINUTE));
		this.moved = moved;
		this.consumed = consumed;
	}

	public void save(String recordName)
	{
		// TODO 총 소모 칼로리, 총 이동거리, 순간 최고·최저속도, 기록 날짜 계산해서 데이터베이스에 저장해야 함
		id = UUID.randomUUID();
		name = recordName;

		Calendar today = Calendar.getInstance();
		date = String.format(Locale.KOREAN, "%04d-%02d-%02d  %d시 %d분",
				today.get(Calendar.YEAR), today.get(Calendar.MONTH) + 1, today.get(Calendar.DAY_OF_MONTH),
				today.get(Calendar.HOUR_OF_DAY), today.get(Calendar.MINUTE));
	}

	public void addPoint(MapPoint point)
	{
		latitudes.add(point.getMapPointGeoCoord().latitude);
		longitudes.add(point.getMapPointGeoCoord().longitude);
		times.add(System.currentTimeMillis());

		// 총 이동거리 계산
		if (times.size() > 1)
		{
			int lastIndex = times.size() - 1;

			double latitude_km = Math.abs(latitudes.get(lastIndex - 1) - latitudes.get(lastIndex)) * 114.64;
			double longitude_km = Math.abs(longitudes.get(lastIndex - 1) - longitudes.get(lastIndex)) * 88;
			long timeDiff = Math.abs(times.get(lastIndex - 1) - times.get(lastIndex)) / 1000;

			double distance = Math.sqrt(latitude_km * latitude_km + longitude_km * longitude_km);
			moved += distance;

			// 순간 속도(km/h) 계산
			currentSpeed = distance / (timeDiff / 3600.0);

			// 순간 최고·최저 속도 계산
			if (currentSpeed > fastestKmh)
				fastestKmh = currentSpeed;

			if (currentSpeed < slowestKmh)
				slowestKmh = currentSpeed;

			// 총 경과 시간 계산
			elapsed = (times.get(times.size() - 1) - times.get(0)) / 1000.0 / 60;

			// 소모 칼로리 계산
			// 체중, 달리기 속도와, 시간당 소모 칼로리의 관계식: kcal = speed*(22.49+2.5*(weight-45)/5) * 2
			double speed = moved / (elapsed / 60);
			consumed = speed * (22.49 + 2.5 * (Pref.weight - 45) / 5) * 2 * (elapsed / 60);
		}
	}

	/**
	 * 기록을 삭제하는 메서드
	 */
	public void discard()
	{
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getDate()
	{
		return date;
	}

	public void setDate(String date)
	{
		this.date = date;
	}

	public double getMoved()
	{
		return moved;
	}

	public void setMoved(double moved)
	{
		this.moved = moved;
	}

	public double getConsumed()
	{
		return consumed;
	}

	public void setConsumed(double consumed)
	{
		this.consumed = consumed;
	}

	public double getElapsed()
	{
		return elapsed;
	}

	public void setElapsed(double elapsed)
	{
		this.elapsed = elapsed;
	}

	public double getCurrentSpeed()
	{
		return currentSpeed;
	}

	public double getFastest()
	{
		return fastestKmh;
	}

	public double getSlowest()
	{
		return slowestKmh;
	}
}