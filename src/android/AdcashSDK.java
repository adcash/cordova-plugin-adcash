package com.adcash.cordova.plugin;

import android.app.Activity;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;

import com.adcash.mobileads.Adcash;
import com.adcash.mobileads.ads.AdcashBannerView;
import com.adcash.mobileads.ads.AdcashInterstitial;
import com.adcash.mobileads.ads.AdcashRewardedVideo;
import com.adcash.mobileads.listeners.AdcashListener;
import com.adcash.mobileads.listeners.AdcashRewardedListener;
import com.adcash.mobileads.models.AdcashError;
import com.adcash.mobileads.models.AdcashReward;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * This class echoes a string called from JavaScript.
 */
public class AdcashSDK extends CordovaPlugin {

    private Activity activity;
    private AdcashRewardedVideo rewardedVideo;
    private AdcashInterstitial interstitial;
    private AdcashBannerView bannerView;

    private static final String OPT_AD_POSITION = "position";
    private static final String OPT_ZONE_ID = "zoneId";

    /**
     * Banner position constant for the top of the screen.
     */
    private static final int POSITION_TOP = 0;

    /**
     * Banner position constant for the bottom of the screen.
     */
    private static final int POSITION_BOTTOM = 1;

    /**
     * Cordova Actions.
     */
    private static final String ACTION_SET_OPTIONS = "setOptions";

    private static final String ACTION_CREATE_BANNER = "createBanner";
    private static final String ACTION_LOAD_BANNER = "loadBanner";
    private static final String ACTION_SHOW_BANNER = "showBanner";
    private static final String ACTION_HIDE_BANNER = "hideBanner";

    private static final String ACTION_PREPARE_INTERSTITIAL = "prepareInterstitial";
    private static final String ACTION_PREPARE_REWARDED_VIDEO = "prepareRewardedVideo";
    private static final String ACTION_SHOW_INTERSTITIAL = "showInterstitial";
    private static final String ACTION_SHOW_REWARDED_VIDEO = "showRewardedVideo";


