package com.jaehong.soo.animalvideo;

import android.content.Context;
import android.content.res.AssetManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class GetJsonData {

    public JSONObject getJsonObject(Context context, String fileName){
        JSONObject jsonObject = null;
        AssetManager assetManager = context.getAssets();

        try {
            AssetManager.AssetInputStream ais = (AssetManager.AssetInputStream) assetManager.open(fileName);

            BufferedReader br = new BufferedReader(new InputStreamReader(ais));

            StringBuffer stringBuffer = new StringBuffer();

            int bufferSize = 1024 * 1024;

            char readBuf[] = new char[bufferSize];
            int resultSize = 0;

            while ((resultSize = br.read(readBuf)) != -1){
                if(resultSize == bufferSize){
                    stringBuffer.append(readBuf);
                }else{
                    for (int i = 0; i < resultSize; i++){
                        stringBuffer.append(readBuf[i]);
                    }
                }
            }

            jsonObject = new JSONObject(stringBuffer.toString());



        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        
        return jsonObject;
    }
}
