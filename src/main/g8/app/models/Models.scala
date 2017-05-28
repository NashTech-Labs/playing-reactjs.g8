package models

import java.util.{ Date }
import play.api.db._
import play.api.Play.current
import anorm._
import anorm.SqlParser._
import scala.language.postfixOps
import play.api.Logger

case class Employee(id: Long, name: String, address: String, designation: String)

/**
 * Helper for pagination.
 */
case class Page[A](items: Seq[A], page: Int, offset: Long, total: Long) {
  lazy val prev = Option(page - 1).filter(_ >= 0)
  lazy val next = Option(page + 1).filter(_ => (offset + items.size) < total)
}

object Employee {

  // -- Parsers

  /**
   * Parse a Employee from a ResultSet
   */
  val employee = {
    get[Long]("employee.id") ~
      get[String]("employee.name") ~
      get[String]("employee.address") ~
      get[String]("employee.designation") map {
        case id ~ name ~ address ~ designation => Employee(id, name, address, designation)
      }
  }

  // -- Queries
  Employee
  /**
   * Retrieve a employee from the id.
   */
  def findById(id: Long): Option[Employee] = {
    DB.withConnection { implicit connection =>
      SQL("select * from employee where id = {id}").on('id -> id).as(employee.singleOpt)
    }
  }

  /**
   * Return a page of (Employee).
   *
   * @param page Page to display
   * @param pageSize Number of employees per page
   * @param orderBy Employee property used for sorting
   * @param filter Filter applied on the name column
   */
  def list(page: Int = 0, pageSize: Int = 10, orderBy: Int = 1, filter: String = "%"): Page[Employee] = {

    val offest = pageSize * page

    DB.withConnection { implicit connection =>

      val employees = SQL(
        """
          select * from employee 
          where employee.name like {filter}
          order by {orderBy} nulls last
          limit {pageSize} offset {offset}
        """).on(
          'pageSize -> pageSize,
          'offset -> offest,
          'filter -> filter,
          'orderBy -> orderBy).as(employee *)

      val totalRows = SQL(
        """
          select count(*) from employee 
          where employee.name like {filter}
        """).on(
          'filter -> filter).as(scalar[Long].single)

      Page(employees, page, offest, totalRows)

    }

  }

  /**
   * Retrieve all employee.
   *
   * @return
   */
  def findAll(): List[Employee] = {
    DB.withConnection { implicit connection =>
      try {
        SQL("select * from employee order by name").as(employee *)
      } catch {
        case ex: Exception => Logger.info("ERROR", ex); Nil
      }
    }
  }

  /**
   * Update a employee.
   *
   * @param id The employee id
   * @param employee The employee values.
   */
  def update(id: Long, employee: Employee): Int = {
    DB.withConnection { implicit connection =>
      SQL(
        """
          update employee
          set name = {name}, address = {address}, designation = {designation}
          where id = {id}
        """).on(
          'id -> id,
          'name -> employee.name,
          'address -> employee.address,
          'designation -> employee.designation).executeUpdate()
    }
  }

  /**
   * Insert a new employee.
   *
   * @param employee The employee values.
   */
  def insert(employee: Employee): Option[Long] = {
    DB.withConnection { implicit connection =>
      SQL(
        """
          insert into employee values (
    		{id}, {name}, {address}, {designation}
          )
        """).on(
          'id -> Option.empty[Long],
          'name -> employee.name,
          'address -> employee.address,
          'designation -> employee.designation).executeInsert()
    }
  }

  /**
   * Delete a employee.
   *
   * @param id Id of the employee to delete.
   */
  def delete(id: Long): Int = {
    DB.withConnection { implicit connection =>
      SQL("delete from employee where id = {id}").on('id -> id).executeUpdate()
    }
  }

}
