package com.damoshow.aia.testdriven

import akka.actor.ActorSystem
import akka.testkit.TestKit
import org.scalatest.{MustMatchers, WordSpecLike}

import scala.concurrent.duration._

/**
  * 2017-10-17
  */
class FilteringActorTest extends TestKit(ActorSystem("testsystem"))
  with WordSpecLike
  with MustMatchers
  with StopSystemAfterAll {


  "A Filtering Actor" must {
    "filter out particular messages" in {
      import FilteringActor._

      val props = FilteringActor.props(testActor, 5)
      val filter = system.actorOf(props, "filter-1")
      filter ! Event(1)
      filter ! Event(2)
      filter ! Event(1)
      filter ! Event(3)
      filter ! Event(2)
      filter ! Event(4)
      filter ! Event(5)
      filter ! Event(5)
      filter ! Event(7)

      val eventIds = receiveWhile() {
        case Event(id) if id <= 5 => id
      }

      eventIds must be (List(1, 2, 3, 4, 5))
      expectMsg(Event(6))
    }

    "filter out particular messages using expectNoMsg" in {
      import FilteringActor._

      val props = FilteringActor.props(testActor, 5)
      val filter = system.actorOf(props, "filter-02")

      filter ! Event(1)
      filter ! Event(2)
      expectMsg(Event(1))
      expectMsg(Event(2))
      filter ! Event(2)
      expectNoMsg
      filter ! Event(4)
      expectMsg(Event(4))
      filter ! Event(3)
      expectMsg(Event(3))
      filter ! Event(1)
      expectNoMessage(timeout)

      filter ! Event(4)
      expectNoMessage(timeout)
      filter ! Event(5)
      filter ! Event(5)

      expectMsg(Event(5))
      expectNoMessage(timeout)
    }

  }
}
