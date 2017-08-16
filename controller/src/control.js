module.exports = class Control {

  constructor($interval) {
    this.$interval = $interval
  }

  $onInit() {
    this.$interval(_ => {
      // 何もしないが、$intervalのコールバックが実行されることでプログレスバーが更新される
    }, 1000)
  }

  /**
   * プログレスバーのアニメーション時間を返す。
   * 動画を切り替えた時にすぐ反映するように、再生開始直後は0秒、そうでなければおよそコントロールメッセージの送信間隔。
   */
  get transitionTime() {
    return this.position < 100 ? 0 : 1
  }

  /**
   * プログレスバーの要素に適用するtransitionスタイルの値。
   */
  get transitionValue() {
    return `width ${this.transitionTime}s linear`
  }

  /**
   * プログレスバーの要素に適用するwidthスタイルの値。
   */
  get widthValue() {
    return `${Math.floor(this.position / this.duration * 100)}%`
  }
}