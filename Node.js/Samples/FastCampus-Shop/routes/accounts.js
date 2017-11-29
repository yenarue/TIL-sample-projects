const express = require('express');
const router = express.Router();
const passwordHash = require('../libs/passwordHash');
const Users = require('../models/users');

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
    res.render('accounts/login');
});

module.exports = router;