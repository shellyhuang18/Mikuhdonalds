package com.example.puhbuh.mikuhdonalds;

/*
 * One of the UI component we use often is ListView, for example
 * when we need to show items in a vertical scrolling list.
 * One interesting aspect is this component can be deeply customized
 * and can be adapted to our needs. We will analyze
 *  the basic concepts behind the ListView class and how it is used.
 *  In this example, we will create a ListView with single selection mode,
 *  multiple selection mode with delete option.
 */

import java.util.ArrayList;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class Selection extends Activity {
    private ListView lView;
    ArrayList<String> data;
    Intent intent;


    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.selection);
        data = new ArrayList<String>();

        // 1. This part is similar to the ArrayAdapter we saw earlier. We create an array of fruits to be shown in a list
        String[] options = new String[] {"Has Delivery", "Has Takeaway"};

        ArrayAdapter<String> adapter= new ArrayAdapter<String>(this, android.R.layout.simple_list_item_checked, options);
        ListView lv= (ListView)findViewById(R.id.list);
        lv.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        lv.setAdapter(adapter);

        intent = new Intent(this, MapData.class);

        SparseBooleanArray checked = lv.getCheckedItemPositions();
        if(checked.get(0)){
            intent.putExtra("DELIVERY", "meal_delivery");
        }else{
            intent.putExtra("DELIVERY", "");
        }
        if(checked.get(1)){
            intent.putExtra("TAKEAWAY", "meal_takeaway");
        }else{
            intent.putExtra("TAKEAWAY", "");
        }
    }

    public void onProceed(View v){
        startActivity(intent);
    }
}