package android.prompt.n2mf.main;

import android.app.Activity;
import android.os.Bundle;
import android.prompt.n2mf.R;
import android.prompt.n2mf.adapter.FruitLVAdapter;
import android.prompt.n2mf.global.Global;
import android.widget.ListView;

/**
 *  This activity shows the Fruits in list. Which will be consider during the suggestion.
 */
public class Act_Fruit extends Activity {

    private ListView lvFruit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_fruit);
        initView();
        loadData();
        Global.hideSoftKeyboard(this);
    }
    private void initView(){
        lvFruit = (ListView) findViewById(R.id.lvFruit);
    }
    private void loadData(){
        lvFruit.setAdapter(new FruitLVAdapter(getApplicationContext(), Global.fruitsList));
    }
}
