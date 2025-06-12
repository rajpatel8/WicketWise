package com.lords.becomebetter;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class TextAnnotationDialog extends Dialog {

    private EditText annotationText;
    private Button saveBtn, cancelBtn;
    private TextView timeStampText;

    private String currentTimeStamp;
    private OnAnnotationSavedListener listener;

    public interface OnAnnotationSavedListener {
        void onAnnotationSaved(String text, String timeStamp);
    }

    public TextAnnotationDialog(Context context, String timeStamp, OnAnnotationSavedListener listener) {
        super(context);
        this.currentTimeStamp = timeStamp;
        this.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_text_annotation);

        initializeViews();
        setupClickListeners();
    }

    private void initializeViews() {
        annotationText = findViewById(R.id.annotationTextEdit);
        saveBtn = findViewById(R.id.saveAnnotationBtn);
        cancelBtn = findViewById(R.id.cancelAnnotationBtn);
        timeStampText = findViewById(R.id.timeStampText);

        timeStampText.setText("Add annotation at: " + currentTimeStamp);

        // Set dialog properties
        setTitle("Add Text Annotation");
        setCancelable(true);
    }

    private void setupClickListeners() {
        saveBtn.setOnClickListener(v -> {
            String text = annotationText.getText().toString().trim();
            if (!text.isEmpty() && listener != null) {
                listener.onAnnotationSaved(text, currentTimeStamp);
                dismiss();
            }
        });

        cancelBtn.setOnClickListener(v -> dismiss());
    }
}