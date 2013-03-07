package de.alosdev.android.customerschoice.demo;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.ListView;
import android.widget.Toast;
import de.alosdev.android.customerschoice.CustomersChoice;
import de.alosdev.customerschoice.demo.R;


public class ShoppingCardActivity extends Activity {
  private static final String VARIANT_NAME = "buttonColor";
  double amount = 0.0;
  private int buttonVariant;
  private static final String BUNDLE_VARIANT = "bundle.variant.color";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.shopping_card);

    ListView list = (ListView) findViewById(R.id.list);
    list.setAdapter(new Adapter());

    Button button = (Button) findViewById(R.id.order);
    button.setOnClickListener(new OnClickListener() {
        @Override
        public void onClick(View v) {
          Toast.makeText(ShoppingCardActivity.this, "you ordered for: " + amount, Toast.LENGTH_SHORT).show();
          CustomersChoice.reachesGoal(VARIANT_NAME);
          finish();
        }
      });

    if (null == savedInstanceState) {
      buttonVariant = CustomersChoice.getVariant(this, VARIANT_NAME);
    } else {
      buttonVariant = savedInstanceState.getInt(BUNDLE_VARIANT);
    }

    int resId;
    switch (buttonVariant) {
      case 2: {
        resId = R.drawable.btn_red;
        break;
      }

      default: {
        resId = R.drawable.btn_blue;
        break;
      }
    }
    button.setBackgroundDrawable(getResources().getDrawable(resId));
  }

  @Override
  protected void onSaveInstanceState(Bundle outState) {
    outState.putInt(BUNDLE_VARIANT, buttonVariant);
    super.onSaveInstanceState(outState);
  }


  private class Adapter extends BaseAdapter {
    private String[][] products = new String[][] {
      { "sticker Vol. 1", "2.00" },
      { "figure", "8.00" },
      { "android", "12.50" },
      { "sticker Vol. 2", "3.00" }
    };

    @Override
    public int getCount() {
      return products.length;
    }

    @Override
    public String[] getItem(int position) {
      return products[position];
    }

    @Override
    public long getItemId(int position) {
      return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
      if (convertView == null) {
        convertView = LayoutInflater.from(ShoppingCardActivity.this).inflate(android.R.layout.simple_list_item_checked,
          parent, false);
      }

      final String[] item = getItem(position);
      final CheckedTextView checked = (CheckedTextView) convertView;
      checked.setText(item[0] + " - " + item[1]);
      checked.setOnClickListener(new OnClickListener() {
          @Override
          public void onClick(View v) {
            checked.toggle();
            if (checked.isChecked()) {
              amount += Double.valueOf(item[1]);
            } else {
              amount -= Double.valueOf(item[1]);
            }
          }
        });
      return convertView;
    }
  }
}
