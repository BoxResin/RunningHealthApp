package data;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import net.daum.mf.map.api.MapPoint;

import java.util.ArrayList;
import java.util.UUID;

/**
 * 운동 기록 데이터베이스를 관리하는 클래스
 */
public class RecordDatabase
{
	private DBHelper helper;

	@SuppressLint("DefaultLocale")
	void save(Record record)
	{
		SQLiteDatabase db = helper.getWritableDatabase();

		db.execSQL(String.format("INSERT INTO Record (id, name, date, moved, consumed, elapsed, fastest, slowest) VALUES (%s, %s, %s, %f, %f, %f, %f, %f);",
				record.getId(), record.getName(), record.getDate(), record.getMoved(), record.getConsumed(),
				record.getElapsed(), record.getFastest(), record.getSlowest()));

		for (int i = 0; i < record.getPointCount(); i++)
		{
			db.execSQL(String.format("INSRT INTO RecordPoint (id, latitude, longitude, time) VALUES (" +
					"%s, %f, %f, %d);", record.getId(), record.getLatitudes().get(i), record.getLongitudes().get(i), record.getTimes().get(i)));
		}

		db.close();
	}

	Record[] readAll()
	{
		SQLiteDatabase db = helper.getReadableDatabase();
		ArrayList<Record> records = new ArrayList<>();

		Cursor cursor = db.rawQuery("SELECT id, name, date, moved, consumed, elapsed, fastest, slowest FROM Record;", null);
		if (cursor.moveToFirst())
		{
			do
			{
				String uuid = cursor.getString(0);
				String name = cursor.getString(1);
				String date = cursor.getString(2);
				double moved = cursor.getDouble(3);
				double consumed = cursor.getDouble(4);
				double elapsed = cursor.getDouble(5);
				double fastest = cursor.getDouble(6);
				double slowest = cursor.getDouble(7);
				Record record = new Record(UUID.fromString(uuid), name, date, moved, consumed, elapsed, fastest, slowest);
				records.add(record);

				Cursor c = db.rawQuery("SELECT latitude, longitude, time FROM RecordPoint WHERE id = '" + uuid + "';", null);
				if (c.moveToFirst())
				{
					do
					{
						record.addPoint(MapPoint.mapPointWithGeoCoord(c.getDouble(0), c.getDouble(1)), c.getLong(2));
					} while (c.moveToNext());
				}
				c.close();
			} while (cursor.moveToNext());
		}

		cursor.close();
		db.close();
		return (Record[]) records.toArray();
	}

	private static class DBHelper extends SQLiteOpenHelper
	{
		public DBHelper(Context context)
		{
			super(context, "Local.db", null, 1);
		}

		@Override
		public void onCreate(SQLiteDatabase db)
		{
			db.execSQL("CREATE TABLE Record (_index INTEGER PRIMARY KEY AUTOINCREMENT, " +
					"id TEXT NOT NULL, name TEXT NOT NULL, date TEXT NOT NULL, moved REAL, consumed REAL, " +
					"elapsed REAL, fastest REAL, slowest REAL);");

			db.execSQL("CREATE TABLE RecordPoint (_index INTEGER PRIMARY KEY AUTOINCREMENT, id TEXT NOT NULL, " +
					"latitude REAL, longitude REAL, time INTEGER);");
		}
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
		{
			db.execSQL("DROP TABLE IF EXISTS Record");
			onCreate(db);
		}
	}

	private static RecordDatabase ourInstance = new RecordDatabase();

	public static void init(Context context)
	{
		ourInstance.helper = new DBHelper(context);
	}

	static RecordDatabase get()
	{
		return ourInstance;
	}

	private RecordDatabase()
	{
	}
}