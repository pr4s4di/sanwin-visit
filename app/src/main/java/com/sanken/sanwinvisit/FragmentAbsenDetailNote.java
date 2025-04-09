package com.sanken.sanwinvisit;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.asynctasks.AsyncTaskBackground;
import com.enums.EnumAsyncTask;
import com.interfaces.IFragmentAbsenDetailNote;
import com.presenter.PresenterFragmentAbsenDetailNote;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentAbsenDetailNote extends Fragment implements IFragmentAbsenDetailNote.IView {

    private String idKunjungan;
    private String kodeUser;
    private IFragmentAbsenDetailNote.IPresenter presenter;
    private TextInputEditText editTextNote1;
    private TextInputEditText editTextNote2;
    private TextInputEditText editTextNote3;
    private Button btnSaveNote;

    private boolean isEdit;

    public FragmentAbsenDetailNote() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_absen_detail_note, container, false);
        initializeComponent(view);
        return view;
    }

    private void initializeComponent(View view) {
        idKunjungan = getArguments().getString("idkunjungan");
        kodeUser = getArguments().getString("kduser");
        presenter = new PresenterFragmentAbsenDetailNote(this);
        editTextNote1 = view.findViewById(R.id.editTextNote1);
        editTextNote2 = view.findViewById(R.id.editTextNote2);
        editTextNote3 = view.findViewById(R.id.editTextNote3);
        btnSaveNote = view.findViewById(R.id.btnSaveNote);
        btnSaveNote.setOnClickListener(this);

        doAsyncTaskWork(EnumAsyncTask.GET_ABSEN_DATA);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btnSaveNote) {
            doValidateInput();
        }
    }

    private void doValidateInput() {
        boolean isTextNote1Empty = editTextNote1.getText().toString().trim().isEmpty();
        boolean isTextNote2Empty = editTextNote2.getText().toString().trim().isEmpty();
        boolean isTextNote3Empty = editTextNote3.getText().toString().trim().isEmpty();
        if (isTextNote1Empty && isTextNote2Empty && isTextNote3Empty) {
            editTextNote1.setError("Harap input salah satu note");
            editTextNote2.setError("Harap input salah satu note");
            editTextNote3.setError("Harap input salah satu note");
            return;
        } else {
            editTextNote1.setError(null);
            editTextNote2.setError(null);
            editTextNote3.setError(null);
        }

        if (isEdit) {
            doAsyncTaskWork(EnumAsyncTask.DO_UPDATE_ABSEN);
        } else {
            doAsyncTaskWork(EnumAsyncTask.DO_SAVE_ABSEN);
        }
    }

    private void doAsyncTaskWork(EnumAsyncTask enumAsyncTask) {
        AsyncTaskBackground asyncTaskBackground = new AsyncTaskBackground();
        asyncTaskBackground.setEnumAsyncTask(enumAsyncTask);
        asyncTaskBackground.setListener(this);
        asyncTaskBackground.execute();
    }

    @Override
    public void onPreExecute(EnumAsyncTask enumAsyncTask) {

    }

    @Override
    public void onPostExecute(EnumAsyncTask enumAsyncTask, String result) {
        switch (enumAsyncTask) {
            case GET_ABSEN_DATA:
                fetchDataToEditText(result);
                break;
            case DO_SAVE_ABSEN:
                showDialogResult(result);
                break;
            case DO_UPDATE_ABSEN:
                showDialogResult(result);
                break;
        }

    }

    private void fetchDataToEditText(String result) {
        try {
            JSONObject jsonObject = new JSONObject(result);
            boolean resultBoolean = jsonObject.getBoolean("result");
            if (resultBoolean) {
                JSONArray jsonArray = jsonObject.getJSONArray("message");
                if (jsonArray.length() > 0) {
                    String note1 = jsonArray.getJSONObject(0).getString("note1");
                    String note2 = jsonArray.getJSONObject(0).getString("note2");
                    String note3 = jsonArray.getJSONObject(0).getString("note3");
                    editTextNote1.setText(note1);
                    editTextNote2.setText(note2);
                    editTextNote3.setText(note3);
                    isEdit = true;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void showDialogResult(String result) {
        try {
            JSONObject jsonObject = new JSONObject(result);
            boolean resultBoolean = jsonObject.getBoolean("result");
            if (resultBoolean) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Sukses");
                builder.setMessage(jsonObject.getString("message"));
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        getActivity().finish();
                    }
                });
                builder.show();
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Gagal");
                builder.setMessage(jsonObject.getString("message"));
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.show();
                resetInputtedData();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void resetInputtedData() {
        editTextNote1.setText("");
        editTextNote2.setText("");
        editTextNote3.setText("");
    }

    @Override
    public String doInBackground(EnumAsyncTask enumAsyncTask) {
        String note1 = editTextNote1.getText().toString().trim();
        String note2 = editTextNote2.getText().toString().trim();
        String note3 = editTextNote3.getText().toString().trim();
        switch (enumAsyncTask) {
            case DO_SAVE_ABSEN:
                return presenter.doSaveAbsensiDetail(idKunjungan, note1, note2, note3, kodeUser);
            case GET_ABSEN_DATA:
                return presenter.getAbsensiDataDetail(idKunjungan);
            case DO_UPDATE_ABSEN:
                return presenter.doSaveAbsensiDetail(idKunjungan, note1, note2, note3, kodeUser);
        }
        return null;
    }

    @Override
    public void onProgressUpdate(EnumAsyncTask enumAsyncTask, Void... values) {

    }
}
