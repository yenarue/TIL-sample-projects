var express = require('express');
var admin = require('./routes/admin');

var app = express();
var port = 3000;

app.get('/', (req, res) => {
    res.send('It is my first web app');
});

// Routing :-)
app.use('/admin', admin);

app.listen(port, () => {
   console.log('Express listening on port', port); 
});