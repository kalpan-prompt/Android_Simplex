package android.prompt.n2mf.adapter;

import android.content.Context;
import android.prompt.n2mf.R;
import android.prompt.n2mf.global.Global;
import android.prompt.n2mf.model.Fruit;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by prompt on 2/3/17.
 *
 * To bind the Fruit items with it's list as Fruit Name, Price, Calories, Protein, Vitamin A & Vitamin C
 *
 */

public class FruitLVAdapter extends BaseAdapter {
    private Context cntxt;
    private ArrayList<Fruit> listFruits;
    public FruitLVAdapter(Context context, ArrayList<Fruit> fruitsList) {
        this.cntxt = context;
        this.listFruits = new ArrayList<Fruit>();
        for (Fruit obj : fruitsList) {
            this.listFruits.add(obj);
        }
    }

    @Override
    public int getCount() {
        return listFruits.size();
    }

    @Override
    public Object getItem(int positon) {
        return listFruits.get(positon);
    }

    @Override
    public long getItemId(int positon) {
        return positon;
    }

    private class ViewHolder {
        TextView txtName, txtRate, txtCalories, txtProtein, txtVitA, txtVitC;
        ImageView imgFruit;

    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();

            convertView = LayoutInflater.from(cntxt).inflate(R.layout.item_lv_fruit, parent, false);

//            holder.imgFruit = (ImageView) convertView .findViewById(R.id.imgFruit);
            holder.txtName = (TextView) convertView.findViewById(R.id.txtName);
            holder.txtRate = (TextView) convertView.findViewById(R.id.txtRate);
            holder.txtCalories = (TextView) convertView.findViewById(R.id.txtCalories);
            holder.txtProtein = (TextView) convertView.findViewById(R.id.txtProtein);
            holder.txtVitA = (TextView) convertView.findViewById(R.id.txtVitA);
            holder.txtVitC = (TextView) convertView.findViewById(R.id.txtVitC);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.txtName.setText(listFruits.get(position).getName());
        holder.txtRate.setText(Html.fromHtml("<b>"+Fruit.getUnitRate(cntxt) +"</b> "+ Global.formatAmount(String.valueOf(listFruits.get(position).getRate()))));
        holder.txtCalories.setText(Html.fromHtml(String.valueOf(listFruits.get(position).getCalories())+"<b>"+ Fruit.getUnitCalories()+"</b>"));
        holder.txtProtein.setText(Html.fromHtml("<b>"+Fruit.getLabelProtein(true)+"</b>"+ Global.format2Decimal(String.valueOf(listFruits.get(position).getProtein())) + Fruit.getUnitProtein()));
        holder.txtVitA.setText(Html.fromHtml("<b>"+Fruit.getLabelVitA(true)+"</b>"+ String.valueOf(listFruits.get(position).getVitA()) + Fruit.getUnitVitA()));
        holder.txtVitC.setText(Html.fromHtml("<b>"+Fruit.getLabelVitC(true)+"</b>"+ Global.format1Decimal(String.valueOf(listFruits.get(position).getVitC())) + Fruit.getUnitVitC()));

        return convertView;
    }
}
