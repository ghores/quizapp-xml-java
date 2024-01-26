package com.example.quizgame;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class StartBtn extends FrameLayout {

    private View animView;
    private Button startBtn;

    public StartBtn(@NonNull Context context) {
        super(context);
        setupViews();
    }

    public StartBtn(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setupViews();
    }

    public StartBtn(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setupViews();
    }

    public StartBtn(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setupViews();
    }

    public void setupViews() {
        LayoutInflater.from(getContext()).inflate(R.layout.start_btn, this, true);
        animView = findViewById(R.id.view_start_btn);
        startBtn = findViewById(R.id.button_start_btn);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (!isInEditMode()) {
            ScaleAnimation scaleAnimation = new ScaleAnimation(1, 1.2f, 1, 1.2f,
                    Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            scaleAnimation.setDuration(400);
            scaleAnimation.setRepeatCount(Animation.INFINITE);
            scaleAnimation.setRepeatMode(Animation.REVERSE);
            animView.startAnimation(scaleAnimation);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int size = getContext().getResources().getDimensionPixelSize(R.dimen.size_startButton);
        super.onMeasure(MeasureSpec.makeMeasureSpec(size, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(size, MeasureSpec.EXACTLY));
    }

    public void setOnStartClickListener(OnClickListener onClickListener) {
        startBtn.setOnClickListener(onClickListener);
    }

    public void setOnStartButtonClickListener(OnClickListener onClickListener) {
        startBtn.setOnClickListener(onClickListener);
    }
}
