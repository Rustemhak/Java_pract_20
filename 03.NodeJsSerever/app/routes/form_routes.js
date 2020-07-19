bodyParser = require('body-parser').json();
const {v4: uuidv4} = require('uuid');
const fs = require('fs');
const path = require('path');
const {Client} = require('pg');

const db = new Client({
    user: 'postgres',
    host: 'localhost',
    database: 'summary',
    password: '384953',
    port: 5432
});

module.exports = function (app) {
    app.get('/form', (request, response) => {
        let dataString = "";
        db.connect();
        var query = db.query('SELECT * FROM users', (err, data) => {
            if (err)
                throw new Error(err);
            let res = "";
            data.rows.forEach(value => {
                res += JSON.stringify(value) + "<br>";
            })

            response.send(`<!DOCTYPE html>
  <html>
  <head>
      <title>Заявки</title>
      <meta charset="utf-8" />
  </head>
  <body>
      <h1>Заявки</h1>
      <p>
      ${res}
      </p>
  </body>
  <html>`);
        })
        console.log("2: " + dataString);

    });


    app.post('/form', bodyParser, (request, response) => {
        let body = request.body;
        // console.log(body);
        let responseBody = {
            id: uuidv4(),
            "name": body["name"],
            "dis": body["dis"],
            "email": body["email"],
            "phone": body["phone"]
        }
        db.connect();
        db.query("INSERT INTO users(id,name,dis,email,phone) values($1,$2,$3,$4,$5);",
            [responseBody.id, responseBody.name, responseBody.dis, responseBody.email, responseBody.phone], (err,result) => {
            if (err)
                throw new Error(err);
        });
        // console.log(__dirname);
        // console.log(path.join(__dirname, '../notes', 'notes.txt'));
        // fs.appendFile(path.join(__dirname, '../notes', 'notes.txt'), JSON.stringify(responseBody) + ";", (err) => {
        //     if (err) throw err;
        //     console.log("Create File");
        // });

        // response.кsetHeader("Content-Type","application/json");
        // response.send(JSON.stringify(responseBody));
        response.redirect('/form');
    });
};
