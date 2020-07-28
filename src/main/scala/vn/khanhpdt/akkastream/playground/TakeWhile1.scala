package vn.khanhpdt.akkastream.playground

import scala.concurrent.duration._
import scala.util.{Failure, Success}

import akka.actor.ActorSystem
import akka.stream.scaladsl._
import akka.NotUsed
import akka.stream.ThrottleMode
import com.typesafe.scalalogging.StrictLogging

object TakeWhile1 extends App with StrictLogging {

  implicit val system: ActorSystem = ActorSystem("QuickStart")

  val items = 1 to 10
  var i = 0

  val graph = {
    Source.repeat(NotUsed)
      .initialDelay(100.millis)
      .throttle(1, 1.second, 1, ThrottleMode.Shaping)
      .map(_ => {
        val item = items(i)
        i += 1
        item
      })
      .takeWhile(_ < 7)
      .via(Flow.fromFunction(s => logger.info(s"Flow received: $s")))
      .to {
        Sink.onComplete {
          case Success(value) => logger.info(s"Completed: $value")
          case Failure(exception) => logger.info("Failed", exception)
        }
      }
  }

  graph.run()
}
