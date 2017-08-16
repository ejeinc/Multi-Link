package com.eje_c.multilink.data

/**
 * 動画再生状態を制御するメッセージ。全てのフィールドをセットする必要がある。
 * 同じ値が複数回渡されても、最初の一回しか動作しない。
 * 例えば path に Movies/video.mp4 が設定されたメッセージを2回連続で受信しても、
 * 最初に受信した時に一度ロードするだけで、2回目は無視する。
 */
class ControlMessage(var path: String = "", var playing: Boolean = false, var position: Long = 0)