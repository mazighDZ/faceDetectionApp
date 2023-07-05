package com.section39.facerecognition;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class ResultDialog extends DialogFragment {
    Button btn;
    TextView textView;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    //link with layout
        View view = inflater.inflate(R.layout.fragment_resultdialog, container , false);
       String text = "";
        btn = view.findViewById(R.id.ok_bnt);
        textView = view.findViewById(R.id.dialog);

        //Getting the bundle:
        Bundle bundle = getArguments();
        // receive data that result from faceDetection
        text = bundle.getString("RESULT_TEXT");
        textView.setText(text);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
return view;
    }
}
