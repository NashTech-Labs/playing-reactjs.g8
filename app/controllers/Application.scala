package controllers

import play.api._
import play.api.mvc._
import play.api.libs.json.JsArray
import java.util.UUID
import play.api.libs.json.Json

import scala.concurrent.Future
import scala.concurrent.duration._

import anorm._
import models._
import play.api.data._
import play.api.data.Forms._
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import views._
import java.util.concurrent.TimeoutException
import play.api.libs.concurrent.Promise


class Application extends Controller {
  
  implicit val empJsonFormat = Json.format[Employee]

  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }
  
  def dashboard = Action {
    Ok(views.html.dashboard("Your new application is ready."))
  }
  
  // The json keys. The 'id' field was added as without it we would get a warning like this:
  // Warning: Each child in an array or iterator should have a unique "key" prop. Check the render method of CommentList. See https://fb.me/react-warning-keys for more information.
  val JSON_KEY_COMMENTS = "comments"
  val JSON_KEY_AUTHOR = "author"
  val JSON_KEY_TEXT = "text"
  val JSON_KEY_ID = "id"

  // Initialise the comments list
  var commentsJson: JsArray = Json.arr(
    Json.obj(JSON_KEY_ID -> UUID.randomUUID().toString, JSON_KEY_AUTHOR -> "Pete Hunt", JSON_KEY_TEXT -> "This is one comment"),
    Json.obj(JSON_KEY_ID -> UUID.randomUUID().toString, JSON_KEY_AUTHOR -> "Jordan Walke", JSON_KEY_TEXT -> "This is *another* comment")
  )

  // Returns the comments list
  def comments = Action {
    Ok(commentsJson)
  }

  // Adds a new comment to the list and returns it
  def comment(author: String, text: String) = Action {
    val newComment = Json.obj(
      JSON_KEY_ID -> UUID.randomUUID().toString,
      JSON_KEY_AUTHOR -> author,
      JSON_KEY_TEXT -> text)
    commentsJson = commentsJson :+ newComment
    Ok(newComment)
  }
  
    implicit val timeout = 10.seconds

  /**
   * Describe the employee form (used in both edit and create screens).
   */
  val employeeForm = Form(
    mapping(
      "id" -> ignored(0: Long),
      "name" -> nonEmptyText,
      "address" -> nonEmptyText,
      "designation" -> nonEmptyText)(Employee.apply)(Employee.unapply))

  /**
   * This result directly redirect to the application home.
   */
  val Home = Redirect(routes.Application.list())

  /**
   * Display the list of employees.
   *
   */
  def list = Action.async { implicit request =>
    val futurePage: Future[List[Employee]] = TimeoutFuture(Employee.findAll)
    futurePage.map(employees => Ok(Json.toJson(employees))).recover {
      case t: TimeoutException =>
        Logger.error("Problem found in employee list process")
        InternalServerError(t.getMessage)
    }
  }

  /**
   * Display the 'edit form' of a existing Employee.
   *
   * @param id Id of the employee to edit
   */
  def edit(id: Long) = Action.async {
    val futureEmp: Future[Option[models.Employee]] = TimeoutFuture(Employee.findById(id))
    futureEmp.map {
      case Some(employee) => Ok("")
      case None => NotFound
    }.recover {
      case t: TimeoutException =>
        Logger.error("Problem found in employee edit process")
        InternalServerError(t.getMessage)
    }
  }

  /**
   * Handle the 'edit form' submission
   *
   * @param id Id of the employee to edit
   */
  def update(id: Long) = Action.async { implicit request =>
    employeeForm.bindFromRequest.fold(
      formWithErrors => Future.successful(BadRequest("")),
      employee => {
        val futureUpdateEmp: Future[Int] = TimeoutFuture(Employee.update(id, employee))
        futureUpdateEmp.map { empId =>
          Home.flashing("success" -> s"Employee ${employee.name} has been updated")
        }.recover {
          case t: TimeoutException =>
            Logger.error("Problem found in employee update process")
            InternalServerError(t.getMessage)
        }
      })
  }

  /**
   * Handle the 'new employee form' submission.
   */
  def save = Action.async { implicit request =>
    employeeForm.bindFromRequest.fold(
      formWithErrors => Future.successful(BadRequest("")),
      employee => {
        val futureUpdateEmp: Future[Option[Long]] = TimeoutFuture(Employee.insert(employee))
        futureUpdateEmp.map {
          case Some(empId) =>
            val msg = s"Employee ${employee.name} has been created"
            Logger.info(msg)
            Home.flashing("success" -> msg)
          case None =>
            val msg = s"Employee ${employee.name} has not created"
            Logger.info(msg)
            Home.flashing("error" -> msg)
        }.recover {
          case t: TimeoutException =>
            Logger.error("Problem found in employee update process")
            InternalServerError(t.getMessage)
        }
      })
  }

  /**
   * Handle employee deletion.
   */
  def delete(id: Long) = Action.async {
    val futureInt = TimeoutFuture(Employee.delete(id))
    futureInt.map(i => Home.flashing("success" -> "Employee has been deleted")).recover {
      case t: TimeoutException =>
        Logger.error("Problem deleting employee")
        InternalServerError(t.getMessage)
    }
  }

  object TimeoutFuture {

    def apply[A](block: => A)(implicit timeout: FiniteDuration): Future[A] = {

      val promise = scala.concurrent.Promise[A]()

      // if the promise doesn't have a value yet then this completes the future with a failure
      Promise.timeout(Nil, timeout).map(_ => promise.tryFailure(new TimeoutException("This operation timed out")))

      // this tries to complete the future with the value from block
      Future(promise.success(block))

      promise.future
    }

  }

}
