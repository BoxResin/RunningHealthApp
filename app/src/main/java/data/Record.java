package data;

import java.util.Calendar;
import java.util.Locale;

/**
 * 기록 클래스
 */
public class Record
{
	private String name; // 기록 이름
	private String date; // 기록 날짜 (운동을 끝낸 시간)
	private double moved; // 총 움직인 거리
	private double consumed; // 총 소비한 칼로리
	private double elapsed; // 총 운동 시간

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
}