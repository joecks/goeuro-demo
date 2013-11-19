package de.halfreal.activity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.EditText;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.Views;
import de.halfreal.fragment.AutoCompleteEditField;
import de.halfreal.fragment.AutoCompleteEditField.OnFinishedListener;
import de.halfreal.fragment.DatePickerFragment;
import de.halfreal.fragment.DatePickerFragment.OnDateSetListener;
import de.halfreal.goeuro.R;
import de.halfreal.model.GoEuroSuggestion;

public class SearchActivity extends Activity {

	AutoCompleteEditField autocompleteEditFieldDeparture;

	AutoCompleteEditField autocompleteEditFieldDestination;

	@InjectView(R.id.buttonPickDate)
	Button buttonPickDate;

	@InjectView(R.id.buttonStartSearch)
	Button buttonStartSearch;

	private SimpleDateFormat DD_MM_YYYY = new SimpleDateFormat("dd.MM.yyyy",
			Locale.GERMAN);

	@InjectView(R.id.editTextDate)
	EditText editTextDate;

	protected void checkButtonAvailabe() {
		String departure = autocompleteEditFieldDeparture.getInput();
		String destination = autocompleteEditFieldDestination.getInput();
		Editable text = editTextDate.getText();
		String date = null;
		if (text != null) {
			date = text.toString();
		}

		if (TextUtils.isEmpty(destination) || TextUtils.isEmpty(departure)
				|| TextUtils.isEmpty(date)) {
			buttonStartSearch.setEnabled(false);
		} else {
			buttonStartSearch.setEnabled(true);
		}
	}

	private void initViews() {

		buttonStartSearch.setEnabled(false);

		autocompleteEditFieldDeparture = (AutoCompleteEditField) getFragmentManager()
				.findFragmentById(R.id.autocompleteEditFieldDeparture);
		autocompleteEditFieldDestination = (AutoCompleteEditField) getFragmentManager()
				.findFragmentById(R.id.autocompleteEditFieldDestination);

		autocompleteEditFieldDeparture
				.setHint(getString(R.string.search_hint_departure));
		autocompleteEditFieldDestination
				.setHint(getString(R.string.search_hint_destination));

		LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		autocompleteEditFieldDeparture
				.setTextChangeListener(new GoEuroSuggestion(locationManager));
		autocompleteEditFieldDestination
				.setTextChangeListener(new GoEuroSuggestion(locationManager));

		OnFinishedListener checkButtonAvialble = new OnFinishedListener() {

			public void finished() {
				checkButtonAvailabe();
			}
		};
		autocompleteEditFieldDeparture
				.setOnFinishedListener(checkButtonAvialble);
		autocompleteEditFieldDestination
				.setOnFinishedListener(checkButtonAvialble);

		editTextDate.setOnFocusChangeListener(new OnFocusChangeListener() {

			public void onFocusChange(View v, boolean hasFocus) {
				checkButtonAvailabe();
			}
		});

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
		Views.inject(this);
		initViews();
	}

	@OnClick(R.id.buttonPickDate)
	protected void onPickDateClicked() {
		DatePickerFragment datePicker = new DatePickerFragment();
		datePicker.show(getFragmentManager(), "datePicker");
		datePicker.setOnDateSetListener(new OnDateSetListener() {

			public void onDateSet(Date date) {
				editTextDate.setText(DD_MM_YYYY.format(date));
			}
		});
	}

	@OnClick(R.id.buttonStartSearch)
	protected void onStartSearchClicked() {
		new AlertDialog.Builder(this)
				.setTitle(R.string.dialog_title_not_available)
				.setMessage(R.string.dialog_message_not_available)
				.setPositiveButton(R.string.dialog_button_ok, null).create()
				.show();

	}

}
