package vn.khanhpdt.akkastream.playground

import scala.concurrent.duration._

import akka.actor.ActorSystem
import akka.stream._
import akka.stream.scaladsl._
import com.typesafe.scalalogging.StrictLogging

object FastProducerSlowConsumerWithThrottle2 extends App with StrictLogging {

  implicit val system: ActorSystem = ActorSystem("QuickStart")

  val fastProducerSlowConsumerGraph =
    Source.tick(10.millis, 500.millis, "tick")
      // make sure that at most 1 message is emitted in 5 seconds
      .throttle(1, 5.second, 1, ThrottleMode.Shaping)
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
