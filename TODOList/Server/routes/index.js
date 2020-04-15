var express = require('express');
var router = express.Router();
let mysql = require('mysql');
let db_info = require('../lib/db');

router.post('/getAllTodoList', (req, res) => {
  var connection = mysql.createConnection(db_info);
  connection.connect();
  let SQLQuery = "SELECT * FROM todo;";
  connection.query(SQLQuery, (error, query_results, fields) => {
    if (error)
      throw error;
	
    res.send({result:0, data:query_results});
  });
  connection.end();
});

router.post('/insertTodoList', (req, res) => {
  var title = req.body.title;
  var deadline = req.body.deadline;
  var priority = req.body.priority;
  var content = req.body.content;
  var connection = mysql.createConnection(db_info);
  connection.connect();
  let SQLQuery = "INSERT INTO todo(title, deadline, priority, content, status) VALUES('"+title+"', '"+deadline+"', "+priority+", '"+content+"', 0);";
  connection.query(SQLQuery, (error, query_results, fields) => {
    if (error)
      throw error;

    res.send({result:0, data:query_results});
  });
  connection.end();
});

router.post('/getTodoList', (req, res) => {
  var id = req.body.id;
  var connection = mysql.createConnection(db_info);
  connection.connect();
  let SQLQuery = "SELECT * FROM todo where id="+id+";";
  connection.query(SQLQuery, (error, query_results, fields) => {
    if (error)
      throw error;
	
    res.send({result:0, data:query_results});
  });
  connection.end();
});

router.post('/updateTodoList', (req, res) => {
  var id = req.body.id;
  var title = req.body.title;
  var deadline = req.body.deadline;
  var priority = req.body.priority;
  var content = req.body.content;
  var connection = mysql.createConnection(db_info);
  connection.connect();
  let SQLQuery = "UPDATE todo SET title='"+title+"', deadline='"+deadline+"', priority="+priority+", content='"+content+"' WHERE id="+id+";";
  connection.query(SQLQuery, (error, query_results, fields) => {
    if (error)
      throw error;
	
    res.send({result:0, data:query_results});
  });
  connection.end();
});

router.post('/updateCheck', (req, res) => {
  var id = req.body.id;
  var status = req.body.status;
  var connection = mysql.createConnection(db_info);
  connection.connect();
  let SQLQuery = "UPDATE todo SET status="+status+"  WHERE id="+id+";";
  connection.query(SQLQuery, (error, query_results, fields) => {
    if (error)
      throw error;
	
    res.send({result:0, data:query_results});
  });
  connection.end();
});

router.post('/deleteTodoList', (req, res) => {
  var id = req.body.id;
  var connection = mysql.createConnection(db_info);
  connection.connect();
  let SQLQuery = "DELETE FROM todo WHERE id="+id+";";
  connection.query(SQLQuery, (error, query_results, fields) => {
    if (error)
      throw error;
	
    res.send({result:0, data:query_results});
  });
  connection.end();
});

module.exports = router;
