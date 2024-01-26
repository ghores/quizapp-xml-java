package com.example.quizgame;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private FrameLayout getStartDialog;

    private QuestionManager questionManager;

    private TextView questionTextView;
    private TextView option0Button;
    private TextView option1Button;
    private TextView option2Button;
    private TextView option3Button;
    private TextView pointTextView;
    private TextView remainingTimeTextView;
    private Question currentQuestion;
    private int point;
    private Timer timer;
    private int remainingTime = 34000;
    private MediaPlayer mediaPlayer, mediaPlayer2, mediaPlayer3;
    private PointsManager pointsManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mediaPlayer = MediaPlayer.create(this, R.raw.background_music);
        mediaPlayer2 = MediaPlayer.create(this, R.raw.positive);
        mediaPlayer3 = MediaPlayer.create(this, R.raw.wrong);
        timer = new Timer();

        questionManager = new QuestionManager(this);

        pointsManager = new PointsManager(this);

        setupViews();

    }

    private void setupViews() {

        getStartDialog = findViewById(R.id.frame_main_startDialog);
        questionTextView = findViewById(R.id.textView_main_question);
        pointTextView = findViewById(R.id.textView_main_point);
        remainingTimeTextView = findViewById(R.id.textView_main_remainingTime);
        Button exitButton = findViewById(R.id.button_main_exit);
        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        option0Button = findViewById(R.id.button_main_answer_0);
        option0Button.setTag(0);
        option0Button.setOnClickListener(this);

        option1Button = findViewById(R.id.button_main_answer_1);
        option1Button.setTag(1);
        option1Button.setOnClickListener(this);

        option2Button = findViewById(R.id.button_main_answer_2);
        option2Button.setTag(2);
        option2Button.setOnClickListener(this);

        option3Button = findViewById(R.id.button_main_answer_3);
        option3Button.setTag(3);
        option3Button.setOnClickListener(this);

        final StartBtn startBtn = findViewById(R.id.startButton_main);
        startBtn.setOnStartButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlphaAnimation alphaAnimation = new AlphaAnimation(1f, 0f);
                alphaAnimation.setDuration(300);
                alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        getStartDialog.setVisibility(View.GONE);
                        onGameStarted();
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });

                getStartDialog.startAnimation(alphaAnimation);

            }
        });

    }

    private void setPoint(int point) {
        this.point = point;
        pointTextView.setText(String.valueOf(point));
    }

    private void loadNewQuestion() {
        currentQuestion = questionManager.getQuestion();
        questionTextView.setText(currentQuestion.getQuestion());
        option0Button.setText(currentQuestion.getOptions().get(0));
        option1Button.setText(currentQuestion.getOptions().get(1));
        option2Button.setText(currentQuestion.getOptions().get(2));
        option3Button.setText(currentQuestion.getOptions().get(3));
    }

    private void onGameStarted() {
        setPoint(0);
        mediaPlayer.start();
        remainingTime = 121000;

        remainingTimeTextView.setText(formatTime(remainingTime));
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        remainingTime -= 1000;
                        if (remainingTime < 20000) {
                            if (remainingTime < 10000) {
                                if (remainingTime <= 0) {
                                    onGameFinished();
                                }
                                remainingTimeTextView.setTextColor(ContextCompat.getColor(MainActivity.this, android.R.color.holo_red_light));
                            } else {
                                remainingTimeTextView.setTextColor(ContextCompat.getColor(MainActivity.this, android.R.color.holo_orange_light));
                            }
                        } else {
                            remainingTimeTextView.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.green));
                        }
                        remainingTimeTextView.setText(formatTime(remainingTime));
                    }
                });
            }
        }, 0, 1000);
        loadNewQuestion();
    }

    private boolean isShowingSelectedOptionResult = false;

    @Override
    public void onClick(final View view) {

        if (questionManager.finishAnswer) {
            onGameFinished();
        }

        if (!isShowingSelectedOptionResult) {
            int selectedOption = (int) view.getTag();
            if (selectedOption == currentQuestion.getAnswer()) {
                view.setBackgroundResource(R.drawable.shape_correct_option);
                if (point < 30) {
                    setPoint(point + 1);
                }

                mediaPlayer2.start();

            } else {
                view.setBackgroundResource(R.drawable.shape_wrong_option);
                mediaPlayer3.start();
            }
            isShowingSelectedOptionResult = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    loadNewQuestion();
                    view.setBackgroundResource(R.drawable.bg_option_btn);
                    isShowingSelectedOptionResult = false;
                }
            }, 500);
        }

    }

    private String formatTime(long duration) {
        int seconds = (int) (duration / 1000);
        int minutes = seconds / 60;
        seconds %= 60;
        return String.format(Locale.ENGLISH, "%02d", minutes) + ":" + String.format(Locale.ENGLISH, "%02d", seconds);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        timer.cancel();
        mediaPlayer.release();
        mediaPlayer2.release();
        mediaPlayer3.release();
    }

    private void onGameFinished() {
        pointsManager.savePoint(point);
        timer.cancel();
        mediaPlayer.seekTo(0);
        mediaPlayer.pause();
        mediaPlayer2.seekTo(0);
        mediaPlayer2.pause();
        mediaPlayer3.seekTo(0);
        mediaPlayer3.pause();
        showGameResult();
    }

    public void showGameResult() {
        final FrameLayout frameLayout = findViewById(R.id.frame_resultDialog);
        frameLayout.setVisibility(View.VISIBLE);
        AlphaAnimation alphaAnimation = new AlphaAnimation(0f, 1f);
        alphaAnimation.setDuration(300);
        frameLayout.startAnimation(alphaAnimation);

        TextView resultMessageTextView = findViewById(R.id.textView_gameResultDialog_message);
        TextView resultPointTextView = findViewById(R.id.txt_result_point);
        TextView bestRecordTextView = findViewById(R.id.txt_resultDialog_best);

        Button exitButton = findViewById(R.id.button_gameResultDialog_exit);
        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        StartBtn startButton = findViewById(R.id.startButton_gameResultDialog);
        startButton.setOnStartButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                frameLayout.setVisibility(View.GONE);
                onGameStarted();
            }
        });

        if (point >= 28) {
            resultMessageTextView.setText("Perfect!");
        } else if (point >= 24) {
            resultMessageTextView.setText("Good!");
        } else if (point >= 15) {
            resultMessageTextView.setText("Not Bad!");
        } else if (point >= 0) {
            resultMessageTextView.setText("Bad , Try Again");
        }

        resultPointTextView.setText(String.valueOf(point));
        bestRecordTextView.setText("Best Record : " + pointsManager.getBestRecord() + " Points");
    }

}