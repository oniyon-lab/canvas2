package com.oniyon;

import android.app.Activity;
import android.os.Bundle;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;
import android.view.MotionEvent;

public class Canvas2Activity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DroidAnimeView myView
             = new DroidAnimeView( getApplication() );
        setContentView(myView);  
    }  
}

/**
 * 描画用のクラス
 */
class DroidAnimeView extends View {  
    private Paint myPaint = new Paint();
    private Bitmap myBitmap1, myBitmap2;
    private Bitmap myBitmap3, myBitmap4;
    private int displayWidth, displayHeight;
    private int imageWidth, imageHeight;
    private int x, y;
    private int dx, dy;
    private int count;
    private boolean isOffBalance = false;
    private boolean isAttached;

    private static final long DELAY_MILLIS = 60;

    /**
     * コンストラクタ
     */
    public DroidAnimeView(Context c) {  
        super(c);

        // イベントが取得できるようにFocusを有効にする
        setFocusable(true);  

        Resources res = this.getContext().getResources();

        // 画像の読み込み
        myBitmap1 = BitmapFactory.decodeResource(res,
                                              R.drawable.droid1);
        myBitmap2 = BitmapFactory.decodeResource(res,
                                              R.drawable.droid2);
        myBitmap3 = BitmapFactory.decodeResource(res,
                                              R.drawable.droid3);
        myBitmap4 = BitmapFactory.decodeResource(res,
                                              R.drawable.droid4);

        // 画像サイズの取得
        imageWidth = myBitmap1.getWidth();
        imageHeight = myBitmap1.getHeight();

        // 座標、差分の初期化
        x = 0;
        y = 0;
        dx = 2;
        dy = imageHeight;

        // 慌てる回数の初期化
        count = 0;
    }

    /**
     * 描画処理
     */
    protected void onDraw(Canvas canvas) {
        Bitmap myBitmap;

        // 画面を白色にする
        canvas.drawColor(Color.WHITE);

        // 移動方向のチェック
        if ( dx < 0 ) {
            // 左向きの画像
            myBitmap = myBitmap1;
        } else {
            // 右向きの画像
            myBitmap = myBitmap2;
        }

        // 不安定な姿勢かどうかのチェック
        if ( isOffBalance ) {
            count++;
            // 3回に1回交互に画像を入れ替える
            if ( count/3 - count/6*2 == 0  ) {
                myBitmap = myBitmap3;
            } else {
                myBitmap = myBitmap4;
            }

            if ( count > 18 ) {
                // 不安定な姿勢の終了
                count = 0;
                isOffBalance = false;
            }
        }

        // 画像の描画
        canvas.drawBitmap(myBitmap, x, y, myPaint);
    }

    /**
     * サイズが変更された時(縦から横モードになった時)に
     * 呼び出される
     */
    protected void onSizeChanged(int w, int h,
                                     int oldw, int oldh) {
        // 画面の縦と横のサイズを得る
        displayWidth = w;  
        displayHeight = h;  
    }

    /**
     * タッチイベント
     */
    public boolean onTouchEvent(MotionEvent event) {

        // タッチされた座標の取得
        int x1 = (int)event.getX();
        int y1 = (int)event.getY();
        
        // タッチされた座標がアイコン内かどうか
        if (x < x1 && x1 < x + imageWidth) {
            if (y < y1 && y1 < y + imageHeight ) {
                // 不安定な姿勢になる
                isOffBalance = true;
            }
        }
        return true;
    }

    /**
     * 移動処理
     */
    private void move() {
        // x方向の画面の端まで移動したかのチェック
        if (x < 0 || x + imageWidth > displayWidth) {
            // x方向の移動を反転
            dx = -dx;

            // y座標の移動
            y += dy;
        }

        // y方向の画面の端まで移動したかのチェック
        if (y < 0 || y + imageHeight > displayHeight) {
            // y方向の移動を反転
            dy = -dy;  

            // y座標の移動
            y += dy*2;
        }

        // x座標の移動
        x += dx;  
    }

    /**
     * タイマーハンドラー
     */
    private Handler myHandler = new Handler() {
        @Override 
        public void handleMessage(Message msg) {
            if (isAttached) {
                // 移動処理
                move();
                    
                // 再描画
                invalidate();
                sendEmptyMessageDelayed(0, DELAY_MILLIS);
            }
        }
    };

    /**
     * WindowにAttachされた時の処理
     */
    protected void onAttachedToWindow() {  
        isAttached = true;  
        myHandler.sendEmptyMessageDelayed(0, DELAY_MILLIS);  
        super.onAttachedToWindow();
    }  

    /**
     * WindowからDetachされた時の処理
     */
    protected void onDetachedFromWindow() {  
        isAttached = false;  
        super.onDetachedFromWindow();  
    }
}