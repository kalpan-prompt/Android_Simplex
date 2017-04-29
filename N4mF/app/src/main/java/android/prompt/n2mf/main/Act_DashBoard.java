package android.prompt.n2mf.main;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.prompt.n2mf.R;
import android.prompt.n2mf.global.Global;
import android.widget.TabHost;

/**
 * It is a main activity which will show 2 tabs and each tab will have own activity for UI and other stubs...
 */
public class Act_DashBoard extends TabActivity {
    private TabHost tabHost;
    private TabHost.TabSpec tabSpecNutrient, tabSpecFruit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_dash_board);
        initView();
        postInitView();
        loadData();
        Global.hideSoftKeyboard(this);
    }
    private void initView(){
        tabHost = getTabHost();
        tabSpecNutrient = tabHost.newTabSpec("Nutrient");
        tabSpecFruit = tabHost.newTabSpec("Fruit");
    }
    private void postInitView(){
        loadTabNutrient();
        loadTabFruit();
    }
    private void loadData(){
        Global.setFruitsList();
        tabHost.addTab(tabSpecNutrient);
        tabHost.addTab(tabSpecFruit);
    }
    private void loadTabNutrient(){
        tabSpecNutrient.setIndicator("Nutrient");
        tabSpecNutrient.setContent(new Intent(this, Act_Nutrient.class));
    }
    private void loadTabFruit(){
        tabSpecFruit.setIndicator("Fruit");
        tabSpecFruit.setContent(new Intent(this, Act_Fruit.class));
    }
}
