bodyParser = require('body-parser').json();
const {v4: uuidv4} = require('uuid');
const fs = require('fs');
const path = require('path');
module.exports = function (app) {
    app.get('/form', (request, response) => {
        console.log("Успешно отправлена заявка");
        console.log("Синхронное чтение файла map")
        let result = fs.readFileSync(path.join(__dirname, '../notes', 'notes.txt'), "utf8");
        let resString="";
        result.split(';').forEach(value => resString+=value+"<br>");
        response.send(`<!DOCTYPE html>
  <html>
  <head>
      <title>Заявки</title>
      <meta charset="utf-8" />
  </head>
  <body>
      <h1>Заявки</h1>
      <p>
      ` + resString + `
      </p>
  </body>
  <html>`);

    });
    app.post('/form', bodyParser, (request, response) => {
        let body = request.body;
        console.log(body);
        let responseBody = {
            id: uuidv4(),
            "name": body["name"],
            "dis": body["dis"],
            "email": body["email"],
            "phone": body["phone"]
        }
        console.log(__dirname);
        console.log(path.join(__dirname, '../notes', 'notes.txt'));
        fs.appendFile(path.join(__dirname, '../notes', 'notes.txt'),JSON.stringify(responseBody)+";", (err) => {
            if (err) throw err;
            console.log("Create File");
        });

        // response.setHeader("Content-Type","application/json");
        // response.send(JSON.stringify(responseBody));
        response.redirect('/form');
    });
};
