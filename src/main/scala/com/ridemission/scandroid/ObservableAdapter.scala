package com.ridemission.scandroid

import android.widget._
import android.view._
import android.content._
import android.util._

import scala.collection.mutable._
import scala.collection.script.Message

/// Like array adapter, but watches Scala ObservableBuffers
class ObservableAdapter[T](context: Context, resource: Int, textViewResId: Int, val array: ObservableBuffer[T]) extends BaseAdapter {

  def this(context: Context, textViewResId: Int, array: ObservableBuffer[T]) =
    this(context, textViewResId, 0, array)

  private val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE).asInstanceOf[LayoutInflater]

  /// Resource to use when displaying as a drop down widget (defaults
  /// to resource)
  var dropDownResource = resource

  val sub = new array.Sub {
    override def notify(cont: array.Pub, msg: Message[T] with Undoable) {
      notifyDataSetChanged()
    }
  }

  array.subscribe(sub)

  override def getCount = array.length
  override def getItem(n: Int): Object = getItem(n).asInstanceOf[AnyRef]
  override def getItemId(n: Int) = n
  override def getView(position: Int, convertView: View, parent: ViewGroup) =
    createViewFromResource(position, convertView, parent, resource)
  override def getDropDownView(position: Int, convertView: View, parent: ViewGroup) =
    createViewFromResource(position, convertView, parent, dropDownResource)

  /// Modeled after the ArrayAdapter implementation
  private def createViewFromResource(position: Int, convertView: View,
    parent: ViewGroup, resource: Int) = {

    val view: View = if (convertView == null)
      inflater.inflate(resource, parent, false)
    else
      convertView

    val text: TextView = try {
      if (textViewResId == 0)
        //  If no custom field is assigned, assume the whole resource is a TextView
        view.asInstanceOf[TextView]
      else
        //  Otherwise, find the TextView field within the layout
        view.findViewById(textViewResId).asInstanceOf[TextView]
    } catch {
      case e: ClassCastException =>
        Log.e("ObservableAdapter", "You must supply a resource ID for a TextView")
        throw new IllegalStateException(
          "ObservableAdapter requires the resource ID to be a TextView", e)
    }

    val item = array(position)
    if (item.isInstanceOf[CharSequence]) {
      text.setText(item.asInstanceOf[CharSequence])
    } else {
      text.setText(item.toString)
    }

    view
  }
}
