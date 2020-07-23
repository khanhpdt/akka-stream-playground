package vn.khanhpdt.akkastream.playground

import scala.concurrent.duration._

import akka.actor.ActorSystem
import akka.stream._
import akka.stream.scaladsl._
import com.typesafe.scalalogging.StrictLogging

object FastProducerSlowConsumerWithThrottle1 extends App with StrictLogging {

  implicit val system: ActorSystem = ActorSystem("QuickStart")

  val fastProducerSlowConsumerGraph =
    Source.tick(10.millis, 500.millis, "tick")
      // though the throttling rate (1 second) is shorter than the processing time (5 second),
      // akka-stream still makes sure that only when the downstream finishes its processing, it will then emit
      // another message to downstream
      .throttle(1, 1.second, 1, ThrottleMode.Shaping)
      .via {
        Flow.fromFunction(s => {
          logger.info(s"Flow received $s. Sleep for 5 seconds.")
          Thread.sleep(5.seconds.toMillis)
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
