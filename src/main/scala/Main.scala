/**
  * Created by swei on 1/12/17.
  */

import java.util.Date

import akka.actor.Actor.Receive
import akka.actor.{Actor, ReceiveTimeout, _}
import org.omg.CORBA.TIMEOUT

import scala.concurrent.duration.{Duration, _}

abstract class AuctionMessage

case class Offer(bid: Int, client: ActorRef) extends AuctionMessage

case class Inquire(client: ActorRef) extends AuctionMessage


abstract class AuctionReply

case class Status(asked: Int, expire: Date) extends AuctionReply

case object BestOffer extends AuctionReply

case class BeatenOffer(maxBid: Int) extends AuctionReply

case class AuctionConcluded(seller: ActorRef, client: ActorRef) extends AuctionReply

case object AuctionFailed extends AuctionReply

case object AuctionOver extends AuctionReply

class Auction(seller: ActorRef, minBid: Int, closing: Date) extends Actor {
  val timeToShutdown = 36000000
  // msec
  val bidIncrement = 10

  def act() {
    var maxBid = minBid - bidIncrement
    var maxBidder: ActorRef = null
    var running = true
    while (running) {
      val timeout = Duration(closing.getTime() - new Date().getTime(), MILLISECONDS)
      context.setReceiveTimeout(timeout)

      def receive = {
        case Offer(bid, client) =>
          if (bid >= maxBid + bidIncrement) {
            if (maxBid >= minBid) maxBidder ! BeatenOffer(bid)
            maxBid = bid;
            maxBidder = client;
            client ! BestOffer
          } else {
            client ! BeatenOffer(maxBid)
          }
        case Inquire(client) =>
          client ! Status(maxBid, closing)
        case ReceiveTimeout =>
          if (maxBid >= minBid) {
            val reply = AuctionConcluded(seller, maxBidder)
            maxBidder ! reply;
            seller ! reply
          } else {
            seller ! AuctionFailed
          }
          context.setReceiveTimeout(Duration(timeToShutdown, MILLISECONDS))

          def receive = {
            case Offer(_, client) => client ! AuctionOver
            case TIMEOUT => running = false
          }
      }
    }
  }

  override def receive: Receive = ???
}

class Main {

}
