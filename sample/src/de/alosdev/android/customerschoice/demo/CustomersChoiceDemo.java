/*
 * Copyright 2012 Hasan Hosgel
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.alosdev.android.customerschoice.demo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import de.alosdev.android.customerschoice.broadcast.OverwriteVariantBroadCastReceiver;
import de.alosdev.customerschoice.demo.R;


public class CustomersChoiceDemo extends Activity implements OnItemSelectedListener {
  String[] items = { "buttonColor", "Variant2", "Variant3" };
  int variant = 0;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);
    findViewById(R.id.button).setOnClickListener(new OnClickListener() {
        @Override
        public void onClick(View v) {
          Intent shoppingCard = new Intent(CustomersChoiceDemo.this, ShoppingCardActivity.class);
          startActivity(shoppingCard);
        }
      });

    Spinner spinner = (Spinner) findViewById(R.id.spinner);
    spinner.setOnItemSelectedListener(this);

    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, items);
    arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    spinner.setAdapter(arrayAdapter);
    findViewById(R.id.sendBroatcast).setOnClickListener(new OnClickListener() {
        @Override
        public void onClick(View v) {
          Editable forceVariant = ((EditText) findViewById(R.id.forceVariant)).getText();
          String variantName = items[variant];
          Toast.makeText(CustomersChoiceDemo.this,
            "variant: " + variantName + " " + forceVariant, Toast.LENGTH_SHORT).show();

          Intent intent = new Intent("de.alosdev.android.customerschoice.demo.broadcast");
          intent.putExtra(OverwriteVariantBroadCastReceiver.KEY_OVERWRITE_VARIANT, variantName);
          intent.putExtra(OverwriteVariantBroadCastReceiver.KEY_FORCE_VARIANT,
            Integer.parseInt(forceVariant.toString()));
          sendBroadcast(intent);
        }
      });
  }

  @Override
  public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
    variant = arg2;
  }

  @Override
  public void onNothingSelected(AdapterView<?> arg0) {
  }

}
