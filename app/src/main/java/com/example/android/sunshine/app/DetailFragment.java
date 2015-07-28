package com.example.android.sunshine.app;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.sunshine.app.data.WeatherContract;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = DetailFragment.class.getSimpleName();

    private static final int DETAILS_LOADER = 0;

    public static final String DETAIL_URI = "DURI";



    private static final String[] FORECASE_COLUMNS = {
            WeatherContract.WeatherEntry.TABLE_NAME + "." + WeatherContract.WeatherEntry._ID
            , WeatherContract.WeatherEntry.COLUMN_DATE
            , WeatherContract.WeatherEntry.COLUMN_SHORT_DESC
            , WeatherContract.WeatherEntry.COLUMN_MAX_TEMP
            , WeatherContract.WeatherEntry.COLUMN_MIN_TEMP
            , WeatherContract.WeatherEntry.COLUMN_HUMIDITY
            , WeatherContract.WeatherEntry.COLUMN_WIND_SPEED
            , WeatherContract.WeatherEntry.COLUMN_DEGREES
            , WeatherContract.WeatherEntry.COLUMN_PRESSURE
            , WeatherContract.WeatherEntry.COLUMN_WEATHER_ID

    };

    private static final int COL_WEATHER_ID = 0;
    private static final int COL_WEATHER_DATE = 1;
    private static final int COL_WEATHER_DESC = 2;
    private static final int COL_WEATHER_MAX_TEMP = 3;
    private static final int COL_WEATHER_MIN_TEMP = 4;
    private static final int COL_WEATHER_HUMIDITY = 5;
    private static final int COL_WEATHER_WIND_SPEED = 6;
    private static final int COL_WEATHER_DEGREES = 7;
    private static final int COL_WEATHER_PRESSURE = 8;
    private static final int COL_WEATHER_CONDITION_ID = 9;

    private static final String FORECAST_SHARE_HASHTAG = " #SunshineApp";

    private String mForecastStr;
    private ShareActionProvider mShareActionProvider;

    private TextView mDateView;
    private ImageView mIconView;
    private TextView mFriendlyDateView;
    private TextView mDescView;
    private TextView mHighView;
    private TextView mLowView;
    private TextView mHumidView;
    private TextView mWindView;
    private TextView mPressureView;
    private Uri mUri;

    public static DetailFragment newInstance(Uri detailUri)
    {
        Bundle args = new Bundle();
        args.putParcelable(DETAIL_URI, detailUri);
        DetailFragment fragment = new DetailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(DETAILS_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.v(LOG_TAG, "In onCreateLoader");

        if(mUri != null)
        {
            return new CursorLoader(
                    getActivity()
                    ,mUri
                    ,FORECASE_COLUMNS
                    ,null
                    ,null
                    ,null
            );
        }
        return null;

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if(!data.moveToFirst()) { return; }

        long date = data.getLong(COL_WEATHER_DATE);
        String formattedDate = Utility.formatDate(date);
        mDateView.setText(formattedDate);
        mFriendlyDateView.setText(Utility.getFriendlyDayString(getActivity(), date));

        String desc = data.getString(COL_WEATHER_DESC);
        mDescView.setText(desc);
        boolean isMetric = Utility.isMetric(getActivity());
        String minTemp = Utility.formatTemperature(getActivity(), data.getDouble(COL_WEATHER_MIN_TEMP), isMetric);
        mLowView.setText(minTemp);
        String maxTemp = Utility.formatTemperature(getActivity(), data.getDouble(COL_WEATHER_MAX_TEMP), isMetric);
        mHighView.setText(maxTemp);

        mWindView.setText(Utility.getFormattedWind(getActivity(), data.getFloat(COL_WEATHER_WIND_SPEED), data.getFloat(COL_WEATHER_DEGREES)));

        float humidity = data.getFloat(COL_WEATHER_HUMIDITY);
        mHumidView.setText(getActivity().getString(R.string.format_humidity, humidity));

        float pressure = data.getFloat(COL_WEATHER_PRESSURE);
        mPressureView.setText(getActivity().getString(R.string.format_pressure, pressure));

        mIconView.setImageResource(Utility.getArtResourceForWeatherCondition(data.getInt(COL_WEATHER_CONDITION_ID)));

        mForecastStr = String.format("%s - %s - %s/%s", formattedDate, desc, maxTemp, minTemp);

        if(mShareActionProvider != null)
        {
            mShareActionProvider.setShareIntent(createShareForecastIntent());
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }



    public DetailFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle args = getArguments();
        if(args != null)
        {
            mUri = args.getParcelable(DETAIL_URI);
        }
        View view = inflater.inflate(R.layout.fragment_detail, container, false);
        mIconView = (ImageView) view.findViewById(R.id.detail_icon);
        mDateView = (TextView) view.findViewById(R.id.detail_date_textview);
        mFriendlyDateView = (TextView) view.findViewById(R.id.detail_day_textview);
        mDescView = (TextView) view.findViewById(R.id.detail_forecast_textview);
        mHighView = (TextView) view.findViewById(R.id.detail_high_textview);
        mLowView = (TextView) view.findViewById(R.id.detail_low_textview);
        mHumidView = (TextView) view.findViewById(R.id.detail_humidity_textview);
        mWindView = (TextView) view.findViewById(R.id.detail_wind_textview);
        mPressureView = (TextView) view.findViewById(R.id.detail_pressure_textview);
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.detailfragment, menu);

        // Retrieve the share menu item
        MenuItem menuItem = menu.findItem(R.id.action_share);

        // Get the provider and hold onto it to set/change the share intent.
        mShareActionProvider =
                (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

        // Attach an intent to this ShareActionProvider.  You can update this at any time,
        // like when the user selects a new piece of data they might like to share.
        if (mForecastStr != null ) {
            mShareActionProvider.setShareIntent(createShareForecastIntent());
        }

        if(mShareActionProvider != null)
        {
            mShareActionProvider.setShareIntent(createShareForecastIntent());
        }
    }

    public void onLocationChanged(String newLocation)
    {
        Uri uri = mUri;
        if(mUri != null)
        {
            long date = WeatherContract.WeatherEntry.getDateFromUri(uri);
            mUri = WeatherContract.WeatherEntry.buildWeatherLocationWithDate(newLocation, date);
            getLoaderManager().restartLoader(DETAILS_LOADER, null, this);
        }
    }

    private Intent createShareForecastIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT,
                mForecastStr + FORECAST_SHARE_HASHTAG);
        return shareIntent;
    }
}
