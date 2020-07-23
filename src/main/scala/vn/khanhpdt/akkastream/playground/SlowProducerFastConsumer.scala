package vn.khanhpdt.akkastream.playground

import scala.concurrent.duration._

import akka.actor.ActorSystem
import akka.stream.scaladsl._
import com.typesafe.scalalogging.StrictLogging

object SlowProducerFastConsumer extends App with StrictLogging {

  implicit val system: ActorSystem = ActorSystem("QuickStart")

  val slowProducerFastConsumerGraph =
    Source.tick(10.millis, 3.seconds, "tick")
      .via {
        Flow.fromFunction(s => {
          logger.info(s"Flow received $s. Sleep for 1 seconds.")
          Thread.sleep(1.seconds.toMillis)
          s"From Flow: $s"
        })
      }
      .to {
        Sink.foreach(s => {
          logger.info(s"Sink received $s")
        })
      }

  slowProducerFastConsumerGraph.run()

}
