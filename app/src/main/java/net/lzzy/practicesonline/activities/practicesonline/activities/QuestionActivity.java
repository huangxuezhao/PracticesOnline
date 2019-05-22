package net.lzzy.practicesonline.activities.practicesonline.activities;


import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.os.Parcelable;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import net.lzzy.practicesonline.R;
import net.lzzy.practicesonline.activities.practicesonline.fragments.QuestionFragment;
import net.lzzy.practicesonline.activities.practicesonline.models.FavoriteFactory;
import net.lzzy.practicesonline.activities.practicesonline.models.Question;
import net.lzzy.practicesonline.activities.practicesonline.models.QuestionFactory;
import net.lzzy.practicesonline.activities.practicesonline.models.UserCookies;
import net.lzzy.practicesonline.activities.practicesonline.models.view.PracticeResult;
import net.lzzy.practicesonline.activities.practicesonline.models.view.QuestionResult;
import net.lzzy.practicesonline.activities.practicesonline.network.PracticeService;
import net.lzzy.practicesonline.activities.practicesonline.utils.AbstractStaticHandler;
import net.lzzy.practicesonline.activities.practicesonline.utils.AppUtils;
import net.lzzy.practicesonline.activities.practicesonline.utils.ViewUtils;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;



public class QuestionActivity extends AppCompatActivity {
    public static final int REQUEST_CODE_RESULT = 0;
    public static final String EXTRA_PRACTICE_ID = "extraPracticeId";
    public static final String EXTRA_RESULT = "extraResult";

    public String practocaId;
    private int apiId;
    private List<Question> questions;
    private TextView tvView;
    private TextView tvCommit;
    private ViewPager pager;
    private boolean isCommitted = false;
    private TextView tvHint;
    private FragmentStatePagerAdapter adapter;

