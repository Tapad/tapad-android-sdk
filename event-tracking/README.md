# Tapad Android Tracking API
This API allows for app installation tracking (and attribution) as well as tracking other, arbitrary
events which then can be analyzed and visualized in the Tapad reporting dashboards.

## Install tracking
Place the following line of code in you startup activity onCreate():

```java
  com.tapad.tracking.Tracking.init(this);
```

When the application is launched for the first time, a unique ID will be generated which will be used for all
future tracking calls, and an installation event will be sent to the Tapad server to allow for impression attribution.

## Custom event tracking
To track other events, such as a sign-up or an in-app purchase, place the following line of code after the event has happened:

```java
  com.tapad.tracking.Tracking.get().onEvent("some-event");
```

These events will then be available for bid optimization in the DSP self service UI allowing you to set a
target price for each event.