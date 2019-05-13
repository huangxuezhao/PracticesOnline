package net.lzzy.practicesonline.activities.practicesonline.fragments;

import android.app.AlertDialog;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import net.lzzy.practicesonline.R;
import net.lzzy.practicesonline.activities.practicesonline.models.FavoriteFactory;
import net.lzzy.practicesonline.activities.practicesonline.models.Option;
import net.lzzy.practicesonline.activities.practicesonline.models.Question;
import net.lzzy.practicesonline.activities.practicesonline.models.QuestionFactory;
import net.lzzy.practicesonline.activities.practicesonline.models.UserCookies;
import net.lzzy.practicesonline.activities.practicesonline.models.view.QuestionType;

import java.util.List;

/**
 * Created by lzzy_gxy on 2019/4/30.
 * Description:
 */
public class QuestionFragment extends BaseFragment{
    private Question question;
    private int pos;
    private boolean isCommitted;
    private static final String ARG_QUESTION_ID="argQuestionId";
    private static final String ARG_POS="argPos";
    private static final String ARG_IS_COMMITTED="argIsCommitted";
    private TextView tvType;
    private ImageButton imgFavorite;
    private TextView tvContent;
    private RadioGroup rgOptions;
    private boolean isMulti=false;

    public static QuestionFragment newInstace(String questionId,int pos,boolean isCommitted){
        QuestionFragment fragment=new QuestionFragment();
        Bundle args=new Bundle();
        args.putString(ARG_QUESTION_ID,questionId);
        args.putInt(ARG_POS,pos);
        args.putBoolean(ARG_IS_COMMITTED,isCommitted);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() !=null){
            pos=getArguments().getInt(ARG_POS);
            isCommitted=getArguments().getBoolean(ARG_IS_COMMITTED);
            question= QuestionFactory.getInstance().getById(getArguments().getString(ARG_QUESTION_ID));

        }
    }

    @Override
    protected void populate() {

        tvType = find(R.id.fragment_question_tv_question_type);
        initVies();
        isMulti=question.getType()== QuestionType.MULTI_CHOICE;
        displayQuestion();
        generateOption();


        //收藏
        imgFavorite.setOnClickListener(v -> {
            boolean collect=FavoriteFactory.getInstance().isQuestionStarred(question.getId().toString());

            if (collect){
                FavoriteFactory.getInstance().canceIStarQuestion(question.getId());
                imgFavorite.setImageResource(android.R.drawable.star_off);
            }else {
                FavoriteFactory.getInstance().starQuestion(question.getId());
                imgFavorite.setImageResource(android.R.drawable.star_on );
            }
        });


        //题目
         tvContent=find(R.id.fragment_question_tv_content);
        String content=question.getContent();
        tvContent.setText(content);

        //选项
//        RadioGroup container=find(R.id.fragment_question_option_container);
//
//        List<Option> options=question.getOptions();
//        if (question.getType().equals(QuestionType.SINGLE_CHOICE)||question.getType().equals(QuestionType.JUDGE)){
//            for (Option option:options){
//                RadioButton radioButton=new RadioButton(getContext());
//                String radio=option.getLabel()+"."+option.getContent();
//                radioButton.setText(radio);
//                container.addView(radioButton);
//            }
//        }
//        if (question.getType().equals(QuestionType.MULTI_CHOICE)){
//            for (Option option:options){
//                CheckBox checkBox=new CheckBox(getContext());
//                String check=option.getLabel()+"."+option.getContent();
//                checkBox.setText(check);
//                container.addView(checkBox);
//            }
//        }
    }

    private void displayQuestion() {

        int label = pos + 1;
        String qType = label + "." + question.getType().toString();
        tvType.setText(qType);
        tvContent.setText(question.getContent());
        int starId = FavoriteFactory.getInstance().isQuestionStarred(question.getId().toString()) ?
                android.R.drawable.star_on : android.R.drawable.star_off;
        imgFavorite.setImageResource(starId);
        imgFavorite.setOnClickListener(v -> switchStar());
    }

    private void switchStar() {
        FavoriteFactory factory=FavoriteFactory.getInstance();
        if (factory.isQuestionStarred(question.getId().toString())){
            factory.canceIStarQuestion(question.getId());
            imgFavorite.setImageResource(android.R.drawable.star_off);

        }else {
            factory.starQuestion(question.getId());
            imgFavorite.setImageResource(android.R.drawable.star_on);

        }
    }

    private void initVies() {
        imgFavorite = find(R.id.fragment_question_img_favorite);
        tvContent=find(R.id.fragment_question_tv_content);
        rgOptions=find(R.id.fragment_question_option_container);
        if (isCommitted){
            rgOptions.setOnClickListener(v->new AlertDialog.Builder(getContext()).setMessage(question.getAnalysis()).show());
        }
    }

    private void generateOption() {
        List<Option> options = question.getOptions();
        for (Option option:options){
            CompoundButton btn=isMulti ? new CheckBox(getContext()): new RadioButton(getContext());
            String content=option.getLabel()+" . "+option.getContent();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                btn.setButtonTintList(ColorStateList.valueOf(Color.GRAY));
            }
            btn.setText(content);
            btn.setEnabled(!isCommitted);
            //添加点击监听，选中了就要记录到选项文件SharedPreferences，取消选中则从文件中把选项去掉
            rgOptions.addView(btn);
            btn.setOnCheckedChangeListener((buttonView, isChecked) ->
                    UserCookies.getInstance().changeOptionState(option,isChecked,isMulti));

            //勾选，到文件中是否存在选项的id，存在则勾选
            boolean shouldCheck = UserCookies.getInstance().isOptionSelected(option);
            if (isMulti){
                btn.setChecked(shouldCheck);
            }else if (shouldCheck){
                rgOptions.check(btn.getId());
            }

            /** 正确答案选项改变成绿色 **/
            if (isCommitted && option.isAnswer()){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    btn.setTextColor(getResources().getColor(R.color.colorPrimary,null));
                }else {
                    btn.setTextColor(getResources().getColor(R.color.colorPrimary));
                }
            }
            /****/

        }
    }



    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_question;
    }

    @Override
    public void search(String kw) {

    }
}
