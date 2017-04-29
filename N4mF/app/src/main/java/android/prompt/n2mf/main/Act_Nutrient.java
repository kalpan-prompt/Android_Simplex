package android.prompt.n2mf.main;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.prompt.n2mf.R;
import android.prompt.n2mf.adapter.NutrientLVAdapter;
import android.prompt.n2mf.global.Global;
import android.prompt.n2mf.global.SimplexLP;
import android.prompt.n2mf.model.Fruit;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * This activity helps the end user to give their requirement from the Calories, Protein, Vitamin A & Vitamin C.
 * Base on it they can generate the suggestion which fit their requirement from the available Fruits.
 */
public class Act_Nutrient extends Activity implements View.OnClickListener {

    TextView txtRequirements, txtCalories, txtProtein, txtVitaminsA, txtVitaminsC, txtSuggestion;
    EditText edtCalories, edtProtein, edtVitaminsA, edtVitaminsC;
    Button btnSuggest, btnClear;
    ListView lvNutrient;
    View viewSeparatorSuggestion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_nutrient);
        initView();
        initSuggestion();
        postInitView();
        addListener();
    }

    private void initView() {
        txtRequirements = (TextView) findViewById(R.id.txtRequirements);
        txtCalories = (TextView) findViewById(R.id.txtCalories);
        txtProtein = (TextView) findViewById(R.id.txtProtein);
        txtVitaminsA = (TextView) findViewById(R.id.txtVitaminsA);
        txtVitaminsC = (TextView) findViewById(R.id.txtVitaminsC);
        edtCalories = (EditText) findViewById(R.id.edtCalories);
        edtProtein = (EditText) findViewById(R.id.edtProtein);
        edtVitaminsA = (EditText) findViewById(R.id.edtVitaminsA);
        edtVitaminsC = (EditText) findViewById(R.id.edtVitaminsC);
        btnSuggest = (Button) findViewById(R.id.btnSuggest);
        btnClear = (Button) findViewById(R.id.btnClear);
        txtSuggestion = (TextView) findViewById(R.id.txtSuggestion);
        lvNutrient = (ListView) findViewById(R.id.lvNutrient);
        viewSeparatorSuggestion = (View) findViewById(R.id.viewSeparatorSuggestion);
    }

    private void postInitView() {
        txtCalories.setText(Fruit.getLabelCalories().trim());
        txtProtein.setText(Fruit.getLabelProtein(false).trim());
        txtVitaminsA.setText(Fruit.getLabelVitA(false).trim());
        txtVitaminsC.setText(Fruit.getLabelVitC(false).trim());
        edtCalories.setHint("55" + Fruit.getUnitCalories());
        edtProtein.setHint("0.85" + Fruit.getUnitProtein());
        edtVitaminsA.setHint("1532" + Fruit.getUnitVitA());
        edtVitaminsC.setHint("86.5" + Fruit.getUnitVitC());
        addHeaderView(lvNutrient);
    }

    private void addHeaderView(ListView lv) {
        View view = LayoutInflater.from(this).inflate(R.layout.item_header_lv_fruit_nutrient_suggestion, null);
        //lv.removeHeaderView(view);
        lv.addHeaderView(view, null, false);
    }

    private void addListener() {
        btnSuggest.setOnClickListener(this);
        btnClear.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Global.hideSoftKeyboard(this);
        switch (view.getId()) {
            case R.id.btnSuggest:
                showSuggestion();
                break;
            case R.id.btnClear:
                initSuggestion();
                break;
        }
    }

    /**
     * Make it empty view or show view as first time open
     */
    private void initSuggestion() {
        edtCalories.setText("");
        edtProtein.setText("");
        edtVitaminsA.setText("");
        edtVitaminsC.setText("");
        hideSuggestion();
    }

    /**
     * Hide the UI for Suggested Nutrient including list
     */
    private void hideSuggestion() {
        showHideSuggestion(View.GONE);
        lvNutrient.setAdapter(null);
    }
    /**
     * Hide/Show the UI for Suggested Nutrient without Suggested Nutrient fruit list
     */
    private void showHideSuggestion(int visibility) {
        txtSuggestion.setVisibility(visibility);
        viewSeparatorSuggestion.setVisibility(visibility);
        lvNutrient.setVisibility(visibility);
    }

    /**
     * Handle the suggestion button effect (i.e. validate the form and call the class for suggestion)
     */
    private void showSuggestion() {
        hideSuggestion();
        if (validate()) {
            new GenerateNutrientFromFruit(this, Global.fruitsList).execute();
        }
    }

    /**
     *
     *  @return true if form is not empty and logical form validation is proper.
     */
    private boolean validate() {
        if (requiredFieldValidate() && logicalValidate())
            return true;
        return false;
    }

    /**
     *
     * @return true if any of the four (Calories, Protein, Vitamin A & Vitamin C.) are valid in the form otherwise false.
     */
    private boolean requiredFieldValidate() {
        if (edtCalories.getText().length() != 0 || edtProtein.getText().length() != 0
                || edtVitaminsA.getText().length() != 0 || edtVitaminsC.getText().length() != 0)
            return true;
        else {
            showToast(getResources().getString(R.string.msg_erf));
            return false;
        }
    }
    /**
     *
     * @return false if any of the four (Calories, Protein, Vitamin A & Vitamin C.) are logical not valid in the form otherwise true.
     */
    private boolean logicalValidate() {
        if(isValid(edtCalories, txtCalories) && isValid(edtVitaminsA, txtVitaminsA)
                && isValidDecimal(edtProtein, txtProtein) && isValidDecimal(edtVitaminsC, txtVitaminsC))
            return true;
        return false;
    }

    /**
     *
     * @param edtText   object of editText for Calories or Vitamin A
     * @param txtView   object textView for Calories or Vitamin A
     * @return  true if editText Value is match with Regex and the value is more then 0 (e.g. 4 is valid while 0 is not valid)
     */
    private boolean isValid(EditText edtText, TextView txtView){
        if (!TextUtils.isEmpty(getValue(edtText))) {
            if (!getValue(edtText).matches(Global.REGEX_DEC_LTH_4)) {
                showToast(getResources().getString(R.string.msg_eod) +" "+ txtView.getText().toString());
                return false;
            }else
                return isMoreThenZero(edtText, txtView);
        }
        return true;
    }
    /**
     *
     * @param edtText   object of editText for Protein or Vitamin C
     * @param txtView   object textView for Protein or Vitamin C
     * @return  true if editText Value is match with Regex and the value is more then 0 (e.g. 4.5 is valid while 4. is not valid)
     */
    private boolean isValidDecimal(EditText edtText, TextView txtView){
        if (!TextUtils.isEmpty(getValue(edtText))) {
            if (!getValue(edtText).matches(Global.REGEX_DEC_LTH_6)) {
                showToast(getResources().getString(R.string.msg_epd) +" "+ txtView.getText().toString());
                return false;
            }else
                return isMoreThenZero(edtText, txtView);
        }
        return true;
    }

    /**
     *
     * @param edtText   object of editText
     * @param txtView   object textView
     * @return true if editText value is more then 0, otherwise false
     */
    private boolean isMoreThenZero(EditText edtText, TextView txtView){
        if (getValue(edtText).matches(Global.REGEX_DEC_LTH_6) && Float.parseFloat(getValue(edtText))<=0){
            showToast(getResources().getString(R.string.msg_emt0v) +" "+ txtView.getText().toString());
            return false;
        }
        return true;
    }

    /**
     *
     * @param edtText  object of editText
     * @return String value of the editText as trim if data present otherwise empty string value
     */
    private String getValue(EditText edtText) {
        return (edtText.getText().length() > 0) ? edtText.getText().toString().trim() : "";
    }

    /**
     *
     * @param msg String message which require to show in Toast
     */
    private void showToast(String msg) {
        Global.showToast(getApplicationContext(), msg);
    }

    /**
     * Async Class to generate the Nutrient from fruit list based on the added requirement
     */
    private class GenerateNutrientFromFruit extends AsyncTask<Void, Void, ArrayList<Fruit>> {
        ProgressDialog prgDialog;
        // List of fruits which we will take part in to the requirement nutrients (i.e. simplex algorithm)
        ArrayList<Fruit> fruitsList;
        //fruitCalories is a constraint value which set from the list of Fruits by taking each Calories
        //fruitProtein is a constraint value which set from the list of Fruits by taking each Protein
        //fruitVitA is a constraint value which set from the list of Fruits by taking each Vitamins A
        //fruitVitC is a constraint value which set from the list of Fruits by taking each Vitamins C
        //fruitRequirement is a base value of the constraint which will comes in Simplex right side as B
        //fruitRate is a Z value which set from the list of Fruits by taking each rate (i.e. to minimize the cost)
        //fruitArtificial is value of the artificial variable which will take part in this suggestion
        //fruitSurplush is value of the surplus variable which will take part in this suggestion, each Surplush value in the array of Surplush as 0 as per the Z co-efficient of the Artificial
        //note: we are taking the Z as minimum so here artifial and surplus variable will be same size as per the constraint length (i.e. totalRequirement)
        double[] fruitCalories, fruitProtein, fruitVitA, fruitVitC, fruitRequirement, fruitRate,
                fruitArtificial, fruitSurplush;
        //fruitsNutrient main array including the constraint array with its surplus and artificial value individual
        double[][] fruitsNutrient;
        //fruitCoEffiecient initially the column number which will take place in iteration
        int[] fruitCoefficient;
        int totalRequirement; //totalRequirement no of constraint in the suggestion
        boolean isRequirementCalories, isRequirementProtein, isRequirementVitA, isRequirementVitC;
        Activity act;
        SimplexLP nutrientRequirement;

        public GenerateNutrientFromFruit(Activity activity, ArrayList<Fruit> listFruits) {
            totalRequirement = 0;
            act = activity;
            fruitsList = listFruits;
            setRequirementCount();
        }

        /**
         * To set the constraint for the requirement based on the added requirement among the four (Calories, Protein, Vitamin A & Vitamin C.)
         * Bases on the requirement we will set the Base value for the simplex method in the fruitRequirement array
         */
        private void setRequirementCount() {
            if (edtCalories.getText().length() != 0) {
                isRequirementCalories = true;
                ++totalRequirement;
            }
            if (edtProtein.getText().length() != 0) {
                isRequirementProtein = true;
                ++totalRequirement;
            }
            if (edtVitaminsA.getText().length() != 0) {
                isRequirementVitA = true;
                ++totalRequirement;
            }
            if (edtVitaminsC.getText().length() != 0) {
                isRequirementVitC = true;
                ++totalRequirement;
            }
            fruitRequirement = new double[totalRequirement];
            int row = 0;
            if(isRequirementCalories && row<totalRequirement){
                fruitRequirement[row++] = Integer.parseInt(getValue(edtCalories));
            }
            if(isRequirementProtein && row<totalRequirement){
                fruitRequirement[row++] = Float.parseFloat(getValue(edtProtein));
            }
            if(isRequirementVitA && row<totalRequirement){
                fruitRequirement[row++] = Integer.parseInt(getValue(edtVitaminsA));
            }
            if(isRequirementVitC && row<totalRequirement){
                fruitRequirement[row++] = Float.parseFloat(getValue(edtVitaminsC));
            }
        }

        @Override
        protected void onPreExecute() {
            prgDialog = new ProgressDialog(act);
            prgDialog.setCancelable(false);
            prgDialog.setMessage(getResources().getString(R.string.msg_dia_suggestion));
            prgDialog.show();
            super.onPreExecute();
        }

        @Override
        protected ArrayList<Fruit>  doInBackground(Void... voids) {
            if (fruitsList == null || (fruitsList != null && fruitsList.size() == 0) || totalRequirement<=0)
                return null;
            try {
                fruitsNutrient = new double[totalRequirement][];
                fruitRate = new double[fruitsList.size()];
                fruitSurplush = new double[totalRequirement];
                fruitArtificial = new double[totalRequirement];
                fruitCoefficient = new int[totalRequirement];
                int row = 0;
                if (isRequirementCalories) {
                    fruitCalories = new double[fruitsList.size() + (totalRequirement*2)];
                    for (int column = 0; column < fruitsList.size(); column++) {
                        fruitCalories[column] = fruitsList.get(column).getCalories();//set each calories value in the array of Calories for constraint
                    }
                    fruitCalories[fruitsList.size()+row] = -1;//set each surplus value in the array of Calories for constraint
                    fruitCalories[fruitsList.size()+row+totalRequirement] = 1;//set each artificial value in the array of Calories for constraint
                    fruitCoefficient[row] = fruitsList.size()+row+totalRequirement;//set each artificial value in the array of fruitCoEffiecient for Calories constraint
                    fruitsNutrient[row] = fruitCalories;//add this constraint to the main array which will take part in iteration
                    ++row;
                }

                if (isRequirementProtein) {
                    fruitProtein = new double[fruitsList.size() + (totalRequirement*2)];
                    for (int column = 0; column < fruitsList.size(); column++) {
                        fruitProtein[column] = fruitsList.get(column).getProtein();//set each protein value in the array of Protein for constraint
                    }
                    fruitProtein[fruitsList.size()+row] = -1;//set each surplus value in the array of Protein for constraint
                    fruitProtein[fruitsList.size()+row+totalRequirement] = 1;//set each artificial value in the array of Protein for constraint
                    fruitCoefficient[row] = fruitsList.size()+row+totalRequirement;//set each artificial value in the array of fruitCoEffiecient for Protein constraint
                    fruitsNutrient[row] = fruitProtein;//add this constraint to the main array which will take part in iteration
                    ++row;
                }

                if (isRequirementVitA) {
                    fruitVitA = new double[fruitsList.size() + (totalRequirement*2)];
                    for (int column = 0; column < fruitsList.size(); column++) {
                        fruitVitA[column] = fruitsList.get(column).getVitA();//set each vitamin A value in the array of VitA for constraint
                    }
                    fruitVitA[fruitsList.size()+row] = -1;//set each surplus value in the array of VitA for constraint
                    fruitVitA[fruitsList.size()+row+totalRequirement] = 1;//set each artificial value in the array of VitA  for constraint
                    fruitCoefficient[row] = fruitsList.size()+row+totalRequirement;//set each artificial value in the array of fruitCoEffiecient for VitA constraint
                    fruitsNutrient[row] = fruitVitA;//add this constraint to the main array which will take part in iteration
                    ++row;
                }

                if (isRequirementVitC) {
                    fruitVitC = new double[fruitsList.size() + (totalRequirement*2)];
                    for (int column = 0; column < fruitsList.size(); column++) {
                        fruitVitC[column] = fruitsList.get(column).getVitC();//set each vitamin C value in the array of VitC for constraint
                    }
                    fruitVitC[fruitsList.size()+row] = -1;//set each surplus value in the array of VitC for constraint
                    fruitVitC[fruitsList.size()+row+totalRequirement] = 1;//set each artificial value in the array of VitC for constraint
                    fruitCoefficient[row] = fruitsList.size()+row+totalRequirement;
                    fruitsNutrient[row] = fruitVitC;//add this constraint to the main array which will take part in iteration
                    ++row;
                }
                for (int column = 0; column < fruitRate.length; column++) {
                    fruitRate[column] = (-1 * fruitsList.get(column).getRate());//set each rate value in the array of rate for fruits
                }
                for (int column = 0; column < fruitArtificial.length; column++) {
                    fruitArtificial[column] = -1;//set each artificial value in the array of artificial as -1 as per the Z co-efficient of the Artificial
                }
                //We are creating the object of the simplex as twoPhase problem
                nutrientRequirement = new SimplexLP(
                    fruitsNutrient, fruitRequirement, fruitRate,
                    fruitSurplush, fruitArtificial, fruitCoefficient);

                nutrientRequirement.startIteration();//start the iteration of Simplex
                if (nutrientRequirement.isFeasibleSolution && nutrientRequirement.secondPhase != null // isFeasibleSolution if true then there will be a second phase
                        && nutrientRequirement.secondPhase.isFeasibleSolution) {// secondPhase.isFeasibleSolution if true then there will be a solution
                    ArrayList<Fruit> suggestedFruitList = new ArrayList<Fruit>();
                    float totalQty =0, totalRate = 0;
                    for (int index = 0; index < nutrientRequirement.secondPhase.var.length; index++)
                    {
                        //secondPhase.var contains the column index which will be count in solution
                        //secondPhase.bCoefficientSolved contains the value which we need to set/get it for the specified the column index
                        if (nutrientRequirement.secondPhase.var[index] < fruitsList.size() && nutrientRequirement.secondPhase.bCoefficientSolved[index] >0)
                        {
                            totalQty += nutrientRequirement.secondPhase.bCoefficientSolved[index];
                            totalRate += Float.parseFloat(String.valueOf(Global.roundDouble(String.valueOf(fruitsList.get(nutrientRequirement.secondPhase.var[index]).getRate()),3) * nutrientRequirement.secondPhase.bCoefficientSolved[index]));
                            suggestedFruitList.add(new Fruit(
                                    fruitsList.get(nutrientRequirement.secondPhase.var[index]).getName(),
                                    fruitsList.get(nutrientRequirement.secondPhase.var[index]).getDisplayName(),
                                    fruitsList.get(nutrientRequirement.secondPhase.var[index]).getCalories(),
                                    fruitsList.get(nutrientRequirement.secondPhase.var[index]).getProtein(),
                                    fruitsList.get(nutrientRequirement.secondPhase.var[index]).getVitA(),
                                    fruitsList.get(nutrientRequirement.secondPhase.var[index]).getVitC(),
                                    Float.parseFloat(String.valueOf(Global.roundDouble(String.valueOf(fruitsList.get(nutrientRequirement.secondPhase.var[index]).getRate()),3) * nutrientRequirement.secondPhase.bCoefficientSolved[index])),
                                    nutrientRequirement.secondPhase.bCoefficientSolved[index]
                            ));
                        }
                    }
                    if(suggestedFruitList.size()>0){
                        Global.sortFruits(suggestedFruitList);
                        suggestedFruitList.add(new Fruit(
                                "", "", 0, 0, 0, 0,
                                totalRate,
                                totalQty
                        ));
                        return suggestedFruitList;
                    }
                }
            }catch (Exception ex){
                Log.e("SimplexLP_startItration",ex.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<Fruit> suggestedFruitList) {
            super.onPostExecute(suggestedFruitList);
            if(suggestedFruitList!=null && suggestedFruitList.size()>0){//if list is not empty then show the suggested fruits for specified requirement else showing the message
                lvNutrient.setAdapter(new NutrientLVAdapter(getApplicationContext(), lvNutrient, suggestedFruitList));
                if (prgDialog.isShowing())
                    prgDialog.cancel();
                showHideSuggestion(View.VISIBLE);
            }else{
                feedSuggestionNotFound();
            }
        }
        private void feedSuggestionNotFound(){
            if (prgDialog.isShowing())
                prgDialog.cancel();
            showToast(getResources().getString(R.string.msg_ugs_pta));
            hideSuggestion();
        }
    }
}

