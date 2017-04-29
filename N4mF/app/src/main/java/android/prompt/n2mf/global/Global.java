package android.prompt.n2mf.global;

import android.app.Activity;
import android.content.Context;
import java.math.BigDecimal;
import android.prompt.n2mf.model.Fruit;
import android.text.TextUtils;
import android.util.Log;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;

/**
 * Created by prompt on 2/3/17.
 * To store all global or static content which will use many places
 */

public class Global {

    public static ArrayList<Fruit> fruitsList;
    public static final String REGEX_DEC_LTH_4="^\\d{0,4}?$";
    public static final String REGEX_DEC_LTH_6="^\\d{0,6}(\\.\\d{1,3})?$";

    /**
     *
     * @param amount require to set amount format (e.g. 25)
     * @return  return the String value as 2 decimal point (e.g. 25.00)
     */
    public static String formatAmount(String amount){
        return format2Decimal(amount);
    }

    /**
     *
     * @param value require to set value format (e.g. 25)
     * @return  return the String value as 1 decimal point (e.g. 25.0)
     */
    public static String format1Decimal(String value){
        return format(value, 1);
    }
    /**
     *
     * @param value require to set value format (e.g. 25)
     * @return  return the String value as 2 decimal point (e.g. 25.00)
     */
    public static String format2Decimal(String value){
        return format(value, 2);
    }
    /**
     *
     * @param value require to set value format (e.g. 25)
     * @return  return the String value as 3 decimal point (e.g. 25.000)
     */
    public static String format3Decimal(String value){
        return format(value, 3);
    }
    /**
     *
     * @param value require to set value format (e.g. 25)
     * @param decimalPlace require to set decimal Place in format
     * @return  return the String value as n decimal point (e.g. 25.n)
     */
    public static String format(String value, int decimalPlace){
        return isValid(value)? String.format(Locale.US, "%."+decimalPlace+"f", Float.parseFloat(value.toString())):"";
    }

    /**
     *
     * @param value String type value which require to make in proper decimal value
     * @param decimalPlace  require to set decimal Place in format
     * @return  the double value with the decimal places as format in value
     */
    public static double roundDouble(String value, int decimalPlace) {
        return isValid(value) ? round(Double.parseDouble(value), decimalPlace) : 0d;
    }

    /**
     *
     * @param value double type value which require to make in proper decimal value
     * @param numberOfDigitsAfterDecimalPoint  require to set decimal Place in format
     * @return  the double value with the decimal places as format in value
     */
    public static double round(double value, int numberOfDigitsAfterDecimalPoint) {
        try {
            BigDecimal bigDecimal = new BigDecimal(value);
            bigDecimal = bigDecimal.setScale(numberOfDigitsAfterDecimalPoint,
                    BigDecimal.ROUND_HALF_UP);
            return bigDecimal.doubleValue();
        }catch(Exception ex){
            Log.e("Global_round_exception",ex.getMessage());
        }
        return value;
    }

    /**
     *
     * @param value to require check value valid or not
     * @return  true if value is valid otherwise false
     */
    public static boolean isValid(Object value){
        return (value!=null && !TextUtils.isEmpty(String.valueOf(value).trim()) && !String.valueOf(value).trim().equalsIgnoreCase("null"));
    }

    /**
     * Set the Fruits
     */
    public static void setFruitsList(){
        fruitsList = new ArrayList<Fruit>();
        fruitsList.add(new Fruit("Apple (1 medium)",    "Apple (medium)",       95,    0.47f,  98,    8.4f,   10f));
        fruitsList.add(new Fruit("Banana (1 medium)",   "Banana (medium)",      105,   1.29f,  76,    10.3f,  4f));
        fruitsList.add(new Fruit("Blackberry (1 cup)",  "Blackberry (cup)",     62,    2f,     308,   30.2f,  30f));
        fruitsList.add(new Fruit("Grapes (1 cup)",      "Grapes (cup)",         104,   1.09f,  100,   16.3f,  28f));
        fruitsList.add(new Fruit("Orange (1 medium)",   "Orange (medium)",      62,    1.23f,  295,   69.7f,  20f));
        fruitsList.add(new Fruit("Papayas (1 cup)",     "Papayas ( cup)",       55,    0.85f,  1532,  86.5f,  40f));
        sortFruits(fruitsList);
    }

    /**
     * To sort the list by name
     *
     * @param fruitsList list of the fruits
     */
    public static void sortFruits(ArrayList<Fruit> fruitsList){
        Collections.sort(fruitsList, new Comparator<Fruit>() {

            @Override
            public int compare(Fruit lhs, Fruit rhs) {
                if (lhs != null && rhs!=null && !TextUtils.isEmpty(lhs.getName()) && !TextUtils.isEmpty(rhs.getName())) {
                    return lhs.getName().compareToIgnoreCase(rhs.getName());
                }
                return 0;
            }
        });
    }

    /**
     *
     * @param activity object of Activity to hide the keyboard from activity
     */
    public static void hideSoftKeyboard(Activity activity) {
        if (activity != null) {
            try {
                if(activity.getCurrentFocus()!=null)
                    ((InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
                else
                    activity.getWindow().setSoftInputMode((WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN));
            } catch (Exception ex) {
                Log.e("Global hideKb_exception",ex.getMessage());
            }
        }
    }

    /**
     * To show the Toast from activity
     * @param cntxt object of Activity context
     * @param text
     */
    public static void showToast(Context cntxt, String text){
        Toast.makeText(cntxt, text, Toast.LENGTH_SHORT).show();
    }
}
