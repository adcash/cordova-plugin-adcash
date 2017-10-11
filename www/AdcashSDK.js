var argscheck = require('cordova/argscheck'),
    exec = require('cordova/exec');

var AdcashSDK = {};

AdcashSDK.AD_POSITION = {
    TOP: 0,
    BOTTOM: 1 //Default
};

/*
 * set options:
 *  {
 *    adSize: string, // banner type size
 *    position: integer, // default position
 *    autoShow: boolean,  // if set to true, no need call showBanner or showInterstitial
 *    adExtra: {
 *    }
 *   }
 */
AdcashSDK.setOptions = function (options, successCallback, failureCallback) {
    if (typeof options === 'object') {
        cordova.exec(successCallback, failureCallback, 'AdcashSDK', 'setOptions', [options]);
    } else {
        if (typeof failureCallback === 'function') {
            failureCallback('options should be specified.');
        }
    }
};

AdcashSDK.createBanner = function (args, successCallback, failureCallback) {
    var options = {};
    if (typeof args === 'object') {
        for (var k in args) {
            if (k === 'success') { if (args[k] === 'function') successCallback = args[k]; }
            else if (k === 'error') { if (args[k] === 'function') failureCallback = args[k]; }
            else {
                options[k] = args[k];
            }
        }
    } else if (typeof args === 'string') {
        options = { zoneId: args };
    }
    cordova.exec(successCallback, failureCallback, 'AdcashSDK', 'createBanner', [options]);
};

AdcashSDK.hideBanner = function (successCallback, failureCallback) {
    cordova.exec(successCallback, failureCallback, 'AdcashSDK', 'hideBanner', []);
};

AdcashSDK.showBanner = function (position, successCallback, failureCallback) {
    if (typeof position === 'undefined') position = 1;
    cordova.exec(successCallback, failureCallback, 'AdcashSDK', 'showBanner', [position]);
};

AdcashSDK.prepareInterstitial = function (args, successCallback, failureCallback) {
    var options = {};
    if (typeof args === 'object') {
        for (var k in args) {
            if (k === 'success') { if (args[k] === 'function') successCallback = args[k]; }
            else if (k === 'error') { if (args[k] === 'function') failureCallback = args[k]; }
            else {
                options[k] = args[k];
            }
        }
    } else if (typeof args === 'string') {
        options = { zoneId: args };
    }
    cordova.exec(successCallback, failureCallback, 'AdcashSDK', 'prepareInterstitial', [options]);
};

AdcashSDK.prepareRewardedVideo = function (args, successCallback, failureCallback) {
    var options = {};
    if (typeof args === 'object') {
        for (var k in args) {
            if (k === 'success') { if (args[k] === 'function') successCallback = args[k]; }
            else if (k === 'error') { if (args[k] === 'function') failureCallback = args[k]; }
            else {
                options[k] = args[k];
            }
        }
    } else if (typeof args === 'string') {
        options = { zoneId: args };
    }
    cordova.exec(successCallback, failureCallback, 'AdcashSDK', 'prepareRewardedVideo', [options]);
};

AdcashSDK.showInterstitial = function (successCallback, failureCallback) {
    cordova.exec(successCallback, failureCallback, 'AdcashSDK', 'showInterstitial', []);
};

AdcashSDK.showRewardedVideo = function (successCallback, failureCallback) {
    cordova.exec(successCallback, failureCallback, 'AdcashSDK', 'showRewardedVideo', []);
};

AdcashSDK.reportAppOpen = function (successCallback, failureCallback) {
    cordova.exec(successCallback, failureCallback, 'AdcashSDK', 'reportAppOpen', []);
}

module.exports = AdcashSDK;
