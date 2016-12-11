package app.boxresin.runninghealthapp;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

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
		binding.listRecord.setAdapter(adapter);
		binding.listRecord.setOnItemClickListener(this);
		Settings.get().setRecordAdapter(adapter);

		return binding.getRoot();
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