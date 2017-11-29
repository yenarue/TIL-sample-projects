const express = require('express');
const path = require('path');
const logger = require('morgan');
const bodyParser = require('body-parser');
const cookieParser = require('cookie-parser');

/////DB
const mongoose = require('mongoose');
const autoIncrement = require('mongoose-auto-increment');
const db = mongoose.connection;
db.on('error', console.error);
db.once('open', () => {
    console.log('mongodb connect');
})
const connect = mongoose.connect('mongodb://127.0.0.1:27017/fastcampus', { useMongoClient : true});
autoIncrement.initialize(connect);
/////

const admin = require('./routes/admin');
const contacts = require('./routes/contacts');

const app = express();
const port = 3000;


// *.ejs View Engine을 추가한다
app.set('views', path.join(__dirname, 'views'));
app.set('view engine', 'ejs');

app.get('/', (req, res) => {
    res.send('It is my first web app');
});

// Middlewares
app.use(logger('dev'));
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({extended : false}));
app.use(cookieParser());

// Routing :-)
app.use('/admin', admin);
app.use('/contacts', contacts);
app.use('/uploads', express.static('uploads'));

app.listen(port, () => {
   console.log('Express listening on port', port); 
});