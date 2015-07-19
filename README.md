# playing-reactjs

This repository describes a basic example to render UI using ReactJS with Play 2.4.x, Scala and Anorm.
It also demonstrates the use of evolution in Play 2.4.x
  
This is a classic CRUD application, backed by a JDBC database. It demonstrates:
- Handling asynchronous results, Handling time-outs
- Achieving, Futures to use more idiomatic error handling.
- Accessing a JDBC database, using Anorm.
- Replaced the embedded JS & CSS libraries with [WebJars](http://www.webjars.org/).
- Play and Scala-based template engine implementation
- Integrating with a CSS framework (Twitter Bootstrap).  Twitter Bootstrap requires a different form layout to the default one that the Play form helper generates, so this application also provides an example of integrating a custom form input constructor.
- Flat-UI with Twitter Bootstrap to improve the look and feel of the application

-----------------------------------------------------------------------
###Instructions :-
-----------------------------------------------------------------------
* Clone the project into local system
* To run the Play framework 2.4.x, you need JDK 8 or later
* Install Typesafe Activator if you do not have it already. You can get it from [here](http://www.playframework.com/download)
* Execute `activator clean compile` to build the product
* Execute `activator run` to execute the product
* playing-reactjs should now be accessible at localhost:9000

All the screens:

### Play Evolution

![alt tag](/public/images/evolution.png)

### [Home Page](http://localhost:9000)

![alt tag](/public/images/reactjs-home.png)

### [Dashboard Page](http://localhost:9000/dashboard)

![alt tag](/public/images/reactjs-dashboard.png) 

-----------------------------------------------------------------------
###References :-
-----------------------------------------------------------------------
* [Play 2.4.x](http://www.playframework.com)
* [ReactJS](https://facebook.github.io/react/docs/tutorial.html)
* [Anorm](https://playframework.com/documentation/2.4.x/ScalaAnorm)
* [WebJars](http://www.webjars.org/)
* [Bootstrap](http://getbootstrap.com/css/)
* [Flat-UI](http://designmodo.github.io/Flat-UI/)
* [ReactJS Tutorial](http://ticofab.io/react-js-tutorial-with-play_scala_webjars/)

