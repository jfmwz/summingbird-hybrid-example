package com.akka

/**
 * Created by jose on 09/01/2015.
 */

import akka.actor._

// (1) changed the constructor here
class SimpleAkka(myName: String) extends Actor {
  def receive = {
    // (2) changed these println statements
    case "hello" => println("hello from %s".format(myName))
    case _ => println("'huh?', said %s".format(myName))
  }
}

object Main extends App {
  val system = ActorSystem("HelloSystem")
  // (3) changed this line of code
  val helloActor = system.actorOf(Props(new SimpleAkka("Fred")), name = "helloactor")
  helloActor ! "hello"
  helloActor ! "buenos dias"
}

