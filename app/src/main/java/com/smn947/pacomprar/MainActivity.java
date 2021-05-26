package com.smn947.pacomprar;

import android.app.*;
import android.os.*;
import okhttp3.*;
import android.util.*;
import java.io.*;

public class MainActivity extends Activity 
{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);

		super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

		test();
    }
	public void test() {
		OkHttpClient cliente = new OkHttpClient();
		MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
		RequestBody body = RequestBody.create(mediaType, "uid=test1%2f26&cell=0");

		start(cliente, body);
	}
	public Request buildReq(RequestBody body) {
		Request request = new Request.Builder()
			.url("https://jsonplaceholder.typicode.com/users/1")
			.build();
		return request;
	}
	public void start(OkHttpClient client, RequestBody body) {
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
								}
							});
					}
				}

			});
	}
}
