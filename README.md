#CustomersChoice

This library can be used to make simple usability tests on live Android applications, so you can find the best choice, if you have two or more different solutions. The Customer choose the best solution. For Example, if it is better to have a red or blue purchase button.

##USAGE
The library is really esy to use, if it is configured.

    CustomersChoice.getVariant("Variant name");
###adding a Variant by code with a spreading of 50:50 with CustomersChoice.addVariant(Variant)

    CustomersChoice.addVariant(new VariantBuilder("Variant name").setSpreading(new int[] { 50, 50 }).build());

###JSON structure for configurations

```
 { 
   "variants": [
     {
       "startTime": 51,
       "endTime": 53,
       "spreading": [ 1, 2 ],
       "name": "Variant1"
     },
     {
       "endTime": 53,
       "spreading": [ 3, 3 ],
       "name": "Variant2"
     }
   ]
 }
```

****attributes****

- name - reqiured name as String
- startTime - in Milliseconds from midnight, January 1, 1970 UTC
- endTime - in Milliseconds from midnight, January 1, 1970 UTC
- spreading - integer definition of ratio between the possible variants. The amount of definition defines the amount of possible variants.
- 

###adding several Variants by a String resource with CustomersChoice.configureByResource(Context, int).

    CustomersChoice.configureByResource(this, R.string.resource);

**With this you can add different configurations by locale, density and/or size.
**

###adding several Variants by a file one the SD Card CustomersChoice.configureBySD(String)
    
    CustomersChoice.configureBySD("FilepathWithFileName");

###adding several Variants by a network CustomersChoice.configureByNetwork(String)
    CustomersChoice.configureByNetwork("configurationURL");

###adding a loggers
    
    CustomersChoice.addLoggers(new AndroidLogger(), new CustomLogger());



### License

* [Apache Version 2.0](http://www.apache.org/licenses/LICENSE-2.0.html)
