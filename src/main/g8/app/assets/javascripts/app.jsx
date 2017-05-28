var EmployeeDetail = React.createClass({
  loadEmployees: function() {
    $.ajax({
      url: this.props.url,
      dataType: 'json',
      cache: false,
      success: function(data) {
        this.setState({data: data});
      }.bind(this),
      error: function(xhr, status, err) {
        console.error(this.props.url, status, err.toString());
      }.bind(this)
    });
  },
  getInitialState: function() {
    return {data: []};
  },
  componentDidMount: function() {
    this.loadEmployees();
    setInterval(this.loadEmployees, this.props.pollInterval);
  },
  render: function() {
    return (
      <div className="">
        <h1>Employees</h1>
        <EmployeeForm />
        <Employees data={this.state.data} />
      </div>
    );
  }
});

var Employees = React.createClass({
  render: function() {
    var employeeNodes = this.props.data.map(function (employee) {
      return (
        <Employee key={employee.id} name={employee.name} address={employee.address} designation={employee.designation} />
      );
    });

    return (
      <div className="well">
        {employeeNodes}
      </div>
    );
  }
});

var Employee = React.createClass({
  render: function() {
    return (
      <blockquote>
        <p>{this.props.name}</p>
        <strong>{this.props.address}</strong>
        <small>{this.props.designation}</small>
      </blockquote>
    );
  }
});

var EmployeeForm = React.createClass({
  handleSubmit: function(e) {
    e.preventDefault();
        
    var formData = $("#employeeForm").serialize();

    var saveUrl = "http://localhost:9000/employees/save";
    $.ajax({
      url: saveUrl,
      method: 'POST',
      dataType: 'json',
      data: formData,
      cache: false,
      success: function(data) {
        console.log(data)
      }.bind(this),
      error: function(xhr, status, err) {
        console.error(saveUrl, status, err.toString());
      }.bind(this)
    });

    // clears the form fields
    React.findDOMNode(this.refs.name).value = '';
    React.findDOMNode(this.refs.address).value = '';
    React.findDOMNode(this.refs.designation).value = '';
    return;
  },
  render: function() {
    return (
    	<div className="row">
      		<form id="employeeForm" onSubmit={this.handleSubmit}>
		        <div className="col-xs-3">
		          <div className="form-group">
		            <input type="text" name="name" required="required" ref="name" placeholder="Name" className="form-control" />
		          </div>
		        </div>
		        <div className="col-xs-3">
		          <div className="form-group">
		            <input type="text" name="address"required="required"  ref="address" placeholder="Address" className="form-control" />
		          </div>
		        </div>
		        <div className="col-xs-3">
		          <div className="form-group">
		            <input type="text" name="designation" required="required" ref="designation" placeholder="Designation" className="form-control" />
		            <span className="input-icon fui-check-inverted"></span>
		          </div>
		        </div>
		        <div className="col-xs-3">
		          <input type="submit" className="btn btn-block btn-info" value="Add" />
		        </div>
			</form>
	   </div>
    );
  }
});

React.render(<EmployeeDetail url="http://localhost:9000/employees" pollInterval={2000} />, document.getElementById('content'));
