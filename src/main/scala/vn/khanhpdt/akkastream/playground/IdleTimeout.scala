package vn.khanhpdt.akkastream.playground

import scala.concurrent.duration._
import scala.concurrent.TimeoutException

import akka.actor.{ActorSystem, Cancellable}
import akka.stream.scaladsl._
import akka.NotUsed
import com.typesafe.scalalogging.StrictLogging

object IdleTimeout extends App with StrictLogging {

  implicit val system: ActorSystem = ActorSystem("QuickStart")

  val items = 1 to 10
  var i = 0
  var cancel: Cancellable = _
  val fastProducerSlowConsumerGraph = {
    Source.tick(10.millis, 1.seconds, NotUsed)
      .map(_ => {
        val item = items(i)
        i += 1
        item
      })
      .filter(_ > 5)
      .idleTimeout(2.seconds)
      .recover {
        case e: TimeoutException =>
          logger.error("Error", e)
          cancel.cancel()
          "timeout"
      }
      .to {
        Sink.foreach(s => {
          logger.info(s"Sink received $s. Sleep for 3 seconds.")
          Thread.sleep(1.seconds.toMillis)
        })
      }
  }

  cancel = fastProducerSlowConsumerGraph.run()

}
