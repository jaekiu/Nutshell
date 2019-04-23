package com.jackie.nutshell.Utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.jackie.nutshell.SingleInputDialogListener;

public class Utils {
    /**
     * Convert dp to px value.
     * -
     * Source: https://stackoverflow.com/a/6327095/2263329
     */
    public static int dpToPx(float dp, Resources resources) {
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.getDisplayMetrics());
        return (int) px;
    }

    /**
     * Show an AlertDialog with a single input box.
     *
     * @param context         Application context
     * @param title           Dialog title
     * @param message         Dialog input message/hint
     * @param inputType       InputType of EditText
     * @param positiveBtnText Dialog positive button text
     * @param negativeBtnText Dialog negative button text
     * @param listener        Dialog buttons click listener
     */
    public static void showSingleInputDialog(@NonNull final Context context, @NonNull final String title,
                                             @NonNull String message, int inputType, @NonNull String positiveBtnText,
                                             @NonNull String negativeBtnText, @NonNull final SingleInputDialogListener listener) {
        final AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setTitle(title);

        TextInputLayout textInputLayout = new TextInputLayout(context);

        final EditText input = new EditText(context);
        input.setHint(title);
        input.setSingleLine(true);
        input.setInputType(inputType);
        input.setHint(message);

        FrameLayout container = new FrameLayout(context);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        int left_margin = Utils.dpToPx(20, context.getResources());
        int top_margin = Utils.dpToPx(10, context.getResources());
        int right_margin = Utils.dpToPx(20, context.getResources());
        int bottom_margin = Utils.dpToPx(4, context.getResources());
        params.setMargins(left_margin, top_margin, right_margin, bottom_margin);

        textInputLayout.setLayoutParams(params);

        textInputLayout.addView(input);
        container.addView(textInputLayout);

        alert.setView(container);

        alert.setPositiveButton(positiveBtnText, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                listener.positiveCallback(input.getText().toString());

            }
        });

        alert.setNegativeButton(negativeBtnText,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        listener.negativeCallback();

                    }
                });

        alert.show();
    }

    private static boolean checkInput(Context c, String text, int num) {
        switch (num) {
            // 1 = LinkedIn
            case 1:
                if (!text.contains("linkedin.com")) {
                    Toast.makeText(c, "Invalid LinkedIn link!", Toast.LENGTH_SHORT);
                    return false;
                }
                return true;
            // 2 - Github
            case 2:
                if (!text.contains("github.com")) {
                    Toast.makeText(c, "Invalid Github link!", Toast.LENGTH_SHORT);
                    return false;
                }
                return true;
        }
        return false;
    }

}
