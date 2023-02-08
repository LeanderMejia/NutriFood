package com.dpio.nutrifood.Miscellaneous;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;

import androidx.core.content.ContextCompat;

import com.dpio.nutrifood.R;

public class IngrEditText extends androidx.appcompat.widget.AppCompatEditText {

    public Rect rect = new Rect();
    public Paint paint = new Paint();
    private IngrEditText mealIngredientsInput = findViewById(R.id.mealIngredientsInput);
    private boolean isTyping;

    public IngrEditText(Context context, AttributeSet attrs) {
        super(context, attrs);

        paint.setStyle(Paint.Style.FILL);
        int color = ContextCompat.getColor(getContext(), R.color.light);
        paint.setColor(color);
        int spSize = 16;
        float scaledSizeInPixels = spSize * getResources().getDisplayMetrics().scaledDensity;
        paint.setTextSize(scaledSizeInPixels);

        mealIngredientsInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
                if (text.length() == 0) {
                    isTyping = false;
                    return;
                }

                // Execute when user try '\n' in the empty line for the line 1. "\n\n" means the user click the new line twice
                String newText = text.toString().replace("\n\n","\n");
                if(newText.charAt(0) == '\n') {
                    newText = newText.substring(1);
                }
                // Execute when user try '\n' in the empty line for the line 2...
                if(!text.toString().equals(newText)) {
                    setText(newText);
                    mealIngredientsInput.setSelection(mealIngredientsInput.getText().length());
                    return;
                }
                isTyping = true;

            }

            @Override
            public void beforeTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (isTyping) {
            int baseline = getBaseline();
            for (int i = 0; i < getLineCount(); i++) {
                canvas.drawText("  •", rect.left, baseline, paint);
                baseline += getLineHeight();
            }
        }
        super.onDraw(canvas);
    }
}
