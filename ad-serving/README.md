# Tapad / Swappit Android Ad Serving API
This API allows for serving ads through the Swappit Ad service.


## Basic setup

Add the following `uses-permission` and `meta-data` tags to your `AndroidManifest.xml`:

```xml
    <uses-permission android:name="android.permission.INTERNET"/>
	<application ...>
        <meta-data android:name="swappit.PUBLISHER_ID" android:value="INSERT_PUBLISHER_ID_HERE"/>
        <meta-data android:name="swappit.PROPERTY_ID" android:value="INSERT_PROPERTY_ID_HERE"/>
        ...
    </application>
```

Please visit your Swappit dashboard to find the `publisher` and `property` ids as well as the code for each of your placements. This information is located in the Implement Code tab of the Edit Property view.

To navigate to the instructions:

1. Go to the [Swappit Earning Dashboard](https://partner.swappit.com/earn)
2. Select a property from the list
3. Click the Edit property button
4. Select the Implement Code tab
5. Click the Show me the code button
6. If not selected by default, please select the Android App tab to see the relevant instructions

To guarantee proper initialization regardless of which Activity is started, please place the following line of code in your Application's `onCreate()`:

```java
  com.tapad.adserving.AdServing.init(this);
```

Once the API is initialized, there are two major ways to work with it:

1. Using an SDK managed WebView 
2. Using your own WebViews

# Using an SDK managed WebView
The simplest integration approach is to add an SDK managed WebView to your layout. This class is called `AdView` and supports automatic ad refresh intervals etc.

### Add the AdView to your layout
```xml
<com.tapad.adserving.ui.AdView
	android:id="@+id/ad_view"
    android:layout_height="50dip"
    android:layout_width="fill_parent"
    android:visibility="gone"
/>
```

### Add onResume and onPause event hooks to your Activity
To make sure that the view starts refreshing ads when the activity becomes active and stops when the activity is paused, add the following code to the activity containing the `AdView`:

```java
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

Note that we're hiding the `AdView` by default. The view will automatically show itself when an ad is available and hide itself if not (e.g, network connectivity issues). This behaviour can be customized by overriding the `onMarkupLoaded()` and `onNoMarkupAvailable()` methods. If you need more advanced customization, you can, of course, build your own based on the source of those methods.


# Using your own WebViews
After the API has been initialized, you can get a reference to the `AdServiceService` by invoking:

```java
AdServingService service = com.tapad.adserving.AdServing.get();
```   

The `AdServingService` allows you to asynchronously request ads. When the ad markup is available (or an error occurred), the `onResponse` method of your request will be invoked. For example:

```java
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
                // No network connectivity etc.
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


```xml
<dependencies>
  ...
  <dependency>
    <groupId>com.tapad.android</groupId>
    <artifactId>ad-serving</artifactId>
    <version>1.0.1</version>
  </dependency>
  ...
</dependencies>
<repositories>
  ...
  <repository>
      <id>tapad-android</id>
      <releases/>
      <url>https://github.com/Tapad/tapad-android-sdk/tree/gh-pages/repository/releases</url>
  </repository>
  ...
</repositories>

```        
