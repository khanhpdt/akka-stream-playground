package vn.khanhpdt.akkastream.playground

import scala.concurrent.duration._

import akka.actor.ActorSystem
import akka.stream._
import akka.stream.scaladsl._
import com.typesafe.scalalogging.StrictLogging

object FastProducerSlowConsumerWithThrottle4 extends App with StrictLogging {

  implicit val system: ActorSystem = ActorSystem("QuickStart")

  // Producing 5 per 5 seconds. But b/c the processor is slow (1 per 2 seconds),
  // the whole flow is slowing down to 1 per 2 seconds.
  val fastProducerSlowConsumerGraph =
    Source.repeat(true)
      .throttle(5, 5.second, 1, ThrottleMode.Shaping)
      .via {
        Flow.fromFunction(s => {
          logger.info(s"Flow received $s. Sleep for 2 seconds.")
          Thread.sleep(2.seconds.toMillis)
          s"From Flow: $s"
        })
      }
      .to {
        Sink.foreach(s => {
          logger.info(s"Sink received $s")
        })
      }

  fastProducerSlowConsumerGraph.run()

}
