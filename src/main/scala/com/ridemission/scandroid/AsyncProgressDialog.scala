package com.ridemission.scandroid

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

/**
 * An async task that takes care of showing a progress dialog and optionally put
 * up a message to show completion
 *
 * @author kevin
 *
 *         We assume you pass in all the state needed to doInBackground by
 *         bundling it up in a closure, so no execute args are supported.
 *
 * Usage: create an instance of this (possibly overriding background or postExecute) then call execute on the created object.
 */
abstract class AsyncProgressDialog(context: Context, dialogTitle: String, dialogMessage: String) extends AsyncVoidTask with AndroidLogger {

  private var completionMessage: String = null;
  private var completionDialogTitle: String = null;
  private val dialog = ProgressDialog.show(context, dialogTitle, dialogMessage, true, false);

  /// true if we've completed running our background handler without exception
  var completed = false

  /**
   *
   * @return true if we will show a dialog
   */
  def isShowingDialog = completionDialogTitle != null;

  /**
   * The message to show the user after an operation completes.
   *
   * @param message
   *
   *            Note: we show either the completion toast, or the completion
   *            dialog, not both
   */
  def showCompletionToast(message: String) {
    completionMessage = message;
  }

  /**
   * Show the user an alert informing them of the result for an operation
   *
   * @param dialogTitle
   * @param message
   *
   *            Note: we show either the completion toast, or the completion
   *            dialog, not both
   */
  def showCompletionDialog(dialogTitle: String, message: String) {
    completionMessage = message;
    completionDialogTitle = dialogTitle;
  }

  protected override def onPostExecute(unused: Void) {

    try {
      dialog.dismiss();
    } catch {
      case ex: IllegalArgumentException =>
        error("Caught mystery: " + ex.getMessage())
    }

    if (isShowingDialog) {
      val builder = new AlertDialog.Builder(context)
      builder.setTitle(completionDialogTitle)
      builder.setMessage(completionMessage)
      builder.setPositiveButton("Okay", null)

      val alert = builder.create();
      alert.show();
    } else if (completionMessage != null)
      Toast.makeText(context, completionMessage, Toast.LENGTH_LONG).show();

    postExecute()
  }

  override protected[scandroid] def inBackground() {
    try {
      background()
      completed = true
    } catch {
      case ex: Exception =>
        // Turn exception into a toast
        completionMessage = ex.getLocalizedMessage
    }
  }

  /// You must override this to provide the background behavior
  def background(): Unit

  /// Override this if you'd like to do some work in the GUI post completion
  def postExecute() {}
}
