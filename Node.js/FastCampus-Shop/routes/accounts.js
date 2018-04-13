const express = require('express');
const router = express.Router();
const passwordHash = require('../middlewares/passwordHash');
const passport = require('passport');
const LocalStrategy = require('passport-local').Strategy;
const Users = require('../models/users');

passport.serializeUser((user, done) => {
    console.log('serializeUser');
    done(null, user);
});

passport.deserializeUser((user, done) => {
    const result = user;
    result.password = "";
    console.log('deserializeUser');
    done(null, result);
});

passport.use(new LocalStrategy({
        usernameField: 'username',
        passwordField : 'password',
        passReqToCallback : true
    }, (req, username, password, done) => {
        Users.findOne({
            username : username,
            password : passwordHash(password) 
        }, (err, user) => {
            if (!user) {
                done(null, false, { message : '아이디 또는 비밀번호 오류입니다.' });
            } else {
                done(null, user);
            }
        })
    }
));

router.get('/', (req, res) => {
    res.send('account app');
});

router.get('/join', (req, res) => {
    res.render('accounts/join');
});

router.post('/join', (req, res) => {
    const user = new Users({
        username : req.body.username,
        password : passwordHash(req.body.password),
        displayname : req.body.displayname,
    });

    user.save((err) => {
        res.send('<script>alert("회원가입 성공");location.href="/accounts/login";</script>');
    });
})

router.get('/login', (req, res) => {
    res.render('accounts/login', { flashMessage : req.flash().error });
});

router.post('/login' , passport.authenticate('local', { 
        failureRedirect: '/accounts/login', 
        failureFlash: true 
    }), (req, res) => {
        res.send('<script>alert("로그인 성공");location.href="/";</script>');
    }
);

router.get('/success', (req, res) => {
    res.send(req.user);
});

router.get('/logout', (req, res) => {
    req.logout();
    res.redirect('/accounts/login');
});

module.exports = router;