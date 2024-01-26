package com.example.quizgame;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class QuestionManager {

    public static final String TAG = "QuestionManager";
    private List<Question> questions = new ArrayList<>();
    public boolean finishAnswer = false;

    public QuestionManager(Context context) {

        try {
            InputStream inputStream = context.getAssets().open("questions.json");
            String json = null;
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();
            json = new String(buffer, "UTF-8");
            parseJson(json);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private void parseJson(String json){
        try {
            JSONArray questionsJsonArray=new JSONArray(json);
            JSONObject questionJsonObject;
            for (int i = 0; i < questionsJsonArray.length(); i++) {
                questionJsonObject=questionsJsonArray.getJSONObject(i);
                Question question=new Question();
                question.setAnswer(questionJsonObject.getInt("answer"));
                question.setQuestion(questionJsonObject.getString("question"));
                List<String> options=new ArrayList<>();
                JSONArray optionsJsonArray=questionJsonObject.getJSONArray("options");
                for (int j = 0; j < optionsJsonArray.length(); j++) {
                    options.add(optionsJsonArray.getString(j));
                }
                question.setOptions(options);
                this.questions.add(question);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public Question getQuestion(){
        if (questions.isEmpty()) return null;

        int position=0;
        while (isAskedBefore(position)){
            position++;
        }
        askedQuestions.add(position);
        return questions.get(position);
    }

    private List<Integer> askedQuestions=new ArrayList<>();

    private boolean isAskedBefore(int position){
        if (askedQuestions.size()==questions.size()){
            askedQuestions.clear();
        }
        if (askedQuestions.size()==questions.size()-1){
            finishAnswer=true;
        }else {
            finishAnswer=false;
        }
        return askedQuestions.contains(position);
    }

}
