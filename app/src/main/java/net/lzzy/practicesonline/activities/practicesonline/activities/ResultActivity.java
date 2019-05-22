package net.lzzy.practicesonline.activities.practicesonline.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import net.lzzy.practicesonline.R;
import net.lzzy.practicesonline.activities.practicesonline.fragments.ChartFragment;
import net.lzzy.practicesonline.activities.practicesonline.fragments.GridFragment;
import net.lzzy.practicesonline.activities.practicesonline.models.view.QuestionResult;
import net.lzzy.practicesonline.activities.practicesonline.utils.AppUtils;

import java.util.List;


/**
 * Created by lzzy_gxy on 2019/5/13.
 * Description:
 */
public class ResultActivity extends BaseActivity implements GridFragment.OnGridSkipListener, ChartFragment.OnCharSkipListener {


    public static final String QUESTION = "question";
    public static final String COLLECTION = "collection";

    public static final String ENSHRINE = "enshrine";

    public List<QuestionResult> results;
    private Button btn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        btn = findViewById(R.id.activity_result_circle_btn_view);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = getManager().findFragmentById(R.id.activity_result_container);
                if (fragment instanceof GridFragment) {
                    Fragment fragment1=  ChartFragment.newInstance(results);
                    getManager().beginTransaction().replace(R.id.activity_result_container,fragment1).commit();
                    btn.setText("表");

                }
                if (fragment instanceof ChartFragment){
                    Fragment fragment2=createFragment();
                    getManager().beginTransaction().replace(R.id.activity_result_container,fragment2).commit();
                    btn.setText("图");

                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setMessage("返回到哪里？")
                .setPositiveButton("返回题目", (dialog, which) -> {
                    finish();
                })
                .setNegativeButton("章节列表", (dialog, which) -> {
                    startActivity(new Intent(this,PracticesActivity.class));
                    finish();
                })
                .setNeutralButton("查看收藏", (dialog, which) -> {
                    Intent intent=new Intent();
                    intent.putExtra(ENSHRINE,true);
                    setResult(RESULT_OK,intent);
                    finish();
                })
                .show();

    }


    @Override
    protected int getLayoutRes() {
        return  R.layout.activity_result;
    }

    /**
     * 托管activity的布局
     *
     * @return activity的布局
     */

    /**
     * fragment容器的ID
     *
     * @return 容器的ID
     */
    @Override
    protected int getContainerId() {
        return R.id.activity_result_container;
    }

    /**
     * 生成Fragment对象
     *
     * @return Fragment对象
     */
    @Override
    protected Fragment createFragment() {
        results = getIntent().getParcelableArrayListExtra(QuestionActivity.EXTRA_RESULT);
        return GridFragment.newInstance(results);
        //Grid
    }
    /**
     * 跳转返回Question视图查看题目
     *
     * @param position
     */
    @Override
    public void onGridSkip(int position) {
        Intent intent = new Intent();
        intent.putExtra(QUESTION, position);
        setResult(position, intent);
        finish();
    }



    /**
     * 跳转到GridFragment
     */
}
