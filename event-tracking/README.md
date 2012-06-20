# Tapad Android Tracking API
This API allows for app installation tracking (and attribution) as well as tracking other, arbitrary
events which then can be analyzed and visualized in the Tapad reporting dashboards.

## Basic setup

Add the following meta attribute to your `AndroidManifest.xml` in the `<Application>` block:

```xml
	<application ...>
        <meta-data android:name="tapad.APP_ID" android:value="INSERT_APP_ID_HERE"/>
        ... 
    </application>
```

Place the following line of code in you startup activity `onCreate()`:

```java
  com.tapad.tracking.Tracking.init(this);
```

This will send the conversion events, `install` and `first-run`, to Tapad servers the first time a user runs the application.

## Google Play referral tracking (highly recommended)

In order to connect an install to an individual ad impression click-through, Google Play referral tracking must be enabled. This also means that the tracking API will be able to report installs even before the application is opened for the first time. To enable referrer tracking, simply add the following to your `AndroidManifest.xml`

```xml
<application ...>
	...		
	<receiver android:name="com.tapad.tracking.InstallReferrerReceiver" android:exported="true">
    	<intent-filter>
        	<action android:name="com.android.vending.INSTALL_REFERRER"/>
        </intent-filter>
     </receiver>
</application>
```

## Custom event tracking
To track other events, such as a sign-up or an in-app purchase, place the following line of code after the event has happened:

```java
  com.tapad.tracking.Tracking.get().onEvent("some-event");
```

These events will then be available for bid optimization in the DSP self service UI allowing you to set a
target price for each event.