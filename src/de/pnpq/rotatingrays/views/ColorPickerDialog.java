package de.pnpq.rotatingrays.views;

import java.util.Locale;

import android.app.Dialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import de.pnpq.rotatingrays.R;
import de.pnpq.rotatingrays.preferences.ColorPickerPreference;

public class ColorPickerDialog extends Dialog implements ColorPickerView.OnColorChangedListener, View.OnClickListener
{
    private ColorPickerView mColorPicker;

    private ColorPickerPanelView mOldColor;
    private ColorPickerPanelView mNewColor;

    private EditText mHexVal;
    private ColorStateList mHexDefaultTextColor;
    private OnColorChangedListener mListener;

    public interface OnColorChangedListener
    {
        public void onColorChanged(int color);
    }

    public ColorPickerDialog(Context context, int initialColor)
    {
        super(context);
        init(initialColor);
    }

    private void init(int color)
    {
        // To fight color banding.
        getWindow().setFormat(PixelFormat.RGBA_8888);

        setUp(color);
    }

    private void setUp(int color)
    {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View layout = inflater.inflate(R.layout.colorpicker_preference_dialog, null);

        setContentView(layout);

        setTitle(R.string.choose_color);

        mColorPicker = (ColorPickerView) layout.findViewById(R.id.color_picker_view);
        mOldColor = (ColorPickerPanelView) layout.findViewById(R.id.old_color_panel);
        mNewColor = (ColorPickerPanelView) layout.findViewById(R.id.new_color_panel);

        mHexVal = (EditText) layout.findViewById(R.id.hex_val);
        mHexVal.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        mHexVal.setFilters(new InputFilter[] { new InputFilter.LengthFilter(7) });
        mHexDefaultTextColor = mHexVal.getTextColors();

        mHexVal.setOnEditorActionListener(new TextView.OnEditorActionListener()
        {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
            {
                if (actionId == EditorInfo.IME_ACTION_DONE)
                {
                    InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    String s = mHexVal.getText().toString();
                    try
                    {
                        int c = ColorPickerPreference.convertToColorInt(s.toString());
                        mColorPicker.setColor(c, true);
                        mHexVal.setTextColor(mHexDefaultTextColor);
                    }
                    catch (IllegalArgumentException e)
                    {
                        mHexVal.setTextColor(Color.RED);
                    }
                    return true;
                }
                return false;
            }
        });

        ((LinearLayout) mOldColor.getParent()).setPadding(Math.round(mColorPicker.getDrawingOffset()), 0, Math.round(mColorPicker.getDrawingOffset()), 0);

        mOldColor.setOnClickListener(this);
        mNewColor.setOnClickListener(this);
        mColorPicker.setOnColorChangedListener(this);
        mOldColor.setColor(color);
        mColorPicker.setColor(color, true);
    }

    @Override
    public void onColorChanged(int color)
    {
        mNewColor.setColor(color);

        mHexVal.setText(ColorPickerPreference.convertToRGB(color).toUpperCase(Locale.getDefault()));
        mHexVal.setTextColor(mHexDefaultTextColor);
    }

    /**
     * Set a OnColorChangedListener to get notified when the color selected by the user has changed.
     * 
     * @param listener
     */
    public void setOnColorChangedListener(OnColorChangedListener listener)
    {
        mListener = listener;
    }

    public int getColor()
    {
        return mColorPicker.getColor();
    }

    @Override
    public void onClick(View v)
    {
        if (v.getId() == R.id.new_color_panel)
        {
            if (mListener != null)
            {
                mListener.onColorChanged(mNewColor.getColor());
            }
        }
        dismiss();
    }

    @Override
    public Bundle onSaveInstanceState()
    {
        Bundle state = super.onSaveInstanceState();
        state.putInt("old_color", mOldColor.getColor());
        state.putInt("new_color", mNewColor.getColor());
        return state;
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState)
    {
        super.onRestoreInstanceState(savedInstanceState);
        mOldColor.setColor(savedInstanceState.getInt("old_color"));
        mColorPicker.setColor(savedInstanceState.getInt("new_color"), true);
    }
}
