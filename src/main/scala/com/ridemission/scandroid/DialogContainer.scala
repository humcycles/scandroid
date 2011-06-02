package com.ridemission.scandroid

import android.app._
import android.os._

import scala.collection.mutable.ListMap

/// A scala friendly API for dialog operations
trait DialogContainer extends Activity {

  type DialogCreateFunct = Bundle => Dialog
  type DialogPrepareFunct = (Dialog, Bundle) => Unit

  private var nextDialogId = 1
  private var ids = new ListMap[Int, DialogId]

  case class DialogId(num: Int, create: DialogCreateFunct, prepare: DialogPrepareFunct) {
    // This variant of show is only supported on android-8 or later
    // def show(bundle: Bundle) {
    //  assert(showDialog(num, bundle))
    //}

    def show() {
      showDialog(num)
    }

    def dismiss() { dismissDialog(num) }
  }

  /// Only used on android-8
  //override protected def onCreateDialog(id: Int, bundle: Bundle) = {
  //  val d = ids(id).create(bundle)
  //  assert(d != null)
  //  d
  // }

  // Only used on android-8
  //override protected def onPrepareDialog(id: Int, dialog: Dialog, bundle: Bundle) {
  //  ids(id).prepare(dialog, bundle)
  //}

  override protected def onCreateDialog(id: Int) = {
    val d = ids(id).create(null)
    assert(d != null)
    d
  }

  override protected def onPrepareDialog(id: Int, dialog: Dialog) {
    ids(id).prepare(dialog, null)
  }

  /// Create a DialogId - you can call id.show to show the dialog
  /// @create a function that returns the newly created dialog
  /// @prepare populates the dialog with appropriate field contents
  def addCreateDialog(create: DialogCreateFunct)(prepare: DialogPrepareFunct): DialogId = {
    nextDialogId += 1
    val d = new DialogId(nextDialogId, create, prepare)
    ids += nextDialogId -> d
    d
  }

  /// Create a DialogId - you can call id.show to show the dialog
  /// @layoutid the resource id of the layout to load
  /// @prepare populates the dialog with appropriate field contents
  def addDialog(layoutId: Int)(prepare: DialogPrepareFunct): DialogId = {
    def create(bundle: Bundle): Dialog = {
      val dialog = new Dialog(this)

      dialog.setContentView(layoutId)
      dialog.setOwnerActivity(this)

      dialog
    }

    addCreateDialog(create)(prepare)
  }
}
