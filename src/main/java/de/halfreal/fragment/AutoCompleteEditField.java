package de.halfreal.fragment;

import java.util.List;

import android.app.Fragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import butterknife.InjectView;
import butterknife.Views;
import de.halfreal.goeuro.R;

public class AutoCompleteEditField extends Fragment {

	public static interface OnFinishedListener {
		void finished();
	}

	static class SimpleTextWatcher implements TextWatcher,
			OnItemSelectedListener {

		private AutoCompleteEditField parent;
		private boolean selected;
		private TextChangeListener textChangeListener;

		public SimpleTextWatcher(TextChangeListener textChangeListener,
				AutoCompleteEditField parent) {
			this.textChangeListener = textChangeListener;
			this.parent = parent;
		}

		public void afterTextChanged(Editable s) {

		}

		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {

		}

		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			selected = true;
		}

		public void onNothingSelected(AdapterView<?> arg0) {

		}

		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			if (textChangeListener != null && s != null && !selected) {
				textChangeListener.textChanged(parent, s.toString());
			}
			selected = false;
		}

	}

	public static interface TextChangeListener {

		void textChanged(AutoCompleteEditField field, String newText);
	}

	private ArrayAdapter<String> adapter;

	@InjectView(R.id.autoCompleteTextView)
	AutoCompleteTextView autoCompleteTextView;

	private String hint;

	private OnFinishedListener onFinishedListener;

	private TextChangeListener textChangeListener;

	public String getInput() {
		return autoCompleteTextView.getText() != null ? autoCompleteTextView
				.getText().toString() : null;
	}

	private void initView() {
		adapter = new ArrayAdapter<String>(getActivity(),
				android.R.layout.simple_dropdown_item_1line);
		SimpleTextWatcher simpleTextWatcher = new SimpleTextWatcher(
				new TextChangeListener() {

					public void textChanged(AutoCompleteEditField field,
							String newText) {
						if (textChangeListener != null) {
							textChangeListener.textChanged(field, newText);
						}
					}
				}, this);
		autoCompleteTextView.addTextChangedListener(simpleTextWatcher);

		autoCompleteTextView
				.setOnEditorActionListener(new OnEditorActionListener() {

					public boolean onEditorAction(TextView v, int actionId,
							KeyEvent event) {

						v.clearFocus();
						if (onFinishedListener != null) {
							onFinishedListener.finished();
						}

						return true;
					}
				});

		autoCompleteTextView.setOnItemSelectedListener(simpleTextWatcher);
		autoCompleteTextView.setAdapter(adapter);
		autoCompleteTextView.setHint(hint);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_autocomplete, container,
				false);
		Views.inject(this, view);
		initView();

		return view;
	}

	public void setHint(String hint) {
		this.hint = hint;
	}

	public void setOnFinishedListener(OnFinishedListener onFinishedListener) {
		this.onFinishedListener = onFinishedListener;
	}

	public void setTextChangeListener(TextChangeListener textChangeListener) {
		this.textChangeListener = textChangeListener;
	}

	public void updateSuggestions(List<String> suggestions) {
		adapter.clear();
		adapter.addAll(suggestions);
		adapter.notifyDataSetChanged();
	}

}