    private int pos;
    private View[] dots;
    public static final int OK = 0;
    public static final int NO = 1;
    public static final int RETRY = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_question);
        AppUtils.addActivity(this);
        retrieveData();
        initViews();
        initDots();
        setListeners();
        pos = UserCookies.getInstance().getCurrentQuestion(practocaId);
        pager.setCurrentItem(pos);
        refreshDots(pos);
        UserCookies.getInstance().getReadCount(questions.get(pos).getId().toString());

    }

    private void setListeners() {
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                refreshDots(position);
                UserCookies.getInstance().updateCurrentQuestion(practocaId, position);
                UserCookies.getInstance().updateReadCount(questions.get(position).getId().toString());

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        tvCommit.setOnClickListener(v -> commitPractice());
        tvView.setOnClickListener(v -> redirect());
    }

    private void redirect() {
        List<QuestionResult>results=UserCookies.getInstance().getResultFromCookies(questions);
        Intent intent=new Intent(this,ResultActivity.class);
        intent.putExtra(EXTRA_PRACTICE_ID,practocaId);
        intent.putParcelableArrayListExtra(EXTRA_RESULT,(ArrayList<? extends Parcelable>)results);
        startActivityForResult(intent, REQUEST_CODE_RESULT);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            if (data.getBooleanExtra(ResultActivity.COLLECTION, false)) {
                FavoriteFactory favoriteFactory = FavoriteFactory.getInstance();
                List<Question> cc = new ArrayList<>();
                for (Question question : questions) {
                    if (favoriteFactory.isQuestionStarred(question.getId().toString())) {
                        cc.add(question);
                    }
                }
                questions.clear();
                questions.addAll(cc);
                initDots();


            } else {
                pager.setCurrentItem(data.getIntExtra(ResultActivity.QUESTION, -1));
            }
            // TODO:返回查看数据
        }
    }

    String info;

    private void commitPractice() {
        List<QuestionResult> results = UserCookies.getInstance().getResultFromCookies(questions);
        List<String> macs = AppUtils.getMacAddress();
        String[] items = new String[macs.size()];
        macs.toArray(items);
        info = items[0];
        new AlertDialog.Builder(this)
                .setTitle("选择Mac地址")
                .setSingleChoiceItems(items, 0, (dialog, which) -> info = items[which])
                .setNegativeButton("取消", null)
                .setPositiveButton("提交", (dialog, which) -> {
                    PracticeResult result = new PracticeResult(results, apiId, "黄学钊," + info);
                    postResult(result);
                }).show();
    }

    private QuestionActivity.CountHandler handler = new QuestionActivity.CountHandler(this);

    public static class CountHandler extends AbstractStaticHandler<QuestionActivity> {
        CountHandler(QuestionActivity context) {
            super(context);
        }

        @Override
        public void handleMessage(Message msg, QuestionActivity questionActivity) {
            switch (msg.what) {
                case OK:
                    questionActivity.isCommitted = true;
                    UserCookies.getInstance().commitPractice(questionActivity.practocaId);
                    Toast.makeText(questionActivity, "提交成功", Toast.LENGTH_SHORT).show();

                    break;
                case NO:
                    Toast.makeText(questionActivity, "提交失败", Toast.LENGTH_SHORT).show();
                    break;
                case RETRY:
                    Toast.makeText(questionActivity, "提交失败", Toast.LENGTH_SHORT).show();
                    break;

            }

        }

        @Override
        public void sendMessag(Object o) {

        }
    }

    private void postResult(PracticeResult result) {
        AppUtils.getExectuor().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    int r = PracticeService.postResult(result);
                    if (r >= 200 && r <= 220) {
                        handler.sendEmptyMessage(OK);
                    } else {
                        handler.sendEmptyMessage(NO);
                    }
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                    handler.sendEmptyMessage(RETRY);
                }
            }
        });


    }


    //todo:启动线程提交数据


    private void initDots() {
        int count = questions.size();
        dots = new View[count];
        LinearLayout container = findViewById(R.id.activity_question_dote);
        container.removeAllViews();
        int px = ViewUtils.dp2px(16, this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(px, px);
        px = ViewUtils.dp2px(5, this);
        params.setMargins(px, px, px, px);
        for (int i = 0; i < count; i++) {
            TextView tvDot = new TextView(this);
            tvDot.setLayoutParams(params);
            tvDot.setBackgroundResource(R.drawable.dot_style);
            tvDot.setTag(i);
            //todo:添加点击监听
            tvDot.setOnClickListener(v -> pager.setCurrentItem((Integer) v.getTag()));
            container.addView(tvDot);
            dots[i] = tvDot;

        }
    }

    private void refreshDots(int pos) {
        for (int i = 0; i < dots.length; i++) {
            int drawable = i == pos ? R.drawable.dot_fill_style : R.drawable.dot_style;
            dots[i].setBackgroundResource(drawable);
        }
    }

    private void initViews() {
        tvView = findViewById(R.id.activity_question_tv_view);
        tvCommit = findViewById(R.id.activity_question_tv_commit);
        tvHint = findViewById(R.id.activity_question_tv_hint);
        pager = findViewById(R.id.activity_question_pager);
        isCommitted=UserCookies.getInstance().isPracticeCommitted(practocaId);
        if (isCommitted) {
            tvCommit.setVisibility(View.GONE);
            tvView.setVisibility(View.VISIBLE);
            tvHint.setVisibility(View.VISIBLE);
        } else {
            tvView.setVisibility(View.GONE);
            tvCommit.setVisibility(View.VISIBLE);
            tvHint.setVisibility(View.GONE);
        }

        adapter=new FragmentStatePagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                Question question=questions.get(position);
                return  QuestionFragment.newInstace(question.getId().toString(),position,isCommitted);
            }

            @Override
            public int getCount() {
                return questions.size();
            }
        };
        pager.setAdapter(adapter);


        FragmentStatePagerAdapter adapter = new FragmentStatePagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                Question question = questions.get(position);
                return QuestionFragment.newInstace(question.getId().toString(), position, isCommitted);
            }

            @Override
            public int getCount() {
                return questions.size();
            }
        };
        pager.setAdapter(adapter);
    }


    private void retrieveData() {
        practocaId = getIntent().getStringExtra(PracticesActivity.PRACTICES_ID);
        apiId = getIntent().getIntExtra(PracticesActivity.API_ID, -1);
        questions = QuestionFactory.getInstance().getBypractice(practocaId);
        isCommitted = UserCookies.getInstance().isPracticeCommitted(practocaId);
        if (apiId < 0 || questions == null || questions.size() == 0) {
            Toast.makeText(this, "no questions", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppUtils.removeActivity(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        AppUtils.setRunning(getLocalClassName());
    }

    @Override
    protected void onStop() {
        super.onStop();
        AppUtils.setStopped(getLocalClassName());
    }
}
