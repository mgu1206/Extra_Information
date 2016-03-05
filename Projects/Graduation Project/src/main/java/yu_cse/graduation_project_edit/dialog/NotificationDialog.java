package yu_cse.graduation_project_edit.dialog;

import android.content.Intent;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import yu_cse.graduation_project_edit.R;

public class NotificationDialog extends Activity implements View.OnClickListener{

    ImageButton close_Button;
    Intent intent;

    String title, message;

    TextView msg_title, msg_message;

    private SoundPool soundPool;
    private int sound_beep;

    private void initSound()
    {
        soundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
        sound_beep = soundPool.load(this, R.raw.alarm,1);
    }

    public void playSound()
    {
        soundPool.setOnLoadCompleteListener(
                new SoundPool.OnLoadCompleteListener() {
                    @Override
                    public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                        soundPool.play(sound_beep, 1f, 1f, 0, 0, 1f);
                    }
                }
        );

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        layoutParams.dimAmount = 0.95f;
        getWindow().setAttributes(layoutParams);
        setContentView(R.layout.activity_notification_dialog);

        intent = getIntent();
        title = intent.getSerializableExtra("title").toString();
        message = intent.getSerializableExtra("message").toString();

        msg_title = (TextView)findViewById(R.id.title);
        msg_message = (TextView)findViewById(R.id.message);

        msg_title.setText(title);
        msg_message.setText(message);

        close_Button = (ImageButton)findViewById(R.id.closeBtn);
        close_Button.setOnClickListener(this);
        initSound();
        playSound();

    }

    @Override
    public void onClick(View v) {
        switch(v.getId())
        {
            case R.id.closeBtn:
                this.finish();
                break;
        }
    }
}
