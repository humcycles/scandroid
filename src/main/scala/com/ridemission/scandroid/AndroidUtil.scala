package com.ridemission.scandroid

import android.app._
import android.content._
import android.widget._
import android.view._
import android.widget._
import android.location._
import android.os.Build

class DialogExtensions(dialog: Dialog) {
  // easy android callback constructors

  // type safe access to views (FIXME - not accessible current,
  // because TypedResource is generated in the namespace of the
  // calling library currently)
  // import com.ridemission.someapp.TypedResource
  // def findView[T](tr: TypedResource[T]) = dialog.findViewById(tr.id).asInstanceOf[T]

  implicit def toOnCancelListener(handler: DialogInterface => Unit) = new DialogInterface.OnCancelListener {
    override def onCancel(arg: DialogInterface) = handler(arg)
  }

  implicit def toOnDismissListener(handler: DialogInterface => Unit) = new DialogInterface.OnDismissListener {
    override def onDismiss(arg: DialogInterface) = handler(arg)
  }

  def onCancel(listener: DialogInterface => Unit) = dialog.setOnCancelListener(listener)
  def onDismiss(listener: DialogInterface => Unit) = dialog.setOnDismissListener(listener)
}

class ViewExtensions[ViewType <: View](view: ViewType) {
  // easy android callback constructors

  implicit def toOnClickListener(handler: ViewType => Unit) = new View.OnClickListener {
    override def onClick(arg: View) = handler(arg.asInstanceOf[ViewType])
  }

  def onClick(listener: ViewType => Unit) = view.setOnClickListener(listener)
}

class AdapterViewExtensions[ViewType <: AdapterView[_]](view: ViewType) extends ViewExtensions(view) {
  private type Callback = (ViewType, View, Int, Long) => Unit

  implicit def toOnSelectedListener(handler: Callback) = new AdapterView.OnItemSelectedListener {

    override def onItemSelected(parent: AdapterView[_], view: View, pos: Int, id: Long) {
      handler(parent.asInstanceOf[ViewType], view, pos, id)
    }

    override def onNothingSelected(parent: AdapterView[_]) {
      // Do nothing.
    }
  }

  def onItemSelected(callback: Callback) = view.setOnItemSelectedListener(callback)
}

trait AndroidLocationUtil {

  implicit def toGpsStatusListener(handler: Int => Unit) = new GpsStatus.Listener {
    override def onGpsStatusChanged(status: Int) = handler(status)
  }

}

object AndroidUtil {

  // extensions for particular class types

  implicit def DialogExtensions(view: Dialog) = new DialogExtensions(view)
  implicit def CheckBoxExtensions(view: CheckBox) = new ViewExtensions(view)
  implicit def ButtonExtensions(view: Button) = new ViewExtensions(view)
  implicit def AdapterViewExtensions[ViewType <: AdapterView[_]](view: ViewType) = new AdapterViewExtensions(view)

  /// Are we running on emulated hardware?
  def isEmulator = Build.MODEL == "google_sdk"
}
