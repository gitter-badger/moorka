package moorka.rx.base.bindings

import moorka.rx.base.{Dummy, StatefulSource, Rx, Source}
import moorka.rx.death.Reaper

/**
 * @author Aleksey Fomkin <aleksey.fomkin@gmail.com>
 */
private[rx] class StatefulBinding[From, To](initialValue: Option[From],
                                            parent: Source[From],
                                            lambda: From ⇒ Rx[To])
  extends Binding[From, To](parent, lambda) 
  with StatefulSource[To] {

  var state: Option[To] = None

  override private[rx] def update(v: To): Unit = {
    val optionV = Some(v)
    if (_alive && state != optionV) {
      state = optionV
      super.update(v)
    }
  }

  override def flatMap[B](f: (To) => Rx[B])(implicit reaper: Reaper = Reaper.nice): Rx[B] = {
    if (_alive) {
      reaper.mark(new StatefulBinding(state, this, f))
    }
    else {
      state match {
        case Some(x) ⇒ f(x)
        case None ⇒ Dummy
      }
    }
  }

  initialValue foreach run
}
