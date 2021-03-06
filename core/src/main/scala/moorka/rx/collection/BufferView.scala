package moorka.rx.collection

import moorka.rx._

/**
 * Reactive collection presentation. You can't modify
 * element list from this trait
 * @tparam A type of elements
 * @author Aleksey Fomkin <fomkin@tenderowls.com>
 */
trait BufferView[A] extends Mortal {


  //---------------------------------------------------------------------------
  //
  //  Events
  //
  //---------------------------------------------------------------------------

  /**
   * Element in `idx` updated. Note that this event doesn't
   * raise when reactive variable inside element change its
   * value
   */
  val updated: Rx[UpdatedIndexedElement[A]]

  /**
   * Element added into end of collection
   */
  val added: Rx[A]

  /**
   * Element removed
   */
  val removed: Rx[IndexedElement[A]]

  /**
   * Element inserted into `idx`. Collection shifted to right
   */
  val inserted: Rx[IndexedElement[A]]

  /**
   * Length of collection
   */
  val rxLength: Rx[Int]

  /**
   * Length of collection
   */
  @inline def length: Int

  //---------------------------------------------------------------------------
  //
  // Methods
  //
  //---------------------------------------------------------------------------

  /**
   * @param idx index of the element
   * @return Element in `idx`
   */
  def apply(idx: Int): A

  /**
   * Finds element in collection
   * @param e element
   * @return index of element `e`. -1 if not fount
   */
  def indexOf(e: A): Int

  /**
   * Removes all links to another collections or any reactive
   * value. Meaning if you have collection `a` and collection
   * `b` which were mapped to `a`, `b.kill()` would stop `a` -> `b`
   * event propagation.
   */
  def kill(): Unit = {
    added.kill()
    removed.kill()
    inserted.kill()
    updated.kill()
  }

  override def toString = {
    // Force elements (for non-strict collection)
    val values =
      for (x <- 0 until length)
      yield apply(x).toString
    val s = if (values.length > 0)
      values.reduce(_ + "," + _)
    else ""
    s"Collection($s)"
  }
}

case class IndexedElement[A](idx: Int, e: A)
case class UpdatedIndexedElement[A](idx: Int, e: A, prevE: A)
