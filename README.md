# Multi-Link

Simultaneous video playback system for Gear VR and Cardboard. It uses UDP broadcast packet to control playback state. Highly efficient to use with large local network.

**This project is still in development.**

## Required

Node.js (Confirmed with v8.2.1 or later)


## Setup

* Put osig file(s) into app/src/main/assets
* Copy 360 video file(s) into Galaxy device(s). Directory is arbitrary. If you would like to use multiple devices, video files must be in same paths in all devices.
* Prepare controller with these commands

```
cd controller
npm install
```

## Launch

* To launch viewer:
    * Build and run `app` module for Gear VR
    * Build and run `cardboard` module for Cardboard (If you want to use stereoscopic rendering, comment out `surfaceView.stereoModeEnabled = false` in `MainActivity`)
* To launch controller:
    * run `npm start` command in `controller` directory.
    * Build and run `controller-android` module
