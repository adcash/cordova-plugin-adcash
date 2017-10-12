# AdCash Plugin

### Platforms supported:

- Android
- iOS

### Prerequisite

* [Android](https://cordova.apache.org/docs/en/latest/guide/platforms/android/index.html) OR [iOS](https://cordova.apache.org/docs/en/latest/guide/platforms/ios/index.html) development enviroment set up
* [Cordova project created and Cordova CLI installed](https://cordova.apache.org/docs/en/latest/guide/cli/index.html)

### Adcash SDK Integration

1. Create new zone(s) for banner and/or interstitial and/or rewarded video ads in [AdCash portal](https://www.adcash.com/console/scripts.php) for your registered app then use those zone IDs in next steps.
2. Import Adcash SDK in your Cordova project using single line (inside your project and not from outside)
```bash
cordova plugins add https://github.com/adcash/cordova-plugin-adcash.git
```
3. Your Adcash SDK is ready to use just load and display ads of your choice  

### Banner
```javascript
// This line of code will create and display banner on device screen
// 'YOUR_ZONE_ID' should be replaced with Adcash banner zone ID
// AD_POSITION should be replaced with either AdcashSDK.AD_POSITION.TOP or AdcashSDK.AD_POSITION.BOTTOM
AdcashSDK.createBanner({ zoneId: 'YOUR_ZONE_ID', position: AD_POSITION });

// Hide banner if you need to remove it from screen
AdcashSDK.hideBanner()  // AdcashSDK.showBanner() to display it back

// Read about banner event and callbacks below
```

### Interstitial
```javascript
// prepare and load ad resource in background(e.g. at begining of game level) since interstitial might take sometime to load in rare cases due to bad internet connection 
// 'YOUR_ZONE_ID' should be replaced with Adcash banner zone ID
AdcashSDK.prepareInterstitial({ zoneId: 'YOUR_ZONE_ID' });

// Later in the code you can use interstitial (when it is ready) to show fullscreen ads
AdcashSDK.showInterstitial();

// To know when interstitial ad is ready, read more about interstitial event and callbacks below
```

### Rewarded Video
```javascript
// prepare and load ad resource in background(e.g. at begining of game level) since video might take sometime to load in rare cases due to bad internet connection 
// 'YOUR_ZONE_ID' should be replaced with Adcash banner zone ID
AdcashSDK.prepareRewardedVideo({ zoneId: 'YOUR_ZONE_ID' });

// Later in the code call rewarded video (when it is ready) to show fullscreen video ad
AdcashSDK.showRewardedVideo();

// To know when rewarded video ad is ready read more about rewarded video event and callbacks below
```

## Callbacks
You can optionally specify callbacks for banner, interstitial and rewarded video
```javascript
// Banner
createBanner({ zoneId: 'YOUR_ZONE_ID', position: AD_POSITION }, successCallback, failureCallback);
// Interstitial
prepareInterstitial({ zoneId: 'YOUR_ZONE_ID' }, successCallback, failureCallback);
// Rewarded Video
prepareRewardedVideo({ zoneId: 'YOUR_ZONE_ID' }, successCallback, failureCallback);
```

Callback example
```javascript
// Rewarded Video
prepareRewardedVideo({ zoneId: 'YOUR_ZONE_ID' },
                               function() {
                                    // Succeeded in preloading video now show rewarded video ad
                                    AdcashSDK.showRewardedVideo();
                                },
                                function(error){
                                    // For video error handling
                                });
```

### Adcash events
```javascript
// Banner
document.addEventListener("onLoadedBanner", yourMethod);
document.addEventListener("onBannerFailedToLoad", yourMethod(error));
document.addEventListener("onOpenedBanner", yourMethod);
document.addEventListener("onClosedBanner", yourMethod);
document.addEventListener("onBannerLeftApplication", yourMethod);

// Interstitial
document.addEventListener("onLoadedInterstitial", yourMethod);
document.addEventListener("onInterstitialFailedToLoad", yourMethod(error));
document.addEventListener("onOpenedInterstitial", yourMethod);
document.addEventListener("onClosedInterstitial", yourMethod);
document.addEventListener("onInterstitialLeftApplication", yourMethod);

// RewardedVideo
document.addEventListener("onLoadedRewardedVideo", yourMethod);
document.addEventListener("onRewardedVideoFailedToLoad", yourMethod(error));
document.addEventListener("onOpenedRewardedVideo", yourMethod);
document.addEventListener("onClosedRewardedVideo", yourMethod);
document.addEventListener("onRewardedVideoLeftApplication", yourMethod);
document.addEventListener("onVideoReward", yourMethod);
```