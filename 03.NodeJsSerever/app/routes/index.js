const usersRoutes = require('./users_routes');
const formRoutes = require('./form_routes');

module.exports = function(app) {
    usersRoutes(app);
    formRoutes(app);
};