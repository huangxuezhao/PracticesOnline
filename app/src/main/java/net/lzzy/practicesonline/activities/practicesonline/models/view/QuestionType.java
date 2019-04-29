package net.lzzy.practicesonline.activities.practicesonline.models.view;

/**
 * @author lzzy_Colo
 * @date 2019/4/16
 * Description:
 */
public enum QuestionType {
    /**
     *
     */
    SINGLE_CHOICE("单项选择"), MULTI_CHOICE("不定项选择"), JUDGE("判断");

    private String name;

    QuestionType(String name) {
        this.name = name;
    }



    @Override
    public String toString() {
        return name;
    }

    public static QuestionType getQuestiontype(int ordinal) {
        for (QuestionType type : QuestionType.values()) {
            return type;
        }
        return null;
    }
}
