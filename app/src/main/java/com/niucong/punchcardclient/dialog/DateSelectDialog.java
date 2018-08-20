package com.niucong.punchcardclient.dialog;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by think on 2017/7/22.
 */

public class DateSelectDialog {
    /**
     * 选择日期
     */
    public static void selectDate(Context context, final TextView textView) {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                String month = "" + (monthOfYear + 1);
                if (monthOfYear < 9) {
                    month = "0" + month;
                }
                String day = "" + dayOfMonth;
                if (dayOfMonth < 10) {
                    day = "0" + day;
                }
                textView.setText(year + "-" + month + "-" + day);
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    private static String dateTimeStr;

    /**
     * 选择日期时间
     */
    public static void selectDateTime(final Context context, final TextView textView) {
        Calendar c = Calendar.getInstance();
        try {
            c.setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(textView.getText().toString().trim()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        final TimePickerDialog timePickerDialog = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {

            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                StringBuffer timeBuffer = new StringBuffer("");
                if (hourOfDay < 10) {
                    timeBuffer.append("0");
                }
                timeBuffer.append(hourOfDay + ":");
                if (minute < 10) {
                    timeBuffer.append("0");
                }
                timeBuffer.append(minute);
                textView.setText(dateTimeStr + timeBuffer);
            }
        }, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true);

        DatePickerDialog datePickerDialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                dateTimeStr = "";
                dateTimeStr += year + "-";
                if (monthOfYear < 9) {
                    dateTimeStr += "0";
                }
                dateTimeStr += (monthOfYear + 1) + "-";
                if (dayOfMonth < 10) {
                    dateTimeStr += "0";
                }
                dateTimeStr += dayOfMonth + " ";
                if (!timePickerDialog.isShowing()) {
                    timePickerDialog.show();
                }
            }
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }
}
