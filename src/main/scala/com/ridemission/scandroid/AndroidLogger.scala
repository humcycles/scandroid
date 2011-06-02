package com.ridemission.scandroid

import scala.util.logging._

import android.util.Log

/// extend this trait to get all sorts of easy logging wrappers
/// FIXME - turn off logging when debugging is off
trait AndroidLogger extends Logged {
  /// The tag string used for all our messages
  private def tag = getClass.toString

  /// This is the standard scala log method
  override def log(msg: String) = info(msg)

  def info(msg: String) = Log.i(tag, msg)
  def debug(msg: String) = Log.d(tag, msg)
  def error(msg: String) = Log.e(tag, msg)
  def warn(msg: String) = Log.w(tag, msg)

  // Not available in android-2.1/7
  // def wtf(msg: String) = Log.wtf(tag, msg)
  // def wtf(msg: String, ex: Throwable) = Log.wtf(tag, msg, ex)
}
