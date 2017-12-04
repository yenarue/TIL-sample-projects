const express = require('express');
const path = require('path');
const logger = require('morgan');
const bodyParser = require('body-parser');
const cookieParser = require('cookie-parser');
const flash = require('connect-flash');
const passport = require('passport');
const session = require('express-session');

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
const accounts = require('./routes/accounts');
const auth = require('./routes/auth');
const chat = require('./routes/chat');
const home = require('./routes/home');

const loginRequired = require('./middlewares/loginRequired');

const app = express();
const port = 3000;


// *.ejs View Engine을 추가한다
app.set('views', path.join(__dirname, 'views'));
app.set('view engine', 'ejs');

// Upload Path
app.use('/uploads', express.static('uploads'));

// Middlewares
app.use(logger('dev'));
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({extended : false}));
app.use(cookieParser());

/**************** 라우팅 이전에 셋팅되어야 한다 ****************/
//session 관련 셋팅
app.use(session({
    secret: 'fastcampus',
    resave: false,
    saveUninitialized: true,
    cookie: {
      maxAge: 2000 * 60 * 60 //지속시간 2시간
    }
}));
 
//passport 적용
app.use(passport.initialize());
app.use(passport.session());
 
//플래시 메시지 관련
app.use(flash());
/************************************************************/
// 로그인 정보 뷰에서만 변수로 셋팅, 전체 미들웨어는 router위에 두어야 에러가 안난다
app.use((req, res, next) => {
    app.locals.isLogin = req.isAuthenticated();
    next();
})

// Routing :-)
app.use('/admin', loginRequired, admin);
app.use('/contacts', contacts);
app.use('/accounts', accounts);
app.use('/auth', auth);
app.use('/chat', chat);
app.use('/', home);

const server = app.listen(port, () => {
   console.log('Express listening on port', port); 
});

const listen = require('socket.io');
const io = listen(server);
io.on('connection', socket => {
    socket.on('client message', data => {
        io.emit('server message', data.message);
    })
})