package it.optimizeme;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private static long TIMER_TASK;
    private static long TIMER_SHORT_BREAK;
    private static long TIMER_LONG_BREAK;

    private static int PROGRESS_COUNTER = 25;
    private static int NUMBER_TASK = 4; //numTask es. 4 task = 1 pomodoro
    private static int NUMBER_POMODORO = 4; //numPomodoro

    private long timeBuff = 0L;
    private int status = 0; //0 numTask, 1 short break or longer break
    private int numTask = 1;
    private int numPomodoro = 0;
    private int progressTask = 0;

    private TextView mTimer;
    private TextView counterNum;
    private TextView pomodoroNum;
    private TextView taskPause;
    private Button btnStart;
    private Button btnStop;
    private ProgressBar progressBar;
    private CountDownTimer countDownTimer;
    private SharedPreferences sharedPreferences;
    private NotificationManager mNotifyManager;
    private boolean debug = true;

    private long[] pattern = {500,500,500,500,500,500,500,500,500};
    private Uri alarmSound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mTimer = (TextView) findViewById(R.id.timer);
        counterNum = (TextView) findViewById(R.id.quarter_num);
        pomodoroNum = (TextView) findViewById(R.id.pomodoro_num);
        taskPause = (TextView) findViewById(R.id.task_pause);
        btnStart = (Button) findViewById(R.id.btn_start);
        btnStop = (Button) findViewById(R.id.btn_stop);
        progressBar = (ProgressBar) findViewById(R.id.progressBarTask);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(debug) {
            TIMER_TASK = Long.parseLong(sharedPreferences.getString("settings_timer_task", "25")) * 1000;
            TIMER_SHORT_BREAK = Long.parseLong(sharedPreferences.getString("settings_timer_short_break", "3")) * 1000;
            TIMER_LONG_BREAK = Long.parseLong(sharedPreferences.getString("settings_timer_long_break", "25")) * 1000;
        } else {
            TIMER_TASK = Long.parseLong(sharedPreferences.getString("settings_timer_task", "25")) * 1000 * 60;
            TIMER_SHORT_BREAK = Long.parseLong(sharedPreferences.getString("settings_timer_short_break", "3")) * 1000 * 60;
            TIMER_LONG_BREAK = Long.parseLong(sharedPreferences.getString("settings_timer_long_break", "25")) * 1000 * 60;
        }

        Log.d("TIMER TASK", "TIMER TASK" + TIMER_TASK);
    }

    private void startCountDown(long time) {

        mNotifyManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        final NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
        mBuilder.setContentTitle(this.getString(R.string.app_name))
                .setSmallIcon(R.mipmap.ic_launcher);

        countDownTimer = new CountDownTimer(time + 900, 1000) {

            public void onTick(long millisUntilFinished) {
                timeBuff = millisUntilFinished;

                mTimer.setText(""+String.format("%02d:%02d",
                        TimeUnit.MILLISECONDS.toMinutes( millisUntilFinished),
                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))));

                mBuilder.setContentText("" + String.format("%02d:%02d",
                        TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished),
                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))));
                mBuilder.setSubText(status == 0 ? getString(R.string.status_progress) : getString(R.string.status_pause));
                mNotifyManager.notify(1, mBuilder.build());
            }

            public void onFinish() {
                isCompleted();
                mBuilder.setVibrate(pattern)
                        .setSound(alarmSound);
                mNotifyManager.notify(1, mBuilder.build());
            }

        }.start();
    }


    public void startTimer(View v) {
        startCountDown(timeBuff == 0L ? TIMER_TASK : timeBuff);
        taskPause.setText(status == 0 ? this.getString(R.string.status_progress) : this.getString(R.string.status_pause));
        setVisibility(true);
    }

    public void stopTimer(View v) {
        setVisibility(false);
        countDownTimer.cancel();
    }

    private void isCompleted() {
        final long newTimer;
        if(status == 0) {
            if(isAPomodoro(numTask)) {
                if(fourPomodoro(numPomodoro)) newTimer = TIMER_LONG_BREAK;
                else newTimer = TIMER_SHORT_BREAK;

                counterNum.setText(String.valueOf(numTask =0));
                numPomodoro++;
                pomodoroNum.setText(String.valueOf(numPomodoro));
                progressBar.setProgress(progressTask = 0);

            }
            else {
                counterNum.setText(String.valueOf(numTask));
                progressBar.setProgress(progressTask += PROGRESS_COUNTER);
                newTimer = TIMER_SHORT_BREAK;
            }
            mTimer.setText(this.getString(R.string.timer_task));
            numTask++;
            status = 1;
            taskPause.setText(this.getString(R.string.status_pause));
        }
        else {
            mTimer.setText(this.getString(R.string.timer_task));
            status = 0;
            taskPause.setText(this.getString(R.string.status_progress));
            newTimer = TIMER_TASK;
        }

        startCountDown(newTimer);
    }

    private boolean isAPomodoro(int task) {
        if(task == NUMBER_TASK) return true;
        else return false;
    }

    //da rivedere questo metodo
    private boolean fourPomodoro(int pomodoro) {
        if(pomodoro == NUMBER_POMODORO) return true;
        else return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setVisibility(boolean set) {
        btnStart.setVisibility(!set ? View.VISIBLE: View.GONE);
        btnStop.setVisibility(set ? View.VISIBLE : View.GONE);
    }
}
