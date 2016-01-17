package com.example.oluwole.historyapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by Oluwole on 04/12/2015.
 */
public class AddLocationsActivity extends Activity{
    final String PASSLIST = "PASSLIST";//Gets the ArrayList from the intent

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addmarker);


        final Intent ListIntent = getIntent();
        //TODO be able to get previous info, if something may have gone wrong

        final EditText location_name = (EditText) findViewById(R.id.LocNameEditText);
        final EditText location_description = (EditText) findViewById(R.id.LocDescriptionEditText);
        final EditText location_tags = (EditText) findViewById(R.id.TagEditText);

        Button deploy= (Button) findViewById(R.id.deploy);
        deploy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!ifNull(location_name,location_description,location_tags));
                else {
                    if (!isContained(ListIntent,location_name)) {
                        final Intent intent = new Intent();
                        intent.putExtra("location_name", location_name.getText().toString());
                        intent.putExtra("location_description", location_description.getText().toString());
                        String tags=HashTagScan(location_tags.getText().toString());
                        if (tags!=null) {
                            intent.putExtra("location_tags", tags);//tags--> #night#cold+' '
                            setResult(Activity.RESULT_OK, intent);
                            finish();
                        }
                        else
                            PrintToast("Please rewrite the tags (follow the initial hint).");


                    }

                }

            }
        });
    }

    private boolean ifNull(EditText name, EditText description,EditText tags){
        Toast toast;
        boolean result=true;
        if ((name.getText().toString().equals(""))){
            PrintToast("Please type the location's name");
            result=false;
        }
        if ((description.getText().toString().equals(""))){
            PrintToast("Please type the location's description");
            result=false;
        }
        if ((tags.getText().toString().equals(""))){
            PrintToast("Please write at least one tag");
            result=false;
        }

        return result;
    }
    private void PrintToast(String s){
        Toast.makeText(getApplicationContext(),s, Toast.LENGTH_SHORT).show();
    }

    private boolean isContained(Intent ListIntent,EditText location_name){
        if (ListIntent.getParcelableArrayListExtra(PASSLIST).contains(new StoreLocation(
                null,location_name.getText().toString(),"","",0,false,false))){
            //if the object name is already contained in the list then prompt the user to change name
            PrintToast("This Location's name was already taken, please change it.");
            return true;
        }
        else
            return false;
    }
    //TODO:
    //it will reformat the tag string before sending it back.
    //i.e: #space stars Mars --> #space#stars#Mars

    private String HashTagScan(String s){
        String tmp="";
        int i=0;
        //TODO: quite important, must check if the first char was a # or not
        if (s.charAt(i)!='#')
            return null;
        while (s.charAt(i)=='#') {
            tmp+=s.charAt(i);
            i++;
            while (s.charAt(i) != '#') {
                if (i>=s.length()){
                    break;
                }
                if (checkNotAllowedChar(s.charAt(i))){
                    i++;
                    if (i>=s.length()){
                        break;
                    }
                }
                else {
                    tmp+=s.charAt(i);
                    i++;
                    if (i>=s.length()){
                        break;
                    }
                }
            }

            if (i>=s.length()){
                tmp+=' ';
                break;
            }

        }
        return tmp;
    }//END HASHTAGSCAN

    private boolean checkNotAllowedChar(char current){
        final char[] NOT_ALLOWED={'/','.','$','[',']',' '};
        for (int i=0;i<NOT_ALLOWED.length;i++){
            if (current==NOT_ALLOWED[i])
                return true;
        }
        return false;
    }
}
