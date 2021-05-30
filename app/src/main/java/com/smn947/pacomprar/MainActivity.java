package com.smn947.pacomprar;

import android.app.*;
import android.content.*;
import android.os.*;
import android.util.*;
import android.view.*;
import android.widget.*;
import java.io.*;
import java.security.*;
import java.util.*;
import okhttp3.*;
import org.json.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;



public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);

		super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
		
		ToggleButton toggleAlarm = findViewById(R.id.pendbtn);
		toggleAlarm.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					if(isChecked)
					{
						Log.d("alarmCheck","ALARM SET TO TRUE");
						//sched.setAlarm(true);
					}
					else
					{
						Log.d("alarmCheck","ALARM SET TO FALSE");
						//sched.setAlarm(false);
					}

				}
			});

		start();
		
		
	}

	private class StableArrayAdapter extends ArrayAdapter<String> {

		HashMap<String, Integer> mIdMap = new HashMap<String, Integer>();

		public StableArrayAdapter(Context context, int textViewResourceId,
								  List<String> objects) {
			super(context, textViewResourceId, objects);
			for (int i = 0; i < objects.size(); ++i)
			{
				mIdMap.put(objects.get(i), i);
			}
		}

		@Override
		public long getItemId(int position) {
			String item = getItem(position);
			return mIdMap.get(item);
		}

		@Override
		public boolean hasStableIds() {
			return true;
		}

	}
	
	public Request buildReq(RequestBody body) {
		Request request = new Request.Builder()
			.url("https://smn947.com.co/API/index.php?t=r")
			.build();
		return request;
	}
	public void start() {
		OkHttpClient client = new OkHttpClient();
		MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
		RequestBody body = RequestBody.create(mediaType, "uid=test1%2f26&cell=0");
		
		Request request = buildReq(body);
		client.newCall(request).enqueue(new Callback() {
				@Override
				public void onFailure(Call call, IOException e) {
					e.printStackTrace();
				}
				@Override
				public void onResponse(Call call, Response response) throws IOException {
					if (response.isSuccessful()) {
						final String myRes = response.body().string();

						runOnUiThread(new Runnable() {
								@Override
								public void run() {
									Log.d("La respuesta =>", myRes);
									try {
										JSONObject info = new JSONObject(myRes);
										JSONArray pend = new JSONArray(info.get("pendientes").toString());
										
										ArrayList<String> list = new ArrayList<String>();
										for (int i=0; i < pend.length(); i++) {
											JSONObject u = pend.getJSONObject(i);
											String med = u.get("medida").toString();
											String cant = u.get("cantidad").toString();
											String nomb = u.get("nombre").toString();
											list.add(cant + " " + med + "(s) de " + nomb);
											Log.d("ObjetoParseado", "Pos: " + i + " - " + u.toString());

										}
										renderList(list);
									}
									catch (JSONException e)
									{}
								}
							});
					}
				}

			});
	}
	public void renderList(final ArrayList<String> list) {
		final ListView listview = findViewById(R.id.mylist);
		final StableArrayAdapter adapter = new StableArrayAdapter(this,
																  android.R.layout.simple_list_item_1, list);
		listview.setAdapter(adapter);

		listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, final View view,
										int position, long id) {
					final String item = (String) parent.getItemAtPosition(position);
					view.animate().setDuration(2000).alpha(0)
						.withEndAction(new Runnable() {
							@Override
							public void run() {
								list.remove(item);
								adapter.notifyDataSetChanged();
								view.setAlpha(1);
							}
						});
				}

			});
	}
}
