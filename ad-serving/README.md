# Tapad / Swappit Android Ad Serving API
This API allows for serving ads through the Swappit Ad service.


## Initialization of the API
Place the following line of code in your startup activity's onCreate():

```java
  com.tapad.adserving.AdServing.init(this, publisherId, propertyId);
```

The `publisherId` and `propertyId` are specified in your Swappit dashboard.

Once the API is initialized, there are two major ways to work with it:

1. Using an SDK managed WebView 
2. Using your own WebViews

Note that the SDK requires Internet access, so make sure you have the following permission in your `AndroidManifest.xml`:

```
    <uses-permission android:name="android.permission.INTERNET"/>
```


# Using an SDK managed WebView
The simplest integration approach is to add an SDK managed WebView to your layout. This class is called `AdView` and supports automatic ad refresh intervals etc.

### Add the AdView to your layout
```
    <com.tapad.adserving.ui.AdView
            android:id="@+id/ad_view"
            android:layout_height="50dip"
            android:layout_width="fill_parent"
            android:visibility="gone"
    />
```

### Add onResume and onPause event hooks to your Activity
To make sure that the view start refreshing ads when the activity becomes active and stops when the activity is passivated, add the following code to the activity containing the `AdView`:

```
    @Override
    protected void onResume() {
        super.onResume();
        AdView v = (AdView) findViewById(R.id.ad_view);
        // Display ads for the placement defined by you as PLACEMENT_ID,
        // of size 320x50 with a 20 second refresh interval.
        v.start(PLACEMENT_ID, AdSize.S320x50, 20);
    }
    
    @Override
    protected void onPause() {
		AdView v = (AdView) findViewById(R.id.ad_view);
        v.stopRefreshTimer();
    }
```

### Customizing the AdView

Note that we're hiding the `AdView` by default. The view will automatically show itself when an ad is available and hide itself if not (e.g, network connectivity issues). This behaviour can be customized by overriding the `onMarkupLoaded()` and `onNoMarkupAvailable()` methods. If you need more advanced customization, you can, of course, build your own based on it's source.


# Using your own WebViews
After the API has been initialized, you can get a reference to the `AdServiceService` by invoking:

```
   AdServingService service = com.tapad.adserving.AdServing.get();
```   

The `AdServingService` allows you to asynchronously request ads. When the ad markup is available (or an error occurred), the `onResponse` method of your request will be invoked. For example:

```
AdServing.get().requestAd(new AdRequest(placementId, size) {
	protected void onResponse(final AdResponse response) {
		Runnable action = null;

		switch (response.getResponseCode()) {
        	case AdResponse.OK:
            	String markup = response.getMarkup();
                // Do something with the markup
                break;
            case AdResponse.NO_AD_AVAILABLE:
            	// No match for ad request (will usually only occur if an exotic size is specified)
            case AdResponse.ERROR:
                // No network connectiviti etc.
		}
	}
}
```


## How to include the Tapad code in your project
The source code of our SDK is freely available here on Github, so you are free to include the code however you prefer: 

* Copy the source directly into your project
* Build a jar with Maven and reference it from your project directly
* Deploy the SDK artifacts to your own Maven repository and reference them from your `pom.xml`

For the latter:


```
<dependency>
  <groupId>com.tapad.android</groupId>
  <artifactId>ad-serving</artifactId>
  <version>1.0.0</version>
</dependency>
```        
