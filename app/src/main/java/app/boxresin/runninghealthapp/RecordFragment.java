package app.boxresin.runninghealthapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;

import java.util.Locale;

import app.boxresin.runninghealthapp.databinding.FragmentRecordBinding;
import app.boxresin.runninghealthapp.databinding.ItemRecordBinding;
import data.Record;
import global.Settings;

/**
 * 기록 프래그먼트
 */
public class RecordFragment extends Fragment implements AdapterView.OnItemClickListener
{
	FragmentRecordBinding binding;
	private ArrayAdapter<Record> adapter;

	@Nullable
	@Override
	public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
	{
		binding = DataBindingUtil.inflate(inflater, R.layout.fragment_record, container, false);

		// 리스트뷰 및 어댑터를 초기화한다.
		adapter = new ArrayAdapter<Record>(getContext(), R.layout.item_record)
		{
			@NonNull
			@Override
			public View getView(int position, View convertView, ViewGroup parent)
			{
				ItemRecordBinding binding = null;

				if (convertView == null)
				{
					binding = DataBindingUtil.inflate(inflater, R.layout.item_record, parent, false);
					convertView = binding.getRoot();
				}
				else
					binding = DataBindingUtil.getBinding(convertView);

				Record data = getItem(position);
				binding.txtRecordName.setText(data.getName());
				binding.txtDate.setText(data.getDate() + " 종료");
				binding.txtElasped.setText(String.format(Locale.KOREAN, "%.1f분 동안", data.getElapsed()));
				binding.txtMoved.setText(String.format(Locale.KOREAN, "총 %.2f ㎞ 이동", data.getMoved()));
				binding.txtConsumed.setText(String.format(Locale.KOREAN, "총 %.2f ㎉ 소모", data.getConsumed()));

				return convertView;
			}
		};
		adapter.addAll(Record.readAll());
		binding.listRecord.setAdapter(adapter);
		binding.listRecord.setOnItemClickListener(this);
		registerForContextMenu(binding.listRecord);
		Settings.get().setRecordAdapter(adapter);

		return binding.getRoot();
	}

	/**
	 * 리스트뷰 아이템을 롱 클릭할 때 나타나는 메뉴를 생성하는 메서드
	 */
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo)
	{
		super.onCreateContextMenu(menu, v, menuInfo);
		if (v.getId() == R.id.list_record)
		{
			MenuInflater inflater = getActivity().getMenuInflater();
			inflater.inflate(R.menu.list_record, menu);
		}
	}

	@Override
	public boolean onContextItemSelected(MenuItem item)
	{
		final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

		switch (item.getItemId())
		{
		case R.id.action_change_name: // 이름 수정 메뉴
			final EditText input = new EditText(getContext());
			LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.MATCH_PARENT,
					LinearLayout.LayoutParams.MATCH_PARENT);
			input.setSingleLine(true);
			input.setHint("수정할 이름 입력");
			input.setLayoutParams(layoutParams);

			// 이름 수정 대화상자를 띄운다.
			new AlertDialog.Builder(getContext())
					.setTitle("이름 수정")
					.setView(input)
					.setPositiveButton("수정", new DialogInterface.OnClickListener()
					{
						@Override
						public void onClick(DialogInterface dialog, int which)
						{
							if (!input.getText().toString().equals(""))
							{
								// 기록의 이름을 수정한다.
								Record record = adapter.getItem(info.position);
								record.setName(input.getText().toString());
							}
						}
					})
					.setCancelable(false)
					.setNegativeButton("취소", null)
					.show();

			return true;

		case R.id.action_delete: // 삭제 메뉴
			// 해당 기록을 삭제한다.
			Record record = adapter.getItem(info.position);
			adapter.remove(record);
			record.discard();
			return true;
		}
		return super.onContextItemSelected(item);
	}

	/**
	 * 사용자가 기록 항목을 클릭했을 때 호출되는 메서드
	 */
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id)
	{
		// 기록 데이터를 가져오고 기록 뷰어 액티비티로 넘긴다.
		Record record = adapter.getItem(position);
		if (record != null)
		{
			Settings.get().currentRecord = record;
			startActivity(new Intent(getActivity(), RecordViewerActivity.class));
		}
	}
}