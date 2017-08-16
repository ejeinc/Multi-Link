const TYPE_PING = 0
const TYPE_CONTROL = 1

class App {

  constructor($scope, $interval, udp) {
    this.$scope = $scope
    this.$interval = $interval
    this.udp = udp
    this.selectedVideo = null
    this.playing = false
    this.ended = false // 最後まで再生終了した時にtrueになる
    this.startTime = 0 // 再生開始した時間
    this.devices = []
    this.videos = []
  }

  $onInit() {

    // PINGの結果を受け取った時
    this.udp.on('ping', deviceInfo => {

      // 同じ端末の情報を二度受け取っても無視する
      if (!this.devices.find(device => device.imei === deviceInfo.imei)) {

        // 初回のみ動画を全登録
        if (this.devices.length === 0) {
          this.videos = deviceInfo.videos
        } else {
          // 2回目以降は、動画リストとdeviceInfo.videosを比較して、
          // 動画リストに含まれているがdeviceInfo.videosに含まれていない動画を動画リストから削除する
          this.videos = this.videos.filter(video => {
            return deviceInfo.videos.find(deviceVideo => {
              return deviceVideo.path === video.path
            })
          })
        }

        // 端末リストに登録
        this.devices.push({
          imei: deviceInfo.imei,
          name: deviceInfo.name,
          registeredAt: Date.now()
        })

        this.$scope.$apply()
      }
    })

    // ネットワーク内のデバイスを問い合わせる
    this.pingToDevices()

    // 一定期間でコントロールメッセージを送り続ける
    this.$interval(_ => {

      // 選択中の動画がある場合のみ
      if (this.selectedVideo) {

        // 動画が終わったはずの時間になったら再生を止める
        if (this.selectedVideo.length <= this.estimatedCurrentPosition) {
          this.playing = false
          this.ended = true
        }

        // ローカルネットワークにブロードキャスト
        this.syncDevices()
      }
    }, 2000)
  }

  /**
   * 予想される動画の現在位置を返す。
   */
  get estimatedCurrentPosition() {
    return this.playing
      ? Date.now() - this.startTime
      : this.ended && this.selectedVideo
        ? this.selectedVideo.length
        : 0
  }

  /**
   * ネットワークにPINGメッセージを送信する。
   */
  pingToDevices() {
    this.udp.sendObject({
      type: TYPE_PING
    })
  }

  /**
   * 再生コントロールUIが無効かどうかを返す。
   * @return 動画未選択状態ならtrue、選択済みならfalse
   */
  controlDisabled() {
    return !this.selectedVideo
  }

  togglePlaying() {
    this.playing = !this.playing

    // 再生開始時間を記憶する
    if (this.playing) {
      this.startTime = Date.now()
      this.ended = false
    }

    this.syncDevices()
  }

  /**
   * 一つ前の動画を選択する。
   */
  skipPrevious() {
    if (this.selectedVideo) {
      let currentVideoIndex = this.videos.findIndex(i => i === this.selectedVideo)

      // 選択中の動画が動画リストの中に存在し、一番最初でない場合は前の動画へ
      if (currentVideoIndex > 0 && currentVideoIndex < this.videos.length) {
        this.selectVideo(this.videos[currentVideoIndex - 1])
      }
    }
  }

  /**
   * 次の動画を選択する。
   */
  skipNext() {
    if (this.selectedVideo) {
      let currentVideoIndex = this.videos.findIndex(i => i === this.selectedVideo)

      // 選択中の動画が動画リストの中に存在し、一番最後でない場合は次の動画へ
      if (currentVideoIndex >= 0 && currentVideoIndex < this.videos.length - 1) {
        this.selectVideo(this.videos[currentVideoIndex + 1])
      }
    }
  }

  /**
   * 動画を選択する。
   * @param {*} video 
   */
  selectVideo(video) {

    // 違う動画が選択されたときだけ処理する
    if (this.selectedVideo !== video) {
      this.selectedVideo = video

      if (this.playing) {
        this.startTime = Date.now() // 再生中なら、再生開始時間をリセットする
        this.ended = false
      }
      // this.playing = false
      this.syncDevices()
    }
  }

  /**
   * コントローラーの状態をネットワークにブロードキャストする。
   */
  syncDevices() {
    this.udp.sendObject({
      type: TYPE_CONTROL,
      data: {
        path: this.selectedVideo && this.selectedVideo.path,
        playing: this.playing,
        position: this.estimatedCurrentPosition
      }
    })
  }
}

angular.module('App', [])
  .factory('udp', () => {

    let UDPInterface = require('./UDPInterface')
    let udp = new UDPInterface(50201)
    udp.start()

    window.addEventListener('beforeunload', (e) => {
      udp.stop()
    })

    return udp
  })
  .component('app', {
    templateUrl: 'app.html',
    controller: App
  })
  .component('deviceList', {
    templateUrl: 'device-list.html',
    controller: require('./device-list'),
    bindings: {
      // inputs
      devices: '<',
      // outputs
      onClickReload: '&'
    }
  })
  .component('videoList', {
    templateUrl: 'video-list.html',
    controller: require('./video-list'),
    bindings: {
      // inputs
      videos: '<',
      selectedVideo: '<',
      // outputs
      onSelect: '&'
    }
  })
  .component('control', {
    templateUrl: 'control.html',
    controller: require('./control'),
    bindings: {
      // inputs
      playing: '<',
      disabled: '<',
      duration: '<', // 動画の長さ
      position: '<', // 動画の現在位置

      // outputs
      onSkipPrevious: '&',
      onPlay: '&',
      onPause: '&',
      onSkipNext: '&'
    }
  })