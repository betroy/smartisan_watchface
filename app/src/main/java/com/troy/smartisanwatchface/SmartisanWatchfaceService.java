package com.troy.smartisanwatchface;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.wearable.watchface.CanvasWatchFaceService;
import android.util.Log;
import android.view.SurfaceHolder;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

/**
 * Created by chenlongfei on 15/9/5.
 */
public class SmartisanWatchfaceService extends CanvasWatchFaceService {
    private static final long INTERACTIVE_UPDATE_RATE_MS = TimeUnit.SECONDS.toMillis(1);

    @Override
    public Engine onCreateEngine() {
        return new Engine();
    }

    private class Engine extends CanvasWatchFaceService.Engine {
        private Paint mTickPaint;
        private Paint mBitmapPaint;
        private Bitmap mBackgroundBitmap;
        private Bitmap mBackgroundScaleBitmap;
        private Bitmap mDrakBackgroundBitmap;
        private Bitmap mDrakBackgroundScaleBitmap;
        private Bitmap mHandCenterBlackBitmap;
        private Bitmap mHandCenterBitmap;
        private Bitmap mHourHandBlackBitmap;
        private Bitmap mHourHandBitmap;
        private Bitmap mHourHandShadowBitmap;
        private Bitmap mMinuteHandBlackBitmap;
        private Bitmap mMinuteHandBitmap;
        private Bitmap mMinuteHandShadowBitmap;
        private Bitmap mSecHandBitmap;
        private Bitmap mSecHandShadowBitmap;
        private Bitmap mBackground;
        private Bitmap mCenterBitmap;
        private Bitmap mHourBitmap;
        private Bitmap mMinuteBitmap;
        private Calendar mCalendar;
        private boolean mLowBitAmbient;
        private boolean mBurnInProtection;
        private boolean isDrak;
        private static final int UPDATE_TIME = 0;
        private Handler mUpdateTimeHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == UPDATE_TIME) {
                    invalidate();
                    if (shouldTimerBeRunning()) {
                        long timeMs = System.currentTimeMillis();
                        long delayMs = INTERACTIVE_UPDATE_RATE_MS
                                - (timeMs % INTERACTIVE_UPDATE_RATE_MS);
                        mUpdateTimeHandler.sendEmptyMessageDelayed(UPDATE_TIME, delayMs);
                    }
                }
            }
        };

        @Override
        public void onCreate(SurfaceHolder holder) {
            Log.i("Troy", "onCreate");
            super.onCreate(holder);
            mBitmapPaint = new Paint();
            mBitmapPaint.setAntiAlias(true);

            mTickPaint = new Paint();
            mTickPaint.setAntiAlias(true);
            mTickPaint.setTextSize(24f);
            mTickPaint.setTextAlign(Paint.Align.CENTER);
            mTickPaint.setTypeface(Typeface.createFromAsset(getAssets(), "SmartisanClock-Bold.ttf"));
            Resources resources = getResources();
            mBackgroundBitmap = BitmapFactory.decodeResource(resources, R.drawable.blank_clock);
            mDrakBackgroundBitmap = BitmapFactory.decodeResource(resources, R.drawable.blank_circle);

            mHandCenterBlackBitmap = BitmapFactory.decodeResource(resources, R.drawable.hand_center_black);
            mHandCenterBlackBitmap = Bitmap.createScaledBitmap(mHandCenterBlackBitmap, 60, 60, true);
            mHandCenterBitmap = BitmapFactory.decodeResource(resources, R.drawable.hand_center);
            mHandCenterBitmap = Bitmap.createScaledBitmap(mHandCenterBitmap, 60, 60, true);

            mHourHandBlackBitmap = BitmapFactory.decodeResource(resources, R.drawable.hour_hand_black);
            mHourHandBlackBitmap = Bitmap.createScaledBitmap(mHourHandBlackBitmap, 50, 115, true);
            mHourHandBitmap = BitmapFactory.decodeResource(resources, R.drawable.hour_hand);
            mHourHandBitmap = Bitmap.createScaledBitmap(mHourHandBitmap, 50, 115, true);
            mHourHandShadowBitmap = BitmapFactory.decodeResource(resources, R.drawable.hour_hand_shadow);
            mHourHandShadowBitmap = Bitmap.createScaledBitmap(mHourHandShadowBitmap, 50, 115, true);

            mMinuteHandBlackBitmap = BitmapFactory.decodeResource(resources, R.drawable.minute_hand_black);
            mMinuteHandBlackBitmap = Bitmap.createScaledBitmap(mMinuteHandBlackBitmap, 28, 136, true);
            mMinuteHandBitmap = BitmapFactory.decodeResource(resources, R.drawable.minute_hand);
            mMinuteHandBitmap = Bitmap.createScaledBitmap(mMinuteHandBitmap, 28, 136, true);
            mMinuteHandShadowBitmap = BitmapFactory.decodeResource(resources, R.drawable.minute_hand_shadow);
            mMinuteHandShadowBitmap = Bitmap.createScaledBitmap(mMinuteHandShadowBitmap, 28, 136, true);

            mSecHandBitmap = BitmapFactory.decodeResource(resources, R.drawable.sec_hand);
            mSecHandBitmap = Bitmap.createScaledBitmap(mSecHandBitmap, 14, 175, true);
            mSecHandShadowBitmap = BitmapFactory.decodeResource(resources, R.drawable.sec_hand_shadow);
            mSecHandShadowBitmap = Bitmap.createScaledBitmap(mSecHandShadowBitmap, 14, 175, true);
        }

        @Override
        public void onDestroy() {
            Log.i("Troy", "onDestroy");
            super.onDestroy();
            mUpdateTimeHandler.removeMessages(UPDATE_TIME);
        }

        //获得设备屏幕信息
        @Override
        public void onPropertiesChanged(Bundle properties) {
            Log.i("Troy", "onPropertiesChanged");
            super.onPropertiesChanged(properties);
            mLowBitAmbient = properties.getBoolean(PROPERTY_LOW_BIT_AMBIENT, false);
            mBurnInProtection = properties.getBoolean(PROPERTY_BURN_IN_PROTECTION,
                    false);
        }

        //系统每分钟调用一次
        @Override
        public void onTimeTick() {
            Log.i("Troy", "onTimeTick");
            super.onTimeTick();
            invalidate();
        }

        //环境模式和交互模式之间转换时回调
        @Override
        public void onAmbientModeChanged(boolean inAmbientMode) {
            Log.i("Troy", "onAmbientModeChanged");
            super.onAmbientModeChanged(inAmbientMode);
        }

        //Called when the user changes interruption filter. The watch face should adjust the amount of information it displays.
        @Override
        public void onInterruptionFilterChanged(int interruptionFilter) {
            Log.i("Troy", "onInterruptionFilterChanged");
            super.onInterruptionFilterChanged(interruptionFilter);
        }

        //绘制表盘
        @Override
        public void onDraw(Canvas canvas, Rect bounds) {
            mCalendar = Calendar.getInstance();
            int hour = mCalendar.get(Calendar.HOUR);
            int minute = mCalendar.get(Calendar.MINUTE);
            int second = mCalendar.get(Calendar.SECOND);
            int am_pm = mCalendar.get(Calendar.AM_PM);
            int width = bounds.width();
            int height = bounds.height();
            float centerX = width / 2f;
            float centerY = height / 2f;
            //夜间模式切换
            if (am_pm == Calendar.AM) {
                if (hour > 6) {
                    isDrak = false;
                }
            } else if (am_pm == Calendar.PM) {
                if (hour > 6) {
                    isDrak = true;
                }
            }

            if (mBackgroundScaleBitmap == null || mBackgroundScaleBitmap.getWidth() != width || mBackgroundScaleBitmap.getHeight() != height) {
                mBackgroundScaleBitmap = Bitmap.createScaledBitmap(mBackgroundBitmap, width, height, true);
            }
            if (mDrakBackgroundScaleBitmap == null || mDrakBackgroundScaleBitmap.getWidth() != width || mDrakBackgroundScaleBitmap.getHeight() != height) {
                mDrakBackgroundScaleBitmap = Bitmap.createScaledBitmap(mDrakBackgroundBitmap, width, height, true);
            }
//            Log.i("Troy", String.format("%1$d:%2$d:%3$d", hour, minute, second));

            if (isDrak) {
                mTickPaint.setColor(Color.parseColor("#F4F4F4"));
                mBackground = mDrakBackgroundScaleBitmap;
                mCenterBitmap = mHandCenterBlackBitmap;
                mHourBitmap = mHourHandBlackBitmap;
                mMinuteBitmap = mMinuteHandBlackBitmap;
            } else {
                mTickPaint.setColor(Color.parseColor("#686868"));
                mBackground = mBackgroundScaleBitmap;
                mCenterBitmap = mHandCenterBitmap;
                mHourBitmap = mHourHandBitmap;
                mMinuteBitmap = mMinuteHandBitmap;
            }
            //抗锯齿
            canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            //画背景
            canvas.drawBitmap(mBackground, 0, 0, null);
            //画数字刻度
            Paint.FontMetricsInt fontMetricsInt = mTickPaint.getFontMetricsInt();
            int baseLine = bounds.top + (bounds.bottom - bounds.top - fontMetricsInt.bottom + fontMetricsInt.top) / 2 - fontMetricsInt.top;
            canvas.drawText("12", bounds.centerX(), 40f, mTickPaint);
            canvas.drawText("3", width - 28f, baseLine, mTickPaint);

            float hourRot = (hour + minute / 60f + second / 3600f) * 30f;
            float hourShadowRot = 0f;

            //画时针Shadow
            if (hourRot <= 90f) {
                hourShadowRot = hourRot / 20f;
            } else if (hourRot > 90f && hourRot <= 180f) {
                hourShadowRot = (180f - hourRot) / 20f;
            } else if (hourRot > 180f && hourRot <= 270f) {
                hourShadowRot = (180f - hourRot) / 20f;
            } else if (hourRot > 270f && hourRot <= 360f) {
                hourShadowRot = (hourRot - 360f) / 20f;
            }
            canvas.save();
            canvas.rotate(hourRot, centerX, centerY);
            canvas.drawBitmap(mHourHandShadowBitmap, (width - mHourHandBitmap.getWidth()) / 2 + hourShadowRot, centerY - mHourHandBitmap.getHeight() + 10f, null);
            canvas.restore();

            //画时针
            canvas.save();
            canvas.rotate(hourRot, centerX, centerY);
            canvas.drawBitmap(mHourBitmap, (width - mHourHandBitmap.getWidth()) / 2, centerY - mHourHandBitmap.getHeight() + 10f, null);
            canvas.restore();

            float minuteRot = (minute + second / 60f) * 6f;
            float minuteShadowRot = 0f;

            //画分针Shadow
            if (minuteRot <= 90f) {
                minuteShadowRot = minuteRot / 20f;
            } else if (minuteRot > 90f && minuteRot <= 180f) {
                minuteShadowRot = (180f - minuteRot) / 20f;
            } else if (minuteRot > 180f && minuteRot <= 270f) {
                minuteShadowRot = (180f - minuteRot) / 20f;
            } else if (minuteRot > 270f && minuteRot <= 360f) {
                minuteShadowRot = (minuteRot - 360f) / 20f;
            }
            canvas.save();
            canvas.rotate(minuteRot, centerX, centerY);
            canvas.drawBitmap(mMinuteHandShadowBitmap, (width - mMinuteHandBitmap.getWidth()) / 2 + minuteShadowRot, centerX - mMinuteHandBitmap.getHeight() + 10f, null);
            canvas.restore();

            //画分针
            canvas.save();
            canvas.rotate(minuteRot, centerX, centerY);
            canvas.drawBitmap(mMinuteBitmap, (width - mMinuteHandBitmap.getWidth()) / 2, centerX - mMinuteHandBitmap.getHeight() + 10f, null);
            canvas.restore();

            //画中心点
            canvas.drawBitmap(mCenterBitmap, (width - mHandCenterBitmap.getWidth()) / 2, (height - mHandCenterBitmap.getHeight()) / 2, null);

            float secondRot = second * 6f;
            float secondShadowRot = 0f;

            //画秒针Shadow
            if (secondRot <= 90f) {
                secondShadowRot = secondRot / 20f;
            } else if (secondRot > 90f && secondRot <= 180f) {
                secondShadowRot = (180f - secondRot) / 20f;
            } else if (secondRot > 180f && secondRot <= 270f) {
                secondShadowRot = (180f - secondRot) / 20f;
            } else if (secondRot > 270f && secondRot <= 360f) {
                secondShadowRot = (secondRot - 360f) / 20f;
            }
            canvas.save();
            canvas.rotate(secondRot, centerX, centerY);
            canvas.drawBitmap(mSecHandShadowBitmap, (width - mSecHandBitmap.getWidth()) / 2 + secondShadowRot, 18f, null);
            canvas.restore();

            //画秒针
            canvas.save();
            canvas.rotate(secondRot, centerX, centerY);
            canvas.drawBitmap(mSecHandBitmap, (width - mSecHandBitmap.getWidth()) / 2, 18f, null);
            canvas.restore();
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            Log.i("Troy", "onVisibilityChanged");
            super.onVisibilityChanged(visible);
            updateTime();
        }


        public void updateTime() {
            mUpdateTimeHandler.removeMessages(UPDATE_TIME);
            if (shouldTimerBeRunning()) {
                mUpdateTimeHandler.sendEmptyMessage(UPDATE_TIME);
            }
        }

        private boolean shouldTimerBeRunning() {
            return isVisible() && !isInAmbientMode();
        }
    }
}
