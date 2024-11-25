package com.example.group_alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Calendar;

public class AlarmSetting extends AppCompatActivity {

    private RadioGroup radioGroup;
    private RadioButton rdoWeek, rdoDay;
    private Button btnMon, btnTue, btnWed, btnThu, btnFri, btnSat, btnSun;
    private TimePicker timePicker;
    private TextView tvYear, tvMonth, tvDay, tvHour, tvMinute;
    private EditText etFrequency;
    private Button btnSetAlarm;

    private int[] daySelection = new int[7]; // 요일 선택 상태 저장 (0: 선택 해제, 1: 선택됨)
    private ArrayList<Button> dayButtons = new ArrayList<>(); // 요일 버튼 리스트

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        // 초기화
        radioGroup = findViewById(R.id.radioGroup);
        rdoWeek = findViewById(R.id.rdoWeek);
        rdoDay = findViewById(R.id.rdoDay);

        btnMon = findViewById(R.id.btnMon);
        btnTue = findViewById(R.id.btnTue);
        btnWed = findViewById(R.id.btnWed);
        btnThu = findViewById(R.id.btnThu);
        btnFri = findViewById(R.id.btnFri);
        btnSat = findViewById(R.id.btnSat);
        btnSun = findViewById(R.id.btnSun);

        timePicker = findViewById(R.id.timePicker1);
        timePicker.setIs24HourView(true);

        tvYear = findViewById(R.id.tvYear);
        tvMonth = findViewById(R.id.tvMonth);
        tvDay = findViewById(R.id.tvDay);
        tvHour = findViewById(R.id.tvHour);
        tvMinute = findViewById(R.id.tvMinute);

        etFrequency = findViewById(R.id.etFrequency);
        btnSetAlarm = findViewById(R.id.btnSetAlarm);

        // 요일 버튼 리스트 초기화
        dayButtons.add(btnMon);
        dayButtons.add(btnTue);
        dayButtons.add(btnWed);
        dayButtons.add(btnThu);
        dayButtons.add(btnFri);
        dayButtons.add(btnSat);
        dayButtons.add(btnSun);

        // UI 초기 상태: 숨김
        setViewVisibility(false, false);

        // 라디오 버튼 선택 이벤트
        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rdoWeek) {
                // 주간 설정 선택
                setViewVisibility(true, true); // 요일 선택 포함
            } else if (checkedId == R.id.rdoDay) {
                // 일일 설정 선택
                setViewVisibility(true, false); // 요일 선택 제외
            }
        });

        // 요일 버튼 클릭 이벤트 설정
        for (int i = 0; i < dayButtons.size(); i++) {
            int index = i; // 익명 클래스에서 사용할 final 변수
            dayButtons.get(i).setOnClickListener(view -> {
                toggleDaySelection(index, dayButtons.get(index));
            });
        }

        // 시간 선택 이벤트
        timePicker.setOnTimeChangedListener((view, hourOfDay, minute) -> {
            tvHour.setText(String.format("%02d", hourOfDay));
            tvMinute.setText(String.format("%02d", minute));
            updateDateTimeText();
        });

        // 알림 설정 버튼
        btnSetAlarm.setOnClickListener(v -> {
            if (rdoWeek.isChecked()) {
                setAlarms(dayButtons); // 요일 기반 알림 설정
            } else if (rdoDay.isChecked()) {
                setDailyAlarm(); // 일일 알림 설정
            }
        });
    }

    // UI 동적 표시
    private void setViewVisibility(boolean showAlarmSettings, boolean showDayButtons) {
        int alarmVisibility = showAlarmSettings ? View.VISIBLE : View.GONE;
        int dayVisibility = showDayButtons ? View.VISIBLE : View.GONE;

        // 알림 설정 관련 UI
        timePicker.setVisibility(alarmVisibility);
        etFrequency.setVisibility(alarmVisibility);
        btnSetAlarm.setVisibility(alarmVisibility);

        // 요일 선택 관련 버튼
        for (Button button : dayButtons) {
            button.setVisibility(dayVisibility);
        }
    }

    private void toggleDaySelection(int index, Button button) {
        // 선택 상태 변경 (0 -> 1, 1 -> 0)
        daySelection[index] = (daySelection[index] + 1) % 2;

        // 색상 변경
        if (daySelection[index] == 1) {
            button.setBackgroundColor(Color.GREEN); // 선택됨
        } else {
            button.setBackgroundColor(Color.LTGRAY); // 선택 해제
        }
    }

    private void updateDateTimeText() {
        Calendar calendar = Calendar.getInstance();

        tvYear.setText(String.valueOf(calendar.get(Calendar.YEAR)));
        tvMonth.setText(String.format("%02d", calendar.get(Calendar.MONTH) + 1));
        tvDay.setText(String.format("%02d", calendar.get(Calendar.DAY_OF_MONTH)));
    }

    private void setAlarms(ArrayList<Button> dayButtons) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        int hour = timePicker.getHour();
        int minute = timePicker.getMinute();

        for (int i = 0; i < daySelection.length; i++) {
            if (daySelection[i] == 1) { // 선택된 요일만 처리
                int dayOfWeek = getDayOfWeek(i);

                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.DAY_OF_WEEK, dayOfWeek);
                calendar.set(Calendar.HOUR_OF_DAY, hour);
                calendar.set(Calendar.MINUTE, minute);
                calendar.set(Calendar.SECOND, 0);

                if (calendar.before(Calendar.getInstance())) {
                    calendar.add(Calendar.WEEK_OF_YEAR, 1); // 이미 지난 시간이라면 다음 주로 설정
                }

                Intent intent = new Intent(this, AlarmReceiver.class);
                intent.putExtra("day", dayButtons.get(i).getText().toString());
                PendingIntent pendingIntent = PendingIntent.getBroadcast(
                        this, dayOfWeek, intent, PendingIntent.FLAG_UPDATE_CURRENT
                );

                if (alarmManager != null) {
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                    Toast.makeText(this, dayButtons.get(i).getText() + " 알림이 설정되었습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void setDailyAlarm() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        int hour = timePicker.getHour();
        int minute = timePicker.getMinute();

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);

        if (calendar.before(Calendar.getInstance())) {
            Toast.makeText(this, "이미 지난 시간입니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(this, AlarmReceiver.class);
        intent.putExtra("type", "daily");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT
        );

        if (alarmManager != null) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
            Toast.makeText(this, "오늘 " + hour + "시 " + minute + "분에 알림이 설정되었습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    private int getDayOfWeek(int index) {
        switch (index) {
            case 0: return Calendar.MONDAY;
            case 1: return Calendar.TUESDAY;
            case 2: return Calendar.WEDNESDAY;
            case 3: return Calendar.THURSDAY;
            case 4: return Calendar.FRIDAY;
            case 5: return Calendar.SATURDAY;
            case 6: return Calendar.SUNDAY;
            default: return Calendar.MONDAY;
        }
    }
}

