package de.halfreal.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.http.GET;
import retrofit.http.Path;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import de.halfreal.fragment.AutoCompleteEditField;
import de.halfreal.fragment.AutoCompleteEditField.TextChangeListener;

public class GoEuroSuggestion implements TextChangeListener {

	public static interface GoEuroSuggestionService {

		@GET("/suggest/position/en/name/{query}")
		Result listPositions(@Path("query") String query);

	}

	private static String API_URL = "http://pre.dev.goeuro.de:12345/api/v1/";
	private LocationManager locationManager;

	public GoEuroSuggestion(LocationManager locationManager) {
		this.locationManager = locationManager;

	}

	private float distanceToCurrentLocation(GeoPosition location) {
		GeoPosition currentLocation = getCurrentGeoLocation();

		if (currentLocation == null || location == null) {
			return -1;
		}

		float[] results = new float[1];
		Location.distanceBetween(location.getLatitude(),
				location.getLongitude(), currentLocation.getLatitude(),
				currentLocation.getLongitude(), results);

		return results[0] / 1000f;
	}

	private void fetchSuggestions(final AutoCompleteEditField field,
			final String newText) {
		new AsyncTask<String, Void, List<Position>>() {

			@Override
			protected List<Position> doInBackground(String... params) {
				if (params != null && params[0] != null) {
					try {
						Result listPositions = getService().listPositions(
								params[0]);
						if (listPositions != null) {
							return listPositions.getResults();
						}
					} catch (RetrofitError retrofitError) {
						System.err.println(retrofitError.getResponse()
								.getReason());
					}
				}
				return null;

			}

			@Override
			protected void onPostExecute(List<Position> result) {
				super.onPostExecute(result);
				if (result != null) {
					Collections.sort(result, new Comparator<Position>() {

						public int compare(Position lhs, Position rhs) {
							float lhsDistance = distanceToCurrentLocation(lhs
									.getGeoPosition());
							float rhsDistance = distanceToCurrentLocation(rhs
									.getGeoPosition());

							return Float.compare(lhsDistance, rhsDistance);
						}

					});

					field.updateSuggestions(toNameList(result));

				}
			}

		}.execute(newText);
	}

	private GeoPosition getCurrentGeoLocation() {

		Location lastKnownLocation = locationManager
				.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
		if (lastKnownLocation != null) {
			GeoPosition geoPosition = new GeoPosition();
			geoPosition.setLatitude(lastKnownLocation.getLatitude());
			geoPosition.setLongitude(lastKnownLocation.getLongitude());
			return geoPosition;
		}

		return null;
	}

	protected GoEuroSuggestionService getService() {
		RestAdapter restAdapter = new RestAdapter.Builder().setServer(API_URL)
				.build();

		GoEuroSuggestionService goEuroService = restAdapter
				.create(GoEuroSuggestionService.class);
		return goEuroService;
	}

	public void textChanged(final AutoCompleteEditField field,
			final String newText) {

		fetchSuggestions(field, newText);

	}

	protected List<String> toNameList(List<Position> result) {

		ArrayList<String> names = new ArrayList<String>();
		for (Position position : result) {
			names.add(position.getName());
		}

		return names;
	}
}
