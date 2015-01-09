package com.mosecom

/**
 * Created by jose on 08/01/2015.
 */
class Mosecon {

}


class Market_one {
  type M
}

abstract class Segment[S] {

  def market : Market_one
  def segments: List[S]

}

abstract class Campaign {

  type Steps

  def market : Market_one

}

abstract class Action {

  val id: Long

  def sendData( id: Long ): Unit
  def getData( id: Long ): Unit


}

abstract class ActionSendMail extends Action{

}

abstract  class Modeler[+Action] {

  def campaign : Campaign
  def action: List[Action]

}


sealed class StateAction

case object Start extends StateAction
case object Stop extends StateAction
case object Resume extends StateAction
case object Running extends StateAction

case class Actions( state : StateAction)


class testAction {

  val testActions = Actions(Stop)

}


import scala.slick.driver.H2Driver.simple._

class testSlick {

  // Definition of the SUPPLIERS table
  class Suppliers(tag: Tag) extends Table[(Int, String, String, String, String, String)](tag, "SUPPLIERS") {
    def id = column[Int]("SUP_ID", O.PrimaryKey) // This is the primary key column
    def name = column[String]("SUP_NAME")
    def street = column[String]("STREET")
    def city = column[String]("CITY")
    def state = column[String]("STATE")
    def zip = column[String]("ZIP")
    // Every table needs a * projection with the same type as the table's type parameter
    def * = (id, name, street, city, state, zip)
  }
  val suppliers = TableQuery[Suppliers]

  // Definition of the COFFEES table
  class Coffees(tag: Tag) extends Table[(String, Int, Double, Int, Int)](tag, "COFFEES") {
    def name = column[String]("COF_NAME", O.PrimaryKey)
    def supID = column[Int]("SUP_ID")
    def price = column[Double]("PRICE")
    def sales = column[Int]("SALES")
    def total = column[Int]("TOTAL")
    def * = (name, supID, price, sales, total)
    // A reified foreign key relation that can be navigated to create a join
    def supplier = foreignKey("SUP_FK", supID, suppliers)(_.id)
  }
  val coffees = TableQuery[Coffees]
  Database.forURL("jdbc:h2:mem:test1", driver = "org.h2.Driver") withSession {
    implicit session =>
    // <- write queries here

      // Create the tables, including primary and foreign keys
      (suppliers.ddl ++ coffees.ddl).create

      // Insert some suppliers
      suppliers += (101, "Acme, Inc.",      "99 Market Street", "Groundsville", "CA", "95199")
      suppliers += ( 49, "Superior Coffee", "1 Party Place",    "Mendocino",    "CA", "95460")
      suppliers += (150, "The High Ground", "100 Coffee Lane",  "Meadows",      "CA", "93966")

      // Insert some coffees (using JDBC's batch insert feature, if supported by the DB)
      coffees ++= Seq(
        ("Colombian",         101, 7.99, 0, 0),
        ("French_Roast",       49, 8.99, 0, 0),
        ("Espresso",          150, 9.99, 0, 0),
        ("Colombian_Decaf",   101, 8.99, 0, 0),
        ("French_Roast_Decaf", 49, 9.99, 0, 0)
      )
  }
}