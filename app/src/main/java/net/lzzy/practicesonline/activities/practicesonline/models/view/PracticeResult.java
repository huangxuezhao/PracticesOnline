package net.lzzy.practicesonline.activities.practicesonline.models.view;

import net.lzzy.practicesonline.activities.practicesonline.constants.ApiConstants;
import net.lzzy.practicesonline.activities.practicesonline.models.Question;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lzzy_gxy on 2019/5/8.
 * Description:
 */
public class PracticeResult {
    private static final String SPLITTER = ",";
    private List<QuestionResult> results;
    private int id;
    private String info;

    public PracticeResult(List<QuestionResult> results, int apiId, String ionf) {
        this.id = apiId;
        this.info = ionf;
        this.results = results;
    }


    public List<QuestionResult> getResults() {
        return results;
    }

    public int getId() {
        return id;
    }

    public String getInfo() {
        return info;
    }
private double getRatio(){
        int rightCount=0;
        for (QuestionResult result:results){
            if (result.isRight()){
                rightCount++;

            }
        }
        return rightCount*1.0/results.size();
}
    private String getWrongOrders(){
        int i=0;
        String ids="";
        for (QuestionResult result:results){
            i++;
            if (!result.isRight()){
                ids=ids.concat(i+SPLITTER);
            }
        }
        if (ids.endsWith(SPLITTER)){
            ids=ids.substring(0,ids.length()-1);
        }
        return ids;
    }
    public JSONObject toJson() throws JSONException {
        JSONObject json=new JSONObject();
        json.put(ApiConstants.JSON_RESULT_API_ID,id);
        json.put(ApiConstants.JSON_RESULT_PERSON_INFO,info);
        json.put(ApiConstants.JSON_RESULT_SCORE_RATIO,
                new DecimalFormat("#.00").format(getRatio()));
        json.put(ApiConstants.JSON_RESULT_WRONG_IDS,getWrongOrders());

        return json;
    }

}
