package cn.edu.fudan.cs12.coderrun.fragment;

import android.animation.Animator;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Animatable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;

import java.util.Date;

import cn.edu.fudan.cs12.coderrun.Config;
import cn.edu.fudan.cs12.coderrun.R;
import cn.edu.fudan.cs12.coderrun.action.RunAction;
import cn.edu.fudan.cs12.coderrun.action.UserAction;
import cn.edu.fudan.cs12.coderrun.entity.User;
import cn.edu.fudan.cs12.coderrun.event.ProfileEvent;
import cn.edu.fudan.cs12.coderrun.event.RunEvent;
import cn.edu.fudan.cs12.coderrun.provider.BusProvider;
import de.halfbit.tinybus.Subscribe;
import mehdi.sakout.fancybuttons.FancyButton;


public class RunFragment extends Fragment {
	User user;
	ImageView mRunImage;
	FancyButton mRunButton;
	FancyButton mPauseButton;
	FancyButton mStopButton;
    Chronometer ch;  //计时器
	TextView distanceText;//记录距离的文本框
	TextView speedText;//记录速度的文本框
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		user = UserAction.getCurrentUser();
	}

	@Override
	public void onResume() {
		super.onResume();
		BusProvider.getInstance().register(this);
	}

	@Override
	public void onPause() {
		super.onPause();
		BusProvider.getInstance().unregister(this);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		user = UserAction.getCurrentUser();
		View v = inflater.inflate(R.layout.fragment_run, container, false);

		mRunButton = (FancyButton) v.findViewById(R.id.button_run);
		mPauseButton = (FancyButton) v.findViewById(R.id.button_pause);
		mStopButton = (FancyButton) v.findViewById(R.id.button_stop);
		mRunImage = (ImageView) v.findViewById(R.id.image_run);
		//jiao adds on 2015/11/29
		ch = (Chronometer) v.findViewById(R.id.chronometer1);
		ch.setFormat("时长：%s");
		distanceText=(TextView)v.findViewById(R.id.distance_value);
        speedText=(TextView)v.findViewById(R.id.speed_value);

		mRunButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ch.setBase(SystemClock.elapsedRealtime());
				ch.setFormat("时长：%s");
				ch.start();

				Animation anim = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_right_out);
				anim.setAnimationListener(new Animation.AnimationListener() {
					@Override
					public void onAnimationStart(Animation animation) {}
					@Override
					public void onAnimationRepeat(Animation animation) {}
					@Override
					public void onAnimationEnd(Animation animation) {
						mRunImage.setVisibility(View.GONE);
						Toast.makeText(getActivity(), "run", Toast.LENGTH_SHORT).show();
					}
				});
				mRunImage.startAnimation(anim);
				//jiao adds on 2015/11/29
				final LocationManager manager=(LocationManager)getActivity().getSystemService(Context.LOCATION_SERVICE);
				StringBuilder recordLocation=new StringBuilder();
				final Location initialLocation=manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
				manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, new LocationListener() {
					@Override
					public void onLocationChanged(Location location) {
						Location lastLocation = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
						float distance[] = new float[1];
						Location.distanceBetween(initialLocation.getLatitude(), initialLocation.getLongitude(), lastLocation.getLatitude(), lastLocation.getLongitude(), distance);
						//TextView distanceText=(TextView)findViewById(R.id.distance_value);
						//distanceText.setTextSize(16);
						distanceText.setText(String.valueOf(distance[0]) + "米");
						float totalTime =( SystemClock.elapsedRealtime() - ch.getBase())/1000; //使单位为秒
						float speedValue = distance[0] / totalTime;
						//speedText.setTextSize(16);
						speedText.setText(String.valueOf(speedValue) + "米/s");
						//distanceBundle.putString("distance", String.valueOf(distance[0]) + "公里");
					}

					@Override
					public void onStatusChanged(String provider, int status, Bundle extras) {

					}

					@Override
					public void onProviderEnabled(String provider) {

					}

					@Override
					public void onProviderDisabled(String provider) {

					}
				});


			}
		});
		mPauseButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ch.stop();
				Toast.makeText(getActivity(), "pause", Toast.LENGTH_SHORT).show();

			}
		});
		mStopButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ch.setBase(SystemClock.elapsedRealtime());
				//ch.setTextSize(16);
				ch.setFormat("时长：%s");
				ch.stop();

				Toast.makeText(getActivity(), "stop", Toast.LENGTH_SHORT).show();

			}
		});
		return v;
	}

}


