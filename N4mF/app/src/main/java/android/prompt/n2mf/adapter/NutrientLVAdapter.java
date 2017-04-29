package android.prompt.n2mf.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.prompt.n2mf.R;
import android.prompt.n2mf.global.Global;
import android.prompt.n2mf.model.Fruit;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by prompt on 2/3/17.
 *
 *  To bind the Fruit items with it's list as Fruit Name, total quantity and total Price for Calories, Protein, Vitamin A & Vitamin C
 *  which matches with the required parameters as nutrients
 */

public class NutrientLVAdapter extends BaseAdapter {
    private Context cntxt;
    private ArrayList<Fruit> listFruits;
    public NutrientLVAdapter(Context context, ListView lv, ArrayList<Fruit> fruitsList) {
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
        TextView txtName, txtQty, txtRate;
        ImageView imgFruit;

    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();

            convertView = LayoutInflater.from(cntxt).inflate(R.layout.item_lv_fruit_nutrient_suggestion, parent, false);

//            holder.imgFruit = (ImageView) convertView .findViewById(R.id.imgFruit);
            holder.txtName = (TextView) convertView.findViewById(R.id.txtName);
            holder.txtQty = (TextView) convertView.findViewById(R.id.txtQty);
            holder.txtRate = (TextView) convertView.findViewById(R.id.txtRate);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.txtName.setText(listFruits.get(position).getDisplayName());
        holder.txtQty.setText(Global.format3Decimal(String.valueOf(listFruits.get(position).getQty())));
        holder.txtRate.setText(Html.fromHtml("<b>"+Fruit.getUnitRate(cntxt) +"</b> "+ Global.formatAmount(String.valueOf(listFruits.get(position).getRate()))));
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if((position+1)!=getCount())
                    Global.showToast(cntxt, "You have to eat "+ holder.txtQty.getText().toString() +" "+listFruits.get(position).getName()+" which have cost "+holder.txtRate.getText().toString());
                else
                    Global.showToast(cntxt, "You have to eat total "+ holder.txtQty.getText().toString() +" fruits"+" which have total cost "+holder.txtRate.getText().toString());
            }
        });
        if((position+1)==getCount()){
            holder.txtRate.setText(Html.fromHtml("<b>" +holder.txtRate.getText().toString()+"</b>"));
            holder.txtQty.setText(Html.fromHtml("<b>" +holder.txtQty.getText().toString()+"</b>"));
//            holder.txtQty.setTypeface(holder.txtQty.getTypeface(), Typeface.BOLD);
        }
        return convertView;
    }
}

