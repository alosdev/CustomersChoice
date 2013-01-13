package de.alosdev.android.customerschoice.demo;

import android.app.Application;
import de.alosdev.android.customerschoice.CustomersChoice;
import de.alosdev.android.customerschoice.VariantBuilder;
import de.alosdev.android.customerschoice.logger.AndroidLogger;
import de.alosdev.customerschoice.demo.R;


public class CustomersChoiceApplication extends Application {
  @Override
  public void onCreate() {
    super.onCreate();
    CustomersChoice.addLoggers(new AndroidLogger(), new AndroidLogger());
    CustomersChoice.addVariant(new VariantBuilder("Variant1").setSpreading(new int[] { 50, 50 }).build());
    CustomersChoice.addVariant(new VariantBuilder("Variant2").setSpreading(new int[] { 5, 50 }).build());
    CustomersChoice.configureByResource(this, R.string.test_config);
    CustomersChoice.configureBySD(getPackageName() + ".conf");
    /*
    fileName: de.alosdev.customerschoice.demo.conf put on the SDcard root with conent:
    {"variants":
    [
    {"startTime": 51, "spreading": [ 1, 2], "name": "sd_conf_1" },
    {"endTime": 53, "spreading": [ 3, 3], "name": "sd_conf_2" }
    ]
    }
     */

    CustomersChoice.configureByNetwork(
      "https://raw.github.com/alosdev/CustomersChoice/master/sample/files/customerschoice.net.conf");
  }
}
