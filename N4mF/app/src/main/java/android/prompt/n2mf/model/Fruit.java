package android.prompt.n2mf.model;

import android.content.Context;
import android.prompt.n2mf.R;

/**
 * Created by prompt on 2/3/17.
 */

public class Fruit {
    private String name, displayName;
    private int calories, vitA;
    private float protein, vitC, rate;
    private double qty;

    public Fruit(String fruitName, String displayName, int fruitCalories, float fruitProtein, int fruitVitA,
                 float fruitVitC, float fruitRate, double qty){
        this(fruitName, displayName, fruitCalories, fruitProtein, fruitVitA, fruitVitC, fruitRate);
        setQty(qty);
    }
    public Fruit(String fruitName, String displayName, int fruitCalories, float fruitProtein, int fruitVitA,
                 float fruitVitC, float fruitRate){
        setName(fruitName);setDisplayName(displayName);setCalories(fruitCalories);setProtein(fruitProtein);setVitA(fruitVitA);
        setVitC(fruitVitC);setRate(fruitRate);
    }
   /* public Fruit(int iconResource, String fruitName, String displayName, int fruitCalories, float fruitProtein, int fruitVitA,
                 float fruitVitC, float fruitRate){
        this(fruitName, displayName, fruitCalories, fruitProtein, fruitVitA, fruitVitC, fruitRate);
    }
    public Fruit(int iconResource, String fruitName, String displayName, int fruitCalories, float fruitProtein, int fruitVitA,
                 float fruitVitC, float fruitRate, double qty){
        this(fruitName, displayName, fruitCalories, fruitProtein, fruitVitA, fruitVitC, fruitRate, qty);
    }*/

    public double getQty() { return qty;}

    public void setQty(double qty) {
        this.qty = qty;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public int getCalories() {
        return calories;
    }

    public void setCalories(int calories) {
        this.calories = calories;
    }

    public float getProtein() {
        return protein;
    }

    public void setProtein(float protein) {
        this.protein = protein;
    }

    public int getVitA() {
        return vitA;
    }

    public void setVitA(int vitA) {
        this.vitA = vitA;
    }

    public float getVitC() {
        return vitC;
    }

    public void setVitC(float vitC) {
        this.vitC = vitC;
    }

    public float getRate() {
        return rate;
    }

    public void setRate(float rate) {
        this.rate = rate;
    }

    public static String getLabelCalories() {
        return "Calories";
    }
    public static String getUnitCalories() {
        return " calories";
    }
    public static String getLabelProtein(boolean isLabel) {
        return "Protein"+(isLabel?": ":"");
    }
    public static String getUnitProtein() {
        return " grams";
    }
    public static String getLabelVitA(boolean isLabel) {
        return "Vitamin A"+(isLabel?": ":"");
    }
    public static String getUnitVitA() {
        return " IU";
    }
    public static String getLabelVitC(boolean isLabel) {
        return "Vitamin C"+(isLabel?": ":"");
    }
    public static String getUnitVitC() {
        return " mg";
    }
    public static String getUnitRate(Context cntxt) {
        return cntxt.getResources().getString(R.string.abbreviation_rs);//"Rate: Rs. "
    }

}
