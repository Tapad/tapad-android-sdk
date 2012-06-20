# Tapad Android Tracking API
This API allows for app installation tracking (and attribution) as well as tracking other, arbitrary
events which then can be analyzed and visualized in the Tapad reporting dashboards.

## Basic setup

Add the following `uses-permission` and `meta-data` tags to your `AndroidManifest.xml`:

```xml
    <uses-permission android:name="android.permission.INTERNET"/>
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
    <artifactId>event-tracking</artifactId>
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

## Changelog

### 1.0.1
* Change device identification to harmonize with ad networks
* Allow for configuration via meta-tags in `AndroidManifest.xml`
* Google Play referral tracking

### 1.0.0
* Initial release