    boolean autoShow = true;
    int positionCode = POSITION_BOTTOM;
    JSONObject adExtras;

    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
        this.activity = cordova.getActivity();
        Adcash.initialize(activity);
    }

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (ACTION_PREPARE_REWARDED_VIDEO.equals(action)) {
            JSONObject options = args.getJSONObject(0);
            String zoneId;
            try {
                zoneId = extractZoneId(options);
            } catch (Exception e) {
                callbackContext.error("Error extracting zone ID: " + e.getMessage());
                return true;
            }
            this.prepareRewardedVideo(zoneId, callbackContext);
            return true;
        }
        if (ACTION_PREPARE_INTERSTITIAL.equals(action)) {
            JSONObject options = args.getJSONObject(0);
            String zoneId;
            try {
                zoneId = extractZoneId(options);
            } catch (Exception e) {
                callbackContext.error("Error extracting zone ID: " + e.getMessage());
                return true;
            }
            this.prepareInterstitial(zoneId, callbackContext);
            return true;
        }
        if (ACTION_CREATE_BANNER.equals(action)) {
            JSONObject options = args.getJSONObject(0);
            String zoneId;
            try {
                zoneId = extractZoneId(options);
            } catch (Exception e) {
                callbackContext.error("Error extracting zone ID: " + e.getMessage());
                return true;
            }
            this.createBanner(zoneId, callbackContext);
            return true;
        }
        if (ACTION_SHOW_REWARDED_VIDEO.equals(action)) {
            this.showRewardedVideo(callbackContext);
            return true;
        }
        if (ACTION_SHOW_INTERSTITIAL.equals(action)) {
            this.showInterstitial(callbackContext);
            return true;
        }
        if (ACTION_LOAD_BANNER.equals(action)) {
            this.loadBanner(callbackContext);
            return true;
        }
        if (ACTION_SHOW_BANNER.equals(action)) {
            this.showBanner(callbackContext);
            return true;
        }
        if (ACTION_HIDE_BANNER.equals(action)) {
            this.hideBanner(callbackContext);
            return true;
        }
        if (ACTION_SET_OPTIONS.equals(action)) {
            JSONObject ops = args.getJSONObject(0);
            this.executeSetOptions(ops, callbackContext);
            return true;
        }
        return false;
    }

    private String extractZoneId(JSONObject json) throws JSONException {
        if (!json.has(OPT_ZONE_ID) || json.isNull(OPT_ZONE_ID)) {
            throw new NullPointerException("No zoneId or wrong key");
        }
        if (!(json.get(OPT_ZONE_ID) instanceof String)) {
            throw new ClassCastException("ZoneId type is not String");
        }
        String zoneId = json.getString(OPT_ZONE_ID);
        if (zoneId.trim().length() == 0) {
            throw new IllegalStateException("No zoneId value is empty");
        }
        return zoneId;
    }

    private void executeSetOptions(JSONObject options, CallbackContext callbackContext) {

        String msg = this.setOptions(options);
        if (!msg.equalsIgnoreCase("OK")) {
            callbackContext.error(msg);
        } else {
            callbackContext.success();
        }
    }

    /**
     * apply additional settings to banner or interstitial ads
     *
     * @param options json object containing keys and values for settings
     * @return text containing text "OK" without quotes if everything is OK or message that describes first found error
     */
    private String setOptions(JSONObject options) {
        if (options == null) return "Options are null";

        if (options.has(OPT_AD_POSITION) && !options.isNull(OPT_AD_POSITION)) {
            try {
                positionCode = options.getInt(OPT_AD_POSITION);
            } catch (JSONException exc) {
                return "position has to be from Integer type";
            }
        }

        return "OK";
    }

    private void setDefaultOptions() {
        this.adExtras = null;
        this.autoShow = true;
        this.positionCode = POSITION_BOTTOM;
    }

    private void prepareInterstitial(final String zoneId, final CallbackContext callbackContext) {
        if (zoneId == null || zoneId.length() <= 0) {
            callbackContext.error("Invalid zone id");
        }

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (interstitial == null) {
                    interstitial = new AdcashInterstitial(zoneId);
                    interstitial.setAdListener(createAdListener("Interstitial", callbackContext));
                }


                interstitial.loadAd();

            }
        });
    }

    private void prepareRewardedVideo(final String zoneId, final CallbackContext callbackContext) {
        if (zoneId == null || zoneId.length() <= 0) {
            callbackContext.error("Invalid zone id");
        }

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (rewardedVideo == null) {
                    rewardedVideo = new AdcashRewardedVideo(zoneId);
                    rewardedVideo.setAdListener(createRewardedListener(callbackContext));
                }


                rewardedVideo.loadAd();

            }
        });
    }

    private AdcashRewardedListener createRewardedListener(final CallbackContext callbackContext) {
        return new AdcashRewardedListener() {
            @Override
            public void onAdReward(AdcashReward adcashReward) {
                webView.loadUrl(String.format(
                        "javascript:cordova.fireDocumentEvent('onVideoReward', { 'reward': {currency: '%s', amount: '%s'} });",
                        adcashReward.name, adcashReward.amount));
            }

            @Override
            public void onAdLoaded(AdcashReward adcashReward) {
                webView.loadUrl("javascript:cordova.fireDocumentEvent('onLoadedRewardedVideo');");
                callbackContext.success();
            }

            @Override
            public void onAdFailedToLoad(AdcashError error) {
                webView.loadUrl(String.format(
                        "javascript:cordova.fireDocumentEvent('onRewardedVideoFailedToLoad', { 'error': %d });",
                        error.getErrorCode()));
                callbackContext.error(error.getErrorMessage());
            }

            @Override
            public void onAdOpened() {
                webView.loadUrl("javascript:cordova.fireDocumentEvent('onOpenedRewardedVideo');");
            }

            @Override
            public void onAdClosed() {
                webView.loadUrl("javascript:cordova.fireDocumentEvent('onClosedRewardedVideo');");
            }

            @Override
            public void onAdLeftApplication() {
                webView.loadUrl("javascript:cordova.fireDocumentEvent('onRewardedVideoLeftApplication');");
            }
        };
    }

    private AdcashListener createAdListener(final String tag, final CallbackContext callbackContext) {
        return new AdcashListener() {
            @Override
            public void onAdLoaded() {
                webView.loadUrl("javascript:cordova.fireDocumentEvent('onLoaded"+tag+"');");
                callbackContext.success();
            }

            @Override
            public void onAdFailedToLoad(AdcashError error) {
                webView.loadUrl(String.format(
                        "javascript:cordova.fireDocumentEvent('on"+tag+"FailedToLoad', { 'error': %d });",
                        error.getErrorCode()));
                callbackContext.error(error.getErrorMessage());
            }

            @Override
            public void onAdOpened() {
                webView.loadUrl("javascript:cordova.fireDocumentEvent('onOpened"+tag+"');");
            }

            @Override
            public void onAdClosed() {
                webView.loadUrl("javascript:cordova.fireDocumentEvent('onClosed"+tag+"');");
            }

            @Override
            public void onAdLeftApplication() {
                webView.loadUrl("javascript:cordova.fireDocumentEvent('on"+tag+"LeftApplication');");
            }
        };
    }

    /**
     * Show the ad if it is loaded.
     */
    private void showInterstitial(final CallbackContext callbackContext) {
        if (interstitial == null) {
            callbackContext.error("Interstitial is null.");
            return;
        }

        if (!interstitial.isAdReady()) {
            callbackContext.error("Interstitial has not finished loading yet");
            return;
        }

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                interstitial.show(activity);
                callbackContext.success();
            }
        });
    }

    private void showRewardedVideo(final CallbackContext callbackContext) {
        if (rewardedVideo == null) {
            callbackContext.error("Rewarded video is null.");
            return;
        }

        if (!rewardedVideo.isAdReady()) {
            callbackContext.error("Rewarded video has not finished loading yet");
            return;
        }

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                rewardedVideo.show(activity);
                callbackContext.success();
            }
        });
    }

    private void createBanner(final String zoneId, final CallbackContext callbackContext) {
        if (zoneId == null || zoneId.length() <= 0) {
            callbackContext.error("Invalid zone id");
        }

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                bannerView = new AdcashBannerView(activity);// AttributeSet
                bannerView.setAdZoneId(zoneId);
                bannerView.setAdListener(createAdListener("Banner", callbackContext));

                if (autoShow) {
                    addBannerToTheScreen(bannerView);
                }

                bannerView.loadAd();
                callbackContext.success();
            }
        });
    }

    private void addBannerToTheScreen(AdcashBannerView adView) {
        if (adView == null || adView.getParent() != null) {
            return;
        }

        FrameLayout.LayoutParams adParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT);

        switch (positionCode) {
            case POSITION_TOP:
                adParams.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
                break;
            case POSITION_BOTTOM:
                adParams.gravity = Gravity.BOTTOM
                        | Gravity.CENTER_HORIZONTAL;
                break;
        }

        activity.addContentView(adView, adParams);
    }

    private void removeBannerFromTheScreen(AdcashBannerView adView) {
        if (adView == null || adView.getParent() == null) {
            return;
        }

        ViewParent parentView = adView.getParent();
        if (parentView != null && parentView instanceof ViewGroup) {
            ((ViewGroup) parentView).removeView(adView);
        }
    }

    private void loadBanner(final CallbackContext callbackContext) {
        if (bannerView == null) {
            callbackContext.error("Ad view is null, call 'createBanner' to create banner view");
            return;
        }

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                bannerView.loadAd();
            }
        });
    }

    /**
     * Show the ad if it is loaded.
     */
    private void showBanner(final CallbackContext callbackContext) {
        if (bannerView == null) {
            callbackContext.error("banner is null, call createBanner first.");
            return;
        }

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                addBannerToTheScreen(bannerView);
                callbackContext.success();
            }
        });
    }

    /**
     * Show the ad if it is loaded.
     */
    private void hideBanner(final CallbackContext callbackContext) {
        if (bannerView == null) {
            callbackContext.error("banner is null, call createInterstitial first.");
            return;
        }

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                removeBannerFromTheScreen(bannerView);
                callbackContext.success();
            }
        });
    }
}

