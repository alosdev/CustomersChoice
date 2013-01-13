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
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;
import de.alosdev.android.customerschoice.CustomersChoice;
import de.alosdev.customerschoice.demo.R;


public class CustomersChoiceDemo extends Activity {
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);
    setVariant(R.id.button1, "Variant1");
    setVariant(R.id.button2, "Variant2");
    setVariant(R.id.button3, "Variant3");
  }

  private void setVariant(int resId, final String variantName) {
    final Button button = (Button) findViewById(resId);
    button.setText(variantName + ": " + CustomersChoice.getVariant(variantName));
    button.setOnClickListener(new OnClickListener() {
        @Override
        public void onClick(View v) {
          Toast.makeText(CustomersChoiceDemo.this,
            "reached goal for \"" + variantName + "\" with variant: " + CustomersChoice.getVariant(variantName),
            Toast.LENGTH_SHORT).show();
          CustomersChoice.reachesGoal(variantName);
        }
      });
  }

}
