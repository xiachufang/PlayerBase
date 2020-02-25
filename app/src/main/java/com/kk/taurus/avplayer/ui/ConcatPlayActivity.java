package com.kk.taurus.avplayer.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.kk.taurus.avplayer.R;
import com.kk.taurus.playerbase.assist.RelationAssist;
import com.kk.taurus.playerbase.entity.DataSource;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 播放concat 示例
 */
public class ConcatPlayActivity extends AppCompatActivity {

    private FrameLayout videoContent;
    private TextView start;
    private RelationAssist assist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_concat_play);

        videoContent = findViewById(R.id.videocontent);
        start = findViewById(R.id.start);

        assist = new RelationAssist(this);
        assist.attachContainer(videoContent);

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startConcatPlay();
            }
        });
    }

    private void startConcatPlay() {


        File cacheDir = getExternalCacheDir();
        File concat = new File(cacheDir, "playlist.ffconcat");
        if (concat.exists()) {
            concat.delete();
        }
        //注意：播放文件内容一定要按照如下格式编写，详见 https://ffmpeg.org/ffmpeg-formats.html#concat
        try {
            FileWriter writer = new FileWriter(concat);
            //ffconcat版本
            writer.write("ffconcat version 1.0");
            writer.write("\r\n");

            for (ConcatMedia m : getConcatData()) {
                //地址
                writer.write("file '" + m.url + "'");
                writer.write("\r\n");
                //时长
                writer.write("duration " + m.duration);
                writer.write("\r\n");
            }

            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String concatUrl = concat.getPath();
        assist.setDataSource(new DataSource(concatUrl));
        assist.play();

    }

    static class ConcatMedia {
        public ConcatMedia(String url, long duration) {
            this.url = url;
            this.duration = duration;
        }

        public String url;
        public long duration;
    }

    private List<ConcatMedia> getConcatData() {
        List<ConcatMedia> medias = new ArrayList<>();
        medias.add(new ConcatMedia("http://vfx.mtime.cn/Video/2019/02/04/mp4/190204084208765161.mp4", 31));
        medias.add(new ConcatMedia("http://vfx.mtime.cn/Video/2019/03/21/mp4/190321153853126488.mp4", 100));
        medias.add(new ConcatMedia("http://vfx.mtime.cn/Video/2019/03/19/mp4/190319222227698228.mp4", 60));
        return medias;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (assist != null) {
            assist.pause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (assist != null) {
            assist.resume();
        }
    }

    @Override
    protected void onDestroy() {
        if (assist != null) {
            assist.destroy();
        }
        super.onDestroy();
    }
}
