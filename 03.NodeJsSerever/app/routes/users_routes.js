bodyParser = require('body-parser').json();

module.exports = function (app) {
    app.get('/users', (request, response) => {
            let result = {
                "id": 1,
                "name": "Рустем",
                "surname": "Хакимуллин",
                "phone": "891213456780",
                "email":  "rustemhak@mail.ru",
                "dis": "     Я студент первого курса Высшей школы ИТИС. В университете имею оценку <b> отлично </b> по всем предметам, кроме алгебры и геометрии. Закончил физико-математический лицей №145 с отличием.\n" +
                    "                            Знаю языки:<i>\n" +
                    "                            Java,\n" +
                    "                            Python,\n" +
                    "                            JavaScript.\n" +
                    "                        </i>\n" +
                    "                            Есть опыт работы <i> с фреймворком Django; вёрсткой классическим HTML и CSS, а также с применением Bootstrap; c базой данной PostgresSQL</i>.\n" +
                    "                            Мои работы можно посмотреть на <a href = \"https://github.com/Rustemhak/\"> Github</a>.",


            };

        response.setHeader("Content-Type","application/json");
        response.send(JSON.stringify(result));
    });
    app.post('/users',bodyParser, (request, response) => {
        let body = request.body;
        console.log(body["name"]);
        let responseBody = {
            id : Math.random(),
            "name": body["name"]
        }
        response.setHeader("Content-Type","application/json");
        response.send(JSON.stringify(responseBody));
    });
};
