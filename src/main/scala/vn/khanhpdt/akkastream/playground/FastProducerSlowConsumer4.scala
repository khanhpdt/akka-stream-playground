package vn.khanhpdt.akkastream.playground

import scala.concurrent.duration._

import akka.actor.ActorSystem
import akka.stream.scaladsl._
import akka.NotUsed
import com.typesafe.scalalogging.StrictLogging

object FastProducerSlowConsumer4 extends App with StrictLogging {

  implicit val system: ActorSystem = ActorSystem("QuickStart")

  // Producing 5 per 5 seconds. But b/c the processor is slow (1 per 2 seconds),
  // the whole flow is slowing down to 1 per 2 seconds.
  val fastProducerSlowConsumerGraph = {
    Source.tick(10.millis, 1.seconds, NotUsed)
      .via(Flow.fromFunction(s => {
        logger.info(s"Flow received $s.")
      }))
      .to {
        Sink.foreach(s => {
          logger.info(s"Sink received $s. Sleep for 3 seconds.")
          Thread.sleep(3.seconds.toMillis)
        })
      }
  }

  fastProducerSlowConsumerGraph.run()

}
