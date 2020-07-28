package vn.khanhpdt.akkastream.playground

import scala.concurrent.duration._

import akka.actor.ActorSystem
import akka.stream._
import akka.stream.scaladsl._
import com.typesafe.scalalogging.StrictLogging

object KillSwitch1 extends App with StrictLogging {

  implicit val system: ActorSystem = ActorSystem("QuickStart")

  val sink = Sink.foreach[String] { s =>
    logger.info(s"Sink received $s")
  }

  val (killSwitch, _) =
    Source.repeat("tick")
      .initialDelay(3.seconds)
      .throttle(1, 1.second, 1, ThrottleMode.Shaping)
      .viaMat(KillSwitches.single)(Keep.right)
      .toMat(sink)(Keep.both)
      .run()

  Thread.sleep(5000)

  killSwitch.shutdown()
}
