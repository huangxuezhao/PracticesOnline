package net.lzzy.practicesonline.activities.practicesonline.models.view;

/**
 * Created by lzzy_gxy on 2019/4/16.
 * Description:
 */
public enum QuestionType {;

    private String name;

    QuestionType(String name){
        this.name=name;
    }

    @Override
    public String toString() {
        return name;
    }

    public static QuestionType getInstance(int ordinal){
        for (QuestionType type:QuestionType.values()){
            if (type.ordinal()==ordinal){
                return type;
            }
        }
        return null;
    }

}
